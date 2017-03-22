package id.co.kurindo.kurindo.adapter;

/**
 * Created by DwiM on 11/9/2016.
 */

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.ArrayList;
import java.util.List;

import id.co.kurindo.kurindo.R;
import id.co.kurindo.kurindo.app.AppConfig;
import id.co.kurindo.kurindo.model.Product;

/**
 * Created by DwiM on 11/9/2016.
 */
public class ProductGridAdmAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    Context context;
    List<Product> data = new ArrayList<>();
    protected OnItemClickListener itemClickListener;

    public ProductGridAdmAdapter(Context context, List<Product> data) {
        this.context = context;
        this.data = data;
    }

    public ProductGridAdmAdapter(Context context, List<Product> data, OnItemClickListener listener) {
        this(context, data);
        this.itemClickListener = listener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder;
        View v;
        v = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.list_item_product_adm, parent, false);
        viewHolder = new MyItemHolder(v);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder rvHolder, final int position) {
        MyItemHolder holder = (MyItemHolder) rvHolder;
        Product model = data.get(position);
        if(model != null){
            if(model.getImageName() == null){
                holder.mImg.setImageResource(model.getDrawable());
            }
            else{
                int resId = context.getResources().getIdentifier(model.getImageName().substring(0,model.getImageName().length()-4),"drawable",context.getPackageName());
                if(resId == 0){
                Glide.with(context).load(AppConfig.urlProductImage(model.getImageName()))
                        .thumbnail(0.5f)
                        .crossFade()
                        .placeholder(R.drawable.placeholder)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(holder.mImg);
                }else{
                    holder.mImg.setImageResource(resId);
                }
            }

            //((MyItemHolder) holder).mProductDescription.setText(model.getDescription());
            holder.mProductTitle.setText(model.getName());
            holder.mProductPrice.setText(AppConfig.formatCurrency(model.getPrice().doubleValue()));
            holder.mProductStatus.setText(model.getQuantity() > 0? "Ada" : "Kosong");
            holder.mProductStatus.setBackgroundColor(model.getQuantity() > 0? Color.rgb(59,156,101) : Color.rgb(189,42,34));
            holder.mImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    itemClickListener.onViewButtonClick(v, position);
                }
            });
            holder.btnViewProduct.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    itemClickListener.onViewButtonClick(v, position);
                }
            });
            holder.btnEditProduct.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    itemClickListener.onEditButtonClick(v, position);
                }
            });

            holder.btnDeleteProduct.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    itemClickListener.onDeleteButtonClick(v, position);
                }
            });
        }

    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public static class MyItemHolder extends RecyclerView.ViewHolder {
        ImageView mImg;
        TextView mProductTitle;
        TextView mProductPrice;
        TextView mProductStatus;
        TextView mProductDescription;
        ImageButton btnViewProduct;
        ImageButton btnEditProduct;
        ImageButton btnDeleteProduct;

        public MyItemHolder(View itemView) {
            super(itemView);

            mImg = (ImageView) itemView.findViewById(R.id.item_img);
            mProductTitle = (TextView) itemView.findViewById(R.id.item_name);
            mProductPrice= (TextView) itemView.findViewById(R.id.item_price);
            mProductStatus= (TextView) itemView.findViewById(R.id.item_status);
            mProductDescription= (TextView) itemView.findViewById(R.id.item_description);

            btnViewProduct = (ImageButton) itemView.findViewById(R.id.btnViewProduct);
            btnEditProduct = (ImageButton) itemView.findViewById(R.id.btnEditProduct);
            btnDeleteProduct = (ImageButton) itemView.findViewById(R.id.btnDeleteProduct);
        }

    }

    public interface OnItemClickListener {
        void onViewButtonClick(View view, int position);
        void onEditButtonClick(View view, int position);
        void onDeleteButtonClick(View view, int position);
    }

}