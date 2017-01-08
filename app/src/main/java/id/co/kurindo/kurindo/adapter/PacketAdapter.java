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
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.ArrayList;
import java.util.List;

import id.co.kurindo.kurindo.R;
import id.co.kurindo.kurindo.model.News;
import id.co.kurindo.kurindo.model.Packet;

/**
 * Created by DwiM on 11/9/2016.
 */
public class PacketAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    Context context;
    List<Packet> data = new ArrayList<>();
    private String[] bgColors;

    public PacketAdapter(Context context, List<Packet> data) {
        this.context = context;
        this.data = data;
        bgColors = context.getResources().getStringArray(R.array.list_bg);
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder;
        View v;
        v = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.list_item_packet_trace_result, parent, false);
        viewHolder = new MyItemHolder(v);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Packet packet = data.get(position);
        ((MyItemHolder) holder).resiText.setText(packet.getResi());
        ((MyItemHolder) holder).kotaAsalText.setText(packet.getKotaPengirimText());
        ((MyItemHolder) holder).kotaTujuanText.setText(packet.getKotaPenerimaText());
        ((MyItemHolder) holder).serviceCodeText.setText(packet.getServiceCode());
        ((MyItemHolder) holder).statusText.setText(packet.getStatusText());
        ((MyItemHolder) holder).createdText.setText(packet.getCreatedDate());
        String color = bgColors[position % bgColors.length];
        ((MyItemHolder) holder).serviceCodeText.setBackgroundColor(Color.parseColor(color));
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public static class MyItemHolder extends RecyclerView.ViewHolder {
        protected TextView resiText;
        protected TextView kotaAsalText;
        protected TextView kotaTujuanText;
        protected TextView serviceCodeText;
        protected TextView statusText;
        protected TextView createdText;
        public MyItemHolder(View itemView) {
            super(itemView);
            this.resiText= (TextView) itemView.findViewById(R.id.packet_resi);
            this.kotaAsalText= (TextView) itemView.findViewById(R.id.packet_kota_asal);
            this.kotaTujuanText= (TextView) itemView.findViewById(R.id.packet_kota_tujuan);
            this.serviceCodeText= (TextView) itemView.findViewById(R.id.packet_service);
            this.statusText= (TextView) itemView.findViewById(R.id.packet_status);
            this.createdText = (TextView) itemView.findViewById(R.id.packet_created);
        }
    }

}