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

import com.android.tonyvu.sc.model.Cart;
import com.android.tonyvu.sc.util.CartHelper;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import id.co.kurindo.kurindo.R;
import id.co.kurindo.kurindo.app.Constant;
import id.co.kurindo.kurindo.model.CartItem;
import id.co.kurindo.kurindo.model.Recipient;

/**
 * Created by DwiM on 11/9/2016.
 */
public class RecipientViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;

    Context context;
    private List<Recipient> dataList = Collections.emptyList();

    public RecipientViewAdapter(Context context, List<Recipient> data) {
        this.context = context;
        this.dataList = data;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        View v;
        if(viewType == TYPE_ITEM){
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_recipient_item, parent, false);
            viewHolder = new VHItem(v);
        }else if(viewType == TYPE_HEADER){
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recipient_header, parent, false);
            viewHolder = new VHHeader(v);
        }

        return viewHolder;
    }
    public Recipient getItem(int position) {
        return dataList.get(position);
    }
    public int getItemViewType(int position) {
        //if (isPositionHeader(position)) return TYPE_HEADER;

        return TYPE_ITEM;
    }

    private boolean isPositionHeader(int position) {
        return position == 0;
    }
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if(holder instanceof VHItem){
            final Recipient item = getItem(position);
            ((VHItem) holder).tvName.setText(item.getName() + "\n" + item.getTelepon());
            ((VHItem) holder).tvAlamat.setText(item.getAddress()==null? "":item.getAddress().getAlamat());
            ((VHItem) holder).tvCity.setText(item.getAddress()==null?"":item.getAddress().getCity().getText());
        }
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    class VHItem extends RecyclerView.ViewHolder {
        TextView tvName;
        TextView tvAlamat;
        TextView tvCity;

        public VHItem(View itemView) {
            super(itemView);
            tvName = (TextView) itemView.findViewById(R.id.tvName);
            tvAlamat= (TextView) itemView.findViewById(R.id.tvAlamat);
            tvCity= (TextView) itemView.findViewById(R.id.tvCity);
        }
    }

    class VHHeader extends RecyclerView.ViewHolder {
        TextView tvName;
        TextView tvAlamat;
        TextView tvCity;

        public VHHeader(View itemView) {
            super(itemView);
            tvName = (TextView) itemView.findViewById(R.id.tvName);
            tvAlamat= (TextView) itemView.findViewById(R.id.tvAlamat);
            tvCity= (TextView) itemView.findViewById(R.id.tvCity);
        }
    }

}