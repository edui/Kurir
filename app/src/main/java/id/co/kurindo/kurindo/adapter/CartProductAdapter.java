package id.co.kurindo.kurindo.adapter;

import android.content.Context;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.tonyvu.sc.model.Cart;
import com.android.tonyvu.sc.util.CartHelper;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import id.co.kurindo.kurindo.R;
import id.co.kurindo.kurindo.app.AppConfig;
import id.co.kurindo.kurindo.model.CartItem;

/**
 * Created by dwim on 1/5/2017.
 */

public class CartProductAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private List<CartItem> cartItems = Collections.emptyList();

    private final Context context;
    private OnItemClickListener itemClickListener;

    public CartProductAdapter(Context context, OnItemClickListener mOnItemClickListener) {
        this.context = context;
        this.itemClickListener = mOnItemClickListener;
    }
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder;
        View v;
        v = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.adapter_cart_product, parent, false);
        viewHolder = new ViewHolder(v);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        final Cart cart = CartHelper.getCart();
        final CartItem cartItem = getItem(position);
        ((ViewHolder)holder).tvProductTitle.setText(cartItem.getProduct().getName());
        ((ViewHolder)holder).tvProductPrice.setText(AppConfig.formatCurrency(cartItem.getProduct().getPrice().setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue()));
        ((ViewHolder)holder).quantityStr.setText(String.valueOf(cartItem.getQuantity()));
        ((ViewHolder)holder).inputSpecialRequest.setText(cartItem.getProduct().getNotes());

        int resId = this.context.getResources().getIdentifier(cartItem.getProduct().getImageName().substring(0,cartItem.getProduct().getImageName().length()-4), "drawable", this.context.getPackageName());
        if(resId >0) ((ViewHolder)holder).ivProductImage.setImageResource(resId);

        ((ViewHolder)holder).incrementBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemClickListener.onIncrementBtnClick(v, position, ((ViewHolder)holder).quantityStr);
            }
        });
        ((ViewHolder)holder).decrementBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemClickListener.onDecrementBtnClick(v, position, ((ViewHolder)holder).quantityStr);
            }
        });
        ((ViewHolder)holder).deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemClickListener.onDeleteBtnClick(v, position);
            }
        });

        ((ViewHolder)holder).viewBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemClickListener.onViewBtnClick(v, position);
            }
        });
    }
    public CartItem getItem(int position) {
        return cartItems.get(position);
    }

    @Override
    public int getItemCount() {
        return cartItems.size();
    }

    public void removeItem(int position) {
        cartItems.remove(position);
    }
    public void updateCartItems(List<CartItem> cartItems) {
        this.cartItems = cartItems;
        notifyDataSetChanged();
    }


    private static class ViewHolder extends RecyclerView.ViewHolder{
        public final TextView tvProductPrice;
        public final TextView tvProductTitle;
        public final TextView quantityStr;
        public final AppCompatButton incrementBtn;
        public final AppCompatButton decrementBtn;
        public final ImageView ivProductImage;
        public final ImageButton viewBtn;
        public final ImageButton deleteBtn;
        public final TextView inputSpecialRequest;


        public ViewHolder( View convertView){
            super(convertView);
            tvProductTitle = (TextView) convertView.findViewById(R.id.tvProductTitle);
            tvProductPrice= (TextView) convertView.findViewById(R.id.tvProductPrice);
            quantityStr = (TextView) convertView.findViewById(R.id.quantityStr);
            incrementBtn = (AppCompatButton) convertView.findViewById(R.id.incrementBtn);
            decrementBtn = (AppCompatButton) convertView.findViewById(R.id.decrementBtn);
            ivProductImage= (ImageView) convertView.findViewById(R.id.ivProductImage);
            viewBtn = (ImageButton) convertView.findViewById(R.id.viewBtn);
            deleteBtn = (ImageButton) convertView.findViewById(R.id.deleteBtn);
            inputSpecialRequest = (TextView) convertView.findViewById(R.id.input_special_request);

        }
    }
    public interface OnItemClickListener {
        void onIncrementBtnClick(View view, int position, TextView tvQuantity);
        void onDecrementBtnClick(View view, int position, TextView tvQuantity);
        void onViewBtnClick(View view, int position);
        void onDeleteBtnClick(View view, int position);
    }

}
