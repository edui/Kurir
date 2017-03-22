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
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import id.co.kurindo.kurindo.R;
import id.co.kurindo.kurindo.model.Address;
import id.co.kurindo.kurindo.model.City;
import id.co.kurindo.kurindo.model.Shop;
import id.co.kurindo.kurindo.model.TUser;

/**
 * Created by DwiM on 11/9/2016.
 */
public class ShopBranchAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    Context context;
    List<Shop> data = new ArrayList<>();

    public ShopBranchAdapter(Context context, List<Shop> data) {
        this.context = context;
        this.data = data;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder;
        View v;
        v = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.list_item_city_shop, parent, false);
        viewHolder = new MyItemHolder(v);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder vholder, final int position) {
        MyItemHolder holder = (MyItemHolder) vholder;
        Shop shop = data.get(position);
        TUser pic = shop.getPic();
        if(pic != null){
            holder.pic.setText(pic.getName());

            Address r = pic.getAddress();
            holder.code.setText((r.getKecamatan()));
            holder.text.setText(r.getKabupaten());
        }
        holder.code.setVisibility(View.VISIBLE);
        holder.text.setVisibility(View.VISIBLE);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }


    public static class MyItemHolder extends RecyclerView.ViewHolder {
        protected TextView code;
        protected TextView text;
        protected TextView pic;
        protected Button btnDelete;
        protected Button btnView;

        public MyItemHolder(View itemView) {
            super(itemView);
            this.code= (TextView) itemView.findViewById(R.id.city_code);
            this.text= (TextView) itemView.findViewById(R.id.city_text);
            this.pic= (TextView) itemView.findViewById(R.id.tvPic);
            this.btnDelete= (Button) itemView.findViewById(R.id.btnDelete);
            this.btnView= (Button) itemView.findViewById(R.id.btnView);
        }
    }
}