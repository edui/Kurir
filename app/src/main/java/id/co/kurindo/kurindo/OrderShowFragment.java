package id.co.kurindo.kurindo;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
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
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.DefaultSliderView;

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
import java.util.List;
import java.util.Map;
import java.util.Set;

import butterknife.Bind;
import id.co.kurindo.kurindo.adapter.CartViewAdapter;
import id.co.kurindo.kurindo.adapter.NewsAdapter;
import id.co.kurindo.kurindo.adapter.RecipientViewAdapter;
import id.co.kurindo.kurindo.app.AppConfig;
import id.co.kurindo.kurindo.app.Constant;
import id.co.kurindo.kurindo.base.BaseFragment;
import id.co.kurindo.kurindo.helper.CheckoutHelper;
import id.co.kurindo.kurindo.model.CartItem;
import id.co.kurindo.kurindo.model.News;
import id.co.kurindo.kurindo.model.Order;
import id.co.kurindo.kurindo.model.Product;
import id.co.kurindo.kurindo.model.Recipient;
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

    @Bind(R.id.tvPembeli)
    TextView tvPembeli;

    @Bind(R.id.kur200Btn)
    ImageButton kur200Btn;
    @Bind(R.id.kur999Btn)
    ImageButton kur999Btn;


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

        return view;
    }

    private void setup_pembeli() {
        if(order.getBuyer() != null) {
            tvPembeli.setText(order.getBuyer().getFirstname() + " " + order.getBuyer().getLastname() + "\n"
                    + order.getBuyer().getCityText()
            );
        }
    }

    private void setup_shipping() {
        lvRecipientItems.setLayoutManager(new GridLayoutManager(getContext(), 1));
        lvRecipientItems.setHasFixedSize(true);

        final RecipientViewAdapter recipientAdapter = new RecipientViewAdapter(getActivity(), getRecipientItems(order.getRecipients()));
        lvRecipientItems.setAdapter(recipientAdapter );
    }

    private void setup_products() {
        lvCartItems.setLayoutManager(new GridLayoutManager(getContext(), 1));
        lvCartItems.setHasFixedSize(true);

        tvTotalPrice.setText(AppConfig.formatCurrency(order.getTotalPrice().setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue()));

        CartViewAdapter cartAdapter = new CartViewAdapter(getActivity(), getCartItemsPlusHeader(order));
        lvCartItems.setAdapter(cartAdapter);

        tvPageTitle.setText("Detail Order");

        tvAwbTitle.setText("No. Resi : "+order.getAwb() +"\nStatusHistory : "+ AppConfig.getOrderStatusText(order.getStatus()));
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
                        startActivityForResult(intent, 1);
                    }
                });

                kur999Btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getActivity(), RejectOrderActivity.class);
                        intent.putExtra("order", order);
                        startActivityForResult(intent, 0);
                    }
                });

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
