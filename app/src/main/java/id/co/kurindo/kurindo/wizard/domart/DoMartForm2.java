package id.co.kurindo.kurindo.wizard.domart;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
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
import android.widget.CheckBox;
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
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.stepstone.stepper.Step;
import com.stepstone.stepper.VerificationError;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import butterknife.Bind;
import butterknife.OnClick;
import id.co.kurindo.kurindo.R;
import id.co.kurindo.kurindo.adapter.DoMartViewAdapter;
import id.co.kurindo.kurindo.app.AppConfig;
import id.co.kurindo.kurindo.comp.ProgressDialogCustom;
import id.co.kurindo.kurindo.helper.DoMartHelper;
import id.co.kurindo.kurindo.helper.DoSendHelper;
import id.co.kurindo.kurindo.helper.ViewHelper;
import id.co.kurindo.kurindo.map.DataParser;
import id.co.kurindo.kurindo.map.MapUtils;
import id.co.kurindo.kurindo.model.Address;
import id.co.kurindo.kurindo.model.DoMart;
import id.co.kurindo.kurindo.model.Route;
import id.co.kurindo.kurindo.model.TOrder;
import id.co.kurindo.kurindo.model.TPrice;
import id.co.kurindo.kurindo.model.TUser;
import id.co.kurindo.kurindo.util.LogUtil;
import id.co.kurindo.kurindo.wizard.BaseStepFragment;

import static android.widget.Toast.LENGTH_SHORT;
import static id.co.kurindo.kurindo.util.LogUtil.makeLogTag;

/**
 * Created by DwiM on 5/7/2017.
 */

public class DoMartForm2 extends BaseStepFragment implements Step, OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {
    private static final String TAG = makeLogTag(DoMartForm2.class);
    VerificationError invalid = null;
    ProgressDialog progressBar;
    Context context;
    @Bind(R.id.tvTotalPrice)
    TextView tvTotalPrice;

    @Bind(R.id.chkAgrement)
    CheckBox chkAgrement;
    @Bind(R.id.ivAgrement)
    ImageView ivAgrement;
    @Bind(R.id.list)
    RecyclerView recyclerView;
    boolean inDoSendCoverageArea = true;
    boolean inDoMoveCoverageArea = true;
    TUser origin;
    DoMartViewAdapter martAdapter;
    List<DoMart> doMartList;

    @Bind(R.id.orderInfo_layout)
    protected LinearLayout orderLayout;
    @Bind(R.id.service_layout)
    protected LinearLayout serviceLayout;
    @Bind(R.id.ivSwitchInfo)
    protected ImageView ivSwitchInfo;
    BigDecimal tarif = new BigDecimal(0);

    final Handler handler = new Handler() {
        @Override
        public void handleMessage(Message mesg) {
            throw new RuntimeException();
        }
    };
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflateAndBind(inflater, container, R.layout.fragment_domart2);

        context = getContext();
        progressBar = new ProgressDialogCustom(context);

