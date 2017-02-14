package id.co.kurindo.kurindo;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.android.tonyvu.sc.model.Cart;
import com.android.tonyvu.sc.model.Saleable;
import com.android.tonyvu.sc.util.CartHelper;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import id.co.kurindo.kurindo.adapter.CartProductAdapter;
import id.co.kurindo.kurindo.adapter.CartProductItemAdapter;
import id.co.kurindo.kurindo.app.AppConfig;
import id.co.kurindo.kurindo.app.Constant;
import id.co.kurindo.kurindo.base.BaseFragment;
import id.co.kurindo.kurindo.model.CartItem;
import id.co.kurindo.kurindo.model.Product;
import id.co.kurindo.kurindo.wizard.ShopCheckoutActivity;
import id.co.kurindo.kurindo.wizard.doshop.DoShopCheckoutActivity;

/**
 * Created by aspire on 11/25/2016.
 */

public class ShoppingCartFragment extends BaseFragment implements CartProductAdapter.OnItemClickListener {
    private static final String TAG = "ShoppingCartFragment";

    //@Bind(R.id.lvCartItems)
    //ListView lvCartItems;
    //CartProductItemAdapter cartAdapter;

    @Bind(R.id.recycleCartItems)
    RecyclerView recycleCartItems;
    CartProductAdapter cartAdapter;

    @Bind(R.id.bClear)
    Button bClear;
    @Bind(R.id.bShop)
    Button bShop;
    @Bind(R.id.bCheckout)
    Button bCheckout;
    @Bind(R.id.tvTotalPrice)
    TextView tvTotalPrice;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflateAndBind(inflater, container, R.layout.activity_shopingcart);

        final Cart cart = CartHelper.getCart();
        tvTotalPrice.setText(AppConfig.formatCurrency(cart.getTotalPrice().setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue()));

        //lvCartItems.addHeaderView(inflater.inflate(R.layout.cart_header, lvCartItems, false));

        //final CartItemAdapter cartAdapter = new CartItemAdapter(getActivity());
        //cartAdapter.updateCartItems(getCartItems(cart));

        //cartAdapter = new CartProductItemAdapter(getActivity(), this);
        //cartAdapter.updateCartItems(getCartItems(cart));
        //lvCartItems.setAdapter(cartAdapter);

        cartAdapter = new CartProductAdapter(getContext(), this);
        cartAdapter.updateCartItems(getCartItems(cart));
        recycleCartItems.setAdapter(cartAdapter);
        recycleCartItems.setLayoutManager( new LinearLayoutManager(getContext()));
        recycleCartItems.setHasFixedSize(true);

        bClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Clearing the shopping cart");
                cart.clear();
                cartAdapter.updateCartItems(getCartItems(cart));
                cartAdapter.notifyDataSetChanged();
                tvTotalPrice.setText(AppConfig.formatCurrency(cart.getTotalPrice().setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue()));
                //( (ShoppingCartActivity) getActivity()).updateCartCount();
            }
        });

        bShop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finish();
            }
        });

        bCheckout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Intent intent = new Intent(getActivity(), CheckoutStepper.class);
                if(CartHelper.getCart().getTotalQuantity() > 0){
                    Intent intent = new Intent(getActivity(), DoShopCheckoutActivity.class);
                    startActivity(intent);
                }
                getActivity().finish();
            }
        });

        /*lvCartItems.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                new AlertDialog.Builder(getActivity())
                        .setTitle(getResources().getString(R.string.delete_item))
                        .setMessage(getResources().getString(R.string.delete_item_message))
                        .setPositiveButton(getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                List<CartItem> cartItems = getCartItems(cart);
                                cart.remove(cartItems.get(position-1).getProduct());
                                cartItems.remove(position-1);
                                cartAdapter.updateCartItems(cartItems);
                                tvTotalPrice.setText(AppConfig.formatCurrency(cart.getTotalPrice().setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue()));
                            }
                        })
                        .setNegativeButton(getResources().getString(R.string.no), null)
                        .show();
                return false;
            }
        });

        lvCartItems.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(position >0){
                    Bundle bundle = new Bundle();
                    List<CartItem> cartItems = getCartItems(cart);
                    Product product = cartItems.get(position-1).getProduct();
                    Log.d(TAG, "Viewing product: " + product.getName());
                    //bundle.putSerializable("product", product);
                    bundle.putParcelable("product", product);
                    bundle.putString("shopid", ""+product.getShopid());
                    Intent intent = new Intent(getActivity(), ProductActivity.class);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
            }
        });*/

        return  rootView;
    }


    private List<CartItem> getCartItems(Cart cart) {
        List<CartItem> cartItems = new ArrayList<CartItem>();
        Log.d(TAG, "Current shopping cart: " + cart);

        Map<Saleable, Integer> itemMap = cart.getItemWithQuantity();

        for (Map.Entry<Saleable, Integer> entry : itemMap.entrySet()) {
            CartItem cartItem = new CartItem();
            cartItem.setProduct((Product) entry.getKey());
            cartItem.setQuantity(entry.getValue());
            cartItems.add(cartItem);
        }

        Log.d(TAG, "Cart item list: " + cartItems);
        return cartItems;
    }

    @Override
    public void onIncrementBtnClick(View view, int position, TextView target) {
        int q = 0;
        try{ q = Integer.parseInt(target.getText().toString());}catch (Exception e){};
        q++;
        target.setText(""+q);
        Cart cart = CartHelper.getCart();
        List<CartItem> cartItems = getCartItems(cart);
        cart.update(cartItems.get(position).getProduct(), q);
        cartAdapter.getItem(position).setQuantity(q);
        cartAdapter.notifyItemChanged(position);
        //cartAdapter.notifyDataSetChanged();
        tvTotalPrice.setText(AppConfig.formatCurrency(cart.getTotalPrice().setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue()));
    }

    @Override
    public void onDecrementBtnClick(View view, int position, TextView target) {
        int q = 1;
        try{ q = Integer.parseInt(target.getText().toString());}catch (Exception e){};
        q--;
        if(q < 1) q =1;
        target.setText(""+q);
        Cart cart = CartHelper.getCart();
        List<CartItem> cartItems = getCartItems(cart);
        cart.update(cartItems.get(position).getProduct(), q);
        cartAdapter.getItem(position).setQuantity(q);
        cartAdapter.notifyItemChanged(position);
        tvTotalPrice.setText(AppConfig.formatCurrency(cart.getTotalPrice().setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue()));
    }

    @Override
    public void onViewBtnClick(View view, int position) {
        Bundle bundle = new Bundle();
        Cart cart = CartHelper.getCart();
        List<CartItem> cartItems = getCartItems(cart);
        Product product = cartItems.get(position).getProduct();
        Log.d(TAG, "Viewing product: " + product.getName());
        //bundle.putSerializable("product", product);
        bundle.putParcelable("product", product);
        bundle.putString("shopid", ""+product.getShopid());
        Intent intent = new Intent(getActivity(), ProductActivity.class);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    @Override
    public void onDeleteBtnClick(View view, int position) {
        Cart cart = CartHelper.getCart();
        List<CartItem> cartItems = getCartItems(cart);
        cart.remove(cartItems.get(position).getProduct());
        cartAdapter.removeItem(position);
        cartAdapter.notifyDataSetChanged();
        tvTotalPrice.setText(AppConfig.formatCurrency(cart.getTotalPrice().setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue()));
    }
}