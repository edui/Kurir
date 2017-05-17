package id.co.kurindo.kurindo.map;

/**
 * Created by DwiM on 5/11/2017.
 */
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import id.co.kurindo.kurindo.app.AppConfig;
import id.co.kurindo.kurindo.app.AppController;
import id.co.kurindo.kurindo.helper.SQLiteHandler;
import id.co.kurindo.kurindo.helper.ViewHelper;
import id.co.kurindo.kurindo.model.Address;
import id.co.kurindo.kurindo.model.TUser;
import id.co.kurindo.kurindo.util.LogUtil;

public class LocationService extends Service
{
    private static final String TAG = "LocationService";

    public static final String LOCATION_CHANGED = "LOCATION_CHANGED";
    private static final int TWO_MINUTES = 1000 * 60 * 3;
    private static final int INTERVAL_MINUTES = 1000 * 60 * 1;
    private static final int INTERVAL_FLASH= 1000 * 1 * 1;
    public LocationManager locationManager;
    public MyLocationListener listener;
    public Location previousBestLocation = null;

    Intent intent;
    int counter = 0;

    @Override
    public void onCreate()
    {
        super.onCreate();
        intent = new Intent(LOCATION_CHANGED);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        LogUtil.logE("LocationService", "onStartCommand");
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        listener = new MyLocationListener();
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            super.onStartCommand(intent, flags, startId);
        }

        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, INTERVAL_MINUTES, 0, listener);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, INTERVAL_MINUTES, 0, listener);
        return super.onStartCommand(intent, flags, startId);
    }

    protected boolean isBetterLocation(Location location, Location currentBestLocation) {
        if (currentBestLocation == null) {
            // A new location is always better than no location
            return true;
        }

        // Check whether the new location fix is newer or older
        long timeDelta = location.getTime() - currentBestLocation.getTime();
        boolean isSignificantlyNewer = timeDelta > TWO_MINUTES;
        boolean isSignificantlyOlder = timeDelta < -TWO_MINUTES;
        boolean isNewer = timeDelta > 0;

        // If it's been more than two minutes since the current location, use the new location
        // because the user has likely moved
        if (isSignificantlyNewer) {
            return true;
            // If the new location is more than two minutes older, it must be worse
        } else if (isSignificantlyOlder) {
            return false;
        }

        // Check whether the new location fix is more or less accurate
        int accuracyDelta = (int) (location.getAccuracy() - currentBestLocation.getAccuracy());
        boolean isLessAccurate = accuracyDelta > 0;
        boolean isMoreAccurate = accuracyDelta < 0;
        boolean isSignificantlyLessAccurate = accuracyDelta > 200;

        // Check if the old and new location are from the same provider
        boolean isFromSameProvider = isSameProvider(location.getProvider(),
                currentBestLocation.getProvider());

        // Determine location quality using a combination of timeliness and accuracy
        if (isMoreAccurate) {
            return true;
        } else if (isNewer && !isLessAccurate) {
            return true;
        } else if (isNewer && !isSignificantlyLessAccurate && isFromSameProvider) {
            return true;
        }
        return false;
    }



    /** Checks whether two providers are the same */
    private boolean isSameProvider(String provider1, String provider2) {
        if (provider1 == null) {
            return provider2 == null;
        }
        return provider1.equals(provider2);
    }



    @Override
    public void onDestroy() {
        // handler.removeCallbacks(sendUpdatesToUI);
        super.onDestroy();
        LogUtil.logV("STOP_SERVICE", "DONE");
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        locationManager.removeUpdates(listener);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public static Thread performOnBackgroundThread(final Runnable runnable) {
        final Thread t = new Thread() {
            @Override
            public void run() {
                try {
                    runnable.run();
                } finally {

                }
            }
        };
        t.start();
        return t;
    }



    public class MyLocationListener implements LocationListener
    {

        public void onLocationChanged(final Location loc)
        {
            LogUtil.logI("**************************************", "Location changed");
            if(isBetterLocation(loc, previousBestLocation)) {
                intent.putExtra("lat", loc.getLatitude());
                intent.putExtra("lng", loc.getLongitude());
                intent.putExtra("provider", loc.getProvider());
                sendBroadcast(intent);
                LogUtil.logI("**************************************", "sendBroadcast");
                previousBestLocation = loc;
                updatePosition(loc);
                update_city(loc);
                if(counter == 0){
                    counter++;
                }
            }
        }

        public void onProviderDisabled(String provider)
        {
            Toast.makeText( getApplicationContext(), "Gps Disabled", Toast.LENGTH_SHORT ).show();
        }


        public void onProviderEnabled(String provider)
        {
            Toast.makeText( getApplicationContext(), "Gps Enabled", Toast.LENGTH_SHORT).show();
        }


        public void onStatusChanged(String provider, int status, Bundle extras)
        {
            //Toast.makeText( getApplicationContext(), "onStatusChanged "+status, Toast.LENGTH_SHORT).show();
        }

    }

    private void updatePosition(final Location loc) {
         if(loc ==null ) return;

            final SQLiteHandler db = new SQLiteHandler(getApplicationContext());
            Map<String, String> params = new HashMap<String, String>();
            params.put("form-lat", ""+loc.getLatitude());
            params.put("form-lng", ""+loc.getLongitude());

            Map<String, String> headers = new HashMap<String, String>();
            String api = db.getUserApi();
            headers.put("Api", api);
            headers.put("Authorization", api);

        String tag_string_req = "req_updatePosition_"+TAG;
            addRequest(tag_string_req, Request.Method.POST , AppConfig.URL_UPDATE_POSITION, new Response.Listener<String>() {

                @Override
                public void onResponse(String response) {
                    LogUtil.logD(TAG, "updatePosition: " + response.toString());

                    try {
                        JSONObject jObj = new JSONObject(response);
                        /*boolean error = jObj.getBoolean("error");
                        // Check for error node in json
                        if (!error) {
                        } else {
                        }*/
                    } catch (JSONException e) {
                        // JSON error
                        e.printStackTrace();
                        LogUtil.logE(TAG, "onResponse Error: " + e.getMessage());
                    }
                }
            }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    LogUtil.logE(TAG, "logToServer Error: " + error.getMessage());
                }
            }, params, headers);

    }

    private void update_city(final Location loc){
        if(AppController.getInstance().city == null){
            LatLng latLng = new LatLng(loc.getLatitude(), loc.getLongitude());
            requestAddress(latLng);
        }else if(ViewHelper.getInstance().getLastAddress() == null){
            LatLng latLng = new LatLng(loc.getLatitude(), loc.getLongitude());
            requestAddress(latLng);
        }
    }
    private void requestAddress(LatLng latLng) {
        if(latLng == null) {
            return;
        }
        String url = MapUtils.getGeocodeUrl(latLng);
        addRequest("request_geocode_address", Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //LogUtil.logD(TAG, "requestAddress Response: " + response.toString());
                List<List<HashMap<String, String>>> routes = null;
                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean OK = "OK".equalsIgnoreCase(jObj.getString("status"));
                    if(OK){
                        DataParser parser = new DataParser();
                        Address address = parser.parseAddress(jObj);
                        if(address != null) {
                            ViewHelper.getInstance().setLastAddress( address );
                            if(AppController.getInstance().city == null) {
                                update_city(address);
                            }
                        }
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                volleyError.printStackTrace();
            }
        }, null, null);
    }

    private void update_city(final Address address) {
        final SQLiteHandler db = new SQLiteHandler(getApplicationContext());
        String url = AppConfig.URL_USER_CITY_UPDATE;
        GsonBuilder builder = new GsonBuilder();
        builder.setPrettyPrinting();
        builder.serializeNulls();
        final Gson gson = builder.create();

        HashMap params= new HashMap();
        String addressJson = gson.toJson(address);
        params.put("address", addressJson);
        LogUtil.logD(TAG, addressJson);

        Map<String, String> headers = new HashMap<String, String>();
        String api = db.getUserApi();
        headers.put("Api", api);
        headers.put("Authorization", api);

        addRequest("request_update_city", Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                LogUtil.logD(TAG, "request_update_city Response: " + response.toString());
                try {
                    JSONObject jObj = new JSONObject(response);
                    String message = jObj.getString("message");
                    if("OK".equalsIgnoreCase(message)){

                        TUser tuser = gson.fromJson(jObj.getString("data"), TUser.class);
                        if(tuser.getPhone() ==null || tuser.getPhone().isEmpty()){
                            JSONObject user = jObj.getJSONObject("data");
                            String firstname = user.getString("firstname");
                            String lastname = user.getString("lastname");
                            String email = user.getString("email");
                            String created_at = user.getString("created");
                            String phone = user.getString("phone");
                            String gender = user.getString("gender");

                            tuser.setFirstname(firstname);
                            tuser.setLastname(lastname);
                            tuser.setEmail(email);
                            tuser.setPhone(phone);
                            tuser.setGender(gender);
                            tuser.setCreated_at(created_at);
                            tuser.setAddress(address);
                        }

                        //update city
                        String city = jObj.getString("city");

                        if(city == null || city.isEmpty() || city.equalsIgnoreCase("null")){

                        }else{
                            if(tuser != null){
                                db.onUpgrade(db.getWritableDatabase(), 0, 1);
                                //db.onCreateTableRecipient(db.getWritableDatabase());
                                db.onUpgradeTableRecipient(db.getWritableDatabase(),0,1);

                                db.onUpgradeUserAddress(db.getWritableDatabase(), 0, 1);

                                db.addUser(tuser);
                                //db.updateUserCityP(tuser.getPhone(), city);
                                //tuser.setAddress(origin.getAddress());
                                db.addAddress(tuser,"HOMEBASE");
                                AppController.getInstance().city = city;
                            }
                        }
                    }
                }catch (Exception e){e.printStackTrace();}
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                volleyError.printStackTrace();
            }
        }, params, headers);
    }
    public void addRequest(final String tag_string_req, int method, String url, Response.Listener responseListener, Response.ErrorListener errorListener, final Map<String, String> params, final Map<String, String> headers){
        final StringRequest strReq = new StringRequest(method,url, responseListener, errorListener){
            protected Map<String, String> getParams() throws AuthFailureError {
                if(params == null) return super.getParams();
                return params;
            }
            public Map<String, String> getHeaders() throws AuthFailureError{
                if(headers == null) return super.getHeaders();
                return headers;
            }
        };
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }


}