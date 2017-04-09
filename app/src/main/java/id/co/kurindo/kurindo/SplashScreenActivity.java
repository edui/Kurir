package id.co.kurindo.kurindo;

import android.app.Application;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import id.co.kurindo.kurindo.app.AppConfig;
import id.co.kurindo.kurindo.app.AppController;
import id.co.kurindo.kurindo.helper.SQLiteHandler;
import id.co.kurindo.kurindo.helper.ViewHelper;
import id.co.kurindo.kurindo.map.DataParser;
import id.co.kurindo.kurindo.map.MapUtils;
import id.co.kurindo.kurindo.model.Address;
import id.co.kurindo.kurindo.model.News;
import id.co.kurindo.kurindo.model.TUser;
import id.co.kurindo.kurindo.task.ListenableAsyncTask;
import id.co.kurindo.kurindo.task.LoadNewsTask;
import id.co.kurindo.kurindo.task.RequestAddressTask;

/**
 * Created by DwiM on 11/16/2016.
 */

public class SplashScreenActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {
    private static final String TAG = "SplashScreenActivity";

    boolean done = false;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        buildGoogleApiClient();
        //onStart();
        Thread timer = new Thread() {
            public void run(){
                while(!done){
                    try {
                        sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                update_token();
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                //Intent intent = new Intent(getApplicationContext(), MainDrawerActivity.class);
                startActivity(intent);
                finish();
            }
        };
        ListenableAsyncTask loadNewsTask = (ListenableAsyncTask) new LoadNewsTask(this);
        loadNewsTask.listenWith(new ListenableAsyncTask.AsyncTaskListener() {
            @Override
            public void onPostExecute(Object o) {
                List<News> newsList = (List<News>)o;
                Log.i("loadNewsTask","newsList size:"+newsList.size());
                if(newsList != null && newsList.size() > 0){
                    AppController.getInstance().banners = newsList;
                }
                done = true;
            }
        });
        timer.start();
        loadNewsTask.execute("promo");
    }

    private void update_token() {
        if(AppConfig.FCM_TOKEN != null){
            String tag_string_req = "req_sendRegistrationToServer";

            final SQLiteHandler db = new SQLiteHandler(getApplicationContext());
            String api = db.getUserApi();

            Map<String, String> headers= new HashMap<String, String>();
            headers.put("Api", api);

            Map<String, String> params = new HashMap<String, String>();
            params.put("form-token", AppConfig.FCM_TOKEN);
            addRequest(tag_string_req, Request.Method.POST, AppConfig.URL_REGISTER_FCM, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Log.d(TAG, "update_token Response: " + response.toString());

                    try {
                        JSONObject jObj = new JSONObject(response);

                        String message= jObj.getString("message");
                        Log.i(TAG, "message : "+ message);
                        AppConfig.FCM_TOKEN = null;

                    } catch (JSONException e) {
                        // JSON error
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e(TAG, "update_token Error: " + error.getMessage());
                }
            }, params, headers);

        }
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

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.d(TAG, "onConnected(@Nullable Bundle bundle)");

        if (ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);

            ActivityCompat.requestPermissions(this,
                    new String[]{ android.Manifest.permission.ACCESS_COARSE_LOCATION},
                    MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION);

            return;
        }
        Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (mLastLocation != null) {
            Log.d(TAG, "ON connected ");
            //requestAddress(new LatLng( mLastLocation.getLatitude(), mLastLocation.getLongitude()));
            String url = MapUtils.getGeocodeUrl(new LatLng( mLastLocation.getLatitude(), mLastLocation.getLongitude()));
            RequestAddressTask addr = new RequestAddressTask(this);
            addr.listenWith(new ListenableAsyncTask.AsyncTaskListener<List>() {
                @Override
                public void onPostExecute(List list) {
                    done = true;
                }
            });
            addr.execute(url);

            this.mLastLocation = mLastLocation;

        } else
            try {
                LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
            } catch (Exception e) {
                e.printStackTrace();
            }
        try {
            LocationRequest mLocationRequest = new LocationRequest();
            mLocationRequest.setInterval(10000);
            mLocationRequest.setFastestInterval(5000);
            mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i(TAG, "Connection suspended");
        mGoogleApiClient.connect();

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        try {
            if (location != null){
                //changeMap(location);
            }

            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this.getApplicationContext())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .enableAutoManage(this, 0 /* clientId */, this)
                .addApi(LocationServices.API)
                .addApi(Places.GEO_DATA_API)
                .build();
    }


    @Override
    public void onStart() {
        try {
            //Log.d(TAG, "onStart()");
            mGoogleApiClient.connect();
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.onStart();
    }

    @Override
    public void onStop() {
        try {

        } catch (RuntimeException e) {
            e.printStackTrace();
        }
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
        super.onStop();
    }

    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getApplicationContext());
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                //finish();
            }
            return false;
        }
        return true;
    }

    public GoogleApiClient mGoogleApiClient;
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private static final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION= 2;
    private static final int MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION= 3;
    protected Location mLastLocation;

}
