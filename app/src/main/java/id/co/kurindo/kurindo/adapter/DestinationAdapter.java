package id.co.kurindo.kurindo.adapter;

/**
 * Created by DwiM on 11/9/2016.
 */

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import id.co.kurindo.kurindo.R;
import id.co.kurindo.kurindo.model.TUser;

/**
 * Created by DwiM on 11/9/2016.
 */
public class DestinationAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    Context context;
    List<TUser> data = new ArrayList<>();
   // private String[] bgColors;
    int selected = -1;
    boolean multiple= false;
    private OnItemClickListener itemClickListener;

    public DestinationAdapter(Context context, List<TUser> data,  OnItemClickListener itemClickListener) {
        this.context = context;
        this.data = data;
        //bgColors = context.getResources().getStringArray(R.array.list_bg);
        this.itemClickListener = itemClickListener;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder;
        View v;
        v = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.list_item_destination, parent, false);
        viewHolder = new MyItemHolder(v);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder vholder, final int position) {
        final TUser r = data.get(position);
        MyItemHolder holder = (MyItemHolder) vholder;
        if(r.getAddress() != null){
            holder.alamat.setText(r.getAddress().toStringFormatted());
            holder.alamat.setVisibility(View.VISIBLE);
        }else{
            holder.alamat.setText("");
            holder.alamat.setVisibility(View.GONE);
        }
        //holder.city.setText((r.getAddress()==null?"": r.getAddress().toStringKecKab()));
        String name = r.getName();
        if(r.getName() != null){
            name = r.getName();
        }
        if(r.getPhone() != null){
            name += ", "+r.getPhone();
        }
        holder.name.setText(name);

        if(r.getName() == null || r.getName().isEmpty()) holder.name.setVisibility(View.GONE);

        holder.btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                itemClickListener.onEditButtonClick(view, position);
            }
        });

        holder.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                itemClickListener.onDeleteButtonClick(view, position);
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
        protected TextView alamat;
        protected TextView name;
        protected ImageButton btnDelete;
        protected ImageButton btnEdit;
        public MyItemHolder(View itemView) {
            super(itemView);
            this.alamat= (TextView) itemView.findViewById(R.id.address_text);
            this.name= (TextView) itemView.findViewById(R.id.name_text);
            this.btnDelete= (ImageButton) itemView.findViewById(R.id.btnDelete);
            this.btnEdit= (ImageButton) itemView.findViewById(R.id.btnEdit);
        }
    }

    public interface OnItemClickListener {
        void onEditButtonClick(View view, int position);
        void onDeleteButtonClick(View view, int position);
    }
}