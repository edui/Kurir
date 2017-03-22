package id.co.kurindo.kurindo.wizard.doshop;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.tonyvu.sc.model.Cart;
import com.android.tonyvu.sc.util.CartHelper;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.lamudi.phonefield.PhoneInputLayout;
import com.stepstone.stepper.Step;
import com.stepstone.stepper.VerificationError;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import id.co.kurindo.kurindo.LoginActivity;
import id.co.kurindo.kurindo.R;
import id.co.kurindo.kurindo.adapter.CartViewAdapter;
import id.co.kurindo.kurindo.adapter.TPacketViewAdapter;
import id.co.kurindo.kurindo.app.AppConfig;
import id.co.kurindo.kurindo.base.BaseActivity;
import id.co.kurindo.kurindo.helper.DoShopHelper;
import id.co.kurindo.kurindo.helper.SessionManager;
import id.co.kurindo.kurindo.helper.ViewHelper;
import id.co.kurindo.kurindo.model.CartItem;
import id.co.kurindo.kurindo.model.TOrder;
import id.co.kurindo.kurindo.model.TPacket;
import id.co.kurindo.kurindo.model.TUser;
import id.co.kurindo.kurindo.wizard.BaseStepFragment;


/**
 * Created by aspire on 11/30/2016.
 */

public class DoShopFormFragment extends BaseStepFragment implements Step{
    private static final String TAG = "DoShopFormFragment";
    VerificationError invalid = null;

    @Bind(R.id.input_nama_penerima)    EditText _namaPenerimaText;
    @Bind(R.id.input_alamat_penerima) EditText _alamatPenerimaText;
    @Bind(R.id.input_telepon_penerima)     PhoneInputLayout _teleponPenerimaText;

    @Bind(R.id.tvPayment)
    TextView tvPayment;

    @Bind(R.id.tvTotalPrice)
    TextView tvTotalPrice;

    @Bind(R.id.ivDoType)
    ImageView ivDoType;
    ProgressDialog progressDialog;

    TOrder order;
    @Bind(R.id.tvPengiriman)
    TextView tvPengiriman;
    @Bind(R.id.lvRecipientItems)
    RecyclerView lvRecipientItems;
    @Bind(R.id.lvCartItems)
    RecyclerView lvCartItems;

    boolean inputBaruPenerima = true;

    private static final String LAYOUT_RESOURCE_ID_ARG_KEY = "messageResourceId";
    private static DoShopFormFragment newInstance(@LayoutRes int layoutResId) {
        Bundle args = new Bundle();
        args.putInt(LAYOUT_RESOURCE_ID_ARG_KEY, layoutResId);
        DoShopFormFragment fragment = new DoShopFormFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        session = new SessionManager(getContext());
        if(!session.isLoggedIn()){
            ((BaseActivity)getActivity()).showActivity(LoginActivity.class);
            getActivity().finish();
            return ;
        }

        if(order == null) order = new TOrder();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflateAndBind(inflater, container, R.layout.fragment_doshop_form);
        //inflater.inflate(R.layout.fragment_confirm_shop_checkout, container, false);
        setupToolbar(v);
        progressDialog = new ProgressDialog(getActivity(),R.style.CustomDialog);

        _teleponPenerimaText.setDefaultCountry(AppConfig.DEFAULT_COUNTRY);
        _teleponPenerimaText.setHint(R.string.telepon_penerima);

        return v;
    }

    private void setup_shipping_form() {

    }
    private void setup_shipping() {
        lvRecipientItems.setLayoutManager(new GridLayoutManager(getContext(), 1));
        lvRecipientItems.setHasFixedSize(true);
        List<TPacket> packets = new ArrayList<>();
        if(order != null && order.getPackets().size() > 0) packets.addAll( order.getPackets());
        //tvPengiriman.setVisibility(View.VISIBLE);
        TPacketViewAdapter adapter = new TPacketViewAdapter(getContext(), packets, order);
        lvRecipientItems.setAdapter(adapter);
    }

    private void setup_products() {
        lvCartItems.setLayoutManager(new GridLayoutManager(getActivity(), 1));
        lvCartItems.setHasFixedSize(true);

        final Cart cart = CartHelper.getCart();
        //tvTotalPrice = (TextView) v.findViewById(R.id.tvTotalPrice);
        tvTotalPrice.setText(AppConfig.formatCurrency(order.getTotalPrice().setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue()));

        CartViewAdapter cartAdapter = new CartViewAdapter(getActivity(), getCartItemsPlusHeader() );
        lvCartItems.setAdapter(cartAdapter);

        //tvPayment = (TextView) v.findViewById(R.id.tvPayment);
        tvPayment.setText(order.getPayment());
    }

