package id.co.kurindo.kurindo;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.OnClick;
import id.co.kurindo.kurindo.adapter.CartViewAdapter;
import id.co.kurindo.kurindo.adapter.DoMartRouteAdapter;
import id.co.kurindo.kurindo.adapter.DoServiceViewAdapter;
import id.co.kurindo.kurindo.adapter.LocationViewAdapter;
import id.co.kurindo.kurindo.adapter.DoMartViewAdapter;
import id.co.kurindo.kurindo.adapter.TPacketViewAdapter;
import id.co.kurindo.kurindo.app.AppConfig;
import id.co.kurindo.kurindo.app.AppController;
import id.co.kurindo.kurindo.base.BaseActivity;
import id.co.kurindo.kurindo.base.BaseFragment;
import id.co.kurindo.kurindo.comp.ProgressDialogCustom;
import id.co.kurindo.kurindo.helper.ViewHelper;
import id.co.kurindo.kurindo.map.LocationMapViewsActivity;
import id.co.kurindo.kurindo.map.MapViewsActivity;
import id.co.kurindo.kurindo.model.CartItem;
import id.co.kurindo.kurindo.model.DoMart;
import id.co.kurindo.kurindo.model.StatusHistory;
import id.co.kurindo.kurindo.model.TOrder;
import id.co.kurindo.kurindo.model.TPacket;
import id.co.kurindo.kurindo.model.TUser;
import id.co.kurindo.kurindo.util.LogUtil;
import id.co.kurindo.kurindo.util.ParserUtil;
import id.co.kurindo.kurindo.wizard.dosend.AcceptTOrderActivity;
import id.co.kurindo.kurindo.wizard.dosend.RejectTOrderActivity;

import static android.widget.Toast.LENGTH_SHORT;

/**
 * Created by Ratan on 7/29/2015.
 */
public class TOrderShowFragment extends BaseFragment {
    private static final String TAG = "OrderShowFragment";
    @Bind(R.id.kur300Btn)
    ImageButton kur300Btn;
    @Bind(R.id.kur310Btn)
    ImageButton kur310Btn;
    @Bind(R.id.kur350Btn)
    ImageButton kur350Btn;
    @Bind(R.id.kur400Btn)
    ImageButton kur400Btn;
    @Bind(R.id.kur500Btn)
    ImageButton kur500Btn;
    @Bind(R.id.kur900Btn)
    ImageButton kur900Btn;
    @Bind(R.id.kur910Btn)
    ImageButton kur910Btn;

    TOrder order;
    @Bind(R.id.tvCartTitle)
    TextView tvCartTitle;
    @Bind(R.id.tvAwbTitle)
    TextView tvAwbTitle;
    @Bind(R.id.lvCartItems)
    RecyclerView lvCartItems;
    @Bind(R.id.tvTotalPrice)
    TextView tvTotalPrice;
    @Bind(R.id.lvRecipientItems)
    RecyclerView lvRecipientItems;

    @Bind(R.id.tvPengiriman)
    TextView tvPengiriman;
    @Bind(R.id.tvPembeli)
    TextView tvPembeli;

    @Bind(R.id.tvPayment)
    TextView tvPayment;

    @Bind(R.id.kur200Btn)
    ImageButton kur200Btn;
    @Bind(R.id.kur999Btn)
    ImageButton kur999Btn;
    @Bind(R.id.infoStatus)
    ImageButton infoStatus;
    @Bind(R.id.order_service_icon)
    ImageView orderIcon;
    List<StatusHistory> historyList = new ArrayList();
    public static final int ACCEPTED_REQUEST_CODE = 1500;
    public static final int REJECTED_REQUEST_CODE = 1999;

    ProgressDialog progressBar;
    Context context;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final Bundle bundle = getArguments();
        //ArrayList list = bundle.getParcelableArrayList("order");
        //bundle.setClassLoader(Order.class.getClassLoader());
        //order = (TOrder) bundle.getParcelable("order");
        //OrderHelper.getInstance().setOrder(order);
        //if(list != null) order = (Order) list.get(0);

