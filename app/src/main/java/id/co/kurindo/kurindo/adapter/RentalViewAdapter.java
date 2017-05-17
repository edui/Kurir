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
import id.co.kurindo.kurindo.model.Vehicle;

/**
 * Created by DwiM on 4/11/2017.
 */

public class RentalViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    Context context;
    List<DoCarRental> datas;
    protected OnItemClickListener itemClickListener;

    public RentalViewAdapter(Context context, List<DoCarRental> datas, OnItemClickListener mOnItemClickListener){
        this.context = context;
        this.datas = datas;
        this.itemClickListener = mOnItemClickListener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder;
        View v;
        v = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.list_rental_view_option, parent, false);
        viewHolder = new MyItemHolder(v);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder vholder, final int position) {
        MyItemHolder holder = (MyItemHolder) vholder;
        DoCarRental r = datas.get(position);
        Vehicle v = r.getVehicle();
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

        holder.tvCarTitle.setText(v.getName());
        double price = 0;
        try {
            price = Double.parseDouble(v.getTarif());
        }catch (Exception e){}
        String priceStr = AppConfig.formatCurrency( price );

        holder.ivRental1.setImageResource(R.drawable.destination_pin);
        holder.tvRental1.setText(r.getActivity());
        holder.ivRental2.setImageResource(R.drawable.ic_event_note_black_18dp);
        holder.tvRental2.setText(r.getDateRange());
        holder.ivRental3.setImageResource(R.drawable.ic_access_time_black_18dp);
        holder.tvRental3.setText(r.getDurasi() + " "+r.getFasilitas());
        holder.ivRental4.setImageResource(R.drawable.ic_euro_symbol_black_18dp);
        holder.tvRental4.setText(priceStr);
    }

    @Override
    public int getItemCount() {
        return datas.size();
    }

    public static class MyItemHolder extends RecyclerView.ViewHolder {
        ImageView thumbnail;
        TextView tvCarTitle;
        ImageView ivCarPromo;

        @Bind(R.id.ivRental1)
        ImageView ivRental1;
        @Bind(R.id.tvRental1)
        TextView tvRental1;
        @Bind(R.id.ivRental2)
        ImageView ivRental2;
        @Bind(R.id.tvRental2)
        TextView tvRental2;
        @Bind(R.id.ivRental3)
        ImageView ivRental3;
        @Bind(R.id.tvRental3)
        TextView tvRental3;
        @Bind(R.id.ivRental4)
        ImageView ivRental4;
        @Bind(R.id.tvRental4)
        TextView tvRental4;


        public MyItemHolder(View itemView) {
            super(itemView);
            this.ivCarPromo = (ImageView) itemView.findViewById(R.id.ivCarPromo);
            this.tvCarTitle = (TextView) itemView.findViewById(R.id.tvCarTitle);
            this.thumbnail = (ImageView) itemView.findViewById(R.id.thumbnail);

            this.tvRental1 = (TextView) itemView.findViewById(R.id.tvRental1);
            this.ivRental1 = (ImageView) itemView.findViewById(R.id.ivRental1);
            this.tvRental2 = (TextView) itemView.findViewById(R.id.tvRental2);
            this.ivRental2 = (ImageView) itemView.findViewById(R.id.ivRental2);
            this.tvRental3 = (TextView) itemView.findViewById(R.id.tvRental3);
            this.ivRental3 = (ImageView) itemView.findViewById(R.id.ivRental3);
            this.tvRental4 = (TextView) itemView.findViewById(R.id.tvRental4);
            this.ivRental4 = (ImageView) itemView.findViewById(R.id.ivRental4);
        }

    }
    public interface OnItemClickListener {
        void onButtonViewPesanClick(View view, int position);
    }
}
