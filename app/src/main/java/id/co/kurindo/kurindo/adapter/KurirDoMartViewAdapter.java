package id.co.kurindo.kurindo.adapter;

/**
 * Created by DwiM on 11/9/2016.
 */

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import id.co.kurindo.kurindo.R;
import id.co.kurindo.kurindo.app.AppConfig;
import id.co.kurindo.kurindo.model.DoMart;

/**
 * Created by DwiM on 11/9/2016.
 */
public class KurirDoMartViewAdapter extends DoMartViewAdapter {
    protected OnItemClickListener itemClickListener;

    public KurirDoMartViewAdapter(Context context, List<DoMart> data, OnItemClickListener itemClickListener) {
        super(context, data);
        this.itemClickListener = itemClickListener;
    }
    public void onBindViewHolder(RecyclerView.ViewHolder vholder, final int position) {
        super.onBindViewHolder(vholder, position);
        if (vholder instanceof VHItem) {
            VHItem holder = (VHItem) vholder;
            final DoMart item = getItem(position);
            if(item.getQty() < 0){
                holder.btnUpdateHarga.setVisibility(View.VISIBLE);
                holder.btnUpdateHarga.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        itemClickListener.onBtnUpdateHargaClick(v, position, item);
                    }
                });
            }
        }
    }
    public interface OnItemClickListener {
        void onBtnUpdateHargaClick(View view, int position, DoMart domart);
    }

}