        return v;
    }

    @Override
    public int getName() {
        return 0;
    }
    public boolean validate(){
        boolean valid = true;

        if(valid){
            if(!chkAgrement.isChecked()) {
                invalid = new VerificationError("Anda belum membaca dan menyetujui syarat dan ketentuan kami.");
                valid = false;
                return valid;
            }
        }
        return valid;
    }
    @Override
    public VerificationError verifyStep() {

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
        showConfirmationDialog("Konfirmasi Pesanan","Konfirmasi, Data yang Anda masukkan sudah benar?\nAnda akan menggunakan layanan "+ AppConfig.KEY_DOMART+". ", YesClickListener, null);

        // loop till a runtime exception is triggered.
        try { Looper.loop(); }
        catch(RuntimeException e2) { }

        return null;
    }

    private void place_an_order(Handler handler) {
        LogUtil.logD(TAG, "place_an_order");

        progressBar.setMessage("Sedang memproses Pesanan....");
        progressBar.show();

        GsonBuilder builder = new GsonBuilder();
        builder.setPrettyPrinting(); builder.serializeNulls();
        Gson gson = builder.create();

        Map<String, String> params = new HashMap<>();
        params.put("user_agent", AppConfig.USER_AGENT);
        TOrder order = DoMartHelper.getInstance().getOrder();
        order.setTotalPrice(order.getTotalPrice().add(tarif));
        doMartList.remove(0);
        Set<DoMart> sets = new LinkedHashSet<DoMart>();
        sets.addAll(doMartList);
        order.setMarts(sets);

        String orderStr = gson.toJson(order);
        LogUtil.logD(TAG, "place_an_order: "+orderStr);
        params.put("order", orderStr);
        process_order(params, handler);
    }
    private void process_order(Map<String, String> params, final Handler handler) {
        String url = AppConfig.URL_DOSEND_ORDER;
        addRequest("request_domart_order", Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                LogUtil.logD(TAG, "request_domart_order Response: " + response.toString());
                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean OK = "OK".equalsIgnoreCase(jObj.getString("status"));
                    if(OK){
                        String awb = jObj.getString("awb");
                        String status = jObj.getString("order_status");
                        String statusText = jObj.getString("order_status_text");

                        DoMartHelper.getInstance().getOrder().setAwb(awb);
                        DoMartHelper.getInstance().getOrder().setStatus(status);
                        DoMartHelper.getInstance().getOrder().setStatusText(statusText);

                        ViewHelper.getInstance().setOrder(DoMartHelper.getInstance().getOrder());

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
        ivAgrement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopupWindow("Kurindo \nService Agreement", R.raw.snk_file, R.drawable.icon_syarat_ketentuan);
            }
        });

        setup_product();
        refreshMap();
    }

    private void setup_product() {
        recyclerView.setLayoutManager(new GridLayoutManager(context, 1));
        recyclerView.setHasFixedSize(true);
        doMartList = getDoMartList();
        martAdapter = new DoMartViewAdapter(context, doMartList);
        recyclerView.setAdapter(martAdapter);
        tvTotalPrice.setText(AppConfig.formatCurrency(DoMartHelper.getInstance().getOrder().getTotalPrice().setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue()));
    }
    public List<DoMart> getDoMartList(){
        List<DoMart> list = new ArrayList<>();
        list.add(new DoMart());
        list.addAll(DoMartHelper.getInstance().getServices());
        return list;
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
        ivSwitchInfo.setImageResource(show? R.drawable.ic_expand_more_black_18dp : R.drawable.ic_expand_less_black_18dp);
        recyclerView.setVisibility(show ? View.VISIBLE: View.GONE );
        serviceLayout.setVisibility(show ? View.GONE : View.VISIBLE );
    }
    public void handleNext(){
        int dist = 0;
        try{
            dist = Integer.parseInt(route.getDistance().getValue());
        }catch (Exception e){}
        if(dist > AppConfig.MAX_DOSEND_COVERAGE_KM ){
            if(dist > AppConfig.MAX_DOMOVE_COVERAGE_KM ){
                Toast.makeText(getContext(), "( " + route.getDistance().getText() + " ): Jarak terlalu jauh. Tidak ada layanan.", LENGTH_SHORT).show();
                inDoMoveCoverageArea = false;
            }else{
                //showErrorDialog("Distance Limited.", "Jarak terlalu jauh. Silahkan menggunakan jasa DO-MOVE.");
                Toast.makeText(getContext(), "( " + route.getDistance().getText() + " ): Jarak terlalu jauh. Tarif menggunakan jasa DO-MOVE.", LENGTH_SHORT).show();
                inDoSendCoverageArea = false;
            }
            //showOrderpanel(false);
        }else{
            DoMartHelper.getInstance().addRoute(route);
            requestprice();
        }
    }
    private void requestprice() {
        final TOrder order = DoMartHelper.getInstance().getOrder();
        GsonBuilder builder = new GsonBuilder();
        builder.setPrettyPrinting(); builder.serializeNulls();
        builder.excludeFieldsWithoutExposeAnnotation();
        Gson gson = builder.create();

        Address originAddr = DoSendHelper.getInstance().getOrigin().getAddress() ;
        Address destinationAddr = DoSendHelper.getInstance().getDestination().getAddress() ;
        HashMap<String, String> params = new HashMap();
        //params.put("origin", origin.getAddress().getKecamatan());//TODO json origin
        //params.put("destination", order.getPlace().getAddress().getKecamatan()); //TODO json destination
        params.put("origin", gson.toJson(originAddr));
        params.put("destination", gson.toJson(destinationAddr));
        params.put("distance", (route ==null || route.getDistance()==null? "1" : route.getDistance().getValue()));
        params.put("service_code", order.getService_code());
        params.put("do_type", order.getService_type());
        params.put("berat_kiriman", "1");
        params.put("volume", "0");

        addRequest("request_price_route", Request.Method.POST, AppConfig.URL_CALC_PRICE_KM, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    Log.d(TAG, "requestprice Response: " + response.toString());
                    JSONObject jObj = new JSONObject(response);
                    boolean OK = "OK".equalsIgnoreCase(jObj.getString("status"));
                    if(OK){
                        BigDecimal tariff = new BigDecimal( jObj.getDouble("tarif") );
                        //price += tariff.doubleValue();
                        route.setPrice(tariff.doubleValue());
                        BigDecimal distance = new BigDecimal((route ==null || route.getDistance()==null? "1" : route.getDistance().getValue()));
                        //Set<DoMart> services = DoMartHelper.getInstance().getServices();
                        //List<TUser> waypoints = new ArrayList<TUser>();
                        //for (DoMart shop : services) {
                        //    if(shop.getOrigin().equals(origin)) continue;
                        //    waypoints.add(shop.getOrigin());
                        //}
                        //DoMartHelper.getInstance().addPacket(origin, order.getPlace(), waypoints, tariff, distance);

                        /*Set<Route> routes = DoShopHelper.getInstance().getRoutes();
                        BigDecimal prices = new BigDecimal(0);
                        BigDecimal distances = new BigDecimal(0);
                        for(Route r : routes){
                            prices = prices.add(new BigDecimal(r.getCalculatePrice()));
                            distances = distances.add( new BigDecimal(r.getDistance().getValue())  );
                        }*/

                        //price = prices.doubleValue();
                        //tvPriceInfo.setText(AppConfig.formatCurrency(tariff));
                        //tvPriceInfo.setText(AppConfig.formatCurrency(prices.doubleValue()));
                        //tvDistanceInfo.setText("Harga ( "+AppConfig.formatKm(distances.doubleValue())+" ) : ");
                        BigDecimal total = order.getTotalPrice();
                        total = total.add(tariff);
                        tarif = tariff;
                        tvTotalPrice.setText("Total Biaya (Est.): "+AppConfig.formatCurrency(total.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue()));
                        doMartList.add(doSendFee(tariff, distance));
                        doMartList.add(doMartFee());
                        doMartList.add(doMartParkingFee());
                        martAdapter.notifyDataSetChanged();
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
                handler.handleMessage(null);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                volleyError.printStackTrace();
                handler.handleMessage(null);
            }
        }, params, getKurindoHeaders());

    }

    private DoMart doSendFee(BigDecimal tariff, BigDecimal distance) {
        DoMart doMart = new DoMart();
        doMart.setType(AppConfig.KEY_DOSEND);
        doMart.setNotes(AppConfig.KEY_DOSEND+" : Biaya Kirim (Jarak "+AppConfig.formatKm(distance.doubleValue())+")");
        doMart.setEstHarga(tariff.toString());
        doMart.setPrice_unit(tariff);
        doMart.setQty(1);
        doMart.setPrice(tariff);

        return doMart;
    }

    private DoMart doMartFee() {
        DoMart doMart = new DoMart();
        doMart.setType(AppConfig.KEY_CHARGE);
        TPrice price = DoMartHelper.getInstance().getPriceMaps().get("NORMAL");
        doMart.setNotes(AppConfig.KEY_CHARGE+" : Biaya Antri/Belanja\nTarif : "+(price==null? AppConfig.formatCurrency(AppConfig.DOMART_FEE.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue()) : AppConfig.formatCurrency(price.getPrice1().setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue()))+"/jam");
        doMart.setEstHarga("0");
        doMart.setPrice_unit(new BigDecimal(0));
        doMart.setQty(-1);
        doMart.setPrice(new BigDecimal(0));

        return doMart;
    }
    private DoMart doMartParkingFee() {
        DoMart doMart = new DoMart();
        doMart.setType(AppConfig.KEY_CHARGE);
        doMart.setNotes(AppConfig.KEY_CHARGE+" : Parkir");
        doMart.setEstHarga("0");
        doMart.setPrice_unit(new BigDecimal(0));
        doMart.setQty(-1);
        doMart.setPrice(new BigDecimal(0));

        return doMart;
    }

    private void reDrawRoute() {
        DoMartHelper.getInstance().clearRoutes();
        Response.Listener responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                LogUtil.logD(TAG, "drawRoute Response: " + response.toString());
                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean OK = "OK".equalsIgnoreCase(jObj.getString("status"));
                    if(OK){
                        DataParser parser = new DataParser();
                        route = parser.parseRoutes(jObj);
                        //route.setOrigin(origin);
                        //route.setDestination(destination);
                        handleNext();
                        drawRoute();
                    }
                }catch (Exception e){
                    e.printStackTrace();
                    handler.handleMessage(null);
                }
            }
        };
        Response.ErrorListener responseErrorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Log.e(TAG, "request_direction_route Error: " + volleyError.getMessage());
                volleyError.printStackTrace();
                handler.handleMessage(null);
            }
        };

        Set<DoMart> services = DoMartHelper.getInstance().getServices();
        //LatLng origin = null;
        Set<LatLng> waypoints = new LinkedHashSet<>();
        for (DoMart shop : services) {
            if(shop.getOrigin() != null && shop.getOrigin().getAddress() != null && origin == null){
                origin = shop.getOrigin();
            }else{
                waypoints.add(shop.getOrigin().getAddress().getLocation());
            }
        }
        String url = "";
        if(waypoints.size() > 0){
            url = MapUtils.getDirectionUrl(origin.getAddress().getLocation(), DoMartHelper.getInstance().getOrder().getPlace().getAddress().getLocation(), waypoints);

        }else{
            url = MapUtils.getDirectionUrl(origin.getAddress().getLocation(), DoMartHelper.getInstance().getOrder().getPlace().getAddress().getLocation());
        }
        addRequest("request_direction_route", Request.Method.GET, url, responseListener, responseErrorListener , null, null);
        try { Looper.loop(); }
        catch(RuntimeException e2) { }
    }
    public void drawRoute(){
        if(route != null){
            //DoShopHelper.getInstance().setPacketRoute(origin, destination);

            DataParser parser = new DataParser();
            Set<Route> routes = DoMartHelper.getInstance().getRoutes();
            for(Route r : routes){
                PolylineOptions lineOptions = parser.drawRoutes(r.getRoutes());

                // Drawing polyline in the Google Map for the i-th route
                if(lineOptions != null) {
                    mMap.addPolyline(lineOptions);
                }else {
                    Log.d("drawRoutes","without Polylines drawn");
                }
            }

            //String snippet = originMarker.getSnippet();
            //originMarker.setSnippet(snippet + "\nEst. Distance :"+ route.getDistance().getText());

            LatLngBounds.Builder builder = new LatLngBounds.Builder();
            /*
            List<LatLng> points = lineOptions.getPoints();
            for (int i = 0; i < points.size(); i++) {
                builder.include(points.get(i));
            }*/

            builder.include(DoMartHelper.getInstance().getOrder().getPlace().getAddress().getLocation());
            Set<DoMart> services = DoMartHelper.getInstance().getServices();
            for(DoMart mart : services){
                if(mart.getOrigin() != null && mart.getOrigin().getAddress()!=null){
                    builder.include(mart.getOrigin().getAddress().getLocation());
                }
            }

            LatLngBounds bounds = builder.build();

            int width = getResources().getDisplayMetrics().widthPixels;
            int padding = (int) (width * 0.40); // offset from edges of the map 12% of screen

            CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
            mMap.animateCamera(cu);
            //moveCameraToLocation(bounds.getCenter(), 16f);

        }
    }
    private void showMap() {
        reDrawMarker();
        drawRoute();
    }
    private void refreshMap() {
        reDrawMarker();
        reDrawRoute();
    }
    public void reDrawMarker(){
        mMap.clear();
        Set<DoMart> services = DoMartHelper.getInstance().getServices();
        if(services != null && services.size() > 0){
            int i = 1;
            for(DoMart mart : services){
                if(mart.getOrigin() != null && mart.getOrigin().getAddress()!=null){
                    String title = "Toko "+(mart.getOrigin().getAddress().getNotes()==null? (char) ('0' + i++):mart.getOrigin().getAddress().getNotes());
                    String snippet = (mart.getOrigin().getName() != null ? mart.getOrigin().toStringFormatted() : "");
                    originMarker = mMap.addMarker(
                            new MarkerOptions()
                                    .position(mart.getOrigin().getAddress().getLocation())
                                    .title(title)
                                    .snippet(snippet)
                                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.origin_pin)));

                }
            }
        }
        TOrder order = DoMartHelper.getInstance().getOrder();
        if(order!= null){
            String title = "Tujuan "+(order.getPlace()==null || order.getPlace().getAddress() == null ||order.getPlace().getAddress().getNotes()==null?"":order.getPlace().getAddress().getNotes());
            String snippet = (order.getPlace().getName() != null ? order.getPlace().toStringFormatted() : "");
            destinationMarker = mMap.addMarker(
                    new MarkerOptions()
                            .position(order.getPlace().getAddress().getLocation())
                            .title(title)
                            .snippet(snippet)
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.destination_pin)));
        }

    }
    @Override
    public void onError(@NonNull VerificationError error) {

    }

    public GoogleMap mMap;
    public GoogleApiClient mGoogleApiClient;
    protected LatLng mCenterLatLong;
    protected Location mLastLocation;
    protected Marker originMarker;
    protected Marker destinationMarker;
    public SupportMapFragment mapFragment;
    private static final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION= 2;
    private static final int MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION= 3;
    protected final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    Route route;

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
    public void onMapReady(GoogleMap googleMap) {
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
                LogUtil.logD("Camera position change" + "", cameraPosition + "");
                mCenterLatLong = cameraPosition.target;

                //mMap.clear();

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

    private boolean canDrawRoute() {
        return true;
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
            LogUtil.logD(TAG, "ON connected ");

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
        LogUtil.logI(TAG, "Connection suspended");
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

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

        LogUtil.logD(TAG, "Reaching map" + mMap);

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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
            Toast.makeText(context,
                    "Sorry! unable to create maps", LENGTH_SHORT)
                    .show();
        }

    }
    public void moveCameraToLocation(LatLng location){
        moveCameraToLocation(location, AppConfig.DEFAULT_ZOOM_MAP);
    }

    public void moveCameraToLocation(LatLng location, float zoom){
        CameraPosition cameraPosition = new CameraPosition.Builder().target(location).zoom(zoom).tilt(AppConfig.DEFAULT_TILT_MAP).build();
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }
}
