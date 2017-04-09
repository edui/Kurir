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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import id.co.kurindo.kurindo.R;
import id.co.kurindo.kurindo.app.AppConfig;
import id.co.kurindo.kurindo.model.DoService;

/**
 * Created by DwiM on 11/9/2016.
 */
public class DoServiceViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;

    Context context;
    private List<DoService> data = Collections.emptyList();

    public DoServiceViewAdapter(Context context, List<DoService> datas) {
        this.context = context;
        this.data = new ArrayList<>();
        this.data.add(new DoService());
        this.data.addAll(datas);
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        View v;
        if(viewType == TYPE_ITEM){
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_cart_item, parent, false);
            viewHolder = new VHItem(v);
        }else if(viewType == TYPE_HEADER){
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_header, parent, false);
            viewHolder = new VHHeader(v);
        }

        return viewHolder;
    }
    public DoService getItem(int position) {
        return data.get(position);
    }
    public int getItemViewType(int position) {
        if (isPositionHeader(position))
            return TYPE_HEADER;

        return TYPE_ITEM;
    }

    private boolean isPositionHeader(int position) {
        return position == 0;
    }
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder vholder, int position) {
        if(vholder instanceof VHItem){
            VHItem holder = (VHItem) vholder;
            final DoService item = getItem(position);
                holder.tvName.setText(item.getKode_layanan() +"\n"+item.getJenis_barang());
                holder.tvUnitPrice.setText(AppConfig.formatCurrency(item.getPrice_unit().setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue()));
                holder.tvQuantity.setText(String.valueOf(item.getQty()));
                holder.tvPrice.setText(AppConfig.formatCurrency(item.getPrice().setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue()));
                if(item.getNotes() == null || item.getNotes().isEmpty()){
                    holder.tvNotes.setVisibility(View.GONE);
                }else{
                    holder.tvNotes.setVisibility(View.VISIBLE);
                    holder.tvNotes.setText("Note: "+item.getNotes());
                }
        }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    class VHItem extends RecyclerView.ViewHolder {
        TextView tvName;
        TextView tvNotes;
        TextView tvUnitPrice;
        TextView tvQuantity;
        TextView tvPrice;
        //ImageButton ibPriceUpdate;

        public VHItem(View itemView) {
            super(itemView);
            tvName = (TextView) itemView.findViewById(R.id.tvCartItemName);
            tvNotes = (TextView) itemView.findViewById(R.id.tvCartItemNotes);
            tvUnitPrice = (TextView) itemView.findViewById(R.id.tvCartItemUnitPrice);
            tvQuantity = (TextView) itemView.findViewById(R.id.tvCartItemQuantity);
            tvPrice = (TextView) itemView.findViewById(R.id.tvCartItemPrice);
            //ibPriceUpdate= (ImageButton) itemView.findViewById(R.id.ibPriceUpdate);
        }
    }

    class VHHeader extends RecyclerView.ViewHolder {
        TextView tvName;
        TextView tvUnitPrice;
        TextView tvQuantity;
        TextView tvPrice;

        public VHHeader(View itemView) {
            super(itemView);
            tvName = (TextView) itemView.findViewById(R.id.tvCartItemName);
            tvUnitPrice = (TextView) itemView.findViewById(R.id.tvCartItemUnitPrice);
            tvQuantity = (TextView) itemView.findViewById(R.id.tvCartItemQuantity);
            tvPrice = (TextView) itemView.findViewById(R.id.tvCartItemPrice);
        }
    }

}