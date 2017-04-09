package id.co.kurindo.kurindo.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import id.co.kurindo.kurindo.R;
import id.co.kurindo.kurindo.app.AppConfig;
import id.co.kurindo.kurindo.model.PacketService;
import id.co.kurindo.kurindo.model.TUser;

/**
 * Created by DwiM on 11/9/2016.
 */
public class TUserSpAdapter extends BaseAdapter {

    Context context;
    List<TUser> data = new ArrayList<>();

    public TUserSpAdapter(Context context, List<TUser> data) {
        this.context = context;
        this.data = data;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        int layout = R.layout.list_item_user;
        View v = LayoutInflater.from(parent.getContext()).inflate(layout, parent, false);
        TextView nama = (TextView) v.findViewById(R.id.tvNama);
        TextView alamat = (TextView) v.findViewById(R.id.tvAlamat);
        TextView kota = (TextView) v.findViewById(R.id.tvKota);

        TUser user = data.get(position);

        if(user ==null || user.getName()==null || user.getAddress()==null || user.getAddress().getKabupaten() == null){
            nama.setText("Pilih salah satu ");
            alamat.setVisibility(View.GONE);
            kota.setVisibility(View.GONE);
        }else{
            nama.setText(user.getName() + " ( "+user.getRole()+" ) ");
            alamat.setText(user.getAddress().getAlamat());
            kota.setText(user.getAddress().getKecamatan() +", "+user.getAddress().getKabupaten());
            alamat.setVisibility(View.VISIBLE);
            kota.setVisibility(View.VISIBLE);
        }
       return v;
    }
}