package id.co.kurindo.kurindo.wizard.dosend;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.stepstone.stepper.Step;
import com.stepstone.stepper.VerificationError;

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
import id.co.kurindo.kurindo.R;
import id.co.kurindo.kurindo.adapter.CartViewAdapter;
import id.co.kurindo.kurindo.adapter.PacketViewAdapter;
import id.co.kurindo.kurindo.adapter.TPacketViewAdapter;
import id.co.kurindo.kurindo.app.AppConfig;
import id.co.kurindo.kurindo.app.AppController;
import id.co.kurindo.kurindo.helper.OrderViaMapHelper;
import id.co.kurindo.kurindo.model.CartItem;
import id.co.kurindo.kurindo.model.Order;
import id.co.kurindo.kurindo.model.StatusHistory;
import id.co.kurindo.kurindo.model.TOrder;
import id.co.kurindo.kurindo.model.TPacket;
import id.co.kurindo.kurindo.wizard.AcceptOrderActivity;
import id.co.kurindo.kurindo.wizard.BaseStepFragment;
import id.co.kurindo.kurindo.wizard.RejectOrderActivity;

import static id.co.kurindo.kurindo.R.style.CustomDialog;

/**
 * Created by dwim on 2/7/2017.
 */

public class StepSummaryFragment extends BaseStepFragment implements Step {
    private static final String TAG = "StepSummaryFragment";

    TOrder order;
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


    ProgressDialog progressDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        progressDialog = new ProgressDialog(getActivity(), CustomDialog);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflateAndBind(inflater, container, R.layout.fragment_order_show);
        //order = OrderViaMapHelper.getInstance().getOrder();
        setup_pembeli();
        setup_products();
        setup_shipping();
        setup_status();
        return v;
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

    private void setup_shipping() {
        lvRecipientItems.setLayoutManager(new GridLayoutManager(getContext(), 1));
        lvRecipientItems.setHasFixedSize(true);
        List<TPacket> packets = new ArrayList<>();
        if(order != null && order.getPackets().size() > 0) packets.addAll( order.getPackets());
        tvPengiriman.setVisibility(View.VISIBLE);
        TPacketViewAdapter adapter = new TPacketViewAdapter(getContext(), packets);
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

        tvTotalPrice.setText(AppConfig.formatCurrency( (order ==null ? 0 : order.getTotalPrice().setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue() )));

        CartViewAdapter cartAdapter = new CartViewAdapter(getActivity(), getCartItemsPlusHeader(order));
        lvCartItems.setAdapter(cartAdapter);

        tvPageTitle.setText("Detail Order");

        updateStatus();
    }
    private List<CartItem> getCartItemsPlusHeader(TOrder order) {
        List<CartItem> cartItems = new ArrayList<>();
        CartItem ci = new CartItem();
        ci.setProduct(null);
        ci.setQuantity(0);
        cartItems.add(ci);//headers
        if(order != null && order.getProducts() != null)
            cartItems.addAll( order.getProducts() );
        return cartItems;
    }

    private void updateStatus() {
        tvAwbTitle.setText("No. Resi : "+(order == null ? "":order.getAwb()) +"\nStatus : "+ (order == null ? "": AppConfig.getOrderStatusText(order.getStatus())));
        tvAwbTitle.setVisibility(View.VISIBLE);

        kur200Btn.setVisibility(View.GONE);
        kur999Btn.setVisibility(View.GONE);
        if(session.isKurir() || session.isAdministrator()){
            if(order != null){
                if(order.getStatus().equalsIgnoreCase(AppConfig.KEY_KUR100)
                        || order.getStatus().equalsIgnoreCase(AppConfig.KEY_KUR101)){
                    kur200Btn.setVisibility(View.VISIBLE);
                    kur999Btn.setVisibility(View.VISIBLE);
                    kur200Btn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(getActivity(), AcceptOrderActivity.class);
                            //OrderHelper.getInstance().setOrder(order);
                            //intent.putExtra("order", order);
                            startActivityForResult(intent, ACCEPTED_REQUEST_CODE);
                        }
                    });

                    kur999Btn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(getActivity(), RejectOrderActivity.class);
                            //intent.putExtra("order", order);
                            //OrderHelper.getInstance().setOrder(order);
                            startActivityForResult(intent, REJECTED_REQUEST_CODE);
                        }
                    });

                }
            }
        }
    }
    private void setup_pembeli() {
        if(order != null && order.getBuyer() != null) {
            tvPembeli.setText(order.getBuyer().getFirstname() + " " + order.getBuyer().getLastname()
                    + " "+order.getBuyer().getPhone()+"\n"
                    + order.getBuyer().getAddress().toStringFormatted()
            );
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
        progressDialog.show();

        retrieve_packet_history(handler);

        // loop till a runtime exception is triggered.
        try { Looper.loop(); }
        catch(RuntimeException e2) {}

        String content = "";
        for(StatusHistory hist : historyList){
            content+= hist.toString();
        }
        progressDialog.dismiss();
        showPopupWindow("Order\nStatus Information", content, R.drawable.icon_kurirkurindo);
    }

    private void retrieve_packet_history(final Handler handler) {
        Map params = new HashMap();
        params.put("awb", order.getAwb());
        addRequest("req_retrieve_order_history", Request.Method.POST, AppConfig.URL_TORDER_HISTORY, new Response.Listener<String>() {

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
                        if(jArr.length() > 0){
                            historyList.clear();
                            for (int i = 0; i < jArr.length(); i++) {
                                StatusHistory hist = gson.fromJson(jArr.get(i).toString(), StatusHistory.class);
                                //User by = gson.fromJson(jArr.getJSONObject(i).get("created_by").toString(), User.class);
                                historyList.add(hist);
                            }
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
        }, params, getKurindoHeaders());

    }

    @Override
    public int getName() {
        return 0;
    }

    @Override
    public VerificationError verifyStep() {
        return null;
    }

    @Override
    public void onSelected() {
        order = OrderViaMapHelper.getInstance().getOrder();
        setup_pembeli();
        setup_products();
        setup_shipping();
        setup_status();

    }

    @Override
    public void onError(@NonNull VerificationError error) {

    }
}
