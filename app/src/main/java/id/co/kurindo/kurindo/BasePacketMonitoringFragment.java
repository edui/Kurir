package id.co.kurindo.kurindo;

import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.telephony.PhoneNumberUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import id.co.kurindo.kurindo.adapter.MonitorPacketAdapter;
import id.co.kurindo.kurindo.app.AppConfig;
import id.co.kurindo.kurindo.app.AppController;
import id.co.kurindo.kurindo.base.BaseFragment;
import id.co.kurindo.kurindo.helper.SQLiteHandler;
import id.co.kurindo.kurindo.model.Order;
import id.co.kurindo.kurindo.model.Packet;
import id.co.kurindo.kurindo.model.User;

/**
 * Created by DwiM on 11/9/2016.
 */

public abstract class BasePacketMonitoringFragment extends BaseFragment implements MonitorPacketAdapter.OnItemClickListener, View.OnClickListener {
    private static final String TAG = "PacketAllIncomingFragment";
    private static final int REQUEST_PACKET_LIST =0;

    MonitorPacketAdapter adapter;
    RecyclerView mRecyclerView;
    ArrayList<Packet> packets = new ArrayList<>();
    Timer t ;
    ProgressBar progressBar;
    TextView textView;
    AppCompatButton refreshBtn;
    Packet selectedPacket;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View x = inflateAndBind(inflater, container, R.layout.activity_monitor_packet1);

