package id.co.kurindo.kurindo.adapter;

/**
 * Created by DwiM on 11/9/2016.
 */

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.zxing.BarcodeFormat;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import id.co.kurindo.kurindo.R;
import id.co.kurindo.kurindo.app.AppConfig;
import id.co.kurindo.kurindo.helper.OrderHelper;
import id.co.kurindo.kurindo.model.Order;
import id.co.kurindo.kurindo.model.Packet;

/**
 * Created by DwiM on 11/9/2016.
 */
public class PacketViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    Context context;
    List<Packet> data = new ArrayList<>();
    private String[] bgColors;

    public PacketViewAdapter(Context context, List<Packet> data) {
        this.context = context;
        this.data = data;
        bgColors = context.getResources().getStringArray(R.array.list_bg);
    }

    public void updateData(List<Packet> data){
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
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Packet packet = data.get(position);
        Order order = OrderHelper.getInstance().getOrder();
        if(packet.getServiceCode().equalsIgnoreCase(AppConfig.PACKET_SDS)){
            ((MyItemHolder) holder).ivServiceCodeIcon.setImageResource(R.drawable.icon_sds);
        }else if(packet.getServiceCode().equalsIgnoreCase(AppConfig.PACKET_NDS)) {
            ((MyItemHolder) holder).ivServiceCodeIcon.setImageResource(R.drawable.icon_nds);
        }else if(packet.getServiceCode().equalsIgnoreCase(AppConfig.PACKET_ENS)) {
            ((MyItemHolder) holder).ivServiceCodeIcon.setImageResource(R.drawable.icon_ens);
        }
        ((MyItemHolder) holder)._genderPengirimText.setVisibility(View.GONE);
        ((MyItemHolder) holder)._genderPenerimaText.setVisibility(View.GONE);
        if(order != null){
            if(order.getType().equalsIgnoreCase(AppConfig.KEY_DOSEND)) {
                ((MyItemHolder) holder).ivServiceIcon.setImageResource(R.drawable.do_send_icon);
            }else if(order.getType().equalsIgnoreCase(AppConfig.KEY_DOJEK)) {
                ((MyItemHolder) holder).ivServiceIcon.setImageResource(R.drawable.do_jek_icon);

                ((MyItemHolder) holder)._genderPengirimText.setVisibility(View.VISIBLE);
                ((MyItemHolder) holder)._genderPenerimaText.setVisibility(View.VISIBLE);
                ((MyItemHolder) holder)._genderPengirimText.setText(packet.getGenderPengirim());
                ((MyItemHolder) holder)._genderPenerimaText.setText(packet.getGenderPenerima());
            }else if(order.getType().equalsIgnoreCase(AppConfig.KEY_DOWASH)) {
                ((MyItemHolder) holder).ivServiceIcon.setImageResource(R.drawable.do_wash_icon);
            }else if(order.getType().equalsIgnoreCase(AppConfig.KEY_DOSHOP)) {
                ((MyItemHolder) holder).ivServiceIcon.setImageResource(R.drawable.do_shop_icon);
            }
        }
        ((MyItemHolder) holder)._awbText.setText(packet.getResi());
        ((MyItemHolder) holder)._infoPaketText.setText(packet.getInfoPaket() +(packet.isViaMobil()? "\npakai Mobil = YES":""));
        ((MyItemHolder) holder)._beratText.setText("Berat : "+packet.getBerat() + " Kg");
        ((MyItemHolder) holder)._ongkosPaketText.setText("Biaya : "+ AppConfig.formatCurrency( packet.getBiaya() ));

        ((MyItemHolder) holder)._namaPengirimText.setText(packet.getNamaPengirim());
        ((MyItemHolder) holder)._alamatPengirimText.setText(packet.getAlamatPengirim());
        ((MyItemHolder) holder)._teleponPengirimText.setText(packet.getTeleponPengirim());

        ((MyItemHolder) holder)._namaPenerimaText.setText(packet.getNamaPenerima());
        ((MyItemHolder) holder)._alamatPenerimaText.setText(packet.getAlamatPenerima());
        ((MyItemHolder) holder)._teleponPenerimaText.setText(packet.getTeleponPenerima());

        ((MyItemHolder) holder)._kotaPengirimText.setText("Dari: \n"+packet.getKotaPengirimText());
        ((MyItemHolder) holder)._kotaPenerimaText.setText("Ke: \n"+packet.getKotaPenerimaText());

        ((MyItemHolder) holder)._statusText.setText((packet.getStatusText()==null?"":packet.getStatusText()) +"\n"+packet.getUpdatedDate()==null? packet.getCreatedDate() == null? "":packet.getCreatedDate() : packet.getUpdatedDate());
        String data = packet.getResi()+";"+packet.getNamaPengirim()+";"+packet.getAlamatPengirim()+";"+packet.getTeleponPengirim()+";"+packet.getKotaPengirim()
                +";"+packet.getNamaPenerima()+";"+packet.getTeleponPenerima()+";"+packet.getAlamatPenerima()+";"+packet.getKotaPenerima();
        //barcodeView.setImageBitmap(AppConfig.encodeContentsToBarcode(packet.getResi(), BarcodeFormat.CODE_128));
        ((MyItemHolder) holder).barcodeView.setImageBitmap(AppConfig.encodeContentsToBarcode(data, BarcodeFormat.QR_CODE));
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


        }
    }

}