package id.co.kurindo.kurindo.map;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import id.co.kurindo.kurindo.R;
import id.co.kurindo.kurindo.app.AppConfig;
import id.co.kurindo.kurindo.base.KurindoActivity;
import id.co.kurindo.kurindo.helper.ViewHelper;
import id.co.kurindo.kurindo.model.DoMart;
import id.co.kurindo.kurindo.model.Route;
import id.co.kurindo.kurindo.model.TOrder;
import id.co.kurindo.kurindo.model.TUser;

import static android.widget.Toast.LENGTH_SHORT;

public class MapViewsActivity extends KurindoActivity implements OnMapReadyCallback, View.OnClickListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener{

    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private static String TAG = "MAP LOCATION";
    Context mContext;

    private LatLng mCenterLatLong;

    private static final int REQUEST_CODE_AUTOCOMPLETE = 1;
    private static final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION= 2;
    private static final int MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION= 3;

    private static final LatLngBounds BOUNDS_ID = new LatLngBounds(new LatLng(-0, 0), new LatLng(0, 0));


    @Bind(R.id.tvOrigin)
    TextView tvOrigin;
    @Bind(R.id.tvDestination)
    TextView tvDestination;

    @Bind(R.id.ivAddOriginNotes)
    ImageView ivAddOriginNotes;
    @Bind(R.id.ivAddDestinationNotes)
    ImageView ivAddDestinationNotes;

    @Bind(R.id.etOriginNotes)
    EditText etOriginNotes;

    @Bind(R.id.etDestinationNotes)
    EditText etDestinationNotes;

    @Bind(R.id.destination_layout)
    LinearLayout destinationLayout;
    @Bind(R.id.origin_layout)
    LinearLayout originLayout;

    TOrder order;