        if(bundle != null){
            boolean load = bundle.getBoolean("load");
            if(load){
                Handler handler = new Handler() {
                    @Override
                    public void handleMessage(Message mesg) {
                        throw new RuntimeException("RuntimeException");
                    }
                };

                retrieve_order_awb(bundle, handler);

                // loop till a runtime exception is triggered.
                try { Looper.loop(); }
                catch(RuntimeException e2) { e2.printStackTrace();}

            }
        }
        if(order == null) order = ViewHelper.getInstance().getOrder();

        if(order == null) getActivity().finish();

        context = getContext();
        progressBar = new ProgressDialogCustom(context);
    }

    private void retrieve_order_awb(final Bundle bundle, final Handler handler) {
                    String awb = bundle.getString("awb");
                    HashMap<String,String> params =new HashMap<>();
                    params.put("awb", awb);
                    String url = AppConfig.URL_TORDER_RETRIEVE_AWB;
                    url = url +"/"+awb;
                    addRequest("retrieve_order_awb", Request.Method.GET, url, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONObject jObj = new JSONObject(response);
                                String message =jObj.getString("message");
                                if(message.equalsIgnoreCase("OK")){
                                    JSONArray datas = jObj.getJSONArray("data");
                                    if(datas != null && datas.length() > 0) {
                                        ParserUtil util = new ParserUtil();
                                        for (int i = 0; i < datas.length(); i++) {
                                            JSONObject data = datas.optJSONObject(i);
                                            order = util.parseTOrder(data);
                                        }
                                    }
                                }else{
                                    Toast.makeText(context, ""+message, Toast.LENGTH_SHORT).show();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                                Toast.makeText(context, "Error "+e.getMessage(), Toast.LENGTH_SHORT).show();
                            }

                            handler.handleMessage(null);
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError volleyError) {
                            volleyError.printStackTrace();
                            Toast.makeText(context,"Network Error "+volleyError.getMessage(), LENGTH_SHORT).show();
                            handler.handleMessage(null);
                        }
                    }, params, getKurindoHeaders());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflateAndBind(inflater, container, R.layout.fragment_order_show);
        setup_pembeli();
        setup_products();
        setup_shipping();
        setup_status();
        return view;
    }

    private void setup_status() {
        if(order != null){
            if(order.getStatus().equalsIgnoreCase(AppConfig.KEY_KUR100)){
                kur300Btn.setImageResource(R.drawable.status01_0_icon);
                kur300Btn.setVisibility(View.VISIBLE);
                kur310Btn.setImageResource(R.drawable.status03_0_icon);
                kur310Btn.setVisibility(View.VISIBLE);
                kur500Btn.setImageResource(R.drawable.status04_0_icon);
                kur500Btn.setVisibility(View.VISIBLE);

                kur200Btn.setVisibility(View.VISIBLE);
                kur999Btn.setVisibility(View.VISIBLE);

                if(session.isPelanggan() || session.isKurir()){
                    kur200Btn.setVisibility(View.GONE);
                    kur999Btn.setVisibility(View.GONE);
                }

            }else if(order.getStatus().equalsIgnoreCase(AppConfig.KEY_KUR101)){
                kur310Btn.setImageResource(R.drawable.status03_0_icon);
                kur310Btn.setVisibility(View.VISIBLE);
                kur500Btn.setImageResource(R.drawable.status04_0_icon);
                kur500Btn.setVisibility(View.VISIBLE);
                kur350Btn.setVisibility(View.GONE);
                kur400Btn.setVisibility(View.GONE);

                if(session.isPelanggan() || session.isKurir()){
                    kur200Btn.setVisibility(View.GONE);
                    kur999Btn.setVisibility(View.GONE);
                }
            }else if(order.getStatus().equalsIgnoreCase(AppConfig.KEY_KUR200)){
                kur300Btn.setVisibility(View.VISIBLE);
                kur300Btn.setImageResource(R.drawable.status01_1_icon);

                kur350Btn.setVisibility(View.GONE);
                kur400Btn.setVisibility(View.GONE);

                kur310Btn.setImageResource(R.drawable.status03_0_icon);
                kur310Btn.setVisibility(View.VISIBLE);

                kur500Btn.setImageResource(R.drawable.status04_0_icon);
                kur500Btn.setVisibility(View.VISIBLE);

                kur200Btn.setVisibility(View.GONE);
                kur999Btn.setVisibility(View.GONE);

            }else if(order.getStatus().equalsIgnoreCase(AppConfig.KEY_KUR300)){
                kur310Btn.setImageResource(R.drawable.status03_1_icon);
                kur310Btn.setVisibility(View.VISIBLE);
                kur350Btn.setVisibility(View.GONE);
                kur400Btn.setVisibility(View.GONE);

                kur300Btn.setImageResource(R.drawable.status01_2_icon);
                kur300Btn.setVisibility(View.VISIBLE);

                kur500Btn.setImageResource(R.drawable.status04_0_icon);
                kur500Btn.setVisibility(View.VISIBLE);

                kur200Btn.setVisibility(View.GONE);
                kur999Btn.setVisibility(View.GONE);
            }else if(order.getStatus().equalsIgnoreCase(AppConfig.KEY_KUR310) || order.getStatus().equalsIgnoreCase(AppConfig.KEY_KUR350)){
                kur400Btn.setImageResource(R.drawable.status05_1_icon);
                kur400Btn.setVisibility(View.VISIBLE);

                kur500Btn.setImageResource(R.drawable.status04_1_icon);
                kur500Btn.setVisibility(View.VISIBLE);
                kur350Btn.setVisibility(View.GONE);

                kur300Btn.setImageResource(R.drawable.status01_2_icon);
                kur300Btn.setVisibility(View.VISIBLE);
                kur300Btn.setEnabled(false);

                kur310Btn.setImageResource(R.drawable.status03_2_icon);
                kur310Btn.setVisibility(View.VISIBLE);
                kur310Btn.setEnabled(false);

                kur200Btn.setVisibility(View.GONE);
                kur999Btn.setVisibility(View.GONE);
            }else if(order.getStatus().equalsIgnoreCase(AppConfig.KEY_KUR400)){
                kur350Btn.setImageResource(R.drawable.status06_1_icon);
                kur350Btn.setVisibility(View.VISIBLE);
                kur200Btn.setVisibility(View.GONE);
                kur400Btn.setVisibility(View.GONE);
                kur500Btn.setVisibility(View.GONE);

                kur300Btn.setImageResource(R.drawable.status01_2_icon);
                kur300Btn.setVisibility(View.VISIBLE);

                kur310Btn.setImageResource(R.drawable.status03_2_icon);
                kur310Btn.setVisibility(View.VISIBLE);

                kur200Btn.setVisibility(View.GONE);
                kur999Btn.setVisibility(View.GONE);
            }else if(order.getStatus().equalsIgnoreCase(AppConfig.KEY_KUR500)){
                kur500Btn.setImageResource(R.drawable.status04_2_icon);
                kur500Btn.setVisibility(View.VISIBLE);
                kur350Btn.setVisibility(View.GONE);
                kur400Btn.setVisibility(View.GONE);

                kur300Btn.setImageResource(R.drawable.status01_2_icon);
                kur300Btn.setVisibility(View.VISIBLE);

                kur310Btn.setImageResource(R.drawable.status03_2_icon);
                kur310Btn.setVisibility(View.VISIBLE);

                kur200Btn.setVisibility(View.GONE);
                kur999Btn.setVisibility(View.GONE);
            }else{
                kur300Btn.setEnabled(false);
                kur310Btn.setEnabled(false);
                kur350Btn.setEnabled(false);
                kur400Btn.setEnabled(false);
                kur500Btn.setEnabled(false);
                kur200Btn.setVisibility(View.GONE);
                kur999Btn.setVisibility(View.GONE);
            }
        }
    }


    @OnClick(R.id.infoStatus)
    public void OnClick_InfoStatus(){
        final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message mesg) {
                throw new RuntimeException();
            }
        };
        progressBar.show();

        retrieve_packet_history(handler);

        // loop till a runtime exception is triggered.
        try { Looper.loop(); }
        catch(RuntimeException e2) {}

        String content = "";
        for(StatusHistory hist : historyList){
            content+= hist.toString();
        }
        progressBar.dismiss();
        showPopupWindow("Order\nStatus Information", content, R.drawable.icon_kurirkurindo);
    }

    private void retrieve_packet_history(final Handler handler) {
        String tag_string_req = "req_retrieve_order_history";

        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_TORDER_HISTORY, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                LogUtil.logD(TAG, "retrieve_order_history Response: " + response.toString());

                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");
                    if (!error) {
                        JSONArray jArr = jObj.getJSONArray("histories");
                        if(jArr.length() > 0){
                            historyList.clear();
                            ParserUtil parser = new ParserUtil();
                            for (int i = 0; i < jArr.length(); i++) {
                                //StatusHistory hist = gson.fromJson(jArr.get(i).toString(), StatusHistory.class);
                                //User by = gson.fromJson(jArr.getJSONObject(i).get("created_by").toString(), User.class);
                                StatusHistory hist = parser.parserHistory(jArr.getJSONObject(i));
                                historyList.add(hist);
                            }
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(context, "Json error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
                handler.sendMessage(null);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                LogUtil.logE(TAG, "Process_order Error: " + error.getMessage());
                Toast.makeText(context,
                        "Network Error : "+error.getMessage(), Toast.LENGTH_LONG).show();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting params to register url
                Map params = new HashMap();
                params.put("awb", order.getAwb());

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

    private void setup_pembeli() {
        if(order != null && order.getBuyer() != null) {
            tvPembeli.setText(order.getBuyer().getFirstname() + " " + order.getBuyer().getLastname()
                    + " "+order.getBuyer().getPhone()+"\n"
                    + order.getBuyer().getAddress().toStringFormatted());
        }
    }

    private void setup_shipping() {
        lvRecipientItems.setLayoutManager(new GridLayoutManager(context, 1));
        lvRecipientItems.setHasFixedSize(true);
        tvPengiriman.setVisibility(View.VISIBLE);

        if(order.getService_type().equalsIgnoreCase(AppConfig.KEY_DOSERVICE) || order.getService_type().equalsIgnoreCase(AppConfig.KEY_DOWASH)) {
            LocationViewAdapter adapter = new LocationViewAdapter(context, order, new LocationViewAdapter.OnItemClickListener() {
                @Override
                public void onViewLocationButtonClick(View view, int position, TUser location) {
                    ViewHelper.getInstance().setLocation(location);
                    ((BaseActivity) getActivity()).showActivity(LocationMapViewsActivity.class);

                }
            });
            lvRecipientItems.setAdapter(adapter);
            tvPengiriman.setText(getString(R.string.location_address));

        }else  if(order.getService_type().equalsIgnoreCase(AppConfig.KEY_DOMART) ){
            tvPengiriman.setText(getString(R.string.pengiriman));
            DoMartRouteAdapter adapter = new DoMartRouteAdapter(context, order, new DoMartRouteAdapter.OnItemClickListener() {
                @Override
                public void onViewRouteButtonClick(View view, int position, TOrder order) {
                    ViewHelper.getInstance().setOrder(order);
                    ((BaseActivity)getActivity()).showActivity(MapViewsActivity.class);
                }
            });
            lvRecipientItems.setAdapter(adapter);
        }else{
            List<TPacket> packets = new ArrayList<>();
            if(order.getPackets().size() > 0) packets.addAll( order.getPackets());
            if(order.getService_type().equalsIgnoreCase(AppConfig.KEY_DOSEND)) {
                tvPengiriman.setText(getString(R.string.packet));
            }else if(order.getService_type().equalsIgnoreCase(AppConfig.KEY_DOJEK)
                    || order.getService_type().equalsIgnoreCase(AppConfig.KEY_DOCAR)
                    || order.getService_type().equalsIgnoreCase(AppConfig.KEY_DOMOVE) ){
                tvPengiriman.setText(getString(R.string.detail) + " "+order.getService_type());
            }else{
                tvPengiriman.setText(getString(R.string.location_address));
            }
            TPacketViewAdapter adapter = new TPacketViewAdapter(context, packets, order, new TPacketViewAdapter.OnItemClickListener() {
                @Override
                public void onViewRouteButtonClick(View view, int position, TPacket packet) {
                    ViewHelper.getInstance().setPacket(packet);
                    ((BaseActivity)getActivity()).showActivity(MapViewsActivity.class);
                }
            });
            lvRecipientItems.setAdapter(adapter);
        }

        /*
        if(order.getRecipients().size() > 0){
            final RecipientViewAdapter recipientAdapter = new RecipientViewAdapter(getActivity(), getRecipientItems(order.getRecipients()));
            lvRecipientItems.setAdapter(recipientAdapter );
        }*/

    }

    public List<DoMart> getDoMartList(TOrder order){
        List<DoMart> list = new ArrayList<>();
        list.add(new DoMart());
        if(order != null && order.getMarts() != null){
            list.addAll(order.getMarts());
        }
        return list;
    }

    private void setup_products() {
        lvCartItems.setLayoutManager(new GridLayoutManager(context, 1));
        lvCartItems.setHasFixedSize(true);
        if(order.getService_type().equalsIgnoreCase(AppConfig.KEY_DOSERVICE) || order.getService_type().equalsIgnoreCase(AppConfig.KEY_DOWASH)){
            tvCartTitle.setText(getString(R.string.detil_layanan));
            DoServiceViewAdapter cartAdapter = new DoServiceViewAdapter(getActivity(), (order.getServices()==null? new ArrayList() : new ArrayList(order.getServices())));
            lvCartItems.setAdapter(cartAdapter);

        //}else if(order.getService_type().equalsIgnoreCase(AppConfig.KEY_DOSEND) || order.getService_type().equalsIgnoreCase(AppConfig.KEY_DOJEK) || order.getService_type().equalsIgnoreCase(AppConfig.KEY_DOSHOP)){

        }else  if(order.getService_type().equalsIgnoreCase(AppConfig.KEY_DOMART) ){
            tvCartTitle.setText(getString(R.string.titip_belanja));
            DoMartViewAdapter adapter = new DoMartViewAdapter(context, getDoMartList(order));
            lvCartItems.setAdapter(adapter);
            tvPengiriman.setText(getString(R.string.detail_titipan));
        }else {
            tvCartTitle.setText(getString(R.string.detail_product));
            CartViewAdapter cartAdapter = new CartViewAdapter(getActivity(), getCartItemsPlusHeader(order));
            lvCartItems.setAdapter(cartAdapter);
        }

        tvTotalPrice.setText(AppConfig.formatCurrency(order.getTotalPrice().setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue()));
        //tvPageTitle.setText("Detail Order "+order.getService_type());
        setup_icon();
        updateStatus();
    }

    private void setup_icon() {
        if(order.getService_type().equalsIgnoreCase(AppConfig.KEY_DOSEND)) {
            orderIcon.setImageResource(R.drawable.do_send_icon);
        }else if(order.getService_type().equalsIgnoreCase(AppConfig.KEY_DOJEK)) {
            orderIcon.setImageResource(R.drawable.do_jek_icon);
        }else if(order.getService_type().equalsIgnoreCase(AppConfig.KEY_DOCAR)) {
            orderIcon.setImageResource(R.drawable.do_car_icon);
        }else if(order.getService_type().equalsIgnoreCase(AppConfig.KEY_DOMOVE)) {
            orderIcon.setImageResource(R.drawable.do_move_icon);
        }else if(order.getService_type().equalsIgnoreCase(AppConfig.KEY_DOWASH)) {
            orderIcon.setImageResource(R.drawable.do_wash_icon);
        }else if(order.getService_type().equalsIgnoreCase(AppConfig.KEY_DOSERVICE)) {
            orderIcon.setImageResource(R.drawable.do_service_icon);
        }else if(order.getService_type().equalsIgnoreCase(AppConfig.KEY_DOHIJAMAH)) {
            orderIcon.setImageResource(R.drawable.do_hijamah_icon);
        }else if(order.getService_type().equalsIgnoreCase(AppConfig.KEY_DOSHOP)) {
            orderIcon.setImageResource(R.drawable.do_shop_icon);
        }else if(order.getService_type().equalsIgnoreCase(AppConfig.KEY_DOMART)) {
            orderIcon.setImageResource(R.drawable.do_mart_icon);
        }
    }

    private void updateStatus() {
        tvAwbTitle.setText("No. Resi : "+order.getAwb() +"\nStatus : "+ AppConfig.getOrderStatusText(order.getStatus()));
        tvAwbTitle.setVisibility(View.VISIBLE);

        tvPayment.setText(order==null || order.getPayment()==null?"":order.getPayment());

        kur200Btn.setVisibility(View.GONE);
        kur999Btn.setVisibility(View.GONE);
        if(session.isKurir() || session.isAdministrator()){
            if(order.getStatus().equalsIgnoreCase(AppConfig.KEY_KUR100)
                    || order.getStatus().equalsIgnoreCase(AppConfig.KEY_KUR101)){
                kur200Btn.setVisibility(View.VISIBLE);
                kur999Btn.setVisibility(View.VISIBLE);
                kur200Btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ViewHelper.getInstance().setOrder(order);
                        Intent intent = new Intent(context, AcceptTOrderActivity.class);
                        //OrderHelper.getInstance().setOrder(order);
                        //intent.putExtra("order", order);
                        startActivityForResult(intent, ACCEPTED_REQUEST_CODE);
                    }
                });

                kur999Btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ViewHelper.getInstance().setOrder(order);
                        Intent intent = new Intent(getActivity(), RejectTOrderActivity.class);
                        //intent.putExtra("order", order);
                        //OrderHelper.getInstance().setOrder(order);
                        startActivityForResult(intent, REJECTED_REQUEST_CODE);
                    }
                });
            }else if(order.getStatus().equalsIgnoreCase(AppConfig.KEY_KUR200)){
                setup_status_kur200(order);
            }else if(order.getStatus().equalsIgnoreCase(AppConfig.KEY_KUR300)){
                setup_status_kur300(order);
            }else if(order.getStatus().equalsIgnoreCase(AppConfig.KEY_KUR310)){
                setup_status_kur310(order);
            }else if(order.getStatus().equalsIgnoreCase(AppConfig.KEY_KUR400)){
                setup_status_kur400(order);
            }else if(order.getStatus().equalsIgnoreCase(AppConfig.KEY_KUR350)){
                setup_status_kur350(order);
            }else if(order.getStatus().equalsIgnoreCase(AppConfig.KEY_KUR500)){
                setup_status_kur500(order);
            }else{
                setup_hide_btn();
            }
            //TODO update status button
        }
    }


    private void action_order(final TOrder p, final String status, final String statusBefore, final Handler handler) {
        if(getActivity() != null){
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    progressBar.show();
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
                                    order.setStatus(status);
                                    String msg = jObj.getString("message");
                                    Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
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

                            progressBar.dismiss();
                            handler.handleMessage(null);
                        }
                    }, new Response.ErrorListener() {

                        @Override
                        public void onErrorResponse(VolleyError error) {
                            LogUtil.logE(TAG, "action_order Error: " + error.getMessage());
                            Toast.makeText(context, "Network Error "+error.getMessage(), Toast.LENGTH_LONG).show();
                            progressBar.dismiss();
                            handler.handleMessage(null);
                        }
                    }) {

                        @Override
                        protected Map<String, String> getParams() {
                            // Posting parameters to  url
                            Map<String, String> params = new HashMap();
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
    public void place_action_order(final TOrder order, final String status){
    final Handler handler = new Handler() {
        @Override
        public void handleMessage(Message mesg) {
            throw new RuntimeException();
        }
    };

    DialogInterface.OnClickListener YesClickListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            action_order(order, status, order.getStatus(), handler);
        }
    };
    showConfirmationDialog("Confirm Status","Anda Yakin akan merubah status order '"+order.getAwb()+"' menjadi '"+AppConfig.getOrderStatusText(status)+"' ?", YesClickListener, null);

    // loop till a runtime exception is triggered.
    try { Looper.loop(); }
    catch(RuntimeException e2) {}
}
    protected void setup_status_kur200(final TOrder order) {
        kur300Btn.setImageResource(R.drawable.status01_1_icon);
        kur300Btn.setVisibility(View.VISIBLE);
        kur300Btn.setEnabled(true);

        kur300Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                place_action_order(order, AppConfig.KEY_KUR300);
            }
        });

        kur310Btn.setVisibility(View.VISIBLE);
        kur310Btn.setEnabled(false);

        kur200Btn.setVisibility(View.GONE);

        kur400Btn.setVisibility(View.GONE);
        kur400Btn.setEnabled(false);

        kur500Btn.setVisibility(View.VISIBLE);
        kur500Btn.setEnabled(false);
    }

    protected void setup_status_kur300(final TOrder order) {
        kur310Btn.setImageResource(R.drawable.status03_1_icon);
        kur310Btn.setVisibility(View.VISIBLE);
        kur310Btn.setEnabled(true);
        kur310Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                place_action_order(order, AppConfig.KEY_KUR310);
            }
        });

        kur300Btn.setImageResource(R.drawable.status01_2_icon);
        kur300Btn.setVisibility(View.VISIBLE);
        kur300Btn.setEnabled(false);

        kur200Btn.setVisibility(View.GONE);
        kur350Btn.setVisibility(View.GONE);
        kur400Btn.setVisibility(View.GONE);
        kur400Btn.setEnabled(false);

        kur500Btn.setImageResource(R.drawable.status04_0_icon);
        kur500Btn.setVisibility(View.VISIBLE);
        kur500Btn.setEnabled(false);
    }

    protected void setup_status_kur310(final TOrder order) {
        kur500Btn.setImageResource(R.drawable.status04_1_icon);
        kur500Btn.setVisibility(View.VISIBLE);
        kur500Btn.setEnabled(true);
        kur500Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                place_action_order(order, AppConfig.KEY_KUR500);
            }
        });

        kur400Btn.setImageResource(R.drawable.status05_1_icon);
        kur400Btn.setVisibility(View.VISIBLE);
        kur400Btn.setEnabled(true);
        kur400Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                place_action_order(order, AppConfig.KEY_KUR400);
            }
        });

        kur300Btn.setImageResource(R.drawable.status01_2_icon);
        kur300Btn.setVisibility(View.VISIBLE);
        kur300Btn.setEnabled(false);

        kur310Btn.setImageResource(R.drawable.status03_2_icon);
        kur310Btn.setVisibility(View.VISIBLE);
        kur310Btn.setEnabled(false);

        kur200Btn.setVisibility(View.GONE);
    }

    protected void setup_status_kur400(final TOrder order) {
        kur350Btn.setImageResource(R.drawable.status06_1_icon);
        kur350Btn.setVisibility(View.VISIBLE);
        kur350Btn.setEnabled(true);
        kur350Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                place_action_order(order, AppConfig.KEY_KUR350);
            }
        });

        kur400Btn.setImageResource(R.drawable.status05_2_icon);
        kur400Btn.setVisibility(View.VISIBLE);
        kur400Btn.setEnabled(false);

        kur500Btn.setImageResource(R.drawable.status04_0_icon);
        kur500Btn.setVisibility(View.VISIBLE);
        kur500Btn.setEnabled(false);

        kur300Btn.setImageResource(R.drawable.status01_2_icon);
        kur300Btn.setVisibility(View.VISIBLE);
        kur300Btn.setEnabled(false);

        kur310Btn.setImageResource(R.drawable.status03_2_icon);
        kur310Btn.setVisibility(View.VISIBLE);
        kur310Btn.setEnabled(false);

        kur200Btn.setVisibility(View.GONE);

    }

    protected void setup_status_kur350(final TOrder order) {
        kur500Btn.setImageResource(R.drawable.status04_1_icon);
        kur500Btn.setVisibility(View.VISIBLE);
        kur500Btn.setEnabled(true);
        kur500Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                place_action_order(order, AppConfig.KEY_KUR500);
            }
        });

        kur400Btn.setImageResource(R.drawable.status05_1_icon);
        kur400Btn.setVisibility(View.VISIBLE);
        kur400Btn.setEnabled(true);
        kur400Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                place_action_order(order, AppConfig.KEY_KUR400);
            }
        });

        kur300Btn.setImageResource(R.drawable.status01_2_icon);
        kur300Btn.setVisibility(View.VISIBLE);
        kur300Btn.setEnabled(false);

        kur310Btn.setImageResource(R.drawable.status03_2_icon);
        kur310Btn.setVisibility(View.VISIBLE);
        kur310Btn.setEnabled(false);

        kur350Btn.setImageResource(R.drawable.status06_2_icon);
        kur350Btn.setVisibility(View.VISIBLE);
        kur350Btn.setEnabled(false);

        kur200Btn.setVisibility(View.GONE);
    }


    protected void setup_status_kur500(TOrder order) {
        kur500Btn.setImageResource(R.drawable.status04_2_icon);
        kur500Btn.setVisibility(View.VISIBLE);
        kur500Btn.setEnabled(false);

        kur300Btn.setImageResource(R.drawable.status01_2_icon);
        kur300Btn.setVisibility(View.VISIBLE);
        kur300Btn.setEnabled(false);

        kur310Btn.setImageResource(R.drawable.status03_2_icon);
        kur310Btn.setVisibility(View.VISIBLE);
        kur310Btn.setEnabled(false);

        kur200Btn.setVisibility(View.GONE);
    }


    protected void setup_hide_btn() {
        kur300Btn.setVisibility(View.GONE);
        kur310Btn.setVisibility(View.GONE);
        kur400Btn.setVisibility(View.GONE);
        kur500Btn.setVisibility(View.GONE);
        kur350Btn.setVisibility(View.GONE);
        kur200Btn.setVisibility(View.GONE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if ((requestCode == ACCEPTED_REQUEST_CODE ||requestCode == REJECTED_REQUEST_CODE )&& resultCode == Activity.RESULT_OK) {
            //Order order = OrderHelper.getInstance().getOrder();
            //Order order = data.getExtras().getParcelable("order");
            TOrder order = ViewHelper.getInstance().getOrder();
            if(order != null) {
                this.order = order;
                updateStatus();
                setup_status();
            }
        }
    }

    private List<CartItem> getCartItemsPlusHeader(TOrder order) {
        List<CartItem> cartItems = new ArrayList<>();
        CartItem ci = new CartItem();
        ci.setProduct(null);
        ci.setQuantity(0);
        cartItems.add(ci);//headers
        if(order != null && order.getProducts() != null)
            cartItems.addAll( order.getProducts() );

        /*Map<Saleable, Integer> itemMap = order.getProducts();
        if(itemMap !=null){
            CartItem ci = new CartItem();
            ci.setProduct(null);
            ci.setQuantity(0);
            cartItems.add(ci);//headers
            for (Map.Entry<Saleable, Integer> entry : itemMap.entrySet()) {
                //CartItem cartItem = new CartItem();
                cartItem.setProduct((Product) entry.getKey());
                cartItem.setQuantity(entry.getValue());
                cartItems.add(cartItem);
            }
        }
        */
        return cartItems;
    }

}
