package id.co.kurindo.kurindo.wizard.dosend;

import android.app.ProgressDialog;
import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import id.co.kurindo.kurindo.R;
import id.co.kurindo.kurindo.app.AppConfig;
import id.co.kurindo.kurindo.comp.ProgressDialogCustom;
import id.co.kurindo.kurindo.map.BaseMap;
import id.co.kurindo.kurindo.model.TUser;
import id.co.kurindo.kurindo.util.LogUtil;
import id.co.kurindo.kurindo.util.ParserUtil;

/**
 * Created by DwiM on 6/10/2017.
 */

public class KurirMapFragment extends BaseMap {
    ArrayList<TUser> data = new ArrayList<>();
    Context context;
    private ProgressDialog progressBar;
    private LatLng refPoint = null;
    private String doType = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if(bundle != null){
            double lat = bundle.getDouble("lat");
            double lng = bundle.getDouble("lng");
            refPoint = new LatLng(lat,lng);
            doType = bundle.getString("do_type");
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflateAndBind(inflater, container, R.layout.fragment_maps);
        context = getContext();
        progressBar = new ProgressDialogCustom(context);
        request_list_kurir("KURIR,ADMIN");

        //request_list_admin("ADMIN");
        return v;
    }


    //run timer
    Timer t ;
    private void setup_timer() {
        t = new Timer();
        scheduled();
    }
    private void scheduled(){
        int minutes = 10;
        t.schedule(new TimerTask() {

            public void run() {
                request_list_kurir("KURIR,ADMIN");
            }
        }, 1000,minutes * 60 * 1000);
    }


    @Override
    public void reDrawMarker() {
        super.reDrawMarker();
        for (int i = 0; i < data.size(); i++) {
            TUser user = data.get(i);
            String title = user.getName();
            String snippet = user.getPhone()+" Kurir";
            mMap.addMarker(
                    new MarkerOptions()
                            .position(user.getLast() == null ? user.getAddress().getLocation() : user.getLast())
                            .title(title)
                            .snippet(snippet)
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_motorcycle)));

        }

        if(refPoint != null){
            mMap.addMarker(
                    new MarkerOptions()
                            .position(refPoint)
                            .title("Reference Point")
                            .snippet("Reference Point")
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.origin_pin)));
        }
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                marker.showInfoWindow();
                return true;
            }
        });
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        super.onConnected(bundle);
        if(refPoint != null){
            Location loc = new Location("RefPoint");
            loc.setLatitude(refPoint.latitude);
            loc.setLongitude(refPoint.longitude);
            changeMap(loc);
        }
    }

    private void request_list_kurir(String...params) {
        progressBar.show();
        final String param = params[0].toString();
        //String param2 = null;
        //if(params.length > 1) param2 = params[1].toString();
        //String URI = AppConfig.URL_LIST_USER_LASTLOCATION;
        String URI = AppConfig.URL_LIST_USER_SKILLLOCATIONBASED;
        //URI = URI.replace("{type}", param);
        final String tag_string_req = "req_list_kurir";
        HashMap<String, String > maps = new HashMap<>();
        maps.put("form-type", "json");
        maps.put("type", param);
        if(refPoint !=null) maps.put("location", refPoint.latitude+","+refPoint.longitude);
        if(doType != null) maps.put("do-type", doType);
        addRequest(tag_string_req , Request.Method.POST, URI, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                LogUtil.logD(TAG, "list KURIR Response: " + response.toString());
                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean success = jObj.getBoolean("success");
                    if (success) {
                        data.clear();
                        ParserUtil parser = new ParserUtil();
                        JSONArray datas = jObj.getJSONArray("data");
                        for (int j = 0; j < datas.length(); j++) {
                            TUser recipient = parser.parserUser(datas.getJSONObject(j));
                            /*
                            TUser recipient = gson.fromJson(datas.getString(j), TUser.class);
                            Address addr= gson.fromJson(datas.getString(j), Address.class);
                            recipient.setAddress(addr);
                            City city = gson.fromJson(datas.getString(j),City.class);
                            addr.setCity(city);
                            */
                            boolean add = true;
                            data.add(recipient);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                reDrawMarker();
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

    public static KurirMapFragment newInstance(LatLng point, String doType) {
        Bundle bundle = new Bundle();
        if(point != null){
            bundle.putDouble("lat", point.latitude);
            bundle.putDouble("lng", point.longitude);
        }
        bundle.putString("do_type", doType);
        KurirMapFragment f = new KurirMapFragment();
        f.setArguments(bundle);
        return f;
    }

}
