package id.co.kurindo.kurindo.adapter;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import id.co.kurindo.kurindo.R;
import id.co.kurindo.kurindo.app.AppConfig;
import id.co.kurindo.kurindo.model.TOrder;
import id.co.kurindo.kurindo.model.TPacket;

/**
 * Created by DwiM on 11/9/2016.
 */
public class MonitorTOrderAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    Context context;
    List<TOrder> data = new ArrayList<>();
    private String[] bgColors;
    protected OnItemClickListener itemClickListener;

    public MonitorTOrderAdapter(Context context, List<TOrder> data, OnItemClickListener mOnItemClickListener) {
        this.context = context;
        this.data = data;
        this.itemClickListener = mOnItemClickListener;
        bgColors = context.getResources().getStringArray(R.array.list_bg);
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder;
        View v;
        v = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.list_item_monitor_order, parent, false);
        viewHolder = new MyItemHolder(v);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder vholder, final int position) {
        TOrder order = data.get(position);
        MyItemHolder holder = (MyItemHolder) vholder;
        setupTextAndStatus(holder, order, position);
        setupServiceIcon(holder, order, position);
        setupUpdateBtn(holder, order, position);

        if(order.getStatus().equalsIgnoreCase(AppConfig.KEY_KUR100)) {
            setup_status_kur100(holder, order, position);
        }else if(order.getStatus().equalsIgnoreCase(AppConfig.KEY_KUR101)){
            setup_status_kur101(holder, order, position);
        }else if(order.getStatus().equalsIgnoreCase(AppConfig.KEY_KUR200)){
            setup_status_kur200(holder, order, position);

        }else if(order.getStatus().equalsIgnoreCase(AppConfig.KEY_KUR300)){
            setup_status_kur300(holder, order, position);

        }else if(order.getStatus().equalsIgnoreCase(AppConfig.KEY_KUR310)){
            setup_status_kur310(holder, order, position);

        }else if(order.getStatus().equalsIgnoreCase(AppConfig.KEY_KUR400)){
            setup_status_kur400(holder, order, position);

        }else if(order.getStatus().equalsIgnoreCase(AppConfig.KEY_KUR350)){
            setup_status_kur350(holder, order, position);

        }else if(order.getStatus().equalsIgnoreCase(AppConfig.KEY_KUR500)){
            setup_status_kur500(holder, order, position);
        }else{
            setup_hide_btn(holder);
        }

    }

    protected void setup_hide_btn(MyItemHolder holder) {
        holder.kur300Btn.setVisibility(View.GONE);
        holder.kur310Btn.setVisibility(View.GONE);
        holder.kur400Btn.setVisibility(View.GONE);
        holder.kur500Btn.setVisibility(View.GONE);
        holder.kur350Btn.setVisibility(View.GONE);

        holder.kur100Btn.setVisibility(View.GONE);
        holder.kur101Btn.setVisibility(View.GONE);
        holder.kur200Btn.setVisibility(View.GONE);

    }

    protected void setup_status_kur500(MyItemHolder holder, TOrder order, int position) {
        holder.kur500Btn.setImageResource(R.drawable.status04_2_icon);
        holder.kur500Btn.setVisibility(View.VISIBLE);
        holder.kur500Btn.setEnabled(false);

        holder.kur300Btn.setImageResource(R.drawable.status01_2_icon);
        holder.kur300Btn.setVisibility(View.VISIBLE);
        holder.kur300Btn.setEnabled(false);

        holder.kur310Btn.setImageResource(R.drawable.status03_2_icon);
        holder.kur310Btn.setVisibility(View.VISIBLE);
        holder.kur310Btn.setEnabled(false);

        holder.kur100Btn.setVisibility(View.GONE);
        holder.kur101Btn.setVisibility(View.GONE);
        holder.kur200Btn.setVisibility(View.GONE);
    }

    protected void setup_status_kur350(MyItemHolder holder, TOrder order, final int position) {
        holder.kur500Btn.setImageResource(R.drawable.status04_1_icon);
        holder.kur500Btn.setVisibility(View.VISIBLE);
        holder.kur500Btn.setEnabled(true);
        holder.kur500Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemClickListener.onPickButtonClick(v,position, AppConfig.KEY_KUR500);
            }
        });

        holder.kur400Btn.setImageResource(R.drawable.status05_1_icon);
        holder.kur400Btn.setVisibility(View.VISIBLE);
        holder.kur400Btn.setEnabled(true);
        holder.kur400Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemClickListener.onPickButtonClick(v,position, AppConfig.KEY_KUR400);
            }
        });

        holder.kur300Btn.setImageResource(R.drawable.status01_2_icon);
        holder.kur300Btn.setVisibility(View.VISIBLE);
        holder.kur300Btn.setEnabled(false);

        holder.kur310Btn.setImageResource(R.drawable.status03_2_icon);
        holder.kur310Btn.setVisibility(View.VISIBLE);
        holder.kur310Btn.setEnabled(false);

        holder.kur350Btn.setImageResource(R.drawable.status06_2_icon);
        holder.kur350Btn.setVisibility(View.VISIBLE);
        holder.kur350Btn.setEnabled(false);

        holder.kur100Btn.setVisibility(View.GONE);
        holder.kur101Btn.setVisibility(View.GONE);
        holder.kur200Btn.setVisibility(View.GONE);
    }

    protected void setup_status_kur400(MyItemHolder holder, TOrder order, final int position) {
        holder.kur350Btn.setImageResource(R.drawable.status06_1_icon);
        holder.kur350Btn.setVisibility(View.VISIBLE);
        holder.kur350Btn.setEnabled(true);
        holder.kur350Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemClickListener.onPickButtonClick(v,position, AppConfig.KEY_KUR350);
            }
        });

        holder.kur400Btn.setImageResource(R.drawable.status05_2_icon);
        holder.kur400Btn.setVisibility(View.VISIBLE);
        holder.kur400Btn.setEnabled(false);

        holder.kur500Btn.setImageResource(R.drawable.status04_0_icon);
        holder.kur500Btn.setVisibility(View.VISIBLE);
        holder.kur500Btn.setEnabled(false);

        holder.kur300Btn.setImageResource(R.drawable.status01_2_icon);
        holder.kur300Btn.setVisibility(View.VISIBLE);
        holder.kur300Btn.setEnabled(false);

        holder.kur310Btn.setImageResource(R.drawable.status03_2_icon);
        holder.kur310Btn.setVisibility(View.VISIBLE);
        holder.kur310Btn.setEnabled(false);

        holder.kur100Btn.setVisibility(View.GONE);
        holder.kur101Btn.setVisibility(View.GONE);
        holder.kur200Btn.setVisibility(View.GONE);

    }

    protected void setup_status_kur310(MyItemHolder holder, TOrder order, final int position) {
        holder.kur500Btn.setImageResource(R.drawable.status04_1_icon);
        holder.kur500Btn.setVisibility(View.VISIBLE);
        holder.kur500Btn.setEnabled(true);
        holder.kur500Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemClickListener.onPickButtonClick(v,position, AppConfig.KEY_KUR500);
            }
        });

        holder.kur400Btn.setImageResource(R.drawable.status05_1_icon);
        holder.kur400Btn.setVisibility(View.VISIBLE);
        holder.kur400Btn.setEnabled(true);
        holder.kur400Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemClickListener.onPickButtonClick(v,position, AppConfig.KEY_KUR400);
            }
        });

        holder.kur300Btn.setImageResource(R.drawable.status01_2_icon);
        holder.kur300Btn.setVisibility(View.VISIBLE);
        holder.kur300Btn.setEnabled(false);

        holder.kur310Btn.setImageResource(R.drawable.status03_2_icon);
        holder.kur310Btn.setVisibility(View.VISIBLE);
        holder.kur310Btn.setEnabled(false);

        holder.kur100Btn.setVisibility(View.GONE);
        holder.kur101Btn.setVisibility(View.GONE);
        holder.kur200Btn.setVisibility(View.GONE);
    }

    protected void setup_status_kur300(MyItemHolder holder, TOrder order, final int position) {
        holder.kur310Btn.setImageResource(R.drawable.status03_1_icon);
        holder.kur310Btn.setVisibility(View.VISIBLE);
        holder.kur310Btn.setEnabled(true);
        holder.kur310Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemClickListener.onPickButtonClick(v,position, AppConfig.KEY_KUR310);
            }
        });

        holder.kur300Btn.setImageResource(R.drawable.status01_2_icon);
        holder.kur300Btn.setVisibility(View.VISIBLE);
        holder.kur300Btn.setEnabled(false);

        holder.kur100Btn.setVisibility(View.GONE);
        holder.kur101Btn.setVisibility(View.GONE);
        holder.kur200Btn.setVisibility(View.GONE);
        holder.kur350Btn.setVisibility(View.GONE);
        holder.kur400Btn.setVisibility(View.GONE);
        holder.kur400Btn.setEnabled(false);

        holder.kur500Btn.setImageResource(R.drawable.status04_0_icon);
        holder.kur500Btn.setVisibility(View.VISIBLE);
        holder.kur500Btn.setEnabled(false);
    }

    protected void setup_status_kur200(MyItemHolder holder, TOrder order, final int position) {
        holder.kur300Btn.setImageResource(R.drawable.status01_1_icon);
        holder.kur300Btn.setVisibility(View.VISIBLE);
        holder.kur300Btn.setEnabled(true);

        holder.kur300Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemClickListener.onPickButtonClick(v,position, AppConfig.KEY_KUR300);
            }
        });

        holder.kur310Btn.setImageResource(R.drawable.status03_0_icon);
        holder.kur310Btn.setVisibility(View.VISIBLE);
        holder.kur310Btn.setEnabled(false);

        holder.kur100Btn.setVisibility(View.GONE);
        holder.kur101Btn.setVisibility(View.GONE);
        holder.kur200Btn.setVisibility(View.GONE);
        holder.kur350Btn.setVisibility(View.GONE);

        holder.kur400Btn.setVisibility(View.GONE);
        holder.kur400Btn.setEnabled(false);

        holder.kur500Btn.setImageResource(R.drawable.status04_0_icon);
        holder.kur500Btn.setVisibility(View.VISIBLE);
        holder.kur500Btn.setEnabled(false);
    }

    protected void setup_status_kur101(MyItemHolder holder, TOrder order, final int position) {
        holder.kur200Btn.setImageResource(R.drawable.accept_booking_icon);
        holder.kur200Btn.setVisibility(View.VISIBLE);
        holder.kur200Btn.setEnabled(true);
        holder.kur200Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemClickListener.onPickButtonClick(v, position, AppConfig.KEY_KUR200);
            }
        });

        holder.kur100Btn.setImageResource(R.drawable.reject_booking_icon);
        holder.kur100Btn.setVisibility(View.VISIBLE);
        holder.kur100Btn.setEnabled(true);
        holder.kur100Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemClickListener.onPickButtonClick(v, position, AppConfig.KEY_KUR100);
            }
        });

        holder.picText.setVisibility(View.VISIBLE);
        holder.kur101Btn.setVisibility(View.GONE);
        holder.kur300Btn.setVisibility(View.GONE);
        holder.kur310Btn.setVisibility(View.GONE);
        holder.kur350Btn.setVisibility(View.GONE);
        holder.kur400Btn.setVisibility(View.GONE);
        holder.kur500Btn.setVisibility(View.GONE);

        holder.waBtn.setVisibility(View.VISIBLE);
    }

    protected void setup_status_kur100(MyItemHolder holder, TOrder order, final int position) {
        holder.kur101Btn.setVisibility(View.GONE);
        /*
        holder.kur101Btn.setImageResource(R.drawable.booking_order);
        holder.kur101Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemClickListener.onPickButtonClick(v, position, AppConfig.KEY_KUR101);
            }
        });*/

        holder.picText.setVisibility(View.GONE);
        holder.kur500Btn.setVisibility(View.GONE);
        holder.kur400Btn.setVisibility(View.GONE);
        holder.kur350Btn.setVisibility(View.GONE);
        holder.kur300Btn.setVisibility(View.GONE);
        holder.kur310Btn.setVisibility(View.GONE);
        holder.kur200Btn.setVisibility(View.GONE);
        holder.waBtn.setVisibility(View.GONE);
    }

    protected void setupTextAndStatus(MyItemHolder holder, TOrder order, int position) {
        holder.awbText.setText(order.getAwb());
        String nama = "";
        if(order.getBuyer() !=null && order.getBuyer().getAddress() != null){
            nama +=order.getBuyer().getFirstname() + " "+order.getBuyer().getLastname()+"\n("+order.getBuyer().getAddress().getKecamatan()+")";
        }
        holder.namaPengirimText.setText(nama);
        holder.teleponPengirimText.setText(order.getBuyer() ==null? "": order.getBuyer().getPhone());
        String orderAt = "di ";
        if(order.getDocar() != null && order.getDocar().getUser() != null) {
            orderAt += order.getDocar().getUser().getAddress().toStringKecKab();
        }else if(order.getPlace() != null && order.getPlace().getAddress() != null){
            orderAt += order.getPlace().getAddress().toStringKecKab();
        }else if(order.getPackets() != null && !order.getPackets().isEmpty()){
            List l = new ArrayList(order.getPackets());
            TPacket p = (TPacket) l.get(0);
            if(p != null && p.getOrigin() != null && p.getOrigin().getAddress() != null){
                orderAt += p.getOrigin().getAddress().toStringKecKab();
            }
        }
        holder.kotaAsalText.setText(orderAt);

        if(order.getPic() != null && !order.getPic().getFirstname().isEmpty()){
            holder.picText.setVisibility(View.VISIBLE);
            holder.picText.setText("PIC: "+order.getPic().getFirstname() +" "+order.getPic().getLastname());
        }else{
            holder.picText.setVisibility(View.GONE);
        }

        holder.statusText.setText( AppConfig.getOrderStatusText( order.getStatus() ) );
        String joined = "\n"+AppConfig.getTimeAgo(order.getCreated_date());
        holder.createdText.setText(order.getCreated_date()+joined);
    }

    protected void setupUpdateBtn(MyItemHolder holder, TOrder order, final int position) {
        if(order.getStatus().equalsIgnoreCase(AppConfig.KEY_KUR999) || order.getStatus().equalsIgnoreCase(AppConfig.KEY_KUR900) ){
            holder.updateBtn.setImageResource(R.drawable.reject_booking_icon);
        }else if(order.getStatus().equalsIgnoreCase(AppConfig.KEY_KUR500)){
            holder.updateBtn.setImageResource(R.drawable.completed_icon);
        }else{
            holder.updateBtn.setImageResource(R.drawable.detail_order);
        }
        holder.updateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemClickListener.onUpdateButtonClick(v, position);
            }
        });

        //holder.waBtn.setVisibility(View.GONE);
        holder.waBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemClickListener.onWaButtonClick(v, position);
            }
        });
        holder.searchKurirBtn.setVisibility(View.GONE);
        if(order.getStatus().equalsIgnoreCase(AppConfig.KEY_KUR100)) {
            holder.searchKurirBtn.setVisibility(View.VISIBLE);
            holder.searchKurirBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    itemClickListener.onSearchKurirButtonClick(v, position);
                }
            });
        }
        holder.testimoniBtn.setVisibility(View.GONE);
        holder.rbRating.setVisibility(View.GONE);
        if(order.getStatus().equalsIgnoreCase(AppConfig.KEY_KUR500) ){
            if(order.getRating() == 0){
                holder.rbRating.setVisibility(View.GONE);
                holder.testimoniBtn.setVisibility(View.VISIBLE);
                holder.testimoniBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        itemClickListener.onTestimoniButtonClick(v, position);
                    }
                });
            }else{
                holder.rbRating.setRating(order.getRating());
                holder.rbRating.setVisibility(View.VISIBLE);
            }
        }
    }

    private void setupServiceIcon(MyItemHolder holder, TOrder order, int position) {
        if(order.getService_type().equalsIgnoreCase(AppConfig.KEY_DOJEK)){
            holder.item_service.setImageResource(R.drawable.do_jek_icon);
        }else if(order.getService_type().equalsIgnoreCase(AppConfig.KEY_DOWASH)) {
            holder.item_service.setImageResource(R.drawable.do_wash_icon);
        }else if(order.getService_type().equalsIgnoreCase(AppConfig.KEY_DOCAR)) {
            holder.item_service.setImageResource(R.drawable.do_car_icon);
        }else if(order.getService_type().equalsIgnoreCase(AppConfig.KEY_DOSEND) ) {
            holder.item_service.setImageResource(R.drawable.do_send_icon);
        }else if(order.getService_type().equalsIgnoreCase(AppConfig.KEY_DOMOVE)) {
            holder.item_service.setImageResource(R.drawable.do_move_icon);
        }else if(order.getService_type().equalsIgnoreCase(AppConfig.KEY_DOSERVICE)) {
            holder.item_service.setImageResource(R.drawable.do_service_icon);
        }else if(order.getService_type().equalsIgnoreCase(AppConfig.KEY_DOHIJAMAH)) {
            holder.item_service.setImageResource(R.drawable.do_hijamah_icon);
        }else if(order.getService_type().equalsIgnoreCase(AppConfig.KEY_DOSHOP)) {
            holder.item_service.setImageResource(R.drawable.do_shop_icon);
        }else if(order.getService_type().equalsIgnoreCase(AppConfig.KEY_DOMART)) {
            holder.item_service.setImageResource(R.drawable.do_mart_icon);
        }else{
            holder.item_service.setImageResource(R.drawable.icon_nds);
        }

        if(order.getService_code().equalsIgnoreCase(AppConfig.PACKET_SDS)){
            holder.item_serviceType.setImageResource(R.drawable.icon_sds);
        }else if(order.getService_code().equalsIgnoreCase(AppConfig.PACKET_ENS)){
            holder.item_serviceType.setImageResource(R.drawable.icon_ens);
        }else {
            holder.item_serviceType.setImageResource(R.drawable.icon_nds);
        }
        holder.item_serviceType.setVisibility(View.VISIBLE);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public static class MyItemHolder extends RecyclerView.ViewHolder {
        protected TextView awbText;
        protected TextView namaPengirimText;
        protected TextView teleponPengirimText;
        protected TextView kotaAsalText;
        protected TextView kotaTujuanText;
        //protected TextView serviceCodeText;
        protected ImageView item_service;
        protected ImageView item_serviceType;
        protected TextView picText;
        protected TextView statusText;
        protected TextView createdText;
        protected  ImageButton updateBtn;
        protected ImageButton kur101Btn;
        protected ImageButton kur100Btn;
        protected ImageButton kur200Btn;
        protected ImageButton kur300Btn;
        protected ImageButton kur310Btn;
        protected ImageButton kur350Btn;
        protected ImageButton kur400Btn;
        protected ImageButton kur500Btn;

        protected ImageButton waBtn;
        protected ImageButton searchKurirBtn;
        protected ImageButton testimoniBtn;
        RatingBar rbRating;
        public MyItemHolder(View itemView) {
            super(itemView);
            this.awbText= (TextView) itemView.findViewById(R.id.tv_awb);
            this.namaPengirimText= (TextView) itemView.findViewById(R.id.tv_nama_pengirim);
            this.teleponPengirimText= (TextView) itemView.findViewById(R.id.tv_telepon_pengirim);
            this.kotaAsalText= (TextView) itemView.findViewById(R.id.tv_kota_asal);
            this.kotaTujuanText= (TextView) itemView.findViewById(R.id.tv_kota_tujuan);
            this.statusText= (TextView) itemView.findViewById(R.id.tv_status);
            this.createdText = (TextView) itemView.findViewById(R.id.tv_created);
            this.picText= (TextView) itemView.findViewById(R.id.tv_pic);
            //this.packetGrid = (GridLayout)itemView.findViewById(R.id.packet_grid);
            this.item_service = (ImageView) itemView.findViewById(R.id.item_service);
            this.item_serviceType = (ImageView) itemView.findViewById(R.id.item_service_type);
            //this.serviceCodeText= (TextView) itemView.findViewById(R.id.packet_service);
            this.updateBtn= (ImageButton) itemView.findViewById(R.id.updateBtn);
            this.kur101Btn= (ImageButton) itemView.findViewById(R.id.kur101Btn);
            this.kur100Btn= (ImageButton) itemView.findViewById(R.id.kur100Btn);
            this.kur200Btn= (ImageButton) itemView.findViewById(R.id.kur200Btn);
            this.kur300Btn= (ImageButton) itemView.findViewById(R.id.kur300Btn);
            this.kur310Btn= (ImageButton) itemView.findViewById(R.id.kur310Btn);
            this.kur350Btn= (ImageButton) itemView.findViewById(R.id.kur350Btn);
            this.kur400Btn= (ImageButton) itemView.findViewById(R.id.kur400Btn);
            this.kur500Btn = (ImageButton) itemView.findViewById(R.id.kur500Btn);

            this.waBtn= (ImageButton) itemView.findViewById(R.id.waBtn);
            this.searchKurirBtn = (ImageButton) itemView.findViewById(R.id.searchKurirBtn);
            this.testimoniBtn = (ImageButton) itemView.findViewById(R.id.testimoniBtn);
            this.rbRating= (RatingBar) itemView.findViewById(R.id.rbRating);
        }
    }

    public interface OnItemClickListener {
        void onPickButtonClick(View view, int position, String status);
        void onUpdateButtonClick(View view, int position);
        void onWaButtonClick(View view, int position);
        void onSearchKurirButtonClick(View view, int position);
        void onTestimoniButtonClick(View view, int position);
    }
}