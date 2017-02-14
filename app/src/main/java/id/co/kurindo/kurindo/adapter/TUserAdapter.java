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
import id.co.kurindo.kurindo.model.TUser;

/**
 * Created by DwiM on 11/9/2016.
 */
public class TUserAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    Context context;
    List<TUser> data = new ArrayList<>();
   // private String[] bgColors;
    int selected = -1;
    boolean multiple= false;

    public TUserAdapter(Context context, List<TUser> data) {
        this.context = context;
        this.data = data;
        //bgColors = context.getResources().getStringArray(R.array.list_bg);
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder;
        View v;
        v = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.list_item_recipient, parent, false);
        viewHolder = new MyItemHolder(v);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        TUser r = data.get(position);
        ((MyItemHolder) holder).alamat.setText((r.getAddress() == null? "":r.getAddress().getAlamat()));
        ((MyItemHolder) holder).city.setText((r.getAddress()==null || r.getAddress().getCity() == null?"":r.getAddress().getCity().getText()));
        ((MyItemHolder) holder).name.setText(r.getName());
        ((MyItemHolder) holder).phone.setText(r.getPhone());

        if(r.getName() == null || r.getName().isEmpty()) ((MyItemHolder) holder).name.setVisibility(View.GONE);
        if(r.getPhone() == null || r.getPhone().isEmpty()) ((MyItemHolder) holder).phone.setVisibility(View.GONE);

        //String color = bgColors[position % bgColors.length];
        if(multiple){
            for (int i = 0; i < selectedItems.size(); i++) {
                if(position == selectedItems.keyAt(i)) {
                    ((MyItemHolder) holder).radioSelected.setChecked(true);
                    break;
                }
            }
        }else{
            ((MyItemHolder) holder).radioSelected.setChecked(position == selected);
        }
        ((MyItemHolder) holder).alamat.setBackgroundColor(position == selected? Color.CYAN : Color.TRANSPARENT);
        ((MyItemHolder) holder).city.setBackgroundColor(position == selected? Color.CYAN : Color.TRANSPARENT);
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
        protected ImageButton deleteBtn;
        public MyItemHolder(View itemView) {
            super(itemView);
            this.alamat= (TextView) itemView.findViewById(R.id.address_text);
            this.city= (TextView) itemView.findViewById(R.id.city_text);
            this.radioSelected= (RadioButton) itemView.findViewById(R.id.radio_selected);
            this.name= (TextView) itemView.findViewById(R.id.name_text);
            this.phone= (TextView) itemView.findViewById(R.id.telepon_text);
        }
    }
}