    private List<CartItem> getCartItemsPlusHeader() {
        List<CartItem> cartItems = new ArrayList<>();
        CartItem ci = new CartItem();
        ci.setProduct(null);
        ci.setQuantity(0);
        cartItems.add(ci);//headers
        if(order != null && order.getProducts() != null) cartItems.addAll(order.getProducts());

        return cartItems;
    }

    private void process_order(Map<String, String> params, final Handler handler) {
        String url = AppConfig.URL_DOSEND_ORDER;
        addRequest("request_dosend_order", Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "request_dosend_order Response: " + response.toString());
                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean OK = "OK".equalsIgnoreCase(jObj.getString("status"));
                    if(OK){
                        String awb = jObj.getString("awb");
                        String status = jObj.getString("order_status");
                        String statusText = jObj.getString("order_status_text");

                        ViewHelper.getInstance().setOrder(DoShopHelper.getInstance().getOrder());

                        ViewHelper.getInstance().getOrder().setAwb(awb);
                        ViewHelper.getInstance().getOrder().setStatus(status);
                        ViewHelper.getInstance().getOrder().setStatusText(statusText);
                        if(inputBaruPenerima) db.addAddress(DoShopHelper.getInstance().getOrigin());

                    }
                }catch (JSONException e){
                    e.printStackTrace();
                    invalid = new VerificationError("Json error: " + e.getMessage());
                }
                progressDialog.dismiss();
                handler.handleMessage(null);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                volleyError.printStackTrace();
                invalid = new VerificationError("VolleyError : " + volleyError.getMessage());
                progressDialog.dismiss();
                handler.handleMessage(null);
            }
        }, params, getKurindoHeaders());
    }
    private void place_order(Handler handler) {
        Map<String, String> params = new HashMap<>();
        params.put("user_agent", AppConfig.USER_AGENT);

        GsonBuilder builder = new GsonBuilder();
        builder.setPrettyPrinting(); builder.serializeNulls();
        Gson gson = builder.create();

        if(inputBaruPenerima){
            String namaPenerima= _namaPenerimaText.getText().toString();
            //String alamatPenerima = _alamatPenerimaText.getText().toString();
            String teleponPenerima =  _teleponPenerimaText.getPhoneNumber();
            //if(teleponPenerima.startsWith("0")){ teleponPenerima = _teleponPenerimaText.getPhoneNumber().getCountryCode()+""+teleponPenerima; }
            DoShopHelper.getInstance().updateDestination(namaPenerima, teleponPenerima);
            //db.addRecipient(namaPenerima,teleponPenerima, genderPenerima, alamatPenerima,kota_penerima.getCode(), kota_penerima.getText());
        }

        TUser user = db.getUser();
        order.setBuyer(user);

        String orderStr = gson.toJson(DoShopHelper.getInstance().getOrder());
        //Log.d(TAG, "place_an_order: "+orderStr);
        params.put("order", orderStr);

        process_order(params, handler);
    }


    @Override
    public int getName() {
        return R.string.confirm_order;
    }

    @Override
    public VerificationError verifyStep() {
        // make a handler that throws a runtime exception when a message is received
        final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message mesg) {
                throw new RuntimeException();
            }
        };

        DialogInterface.OnClickListener YesClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                place_order(handler);
            }
        };
        showConfirmationDialog("Confirm Order","Anda Yakin akan membeli produk tersebut?", YesClickListener, null);

        //loop till a runtime exception is triggered.
        try { Looper.loop(); }
        catch(RuntimeException e2) {}

        return null;
    }

    @Override
    public void onSelected() {
        order = DoShopHelper.getInstance().getOrder();
        setup_products();
        setup_shipping_form();
        setup_shipping();
        updateUI();
    }

    private void updateUI() {
        TUser destination = DoShopHelper.getInstance().getDestination();
        _teleponPenerimaText.setPhoneNumber("");
        _namaPenerimaText.setText("");
        inputBaruPenerima = true;
        if(destination != null){
            if(destination.getAddress() != null){
                _alamatPenerimaText.setText(destination.getAddress().toStringFormatted());
            }
            if(destination.getFirstname() != null) {
                _teleponPenerimaText.setPhoneNumber(destination.getPhone());
                _namaPenerimaText.setText(destination.getName());
                inputBaruPenerima = false;
            }
        }

/*       if(destination != null ){
            _alamatPenerimaText.setText(destination.getAddress().toStringFormatted());
            _teleponPenerimaText.setPhoneNumber(destination.getPhone());
            _namaPenerimaText.setText(destination.getName());
            inputBaruPenerima = false;
        }else{
            _teleponPenerimaText.setPhoneNumber("");
            _namaPenerimaText.setText("");
            inputBaruPenerima = true;
        }*/
    }

    @Override
    public void onError(@NonNull VerificationError error) {

    }
}
