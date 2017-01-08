package id.co.kurindo.kurindo.wizard.checkout;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.tonyvu.sc.model.Cart;
import com.android.tonyvu.sc.model.Saleable;
import com.android.tonyvu.sc.util.CartHelper;
import com.stepstone.stepper.Step;
import com.stepstone.stepper.VerificationError;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import butterknife.Bind;
import id.co.kurindo.kurindo.R;
import id.co.kurindo.kurindo.adapter.CartViewAdapter;
import id.co.kurindo.kurindo.adapter.RecipientViewAdapter;
import id.co.kurindo.kurindo.app.AppConfig;
import id.co.kurindo.kurindo.app.Constant;
import id.co.kurindo.kurindo.helper.CheckoutHelper;
import id.co.kurindo.kurindo.model.CartItem;
import id.co.kurindo.kurindo.model.Order;
import id.co.kurindo.kurindo.model.Product;
import id.co.kurindo.kurindo.model.Recipient;
import id.co.kurindo.kurindo.model.ShippingCost;

/**
 * Created by DwiM on 12/5/2016.
 */

public class StepSummaryFragment extends BaseStepFragment implements Step{
    @Bind(R.id.tvPayment)
    TextView tvPayment;
    @Bind(R.id.tvTotalPrice)
    TextView tvTotalPrice;
    @Bind(R.id.tvAwbTitle)
    TextView tvAwb;

    @Bind(R.id.tvShippingPrice)
    TextView tvShippingPrice;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflateAndBind(inflater, container, R.layout.fragment_confirm_shop_checkout);
        setupToolbar(v);
        setup_products(v);
        setup_shipping(v);

        return v;
    }
    private void setup_shipping(View v) {
        RecyclerView lvRecipientItems = (RecyclerView) v.findViewById(R.id.lvRecipientItems);
        lvRecipientItems.setLayoutManager(new GridLayoutManager(getContext(), 1));
        lvRecipientItems.setHasFixedSize(true);

        final RecipientViewAdapter recipientAdapter = new RecipientViewAdapter(getActivity(), getRecipientItems(CheckoutHelper.getInstance().getRecipients()));
        lvRecipientItems.setAdapter(recipientAdapter );

        double shippingCost = 0;
        Map<Integer, ShippingCost> shippingCostMap = CheckoutHelper.getInstance().getShippingCost();
        if(shippingCostMap != null){
            for (Map.Entry<Integer, ShippingCost> entry : shippingCostMap.entrySet()) {
                ShippingCost cost = entry.getValue();
                shippingCost += cost.getCost();
            }
            tvShippingPrice.setText(AppConfig.formatCurrency(shippingCost));
        }

    }

    private List<Recipient> getRecipientItems(Set<Recipient> recipients) {
        List<Recipient> results = new ArrayList<>();
        //results.add(new Recipient());
        results.addAll(recipients);
        return results;
    }
    private void setup_products(View v) {
        RecyclerView lvCartItems = (RecyclerView) v.findViewById(R.id.lvCartItems);
        lvCartItems.setLayoutManager(new GridLayoutManager(getContext(), 1));
        lvCartItems.setHasFixedSize(true);

        final Cart order = CartHelper.getCart();
        tvTotalPrice.setText(AppConfig.formatCurrency(order.getTotalPrice().setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue()));

        CartViewAdapter cartAdapter = new CartViewAdapter(getActivity(), getCartItemsPlusHeader(order));
        lvCartItems.setAdapter(cartAdapter);

        tvPayment.setText(CheckoutHelper.getInstance().getPayment());

        TextView tvTitle= (TextView) v.findViewById(R.id.tvPageTitle);
        tvTitle.setText("Order Summary");
    }
    private List<CartItem> getCartItemsPlusHeader(Cart order) {
        List<CartItem> cartItems = new ArrayList<CartItem>();
        Map<Saleable, Integer> itemMap = order.getItemWithQuantity();
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
        Order order = CheckoutHelper.getInstance().getOrder();
        if(order != null){
            tvAwb.setText("No. Resi : "+order.getAwb() +"\nStatusHistory : "+ AppConfig.getOrderStatusText(order.getStatus()));
            tvAwb.setVisibility(View.VISIBLE);
        }

        double shippingCost = 0;
        Map<Integer, ShippingCost> shippingCostMap = CheckoutHelper.getInstance().getShippingCost();
        if(shippingCostMap != null){
            for (Map.Entry<Integer, ShippingCost> entry : shippingCostMap.entrySet()) {
                ShippingCost cost = entry.getValue();
                shippingCost += cost.getCost();
            }
            tvShippingPrice.setText(AppConfig.formatCurrency(shippingCost));
        }

        CartHelper.getCart().clear();
    }

    @Override
    public void onError(@NonNull VerificationError error) {

    }
}
