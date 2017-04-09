package id.co.kurindo.kurindo.adapter;

/**
 * Created by DwiM on 11/9/2016.
 */

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import id.co.kurindo.kurindo.R;
import id.co.kurindo.kurindo.app.AppConfig;
import id.co.kurindo.kurindo.model.TUser;
import id.co.kurindo.kurindo.model.User;

/**
 * Created by DwiM on 11/9/2016.
 */
public class TKurirAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    Context context;
    List<TUser> data = new ArrayList<>();
   // private String[] bgColors;
    int selected = -1;
    boolean multiple= false;
    private OnItemClickListener itemClickListener;

    public TKurirAdapter(Context context, List<TUser> data, OnItemClickListener mOnItemClickListener) {
        //super(context, data);
        this.context = context;
        this.data = data;
        this.itemClickListener = mOnItemClickListener;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder;
        View v;
        v = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.list_item_kurir, parent, false);
        viewHolder = new MyItemHolder(v);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder vholder, final int position) {
        MyItemHolder holder = (MyItemHolder) vholder;
        TUser r = data.get(position);

        String joined = "Joined: "+AppConfig.getTimeAgo(r.getCreated_at());
        if(r.getAddress() != null && r.getAddress().getAlamat() != null ){
            holder.alamat.setText(r.getAddress().getAlamat());
        }
        if(r.getAddress() != null && r.getAddress().getCity() != null ){
            holder.city.setText(r.getAddress().getCity().getText()+"\n"+joined);
        }else{
            holder.city.setText("Unknown"+"\n"+joined);
        }
        holder.name.setText(r.getFirstname() + " "+r.getLastname());
        holder.phone.setText(r.getPhone());
        holder.nik.setText("NIK: "+r.getNik());
        holder.simc.setText("SIM C: "+r.getSimc());

        if(r.getFirstname().isEmpty()) holder.name.setVisibility(View.GONE);
        if(r.getPhone().isEmpty()) holder.phone.setVisibility(View.GONE);
        if(r.getNik()==null || r.getNik().isEmpty()) holder.nik.setVisibility(View.GONE);
        if(r.getSimc()==null || r.getSimc().isEmpty()) holder.simc.setVisibility(View.GONE);

        //String color = bgColors[position % bgColors.length];
        if(multiple){
            for (int i = 0; i < selectedItems.size(); i++) {
                if(position == selectedItems.keyAt(i)) {
                    holder.radioSelected.setChecked(true);
                    break;
                }
            }
        }else{
            holder.radioSelected.setChecked(position == selected);
        }
        holder.alamat.setBackgroundColor(position == selected? Color.CYAN : Color.TRANSPARENT);
        holder.city.setBackgroundColor(position == selected? Color.CYAN : Color.TRANSPARENT);
        holder.approvedBtn.setVisibility(View.VISIBLE);
        holder.approvedBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemClickListener.onApprovedButtonClick(v, position);
            }
        });
    }

    public void setMultipleMode(boolean multiple){
        this.multiple = multiple;
    }
    public void selected(int pos){
        selected = pos;
        notifyItemRangeChanged(0, data.size());
    }
    @Override
    public int getItemCount() {
        return data.size();
    }


    private SparseBooleanArray selectedItems = new SparseBooleanArray();
    public void setSelection(int pos) {
        selectedItems.put(pos, true);
        notifyItemChanged(pos);
    }
    public void toggleSelection(int pos) {
        if (selectedItems.get(pos, false)) {
            selectedItems.delete(pos);
        }
        else {
            selectedItems.put(pos, true);
        }
        notifyItemChanged(pos);
    }

    public void clearSelections() {
        selectedItems.clear();
        notifyDataSetChanged();
    }

    public int getSelectedItemCount() {
        return selectedItems.size();
    }

    public List<Integer> getSelectedItems() {
        List<Integer> items =
                new ArrayList<Integer>(selectedItems.size());
        for (int i = 0; i < selectedItems.size(); i++) {
            items.add(selectedItems.keyAt(i));
        }
        return items;
    }

    public static class MyItemHolder extends RecyclerView.ViewHolder {
        protected RadioButton radioSelected;
        protected TextView alamat;
        protected TextView city;
        protected TextView name;
        protected TextView phone;
        protected TextView nik;
        protected TextView simc;
        protected ImageButton approvedBtn;
        public MyItemHolder(View itemView) {
            super(itemView);
            this.alamat= (TextView) itemView.findViewById(R.id.address_text);
            this.city= (TextView) itemView.findViewById(R.id.city_text);
            this.radioSelected= (RadioButton) itemView.findViewById(R.id.radio_selected);
            this.name= (TextView) itemView.findViewById(R.id.name_text);
            this.phone= (TextView) itemView.findViewById(R.id.telepon_text);
            this.approvedBtn = (ImageButton) itemView.findViewById(R.id.approvedBtn);
            this.nik= (TextView) itemView.findViewById(R.id.nik_text);
            this.simc= (TextView) itemView.findViewById(R.id.simc_text);
        }
    }

    public interface OnItemClickListener {
        void onApprovedButtonClick(View view, int position);
    }
}