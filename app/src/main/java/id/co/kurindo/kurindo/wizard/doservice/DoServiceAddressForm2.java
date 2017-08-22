package id.co.kurindo.kurindo.wizard.doservice;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.stepstone.stepper.VerificationError;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import id.co.kurindo.kurindo.R;
import id.co.kurindo.kurindo.app.AppConfig;
import id.co.kurindo.kurindo.helper.DoSendHelper;
import id.co.kurindo.kurindo.helper.DoServiceHelper;
import id.co.kurindo.kurindo.helper.ViewHelper;
import id.co.kurindo.kurindo.map.SinglePinLocationMapFragment;
import id.co.kurindo.kurindo.model.Address;
import id.co.kurindo.kurindo.model.DoService;
import id.co.kurindo.kurindo.model.TOrder;
import id.co.kurindo.kurindo.model.TUser;
import id.co.kurindo.kurindo.util.LogUtil;

import static id.co.kurindo.kurindo.R.style.CustomDialog;

/**
 * Created by dwim on 3/15/2017.
 */

public class DoServiceAddressForm2 extends SinglePinLocationMapFragment {
    private static final String TAG = "DoServiceAddressForm2";
    VerificationError invalid = null;

    @Bind(R.id.tvAlamat)
    TextView tvAlamat;
    @Bind(R.id.tvKecamatan)
    TextView tvKecamatan;
    @Bind(R.id.tvKabupaten)
    TextView tvKabupaten;
    @Bind(R.id.tvPropinsi)
    TextView tvPropinsi;
    @Bind(R.id.tvNegara)
    TextView tvNegara;

    @Bind(R.id.ivPriceInfo)
    TextView tvPriceInfo;

    @Bind(R.id.tvLayanan)
    TextView tvLayanan;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    protected int getLayout() {
        return R.layout.fragment_maps_doservice;
    }

    public void onClick_mLocationMarkerText(){
        super.onClick_mLocationMarkerText();
        displayAddressText();
        hidepanel(false);
        orderLayout.setVisibility(View.GONE);
    }

    @Override
    protected void hidepanel(boolean hide) {
        super.hidepanel(hide);
        //agrement_layout.setVisibility((hide ? View.GONE : View.VISIBLE));
    }

    @Override
    protected void showAddressLayout() {
        super.showAddressLayout();
        hidepanel(false);
    }

    private void displayAddressText() {
        if(origin != null && origin.getAddress() != null){
            Address addr = origin.getAddress();
            tvAlamat.setText(addr.getAlamat());
            tvKecamatan.setText(addr.getKecamatan());
            tvKabupaten.setText(addr.getKabupaten());
            tvPropinsi.setText(addr.getPropinsi());
            tvNegara.setText(addr.getNegara());
        }
    }

    @Override
    public int getName() {
        return R.string.doservice_form1;
    }

    @Override
    public VerificationError verifyStep() {
        if(originMode) return new VerificationError("Set Lokasi Anda");
        if(origin ==null || origin.getAddress() == null || origin.getAddress().getLocation() == null) return new VerificationError("Set Lokasi Anda");

        DoServiceHelper.getInstance().addOrder(new BigDecimal(0), AppConfig.KEY_DOWASH);
        TOrder order = DoServiceHelper.getInstance().getOrder();
        order.setPlace(origin);

        return invalid;
    }

    private void place_an_order(Handler handler) {

        progressBar.setMessage("Sedang memproses Pesanan....");
        progressBar.show();

        TOrder order = DoServiceHelper.getInstance().getOrder();
        order.setPlace(origin);

        GsonBuilder builder = new GsonBuilder();
        builder.setPrettyPrinting(); builder.serializeNulls();
        Gson gson = builder.create();

        Map<String, String> params = new HashMap<>();
        params.put("user_agent", AppConfig.USER_AGENT);

        String orderStr = gson.toJson(DoServiceHelper.getInstance().getOrder());
        LogUtil.logD(TAG, "place_an_order: "+orderStr);
        params.put("order", orderStr);
        process_order(params, handler);
    }


    private void process_order(Map<String, String> params, final Handler handler) {
        String url = AppConfig.URL_DOSEND_ORDER;
        addRequest("request_service_order", Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                LogUtil.logD(TAG, "request_service_order Response: " + response.toString());
                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean OK = "OK".equalsIgnoreCase(jObj.getString("status"));
                    if(OK){
                        String awb = jObj.getString("awb");
                        String status = jObj.getString("order_status");
                        String statusText = jObj.getString("order_status_text");

                        DoServiceHelper.getInstance().getOrder().setAwb(awb);
                        DoServiceHelper.getInstance().getOrder().setStatus(status);
                        DoServiceHelper.getInstance().getOrder().setStatusText(statusText);

                        ViewHelper.getInstance().setOrder(DoServiceHelper.getInstance().getOrder());

                        invalid = null;
                    }
                }catch (JSONException e){
                    e.printStackTrace();
                    invalid = new VerificationError("Json error: " + e.getMessage());
                }
                progressBar.dismiss();
                handler.handleMessage(null);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                volleyError.printStackTrace();
                invalid = new VerificationError("NetworkError : " + volleyError.getMessage());
                progressBar.dismiss();
                handler.handleMessage(null);
            }
        }, params, getKurindoHeaders());
    }

    @Override
    public void onSelected() {
        //updateUI();
    }

    @Override
    protected void updateUI() {
        super.updateUI();
        /*
        ivAgrement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopupWindow("Kurindo \nService Agreement", R.raw.snk_file, R.drawable.icon_syarat_ketentuan);
            }
        });
        */
        TOrder order = DoServiceHelper.getInstance().getOrder();
        String price = AppConfig.KEY_DOSERVICE +" ( "+order.getTotalQuantity()+" unit ) : "+AppConfig.formatCurrency( order.getTotalPrice().doubleValue() );
        tvPriceInfo.setText(price);

        StringBuilder layanan = new StringBuilder();
        Object[] datas  = order.getServices().toArray();
        for (int i = 0; i < datas.length; i++) {
            DoService data = (DoService) datas[i];
            layanan.append((i+1)+". ");
            layanan.append(data.toString());
            layanan.append("\n");
        }

        tvLayanan.setText(layanan);
    }

    @Override
    public void onError(@NonNull VerificationError error) {

    }
}
