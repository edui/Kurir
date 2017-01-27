package id.co.kurindo.kurindo.adapter;

/**
 * Created by DwiM on 11/9/2016.
 */

import android.content.Context;
import android.content.res.Resources;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import java.util.ArrayList;
import java.util.List;

import id.co.kurindo.kurindo.R;
import id.co.kurindo.kurindo.app.AppConfig;
import id.co.kurindo.kurindo.model.Shop;

/**
 * Created by DwiM on 11/9/2016.
 */
public class ShopAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    Context context;
    List<Shop> data = new ArrayList<>();

    public ShopAdapter(Context context, List<Shop> data) {
        this.context = context;
        this.data = data;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder;
        View v;
        v = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.list_item_shop, parent, false);
        viewHolder = new MyItemHolder(v);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Shop model = data.get(position);
        if(model != null){
            String banner = model.getBanner().substring(0,model.getBanner().length()-4);
            int resId = context.getResources().getIdentifier(banner, "drawable", context.getPackageName());
            if(resId == 0){
                Glide.with(context).load(AppConfig.urlShopImage(model.getBanner()))
                        .thumbnail(0.5f)
                        .override(200,200)
                        .crossFade()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .placeholder(R.drawable.placeholder)
                        //.listener(drawableStrGlideListener())
                        .into(((MyItemHolder) holder).mImg);
            }else{
                ((MyItemHolder) holder).mImg.setImageResource(resId);
            }
        }
        ((MyItemHolder) holder).mTextV.setText(model.getName());
        ((MyItemHolder) holder).mTextVStatus.setText(model.getStatus());
    }

    private RequestListener<? super String, GlideDrawable> drawableStrGlideListener() {
        return new RequestListener<String, GlideDrawable>() {
            @Override
            public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                //progressBar.setVisibility(View.GONE);
                return false;
            }

            @Override
            public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                //progressBar.setVisibility(View.GONE);
                return false;
            }
        };
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public static class MyItemHolder extends RecyclerView.ViewHolder {
        ImageView mImg;
        TextView mTextV;
        TextView mTextVStatus;


        public MyItemHolder(View itemView) {
            super(itemView);

            mImg = (ImageView) itemView.findViewById(R.id.item_img);
            mTextV = (TextView) itemView.findViewById(R.id.item_text);
            mTextVStatus = (TextView) itemView.findViewById(R.id.item_status);
        }

    }


}