    Route route;
    TUser origin = new TUser();
    TUser destination = new TUser();
    Location mLastLocation;
    Marker originMarker;
    Marker destinationMarker;
    List<TUser> waypoints;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps_view);
        ButterKnife.bind(this);

        order = ViewHelper.getInstance().getOrder();
        if(order != null){
            if(order.getService_type().equalsIgnoreCase(AppConfig.KEY_DOMART)){
                for(DoMart dom : order.getMarts()){
                    origin = dom.getOrigin();
                    if(waypoints == null){
                        waypoints = new ArrayList<>();
                    }
                    waypoints.add(dom.getOrigin());
                }
                destination = ViewHelper.getInstance().getOrder().getPlace();
            }else{
                if(ViewHelper.getInstance().getPacket() != null && ViewHelper.getInstance().getPacket().getDestination() != null && ViewHelper.getInstance().getPacket().getOrigin() != null){
                    origin = ViewHelper.getInstance().getPacket().getOrigin();
                    destination = ViewHelper.getInstance().getPacket().getDestination();
                }
            }

            if(origin != null){
                tvOrigin.setText(origin.getAddress().toStringFormatted());
                if(origin.getAddress().getNotes() != null && !origin.getAddress().getNotes().isEmpty()){
                    etOriginNotes.setText(origin.getAddress().getNotes());
                    etOriginNotes.setVisibility(View.VISIBLE);
                }
            }
            if(destination != null){
                tvDestination.setText(destination.getAddress().toStringFormatted());
                if(destination.getAddress().getNotes() != null && !destination.getAddress().getNotes().isEmpty()){
                    etDestinationNotes.setText(destination.getAddress().getNotes());
                    etDestinationNotes.setVisibility(View.VISIBLE);
                }
            }
        }
        mContext = this;
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        buildGoogleApiClient();

        //showAddressLayout();



        if (checkPlayServices()) {
            // If this check succeeds, proceed with normal processing.
            // Otherwise, prompt user to get valid Play Services APK.
            if (!MapUtils.isLocationEnabled(mContext)) {
                // notify user
                AlertDialog.Builder dialog = new AlertDialog.Builder(mContext);
                dialog.setMessage("Location not enabled!");
                dialog.setPositiveButton("Open location settings", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                        Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(myIntent);
                    }
                });
                dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                        // TODO Auto-generated method stub

                    }
                });
                dialog.show();
            }
        } else {
            Toast.makeText(mContext, "Location not supported in this device", LENGTH_SHORT).show();
        }

    }

    private boolean canDrawRoute() {
        return (origin.getAddress() != null && origin.getAddress().getLocation() != null && origin.getAddress() != null && destination.getAddress().getLocation() != null);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        finish();
    }
    public void moveCameraToLocation(LatLng location, float zoom){
        CameraPosition cameraPosition = new CameraPosition.Builder().target(location).zoom(zoom).tilt(70).build();
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }
    public void moveCameraToLocation(LatLng location){
        moveCameraToLocation(location, 19f);
    }
    public void onClick(View v) {
        //TODO
    }

    @Override
    protected ActionBar setupToolbar() {
        ActionBar ab = super.setupToolbar();
        if(ab != null) ab.setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_18dp);
        return  ab;
    }

    @Override
    public Bundle getBundleParams() {
        return null;
    }


    private void showMap() {
        reDrawMarker();
        drawRoute();
    }
    private void refreshMap() {
        reDrawMarker();
        reDrawRoute();
    }

    private void drawRoute() {
        if(route != null){
            DataParser parser = new DataParser();
            String snippet = originMarker.getSnippet();
            originMarker.setSnippet(snippet + "\nEst. Distance :"+ route.getDistance().getText());
            PolylineOptions lineOptions = parser.drawRoutes(route.getRoutes());

            // Drawing polyline in the Google Map for the i-th route
            if(lineOptions != null) {
                mMap.addPolyline(lineOptions);

                LatLngBounds.Builder builder = new LatLngBounds.Builder();
                /*
                List<LatLng> points = lineOptions.getPoints();
                for (int i = 0; i < points.size(); i++) {
                    builder.include(points.get(i));
                }*/
                builder.include(origin.getAddress().getLocation());
                if(destination.getAddress().getLocation() != null) builder.include(destination.getAddress().getLocation());

                LatLngBounds bounds = builder.build();

                int width = getResources().getDisplayMetrics().widthPixels;
                int padding = (int) (width * 0.40); // offset from edges of the map 12% of screen

                CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
                mMap.animateCamera(cu);
                //moveCameraToLocation(bounds.getCenter(), 16f);

            }else {
                Log.d("drawRoutes","without Polylines drawn");
            }
        }
        if(canDrawRoute() && route != null) {
            Location lo = new Location("Origin");
            lo.setLatitude(origin.getAddress().getLocation().latitude);
            lo.setLongitude(origin.getAddress().getLocation().longitude);

            Location ld = new Location("Destination");
            ld.setLatitude(destination.getAddress().getLocation().latitude);
            ld.setLongitude(destination.getAddress().getLocation().longitude);

            //tvDistanceInfo.setText("Harga ( "+route.getDistance().getText()+" ) : ");
            //tvPriceInfo.setText("");

            Toast.makeText(getApplicationContext(), "Calculate distance : " + lo.distanceTo(ld), LENGTH_SHORT).show();
        }

    }
    private void reDrawRoute() {

        if(canDrawRoute()){

            String url = MapUtils.getDirectionUrl(origin.getAddress().getLocation(), destination.getAddress().getLocation());
            addRequest("request_direction_route", Request.Method.GET, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    //Log.d(TAG, "drawRoute Response: " + response.toString());
                    try {
                        JSONObject jObj = new JSONObject(response);
                        boolean OK = "OK".equalsIgnoreCase(jObj.getString("status"));
                        if(OK){
                            DataParser parser = new DataParser();
                            route = parser.parseRoutes(jObj);
                            drawRoute();
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError volleyError) {
                    Log.e(TAG, "request_direction_route Error: " + volleyError.getMessage());
                    volleyError.printStackTrace();
                }
            }, null, null);

        }
    }


    private void requestDistance() {
        String url = MapUtils.getDistancematrixUrl(origin.getAddress().getLocation(), destination.getAddress().getLocation());
        addRequest("request_distance_route", Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //Log.d(TAG, "drawRoute Response: " + response.toString());

                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean OK = "OK".equalsIgnoreCase(jObj.getString("status"));
                    if(OK){
                        String destinationTxt = "";
                        String originTxt = "";
                        String distanceTxt = " ";
                        int distance = 0;
                        int duration = 0;
                        String durationTxt = "";

                        JSONArray originArr = jObj.getJSONArray("origin_addresses");
                        JSONArray destinationArr = jObj.getJSONArray("destination_addresses");
                        JSONArray rows = jObj.getJSONArray("rows");
                        for (int i = 0; i < originArr.length(); i++) {
                            if(i >0) {
                                originTxt +=",";
                                destinationTxt +=",";
                            }
                            originTxt += originArr.getString(i);
                            destinationTxt += destinationArr.getString(i);
                            JSONObject rowObj = rows.getJSONObject(i);
                            JSONArray elementsArr = rowObj.getJSONArray("elements");
                            for (int j = 0; j < elementsArr .length(); j++) {
                                JSONObject elementObj = elementsArr.getJSONObject(j);
                                JSONObject distanceObj = elementObj.getJSONObject("distance");
                                JSONObject durationObj = elementObj.getJSONObject("duration");
                                distance += distanceObj.getInt("value");
                                distanceTxt = distanceObj.getString("text");
                                duration += durationObj.getInt("value");
                            }
                        }
                        /*GsonBuilder builder = new GsonBuilder();
                        builder.setPrettyPrinting();
                        Gson gson = builder.create();
                        DistanceMatrixResponse mtx = gson.fromJson(response, DistanceMatrixResponse.class);
                        */
                        tvOrigin.setText(originTxt);
                        tvDestination.setText(destinationTxt);
                        originMarker.setSnippet("Est. Distance :"+distanceTxt );
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


    public void reDrawMarker(){
        mMap.clear();
        if(destination != null && destination.getAddress() != null && destination.getAddress().getLocation() != null){
            String title = "Destination";
            String snippet = (destination.getName() != null ? destination.toStringFormatted() : "");
            destinationMarker = mMap.addMarker(
                    new MarkerOptions()
                            .position(destination.getAddress().getLocation())
                            .title(title)
                            .snippet(snippet)
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.destination_pin)));
            destinationMarker.showInfoWindow();
        }
        if(origin != null && origin.getAddress() != null && origin.getAddress().getLocation() != null){
            String title = "Origin";
            String snippet = (origin.getName() != null ? origin.toStringFormatted() : "");
            originMarker = mMap.addMarker(
                    new MarkerOptions()
                            .position(origin.getAddress().getLocation())
                            .title(title)
                            .snippet(snippet)
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.origin_pin)));
            originMarker.showInfoWindow();
        }

    }



    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.d(TAG, "OnMapReady");
        mMap = googleMap;
        mMap.setOnCameraMoveStartedListener(new GoogleMap.OnCameraMoveStartedListener() {
            @Override
            public void onCameraMoveStarted(int i) {
                if(!canDrawRoute() ){
                    reDrawMarker();
                }
            }
        });
        mMap.setOnCameraMoveCanceledListener(new GoogleMap.OnCameraMoveCanceledListener() {
            @Override
            public void onCameraMoveCanceled() {
                reDrawMarker();
            }
        });
        mMap.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
            @Override
            public void onCameraChange(CameraPosition cameraPosition) {
                Log.d("Camera position change" + "", cameraPosition + "");
                mCenterLatLong = cameraPosition.target;

                //mMap.clear();

                try {
                    Location mLocation = new Location("");
                    mLocation.setLatitude(mCenterLatLong.latitude);
                    mLocation.setLongitude(mCenterLatLong.longitude);
                    mLastLocation = mLocation;

                    //startIntentService(mLocation);
                    //mLocationMarkerText.setText("Lat : " + mCenterLatLong.latitude + "," + "Long : " + mCenterLatLong.longitude);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

    }

    @Override
    public void onConnected(Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);

            ActivityCompat.requestPermissions(this,
                    new String[]{ Manifest.permission.ACCESS_COARSE_LOCATION},
                    MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION);

            return;
        }
        Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (mLastLocation != null) {
            Log.d(TAG, "ON connected ");
            if(canDrawRoute()) {
                refreshMap();
                Location loc = new Location("Origin");
                loc.setLatitude( origin.getAddress().getLocation().latitude );
                loc.setLongitude( origin.getAddress().getLocation().longitude );
                changeMap(loc);
            }else{
                changeMap(mLastLocation);
                reDrawMarker();
            }
            //showAddressLayout();
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

        //mPlaceArrayAdapter.setGoogleApiClient(mGoogleApiClient);

    }


    @Override
    public void onConnectionSuspended(int i) {
        Log.i(TAG, "Connection suspended");
        mGoogleApiClient.connect();
    }

    @Override
    public void onLocationChanged(Location location) {
        try {
            if (location != null){
                if(canDrawRoute()){
                    drawRoute();
                }else{
                    changeMap(location);
                }
            }

            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }


    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .enableAutoManage(this, 0 /* clientId */, this)
                .addApi(LocationServices.API)
                .addApi(Places.GEO_DATA_API)
                .build();
    }

    @Override
    protected void onStart() {
        try {
            mGoogleApiClient.connect();

        } catch (Exception e) {
            e.printStackTrace();
        }
        super.onStart();
    }

    @Override
    protected void onStop() {
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
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
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

    private void changeMap(Location location) {

        Log.d(TAG, "Reaching map" + mMap);


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);

            ActivityCompat.requestPermissions(this,
                    new String[]{ Manifest.permission.ACCESS_COARSE_LOCATION},
                    MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION);
            return;
        }

        // check if map is created successfully or not
        if (mMap != null) {
            mMap.getUiSettings().setZoomControlsEnabled(false);
            LatLng latLong;

            latLong = new LatLng(location.getLatitude(), location.getLongitude());

            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(true);
            moveCameraToLocation(latLong);

            //mLocationMarkerText.setText("Set "+ (originMode ? "Lokasi Jemput":"Tujuan Anda >"));
            //startIntentService(location);

        } else {
            Toast.makeText(getApplicationContext(),
                    "Sorry! unable to create maps", LENGTH_SHORT)
                    .show();
        }

    }

    @Override
    public boolean providesActivityToolbar() {
        return true;
    }

    @Override
    public Class getFragmentClass() {
        return null;
    }

}
