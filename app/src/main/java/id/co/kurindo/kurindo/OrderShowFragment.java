package id.co.kurindo.kurindo;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.tonyvu.sc.model.Cart;
import com.android.tonyvu.sc.model.Saleable;
import com.android.tonyvu.sc.util.CartHelper;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.DefaultSliderView;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import butterknife.Bind;
import butterknife.OnClick;
import id.co.kurindo.kurindo.adapter.CartViewAdapter;
import id.co.kurindo.kurindo.adapter.NewsAdapter;
import id.co.kurindo.kurindo.adapter.PacketAdapter;
import id.co.kurindo.kurindo.adapter.PacketViewAdapter;
import id.co.kurindo.kurindo.adapter.RecipientViewAdapter;
import id.co.kurindo.kurindo.app.AppConfig;
import id.co.kurindo.kurindo.app.AppController;
import id.co.kurindo.kurindo.app.Constant;
import id.co.kurindo.kurindo.base.BaseFragment;
import id.co.kurindo.kurindo.helper.CheckoutHelper;
import id.co.kurindo.kurindo.model.CartItem;
import id.co.kurindo.kurindo.model.News;
import id.co.kurindo.kurindo.model.Order;
import id.co.kurindo.kurindo.model.Packet;
import id.co.kurindo.kurindo.model.Product;
import id.co.kurindo.kurindo.model.Recipient;
import id.co.kurindo.kurindo.model.StatusHistory;
import id.co.kurindo.kurindo.model.User;
import id.co.kurindo.kurindo.task.ListenableAsyncTask;
import id.co.kurindo.kurindo.task.LoadNewsTask;
import id.co.kurindo.kurindo.wizard.AcceptOrderActivity;
import id.co.kurindo.kurindo.wizard.RejectOrderActivity;
import id.co.kurindo.kurindo.wizard.ShopCheckoutActivity;

/**
 * Created by Ratan on 7/29/2015.
 */
public class OrderShowFragment extends BaseFragment {
    private static final String TAG = "OrderShowFragment";
    @Bind(R.id.kur200BtnS)
    ImageButton kur200BtnS;
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
    @Bind(R.id.kur999BtnS)
    ImageButton kur999BtnS;

    Order order;
    @Bind(R.id.tvPageTitle)
    TextView tvPageTitle;
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

    @Bind(R.id.kur200Btn)
    ImageButton kur200Btn;
    @Bind(R.id.kur999Btn)
    ImageButton kur999Btn;
    @Bind(R.id.infoStatus)
    ImageButton infoStatus;
    List<StatusHistory> historyList = new ArrayList();
    public static final int ACCEPTED_REQUEST_CODE = 1500;
    public static final int REJECTED_REQUEST_CODE = 1999;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        //ArrayList list = bundle.getParcelableArrayList("order");
        bundle.setClassLoader(Order.class.getClassLoader());
        order = (Order) bundle.getParcelable("order");
        //if(list != null) order = (Order) list.get(0);
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
                kur200BtnS.setImageResource(R.drawable.accept_booking_icon);
                kur200BtnS.setVisibility(View.VISIBLE);
                kur300Btn.setImageResource(R.drawable.status01_0_icon);
                kur300Btn.setVisibility(View.VISIBLE);
                kur310Btn.setImageResource(R.drawable.status03_0_icon);
                kur310Btn.setVisibility(View.VISIBLE);
                kur500Btn.setImageResource(R.drawable.status04_0_icon);
                kur500Btn.setVisibility(View.VISIBLE);

