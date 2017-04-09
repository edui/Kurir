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

import com.google.zxing.BarcodeFormat;

import java.util.ArrayList;
import java.util.List;

import id.co.kurindo.kurindo.R;
import id.co.kurindo.kurindo.app.AppConfig;
import id.co.kurindo.kurindo.model.TOrder;
import id.co.kurindo.kurindo.model.TPacket;
import id.co.kurindo.kurindo.model.TUser;

/**
 * Created by DwiM on 11/9/2016.
 */
public class LocationViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    Context context;
    TOrder order;
    private String[] bgColors;
    private OnItemClickListener itemClickListener;

    public LocationViewAdapter(Context context, TOrder order) {
        this(context, order, null);
    }
    public LocationViewAdapter(Context context, TOrder order, OnItemClickListener itemClickListener) {
        this.context = context;
        this.order = order;
        bgColors = context.getResources().getStringArray(R.array.list_bg);
        this.itemClickListener = itemClickListener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder;
        View v;
        v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_location_view, parent, false);
        viewHolder = new MyItemHolder( v);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder vholder, final int position) {
       if(order != null){
           MyItemHolder holder = (MyItemHolder) vholder;
            if(order.getService_code().equalsIgnoreCase(AppConfig.PACKET_SDS)){
                holder.ivServiceCodeIcon.setImageResource(R.drawable.icon_sds);
            }else if(order.getService_code().equalsIgnoreCase(AppConfig.PACKET_NDS)) {
                holder.ivServiceCodeIcon.setImageResource(R.drawable.icon_nds);
            }else if(order.getService_code().equalsIgnoreCase(AppConfig.PACKET_ENS)) {
                holder.ivServiceCodeIcon.setImageResource(R.drawable.icon_ens);
            }else if(order.getService_code().equalsIgnoreCase(AppConfig.PACKET_NDNS)) {
                holder.ivServiceCodeIcon.setImageResource(R.drawable.icon_ens);
            }
            if(order != null){
                if(order.getService_type().equalsIgnoreCase(AppConfig.KEY_DOSERVICE)) {
                    holder.ivServiceIcon.setImageResource(R.drawable.do_service_icon);
                }else if(order.getService_type().equalsIgnoreCase(AppConfig.KEY_DOWASH)) {
                    holder.ivServiceIcon.setImageResource(R.drawable.do_wash_icon);
                }
            }
            holder._awbText.setText(order.getAwb());
           holder._genderPengirimText.setVisibility(View.GONE);

           final TUser location = order.getPlace();
           if(location != null){
               holder._titlePengirimText.setText("Pengguna");
               if(location.getName()==null || location.getName().equalsIgnoreCase("null")){
                   holder._namaPengirimText.setVisibility(View.GONE);
                   holder._teleponPengirimText.setVisibility(View.GONE);
               }else{
                   holder._namaPengirimText.setText(location.getName());
                   holder._namaPengirimText.setVisibility(View.VISIBLE);
                   holder._teleponPengirimText.setText(location.getPhone());
                   holder._teleponPengirimText.setVisibility(View.VISIBLE);
               }
               holder._alamatPengirimText.setText(location.getAddress().toStringFormatted());

               holder._kotaPengirimText.setText("Di : \n"+location.getAddress().getKecamatan());

               holder._statusText.setText((order.getStatusText()==null?"":order.getStatusText()) +"\n"+order.getUpdated_date()==null? order.getCreated_date() == null? "":order.getCreated_date() : order.getUpdated_date());
               String data = order.getAwb()+";"+location.getName()+";"+location.getAddress().toStringFormatted()+";"+location.getPhone()+";"+location.getAddress().getKecamatan()
                       +";"+location.getName()+";"+location.getAddress().toStringFormatted()+";"+location.getPhone()+";"+location.getAddress().getKecamatan();
               //barcodeView.setImageBitmap(AppConfig.encodeContentsToBarcode(packet.getResi(), BarcodeFormat.CODE_128));
               holder.barcodeView.setImageBitmap(AppConfig.encodeContentsToBarcode(data, BarcodeFormat.QR_CODE));

               if(order.getStatus() != null){
                   holder.btnLihatRute.setText("Lihat Lokasi");
                   holder.btnLihatRute.setVisibility(View.VISIBLE);
                   holder.btnLihatRute.setOnClickListener(new View.OnClickListener() {
                       @Override
                       public void onClick(View v) {
                           if(itemClickListener != null) itemClickListener.onViewLocationButtonClick(v, position,  location);
                       }
                   });
               }
               holder.tvPickupTime.setText(order.getPickup()==null?"Call Pembeli":order.getPickup());
               holder.tvDropTime.setText(order.getDroptime()==null?"":order.getDroptime());

           }

       }

    }

    @Override
    public int getItemCount() {
        return (order==null? 0: (order.getPlace() ==null? 0 : 1));
    }

    public static class MyItemHolder extends RecyclerView.ViewHolder {
        TextView _titlePengirimText;
        TextView _namaPengirimText;
        TextView _alamatPengirimText;
        TextView _teleponPengirimText;
        TextView _kotaPengirimText;

        TextView _genderPengirimText;

        TextView _awbText;
        TextView _statusText;
        ImageView barcodeView;
        ImageView ivServiceCodeIcon;
        ImageView ivServiceIcon;

        TextView tvPickupTime;
        TextView tvDropTime;
        AppCompatButton btnLihatRute;

        public MyItemHolder(View itemView) {
            super(itemView);
            _titlePengirimText = (TextView) itemView.findViewById(R.id.text_title_pengirim);
            _namaPengirimText = (TextView) itemView.findViewById(R.id.text_nama_pengirim);
             _alamatPengirimText = (TextView) itemView.findViewById(R.id.text_alamat_pengirim);
             _teleponPengirimText = (TextView) itemView.findViewById(R.id.text_telepon_pengirim);
             _kotaPengirimText = (TextView) itemView.findViewById(R.id.text_kota_pengirim);

            _genderPengirimText = (TextView) itemView.findViewById(R.id.text_gender_pengirim);

             _awbText = (TextView) itemView.findViewById(R.id.awbTextView);

            tvPickupTime= (TextView) itemView.findViewById(R.id.tvPickupTime);
            tvDropTime= (TextView) itemView.findViewById(R.id.tvDropTime);
            _statusText = (TextView )itemView.findViewById(R.id.statusTextView)    ;
            barcodeView = (ImageView) itemView.findViewById(R.id.resi_qrcode)  ;
            ivServiceCodeIcon = (ImageView) itemView.findViewById(R.id.service_code_icon)  ;
            ivServiceIcon = (ImageView) itemView.findViewById(R.id.service_icon) ;
            btnLihatRute= (AppCompatButton) itemView.findViewById(R.id.btnLihatRute) ;


        }
    }
    public interface OnItemClickListener {
        void onViewLocationButtonClick(View view, int position, TUser location);
    }

}