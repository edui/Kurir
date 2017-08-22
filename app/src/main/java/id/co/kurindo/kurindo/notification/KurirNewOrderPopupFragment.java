package id.co.kurindo.kurindo.notification;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import java.math.BigDecimal;
import java.util.HashMap;

import butterknife.Bind;
import butterknife.OnClick;
import id.co.kurindo.kurindo.R;
import id.co.kurindo.kurindo.TOrderShowActivity;
import id.co.kurindo.kurindo.app.AppConfig;
import id.co.kurindo.kurindo.app.AppController;
import id.co.kurindo.kurindo.base.BaseFragment;
import id.co.kurindo.kurindo.comp.ProgressDialogCustom;
import id.co.kurindo.kurindo.model.Notification;

/**
 * Created by Lenovo on 8/12/2017.
 */

public class KurirNewOrderPopupFragment extends BaseFragment {
    @Bind(R.id.ivServiceType)
    ImageView ivServiceType;
    @Bind(R.id.ivServiceCode)
    ImageView ivServiceCode;

    @Bind(R.id.tvPickupLabel)
    TextView tvPickupLabel;
    @Bind(R.id.tvPickupTime)
    TextView tvPickupTime;
    @Bind(R.id.tvOrigin)
    TextView tvOrigin;
    @Bind(R.id.tvDestination)
    TextView tvDestination;
    @Bind(R.id.tvPrice)
    TextView tvPrice;
    @Bind(R.id.tvCod)
    TextView tvCod;
    @Bind(R.id.tvRemark)
    TextView tvRemark;

    @Bind(R.id.btnCancel)
    Button btnCancel;

    @Bind(R.id.btnHide)
    Button btnHide;

    Notification data;
    ProgressDialog progressDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        progressDialog = new ProgressDialogCustom(AppController.applicationContext);

        Bundle bundle = getArguments();
        if(bundle != null){
            data = bundle.getParcelable("notification");
        }

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflateAndBind(inflater, container, R.layout.activity_notif_order_new);

        fill_data();

        return v;
    }

    private void fill_data() {
        if(data != null){
            int resId = R.drawable.doclient_icon;
            if(data.getTag().equalsIgnoreCase(AppConfig.KEY_DOSEND)) resId = R.drawable.do_send_icon;
            if(data.getTag().equalsIgnoreCase(AppConfig.KEY_DOSEND)) resId = R.drawable.do_send_icon;
            if(data.getTag().equalsIgnoreCase(AppConfig.KEY_DOJEK)) resId = R.drawable.do_jek_icon;
            if(data.getTag().equalsIgnoreCase(AppConfig.KEY_DOCAR)) resId = R.drawable.do_car_icon;
            if(data.getTag().equalsIgnoreCase(AppConfig.KEY_DOMOVE)) resId = R.drawable.do_move_icon;
            if(data.getTag().equalsIgnoreCase(AppConfig.KEY_DOMART)) resId = R.drawable.do_mart_icon;
            if(data.getTag().equalsIgnoreCase(AppConfig.KEY_DOSHOP)) resId = R.drawable.do_shop_icon;
            if(data.getTag().equalsIgnoreCase(AppConfig.KEY_DOWASH)) resId = R.drawable.do_wash_icon;
            if(data.getTag().equalsIgnoreCase(AppConfig.KEY_DOSERVICE)) resId = R.drawable.do_service_icon;
            ivServiceType.setImageResource(resId);

            resId = R.drawable.icon_nds;
            if(data.getStatus().equalsIgnoreCase(AppConfig.PACKET_SDS)) resId = R.drawable.icon_sds;
            if(data.getStatus().equalsIgnoreCase(AppConfig.PACKET_ENS)) resId = R.drawable.icon_ens;
            if(data.getStatus().equalsIgnoreCase(AppConfig.PACKET_NNS)) resId = R.drawable.icon_nns;
            ivServiceCode.setImageResource(resId);

            BigDecimal price = new BigDecimal((data.getPrice() != null && !data.getPrice().isEmpty()? data.getPrice() : "0"));
            tvPrice.setText("Price : "+AppConfig.formatCurrency(price.doubleValue()));
            BigDecimal cod = new BigDecimal((data.getCod() != null && !data.getCod().isEmpty()? data.getCod() : "0"));
            tvCod.setText("COD : "+AppConfig.formatCurrency(cod.doubleValue()));

            String text = "";
            if(data.getKotaPengirim() != null && !data.getKotaPengirim().isEmpty()) text = "dari : "+data.getKotaPengirim();
            tvOrigin.setText(text);

            text = "";
            if(data.getKotaPenerima() != null && !data.getKotaPenerima().isEmpty()) text = "ke : "+data.getKotaPenerima();
            tvDestination.setText(text);

            text = "";
            if(data.getMessage() != null && !data.getMessage().isEmpty()) text = "ke : "+data.getMessage();
            tvRemark.setText(text);

        }
    }

    final Handler handler = new Handler() {
        @Override
        public void handleMessage(Message mesg) {
            throw new RuntimeException();
        }
    };

    @OnClick(R.id.btnAccept)
    public void onBtnAccept(){

        accept();

        try { Looper.loop(); }
        catch(RuntimeException e2) {}

        Intent intent = new Intent(AppController.applicationContext, TOrderShowActivity.class);
        startActivity(intent);
        getActivity().finish();
    }

    private void accept() {
        progressDialog.show();
        String url = AppConfig.URL_TORDER_ADDPIC;
        HashMap<String, String> params = new HashMap<>();

        params.put("awb",(data!=null?data.getAwb():""));
        params.put("user_agent", AppConfig.USER_AGENT);
        HashMap headers = new HashMap();
        addRequest("req_place_order", Request.Method.POST, url, new Response.Listener() {
            @Override
            public void onResponse(Object o) {

               progressDialog.dismiss();
               handler.handleMessage(null);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                progressDialog.dismiss();
                handler.handleMessage(null);
            }
        }, params,  getKurindoHeaders());

    }

    @OnClick(R.id.btnCancel)
    public void onBtnCancel(){
        Intent intent = new Intent();
        intent.putExtra("awb", (data != null? data.getAwb() : ""));
        intent.setClass(AppController.applicationContext, CancelOrderActivity.class);
        startActivity(intent);
        getActivity().finish();
    }

}
