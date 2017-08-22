package id.co.kurindo.kurindo.adapter;

/**
 * Created by DwiM on 11/9/2016.
 */

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
public class DoMartViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;

    Context context;
    private List<DoMart> cartItems = Collections.emptyList();

    public DoMartViewAdapter(Context context, List<DoMart> data) {
        this.context = context;
        this.cartItems = data;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        View v;
        if(viewType == TYPE_ITEM){
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_mart_item, parent, false);
            viewHolder = new VHItem(v);
        }else if(viewType == TYPE_HEADER){
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_mart_header, parent, false);
            viewHolder = new VHHeader(v);
        }

        return viewHolder;
    }
    public DoMart getItem(int position) {
        return cartItems.get(position);
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
            //final Cart cart = CartHelper.getCart();
            final DoMart item = getItem(position);
            VHItem holder = (VHItem) vholder;
            if(!item.getType().equalsIgnoreCase(AppConfig.KEY_CHARGE) && item.getOrigin() != null && item.getOrigin().getAddress() != null){
                String location = "Lokasi\n"+item.getOrigin().getAddress().toStringFormatted();
                location += (item.getOrigin().getAddress().getNotes()==null? "" : "\n"+item.getOrigin().getAddress().getNotes());
                holder.tvLocation.setText(location);
                holder.tvLocation.setVisibility(View.VISIBLE);
            }else{
                holder.tvLocation.setText("");
                holder.tvLocation.setVisibility(View.GONE);
            }
            String notes = item.getNotes();
            holder.tvNotes.setText((notes==null?"":notes));
            String price = AppConfig.formatCurrency(item.getPrice().setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
            if(item.getQty() < 0) price ="Dihitung sesuai aslinya.";
            holder.tvPrice.setText(price);
            holder.btnUpdateHarga.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return cartItems.size();
    }

    class VHItem extends RecyclerView.ViewHolder {
        TextView tvLocation;
        TextView tvNotes;
        TextView tvPrice;
        Button btnUpdateHarga;

        public VHItem(View itemView) {
            super(itemView);
            tvLocation = (TextView) itemView.findViewById(R.id.tvLocation);
            tvNotes = (TextView) itemView.findViewById(R.id.tvNotes);
            tvPrice = (TextView) itemView.findViewById(R.id.tvPrice);
            btnUpdateHarga= (Button) itemView.findViewById(R.id.btnUpdateHarga);
        }
    }

    class VHHeader extends RecyclerView.ViewHolder {
        TextView tvLocation;
        TextView tvNotes;
        TextView tvPrice;

        public VHHeader(View itemView) {
            super(itemView);
        }
    }

}