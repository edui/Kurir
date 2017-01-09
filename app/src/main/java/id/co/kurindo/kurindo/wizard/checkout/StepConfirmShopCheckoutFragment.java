package id.co.kurindo.kurindo.wizard.checkout;

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
import android.widget.ImageView;
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
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import butterknife.Bind;
import id.co.kurindo.kurindo.R;
import id.co.kurindo.kurindo.adapter.CartViewAdapter;
import id.co.kurindo.kurindo.adapter.PacketViewAdapter;
import id.co.kurindo.kurindo.adapter.RecipientViewAdapter;
import id.co.kurindo.kurindo.app.AppConfig;
import id.co.kurindo.kurindo.app.AppController;
import id.co.kurindo.kurindo.helper.CheckoutHelper;
import id.co.kurindo.kurindo.model.CartItem;
import id.co.kurindo.kurindo.model.Order;
import id.co.kurindo.kurindo.model.Packet;
import id.co.kurindo.kurindo.model.Product;
import id.co.kurindo.kurindo.model.Recipient;
import id.co.kurindo.kurindo.model.ShippingCost;
import id.co.kurindo.kurindo.model.Shop;
import id.co.kurindo.kurindo.util.DummyContent;


/**
 * Created by aspire on 11/30/2016.
 */

public class StepConfirmShopCheckoutFragment extends BaseStepFragment implements Step{
    private static final String TAG = "StepConfirmShopCheckout";
    VerificationError invalid = null;

    @Bind(R.id.tvPayment)
    TextView tvPayment;

    @Bind(R.id.tvTotalPrice)
    TextView tvTotalPrice;

    @Bind(R.id.tvShippingPrice)
    TextView tvShippingPrice;

    @Bind(R.id.ivPacketType)
    ImageView ivPacketType;
    PacketViewAdapter packetViewAdapter;
    RecipientViewAdapter recipientAdapter;
    List<Recipient> recipients;
    ProgressDialog progressDialog;
    Map<Integer, ShippingCost> shippingCostMap;
    double totalPrice;

    private static final String LAYOUT_RESOURCE_ID_ARG_KEY = "messageResourceId";
    public static StepConfirmShopCheckoutFragment newInstance(@LayoutRes int layoutResId) {
        Bundle args = new Bundle();
        args.putInt(LAYOUT_RESOURCE_ID_ARG_KEY, layoutResId);
        StepConfirmShopCheckoutFragment fragment = new StepConfirmShopCheckoutFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflateAndBind(inflater, container, R.layout.fragment_confirm_shop_checkout);
        //inflater.inflate(R.layout.fragment_confirm_shop_checkout, container, false);
        setupToolbar(v);
        setup_products(v);
        setup_shipping(v);
        progressDialog = new ProgressDialog(getActivity(),R.style.CustomDialog);

        return v;
    }

    private void setup_shipping(View v) {
        RecyclerView lvRecipientItems = (RecyclerView) v.findViewById(R.id.lvRecipientItems);
        lvRecipientItems.setLayoutManager(new GridLayoutManager(getActivity(), 1));
        lvRecipientItems.setHasFixedSize(true);

        calc_shipping_cost();

        //if(CheckoutHelper.getInstance().getPackets() != null && CheckoutHelper.getInstance().getPackets().size() > 0){
        List<Packet> packets = new ArrayList<>();
        if(CheckoutHelper.getInstance().getPackets() != null){
            packets.addAll(CheckoutHelper.getInstance().getPackets());
        }
        packetViewAdapter = new PacketViewAdapter(getContext(), packets);
        lvRecipientItems.setAdapter(packetViewAdapter);
        /*}else{
            recipients = getRecipientItems(CheckoutHelper.getInstance().getRecipients());
            recipientAdapter = new RecipientViewAdapter(getActivity(), recipients);
            lvRecipientItems.setAdapter(recipientAdapter );*/
        //}
    }

