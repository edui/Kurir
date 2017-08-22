package id.co.kurindo.kurindo.adapter;

/**
 * Created by DwiM on 11/9/2016.
 */

import android.content.Context;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import id.co.kurindo.kurindo.R;
import id.co.kurindo.kurindo.app.AppConfig;
import id.co.kurindo.kurindo.model.TOrder;

/**
 * Created by DwiM on 11/9/2016.
 */
public class DoMartRouteAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;

    Context context;
    private TOrder order;
    private OnItemClickListener itemClickListener;

    public DoMartRouteAdapter(Context context, TOrder order, OnItemClickListener itemClickListener) {
        this.context = context;
        this.order = order;
        this.itemClickListener = itemClickListener;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_mart_route, parent, false);
        viewHolder = new VHItem(v);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder vholder, final int position) {
        if(order != null){
            VHItem holder = (VHItem) vholder;
            if(order.getService_code().equalsIgnoreCase(AppConfig.PACKET_SDS)){
                holder.ivServiceCodeIcon.setImageResource(R.drawable.icon_sds);
            }else if(order.getService_code().equalsIgnoreCase(AppConfig.PACKET_NDS)) {
                holder.ivServiceCodeIcon.setImageResource(R.drawable.icon_nds);
            }else if(order.getService_code().equalsIgnoreCase(AppConfig.PACKET_ENS)) {
                holder.ivServiceCodeIcon.setImageResource(R.drawable.icon_ens);
            }else if(order.getService_code().equalsIgnoreCase(AppConfig.PACKET_NNS)) {
                holder.ivServiceCodeIcon.setImageResource(R.drawable.icon_nns);
            }
            holder.tvAlamatLokasi.setText(order.getPlace().toStringAddressFormatted());
            holder.btnLihatRute.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(itemClickListener != null) itemClickListener.onViewRouteButtonClick(v, position,  order);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return 1;
    }


    class VHItem extends RecyclerView.ViewHolder {
        ImageView ivServiceCodeIcon;
        AppCompatButton btnLihatRute;
        TextView tvAlamatLokasi;

        public VHItem(View itemView) {
            super(itemView);
            ivServiceCodeIcon = (ImageView) itemView.findViewById(R.id.ivServiceCodeIcon);
            btnLihatRute = (AppCompatButton) itemView.findViewById(R.id.btnLihatRute);
            tvAlamatLokasi = (TextView) itemView.findViewById(R.id.tvAlamatLokasi);
        }
    }

    public interface OnItemClickListener {
        void onViewRouteButtonClick(View view, int position, TOrder order);
    }
}