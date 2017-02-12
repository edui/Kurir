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
import id.co.kurindo.kurindo.helper.OrderViaMapHelper;
import id.co.kurindo.kurindo.helper.ViewHelper;
import id.co.kurindo.kurindo.model.TOrder;
import id.co.kurindo.kurindo.model.TPacket;

/**
 * Created by DwiM on 11/9/2016.
 */
public class TPacketViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    Context context;
    List<TPacket> data = new ArrayList<>();
    private String[] bgColors;
    private OnItemClickListener itemClickListener;

    public TPacketViewAdapter(Context context, List<TPacket> data) {
        this(context, data, null);
    }
    public TPacketViewAdapter(Context context, List<TPacket> data, OnItemClickListener itemClickListener) {
        this.context = context;
        this.data = data;
        bgColors = context.getResources().getStringArray(R.array.list_bg);
        this.itemClickListener = itemClickListener;
    }

    public void updateData(List<TPacket> data){
        this.data = data;
    }
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder;
        View v;
        v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_packet_view, parent, false);
        viewHolder = new MyItemHolder( v);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        final TPacket packet = data.get(position);
        TOrder order = ViewHelper.getInstance().getOrder();
        if(order != null){
            if(order.getService_code().equalsIgnoreCase(AppConfig.PACKET_SDS)){
                ((MyItemHolder) holder).ivServiceCodeIcon.setImageResource(R.drawable.icon_sds);
            }else if(order.getService_code().equalsIgnoreCase(AppConfig.PACKET_NDS)) {
                ((MyItemHolder) holder).ivServiceCodeIcon.setImageResource(R.drawable.icon_nds);
            }else if(order.getService_code().equalsIgnoreCase(AppConfig.PACKET_ENS)) {
                ((MyItemHolder) holder).ivServiceCodeIcon.setImageResource(R.drawable.icon_ens);
            }
            ((MyItemHolder) holder)._genderPengirimText.setVisibility(View.GONE);
            ((MyItemHolder) holder)._genderPenerimaText.setVisibility(View.GONE);
            if(order != null){
                if(order.getService_type().equalsIgnoreCase(AppConfig.KEY_DOSEND)) {
                    ((MyItemHolder) holder).ivServiceIcon.setImageResource(R.drawable.do_send_icon);
                }else if(order.getService_type().equalsIgnoreCase(AppConfig.KEY_DOJEK)) {
                    ((MyItemHolder) holder).ivServiceIcon.setImageResource(R.drawable.do_jek_icon);

                    ((MyItemHolder) holder)._genderPengirimText.setVisibility(View.VISIBLE);
                    ((MyItemHolder) holder)._genderPenerimaText.setVisibility(View.VISIBLE);
                    ((MyItemHolder) holder)._genderPengirimText.setText(packet.getDestination().getGender());
                    ((MyItemHolder) holder)._genderPenerimaText.setText(packet.getOrigin().getGender());
                }else if(order.getService_type().equalsIgnoreCase(AppConfig.KEY_DOWASH)) {
                    ((MyItemHolder) holder).ivServiceIcon.setImageResource(R.drawable.do_wash_icon);
                }else if(order.getService_type().equalsIgnoreCase(AppConfig.KEY_DOSHOP)) {
                    ((MyItemHolder) holder).ivServiceIcon.setImageResource(R.drawable.do_shop_icon);
                }
            }
            ((MyItemHolder) holder)._awbText.setText(order.getAwb());
            ((MyItemHolder) holder)._infoPaketText.setText(packet.getIsi_kiriman() );
            ((MyItemHolder) holder)._beratText.setText("Berat : "+packet.getBerat_kiriman() + " Kg");
            ((MyItemHolder) holder)._ongkosPaketText.setText("Biaya : "+ AppConfig.formatCurrency( (packet.getBiaya() == null ? 0 : packet.getBiaya().doubleValue()) ));

            ((MyItemHolder) holder)._namaPengirimText.setText(packet.getOrigin().getName());
            ((MyItemHolder) holder)._alamatPengirimText.setText(packet.getOrigin().getAddress().toStringFormatted());
            ((MyItemHolder) holder)._teleponPengirimText.setText(packet.getOrigin().getPhone());

            ((MyItemHolder) holder)._namaPenerimaText.setText(packet.getDestination().getName());
            ((MyItemHolder) holder)._alamatPenerimaText.setText(packet.getDestination().getAddress().toStringFormatted());
            ((MyItemHolder) holder)._teleponPenerimaText.setText(packet.getDestination().getPhone());

            ((MyItemHolder) holder)._kotaPengirimText.setText("Dari: \n"+packet.getOrigin().getAddress().getKecamatan());
            ((MyItemHolder) holder)._kotaPenerimaText.setText("Ke: \n"+packet.getDestination().getAddress().getKecamatan());

            ((MyItemHolder) holder)._statusText.setText((order.getStatusText()==null?"":order.getStatusText()) +"\n"+order.getUpdated_date()==null? order.getCreated_date() == null? "":order.getCreated_date() : order.getUpdated_date());
            String data = order.getAwb()+";"+packet.getOrigin().getName()+";"+packet.getOrigin().getAddress().toStringFormatted()+";"+packet.getOrigin().getPhone()+";"+packet.getOrigin().getAddress().getKecamatan()
                    +";"+packet.getDestination().getName()+";"+packet.getDestination().getAddress().toStringFormatted()+";"+packet.getDestination().getPhone()+";"+packet.getDestination().getAddress().getKecamatan();
            //barcodeView.setImageBitmap(AppConfig.encodeContentsToBarcode(packet.getResi(), BarcodeFormat.CODE_128));
            ((MyItemHolder) holder).barcodeView.setImageBitmap(AppConfig.encodeContentsToBarcode(data, BarcodeFormat.QR_CODE));

            ((MyItemHolder) holder).btnLihatRute.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(itemClickListener != null) itemClickListener.onViewRouteButtonClick(v, position,  packet);
                }
            });

        }

    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public static class MyItemHolder extends RecyclerView.ViewHolder {
        TextView _namaPengirimText;
        TextView _alamatPengirimText;
        TextView _teleponPengirimText;
        TextView _kotaPengirimText;
        TextView _namaPenerimaText;
        TextView _alamatPenerimaText;
        TextView _teleponPenerimaText;
        TextView _kotaPenerimaText;

        TextView _genderPengirimText;
        TextView _genderPenerimaText;

        TextView _beratText;
        TextView _infoPaketText;
        TextView _ongkosPaketText;
        TextView _awbText;
        TextView _statusText;
        ImageView barcodeView;
        ImageView ivServiceCodeIcon;
        ImageView ivServiceIcon;

        AppCompatButton btnLihatRute;

        public MyItemHolder(View itemView) {
            super(itemView);
            _namaPengirimText = (TextView) itemView.findViewById(R.id.text_nama_pengirim);
             _alamatPengirimText = (TextView) itemView.findViewById(R.id.text_alamat_pengirim);
             _teleponPengirimText = (TextView) itemView.findViewById(R.id.text_telepon_pengirim);
             _kotaPengirimText = (TextView) itemView.findViewById(R.id.text_kota_pengirim);
             _namaPenerimaText = (TextView) itemView.findViewById(R.id.text_nama_penerima);
             _alamatPenerimaText = (TextView) itemView.findViewById(R.id.text_alamat_penerima);
             _teleponPenerimaText = (TextView) itemView.findViewById(R.id.text_telepon_penerima);
             _kotaPenerimaText = (TextView) itemView.findViewById(R.id.text_kota_penerima);

            _genderPengirimText = (TextView) itemView.findViewById(R.id.text_gender_pengirim);
            _genderPenerimaText = (TextView) itemView.findViewById(R.id.text_gender_penerima);

            _beratText = (TextView) itemView.findViewById(R.id.text_berat_paket);
             _infoPaketText = (TextView) itemView.findViewById(R.id.text_info_paket);
             _ongkosPaketText= (TextView) itemView.findViewById(R.id.text_ongkos_paket);
             _awbText = (TextView) itemView.findViewById(R.id.awbTextView);

            _statusText = (TextView )itemView.findViewById(R.id.statusTextView)    ;
            barcodeView = (ImageView) itemView.findViewById(R.id.resi_qrcode)  ;
            ivServiceCodeIcon = (ImageView) itemView.findViewById(R.id.service_code_icon)  ;
            ivServiceIcon = (ImageView) itemView.findViewById(R.id.service_icon) ;
            btnLihatRute= (AppCompatButton) itemView.findViewById(R.id.btnLihatRute) ;


        }
    }
    public interface OnItemClickListener {
        void onViewRouteButtonClick(View view, int position, TPacket packet);
    }

}