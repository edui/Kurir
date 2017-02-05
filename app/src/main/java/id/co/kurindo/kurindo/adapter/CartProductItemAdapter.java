package id.co.kurindo.kurindo.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.support.v7.widget.AppCompatButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.tonyvu.sc.model.Cart;
import com.android.tonyvu.sc.util.CartHelper;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import butterknife.Bind;
import id.co.kurindo.kurindo.R;
import id.co.kurindo.kurindo.app.AppConfig;
import id.co.kurindo.kurindo.app.Constant;
import id.co.kurindo.kurindo.model.CartItem;

import static id.co.kurindo.kurindo.R.id.quantityStr;

/**
 * Created by dwim on 1/4/2017.
 */

public class CartProductItemAdapter extends BaseAdapter {
    private static final String TAG = "CartProductItemAdapter";

    private List<CartItem> cartItems = Collections.emptyList();

    private final Context context;
    private OnItemClickListener itemClickListener;

    public CartProductItemAdapter(Context context, OnItemClickListener mOnItemClickListener) {
        this.context = context;
        this.itemClickListener = mOnItemClickListener;
    }

    public void updateCartItems(List<CartItem> cartItems) {
        this.cartItems = cartItems;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return cartItems.size();
    }

    @Override
    public CartItem getItem(int position) {
        return cartItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        TextView tvProductTitle;
        TextView tvProductPrice;
        final TextView quantityStr;
        final AppCompatButton incrementBtn;
        AppCompatButton decrementBtn;
        ImageView ivProductImage;
        ImageButton viewBtn;
        ImageButton deleteBtn;
        if (convertView == null) {
            convertView = LayoutInflater.from(context)
                    .inflate(R.layout.adapter_cart_product, parent, false);
            tvProductTitle = (TextView) convertView.findViewById(R.id.tvProductTitle);
            tvProductPrice= (TextView) convertView.findViewById(R.id.tvProductPrice);
            quantityStr = (TextView) convertView.findViewById(R.id.quantityStr);
            incrementBtn = (AppCompatButton) convertView.findViewById(R.id.incrementBtn);
            decrementBtn = (AppCompatButton) convertView.findViewById(R.id.decrementBtn);
            ivProductImage= (ImageView) convertView.findViewById(R.id.ivProductImage);
            viewBtn = (ImageButton) convertView.findViewById(R.id.viewBtn);
            deleteBtn = (ImageButton) convertView.findViewById(R.id.deleteBtn);
            convertView.setTag(new ViewHolder(tvProductTitle,tvProductPrice, quantityStr, incrementBtn,decrementBtn,ivProductImage, viewBtn, deleteBtn));
        } else {
            ViewHolder viewHolder = (ViewHolder) convertView.getTag();
            tvProductTitle= viewHolder.tvProductTitle;
            tvProductPrice = viewHolder.tvProductPrice;
            quantityStr = viewHolder.quantityStr;
            incrementBtn = viewHolder.incrementBtn;
            decrementBtn = viewHolder.decrementBtn;
            ivProductImage = viewHolder.ivProductImage;
            viewBtn= viewHolder.viewBtn;
            deleteBtn = viewHolder.deleteBtn;
        }

        final Cart cart = CartHelper.getCart();
        final CartItem cartItem = getItem(position);
        tvProductTitle.setText(cartItem.getProduct().getName());
        tvProductPrice.setText(AppConfig.CURRENCY+String.valueOf(cartItem.getProduct().getPrice().setScale(2, BigDecimal.ROUND_HALF_UP)));
        quantityStr.setText(String.valueOf(cartItem.getQuantity()));
        int resId = this.context.getResources().getIdentifier(cartItem.getProduct().getImageName().substring(0,cartItem.getProduct().getImageName().length()-4), "drawable", this.context.getPackageName());
        if(resId >0) ivProductImage.setImageResource(resId);

        incrementBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemClickListener.onIncrementBtnClick(v, position, quantityStr);
            }
        });
        decrementBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemClickListener.onDecrementBtnClick(v, position, quantityStr);
            }
        });
        return convertView;
    }


    private static class ViewHolder {
        public final TextView tvProductPrice;
        public final TextView tvProductTitle;
        public final TextView quantityStr;
        public final AppCompatButton incrementBtn;
        public final AppCompatButton decrementBtn;
        public final ImageView ivProductImage;
        public final ImageButton viewBtn;
        public final ImageButton deleteBtn;


        public ViewHolder( TextView tvProductTitle,TextView tvProductPrice, TextView quantityStr,
                AppCompatButton incrementBtn, AppCompatButton decrementBtn,
                ImageView ivProductImage, ImageButton viewBtn, ImageButton deleteBtn ) {
            this.tvProductTitle = tvProductTitle;
            this.tvProductPrice = tvProductPrice;
            this.quantityStr = quantityStr;
            this.incrementBtn = incrementBtn;
            this.decrementBtn = decrementBtn;
            this.ivProductImage = ivProductImage;
            this.viewBtn = viewBtn;
            this.deleteBtn = deleteBtn;
        }
    }
    public interface OnItemClickListener {
        void onIncrementBtnClick(View view, int position, TextView tvQuantity);
        void onDecrementBtnClick(View view, int position, TextView tvQuantity);
        void onViewBtnClick(View view, int position);
        void onDeleteBtnClick(View view, int position);
    }
}

