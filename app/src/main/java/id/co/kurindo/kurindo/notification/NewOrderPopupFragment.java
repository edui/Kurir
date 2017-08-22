package id.co.kurindo.kurindo.notification;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import java.util.HashMap;

import butterknife.Bind;
import butterknife.OnClick;
import id.co.kurindo.kurindo.R;
import id.co.kurindo.kurindo.app.AppConfig;
import id.co.kurindo.kurindo.app.AppController;
import id.co.kurindo.kurindo.base.BaseFragment;
import id.co.kurindo.kurindo.helper.ViewHelper;
import id.co.kurindo.kurindo.model.DoMart;
import id.co.kurindo.kurindo.model.Notification;
import id.co.kurindo.kurindo.model.TOrder;
import id.co.kurindo.kurindo.model.TPacket;

/**
 * Created by Lenovo on 8/12/2017.
 */

public class NewOrderPopupFragment extends BaseFragment {
    @Bind(R.id.ivServiceType)
    ImageView ivServiceType;
    @Bind(R.id.ivServiceCode)
    ImageView ivServiceCode;

    @Bind(R.id.splash)
    ImageView splash;
    @Bind(R.id.layoutContent)
    LinearLayout layoutContent;

    @Bind(R.id.tvOrigin)
    TextView tvOrigin;
    @Bind(R.id.tvDestination)
    TextView tvDestination;
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

        Bundle bundle = getArguments();
        if(bundle != null){
            data = bundle.getParcelable("notification");
        }
        if(data == null){
            TOrder order = ViewHelper.getInstance().getOrder();
            if(order != null){
                data = new Notification();
                data.setAwb(order.getAwb());
                data.setCod(order.getCod().toString());
                if(order.getDocar() != null && order.getDocar().getUser() != null ){
                    data.setKotaPengirim(order.getDocar().getUser().toStringAddressFormatted());
                    data.setKotaPenerima(order.getDocar().getUser().toStringAddressFormatted());
                } else if(order.getService_type().equalsIgnoreCase(AppConfig.KEY_DOSEND)
                        || order.getService_type().equalsIgnoreCase(AppConfig.KEY_DOJEK)
                        || order.getService_type().equalsIgnoreCase(AppConfig.KEY_DOCAR)
                        || order.getService_type().equalsIgnoreCase(AppConfig.KEY_DOMOVE) ){
                    if(order.getPackets() != null){
                        for (TPacket p : order.getPackets()){
                            data.setKotaPengirim(p.getOrigin().toStringAddressFormatted());
                            data.setKotaPenerima(p.getDestination().toStringAddressFormatted());
                            break;
                        }
                    }
                } else if(order.getService_type().equalsIgnoreCase(AppConfig.KEY_DOMART) ){
                    if(order.getMarts() != null){
                        for(DoMart m : order.getMarts()){
                            data.setKotaPengirim(m.getOrigin().toStringAddressFormatted());
                            break;
                        }
                    }
                    data.setKotaPenerima(order.getPlace().toStringAddressFormatted());
                } else if(order.getService_type().equalsIgnoreCase(AppConfig.KEY_DOSERVICE) ||
                        order.getService_type().equalsIgnoreCase(AppConfig.KEY_DOWASH)  ||
                        order.getService_type().equalsIgnoreCase(AppConfig.KEY_DOHIJAMAH)){
                    data.setKotaPengirim(order.getPlace().toStringAddressFormatted());
                    data.setKotaPenerima(order.getPlace().toStringAddressFormatted());
                }

                data.setMessage(order.getStatusText());
                data.setTag(order.getService_type());
                data.setStatus(order.getService_code());
            }
        }

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflateAndBind(inflater, container, R.layout.activity_notif_order_search);
        fill_data();
        translate_animation();
        //animateBackground();
        return v;
    }

    private static int DEFAULT_ANIMATION_DURATION = 700;
    private void animate(){
        RotateAnimation anim = new RotateAnimation(0f, 350f, 15f, 15f);
        anim.setInterpolator(new LinearInterpolator());
        anim.setRepeatCount(Animation.INFINITE);
        anim.setDuration(DEFAULT_ANIMATION_DURATION);
        anim.setRepeatMode(Animation.REVERSE);

        splash.startAnimation(anim);

        // Later.. stop the animation
        //splash.setAnimation(null);
    }

    private void animateBackground(){
        ObjectAnimator objectAnimator = ObjectAnimator.ofObject(layoutContent, "backgroundColor",
                new ArgbEvaluator(),
                ContextCompat.getColor(AppController.applicationContext, R.color.white),
                ContextCompat.getColor(AppController.applicationContext, R.color.colorAccent));
        objectAnimator.setRepeatCount(Animation.INFINITE);
        objectAnimator.setRepeatMode(ValueAnimator.REVERSE);

        objectAnimator.setDuration(DEFAULT_ANIMATION_DURATION);
        objectAnimator.start();
    }
    private void translate_animation(){
        ValueAnimator animator = ValueAnimator.ofFloat(0, 300);
        //ValueAnimator animator = ValueAnimator.ofFloat(0, 360);

        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                //3
                float value = (float) animation.getAnimatedValue();
                //4
                splash.setTranslationX(value);
            }
        });

        animator.setInterpolator(new LinearInterpolator());
        animator.setDuration(DEFAULT_ANIMATION_DURATION);
        animator.setRepeatCount(Animation.INFINITE);
        animator.setRepeatMode(ValueAnimator.REVERSE);

        animator.start();
    }
    private void fill_data() {
        if(data != null){
            int resId = R.drawable.doclient_icon;
            if(data.getTag().equalsIgnoreCase(AppConfig.KEY_DOSEND))
                resId = R.drawable.do_send_icon;
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

    @Override
    public void onDestroy() {
        super.onDestroy();
        splash.setAnimation(null);
    }

    final Handler handler = new Handler() {
        @Override
        public void handleMessage(Message mesg) {
            throw new RuntimeException();
        }
    };

    @OnClick(R.id.btnHide)
    public void onBtnAccept(){
        getActivity().finish();
    }

    private void accept() {

        String url = AppConfig.URL_TORDER_ADDPIC;
        HashMap<String, String> params = new HashMap<>();

        params.put("awb",(data!=null?data.getAwb():""));
        params.put("user_agent", AppConfig.USER_AGENT);
        HashMap headers = new HashMap();
        addRequest("req_place_order", Request.Method.POST, url, new Response.Listener() {
            @Override
            public void onResponse(Object o) {

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {

            }
        }, params,  getKurindoHeaders());

    }

    @OnClick(R.id.btnCancel)
    public void onBtnCancel(){
        Intent intent = new Intent();
        intent.putExtra("awb", (data != null? data.getAwb() : ""));
        intent.putExtra("data", data);
        intent.setClass(AppController.applicationContext, CancelOrderActivity.class);
        startActivity(intent);
        getActivity().finish();
    }

}
