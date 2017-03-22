package id.co.kurindo.kurindo.map;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageButton;
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
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
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
import com.stepstone.stepper.Step;
import com.stepstone.stepper.VerificationError;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;
import id.co.kurindo.kurindo.R;
import id.co.kurindo.kurindo.adapter.TUserAdapter;
import id.co.kurindo.kurindo.app.AppConfig;
import id.co.kurindo.kurindo.base.RecyclerItemClickListener;
import id.co.kurindo.kurindo.model.Address;
import id.co.kurindo.kurindo.model.Route;
import id.co.kurindo.kurindo.model.TUser;
import id.co.kurindo.kurindo.wizard.BaseStepFragment;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;
import static android.widget.Toast.LENGTH_SHORT;

/**
 * Created by dwim on 2/14/2017.
 */

public class SinglePinLocationMapFragment extends BaseStepFragment
        implements Step, OnMapReadyCallback, View.OnClickListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    public GoogleMap mMap;
    public GoogleApiClient mGoogleApiClient;
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private static String TAG = "MAP LOCATION";
    public Context mContext;

    @Bind(R.id.locationMarkertext)
    public TextView mLocationMarkerText;
    private LatLng mCenterLatLong;
    @Bind(R.id.locationMarker)
    public LinearLayout locationMarkerLayout;

    private static final int REQUEST_CODE_AUTOCOMPLETE = 1;
    private static final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION= 2;
    private static final int MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION= 3;
    private static final LatLngBounds BOUNDS_ID = new LatLngBounds(new LatLng(-0, 0), new LatLng(0, 0));

    @Bind(R.id.iconOrigin)
    protected ImageView ivIconOrigin;
    protected TUserAdapter tUserAdapter;
    protected ArrayList<TUser> data = new ArrayList<>();

    @Bind(R.id.tvOrigin)
    public TextView tvOrigin;

    @Bind(R.id.imageMarker)
    protected ImageView imageMarker;
    @Bind(R.id.ivAddOriginNotes)
    protected ImageView ivAddOriginNotes;

    @Bind(R.id.etOriginNotes)
    protected EditText etOriginNotes;

    @Bind(R.id.origin_layout)
    protected LinearLayout originLayout;

    @Bind(R.id.orderInfo_layout)
    protected LinearLayout orderLayout;
    @Bind(R.id.service_layout)
    protected LinearLayout serviceLayout;
    @Bind(R.id.ivSwitchInfo)
    protected ImageView ivSwitchInfo;

    protected boolean originMode = true;
    protected boolean addOriginNote;

    @Bind(R.id.tvOriginAutoComplete)
    protected AutoCompleteTextView mOriginAutoCompleteTextView;
    protected PlaceArrayAdapter mPlaceArrayAdapter;

    protected Route route;
    protected TUser origin = new TUser();
    protected Location mLastLocation;
    protected Marker originMarker;

    public SupportMapFragment mapFragment;
    View view;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    protected int getLayout(){
        return R.layout.fragment_maps_singlepin_location;
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (view != null) {
            ViewGroup parent = (ViewGroup) view.getParent();
            if (parent != null)
                parent.removeView(view);
        }
        try {
            view = inflateAndBind(inflater, container, getLayout());
        } catch (InflateException e) {
            e.printStackTrace();
        }
        mContext = getContext();
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        //SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        //mapFragment.getMapAsync(this);
        //buildGoogleApiClient();

        initialize();
        showAddressLayout();

        tUserAdapter = new TUserAdapter(getContext(), data);

        mPlaceArrayAdapter = new PlaceArrayAdapter(getContext(), android.R.layout.simple_list_item_1, BOUNDS_ID, null);
        mOriginAutoCompleteTextView.setAdapter(mPlaceArrayAdapter);

        AdapterView.OnItemClickListener adapterOnItemClik =new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final PlaceArrayAdapter.PlaceAutocomplete item = mPlaceArrayAdapter.getItem(position);
                final String placeId = String.valueOf(item.placeId);
                Log.i("", "Selected: " + item.description);
                PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi.getPlaceById(mGoogleApiClient, placeId);
                placeResult.setResultCallback(mUpdatePlaceDetailsCallback);
                Log.i("", "Fetching details for ID: " + item.placeId);
            }
        };
        mOriginAutoCompleteTextView.setOnItemClickListener(adapterOnItemClik);

        //mResultReceiver = new MapsActivity.AddressResultReceiver(new Handler());

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

        return view;

    }

    private void initialize() {
        if(tvOrigin == null || mLocationMarkerText == null){
            tvOrigin = (TextView) view.findViewById(R.id.tvOrigin);
            tvOrigin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onClick_tvOrigin();
                }
            });

            ivAddOriginNotes = (ImageView) view.findViewById(R.id.ivAddOriginNotes);
            ivAddOriginNotes.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onClick_ivAddOriginNotes();
                }
            });
            mOriginAutoCompleteTextView = (AutoCompleteTextView) view.findViewById(R.id.tvOriginAutoComplete);

            originLayout = (LinearLayout) view.findViewById(R.id.origin_layout);
            locationMarkerLayout = (LinearLayout) view.findViewById(R.id.locationMarker);
            imageMarker = (ImageView) view.findViewById(R.id.imageMarker);
            mLocationMarkerText = (TextView) view.findViewById(R.id.locationMarkertext);
            mLocationMarkerText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onClick_mLocationMarkerText();
                }
            });

            orderLayout = (LinearLayout) view.findViewById(R.id.orderInfo_layout);
            serviceLayout = (LinearLayout) view.findViewById(R.id.service_layout);
            ivSwitchInfo = (ImageView) view.findViewById(R.id.ivSwitchInfo);
            ivSwitchInfo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    OnClick_ivSwitchInfo();
                }
            });

        }
    }

    public void onDestroyView() {
        super.onDestroyView();
        //if (mapFragment != null) getFragmentManager().beginTransaction().remove(mapFragment).commit();
        /*FragmentManager fm = getActivity().getSupportFragmentManager();
        Fragment fragment = (fm.findFragmentById(R.id.map));
        FragmentTransaction ft = fm.beginTransaction();
        ft.remove(fragment);
        ft.commit();*/
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


    private ResultCallback<PlaceBuffer> mUpdatePlaceDetailsCallback
            = new ResultCallback<PlaceBuffer>() {
        @Override
        public void onResult(PlaceBuffer places) {
            if (!places.getStatus().isSuccess()) {
                Log.e("ResultCallback", "Place query did not complete. Error: " +places.getStatus().toString());
                return;
            }
            // Selecting the first object buffer.
            final Place place = places.get(0);
            CharSequence attributions = places.getAttributions();

            String name = place.getName().toString();
            String address = place.getAddress().toString();
            if(originMode){
                tvOrigin.setText(address);
                Address addr = origin.getAddress();
                addr.setAlamat(name);
                addr.setFormattedAddress(address);
                addr.setLocation( place.getLatLng());
            }
            showAddressLayout();
            refreshMap();
            if(!canDrawRoute()) moveCameraToLocation( place.getLatLng());
        }
    };

    protected boolean canDrawRoute() {
        return (origin.getAddress().getLocation() != null );
    }

    public void resetAll() {
        route = null;

        origin = new TUser();
    }

    public boolean handleBackPressed(){
        if(originMode){
            originMode = false;
            showAddressLayout();
            if(origin != null && origin.getAddress().getLocation() != null)
                moveCameraToLocation(origin.getAddress().getLocation());
            showMap();
            return true;
        }
        if(canDrawRoute()){
            showMap();
            showAddressLayout();
            moveCameraToLocation(origin.getAddress().getLocation());
            return true;
        }
        resetAll();
        return false;
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            mGoogleApiClient.stopAutoManage(getActivity());
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            mGoogleApiClient.stopAutoManage(getActivity());
            mGoogleApiClient.disconnect();
        }
        getActivity().finish();
    }

    protected void showAddressLayout() {
        tvOrigin.setVisibility(View.VISIBLE);
        ivAddOriginNotes.setVisibility(View.VISIBLE);
        mOriginAutoCompleteTextView.setVisibility(View.GONE);

        originLayout.setVisibility(View.VISIBLE);
        locationMarkerLayout.setVisibility(View.GONE);
    }

    private void changeMarkerIcon(){
        locationMarkerLayout.setVisibility(View.VISIBLE);
        imageMarker.setImageResource(originMode ? R.drawable.origin_pin : R.drawable.destination_pin );
        mLocationMarkerText.setVisibility(View.VISIBLE);
        mLocationMarkerText.setText("Set "+(originMode? "Lokasi Anda" : "Tujuan Anda" ));
    }
    public void moveCameraToLocation(LatLng location, float zoom){
        CameraPosition cameraPosition = new CameraPosition.Builder().target(location).zoom(zoom).tilt(AppConfig.DEFAULT_TILT_MAP).build();
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }
    public void moveCameraToLocation(LatLng location){
        moveCameraToLocation(location, AppConfig.DEFAULT_ZOOM_MAP);
    }
    public void onClick(View v) {
        //TODO
    }

    @OnClick(R.id.iconOrigin)
    public void onClick_IconOrigin(){
        originMode = true;
        showPopupWindow("Daftar Lokasi", R.drawable.origin_pin);

    }

    @OnClick(R.id.locationMarkertext)
    public void onClick_mLocationMarkerText(){
        if(originMode){
            origin.getAddress().setLocation( new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude()) );
            //tvOrigin.setText("Lat. "+ mLastLocation.getLatitude()+ ", "+mLastLocation.getLongitude());
        }
        requestAddress(new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude()));

        mLocationMarkerText.setVisibility(View.GONE);

        showAddressLayout();
        //refreshMap();
        //startIntentService(mLastLocation);
    }

    protected void showMap() {
        reDrawMarker();
    }
    protected void refreshMap() {
        reDrawMarker();
    }

    @OnClick(R.id.ivSwitchInfo)
    public void OnClick_ivSwitchInfo(){
        showOrderpanel(serviceLayout.isShown());
        orderLayout.setVisibility(View.VISIBLE );
        //serviceLayout.setMinimumHeight(100);
        //buttonAddOrder.setVisibility(View.VISIBLE );
    }
    private void showOrderpanel(boolean show){
        orderLayout.setVisibility((show ? View.VISIBLE : View.GONE ));
        ivSwitchInfo.setImageResource(show? R.drawable.ic_expand_less_black_18dp: R.drawable.ic_expand_more_black_18dp );
        serviceLayout.setVisibility(show ? View.GONE : View.VISIBLE );
    }

    protected void hidepanel(boolean hide) {
        orderLayout.setVisibility((hide ? View.GONE : View.VISIBLE));
        serviceLayout.setVisibility((hide ? View.GONE : View.VISIBLE));
        //buttonAddOrder.setVisibility((hide ? View.GONE : View.VISIBLE));
    }
    private void requestAddress(LatLng latLng) {
        String url = MapUtils.getGeocodeUrl(latLng);
        addRequest("request_geocode_address", Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //Log.d(TAG, "requestAddress Response: " + response.toString());
                List<List<HashMap<String, String>>> routes = null;
                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean OK = "OK".equalsIgnoreCase(jObj.getString("status"));
                    if(OK){
                        DataParser parser = new DataParser();
                        Address address = parser.parseAddress(jObj);
                        if(originMode){
                            tvOrigin.setText(address.getFormattedAddress());
                            address.setLocation(origin.getAddress().getLocation());
                            origin.setAddress(  address );
                            //origin = address.getLocation();
                            //originMode= false;
                        }
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
                refreshMap();
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
        if(origin != null && origin.getAddress() != null && origin.getAddress().getLocation() != null){
            String title = "Origin";
            String snippet = (origin.getName() != null ? origin.toStringFormatted() : "");
            originMarker = mMap.addMarker(
                    new MarkerOptions()
                            .position(origin.getAddress().getLocation())
                            .title(title)
                            .snippet(snippet)
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.origin_pin)));
        }

    }


    @OnClick(R.id.tvOrigin)
    public void onClick_tvOrigin(){
        originMode= true;
        tvOrigin.setVisibility(originMode ? View.GONE : View.VISIBLE);
        ivAddOriginNotes.setVisibility(originMode ? View.GONE : View.VISIBLE);
        locationMarkerLayout.setVisibility(View.VISIBLE);
        mOriginAutoCompleteTextView.setVisibility(originMode ? View.VISIBLE : View.GONE);
        mOriginAutoCompleteTextView.setText("");
        if(origin.getAddress().getLocation() != null) moveCameraToLocation(origin.getAddress().getLocation());
        changeMarkerIcon();
        hidepanel(true);
    }


    @OnClick(R.id.ivAddOriginNotes)
    public void onClick_ivAddOriginNotes(){
        addOriginNote =!addOriginNote;
        etOriginNotes.setVisibility(addOriginNote? View.VISIBLE : View.GONE);
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
                if(!canDrawRoute() || originMode ){
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
                    if(!canDrawRoute()) changeMarkerIcon();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

    }

    @Override
    public void onConnected(Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);

            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{ Manifest.permission.ACCESS_COARSE_LOCATION},
                    MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION);

            return;
        }
        Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (mLastLocation != null) {
            Log.d(TAG, "ON connected ");
            if(origin.getAddress().getLocation() == null) {
                origin.getAddress().setLocation( new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude()) );
                originMode= true;
                requestAddress(origin.getAddress().getLocation());
            }
            changeMap(mLastLocation);
            reDrawMarker();
            showAddressLayout();
            this.mLastLocation = mLastLocation;
            onClick_tvOrigin();

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

        mPlaceArrayAdapter.setGoogleApiClient(mGoogleApiClient);

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
                changeMap(location);
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
        mGoogleApiClient = new GoogleApiClient.Builder(getContext())
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

    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getContext());
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, getActivity(),
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


        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);

            ActivityCompat.requestPermissions(getActivity(),
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
            Toast.makeText(getContext(),
                    "Sorry! unable to create maps", LENGTH_SHORT)
                    .show();
        }

    }

    /**
     * Called after the autocomplete activity has finished to return its result.
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Check that the result was from the autocomplete widget.
        if (requestCode == REQUEST_CODE_AUTOCOMPLETE) {
            if (resultCode == RESULT_OK) {
                // Get the user's selected place from the Intent.
                Place place = PlaceAutocomplete.getPlace(mContext, data);

                // TODO call location based filter


                LatLng latLong;


                latLong = place.getLatLng();

                //mLocationText.setText(place.getName() + "");
                if(originMode)
                    tvOrigin.setText(place.getAddress());

                moveCameraToLocation(latLong);

            }


        } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
            Status status = PlaceAutocomplete.getStatus(mContext, data);
        } else if (resultCode == RESULT_CANCELED) {
            // Indicates that the activity closed before a selection was made. For example if
            // the user pressed the back button.
        }
    }


    protected void showPopupWindow(String title, int imageResourceId) {
        data.clear();
        data.addAll(db.getAddressList());

        // Create custom dialog object
        final Dialog dialog = new Dialog(getContext());
        // Include dialog.xml file
        dialog.setContentView(R.layout.popup_list);
        // Set dialog title
        dialog.setTitle("Popup Dialog");

        RecyclerView list = (RecyclerView) dialog.findViewById(R.id.popupList);
        list.setLayoutManager(new GridLayoutManager(getContext(), 1));
        list.setHasFixedSize(true);
        list.setAdapter(tUserAdapter);
        list.addOnItemTouchListener(new RecyclerItemClickListener(getContext(), new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                TUser p = data.get(position);
                showAddressLayout();
                if(originMode){
                    origin = p;
                    tvOrigin.setText(p.getAddress().toStringFormatted());
                    if(p.getAddress().getNotes() != null && !p.getAddress().getNotes().isEmpty()) {
                        etOriginNotes.setText(p.getAddress().getNotes());
                        etOriginNotes.setVisibility(View.VISIBLE);
                    }
                    originMode = false;
                }

                dialog.dismiss();
                refreshMap();
            }
        }));

        // set values for custom dialog components - text, image and button
        //TextView text = (TextView) dialog.findViewById(R.id.textDialog);
        //text.setText(content);
        ImageView image = (ImageView) dialog.findViewById(R.id.imageDialog);
        if(imageResourceId == 0) imageResourceId  = R.drawable.icon_syarat_ketentuan;
        image.setImageResource(imageResourceId);
        TextView textTitleDialog = (TextView) dialog.findViewById(R.id.textTitleDialog);
        if(title != null) textTitleDialog.setText(title);

        dialog.show();

        ImageButton declineButton = (ImageButton) dialog.findViewById(R.id.btncancelcat);
        // if decline button is clicked, close the custom dialog
        declineButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Close dialog
                dialog.dismiss();
                originMode = false;
            }
        });
    }


    @Override
    public int getName() {
        return R.string.pin_single_location;
    }

    public VerificationError verifyStep() {

        String onotes = etOriginNotes.getText().toString();
        origin.getAddress().setNotes(onotes);

        return null;
    }

    @Override
    public void onSelected() {
        updateUI();
    }

    protected void updateUI() {

    }

    @Override
    public void onError(@NonNull VerificationError error) {

    }

    static SinglePinLocationMapFragment instance ;
    protected static Fragment newInstance() {
        if (instance == null) {
            instance = new SinglePinLocationMapFragment();
        }
        return instance;
    }

    protected static Fragment getInstance() {
        return instance;
    }

}


