package id.co.kurindo.kurindo.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import id.co.kurindo.kurindo.R;
import id.co.kurindo.kurindo.app.AppConfig;
import id.co.kurindo.kurindo.model.DoCarRental;
import id.co.kurindo.kurindo.model.TUser;
import id.co.kurindo.kurindo.model.Vehicle;

/**
 * Created by DwiM on 4/11/2017.
 */

public class RentalDetailAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    Context context;
    List<DoCarRental> datas;
    protected OnItemClickListener itemClickListener;

    public RentalDetailAdapter(Context context, List<DoCarRental> datas, OnItemClickListener mOnItemClickListener){
        this.context = context;
        this.datas = datas;
        this.itemClickListener = mOnItemClickListener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder;
        View v;
        v = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.list_rental_detail_option, parent, false);
        //ButterKnife.bind(this, v);
        viewHolder = new MyItemHolder(v);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder vholder, final int position) {
        MyItemHolder holder = (MyItemHolder) vholder;
        holder.ivRental1.setImageResource(R.drawable.destination_pin);
        holder.ivRental2.setImageResource(R.drawable.ic_event_note_black_18dp);
        holder.ivRental3.setImageResource(R.drawable.ic_access_time_black_18dp);
        holder.ivSpec1.setImageResource(R.drawable.ic_event_black);
        holder.ivSpec2.setImageResource(R.drawable.ic_person_black);
        holder.ivSpec3.setImageResource(R.drawable.ic_autorenew_black_18dp);
        holder.ivSpec4.setImageResource(R.drawable.ic_thumb_up_black_18dp);
        holder.ivSpec5.setImageResource(R.drawable.gas_station);
        holder.ivSpec6.setImageResource(R.drawable.engine24);
        holder.ivUser1.setImageResource(R.drawable.ic_person_black);
        holder.ivUser2.setImageResource(R.drawable.ic_phone_black);
        holder.ivUser3.setImageResource(R.drawable.ic_pin_drop_black);

        DoCarRental r = datas.get(position);
        if(r != null){
            Vehicle v = r.getVehicle();
            int resId = context.getResources().getIdentifier(v.getImage().substring(0,v.getImage().length()-4),"drawable",context.getPackageName());
            if(resId == 0){
                Glide.with(context).load(AppConfig.urlVehicleImage(v.getImage()))
                        .thumbnail(0.5f)
                        .crossFade()
                        .placeholder(R.drawable.placeholder)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(holder.ivProductImage);
            }else{
                holder.ivProductImage.setImageResource(resId);
            }
            holder.ivProductImage.invalidate();

            holder.tvProductName.setText(v.getName());
            holder.rbRentalRating.setRating(v.getRating());
            holder.tvRentalRating.setText(""+v.getRating());
            holder.tvRatingAc.setText(""+v.getAc());
            holder.rbRatingAc.setRating(v.getAc());
            holder.tvRatingKebersihan.setText(""+v.getKebersihan());
            holder.rbRatingKebersihan.setRating(v.getKebersihan());
            holder.tvRatingKondisi.setText(""+v.getKondisi());
            holder.rbRatingKondisi.setRating(v.getKondisi());

            TUser user = r.getUser();
            holder.tvUser1.setText(user.getName());
            holder.tvUser2.setText(user.getPhone());
            holder.tvUser3.setText(user.getAddress().toStringFormatted());
            holder.btnLihatRute.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    itemClickListener.onBtnLihatRuteClick(v, position);
                }
            });

            holder.tvRental1.setText(r.getActivity() +" "+r.getCity());
            holder.tvRental2.setText(r.displayDate(false));
            holder.tvRental3.setText(r.getDurasi());
            //holder.ivRental4.setImageResource(R.drawable.ic_euro_symbol_black_18dp);
            holder.tvRental4.setText(r.getFasilitas());

            String priceStr = AppConfig.formatCurrency( r.getCalculatePrice(context, v).doubleValue());
            holder.tvTotalPrice.setText( priceStr );

            holder.tvSpec1.setText(v.getTahun());
            holder.tvSpec2.setText(v.getDayamuat());
            holder.tvSpec3.setText(v.getWarna());
            holder.tvSpec4.setText(v.getMerk() + " " + v.getModel());
            holder.tvSpec5.setText(v.getBbm());
            holder.tvSpec6.setText(v.getTransmisi());

            holder.tvDescription.setText(v.getDescription());
            holder.tvIncludeBiayaDetail.setText(AppConfig.getIncludeBiaya(context, r.getFasilitas(), AppConfig.KEY_DOCAR));
            holder.tvExcludeBiayaDetail.setText(AppConfig.getExcludeBiaya(context, r.getFasilitas(), AppConfig.KEY_DOCAR));
            if(r.getActivity().equalsIgnoreCase("City Tour") || r.getActivity().equalsIgnoreCase("Dalam Kota")){
                holder.tvRule.setText("Rule : "+AppConfig.getRule(context, r.getDurasi(), r.getActivity(), AppConfig.KEY_DOCAR));
            }else{
                holder.tvRule.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public int getItemCount() {
        return datas.size();
    }

    public static class MyItemHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.ivProductImage)
        ImageView ivProductImage;

        @Bind(R.id.tvProductName)
        TextView tvProductName;
        @Bind(R.id.rbRentalRating)
        RatingBar rbRentalRating;
        @Bind(R.id.tvRentalRating)
        TextView tvRentalRating;

        @Bind(R.id.rbRatingAc)
        RatingBar rbRatingAc;
        @Bind(R.id.tvRatingAc)
        TextView tvRatingAc;

        @Bind(R.id.rbRatingKebersihan)
        RatingBar rbRatingKebersihan;
        @Bind(R.id.tvRatingKebersihan)
        TextView tvRatingKebersihan;

        @Bind(R.id.rbRatingKondisi)
        RatingBar rbRatingKondisi;
        @Bind(R.id.tvRatingKondisi)
        TextView tvRatingKondisi;

        @Bind(R.id.tvUlasan)
        TextView tvUlasan;

        @Bind(R.id.ivSpec1)
        ImageView ivSpec1;
        @Bind(R.id.tvSpec1)
        TextView tvSpec1;
        @Bind(R.id.ivSpec2)
        ImageView ivSpec2;
        @Bind(R.id.tvSpec2)
        TextView tvSpec2;
        @Bind(R.id.ivSpec3)
        ImageView ivSpec3;
        @Bind(R.id.tvSpec3)
        TextView tvSpec3;

        @Bind(R.id.ivSpec4)
        ImageView ivSpec4;
        @Bind(R.id.tvSpec4)
        TextView tvSpec4;
        @Bind(R.id.ivSpec5)
        ImageView ivSpec5;
        @Bind(R.id.tvSpec5)
        TextView tvSpec5;
        @Bind(R.id.ivSpec6)
        ImageView ivSpec6;
        @Bind(R.id.tvSpec6)
        TextView tvSpec6;

        @Bind(R.id.tvDescription)
        TextView tvDescription;

        @Bind(R.id.ivUser1)
        ImageView ivUser1;
        @Bind(R.id.tvUser1)
        TextView tvUser1;
        @Bind(R.id.ivUser2)
        ImageView ivUser2;
        @Bind(R.id.tvUser2)
        TextView tvUser2;
        @Bind(R.id.ivUser3)
        ImageView ivUser3;
        @Bind(R.id.tvUser3)
        TextView tvUser3;
        @Bind(R.id.btnLihatRute)
        Button btnLihatRute;

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
        @Bind(R.id.tvRental5)
        TextView tvRental5;

        @Bind(R.id.tvTotalPrice)
        TextView tvTotalPrice;
        @Bind(R.id.tvIncludeBiayaDetail)
        TextView tvIncludeBiayaDetail;
        @Bind(R.id.tvExcludeBiayaDetail)
        TextView tvExcludeBiayaDetail;
        @Bind(R.id.tvRule)
        TextView tvRule;

        public MyItemHolder(View itemView) {
            super(itemView);
            //ButterKnife.bind(itemView);
            this.tvProductName = (TextView) itemView.findViewById(R.id.tvProductName);
            this.ivProductImage = (ImageView) itemView.findViewById(R.id.ivProductImage);

            this.rbRentalRating = (RatingBar) itemView.findViewById(R.id.rbRentalRating);
            this.tvRentalRating = (TextView) itemView.findViewById(R.id.tvRentalRating);

            this.rbRatingAc =(RatingBar) itemView.findViewById(R.id.rbRatingAc);
            this.tvRatingAc = (TextView) itemView.findViewById(R.id.tvRatingAc);

            this.rbRatingKebersihan = (RatingBar) itemView.findViewById(R.id.rbRatingKebersihan);
            this.tvRatingKebersihan = (TextView) itemView.findViewById(R.id.tvRatingKebersihan);

            this.rbRatingKondisi = (RatingBar) itemView.findViewById(R.id.rbRatingKondisi);
            this.tvRatingKondisi = (TextView) itemView.findViewById(R.id.tvRatingKondisi);

            this.tvUlasan = (TextView) itemView.findViewById(R.id.tvUlasan);

            this.ivSpec1 = (ImageView) itemView.findViewById(R.id.ivSpec1);
            this.tvSpec1 = (TextView) itemView.findViewById(R.id.tvSpec1);
            this.ivSpec2 = (ImageView) itemView.findViewById(R.id.ivSpec2);
            this.tvSpec2 = (TextView) itemView.findViewById(R.id.tvSpec2);
            this.ivSpec3 = (ImageView) itemView.findViewById(R.id.ivSpec3);
            this.tvSpec3 = (TextView) itemView.findViewById(R.id.tvSpec3);

            this.ivSpec4 = (ImageView) itemView.findViewById(R.id.ivSpec4);
            this.tvSpec4 = (TextView) itemView.findViewById(R.id.tvSpec4);
            this.ivSpec5 = (ImageView) itemView.findViewById(R.id.ivSpec5);
            this.tvSpec5 = (TextView) itemView.findViewById(R.id.tvSpec5);
            this.ivSpec6 = (ImageView) itemView.findViewById(R.id.ivSpec6);
            this.tvSpec6= (TextView) itemView.findViewById(R.id.tvSpec6);

            this.tvDescription = (TextView) itemView.findViewById(R.id.tvDescription);

            this.ivUser1 = (ImageView) itemView.findViewById(R.id.ivUser1);
            this.tvUser1 = (TextView) itemView.findViewById(R.id.tvUser1);
            this.ivUser2 = (ImageView) itemView.findViewById(R.id.ivUser2);
            this.tvUser2 = (TextView) itemView.findViewById(R.id.tvUser2);
            this.ivUser3 = (ImageView) itemView.findViewById(R.id.ivUser3);
            this.tvUser3 = (TextView) itemView.findViewById(R.id.tvUser3);
            this.btnLihatRute= (Button) itemView.findViewById(R.id.btnLihatRute);

            this.ivRental1 = (ImageView) itemView.findViewById(R.id.ivRental1);
            this.tvRental1 = (TextView) itemView.findViewById(R.id.tvRental1);
            this.ivRental2 = (ImageView) itemView.findViewById(R.id.ivRental2);
            this.tvRental2 = (TextView) itemView.findViewById(R.id.tvRental2);
            this.ivRental3 = (ImageView) itemView.findViewById(R.id.ivRental3);
            this.tvRental3 = (TextView) itemView.findViewById(R.id.tvRental3);
            this.ivRental4 = (ImageView) itemView.findViewById(R.id.ivRental4);
            this.tvRental4 = (TextView) itemView.findViewById(R.id.tvRental4);
            this.tvRental5 = (TextView) itemView.findViewById(R.id.tvRental5);

            this.tvTotalPrice = (TextView) itemView.findViewById(R.id.tvTotalPrice);
            this.tvIncludeBiayaDetail = (TextView) itemView.findViewById(R.id.tvIncludeBiayaDetail);
            this.tvExcludeBiayaDetail = (TextView) itemView.findViewById(R.id.tvExcludeBiayaDetail);
            this.tvRule = (TextView) itemView.findViewById(R.id.tvRule);
        }

    }
    public interface OnItemClickListener {
        void onBtnLihatRuteClick(View view, int position);
    }
}
