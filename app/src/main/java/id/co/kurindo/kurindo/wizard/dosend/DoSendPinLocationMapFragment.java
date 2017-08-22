package id.co.kurindo.kurindo.wizard.dosend;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.AppCompatButton;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.stepstone.stepper.VerificationError;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;

import butterknife.Bind;
import butterknife.OnClick;
import id.co.kurindo.kurindo.R;
import id.co.kurindo.kurindo.app.AppConfig;
import id.co.kurindo.kurindo.helper.DoSendHelper;
import id.co.kurindo.kurindo.map.DataParser;
import id.co.kurindo.kurindo.model.TUser;
import id.co.kurindo.kurindo.util.LogUtil;
import id.co.kurindo.kurindo.util.ParserUtil;
import id.co.kurindo.kurindo.wizard.BaseLocationMapFragment;

/**
 * Created by dwim on 2/14/2017.
 */

public class DoSendPinLocationMapFragment extends BaseLocationMapFragment {


    @Bind(R.id.input_berat_barang) protected TextView _beratBarangText;
    @Bind(R.id.incrementBtn)
    protected AppCompatButton incrementBtn;
    @Bind(R.id.decrementBtn) protected AppCompatButton decrementBtn;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    public int getLayout() {
        return R.layout.fragment_maps_dosend;
    }

    @Override
    protected void afterOnCreateView() {
        super.afterOnCreateView();
        updateUI();
    }

    @Override
    protected void rgDoType_onCheckedChanged(RadioGroup group, int checkedId) {
        super.rgDoType_onCheckedChanged(group, checkedId);
        FragmentActivity act = getActivity();
        if(act instanceof DoSendOrderActivity){
            ((DoSendOrderActivity) act).doType = doType;
            ((DoSendOrderActivity) act).getStepperAdapter();
        }
    }

