package id.co.kurindo.kurindo.adapter;

/**
 * Created by DwiM on 11/9/2016.
 */

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.ArrayList;
import java.util.List;

import id.co.kurindo.kurindo.R;
import id.co.kurindo.kurindo.app.AppConfig;
import id.co.kurindo.kurindo.app.Constant;
import id.co.kurindo.kurindo.model.ImageModel;
import id.co.kurindo.kurindo.model.Product;

/**
 * Created by DwiM on 11/9/2016.
 */
public class ProductGridAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    Context context;
    List<Product> data = new ArrayList<>();

    public ProductGridAdapter(Context context, List<Product> data) {
        this.context = context;
        this.data = data;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder;
        View v;
        v = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.list_item_product, parent, false);
        viewHolder = new MyItemHolder(v);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Product model = data.get(position);
        if(model != null){
            if(model.getImageName() == null){
                ((MyItemHolder) holder).mImg.setImageResource(model.getDrawable());
            }
            else{
                int resId = context.getResources().getIdentifier(model.getImageName().substring(0,model.getImageName().length()-4),"drawable",context.getPackageName());
                if(resId == 0){
                Glide.with(context).load(AppConfig.urlProductImage(model.getImageName()))
                        .thumbnail(0.5f)
                        .crossFade()
                        .placeholder(R.drawable.placeholder)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(((MyItemHolder) holder).mImg);
                }else{
                    ((MyItemHolder) holder).mImg.setImageResource(resId);
                }
            }

            //((MyItemHolder) holder).mProductDescription.setText(model.getDescription());
            ((MyItemHolder) holder).mProductTitle.setText(model.getName());
            ((MyItemHolder) holder).mProductPrice.setText(AppConfig.formatCurrency(model.getPrice().doubleValue()));
            ((MyItemHolder) holder).mProductStatus.setText(model.getQuantity() > 0? "Ada" : "Kosong");
            ((MyItemHolder) holder).mProductStatus.setBackgroundColor(model.getQuantity() > 0? Color.rgb(59,156,101) : Color.rgb(189,42,34));
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

        public MyItemHolder(View itemView) {
            super(itemView);

            mImg = (ImageView) itemView.findViewById(R.id.item_img);
            mProductTitle = (TextView) itemView.findViewById(R.id.item_name);
            mProductPrice= (TextView) itemView.findViewById(R.id.item_price);
            mProductStatus= (TextView) itemView.findViewById(R.id.item_status);
            mProductDescription= (TextView) itemView.findViewById(R.id.item_description);
        }

    }


}