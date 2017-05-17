package id.co.kurindo.kurindo.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.List;

import id.co.kurindo.kurindo.R;
import id.co.kurindo.kurindo.app.AppConfig;
import id.co.kurindo.kurindo.model.Vehicle;

/**
 * Created by DwiM on 4/11/2017.
 */

public class ItemAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    Context context;
    List<Vehicle> datas;
    public ItemAdapter(Context context, List<Vehicle> datas){
        this.context = context;
        this.datas = datas;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder;
        View v;
        v = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.list_item_option, parent, false);
        viewHolder = new MyItemHolder(v);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder vholder, int position) {
        MyItemHolder holder = (MyItemHolder) vholder;
        Vehicle v = datas.get(position);
        int resId = context.getResources().getIdentifier(v.getImage().substring(0,v.getImage().length()-4),"drawable",context.getPackageName());
        if(resId == 0){
            Glide.with(context).load(AppConfig.urlProductImage(v.getImage()))
                    .thumbnail(0.5f)
                    .crossFade()
                    .placeholder(R.drawable.placeholder)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(holder.thumbnail);
        }else{
            holder.thumbnail.setImageResource(resId);
        }
        String desc = "";
        if(v.getDescription() != null) desc+=v.getDescription();
        if(v.getDimensi() != null) desc+="\n"+v.getDimensi();
        if(v.getDayamuat() != null) desc+="\n"+v.getDayamuat();

        holder.description.setText((desc == null ? "":desc));
        holder.title.setText(v.getName());

    }

    @Override
    public int getItemCount() {
        return datas.size();
    }

    public static class MyItemHolder extends RecyclerView.ViewHolder {
        ImageView thumbnail;
        TextView title;
        TextView description;

        public MyItemHolder(View itemView) {
            super(itemView);
            this.title= (TextView) itemView.findViewById(R.id.title);
            this.description= (TextView) itemView.findViewById(R.id.description);
            this.thumbnail= (ImageView) itemView.findViewById(R.id.thumbnail);
        }

    }

}
