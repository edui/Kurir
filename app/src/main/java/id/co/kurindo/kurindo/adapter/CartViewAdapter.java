package id.co.kurindo.kurindo.adapter;

/**
 * Created by DwiM on 11/9/2016.
 */

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.tonyvu.sc.model.Cart;
import com.android.tonyvu.sc.util.CartHelper;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import id.co.kurindo.kurindo.R;
import id.co.kurindo.kurindo.app.AppConfig;
import id.co.kurindo.kurindo.app.Constant;
import id.co.kurindo.kurindo.model.CartItem;
import id.co.kurindo.kurindo.model.News;

/**
 * Created by DwiM on 11/9/2016.
 */
public class CartViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;

    Context context;
    private List<CartItem> cartItems = Collections.emptyList();

    public CartViewAdapter(Context context, List<CartItem> data) {
        this.context = context;
        this.cartItems = data;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        View v;
        if(viewType == TYPE_ITEM){
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_cart_item, parent, false);
            viewHolder = new VHItem(v);
        }else if(viewType == TYPE_HEADER){
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_header, parent, false);
            viewHolder = new VHHeader(v);
        }

        return viewHolder;
    }
    public CartItem getItem(int position) {
        return cartItems.get(position);
    }
    public int getItemViewType(int position) {
        if (isPositionHeader(position))
            return TYPE_HEADER;

        return TYPE_ITEM;
    }

    private boolean isPositionHeader(int position) {
        return position == 0;
    }
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if(holder instanceof VHItem){
            //final Cart cart = CartHelper.getCart();
            final CartItem cartItem = getItem(position);
            if(cartItem.getProduct() != null){
                ((VHItem) holder).tvName.setText(cartItem.getProduct().getName() );
                ((VHItem) holder).tvUnitPrice.setText(AppConfig.formatCurrency(cartItem.getProduct().getPrice().setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue()));
                ((VHItem) holder).tvQuantity.setText(String.valueOf(cartItem.getQuantity()));
                ((VHItem) holder).tvPrice.setText(AppConfig.formatCurrency(cartItem.getProduct().getPrice().multiply(new BigDecimal(cartItem.getQuantity())).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue()));
                if(cartItem.getProduct().getNotes() == null || cartItem.getProduct().getNotes().isEmpty()){
                    ((VHItem) holder).tvNotes.setVisibility(View.GONE);
                }else{
                    ((VHItem) holder).tvNotes.setVisibility(View.VISIBLE);
                    ((VHItem) holder).tvNotes.setText("Note: "+cartItem.getProduct().getNotes());
                }
                /*if(cartItem.getQuantity() > 0){
                    ((VHItem) holder).ibPriceUpdate.setVisibility(View.GONE);
                }else{
                    ((VHItem) holder).ibPriceUpdate.setVisibility(View.VISIBLE);
                    ((VHItem) holder).ibPriceUpdate.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                        }
                    });
                }*/

            }
        }
    }

    @Override
    public int getItemCount() {
        return cartItems.size();
    }

    class VHItem extends RecyclerView.ViewHolder {
        TextView tvName;
        TextView tvNotes;
        TextView tvUnitPrice;
        TextView tvQuantity;
        TextView tvPrice;
        //ImageButton ibPriceUpdate;

        public VHItem(View itemView) {
            super(itemView);
            tvName = (TextView) itemView.findViewById(R.id.tvCartItemName);
            tvNotes = (TextView) itemView.findViewById(R.id.tvCartItemNotes);
            tvUnitPrice = (TextView) itemView.findViewById(R.id.tvCartItemUnitPrice);
            tvQuantity = (TextView) itemView.findViewById(R.id.tvCartItemQuantity);
            tvPrice = (TextView) itemView.findViewById(R.id.tvCartItemPrice);
            //ibPriceUpdate= (ImageButton) itemView.findViewById(R.id.ibPriceUpdate);
        }
    }

    class VHHeader extends RecyclerView.ViewHolder {
        TextView tvName;
        TextView tvUnitPrice;
        TextView tvQuantity;
        TextView tvPrice;

        public VHHeader(View itemView) {
            super(itemView);
            tvName = (TextView) itemView.findViewById(R.id.tvCartItemName);
            tvUnitPrice = (TextView) itemView.findViewById(R.id.tvCartItemUnitPrice);
            tvQuantity = (TextView) itemView.findViewById(R.id.tvCartItemQuantity);
            tvPrice = (TextView) itemView.findViewById(R.id.tvCartItemPrice);
        }
    }

}