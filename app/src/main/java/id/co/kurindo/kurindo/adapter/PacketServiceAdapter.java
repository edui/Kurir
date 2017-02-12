package id.co.kurindo.kurindo.adapter;

/**
 * Created by DwiM on 11/9/2016.
 */

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
import id.co.kurindo.kurindo.model.City;
import id.co.kurindo.kurindo.model.Packet;
import id.co.kurindo.kurindo.model.PacketService;

/**
 * Created by DwiM on 11/9/2016.
 */
public class PacketServiceAdapter extends BaseAdapter {

    Context context;
    List<PacketService> data = new ArrayList<>();
    private String[] bgColors;
    private int size;

    public PacketServiceAdapter(Context context, List<PacketService> data) {
        this(context, data, 10);
    }
    public PacketServiceAdapter(Context context, List<PacketService> data, int size) {
        this.context = context;
        this.data = data;
        this.size = size;
        bgColors = context.getResources().getStringArray(R.array.list_bg);
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
        int layout = R.layout.list_item_packet_service;
        if(size == 1) layout = R.layout.list_item_packet_service_small;
        View v = LayoutInflater.from(parent.getContext()).inflate(layout, parent, false);
        ImageView ivCode = (ImageView) v.findViewById( R.id.iv_service_code);
        //TextView code = (TextView) v.findViewById(R.id.service_code);
        TextView text = (TextView) v.findViewById(R.id.service_text);
        TextView etd = (TextView) v.findViewById(R.id.service_etd);

        PacketService paket = data.get(position);

        if(paket.getCode().equalsIgnoreCase(AppConfig.PACKET_SDS)) {
            ivCode.setImageResource(R.drawable.icon_sds);
            etd.setBackgroundColor(Color.argb(200,209,124,31));
        }else if(paket.getCode().equalsIgnoreCase(AppConfig.PACKET_NDS)){
            ivCode.setImageResource(R.drawable.icon_nds);
            etd.setBackgroundColor(Color.argb(200,15,131,180));
        }else{
            ivCode.setImageResource(R.drawable.icon_ens);
            etd.setBackgroundColor(Color.argb(200,44,39,85));
        }
        etd.setText(paket.getEtd());
        //code.setText(String.valueOf(data.get(position).getCode()));
        text.setText(paket.getText());
        String color = bgColors[position % bgColors.length];
        return v;
    }
}