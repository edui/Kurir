package id.co.kurindo.kurindo.wizard.dojek;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.RadioGroup;

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

import java.util.ArrayList;
import java.util.HashMap;

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

public class DoJekPinLocationMapFragment extends BaseLocationMapFragment{
    @Override
    public int getLayout() {
        return R.layout.fragment_maps_dojek;
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
        if(act instanceof DoJekOrderActivity){
            ((DoJekOrderActivity) act).doType = doType;
            ((DoJekOrderActivity) act).getStepperAdapter();
        }

    }

    @Override
    public VerificationError verifyStep() {

        if(!inDoJekCoverageArea){
            return new VerificationError("Jarak terlalu jauh untuk "+AppConfig.KEY_DOJEK+". Gunakan "+AppConfig.KEY_DOCAR+" sebagai alternatif.");
        }
        if(!inDoCarCoverageArea){
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

        if( doType.equalsIgnoreCase(AppConfig.KEY_DOJEK)) {
            //TODO : check location based service / kedua rute masih dalam 1 kabupaten (dojek, dosend) atau propinsi (domove, docar) dan exception propinsi kecil2

            DoSendHelper.getInstance().addDoJekOrder(payment.getText(), serviceCode, (route == null || route.getDistance() == null ? "" : route.getDistance().getValue()), price);
        }else{
            //Toast.makeText(getContext(), "Not Available", LENGTH_SHORT).show();
            DoSendHelper.getInstance().addDoCarOrder(payment.getText(), serviceCode, (route == null || route.getDistance() == null ? "" : route.getDistance().getValue()), price);
        }
        return null;

    }

    @Override
    protected void updateUI() {
        radioDoSend.setVisibility(View.GONE);
        radioDoMove.setVisibility(View.GONE);
        radioDoJek.setVisibility(View.VISIBLE);
        radioDoCar.setVisibility(View.VISIBLE);

        if(DoSendHelper.getInstance().getDoType() != null){
            this.doType = DoSendHelper.getInstance().getDoType();
        }
        switch (this.doType){
            case AppConfig.KEY_DOJEK:
                rgDoType.check(R.id.radio_dojek);
                break;
            case AppConfig.KEY_DOCAR:
                rgDoType.check(R.id.radio_docar);
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

    static DoJekPinLocationMapFragment instance;
    public static Fragment newInstance(String keyDo) {
        if (instance == null || !instance.doType.equalsIgnoreCase(keyDo)) {
            Bundle bundle = new Bundle();
            bundle.putString("doType", keyDo);
            instance = new DoJekPinLocationMapFragment();
            instance.setArguments(bundle);
        }
        Bundle bundle = instance.getArguments();
        bundle.putString("doType", keyDo);
        instance.setDoType(keyDo);
        return instance;
    }

    public static DoJekPinLocationMapFragment getInstance() {
        return instance;
    }
}
