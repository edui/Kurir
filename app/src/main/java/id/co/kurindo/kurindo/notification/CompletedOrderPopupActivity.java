package id.co.kurindo.kurindo.notification;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONObject;

import java.util.HashMap;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import id.co.kurindo.kurindo.R;
import id.co.kurindo.kurindo.app.AppConfig;
import id.co.kurindo.kurindo.app.AppController;
import id.co.kurindo.kurindo.base.BaseActivity;
import id.co.kurindo.kurindo.helper.SQLiteHandler;
import id.co.kurindo.kurindo.helper.ViewHelper;
import id.co.kurindo.kurindo.model.DoMart;
import id.co.kurindo.kurindo.model.Notification;
import id.co.kurindo.kurindo.model.TOrder;
import id.co.kurindo.kurindo.model.TPacket;
import id.co.kurindo.kurindo.util.LogUtil;

import static id.co.kurindo.kurindo.util.LogUtil.makeLogTag;

/**
 * Created by dwim on 8/5/2017.
 */

public class CompletedOrderPopupActivity extends BaseActivity {
    private static final String TAG = makeLogTag(CompletedOrderPopupActivity.class);

    @Bind(R.id.ivServiceType)
    ImageView ivServiceType;
    @Bind(R.id.ivServiceCode)
    ImageView ivServiceCode;

    @Bind(R.id.tvTitle)
    TextView tvTitle;
    @Bind(R.id.tvOrigin)
    TextView tvOrigin;
    @Bind(R.id.tvDestination)
    TextView tvDestination;
    @Bind(R.id.tvRemark)
    TextView tvRemark;
    @Bind(R.id.tvPrice)
    TextView tvPrice;
    @Bind(R.id.tvCod)
    TextView tvCod;

    @Bind(R.id.btnDismis)
    Button btnDismis;

    @Bind(R.id.btnAccept)
    Button btnAccept;
    @Bind(R.id.rbRatingKurir)
    RatingBar rbRatingKurir;
    @Bind(R.id.etTestimoni)
    EditText etTestimoni;

    Notification data;

    final Handler handler = new Handler() {
        @Override
        public void handleMessage(Message mesg) {
            throw new RuntimeException();
        }
    };
    @Override
    public boolean providesActivityToolbar() {
        return false;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        int flags = WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_FULLSCREEN;

        getWindow().addFlags(flags);
        AppConfig.taskID = getTaskId();

        setContentView(R.layout.activity_notif_order_completed);
        ButterKnife.bind(this);

        Bundle bundle = getIntent().getExtras();
        if(bundle != null){
            data = bundle.getParcelable("notification");
        }
        setup_rating();
        fill_order();
        fill_data();
    }

    private void setup_rating() {
        rbRatingKurir.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                if(rating >= 4.5) etTestimoni.setHint("Terima kasih. Tuliskan testimoni kepuasan anda.");
                else if(rating >= 3 && rating < 4.5) etTestimoni.setHint("Terima kasih. Berikan kami saran untuk perbaikan layanan.");
                else etTestimoni.setHint("Terima kasih. Kritik dan saran membangun anda sangat kami harapkan.");
            }
        });

    }

    private void fill_order() {
        if(data == null) {
            TOrder order = ViewHelper.getInstance().getOrder();
            if(order != null){
                data = new Notification();
                data.setAwb(order.getAwb());
                data.setCod(order.getCod().toString());
                data.setMessage(order.getStatusText());
                data.setTag(order.getService_type());
                data.setStatus(order.getService_code());
                data.setPrice(order.getTotalPrice().toString());
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
            }
        }
    }

    private void fill_data() {
        if(data != null){
            int resId = R.drawable.doclient_icon;
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

            String text = "Order Completed\n"+data.getAwb();
            tvTitle.setText(text);

            text = "";
            if(data.getKotaPengirim() != null && !data.getKotaPengirim().isEmpty()) text = "dari : "+data.getKotaPengirim();
            tvOrigin.setText(text);

            text = "";
            if(data.getKotaPenerima() != null && !data.getKotaPenerima().isEmpty()) text = "ke : "+data.getKotaPenerima();
            tvDestination.setText(text);

            text = "";
            if(data.getPrice() != null && !data.getPrice().isEmpty()) text = "Biaya : "+AppConfig.formatCurrency( new Double(data.getPrice()) );
            tvPrice.setText(text);

            text = "";
            if(data.getCod() != null && !data.getCod().isEmpty()) text = "COD : "+AppConfig.formatCurrency( new Double(data.getCod()) );
            tvCod.setText(text);

            text = "";
            if(data.getMessage() != null && !data.getMessage().isEmpty()) text = "ke : "+data.getMessage();
            tvRemark.setText(text);
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        AppConfig.taskID = 0;
    }

    @OnClick(R.id.btnAccept)
    public void onBtnAccept(){
        boolean valid = true;
        if(rbRatingKurir.getRating() == 0) {
            Toast.makeText(AppController.applicationContext, "Berikan penilaian anda terhadap layanan kami.", Toast.LENGTH_SHORT).show();
            valid = false;
        }
        if(etTestimoni.getText() == null || etTestimoni.getText().toString().isEmpty()){
            etTestimoni.setError("Tuliskan Testimoni anda.");
            valid = false;
        }else{
            etTestimoni.setError(null);
        }

        if(valid){
            SQLiteHandler db = new SQLiteHandler(AppController.applicationContext);
            String api = db.getUserApi();
            HashMap<String,String> params= new HashMap<>();
            params.put("awb", data.getAwb());
            params.put("rating", ""+rbRatingKurir.getRating());
            params.put("note", etTestimoni.getText().toString());
            HashMap<String,String> headers = new HashMap<>();
            headers.put("Authorization", api);
            headers.put("Api", api);
            addRequest("req_completed_order", Request.Method.POST, AppConfig.URL_DO_TESTIMONI, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        JSONObject jObj = new JSONObject(response);
                        String message = jObj.getString("message");
                        Toast.makeText(AppController.applicationContext, ""+message, Toast.LENGTH_SHORT).show();
                        if(message.equalsIgnoreCase("OK")) finish();
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    handler.handleMessage(null);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError volleyError) {
                    LogUtil.logD(TAG, "onErrorResponse: "+volleyError.getMessage());
                    handler.handleMessage(null);
                }
            }, params, headers);

            try { Looper.loop(); }
            catch(RuntimeException e2) {}
        }
    }

    @OnClick(R.id.btnDismis)
    public void onBtnDismis(){
        finish();
    }

}