    @OnClick(R.id.incrementBtn)
    public void onClick_incrementBtn(){
        //float q = 0;
        //try{ q = Float.parseFloat(_beratBarangText.getText().toString());}catch (Exception e){};
        //q +=0.1;
        BigDecimal b = new BigDecimal(_beratBarangText.getText().toString());
        b = b.add(new BigDecimal(0.1));
        _beratBarangText.setText(""+b.floatValue());
        beratKiriman = b.floatValue();

        //if(q % 0.5 == 0){checkTarif();}
    }
    @OnClick(R.id.decrementBtn)
    public void onClick_decrementBtn(){
        //float q = 1;
        //try{ q = Float.parseFloat(_beratBarangText.getText().toString());}catch (Exception e){};
        //q -= 0.1;
        //if(q < 1) q = 1;

        BigDecimal b = new BigDecimal(_beratBarangText.getText().toString());
        b = b.subtract(new BigDecimal(0.1));
        if(b.floatValue() < 1) b = new BigDecimal(1);
        _beratBarangText.setText(""+b.floatValue());
        beratKiriman = b.floatValue();

        //if(q % 0.5 == 0 && q > 1){checkTarif();        }
    }
    private void setup_berat_barang() {
        beratKiriman = 1;
        _beratBarangText.setText(""+beratKiriman);
        _beratBarangText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            Handler handler = new Handler(Looper.getMainLooper());
            Runnable runnable;
            @Override
            public void afterTextChanged(final Editable v) {
                handler.removeCallbacks(runnable);
                runnable =new Runnable() {
                    @Override
                    public void run() {
                        try {
                            beratKiriman = Float.parseFloat(v.toString());
                        } catch (Exception e) {
                        }

                        if(beratKiriman >= AppConfig.MIN_WEIGHT_DOSEND) {
                            requestprice();
                        }
                    }
                };
                handler.postDelayed(runnable, 800);
            }
        });
    }

    public VerificationError verifyStep() {
        if(!inDoSendCoverageArea){
            return new VerificationError("Jarak terlalu jauh untuk DOSEND. Gunakan DOMOVE sebagai alternatif.");
        }
        if(!inDoMoveCoverageArea){
            return new VerificationError("Jarak terlalu jauh. Tidak ada layanan.");
        }
        if(route ==null || route.getDistance() == null || route.getDistance().getText().isEmpty() || route.getDistance().getText().equalsIgnoreCase("null") ){
            return new VerificationError("Rute dan Jarak tidak diketahui. Silahkan dicoba lagi.");
        }

        String onotes = etOriginNotes.getText().toString();
        String dnotes = etDestinationNotes.getText().toString();
        origin.getAddress().setNotes(onotes);
        destination.getAddress().setNotes(dnotes);
        if (!canDrawRoute()) {
            return new VerificationError("Pilih rute lokasi anda.");
        }


        DoSendHelper.getInstance().setPacketRoute(origin, destination);
        if( doType.equalsIgnoreCase(AppConfig.KEY_DOSEND)) {
            //TODO : check location based service / kedua rute masih dalam 1 kabupaten (dojek, dosend) atau propinsi (domove, docar) dan exception propinsi kecil2

            DoSendHelper.getInstance().addDoSendOrder(payment.getText(), serviceCode, (route == null || route.getDistance() == null ? "" : route.getDistance().getValue()), price, beratKiriman);
            //showActivity( DoSendOrderActivity.class );
            //finish();
        }else{
            //Toast.makeText(getContext(), "Not Available", LENGTH_SHORT).show();
            DoSendHelper.getInstance().addDoMoveOrder(payment.getText(), serviceCode, (route == null || route.getDistance() == null ? "" : route.getDistance().getValue()), price, beratKiriman);
            //return new VerificationError(doType+" Not Available");
        }
        return null;
    }

    protected void updateUI() {
        setup_berat_barang();

        radioDoSend.setVisibility(View.VISIBLE);
        radioDoMove.setVisibility(View.VISIBLE);
        radioDoJek.setVisibility(View.GONE);
        radioDoCar.setVisibility(View.GONE);

        if(DoSendHelper.getInstance().getDoType() != null){
            this.doType = DoSendHelper.getInstance().getDoType();
        }
        switch (this.doType){
            case AppConfig.KEY_DOSEND:
                rgDoType.check(R.id.radio_dosend);
                break;
            case AppConfig.KEY_DOMOVE:
                rgDoType.check(R.id.radio_domove);
                break;
        }
    }

    @Override
    public void requestAddress(LatLng latLng) {
        request_list_kurir("KURIR,ADMIN", latLng);
        super.requestAddress(latLng);
    }

    protected ArrayList<TUser> kurir = new ArrayList<>();
    private void request_list_kurir(String param, LatLng latLng) {
        String location = (latLng == null? origin.getAddress().getLocationStr() : latLng.latitude+","+latLng.longitude);
        String URI = AppConfig.URL_LIST_USER_SKILLLOCATIONBASED;
        //URI = URI.replace("{type}", param);
        final String tag_string_req = "req_list_kurir";
        HashMap<String, String > maps = new HashMap<>();
        maps.put("form-type", "json");
        maps.put("type", param);
        maps.put("location", location);
        maps.put("do-type", doType);
        addRequest(tag_string_req , Request.Method.POST, URI, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                LogUtil.logD(TAG, "list KURIR Response: " + response.toString());
                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean success = jObj.getBoolean("success");
                    if (success) {
                        kurir.clear();
                        ParserUtil parser = new ParserUtil();
                        JSONArray datas = jObj.getJSONArray("data");
                        for (int j = 0; j < datas.length(); j++) {
                            TUser recipient = parser.parserUser(datas.getJSONObject(j));
                            kurir.add(recipient);
                        }
                        reDrawMarker();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                progressBar.dismiss();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                LogUtil.logD(tag_string_req, ""+volleyError.getMessage());
                progressBar.dismiss();
            }
        }, maps, getKurindoHeaders());

    }

    @Override
    public void reDrawMarker() {
        super.reDrawMarker();
        for (int i = 0; i < kurir.size(); i++) {
            TUser user = kurir.get(i);
            //String title = user.getName();
            //String snippet = user.getPhone()+ (AppConfig.isAdmin(user.getRole())? "Admin" : (AppConfig.isKurir(user.getRole())? " Kurir" : "Unknown"));
            mMap.addMarker(
                    new MarkerOptions()
                            .position(user.getLast() == null ? user.getAddress().getLocation() : user.getLast())
                            //.title(title)
                            //.snippet(snippet)
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_motorcycle)));
        }

        if(route != null){
            DataParser parser = new DataParser();
            PolylineOptions lineOptions = parser.drawRoutes(route.getRoutes());
            // Drawing polyline in the Google Map for the i-th route
            if(lineOptions != null) {
                mMap.addPolyline(lineOptions);
            }
        }
    }

    static DoSendPinLocationMapFragment instance ;
    protected static Fragment newInstance(String keyDo) {
        Bundle bundle = new Bundle();
        if (instance == null || !instance.doType.equalsIgnoreCase(keyDo)) {
            instance = new DoSendPinLocationMapFragment();
            instance.setArguments(bundle);
        }
        bundle = instance.getArguments();
        bundle.putString("doType", keyDo);
        instance.setDoType(keyDo);
        return instance;
    }

    protected static Fragment getInstance() {
        return instance;
    }

}


