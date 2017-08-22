package id.co.kurindo.kurindo.adapter;

import android.content.Context;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.math.BigDecimal;
import java.util.List;

import id.co.kurindo.kurindo.R;
import id.co.kurindo.kurindo.app.AppConfig;
import id.co.kurindo.kurindo.model.DoCarRental;
import id.co.kurindo.kurindo.model.Vehicle;

/**
 * Created by DwiM on 4/11/2017.
 */

public class VehicleItemAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    Context context;
    List<Vehicle> datas;
    protected OnItemClickListener itemClickListener;
    DoCarRental rental;
    public VehicleItemAdapter(Context context, DoCarRental rental, List<Vehicle> datas, OnItemClickListener mOnItemClickListener){
        this.context = context;
        this.rental = rental;
        this.datas = datas;
        this.itemClickListener = mOnItemClickListener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder;
        View v;
        v = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.list_vehicle_item_option, parent, false);
        viewHolder = new MyItemHolder(v);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder vholder, final int position) {
        MyItemHolder holder = (MyItemHolder) vholder;
        Vehicle v = datas.get(position);
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

        float carRating = (v.getAc() + v.getKebersihan() + v.getKondisi())/3;//calculate kebersihan, tahun, kondisi, ac, rating
        holder.rbCarRating.setRating(carRating);
        holder.tvCarRating.setText(""+carRating);
        holder.rbRentalRating.setRating(v.getRating());
        holder.tvRentalRating.setText(""+v.getRating());
        holder.tvRentalRateTitle.setText("Rental Rating");
        holder.tvCarTitle.setText(v.getName());
        String priceStr = AppConfig.formatCurrency( (rental == null? 0 : rental.getCalculatePrice(context, v).doubleValue()) );
        holder.tvPrice.setText(priceStr);

        holder.ivSpec1.setImageResource(R.drawable.ic_event_black);
        holder.tvSpec1.setText(v.getTahun());
        holder.ivSpec2.setImageResource(R.drawable.ic_person_black);
        holder.tvSpec2.setText(v.getDayamuat());
        holder.ivSpec3.setImageResource(R.drawable.ic_autorenew_black_18dp);
        holder.tvSpec3.setText(v.getWarna());
        holder.ivSpec4.setImageResource(R.drawable.ic_thumb_up_black_18dp);
        holder.tvSpec4.setText(v.getMerk() + " " + v.getModel());
        holder.ivSpec5.setImageResource(R.drawable.gas_station);
        holder.tvSpec5.setText(v.getBbm());
        holder.ivSpec6.setImageResource(R.drawable.engine24);
        holder.tvSpec6.setText(v.getTransmisi());
        holder.btnViewDetil.setOnClickListener(new View.OnClickListener() {
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
        RatingBar rbCarRating;
        TextView tvCarRating;
        TextView tvPrice;
        TextView tvCarTitle;
        ImageView ivCarPromo;

        ImageView ivSpec1;
        TextView tvSpec1;
        ImageView ivSpec2;
        TextView tvSpec2;
        ImageView ivSpec3;
        TextView tvSpec3;
        ImageView ivSpec4;
        TextView tvSpec4;
        ImageView ivSpec5;
        TextView tvSpec5;
        ImageView ivSpec6;
        TextView tvSpec6;

        TextView tvRentalRateTitle;
        RatingBar  rbRentalRating;
        TextView tvRentalRating;

        AppCompatButton btnViewDetil;

        public MyItemHolder(View itemView) {
            super(itemView);
            this.tvSpec6= (TextView) itemView.findViewById(R.id.tvSpec6);
            this.ivSpec6= (ImageView) itemView.findViewById(R.id.ivSpec6);
            this.tvSpec5= (TextView) itemView.findViewById(R.id.tvSpec5);
            this.ivSpec5= (ImageView) itemView.findViewById(R.id.ivSpec5);
            this.tvSpec4= (TextView) itemView.findViewById(R.id.tvSpec4);
            this.ivSpec4= (ImageView) itemView.findViewById(R.id.ivSpec4);
            this.tvSpec3= (TextView) itemView.findViewById(R.id.tvSpec3);
            this.ivSpec3= (ImageView) itemView.findViewById(R.id.ivSpec3);
            this.tvSpec2= (TextView) itemView.findViewById(R.id.tvSpec2);
            this.ivSpec2= (ImageView) itemView.findViewById(R.id.ivSpec2);
            this.tvSpec1= (TextView) itemView.findViewById(R.id.tvSpec1);
            this.ivSpec1= (ImageView) itemView.findViewById(R.id.ivSpec1);
            this.ivCarPromo= (ImageView) itemView.findViewById(R.id.ivCarPromo);
            this.tvCarTitle= (TextView) itemView.findViewById(R.id.tvCarTitle);
            this.tvPrice= (TextView) itemView.findViewById(R.id.tvPrice);
            this.tvCarRating= (TextView) itemView.findViewById(R.id.tvCarRating);
            this.rbCarRating= (RatingBar) itemView.findViewById(R.id.rbCarRating);
            this.thumbnail= (ImageView) itemView.findViewById(R.id.thumbnail);

            this.rbRentalRating= (RatingBar) itemView.findViewById(R.id.rbRentalRating);
            this.tvRentalRating= (TextView) itemView.findViewById(R.id.tvRentalRating);
            this.tvRentalRateTitle= (TextView) itemView.findViewById(R.id.tvRentalRateTitle);

            this.btnViewDetil= (AppCompatButton) itemView.findViewById(R.id.btnViewDetil);
        }

    }
    public interface OnItemClickListener {
        void onButtonViewPesanClick(View view, int position);
    }
}