        mRecyclerView = (RecyclerView) x.findViewById(R.id.list);
        mRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 1));
        mRecyclerView.setHasFixedSize(true);

        adapter = new MonitorPacketAdapter(getContext(), packets, this);
        mRecyclerView.setAdapter(adapter);
        progressBar = (ProgressBar) x.findViewById(R.id.progressBar1);
        textView = (TextView) x.findViewById(R.id.TextViewTitle);
        textView.setText("Order Masuk ....");
        refreshBtn = (AppCompatButton) x.findViewById(R.id.RefreshBtn);
        refreshBtn.setOnClickListener(this);
        setup_timer();
        return x;
    }

    @Override
    public void onResume() {
        super.onResume();
        //scheduled();
    }

    @Override
    public void onPause() {
        super.onPause();
        //t.cancel();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        t.cancel();
        t = null;
    }
    private void setup_timer() {
        t = new Timer();
        scheduled();
    }
    private void scheduled(){
        int minutes = 10;
        t.schedule(new TimerTask() {

            public void run() {
                check_order();
            }
        }, 1000,minutes * 60 * 1000);
    }

    public abstract void check_order() ;

    public void onUpdateButtonClick(View view, int position) {
        ImageButton btn = (ImageButton) view;
        selectedPacket= packets.get(position);
        Bundle bundle = new Bundle();
        ArrayList list = new ArrayList();
        list.add(selectedPacket);
        bundle.putParcelableArrayList("packets", list);

        Intent intent = new Intent(getContext(), PacketShowActivity.class);
        intent.putExtras(bundle);
        startActivityForResult(intent, 0);
    }

    public void onPickButtonClick(View view, final int position, final String status) {
        if(session.isKurir() || session.isAdministrator()){
            selectedPacket = packets.get(position);

            final Handler handler = new Handler() {
                @Override
                public void handleMessage(Message mesg) {
                    throw new RuntimeException();
                }
            };

            DialogInterface.OnClickListener YesClickListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    action_packet(selectedPacket, position, status, selectedPacket.getStatus(), handler);
                }
            };
            showConfirmationDialog("Confirm StatusHistory","Anda Yakin akan merubah status packet '"+selectedPacket.getResi()+"' menjadi '"+AppConfig.getStatusText(status)+"' ?", YesClickListener, null);

            // loop till a runtime exception is triggered.
            try { Looper.loop(); }
            catch(RuntimeException e2) {}
        }

    }

    private void action_packet(final Packet p, final int position, final String status, final String statusBefore, final Handler handler) {
        if(getActivity() != null){
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    progressBar.setVisibility(View.VISIBLE);
                    String tag_string_req = "req_monitor_open_packet";
                    StringRequest strReq = new StringRequest(Request.Method.POST,
                            AppConfig.URL_PACKET_ACTION, new Response.Listener<String>() {

                        @Override
                        public void onResponse(String response) {
                            Log.d(TAG, "MonitorOrder > PACKET ACTION  : Response: " + response.toString());
                            //hideDialog();

                            try {
                                JSONObject jObj = new JSONObject(response);
                                boolean error = jObj.getBoolean("error");

                                // Check for error node in json
                                if (!error) {
                                    //check_order();
                                    Packet packet = packets.get(position);
                                    packet.setStatus(status);
                                    if(status.equalsIgnoreCase(AppConfig.KEY_KUR100) || status.equalsIgnoreCase(AppConfig.KEY_KUR101) || status.equalsIgnoreCase(AppConfig.KEY_KUR500))
                                        packets.remove(position);
                                    adapter.notifyDataSetChanged();
                                    String msg = jObj.getString("message");
                                    Toast.makeText(getContext(), msg, Toast.LENGTH_LONG).show();
                                    progressBar.setVisibility(View.GONE);
                                } else {
                                    //Get the error message
                                    String errorMsg = jObj.getString("message");
                                    Toast.makeText(getContext(), errorMsg, Toast.LENGTH_LONG).show();
                                }
                            } catch (JSONException e) {
                                // JSON error
                                e.printStackTrace();
                                Toast.makeText(getContext(), "Json error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                            }

                            progressBar.setVisibility(View.GONE);
                            handler.handleMessage(null);
                        }
                    }, new Response.ErrorListener() {

                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.e(TAG, "Booking Error: " + error.getMessage());
                            Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_LONG).show();
                            progressBar.setVisibility(View.GONE);
                            handler.handleMessage(null);
                        }
                    }) {

                        @Override
                        protected Map<String, String> getParams() {
                            // Posting parameters to  url
                            Map<String, String> params = getRequestParams();
                            params.put("awb", p.getResi());
                            params.put("action", status);
                            params.put("filter", statusBefore);

                            return params;
                        }
                        @Override
                        public Map<String, String> getHeaders() throws AuthFailureError {
                            Map<String, String> params = new HashMap<String, String>();
                            String api = db.getUserApi();
                            params.put("Api", api);

                            return params;
                        }

                    };

                    // Adding request to request queue
                    AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
                }
            });
        }

    }

    protected Map<String, String> getRequestParams(){
        Map<String, String> params = new HashMap<String, String>();
        params.put("form-type", "json");
        params.put("user_agent", AppConfig.USER_AGENT);

        return params;
    }
    @Override
    public void onClick(View v) {
        check_order();
    }

    @Override
    public void onWaButtonClick(View view, int position, String phoneNumber) {
        sendWAMessageTo(phoneNumber);
    }

    private void sendWAMessageTo(String phoneNumber){
            Intent waIntent = new Intent("android.intent.action.MAIN");
            waIntent.setComponent(new ComponentName("com.whatsapp","com.whatsapp.Conversation"));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            phoneNumber = PhoneNumberUtils.stripSeparators(PhoneNumberUtils.formatNumber(phoneNumber, "ID"));
        }else{
            phoneNumber = PhoneNumberUtils.stripSeparators(PhoneNumberUtils.formatNumber(phoneNumber));
        }
        waIntent.putExtra("jid", phoneNumber+"@s.whatsappp.net");
        startActivity(waIntent);
    }

    private void cara1(){
        PackageManager pm=getActivity().getPackageManager();
        try {

            Intent waIntent = new Intent(Intent.ACTION_SEND);
            waIntent.setType("text/plain");
            String text = "YOUR TEXT HERE";

            PackageInfo info=pm.getPackageInfo("com.whatsapp", PackageManager.GET_META_DATA);
            //Check if package exists or not. If not then code
            //in catch block will be called
            waIntent.setPackage("com.whatsapp");

            waIntent.putExtra(Intent.EXTRA_TEXT, text);
            startActivity(Intent.createChooser(waIntent, "Share with"));

        } catch (PackageManager.NameNotFoundException e) {
            Toast.makeText(getActivity(), "WhatsApp not Installed", Toast.LENGTH_SHORT)
                    .show();
        }
    }
    private boolean whatsappInstalledOrNot(String uri) {
        PackageManager pm = getActivity().getPackageManager();
        boolean app_installed = false;
        try {
            pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES);
            app_installed = true;
        } catch (PackageManager.NameNotFoundException e) {
            app_installed = false;
        }
        return app_installed;
    }


    public static void parsePackets(List packets, JSONObject jObj) throws JSONException {
        GsonBuilder builder = new GsonBuilder();
        builder.setPrettyPrinting();
        Gson gson = builder.create();


        JSONArray datas = jObj.getJSONArray("data");
        if(datas != null && datas.length() > 0){

            packets.clear();

            for (int i = 0; i < datas.length(); i++) {
                JSONObject data = datas.optJSONObject(i);

                String no_resi = data.getString("awb_number");
                String service_code = data.getString("service_code");

                String nama_pengirim = data.getString("nama_pengirim");
                String alamat_pengirim = data.getString("alamat_pengirim");
                String telepon_pengirim = data.getString("telepon_pengirim");
                String kota_pengirim = data.getString("kota_pengirim");
                String kota_pengirim_text = data.getString("kota_pengirim_text");

                String nama_penerima = data.getString("nama_penerima");
                String alamat_penerima = data.getString("alamat_penerima");
                String telepon_penerima = data.getString("telepon_penerima");
                String kota_penerima = data.getString("kota_penerima");
                String kota_penerima_text = data.getString("kota_penerima_text");

                String berat_kiriman = data.getString("berat_kiriman");
                int berat = 0;
                try{berat = Integer.parseInt(berat_kiriman); }catch (Exception e){}

                String isi_kiriman = data.getString("isi_kiriman");

                String status = data.getString("status");
                String statusText = data.getString("status_text");
                double biayaPacket = data.getDouble("biaya");
                String created = data.getString("created_date");
                String updated = data.getString("updated_date");

                Packet packet = new Packet();
                packet.setResi(no_resi);
                packet.setServiceCode(service_code);
                packet.setNamaPengirim(nama_pengirim);
                packet.setAlamatPengirim(alamat_pengirim);
                packet.setTeleponPengirim(telepon_pengirim);
                packet.setKotaPengirim(kota_pengirim);
                packet.setNamaPenerima(nama_penerima);
                packet.setAlamatPenerima(alamat_penerima);
                packet.setTeleponPenerima(telepon_penerima);
                packet.setKotaPenerima(kota_penerima);
                packet.setBerat(berat);
                packet.setInfoPaket(isi_kiriman);
                packet.setStatus(status);
                packet.setStatusText(statusText);
                packet.setBiaya(biayaPacket);
                packet.setCreatedDate(created);
                packet.setUpdatedDate(updated);

                packet.setKotaPengirimText(kota_pengirim_text);
                packet.setKotaPenerimaText(kota_penerima_text);
                try {
                    User kurir = gson.fromJson(data.getString("kurir"), User.class);
                    packet.setKurir(kurir);
                }catch (Exception e){}

                try {
                    Order order = gson.fromJson(data.getString("order"), Order.class);
                    packet.setOrder(order);
                }catch (Exception e){}

                packets.add(packet);
            }
        }
    }

}
