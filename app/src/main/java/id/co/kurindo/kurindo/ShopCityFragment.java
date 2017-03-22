package id.co.kurindo.kurindo;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.stepstone.stepper.VerificationError;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.OnClick;
import id.co.kurindo.kurindo.adapter.ShopBranchAdapter;
import id.co.kurindo.kurindo.adapter.TUserAdapter;
import id.co.kurindo.kurindo.app.AppConfig;
import id.co.kurindo.kurindo.app.AppController;
import id.co.kurindo.kurindo.base.BaseActivity;
import id.co.kurindo.kurindo.base.BaseFragment;
import id.co.kurindo.kurindo.base.RecyclerItemClickListener;
import id.co.kurindo.kurindo.helper.ViewHelper;
import id.co.kurindo.kurindo.model.Address;
import id.co.kurindo.kurindo.model.Shop;
import id.co.kurindo.kurindo.model.TUser;
import id.co.kurindo.kurindo.util.ParserUtil;
import id.co.kurindo.kurindo.wizard.shopadm.AddProductActivity;
import id.co.kurindo.kurindo.wizard.shopadm.AddShopBranchActivity;

import static android.widget.Toast.LENGTH_SHORT;

/**
 * Created by dwim on 3/12/2017.
 */

public class ShopCityFragment extends BaseFragment implements OnMapReadyCallback, LocationListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    private static final String TAG = "ShopCityFragment";

    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;
    private LatLng mCenterLatLong;
    Location mLastLocation;
    private static final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION= 2;
    private static final int MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION= 3;
    SupportMapFragment mapFragment;


    @Bind(R.id.list_request)
    RecyclerView mRequestRecyclerView;
    protected ArrayList<TUser> data = new ArrayList<>();
    protected TUserAdapter tUserAdapter;

    @Bind(R.id.list)
    RecyclerView mRecyclerView;
    ShopBranchAdapter mAdapter;
    ArrayList<Shop> shops = new ArrayList<>();
    Shop shop;

    @Bind(R.id.rgLayoutType)
    protected RadioGroup rgLayoutType;

    int count = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        shop = ViewHelper.getInstance().getShop();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflateAndBind(inflater, container, R.layout.shop_city_layout);

        //if admin loadNewsDummy();
        setup_radio_group();
        setup_list_branch();
        setup_request_baru();
        return  view;
    }

    private void setup_radio_group() {

        rgLayoutType.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (group.getCheckedRadioButtonId()){
                    case R.id.radio_main:
                        mRecyclerView.setVisibility(View.VISIBLE);
                        mRequestRecyclerView.setVisibility(View.GONE);
                        break;
                    case R.id.radio_request:
                        mRequestRecyclerView.setVisibility(View.VISIBLE);
                        mRecyclerView.setVisibility(View.GONE);
                        break;
                }
                count++;
                if(count > 20) {
                    requestDataPengelola();
                    load_shops();
                }
            }
        });
        rgLayoutType.check(R.id.radio_main);
    }

    private void setup_list_branch() {

        mRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 1));
        mRecyclerView.setHasFixedSize(true);

        mAdapter = new ShopBranchAdapter(getContext(), shops);
        mRecyclerView.setAdapter(mAdapter);

        shops.add(shop);
        mRecyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getContext(),
                new RecyclerItemClickListener.OnItemClickListener() {

                    @Override
                    public void onItemClick(View view, int position) {
                        Shop p = shops.get(position);
                        TUser pic = p.getPic();
                        if(pic != null && pic.getAddress() != null)
                            moveCameraToLocation(pic.getAddress().getLocation());
                    }
                }));

        load_shops();
    }

    private void setup_request_baru() {
        tUserAdapter = new TUserAdapter(getContext(), data);
        TUser u = new TUser();
        u.setFirstname("asasasa");
        Address addr = new Address();
        addr.setLocation(new LatLng(-0.3320503, 117.429754));
        u.setAddress(addr);
        data.add(u);

        mRequestRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 1));
        mRequestRecyclerView.setHasFixedSize(true);
        mRequestRecyclerView.setAdapter(tUserAdapter);
        mRequestRecyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getContext(), new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                TUser p = data.get(position);
                moveCameraToLocation(p.getAddress().getLocation());
            }
        }));
        requestDataPengelola();
    }

    private void requestDataPengelola() {
        HashMap<String, String> params = new HashMap();
        params.put("shop_id", ""+shop.getId());

        HashMap<String, String> headers = new HashMap();
        headers.put("Api", db.getUserApi());

        addRequest("request_data_pengelola", Request.Method.POST, AppConfig.URL_SHOP_PREPIC_LIST, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    Log.d(TAG, "requestDataPengelola Response: " + response.toString());
                    JSONObject jObj = new JSONObject(response);
                    String message = jObj.getString("status");
                    boolean OK = "OK".equalsIgnoreCase(message);
                    if(OK){
                        JSONArray datas = jObj.getJSONArray("data");
                        if(datas != null && datas.length() > 0) {
                            data.clear();
                            ParserUtil parser = new ParserUtil();
                            for (int i = 0; i < datas.length(); i++) {
                                TUser user = parser.parserUser(datas.getJSONObject(i));
                                data.add(user);
                            }
                            tUserAdapter.notifyDataSetChanged();
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
        }, params, headers);
    }

    private void load_shops(){
        final String URI = AppConfig.URL_SHOP_CITY_LIST;
        Map<String, String> params = new HashMap<String, String>();
        params.put("shop_id", ""+shop.getId());

        addRequest("request_shop_city_list", Request.Method.POST, URI, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "request_shop_city_list Response: " + response.toString());
                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean success = jObj.getBoolean("success");
                    if (success) {
                        shops.clear();
                        JSONArray datas = jObj.getJSONArray("data");
                        ParserUtil parser = new ParserUtil();
                        for (int j = 0; j < datas.length(); j++) {
                            JSONObject obj = datas.getJSONObject(j);
                            Shop s = parser.parserShop(obj);
                            if(s != null) shops.add(s);
                        }
                        mAdapter.notifyDataSetChanged();
                        reDrawMarker();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                volleyError.printStackTrace();
                Log.e(TAG, "LoadShopTask Error: " + volleyError.getMessage());
                Toast.makeText(getActivity(),volleyError.getMessage(), Toast.LENGTH_LONG).show();
            }
        }, params, getKurindoHeaders());

    }
    /*
    @OnClick(R.id.addBranchCity)
    public void addBranchCity_onClick(){
        ((BaseActivity)getActivity()).showActivity(AddShopBranchActivity.class);
    }
    */

    public void reDrawMarker(){
        if(mMap != null){
            mMap.clear();
            String title = "Shop "+shop.getName();
            int i = 1;
            for (Shop s: shops) {
                String snippet = (shop.getAddress() != null ? shop.getAddress().toStringShortFormatted() : "");
                mMap.addMarker(
                        new MarkerOptions()
                                .position( (s.getAddress() != null && s.getAddress().getLocation() != null ? s.getAddress().getLocation() : (mLastLocation==null? new LatLng(0,0) : new LatLng( mLastLocation.getLatitude(), mLastLocation.getLongitude() ) ) ) )
                                .title(title + " "+i++)
                                .snippet(snippet)
                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.origin_pin)));

            }

             title = "Request"; i =1;
            for (TUser s: data) {
                String snippet = (s.getAddress() != null ? s.getAddress().toStringShortFormatted() : "");
                mMap.addMarker(
                        new MarkerOptions()
                                .position( (s.getAddress() != null && s.getAddress().getLocation() != null ? s.getAddress().getLocation() : (mLastLocation==null? new LatLng(0,0) : new LatLng( mLastLocation.getLatitude(), mLastLocation.getLongitude() ) ) ) )
                                .title(title + " "+ i++)
                                .snippet(snippet)
                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.destination_pin)));

            }
        }

    }

    public void moveCameraToLocation(LatLng location, float zoom){
        CameraPosition cameraPosition = new CameraPosition.Builder().target(location).zoom(zoom).tilt(AppConfig.DEFAULT_TILT_MAP).build();
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }
    public void moveCameraToLocation(LatLng location){
        moveCameraToLocation(location, AppConfig.MAP_ZOOM_OUT);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.d(TAG, "OnMapReady");
        mMap = googleMap;
        mMap.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
            @Override
            public void onCameraChange(CameraPosition cameraPosition) {
                Log.d("Camera position change" + "", cameraPosition + "");
                mCenterLatLong = cameraPosition.target;

                try {
                    Location mLocation = new Location("");
                    mLocation.setLatitude(mCenterLatLong.latitude);
                    mLocation.setLongitude(mCenterLatLong.longitude);
                    mLastLocation = mLocation;


                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

    }

    @Override
    public void onLocationChanged(Location location) {
        try {
            if (location != null){
                changeMap(location);
            }

            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void changeMap(Location location) {

        Log.d(TAG, "Reaching map" + mMap);


        if (ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);

            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{ android.Manifest.permission.ACCESS_COARSE_LOCATION},
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
            Toast.makeText(getActivity(),
                    "Sorry! unable to create maps", LENGTH_SHORT)
                    .show();
        }

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);

            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{ android.Manifest.permission.ACCESS_COARSE_LOCATION},
                    MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION);

            return;
        }
        Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (mLastLocation != null) {
            Log.d(TAG, "ON connected ");
            moveCameraToLocation(new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude()));
            reDrawMarker();
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
        mGoogleApiClient.connect();
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .enableAutoManage(getActivity(), 0 /* clientId */, this)
                .addApi(LocationServices.API)
                .addApi(Places.GEO_DATA_API)
                .build();
    }

    @Override
    public void onStart() {
        try {
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

    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        FragmentManager fm = getChildFragmentManager();
        mapFragment = (SupportMapFragment) fm.findFragmentById(R.id.map);
        if (mapFragment == null) {
            mapFragment = SupportMapFragment.newInstance();
            fm.beginTransaction().replace(R.id.map, mapFragment).commit();
        }
        mapFragment.getMapAsync(this);
        buildGoogleApiClient();

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