                kur200Btn.setVisibility(View.VISIBLE);
                kur999Btn.setVisibility(View.VISIBLE);

            }else if(order.getStatus().equalsIgnoreCase(AppConfig.KEY_KUR101)){
                kur200BtnS.setImageResource(R.drawable.status01_0_icon);
                kur200BtnS.setVisibility(View.VISIBLE);
                kur310Btn.setImageResource(R.drawable.status03_0_icon);
                kur310Btn.setVisibility(View.VISIBLE);
                kur500Btn.setImageResource(R.drawable.status04_0_icon);
                kur500Btn.setVisibility(View.VISIBLE);
                kur350Btn.setVisibility(View.GONE);
                kur400Btn.setVisibility(View.GONE);

            }else if(order.getStatus().equalsIgnoreCase(AppConfig.KEY_KUR200)){
                kur300Btn.setVisibility(View.VISIBLE);
                kur300Btn.setImageResource(R.drawable.status01_1_icon);

                kur200BtnS.setVisibility(View.GONE);
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
                kur200BtnS.setVisibility(View.GONE);
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
                kur200BtnS.setVisibility(View.GONE);
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
                kur200BtnS.setVisibility(View.GONE);
                kur350Btn.setVisibility(View.GONE);
                kur400Btn.setVisibility(View.GONE);

                kur300Btn.setImageResource(R.drawable.status01_2_icon);
                kur300Btn.setVisibility(View.VISIBLE);

                kur310Btn.setImageResource(R.drawable.status03_2_icon);
                kur310Btn.setVisibility(View.VISIBLE);

                kur200Btn.setVisibility(View.GONE);
                kur999Btn.setVisibility(View.GONE);
            }else{
                kur200BtnS.setEnabled(false);
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

        retrieve_packet_history(handler);

        // loop till a runtime exception is triggered.
        try { Looper.loop(); }
        catch(RuntimeException e2) {}

        String content = "";
        for(StatusHistory hist : historyList){
            content+= hist.toString();
        }
        showPopupWindow("Order\nStatus Information", content, R.drawable.icon_kurirkurindo);
    }

    private void retrieve_packet_history(final Handler handler) {
        String tag_string_req = "req_retrieve_order_history";

        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_ORDER_HISTORY, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "retrieve_order_history Response: " + response.toString());

                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");
                    if (!error) {
                        GsonBuilder builder = new GsonBuilder();
                        builder.setPrettyPrinting();
                        Gson gson = builder.create();

                        JSONArray jArr = jObj.getJSONArray("histories");
                        for (int i = 0; i < jArr.length(); i++) {
                            StatusHistory hist = gson.fromJson(jArr.get(i).toString(), StatusHistory.class);
                            User by = gson.fromJson(jArr.getJSONObject(i).get("created_by").toString(), User.class);
                            historyList.add(hist);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getContext(), "Json error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
                handler.sendMessage(null);
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
                params.put("awb", order.getAwb());

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

    private void setup_pembeli() {
        if(order.getBuyer() != null) {
            tvPembeli.setText(order.getBuyer().getFirstname() + " " + order.getBuyer().getLastname()
                    + " "+order.getBuyer().getPhone()+"\n"
                    + order.getBuyer().getCityText()
            );
        }
    }

    private void setup_shipping() {
        lvRecipientItems.setLayoutManager(new GridLayoutManager(getContext(), 1));
        lvRecipientItems.setHasFixedSize(true);
        List<Packet> packets = new ArrayList<>();
        if(order.getPackets().size() > 0) packets.addAll( order.getPackets());
        tvPengiriman.setVisibility(View.VISIBLE);
        PacketViewAdapter adapter = new PacketViewAdapter(getContext(), packets);
        lvRecipientItems.setAdapter(adapter);

        /*
        if(order.getRecipients().size() > 0){
            final RecipientViewAdapter recipientAdapter = new RecipientViewAdapter(getActivity(), getRecipientItems(order.getRecipients()));
            lvRecipientItems.setAdapter(recipientAdapter );
        }*/

    }

    private void setup_products() {
        lvCartItems.setLayoutManager(new GridLayoutManager(getContext(), 1));
        lvCartItems.setHasFixedSize(true);

        tvTotalPrice.setText(AppConfig.formatCurrency(order.getTotalPrice().setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue()));

        CartViewAdapter cartAdapter = new CartViewAdapter(getActivity(), getCartItemsPlusHeader(order));
        lvCartItems.setAdapter(cartAdapter);

        tvPageTitle.setText("Detail Order");

        updateStatus();
    }

    private void updateStatus() {
        tvAwbTitle.setText("No. Resi : "+order.getAwb() +"\nStatus : "+ AppConfig.getOrderStatusText(order.getStatus()));
        tvAwbTitle.setVisibility(View.VISIBLE);

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
                        Intent intent = new Intent(getActivity(), AcceptOrderActivity.class);
                        intent.putExtra("order", order);
                        startActivityForResult(intent, ACCEPTED_REQUEST_CODE);
                    }
                });

                kur999Btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getActivity(), RejectOrderActivity.class);
                        intent.putExtra("order", order);
                        startActivityForResult(intent, REJECTED_REQUEST_CODE);
                    }
                });

            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if ((requestCode == ACCEPTED_REQUEST_CODE ||requestCode == REJECTED_REQUEST_CODE )&& resultCode == Activity.RESULT_OK) {
            Order order = data.getExtras().getParcelable("order");
            if(order != null) {
                this.order = order;
                updateStatus();
            }
        }
    }
    private List<Recipient> getRecipientItems(Set<Recipient> recipients) {
        List<Recipient> results = new ArrayList<>();
        //results.add(new Recipient());
        results.addAll(recipients);
        return results;
    }
    private List<CartItem> getCartItemsPlusHeader(Order order) {
        List<CartItem> cartItems = new ArrayList<>();
        CartItem ci = new CartItem();
        ci.setProduct(null);
        ci.setQuantity(0);
        cartItems.add(ci);//headers
        if(order != null && order.getItems() != null)
            cartItems.addAll( order.getItems() );

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
