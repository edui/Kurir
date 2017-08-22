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

import butterknife.Bind;
import id.co.kurindo.kurindo.R;
import id.co.kurindo.kurindo.app.AppConfig;
import id.co.kurindo.kurindo.model.DoCarRental;
import id.co.kurindo.kurindo.model.Help;
import id.co.kurindo.kurindo.model.Vehicle;

/**
 * Created by DwiM on 4/11/2017.
 */

public class HelpItemAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    Context context;
    List<Help> datas;
    protected OnItemClickListener itemClickListener;

    public HelpItemAdapter(Context context, List<Help> datas, OnItemClickListener mOnItemClickListener){
        this.context = context;
        this.datas = datas;
        this.itemClickListener = mOnItemClickListener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder;
        View v;
        v = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.list_help_item, parent, false);
        viewHolder = new MyItemHolder(v);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder vholder, final int position) {
        MyItemHolder holder = (MyItemHolder) vholder;
        Help v = datas.get(position);
        int resId = context.getResources().getIdentifier(v.getImage().substring(0,v.getImage().length()-4),"drawable",context.getPackageName());
        if(resId == 0){
            Glide.with(context).load(AppConfig.urlVehicleImage(v.getImage()))
                    .thumbnail(0.5f)
                    .crossFade()
                    .placeholder(R.drawable.placeholder)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(holder.thumbnail);
        }else{
            holder.thumbnail.setImageResource(resId);
        }
        holder.thumbnail.invalidate();
        holder.title.setText( v.getTitle() );
        holder.description.setText( v.getDescription());

        holder.title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemClickListener.onButtonViewPesanClick(v, position);
            }
        });
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
            this.title = (TextView) itemView.findViewById(R.id.title);
            this.description = (TextView) itemView.findViewById(R.id.description);
            this.thumbnail = (ImageView) itemView.findViewById(R.id.thumbnail);

        }

    }
    public interface OnItemClickListener {
        void onButtonViewPesanClick(View view, int position);
    }
}
