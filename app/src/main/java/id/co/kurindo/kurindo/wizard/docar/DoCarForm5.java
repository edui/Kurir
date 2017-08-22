package id.co.kurindo.kurindo.wizard.docar;

/**
 * Created by aspire on 3/26/2017.
 */

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.stepstone.stepper.Step;
import com.stepstone.stepper.VerificationError;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.OnClick;
import id.co.kurindo.kurindo.R;
import id.co.kurindo.kurindo.app.AppConfig;
import id.co.kurindo.kurindo.comp.ProgressDialogCustom;
import id.co.kurindo.kurindo.helper.DoCarHelper;
import id.co.kurindo.kurindo.helper.DoMartHelper;
import id.co.kurindo.kurindo.helper.ViewHelper;
import id.co.kurindo.kurindo.model.DoCarRental;
import id.co.kurindo.kurindo.model.TOrder;
import id.co.kurindo.kurindo.model.Vehicle;
import id.co.kurindo.kurindo.util.LogUtil;
import id.co.kurindo.kurindo.wizard.BaseStepFragment;

/**
 * Created by dwim on 3/15/2017.
 */

public class DoCarForm5 extends BaseStepFragment implements Step {
    private static final String TAG = "DoCarForm5";
    VerificationError invalid = null;
    @Bind(R.id.ivProductImage)
    ImageView ivProductImage;

    @Bind(R.id.tvProductName)
    TextView tvProductName;
    @Bind(R.id.rbRentalRating)
    RatingBar rbRentalRating;
    @Bind(R.id.tvRentalRating)
    TextView tvRentalRating;

    @Bind(R.id.rbRatingAc)
    RatingBar rbRatingAc;
    @Bind(R.id.tvRatingAc)
    TextView tvRatingAc;

    @Bind(R.id.rbRatingKebersihan)
    RatingBar rbRatingKebersihan;
    @Bind(R.id.tvRatingKebersihan)
    TextView tvRatingKebersihan;

    @Bind(R.id.rbRatingKondisi)
    RatingBar rbRatingKondisi;
    @Bind(R.id.tvRatingKondisi)
    TextView tvRatingKondisi;

    @Bind(R.id.tvUlasan)
    TextView tvUlasan;

    @Bind(R.id.ivSpec1)
    ImageView ivSpec1;
    @Bind(R.id.tvSpec1)
    TextView tvSpec1;
    @Bind(R.id.ivSpec2)
    ImageView ivSpec2;
    @Bind(R.id.tvSpec2)
    TextView tvSpec2;
    @Bind(R.id.ivSpec3)
    ImageView ivSpec3;
    @Bind(R.id.tvSpec3)
    TextView tvSpec3;

    @Bind(R.id.ivSpec4)
    ImageView ivSpec4;
    @Bind(R.id.tvSpec4)
    TextView tvSpec4;
    @Bind(R.id.ivSpec5)
    ImageView ivSpec5;
    @Bind(R.id.tvSpec5)
    TextView tvSpec5;
    @Bind(R.id.ivSpec6)
    ImageView ivSpec6;
    @Bind(R.id.tvSpec6)
    TextView tvSpec6;

    @Bind(R.id.tvDescription)
    TextView tvDescription;

    @Bind(R.id.ivRental1)
    ImageView ivRental1;
    @Bind(R.id.tvRental1)
    TextView tvRental1;
    @Bind(R.id.ivRental2)
    ImageView ivRental2;
    @Bind(R.id.tvRental2)
    TextView tvRental2;
    @Bind(R.id.ivRental3)
    ImageView ivRental3;
    @Bind(R.id.tvRental3)
    TextView tvRental3;
    @Bind(R.id.ivRental4)
    ImageView ivRental4;
    @Bind(R.id.tvRental4)
    TextView tvRental4;

    @Bind(R.id.tvTotalPrice)
    TextView tvTotalPrice;
    @Bind(R.id.tvIncludeBiayaDetail)
    TextView tvIncludeBiayaDetail;
    @Bind(R.id.tvExcludeBiayaDetail)
    TextView tvExcludeBiayaDetail;
    @Bind(R.id.tvRule)
    TextView tvRule;


    DoCarRental rental;
    Context context;
    ProgressDialog progressDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        rental = DoCarHelper.getInstance().getRental();
        context = getContext();
        progressDialog = new ProgressDialogCustom(context);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflateAndBind(inflater, container, R.layout.fragment_docar5);

