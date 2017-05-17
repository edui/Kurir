package id.co.kurindo.kurindo;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

import id.co.kurindo.kurindo.adapter.KurirMonitorTOrderAdapter;
import id.co.kurindo.kurindo.adapter.MonitorTOrderAdapter;
import id.co.kurindo.kurindo.app.AppConfig;
import id.co.kurindo.kurindo.app.AppController;
import id.co.kurindo.kurindo.base.BaseActivity;
import id.co.kurindo.kurindo.base.BaseFragment;
import id.co.kurindo.kurindo.helper.ViewHelper;
import id.co.kurindo.kurindo.model.TOrder;
import id.co.kurindo.kurindo.util.LogUtil;
import id.co.kurindo.kurindo.wizard.dosend.AcceptTOrderActivity;

/**
 * Created by DwiM on 11/9/2016.
 */

public abstract class BaseTOrderMonitoringFragment extends BaseFragment implements MonitorTOrderAdapter.OnItemClickListener, View.OnClickListener {
    private static final String TAG = "BaseTOrderMonitoringFragment";
    private static final int REQUEST_PACKET_LIST =0;
    public static final int ACCEPTED_REQUEST_CODE = 1500;
    public static final int REJECTED_REQUEST_CODE = 1999;

    MonitorTOrderAdapter adapter;
    RecyclerView mRecyclerView;
    ArrayList<TOrder> orders = new ArrayList<>();
    Timer t ;
    ProgressBar progressBar;
    TextView textView;
    AppCompatButton refreshBtn;
    TOrder selectedOrder;

    protected Context context;
    
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View x = inflateAndBind(inflater, container, R.layout.activity_monitor_order1);
        
        context = getContext();
        
