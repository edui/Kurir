package id.co.kurindo.kurindo.adapter;

/**
 * Created by DwiM on 11/9/2016.
 */

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import id.co.kurindo.kurindo.R;
import id.co.kurindo.kurindo.app.AppConfig;
import id.co.kurindo.kurindo.model.Notification;

/**
 * Created by DwiM on 11/9/2016.
 */
public class NotificationAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    Context context;
    List<Notification> data = new ArrayList<>();
    OnItemClickListener onItemClickListener;
    public NotificationAdapter(Context context, List<Notification> data, OnItemClickListener onItemClickListener) {
        this.context = context;
        this.data = data;
        this.onItemClickListener = onItemClickListener;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder;
        View v;
        v = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.list_item_notification, parent, false);
        viewHolder = new MyItemHolder(v);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder vholder, final int position) {
        Notification model = data.get(position);
        MyItemHolder holder = (MyItemHolder) vholder;

        int resid = R.drawable.ic_info_outline_black_18dp;
        if(model != null && model.getTag() != null){
            if(model.getTag().equalsIgnoreCase(AppConfig.KEY_DOSEND)) resid = R.drawable.do_send_icon;
            if(model.getTag().equalsIgnoreCase(AppConfig.KEY_DOJEK)) resid = R.drawable.do_jek_icon;
            if(model.getTag().equalsIgnoreCase(AppConfig.KEY_DOCAR)) resid = R.drawable.do_car_icon;
            if(model.getTag().equalsIgnoreCase(AppConfig.KEY_DOMOVE)) resid = R.drawable.do_move_icon;
            if(model.getTag().equalsIgnoreCase(AppConfig.KEY_DOMART)) resid = R.drawable.do_mart_icon;
            if(model.getTag().equalsIgnoreCase(AppConfig.KEY_DOSHOP)) resid = R.drawable.do_shop_icon;
            if(model.getTag().equalsIgnoreCase(AppConfig.KEY_DOWASH)) resid = R.drawable.do_wash_icon;
            if(model.getTag().equalsIgnoreCase(AppConfig.KEY_DOSERVICE)) resid = R.drawable.do_service_icon;
        }
        holder.mImg.setImageResource(resid);
        String text = "";
        text += "AWB: "+model.getAwb();
        if(model.getKotaPengirim() != null && !model.getKotaPengirim().isEmpty()) text += "\n dari "+model.getKotaPengirim();
        if(model.getKotaPenerima() != null && !model.getKotaPenerima().isEmpty()) text += "\n ke "+model.getKotaPenerima();
        if(model.getPrice() != null && !model.getPrice().isEmpty()) text += "\n Biaya: "+model.getPrice();
        if(model.getStatus() != null && !model.getStatus().isEmpty()) text += "\n "+model.getStatus();
        if(model.getMessage() != null && !model.getMessage().isEmpty()) text += " "+model.getMessage();
        holder.mTextView.setText(text);
        if(onItemClickListener != null){
            holder.btnAction.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemClickListener.onActionButtonClick(v, position);
                }
            });
            holder.btnDismiss.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemClickListener.onDismissButtonClick(v, position);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public static class MyItemHolder extends RecyclerView.ViewHolder {
        ImageView mImg;
        TextView mTextView;
        ImageButton btnAction;
        ImageButton btnDismiss;

        public MyItemHolder(View itemView) {
            super(itemView);

            mImg = (ImageView) itemView.findViewById(R.id.item_image);
            mTextView = (TextView) itemView.findViewById(R.id.item_text);
            btnDismiss = (ImageButton) itemView.findViewById(R.id.btnDismis);
            btnAction = (ImageButton) itemView.findViewById(R.id.btnAction);
        }

    }

    public interface OnItemClickListener {
        void onActionButtonClick(View view, int position);
        void onDismissButtonClick(View view, int position);
    }
}