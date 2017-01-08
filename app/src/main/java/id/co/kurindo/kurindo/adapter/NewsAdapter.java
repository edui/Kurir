package id.co.kurindo.kurindo.adapter;

/**
 * Created by DwiM on 11/9/2016.
 */

import android.content.Context;
import android.support.v7.widget.RecyclerView;
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
import id.co.kurindo.kurindo.model.ImageModel;
import id.co.kurindo.kurindo.model.News;

/**
 * Created by DwiM on 11/9/2016.
 */
public class NewsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    Context context;
    List<News> data = new ArrayList<>();

    public NewsAdapter(Context context, List<News> data) {
        this.context = context;
        this.data = data;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder;
        View v;
        v = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.list_item_news, parent, false);
        viewHolder = new MyItemHolder(v);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        News news = data.get(position);
        if(news.getImg() == null)
            ((MyItemHolder) holder).mImg.setImageResource(news.getDrawable());
        else
        Glide.with(context).load(news.getImg())
                .thumbnail(0.5f)
                //.override(100,100)
                .crossFade()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .error(R.drawable.placeholder)
                .placeholder(R.drawable.placeholder)
                .into(((MyItemHolder) holder).mImg);
        ((MyItemHolder) holder).textView.setText(news.getTitle());
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public static class MyItemHolder extends RecyclerView.ViewHolder {
        ImageView mImg;
        protected TextView textView;
        public MyItemHolder(View itemView) {
            super(itemView);
            mImg = (ImageView) itemView.findViewById(R.id.thumbnail);
            this.textView = (TextView) itemView.findViewById(R.id.title);
        }
    }

}