    private void calc_shipping_cost() {
        shippingCostMap = new HashMap<>();
        Set<Saleable> products = CartHelper.getCart().getProducts();
        for (Saleable saleable : products) {
            Product product = (Product) saleable;
            Shop shop = DummyContent.SHOP_MAP.get(""+product.getShopid());
            for (Recipient rec : CheckoutHelper.getInstance().getRecipients()) {
                ShippingCost cost = new ShippingCost();
                cost.setDestination(rec.getAddress().getCity());
                cost.setOrigin(shop.getAddress().getCity());
                cost.setInformation(product.getName());
                cost.setServiceCode(CheckoutHelper.getInstance().getPacketType());
                Integer qty = CartHelper.getCart().getQuantity(saleable);
                //int weight = (int) (qty.intValue() * product.getWeight().doubleValue());
                int weight = 1;
                cost.setWeight(weight);
                shippingCostMap.put(shop.getId(), cost);
            }
        }

        request_shipping_cost();
    }
    private void setup_packet(){
        Set<Saleable> products = CartHelper.getCart().getProducts();
        Set<Packet> packets = new LinkedHashSet<>();
        for (Saleable saleable : products) {
            Product product = (Product) saleable;
            Shop shop = DummyContent.SHOP_MAP.get(""+product.getShopid());
            for (Recipient rec : CheckoutHelper.getInstance().getRecipients()) {
                ShippingCost cost = shippingCostMap.get(shop.getId());
                if(cost != null){
                    Packet p =new Packet();
                    p.setNamaPenerima(rec.getName());
                    p.setKotaPenerima(cost.getDestination().getCode());
                    p.setKotaPenerimaText(cost.getDestination().getText());
                    p.setAlamatPenerima(rec.getAddress().getAlamat());

                    p.setNamaPengirim(shop.getName());
                    p.setAlamatPengirim(shop.getAddress().getAlamat());
                    p.setKotaPengirim(cost.getOrigin().getCode());
                    p.setKotaPengirimText(cost.getOrigin().getText());
                    p.setServiceCode(cost.getServiceCode());
                    p.setInfoPaket(product.getName());

                    p.setBiaya(cost.getCost());
                    p.setBerat(cost.getWeight());
                    packets.add(p);
                }
            }
        }
        CheckoutHelper.getInstance().setPackets(packets);
        packetViewAdapter.updateData(new ArrayList<Packet>(packets));
        packetViewAdapter.notifyDataSetChanged();
    }
    private void request_shipping_cost() {

        String tag_string_req = "req_packet_price_group";
        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_PACKET_PRICE_GROUP, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Price Check Response: " + response.toString());

                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");
                    if (!error) {
                        JSONArray datas = jObj.getJSONArray("data");
                        double totalCost = 0;
                        for (int j = 0; j < datas.length(); j++) {
                            JSONObject  prices = datas.getJSONObject(j);
                            ShippingCost cost = (ShippingCost) shippingCostMap.get(prices.getInt("id"));
                            if(cost != null) cost.setCost(prices.getDouble("price"));
                            totalCost += cost.getCost();
                        }
                        tvShippingPrice.setText(AppConfig.formatCurrency(totalCost));
                        setup_packet();
                    }
                    //}else{
                    //    String message= jObj.getString("message");
                    //Toast.makeText(getApplicationContext(), "Error: " + message, Toast.LENGTH_LONG).show();
                    //}
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getContext(), "Json error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                Log.e(TAG, "Price Check Error: " + error.getMessage());
                //Toast.makeText(getApplicationContext(),error.getMessage(), Toast.LENGTH_LONG).show();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting params to register url
                Map<String,String> params = new HashMap<>();
                GsonBuilder builder = new GsonBuilder();
                builder.setPrettyPrinting();
                Gson gson = builder.create();
                //Log.d("params",gson.toJson(shippingCostMap).toString());
                params.put("params", gson.toJson(shippingCostMap));
                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    private List<Recipient> getRecipientItems(Set<Recipient> recipients) {
        List<Recipient> results = new ArrayList<>();
        //results.add(new Recipient());
        results.addAll(recipients);
        return results;
    }

    private void setup_products(View v) {
        RecyclerView lvCartItems = (RecyclerView) v.findViewById(R.id.lvCartItems);
        lvCartItems.setLayoutManager(new GridLayoutManager(getActivity(), 1));
        lvCartItems.setHasFixedSize(true);

        final Cart cart = CartHelper.getCart();
        //tvTotalPrice = (TextView) v.findViewById(R.id.tvTotalPrice);
        tvTotalPrice.setText(AppConfig.formatCurrency(cart.getTotalPrice().setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue()));

        CartViewAdapter cartAdapter = new CartViewAdapter(getActivity(), getCartItemsPlusHeader(cart));
        lvCartItems.setAdapter(cartAdapter);

        //tvPayment = (TextView) v.findViewById(R.id.tvPayment);
        tvPayment.setText(CheckoutHelper.getInstance().getPayment());
    }

    private List<CartItem> getCartItemsPlusHeader(Cart cart) {
        List<CartItem> cartItems = new ArrayList<CartItem>();
        Map<Saleable, Integer> itemMap = cart.getItemWithQuantity();
        CartItem ci = new CartItem();
        ci.setProduct(null);
        ci.setQuantity(0);
        cartItems.add(ci);//headers
        for (Map.Entry<Saleable, Integer> entry : itemMap.entrySet()) {
            CartItem cartItem = new CartItem();
            cartItem.setProduct((Product) entry.getKey());
            cartItem.setQuantity(entry.getValue());
            cartItems.add(cartItem);
        }
        return cartItems;
    }

    private void place_order(final Handler handler) {
        String tag_string_req = "req_place_shop_order";
        AppController.getInstance().cancelPendingRequests(tag_string_req);

        progressDialog.setMessage("Processing your order....");
        progressDialog.show();

        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_PLACE_ORDER_SHOP, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Process_order Response: " + response.toString());
                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");
                    if (!error) {
                        Order order= new Order();
                        order.setAwb(jObj.getString("awb"));
                        order.setStatus(jObj.getString("status"));
                        order.setStatusText(jObj.getString("statusText"));
                        CheckoutHelper.getInstance().setFinishOrder(order);
                    }
                    progressDialog.dismiss();
                } catch (JSONException e) {
                    e.printStackTrace();
                    invalid = new VerificationError("Json error: " + e.getMessage());
                    progressDialog.dismiss();
                }

                handler.handleMessage(null);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Process_order Error: " + error.getMessage());
                invalid = new VerificationError(error.getMessage());
                progressDialog.dismiss();
                handler.handleMessage(null);
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting params to register url
                HashMap<String, String> params = new HashMap<>();
                GsonBuilder builder = new GsonBuilder();
                builder.setPrettyPrinting();
                builder.serializeNulls();
                Gson gson = builder.create();

                CheckoutHelper.getInstance().setBuyer(db.toUser(db.getUserDetails()));
                CheckoutHelper.getInstance().setShippingCost(shippingCostMap);

                String pembeli =  gson.toJson(CheckoutHelper.getInstance().getBuyer());
                String penerima = gson.toJson(CheckoutHelper.getInstance().getRecipients());
                String products =gson.toJson(CheckoutHelper.getInstance().getProductsAsParam());
                String payment = (CheckoutHelper.getInstance().getPayment()==null? tvPayment.getText().toString() :CheckoutHelper.getInstance().getPayment()) ;
                String shipping =gson.toJson(shippingCostMap);

                params.put("pembeli",pembeli);
                params.put("penerima", penerima);
                params.put("produk", products);
                params.put("shipping", shipping);
                params.put("payment", payment);
                params.put("totalPrice", ""+totalPrice);
                params.put("user_agent", AppConfig.USER_AGENT);
                //Log.d("PARAMS",gson.toJson(params));
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

        // loop till a runtime exception is triggered.
        try { Looper.loop(); }
        catch(RuntimeException e2) {}

        return null;
    }

