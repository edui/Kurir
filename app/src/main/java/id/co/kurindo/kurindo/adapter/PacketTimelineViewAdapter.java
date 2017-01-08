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

import com.vipul.hp_hp.timelineview.TimelineView;

import java.util.ArrayList;
import java.util.List;

import id.co.kurindo.kurindo.R;
import id.co.kurindo.kurindo.app.AppConfig;
import id.co.kurindo.kurindo.model.StatusHistory;

/**
 * Created by DwiM on 11/9/2016.
 */
public class PacketTimelineViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    Context context;
    List<StatusHistory> data = new ArrayList<>();

    public PacketTimelineViewAdapter(Context context, List<StatusHistory> data) {
        this.context = context;
        this.data = data;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder;
        View v;
        v = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.item_timeline, parent, false);
        viewHolder = new MyItemHolder(v, viewType);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        StatusHistory model = data.get(position);
        if(model != null && model.getStatus() != null){
            /*
            Glide.with(context).load(model.getUrl())
                    .thumbnail(0.5f)
                    //.override(200,200)
                    .crossFade()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(((MyItemHolder) holder).mImg);
            */
            String statusText = model.getRemarks() +"\n"+model.getCreated_date()+"\n"+model.getLocation() + (model.getPic()==null? (model.getCreated_by()==null? "":"by "+model.getCreated_by().getFirstname()):" by "+model.getPic().getFirstname());
            ((MyItemHolder) holder).mTextView.setText(statusText);

            if(model.getStatus().equalsIgnoreCase(AppConfig.KEY_KUR100)) {
                ((MyItemHolder) holder).mImg.setImageResource(R.drawable.booking_order);
            }else if(model.getStatus().equalsIgnoreCase(AppConfig.KEY_KUR101)){
                ((MyItemHolder) holder).mImg.setImageResource(R.drawable.accept_booking_icon);
            }else if(model.getStatus().equalsIgnoreCase(AppConfig.KEY_KUR200)){
                ((MonitorPacketAdapter.MyItemHolder) holder).kur300Btn.setImageResource(R.drawable.status01_2_icon);
            }else if(model.getStatus().equalsIgnoreCase(AppConfig.KEY_KUR300)){
                ((MonitorPacketAdapter.MyItemHolder) holder).kur310Btn.setImageResource(R.drawable.status03_2_icon);
            }else if(model.getStatus().equalsIgnoreCase(AppConfig.KEY_KUR310) || model.getStatus().equalsIgnoreCase(AppConfig.KEY_KUR350)){
                ((MonitorPacketAdapter.MyItemHolder) holder).kur400Btn.setImageResource(R.drawable.status05_2_icon);
            }else if(model.getStatus().equalsIgnoreCase(AppConfig.KEY_KUR400)){
                ((MonitorPacketAdapter.MyItemHolder) holder).kur350Btn.setImageResource(R.drawable.status06_2_icon);
            }else if(model.getStatus().equalsIgnoreCase(AppConfig.KEY_KUR500)){
                ((MonitorPacketAdapter.MyItemHolder) holder).kur500Btn.setImageResource(R.drawable.status04_2_icon);
            }
        }
    }
    @Override
    public int getItemViewType(int position) {
        return TimelineView.getTimeLineViewType(position,getItemCount());
    }
    @Override
    public int getItemCount() {
        return data.size();
    }

    public static class MyItemHolder extends RecyclerView.ViewHolder {
        ImageView mImg;
        TextView mTextView;
        TimelineView mTimelineView;

        public MyItemHolder(View itemView, int viewType) {
            super(itemView);

            mImg = (ImageView) itemView.findViewById(R.id.item_image);
            mTextView = (TextView) itemView.findViewById(R.id.item_text);
            mTimelineView = (TimelineView) itemView.findViewById(R.id.time_marker);
            mTimelineView.initLine(viewType);
        }

    }


}