        return v;
    }

    @Override
    public int getName() {
        return R.string.docar_form;
    }

    @Override
    public VerificationError verifyStep() {
        return null;
    }

    @Override
    public void onSelected() {
        if(rental != null){
            setup_rating();
            setup_ulasan();
            setup_spesifikasi();
            setup_pesanan();
        }
    }

    private void setup_pesanan() {
        ivRental1.setImageResource(R.drawable.destination_pin);
        tvRental1.setText(rental.getActivity());
        ivRental2.setImageResource(R.drawable.ic_event_note_black_18dp);
        tvRental2.setText(rental.displayDate(true));
        ivRental3.setImageResource(R.drawable.ic_access_time_black_18dp);
        tvRental3.setText(rental.getDurasi());
        //ivRental4.setImageResource(R.drawable.ic_access_time_black_18dp);
        tvRental4.setText(rental.getFasilitas());

        tvTotalPrice.setText(AppConfig.formatCurrency( rental == null ? 0 : rental.getCalculatePrice(context, rental.getVehicle()).doubleValue()));
        tvIncludeBiayaDetail.setText(AppConfig.getIncludeBiaya(context, rental.getFasilitas(), AppConfig.KEY_DOCAR));
        tvExcludeBiayaDetail.setText(AppConfig.getExcludeBiaya(context, rental.getFasilitas(), AppConfig.KEY_DOCAR));
        tvRule.setText(AppConfig.getRule(context, rental.getDurasi(), rental.getActivity(), AppConfig.KEY_DOCAR));
    }

    private void setup_ulasan() {

    }

    private void setup_spesifikasi() {
        Vehicle v = rental.getVehicle();
        ivSpec1.setImageResource(R.drawable.ic_event_black);
        tvSpec1.setText(v.getTahun());
        ivSpec2.setImageResource(R.drawable.ic_person_black);
        tvSpec2.setText(v.getDayamuat());
        ivSpec3.setImageResource(R.drawable.ic_autorenew_black_18dp);
        tvSpec3.setText(v.getWarna());
        ivSpec4.setImageResource(R.drawable.ic_thumb_up_black_18dp);
        tvSpec4.setText(v.getMerk() + " " + v.getModel());
        ivSpec5.setImageResource(R.drawable.gas_station);
        tvSpec5.setText(v.getBbm());
        ivSpec6.setImageResource(R.drawable.engine24);
        tvSpec6.setText(v.getTransmisi());

        tvDescription.setText(v.getDescription());
    }

    private void setup_rating() {
        Vehicle v = rental.getVehicle();
        String image = v.getImage();
        int resId = context.getResources().getIdentifier(image.substring(0, image.length()-4),"drawable",context.getPackageName());
        if(resId == 0){
            Glide.with(context).load(AppConfig.urlVehicleImage(image))
                    .thumbnail(0.5f)
                    .crossFade()
                    .placeholder(R.drawable.placeholder)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(ivProductImage);
        }else{
            ivProductImage.setImageResource(resId);
        }
        tvProductName.setText(v.getName());
        rbRentalRating.setRating(v.getRating());
        tvRentalRating.setText(""+v.getRating());
        tvRatingAc.setText(""+v.getAc());
        rbRatingAc.setRating(v.getAc());
        tvRatingKebersihan.setText(""+v.getKebersihan());
        rbRatingKebersihan.setRating(v.getKebersihan());
        tvRatingKondisi.setText(""+v.getKondisi());
        rbRatingKondisi.setRating(v.getKondisi());
    }
    @OnClick(R.id.btnPesanSekarang)
    public void btnPesanSekarang_OnClick() {
    }

    public void _btnPesanSekarang_OnClick(){
        final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message mesg) {
                throw new RuntimeException("RuntimeException");
            }
        };

        DialogInterface.OnClickListener YesClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                place_an_order(handler);
            }
        };
        showConfirmationDialog("Konfirmasi Pesanan","Konfirmasi, Data yang Anda masukkan sudah benar?\nAnda akan menggunakan layanan "+ AppConfig.KEY_DOCAR+". ", YesClickListener, null);

        // loop till a runtime exception is triggered.
        try { Looper.loop(); }
        catch(RuntimeException e2) { }
    }

    private void place_an_order(Handler handler) {
        LogUtil.logD(TAG, "place_an_order");

        progressDialog.setMessage("Sedang memproses Pesanan....");
        progressDialog.show();

        GsonBuilder builder = new GsonBuilder();
        builder.setPrettyPrinting(); builder.serializeNulls();
        Gson gson = builder.create();

        Map<String, String> params = new HashMap<>();
        params.put("user_agent", AppConfig.USER_AGENT);
        TOrder order = DoMartHelper.getInstance().getOrder();
        String orderStr = gson.toJson(order);
        LogUtil.logD(TAG, "place_an_order: "+orderStr);
        params.put("order", orderStr);
        process_order(params, handler);
    }

    private void process_order(Map<String, String> params, final Handler handler) {
        String url = AppConfig.URL_DOSEND_ORDER;
        addRequest("request_docar_order", Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                LogUtil.logD(TAG, "request_docar_order Response: " + response.toString());
                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean OK = "OK".equalsIgnoreCase(jObj.getString("status"));
                    if(OK){
                        String awb = jObj.getString("awb");
                        String status = jObj.getString("order_status");
                        String statusText = jObj.getString("order_status_text");

                        DoCarHelper.getInstance().getOrder().setAwb(awb);
                        DoCarHelper.getInstance().getOrder().setStatus(status);
                        DoCarHelper.getInstance().getOrder().setStatusText(statusText);

                        ViewHelper.getInstance().setOrder(DoCarHelper.getInstance().getOrder());

                        invalid = null;
                    }
                }catch (JSONException e){
                    e.printStackTrace();
                    invalid = new VerificationError("Json error: " + e.getMessage());
                }
                progressDialog.dismiss();
                handler.handleMessage(null);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                volleyError.printStackTrace();
                invalid = new VerificationError("NetworkError : " + volleyError.getMessage());
                progressDialog.dismiss();
                handler.handleMessage(null);
            }
        }, params, getKurindoHeaders());
    }

    @Override
    public void onError(@NonNull VerificationError error) {

    }
}