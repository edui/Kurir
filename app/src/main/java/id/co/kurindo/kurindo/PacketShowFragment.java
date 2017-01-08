package id.co.kurindo.kurindo;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.zxing.BarcodeFormat;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import id.co.kurindo.kurindo.adapter.PacketTimelineViewAdapter;
import id.co.kurindo.kurindo.app.AppConfig;
import id.co.kurindo.kurindo.app.AppController;
import id.co.kurindo.kurindo.base.BaseFragment;
import id.co.kurindo.kurindo.model.Packet;
import id.co.kurindo.kurindo.model.StatusHistory;

/**
 * Created by aspire on 12/20/2016.
 */

public class PacketShowFragment extends BaseFragment{
    private static final String TAG = "PacketShowFragment";
    Packet packet;

    @Bind(R.id.text_nama_pengirim) TextView _namaPengirimText;
    @Bind(R.id.text_alamat_pengirim)    TextView _alamatPengirimText;
    @Bind(R.id.text_telepon_pengirim)    TextView _teleponPengirimText;
    @Bind(R.id.text_kota_pengirim)    TextView _kotaPengirimText;
    @Bind(R.id.text_nama_penerima)    TextView _namaPenerimaText;
    @Bind(R.id.text_alamat_penerima)    TextView _alamatPenerimaText;
    @Bind(R.id.text_telepon_penerima)    TextView _teleponPenerimaText;
    @Bind(R.id.text_kota_penerima)    TextView _kotaPenerimaText;
    @Bind(R.id.awbTextView)    TextView _awbText;
    @Bind(R.id.statusTextView)    TextView _statusText;
    @Bind(R.id.resi_qrcode) ImageView barcodeView;
    @Bind(R.id.service_code_icon) ImageView ivServiceCodeIcon;
    @Bind(R.id.service_icon) ImageView ivServiceIcon;
    @Bind(R.id.text_berat_paket) TextView _beratText;
    @Bind(R.id.text_info_paket) TextView _infoPaketText;
    @Bind(R.id.text_ongkos_paket) TextView _ongkosPaketText;
    @Bind(R.id.recyclerView)    RecyclerView mRecyclerView;
    PacketTimelineViewAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = getArguments();
        ArrayList<Packet> packets = bundle.getParcelableArrayList("packets");
        if(packets != null) packet = packets.get(0);

        retrieve_packet_history();
    }

    private void retrieve_packet_history() {
        String tag_string_req = "req_retrieve_packet_history";

        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_PACKET_HISTORY, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "retrieve_packet_history Response: " + response.toString());

                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");
                    if (!error) {
                        GsonBuilder builder = new GsonBuilder();
                        builder.setPrettyPrinting();
                        Gson gson = builder.create();

                        String no_resi = jObj.getString("awb_number");
                        packet.setResi(no_resi);
                        JSONArray jArr = jObj.getJSONArray("histories");
                        List histList = new ArrayList();
                        for (int i = 0; i < jArr.length(); i++) {
                            StatusHistory hist = gson.fromJson(jArr.get(i).toString(), StatusHistory.class);
                            histList.add(hist);
                        }
                        packet.setStatusHistoryList(histList);
                        if(adapter != null) adapter.notifyDataSetChanged();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getContext(), "Json error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Process_order Error: " + error.getMessage());
                Toast.makeText(getContext(),
                        error.getMessage(), Toast.LENGTH_LONG).show();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting params to register url
                Map params = new HashMap();
                params.put("awb", packet.getResi());

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

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflateAndBind(inflater, container, R.layout.activity_packet_show);
        setup_view(view);
        setup_timeline();
        return view;
    }

    private void setup_timeline() {
        mRecyclerView.setLayoutManager( new LinearLayoutManager(getContext()));
        mRecyclerView.setHasFixedSize(true);
        adapter = new PacketTimelineViewAdapter(getContext(), packet.getStatusHistoryList());
        mRecyclerView.setAdapter(adapter);
    }

    private void setup_view(View v){
        if(packet != null){
            if(packet.getServiceCode().equalsIgnoreCase(AppConfig.PACKET_SDS)){
                ivServiceCodeIcon.setImageResource(R.drawable.icon_sds);
            }else if(packet.getServiceCode().equalsIgnoreCase(AppConfig.PACKET_NDS)) {
                ivServiceCodeIcon.setImageResource(R.drawable.icon_nds);
            }else if(packet.getServiceCode().equalsIgnoreCase(AppConfig.PACKET_ENS)) {
                ivServiceCodeIcon.setImageResource(R.drawable.icon_ens);
            }

            if(packet.getOrder() != null){
                if(packet.getOrder().getType().equalsIgnoreCase(AppConfig.KEY_DOSEND)) {
                    ivServiceIcon.setImageResource(R.drawable.do_send_icon);
                }else if(packet.getOrder().getType().equalsIgnoreCase(AppConfig.KEY_DOJEK)) {
                    ivServiceIcon.setImageResource(R.drawable.do_jek_icon);
                }else if(packet.getOrder().getType().equalsIgnoreCase(AppConfig.KEY_DOWASH)) {
                    ivServiceIcon.setImageResource(R.drawable.do_wash_icon);
                }else if(packet.getOrder().getType().equalsIgnoreCase(AppConfig.KEY_DOSHOP)) {
                    ivServiceIcon.setImageResource(R.drawable.ic_store_black_18dp);
                }
            }
            _infoPaketText.setText(packet.getInfoPaket() +(packet.isViaMobil()? "\npakai Mobil = YES":""));
             _beratText.setText("Berat : "+packet.getBerat() + " Kg");
            _ongkosPaketText.setText("Biaya : "+AppConfig.formatCurrency( packet.getBiaya() ));

            _namaPengirimText.setText(packet.getNamaPengirim());
            _alamatPengirimText.setText(packet.getAlamatPengirim());
            _teleponPengirimText.setText(packet.getTeleponPengirim());

            _namaPenerimaText.setText(packet.getNamaPenerima());
            _alamatPenerimaText.setText(packet.getAlamatPenerima());
            _teleponPenerimaText.setText(packet.getTeleponPenerima());

            _kotaPengirimText.setText("Dari: \n"+packet.getKotaPengirimText());
            _kotaPenerimaText.setText("Ke: \n"+packet.getKotaPenerimaText());
            _awbText.setText(packet.getResi());
            _statusText.setText((packet.getStatusText()==null?"":packet.getStatusText()) +"\n"+packet.getUpdatedDate()==null? packet.getCreatedDate() == null? "":packet.getCreatedDate() : packet.getUpdatedDate());
            //_statusText.setText(AppConfig.getStatusText( packet.getStatus() ));

            String data = packet.getResi()+";"+packet.getNamaPengirim()+";"+packet.getAlamatPengirim()+";"+packet.getTeleponPengirim()+";"+packet.getKotaPengirim()
                    +";"+packet.getNamaPenerima()+";"+packet.getTeleponPenerima()+";"+packet.getAlamatPenerima()+";"+packet.getKotaPenerima();
            //barcodeView.setImageBitmap(AppConfig.encodeContentsToBarcode(packet.getResi(), BarcodeFormat.CODE_128));
            barcodeView.setImageBitmap(AppConfig.encodeContentsToBarcode(data, BarcodeFormat.QR_CODE));
        }
    }

}