    @Override
    public void onSelected() {
        updateUI();
    }

    private void updateUI() {
        Cart cart = CartHelper.getCart();
        tvPayment.setText(CheckoutHelper.getInstance().getPayment());
        totalPrice = cart.getTotalPrice().setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
        double shippingCost = 0;
        for (Map.Entry<Integer, ShippingCost> entry : shippingCostMap.entrySet()) {
            ShippingCost cost = entry.getValue();
            shippingCost += cost.getCost();
        }
        tvShippingPrice.setText(AppConfig.formatCurrency(shippingCost));

        totalPrice += shippingCost;
        tvTotalPrice.setText(AppConfig.formatCurrency( totalPrice));
        if(CheckoutHelper.getInstance().getPacketType() != null){
            ivPacketType.setVisibility(View.VISIBLE);
            ivPacketType.setImageResource(CheckoutHelper.getInstance().getPacketType().equalsIgnoreCase(AppConfig.PACKET_SDS) ? R.drawable.icon_sds : R.drawable.icon_nds);
        }
        else {
            ivPacketType.setVisibility(View.GONE);
        }
        //recipients = getRecipientItems(CheckoutHelper.getInstance().getRecipients());
        //recipientAdapter.notifyDataSetChanged();
    }

    @Override
    public void onError(@NonNull VerificationError error) {

    }
}