        mRecyclerView = (RecyclerView) x.findViewById(R.id.list);
        mRecyclerView.setLayoutManager(new GridLayoutManager(context, 1));
        mRecyclerView.setHasFixedSize(true);
        if(session.isPelanggan()){
            adapter = new MonitorTOrderAdapter(context, orders, this);
        }else{
            adapter = new KurirMonitorTOrderAdapter(context, orders, this);//ADMIN&KURIR
        }
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
        selectedOrder = orders.get(position);
        Bundle bundle = new Bundle();
        //bundle.setClassLoader(Order.class.getClassLoader());
        //bundle.putParcelable("order", selectedOrder);
        ViewHelper.getInstance().setOrder(selectedOrder);
        ((BaseActivity)getActivity()).showActivity(TOrderShowActivity.class);
    }

    public void onPickButtonClick(View view, final int position, final String status) {
        if(session.isAdministrator() || session.isKurir()){
            selectedOrder = orders.get(position);

            if(selectedOrder.getStatus().equalsIgnoreCase(AppConfig.KEY_KUR100) && session.isAdministrator()){
                ViewHelper.getInstance().setOrder(selectedOrder);
                Intent intent = new Intent(getActivity(), AcceptTOrderActivity.class);
                //intent.putExtra("order", selectedOrder);
                startActivityForResult(intent, ACCEPTED_REQUEST_CODE);
            }else{
                final Handler handler = new Handler() {
                    @Override
                    public void handleMessage(Message mesg) {
                        throw new RuntimeException();
                    }
                };

                DialogInterface.OnClickListener YesClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        action_order(selectedOrder, position, status, selectedOrder.getStatus(), handler);
                    }
                };
                showConfirmationDialog("Confirm Status","Anda Yakin akan merubah status order '"+selectedOrder.getAwb()+"' menjadi '"+AppConfig.getOrderStatusText(status)+"' ?", YesClickListener, null);

                // loop till a runtime exception is triggered.
                try { Looper.loop(); }
                catch(RuntimeException e2) {}
            }

        }
    }
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if ((requestCode == ACCEPTED_REQUEST_CODE ||requestCode == REJECTED_REQUEST_CODE )&& resultCode == Activity.RESULT_OK) {
            //TOrder order = data.getExtras().getParcelable("order");
            TOrder order = ViewHelper.getInstance().getOrder();
            if(order != null) {
                int position = orders.indexOf(selectedOrder);
                this.selectedOrder = order;
                adapter.notifyItemChanged(position);
            }
        }
    }

    private void action_order(final TOrder p, final int position, final String status, final String statusBefore, final Handler handler) {
        if(getActivity() != null){
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    progressBar.setVisibility(View.VISIBLE);
                    String tag_string_req = "req_monitor_open_order";
                    StringRequest strReq = new StringRequest(Request.Method.POST,
                            AppConfig.URL_TORDER_ACTION, new Response.Listener<String>() {

                        @Override
                        public void onResponse(String response) {
                            LogUtil.logD(TAG, "MonitorOrder > URL_TORDER_ACTION : Response: " + response.toString());
                            //hideDialog();

                            try {
                                JSONObject jObj = new JSONObject(response);
                                boolean error = jObj.getBoolean("error");

                                // Check for error node in json
                                if (!error) {
                                    //check_order();
                                    TOrder order = orders.get(position);
                                    order.setStatus(status);
                                    if(status.equalsIgnoreCase(AppConfig.KEY_KUR100) || status.equalsIgnoreCase(AppConfig.KEY_KUR101) || status.equalsIgnoreCase(AppConfig.KEY_KUR500))
                                        orders.remove(position);
                                    adapter.notifyDataSetChanged();
                                    String msg = jObj.getString("message");
                                    Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
                                    progressBar.setVisibility(View.GONE);
                                } else {
                                    // Error in login. Get the error message
                                    String errorMsg = jObj.getString("message");
                                    Toast.makeText(context, ""+errorMsg, Toast.LENGTH_LONG).show();
                                }
                            } catch (JSONException e) {
                                // JSON error
                                e.printStackTrace();
                                Toast.makeText(context, "Json error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                            }

                            handler.handleMessage(null);
                            progressBar.setVisibility(View.GONE);
                        }
                    }, new Response.ErrorListener() {

                        @Override
                        public void onErrorResponse(VolleyError error) {
                            LogUtil.logE(TAG, "Booking Error: " + error.getMessage());
                            Toast.makeText(context, "Network Error "+error.getMessage(), Toast.LENGTH_LONG).show();
                            progressBar.setVisibility(View.GONE);
                            handler.handleMessage(null);
                        }
                    }) {

                        @Override
                        protected Map<String, String> getParams() {
                            // Posting parameters to  url
                            Map<String, String> params = getRequestParams();
                            params.put("awb", p.getAwb());
                            params.put("action", status);
                            params.put("filter", statusBefore);

                            return params;
                        }
                        @Override
                        public Map<String, String> getHeaders() throws AuthFailureError {
                            return getKurindoHeaders();
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
    public void onWaButtonClick(View view, int position) {
        cara1();
    }
    private void cara2(){
        boolean isWhatsappInstalled = whatsappInstalledOrNot("com.whatsapp");
        if (isWhatsappInstalled) {
            Uri uri = Uri.parse("smsto:" + "6282110056018");
            Intent sendIntent = new Intent(Intent.ACTION_SENDTO, uri);
            sendIntent.putExtra(Intent.EXTRA_TEXT, "Hai Good Morning");
            sendIntent.setType("text/plain");
            sendIntent.setPackage("com.whatsapp");
            startActivity(sendIntent);
        } else {
            Toast.makeText(getActivity(), "WhatsApp not Installed",Toast.LENGTH_SHORT).show();
            Uri uri = Uri.parse("market://details?id=com.whatsapp");
            Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(goToMarket);
        }
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


    public static Bundle parseOrders(List<TOrder> orders, JSONObject jObj) throws JSONException {
        Bundle bundle = null;
        int doSendCount = 0;
        int doJekCount = 0;
        int doWashCount = 0;
        int doServiceCount = 0;
        int doHijamahCount = 0;
        int doCarCount = 0;
        int doMoveCount = 0;
        int doShopCount = 0;
        int doMartCount = 0;

        orders.clear();

        GsonBuilder builder = new GsonBuilder();
        builder.setPrettyPrinting();
        Gson gson = builder.create();

        JSONArray datas = jObj.getJSONArray("data");
        if(datas != null && datas.length() > 0){

            for (int i = 0; i < datas.length(); i++) {
                JSONObject data = datas.optJSONObject(i);
                TOrder order = gson.fromJson(datas.getString(i), TOrder.class);
/*
                //int id = data.getInt("id");
                String no_resi = data.getString("awb");
                TUser pembeli =  null;
                try {
                    pembeli = gson.fromJson(data.getString("pembeli"),TUser.class);
                }catch (Exception e){}

                TUser pic =  null;
                try {
                    pic = gson.fromJson(data.getString("pic"),TUser.class);
                }catch (Exception e){}

                double totalPrice = data.getDouble("totalPrice");
                int totalQuantity = data.getInt("totalQuantity");
                String type = data.getString("service_type");
                if(type == AppConfig.KEY_DOSEND) doSendCount++;
                else if(type == AppConfig.KEY_DOJEK) doJekCount++;
                else if(type == AppConfig.KEY_DOWASH) doWashCount++;
                else if(type == AppConfig.KEY_DOSERVICE) doServiceCount++;
                else if(type == AppConfig.KEY_DOHIJAMAH) doHijamahCount++;
                else if(type == AppConfig.KEY_DOCAR) doCarCount++;
                else if(type == AppConfig.KEY_DOMOVE) doMoveCount++;
                else if(type == AppConfig.KEY_DOSHOP) doShopCount++;

                String payment = data.getString("payment");
                String status = data.getString("status");
                String statusText = data.getString("status_text");
                String created = data.getString("created_date");

                Set<CartItem> items= new LinkedHashSet<CartItem>();
                JSONArray products = data.getJSONArray("products");
                for (int j = 0; j < products.length(); j++) {
                    JSONObject prod = products.optJSONObject(j);
                    Product product = gson.fromJson(prod.getString("product"), Product.class);
                    int qty = prod.getInt("quantity");
                    CartItem item = new CartItem();
                    item.setProduct(product);
                    item.setQuantity(qty);
                    items.add(item);
                }

                TOrder order = new TOrder();
                //order.setId(id);
                order.setAwb(no_resi);
                order.setService_type(type);
                order.setPayment(payment);
                order.setStatus(status);
                order.setStatusText(statusText);
                order.setCreated_date(created);
                order.setTotalPrice(new BigDecimal(totalPrice));
                order.setTotalQuantity(totalQuantity);
                order.setBuyer(pembeli);
                order.setPic(pic);
                order.setProducts(items);
/*                try{
                    Set<Recipient> recipients = gson.fromJson(data.getString("recipients"), new TypeToken<LinkedHashSet<Recipient>>(){}.getType());
                    /*Set<Recipient> recipients = new LinkedHashSet<Recipient>();
                    JSONArray recps = data.getJSONArray("recipients");
                    for (int j = 0; j < recps.length(); j++) {
                        JSONObject rec = recps.optJSONObject(j);
                        Recipient recp = gson.fromJson(rec.toString(), Recipient.class);
                        recipients.add(recp);
                    }
                    order.setRecipients(recipients);
                }catch (Exception e){}
                   //

                try{
                    Set<TPacket> packets= new LinkedHashSet<TPacket>();
                    JSONArray pkt = data.getJSONArray("packets");
                    for (int j = 0; j < pkt.length(); j++) {
                        JSONObject prod = pkt.optJSONObject(j);
                        //Log.d("dd", prod.toString());
                        try {
                            TPacket packet = gson.fromJson(pkt.getString(j), TPacket.class);
                            packets.add(packet);
                        }catch (Exception e){}
                    }
                    //Set<Packet> packets = gson.fromJson(data.getString("packets"), new TypeToken<LinkedHashSet<Packet>>(){}.getType());
                    order.setPackets(packets);
                }catch (Exception e){e.printStackTrace();}
            */
                orders.add(order);
            }
            bundle = new Bundle();
            bundle.putInt(AppConfig.KEY_DOSEND, doSendCount);
            bundle.putInt(AppConfig.KEY_DOJEK, doJekCount);
            bundle.putInt(AppConfig.KEY_DOSERVICE, doServiceCount);
            bundle.putInt(AppConfig.KEY_DOWASH, doWashCount);
            bundle.putInt(AppConfig.KEY_DOHIJAMAH, doHijamahCount);
            bundle.putInt(AppConfig.KEY_DOCAR, doCarCount);
            bundle.putInt(AppConfig.KEY_DOMOVE, doMoveCount);
            bundle.putInt(AppConfig.KEY_DOSHOP, doShopCount);
            bundle.putInt(AppConfig.KEY_DOMART, doMartCount);
        }
        return bundle;
    }
}
