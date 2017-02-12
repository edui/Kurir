package id.co.kurindo.kurindo.adapter;


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
import id.co.kurindo.kurindo.model.TOrder;

/**
 * Created by DwiM on 11/9/2016.
 */
public class KurirMonitorTOrderAdapter extends MonitorTOrderAdapter {

    public KurirMonitorTOrderAdapter(Context context, List<TOrder> data, MonitorTOrderAdapter.OnItemClickListener mOnItemClickListener) {
        super(context, data, mOnItemClickListener);
    }

    @Override
    protected void setup_status_kur100(MyItemHolder holder, TOrder order, final int position) {
        super.setup_status_kur100(holder, order, position);
        holder.kur101Btn.setVisibility(View.VISIBLE);
        holder.kur101Btn.setImageResource(R.drawable.booking_order);
        holder.kur101Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemClickListener.onPickButtonClick(v, position, AppConfig.KEY_KUR101);
            }
        });
    }
}