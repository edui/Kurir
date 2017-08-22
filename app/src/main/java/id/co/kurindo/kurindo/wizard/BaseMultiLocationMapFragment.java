package id.co.kurindo.kurindo.wizard;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
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
import com.lamudi.phonefield.PhoneInputLayout;
import com.stepstone.stepper.Step;
import com.stepstone.stepper.VerificationError;

import org.json.JSONArray;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;
import id.co.kurindo.kurindo.R;
import id.co.kurindo.kurindo.adapter.DestinationAdapter;
import id.co.kurindo.kurindo.adapter.PacketServiceAdapter;
import id.co.kurindo.kurindo.adapter.PaymentAdapter;
import id.co.kurindo.kurindo.adapter.TUserAdapter;
import id.co.kurindo.kurindo.app.AppConfig;
import id.co.kurindo.kurindo.base.RecyclerItemClickListener;
import id.co.kurindo.kurindo.comp.ProgressDialogCustom;
import id.co.kurindo.kurindo.helper.DoSendHelper;
import id.co.kurindo.kurindo.map.DataParser;
import id.co.kurindo.kurindo.map.MapUtils;
import id.co.kurindo.kurindo.map.PlaceArrayAdapter;
import id.co.kurindo.kurindo.model.Address;
import id.co.kurindo.kurindo.model.PacketService;
import id.co.kurindo.kurindo.model.Payment;
import id.co.kurindo.kurindo.model.Route;
import id.co.kurindo.kurindo.model.TUser;
import id.co.kurindo.kurindo.util.LogUtil;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;
import static android.widget.Toast.LENGTH_SHORT;

/**
 * Created by DwiM on 4/7/2017.
 */

public class BaseMultiLocationMapFragment
        extends BaseStepFragment
        implements Step, OnMapReadyCallback, View.OnClickListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    protected  GoogleMap mMap;
    protected  GoogleApiClient mGoogleApiClient;
    protected  final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    protected  static String TAG = "BaseLocationMapFragment";
    protected Context mContext;
    protected SupportMapFragment mapFragment;

    @Bind(R.id.container_toolbar)
    protected LinearLayout containerToolbarLayout;


    @Bind(R.id.locationMarkertext)
    protected TextView mLocationMarkerText;
    private LatLng mCenterLatLong;
    @Bind(R.id.locationMarker)
    protected LinearLayout locationMarkerLayout;

    private static final int REQUEST_CODE_AUTOCOMPLETE = 1;
    private static final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION= 2;
    private static final int MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION= 3;
    private static final LatLngBounds BOUNDS_ID = new LatLngBounds(new LatLng(-0, 0), new LatLng(0, 0));

    @Bind(R.id.iconOrigin)
    protected ImageView ivIconOrigin;
    @Bind(R.id.iconDestination)
    protected ImageView ivIconDestination;
    protected TUserAdapter tUserAdapter;
    protected ArrayList<TUser> data = new ArrayList<>();

    @Bind(R.id.tvOrigin)
    protected TextView tvOrigin;
    @Bind(R.id.tvDestination)
    protected TextView tvDestination;
    @Bind(R.id.tvDistanceInfo)
    protected TextView tvDistanceInfo;
    @Bind(R.id.tvPriceInfo)
    protected TextView tvPriceInfo;

    protected DestinationAdapter destinationAdapter;
    private List<TUser> destinations = new ArrayList<>();
    @Bind(R.id.destinationList)
    protected RecyclerView destinationList;

    @Bind(R.id.btnAddDestination)
    protected ImageButton btnAddDestination;
    @Bind(R.id.tvAddDestination)
    protected TextView tvAddDestination;

    @Bind(R.id.btnDoneDestination)
    protected ImageButton btnDoneDestination;
    @Bind(R.id.tvDoneDestination)
    protected TextView tvDoneDestination;

    @Bind(R.id.btnSaveDestination)
    protected ImageButton btnSaveDestination;
    @Bind(R.id.tvSaveDestination)
    protected TextView tvSaveDestination;
    @Bind(R.id.btnCancelDestination)
    protected ImageButton btnCancelDestination;
    @Bind(R.id.tvCancelDestination)
    protected TextView tvCancelDestination;

    @Bind(R.id.addLayout)
    protected LinearLayout addLayout;
    @Bind(R.id.doneLayout)
    protected LinearLayout doneLayout;
    @Bind(R.id.saveLayout)
    protected LinearLayout saveLayout;
    @Bind(R.id.cancelLayout)
    protected LinearLayout cancelLayout;
    boolean editMode = false;

    @Bind(R.id.imageMarker)
    protected ImageView imageMarker;
    @Bind(R.id.ivAddOriginNotes)
    protected ImageView ivAddOriginNotes;
    @Bind(R.id.ivAddDestinationNotes)
    protected ImageView ivAddDestinationNotes;

    @Bind(R.id.etOriginNotes)
    protected EditText etOriginNotes;

    @Bind(R.id.etDestinationNotes)
    protected EditText etDestinationNotes;

    @Bind(R.id.etNamaPenerima)
    protected EditText etNamaPenerima;
    @Bind(R.id.etTeleponPenerima)
    protected PhoneInputLayout etTeleponPenerima;

    @Bind(R.id.destination_layout)
    protected LinearLayout destinationLayout;
    @Bind(R.id.origin_layout)
    protected LinearLayout originLayout;

    @Bind(R.id.orderInfo_layout)
    protected LinearLayout orderLayout;
    @Bind(R.id.service_layout)
    protected LinearLayout serviceLayout;
    @Bind(R.id.ivSwitchInfo)
    protected ImageView ivSwitchInfo;

    protected boolean originMode = true;
    protected boolean destinationMode;
    protected boolean addOriginNote;
    protected boolean addDestinationNote;

    @Bind(R.id.tvOriginAutoComplete)
    protected AutoCompleteTextView mOriginAutoCompleteTextView;
    @Bind(R.id.tvDestinationAutoComplete)
    protected AutoCompleteTextView mDestinationAutoCompleteTextView;
    protected PlaceArrayAdapter mPlaceArrayAdapter;

    protected Route route;
    protected TUser tempUserAddress;
    protected TUser origin = new TUser();
    protected TUser destination = new TUser();
    protected Location mLastLocation;
    protected Marker originMarker;
    protected Marker destinationMarker;

    //@Bind(R.id.ButtonAddOrder)
    //AppCompatButton buttonAddOrder;

    @Bind(R.id.rvPayment)
    protected RecyclerView rvPayment;
    protected PaymentAdapter paymentAdapter;
    protected Payment payment;

    @Bind(R.id.rgDoType)
    protected RadioGroup rgDoType;
    @Bind(R.id.radio_dojek)
    protected RadioButton radioDoJek;
    @Bind(R.id.radio_dosend)
    protected RadioButton radioDoSend;
    @Bind(R.id.radio_domove)
    protected RadioButton radioDoMove;
    @Bind(R.id.radio_docar)
    protected RadioButton radioDoCar;

    protected String doType = AppConfig.KEY_DOSEND;
    protected String serviceCode = AppConfig.PACKET_SDS;
    protected double price = 0;
    protected float beratKiriman = 0;
    protected float volume = 0;

    protected boolean updatePlace = false;

    @Bind(R.id.input_service_code)
    protected Spinner _serviceCodeText;
    protected PacketServiceAdapter packetServiceAdapter;

    protected boolean inDoSendCoverageArea = true;
    protected boolean inDoMoveCoverageArea = true;
    protected boolean inDoJekCoverageArea = true;
    protected boolean inDoCarCoverageArea = true;

    protected ProgressDialog progressBar;
    
    public int getLayout() {
        return R.layout.fragment_maps_dosend_multi;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflateAndBind(inflater, container, getLayout());
        if (DoSendHelper.getInstance().getPacket() != null && DoSendHelper.getInstance().getPacket().getOrigin() != null) {
            origin = DoSendHelper.getInstance().getPacket().getOrigin();
            destination = DoSendHelper.getInstance().getPacket().getDestination();
        }
        mContext = getContext();
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        //SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        //mapFragment.getMapAsync(this);
        //buildGoogleApiClient();

        initialize(view);

        progressBar = new ProgressDialogCustom(mContext);

        showAddressLayout();

        tUserAdapter = new TUserAdapter(mContext, data);

        mPlaceArrayAdapter = new PlaceArrayAdapter(mContext, android.R.layout.simple_list_item_1, BOUNDS_ID, null);
        mOriginAutoCompleteTextView.setAdapter(mPlaceArrayAdapter);
        mDestinationAutoCompleteTextView.setAdapter(mPlaceArrayAdapter);

        AdapterView.OnItemClickListener adapterOnItemClik = new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final PlaceArrayAdapter.PlaceAutocomplete item = mPlaceArrayAdapter.getItem(position);
                final String placeId = String.valueOf(item.placeId);
                LogUtil.logI("", "Selected: " + item.description);
                PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi.getPlaceById(mGoogleApiClient, placeId);
                placeResult.setResultCallback(mUpdatePlaceDetailsCallback);
                LogUtil.logI("", "Fetching details for ID: " + item.placeId);
            }
        };
        mOriginAutoCompleteTextView.setOnItemClickListener(adapterOnItemClik);
        mDestinationAutoCompleteTextView.setOnItemClickListener(adapterOnItemClik);
        paymentAdapter = new PaymentAdapter(mContext, getPaymentData(), new PaymentAdapter.OnItemClickListener() {
            @Override
            public void onActionButtonClick(View view, int position) {
                Payment p = paymentAdapter.getItem(position);
                if (p.getAction().equalsIgnoreCase(AppConfig.ISI_SALDO)) {
                    Toast.makeText(mContext, "TODO : " + AppConfig.ISI_SALDO, LENGTH_SHORT).show();
                }
            }
        });
        paymentAdapter.selected(1);
        payment = paymentAdapter.getItem(1);
        rvPayment.setAdapter(paymentAdapter);
        rvPayment.setLayoutManager(new GridLayoutManager(mContext, 1) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        });
        rvPayment.setHasFixedSize(true);
        rvPayment.addOnItemTouchListener(new RecyclerItemClickListener(mContext, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Payment selected = paymentAdapter.getItem(position);
                if (selected.getCredit() == 0) {
                    Toast.makeText(mContext, "Saldo tidak mencukupi. Silahkan isi ulang saldo anda.", LENGTH_SHORT).show();
                    position = paymentAdapter.getSelectedPosition();
                }
                paymentAdapter.selected(position);
                payment = paymentAdapter.getItem(position);
            }
        }));

        rgDoType.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                rgDoType_onCheckedChanged(group, checkedId);
            }
        });

        packetServiceAdapter = new PacketServiceAdapter(mContext, AppConfig.getPacketServiceList(doType), 1);
        _serviceCodeText.setAdapter(packetServiceAdapter);
        _serviceCodeText.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                serviceCode = ((PacketService) parent.getItemAtPosition(position)).getCode();
                cek_with_calendar_time();
                if (canDrawRoute()) requestprice();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        destinationAdapter = new DestinationAdapter(mContext, destinations, new DestinationAdapter.OnItemClickListener() {
            @Override
            public void onEditButtonClick(View view, int position) {
                TUser user = destinations.get(position);
                etNamaPenerima.setText(user.getName());
                etTeleponPenerima.setPhoneNumber(user.getPhone());
                etDestinationNotes.setText(user.getAddress().getNotes());
                showAddressLayout();
                showHideDestinationNotes(true);
                showMultiButton(false);
            }

            @Override
            public void onDeleteButtonClick(View view, int position) {
                //TUser user = destinations.get(position);
                etNamaPenerima.setText(null);
                etTeleponPenerima.setPhoneNumber("");
                etDestinationNotes.setText(null);

                destinations.remove(position);
                destinationAdapter.notifyDataSetChanged();
            }
        });
        destinationList.setAdapter(destinationAdapter);
        destinationList.setHasFixedSize(true);
        destinationList.setLayoutManager(new GridLayoutManager(mContext, 1));

        etTeleponPenerima.setDefaultCountry(AppConfig.DEFAULT_COUNTRY);
        etTeleponPenerima.setHint(R.string.telepon_penerima);

        afterOnCreateView();
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
        cek_with_calendar_time();
        //_serviceCodeText.setSelection(1);
        return view;

    }

    protected void rgDoType_onCheckedChanged(RadioGroup group, int checkedId) {
        switch (group.getCheckedRadioButtonId()) {
            case R.id.radio_dosend:
                doType = AppConfig.KEY_DOSEND;
                break;
            case R.id.radio_dojek:
                doType = AppConfig.KEY_DOJEK;
                break;
            case R.id.radio_domove:
                doType = AppConfig.KEY_DOMOVE;
                break;
            case R.id.radio_docar:
                doType = AppConfig.KEY_DOCAR;
                break;
        }
        if (canDrawRoute()) requestprice();
    }


    int hour;
    int minute;
    private void cek_with_calendar_time() {
        Calendar c = Calendar.getInstance();
        hour = c.get(Calendar.HOUR_OF_DAY);
        minute = c.get(Calendar.MINUTE);
        cek_time();
    }

    private void cek_time() {
        if(serviceCode != null){
            if(AppConfig.isNightService(serviceCode)){
                if(hour+1 >= AppConfig.START_ENS) {
                    hour++;
                } else if(hour+1 >= AppConfig.END_ENS) {
                    hour = AppConfig.START_SDS +2;
                    _serviceCodeText.setSelection(2); //nds
                } else {
                    hour = AppConfig.START_ENS;
                }
            }else if(serviceCode.equalsIgnoreCase(AppConfig.PACKET_SDS)){
                if(hour + 1 < AppConfig.START_SDS) {
                    hour = AppConfig.START_SDS +2;
                    _serviceCodeText.setSelection(0); //sds
                } else if(hour+1 >= AppConfig.END_ENS) {
                    hour = AppConfig.START_SDS +2;
                    _serviceCodeText.setSelection(2); //nds
                } else if(hour+1 >= AppConfig.START_ENS) {
                    hour++;
                    _serviceCodeText.setSelection(2); //nds
                }else{
                    hour++;
                    _serviceCodeText.setSelection(0); //sds
                }
            }else if(serviceCode.equalsIgnoreCase(AppConfig.PACKET_NDS)){
                if(hour + 1 < AppConfig.START_SDS) {
                    hour = AppConfig.START_SDS +2;
                    _serviceCodeText.setSelection(2); //nds
                } else if(hour+1 >= AppConfig.END_ENS) {
                    hour = AppConfig.START_SDS +2;
                } else if(hour+1 >= AppConfig.START_ENS) {
                    hour = AppConfig.START_SDS +2;
                    //hour++;
                    //_serviceCodeText.setSelection(3);
                }
            }
        }
    }

    private void initialize(View view) {

        if(tvOrigin == null){
            mLocationMarkerText = (TextView) view.findViewById(R.id.locationMarkertext);
            mLocationMarkerText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onClick_mLocationMarkerText();
                }
            });
            locationMarkerLayout = (LinearLayout) view.findViewById(R.id.locationMarker);
            ivIconOrigin = (ImageView) view.findViewById(R.id.iconOrigin);
            ivIconOrigin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onClick_IconOrigin();
                }
            });
            ivIconDestination = (ImageView) view.findViewById(R.id.iconDestination);
            ivIconDestination.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onClick_IconDestination();
                }
            });

            tvOrigin = (TextView) view.findViewById(R.id.tvOrigin);
            tvOrigin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onClick_tvOrigin();
                }
            });
            tvDestination = (TextView) view.findViewById(R.id.tvDestination);
            tvDestination.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onClick_tvDestination();
                }
            });
            tvDistanceInfo = (TextView) view.findViewById(R.id.tvDistanceInfo);
            tvPriceInfo = (TextView) view.findViewById(R.id.tvPriceInfo);

            imageMarker = (ImageView) view.findViewById(R.id.imageMarker);
            ivAddOriginNotes = (ImageView) view.findViewById(R.id.ivAddOriginNotes);
            ivAddOriginNotes.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onClick_ivAddOriginNotes();
                }
            });
            ivAddDestinationNotes = (ImageView) view.findViewById(R.id.ivAddDestinationNotes);
            ivAddDestinationNotes.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onClick_ivAddDestinationNotes();
                }
            });

            etOriginNotes = (EditText) view.findViewById(R.id.etOriginNotes);
            etDestinationNotes = (EditText) view.findViewById(R.id.etDestinationNotes);
            etNamaPenerima= (EditText) view.findViewById(R.id.etNamaPenerima);
            etTeleponPenerima = (PhoneInputLayout) view.findViewById(R.id.etTeleponPenerima);

            destinationLayout = (LinearLayout) view.findViewById(R.id.destination_layout);
            originLayout = (LinearLayout) view.findViewById(R.id.origin_layout);
            orderLayout = (LinearLayout) view.findViewById(R.id.orderInfo_layout);
            serviceLayout = (LinearLayout) view.findViewById(R.id.service_layout);
            ivSwitchInfo = (ImageView) view.findViewById(R.id.ivSwitchInfo);
            ivSwitchInfo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    OnClick_ivSwitchInfo();
                }
            });
            mOriginAutoCompleteTextView = (AutoCompleteTextView) view.findViewById(R.id.tvOriginAutoComplete);
            mDestinationAutoCompleteTextView = (AutoCompleteTextView) view.findViewById(R.id.tvDestinationAutoComplete);
            rvPayment = (RecyclerView) view.findViewById(R.id.rvPayment);
            rgDoType = (RadioGroup) view.findViewById(R.id.rgDoType);
            radioDoJek = (RadioButton) view.findViewById(R.id.radio_dojek);
            radioDoSend = (RadioButton) view.findViewById(R.id.radio_dosend);
            radioDoMove = (RadioButton) view.findViewById(R.id.radio_domove);
            radioDoCar = (RadioButton) view.findViewById(R.id.radio_docar);
            _serviceCodeText = (Spinner) view.findViewById(R.id.input_service_code);
        }

    }

    protected void afterOnCreateView() {

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

    private List<Payment> getPaymentData() {
        List<Payment> data = new ArrayList<>();
        Payment payment = new Payment("DO-PAY (Rp 0)", "Get 20% discount by  using DO-PAY", AppConfig.ISI_SALDO);
        //payment.setCredit(10000);
        data.add(payment);
        payment = new Payment("Tunai");
        payment.setCredit(1);
        data.add(payment);
        return data;
    }

    private ResultCallback<PlaceBuffer> mUpdatePlaceDetailsCallback
            = new ResultCallback<PlaceBuffer>() {
        @Override
        public void onResult(PlaceBuffer places) {
            if (!places.getStatus().isSuccess()) {
                LogUtil.logE("ResultCallback", "Place query did not complete. Error: " +places.getStatus().toString());
                return;
            }

            progressBar.show();

            // Selecting the first object buffer.
            final Place place = places.get(0);
            CharSequence attributions = places.getAttributions();

            String name = place.getName().toString();
            String address = place.getAddress().toString();
            updatePlace = true;
            if(originMode){
                tempUserAddress = origin;
                tvOrigin.setText(address);
                etOriginNotes.setText(name);
                etOriginNotes.setFocusable(false);
                etOriginNotes.setVisibility(View.VISIBLE);
                addOriginNote = true;
                requestAddress(place.getLatLng());

                Address addr = origin.getAddress();
                addr.setAlamat(name);
                //addr.setFormattedAddress(address);
                addr.setLocation( place.getLatLng());
                //if(destination.getAddress().getLocation() != null) originMode = false;
            }
            if(destinationMode){
                tempUserAddress = origin;
                tvDestination.setText(address);
                etDestinationNotes.setText(name);
                etDestinationNotes.setFocusable(false);
                etDestinationNotes.setVisibility(View.VISIBLE);
                etNamaPenerima.setVisibility(View.VISIBLE);
                etTeleponPenerima.setVisibility(View.VISIBLE);
                addDestinationNote= true;

                requestAddress(place.getLatLng());

                Address addr = destination.getAddress();
                addr.setAlamat(name);
                //addr.setFormattedAddress(address);
                addr.setLocation( place.getLatLng() );
                //if(origin.getAddress().getLocation() != null) destinationMode = false;
                onClick_ivAddDestinationNotes();
            }
            inDoSendCoverageArea = true;
            inDoMoveCoverageArea = true;
            //showAddressLayout();
            //refreshMap();
            reDrawMarker();
            //if(!canDrawRoute())
            moveCameraToLocation( place.getLatLng());
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            progressBar.dismiss();

        }
    };

    protected boolean canDrawRoute() {
        return (origin.getAddress().getLocation() != null && destination.getAddress().getLocation() != null);
    }

    public void resetAll() {
        route = null;

        origin = new TUser();
        destination = new TUser();
        DoSendHelper.getInstance().clearAll();
    }

    public boolean handleBackPressed(){
        /*
        if(originMode){
            originMode = false;
            showAddressLayout();
            if(tempUserAddress != null) {
                origin = (route!= null && route.getOrigin() != null? route.getOrigin(): tempUserAddress);
                tempUserAddress = null;
            }
            //if(!canDrawRoute()){
                if(origin != null && origin.getAddress().getLocation() != null)
                    moveCameraToLocation(origin.getAddress().getLocation());
                showMap();
                return true;
            //}
        }
        if(destinationMode){
            destinationMode = false;
            showAddressLayout();
            if(tempUserAddress != null) {
                destination = (route != null && route.getDestination() != null? route.getDestination() : tempUserAddress);
                tempUserAddress = null;
            }
            //if(!canDrawRoute()){
                if(destination.getAddress()==null || destination.getAddress().getLocation() == null) moveCameraToLocation(origin.getAddress().getLocation());
                else moveCameraToLocation(destination.getAddress().getLocation());
                showMap();

                return true;
            //}
        }
        if(canDrawRoute()){
            resetDestination();
            showMap();
            showAddressLayout();
            moveCameraToLocation(origin.getAddress().getLocation());
            return true;
        }
        resetAll();
        */
        return false;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().finish();
    }

    private void resetDestination(){
        destination = new TUser();
        route = null;
        tvDestination.setText("Set Tujuan Anda");
        etDestinationNotes.setText("");
        etDestinationNotes.setVisibility(View.GONE);
        etNamaPenerima.setText("");
        etNamaPenerima.setVisibility(View.GONE);
        etTeleponPenerima.setPhoneNumber("");
        etTeleponPenerima.setVisibility(View.GONE);
        destinationMode = true;
        //buttonAddOrder.setVisibility(View.GONE);
        tvPriceInfo.setText(AppConfig.formatCurrency(0));
        tvDistanceInfo.setText("Harga ( ~ ) : ");

    }
    private void showAddressLayout() {
        tvOrigin.setVisibility(View.VISIBLE);
        ivAddOriginNotes.setVisibility(View.VISIBLE);
        mOriginAutoCompleteTextView.setVisibility(View.GONE);
        originLayout.setVisibility(View.VISIBLE);

        destinationLayout.setVisibility(View.VISIBLE);
        tvDestination.setVisibility(View.VISIBLE);
        ivAddDestinationNotes.setVisibility(View.VISIBLE);
        mDestinationAutoCompleteTextView.setVisibility(View.GONE);
        locationMarkerLayout.setVisibility(View.GONE);
        showDestinationList(false);
    }

    public void showDestinationList(boolean show){
        destinationList.setVisibility(show? View.VISIBLE : View.GONE);
        destinationLayout.setVisibility(show?View.GONE:View.VISIBLE);
        if(editMode)
            showMultiButton(show);
        else
            hideMultiButton();
    }

    public void hideMultiButton(){
        addLayout.setVisibility(View.GONE);
        doneLayout.setVisibility(View.GONE );
        saveLayout.setVisibility(View.GONE );
        cancelLayout.setVisibility(View.GONE );
    }
    public void showMultiButton(boolean show){
        addLayout.setVisibility(show?View.VISIBLE:View.GONE);
        doneLayout.setVisibility(show?View.VISIBLE:View.GONE);
        saveLayout.setVisibility(show?View.GONE : View.VISIBLE);
        cancelLayout.setVisibility(show?View.GONE : View.VISIBLE);
    }
    @OnClick(R.id.btnAddDestination)
    public void onBtnAddDestination(){
        destination = new TUser();
        onClick_tvDestination();
        showDestinationList(false);
        showMultiButton(false);
    }

    @OnClick(R.id.btnCancelDestination)
    public void onBtnCancelDestination(){
        destination = new TUser();
        showDestinationList(true);
        showMultiButton(true);
    }

    @OnClick(R.id.btnSaveDestination)
    public void onBtnSaveDestination(){
        if(destination == null || destination.getAddress() == null){
            Toast.makeText(mContext, " Set Tujuan Anda dulu.", LENGTH_SHORT).show();
            return;
        }
        String nama = etNamaPenerima.getText().toString();
        if(nama == null || nama.isEmpty()){
            Toast.makeText(mContext, " Nama Penerima tidak valid.", LENGTH_SHORT).show();
            return;
        }
        if(!etTeleponPenerima.isValid()){
            Toast.makeText(mContext, " Telepon Penerima tidak valid.", LENGTH_SHORT).show();
            return;
        }
        destination.setPhone(etTeleponPenerima.getPhoneNumber());
        destination.setFirstname(etNamaPenerima.getText().toString());
        destination.getAddress().setNotes(etDestinationNotes.getText().toString());
        destinations.add(destination);
        tUserAdapter.notifyDataSetChanged();
        showDestinationList(true);
        ViewGroup.LayoutParams params_new = destinationList.getLayoutParams();
        params_new.height= (params_new.height < 0 ? 100 : params_new.height ) + 20;
        destinationList.setLayoutParams(params_new);
        etDestinationNotes.setText(null);
        etNamaPenerima.setText(null);
        etTeleponPenerima.setPhoneNumber("");
        editMode = false;
    }
    @OnClick(R.id.btnDoneDestination)
    public void onBtnDoneDestination(){
        refreshMap();
    }

    @OnClick(R.id.tvOrigin)
    public void onClick_tvOrigin(){
        originMode= !originMode;
        destinationMode = false;
        tvOrigin.setVisibility(originMode ? View.GONE : View.VISIBLE);
        ivAddOriginNotes.setVisibility(originMode ? View.GONE : View.VISIBLE);
        destinationLayout.setVisibility(originMode ? View.GONE : View.VISIBLE);
        locationMarkerLayout.setVisibility(View.VISIBLE);
        mOriginAutoCompleteTextView.setVisibility(originMode ? View.VISIBLE : View.GONE);
        mOriginAutoCompleteTextView.setText("");
        if(origin.getAddress().getLocation() != null) moveCameraToLocation(origin.getAddress().getLocation());
        changeMarkerIcon();
        hidepanel(true);
    }

    @OnClick(R.id.tvDestination)
    public void onClick_tvDestination(){
        destinationMode= true;
        originMode = false;
        tvDestination.setVisibility(destinationMode? View.GONE : View.VISIBLE);
        ivAddDestinationNotes.setVisibility(destinationMode? View.GONE : View.VISIBLE);
        originLayout.setVisibility(destinationMode ? View.GONE : View.VISIBLE);
        locationMarkerLayout.setVisibility(View.VISIBLE);
        mDestinationAutoCompleteTextView.setVisibility(destinationMode ? View.VISIBLE : View.GONE);
        mDestinationAutoCompleteTextView.setText("");
        if(destination.getAddress().getLocation()!= null) moveCameraToLocation(destination.getAddress().getLocation());
        editMode = true;
        changeMarkerIcon();
        hidepanel(true);
        showHideDestinationNotes(false);
        hideMultiButton();
    }
    private void showHideDestinationNotes(boolean show){
        etDestinationNotes.setVisibility(show? View.VISIBLE : View.GONE);
        etNamaPenerima.setVisibility(show? View.VISIBLE : View.GONE);
        etTeleponPenerima.setVisibility(show? View.VISIBLE : View.GONE);
        showMultiButton(!show);
    }

    @OnClick(R.id.ivAddOriginNotes)
    public void onClick_ivAddOriginNotes(){
        addOriginNote =!addOriginNote;
        etOriginNotes.setVisibility(addOriginNote? View.VISIBLE : View.GONE);
    }
    @OnClick(R.id.ivAddDestinationNotes)
    public void onClick_ivAddDestinationNotes(){
        addDestinationNote =!addDestinationNote;
        etDestinationNotes.setVisibility(addDestinationNote? View.VISIBLE : View.GONE);
        etNamaPenerima.setVisibility(addDestinationNote? View.VISIBLE : View.GONE);
        etTeleponPenerima.setVisibility(addDestinationNote? View.VISIBLE : View.GONE);
    }


    public void onClick(View v) {
        //TODO
    }

    @OnClick(R.id.iconOrigin)
    public void onClick_IconOrigin(){
        originMode = true;
        destinationMode = !originMode;
        showPopupWindow("Daftar Lokasi", R.drawable.origin_pin);

    }
    @OnClick(R.id.iconDestination)
    public void onClick_IconDestination(){
        destinationMode = true;
        originMode = !destinationMode;
        showPopupWindow("Daftar Lokasi", R.drawable.destination_pin);

    }

    @OnClick(R.id.locationMarkertext)
    public void onClick_mLocationMarkerText(){
        progressBar.show();
        if(locationDiff()){
            if(destinationMode){
                destination.getAddress().setLocation( new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude()) );
                //tvDestination.setText("Lat. "+ mLastLocation.getLatitude()+ ", "+mLastLocation.getLongitude());
                etDestinationNotes.setText(null);
                etDestinationNotes.setVisibility(View.GONE);
                onClick_ivAddDestinationNotes();
                showMultiButton(false);
                addDestinationNote= false;
            }else  if(originMode){
                origin.getAddress().setLocation( new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude()) );
                //tvOrigin.setText("Lat. "+ mLastLocation.getLatitude()+ ", "+mLastLocation.getLongitude());
                etOriginNotes.setText(null);
                etOriginNotes.setVisibility(View.GONE);
                addOriginNote= false;
            }
            requestAddress(new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude()));
        }
        inDoSendCoverageArea = true;
        inDoMoveCoverageArea = true;
        updatePlace = false;
        //destinationMode =false;
        originMode =false;
        tempUserAddress = null;
        mLocationMarkerText.setVisibility(View.GONE);

        showAddressLayout();
        reDrawMarker();
        //refreshMap();
        //startIntentService(mLastLocation);
        progressBar.dismiss();
    }

    private boolean locationDiff() {
        if(destinationMode){
            if(destination ==null || destination.getAddress() == null || destination.getAddress().getLocation() == null || mLastLocation == null) return true;
            BigDecimal a = BigDecimal.valueOf( destination.getAddress().getLocation().longitude ).setScale(4, BigDecimal.ROUND_HALF_UP);
            BigDecimal b = BigDecimal.valueOf( mLastLocation.getLongitude() ).setScale(4, BigDecimal.ROUND_HALF_UP);
            BigDecimal c = BigDecimal.valueOf( destination.getAddress().getLocation().latitude).setScale(4, BigDecimal.ROUND_HALF_UP);
            BigDecimal d = BigDecimal.valueOf( mLastLocation.getLatitude() ).setScale(4, BigDecimal.ROUND_HALF_UP);
            return !(a.equals(b) && c.equals(d));
        }
        if(originMode){
            if(origin == null || origin.getAddress() == null || origin.getAddress().getLocation() == null || mLastLocation == null ) return  true;
            BigDecimal a = BigDecimal.valueOf( origin.getAddress().getLocation().longitude ).setScale(4, BigDecimal.ROUND_HALF_UP);
            BigDecimal b = BigDecimal.valueOf( mLastLocation.getLongitude() ).setScale(4, BigDecimal.ROUND_HALF_UP);
            BigDecimal c = BigDecimal.valueOf( origin.getAddress().getLocation().latitude).setScale(4, BigDecimal.ROUND_HALF_UP);
            BigDecimal d = BigDecimal.valueOf( mLastLocation.getLatitude() ).setScale(4, BigDecimal.ROUND_HALF_UP);
            return !(a.equals(b) && c.equals(d));
        }
        return false;
    }

    @OnClick(R.id.ButtonAddOrder)
    public void onClick_buttonAddOrder(){
        String onotes = etOriginNotes.getText().toString();
        String dnotes = etDestinationNotes.getText().toString();
        origin.getAddress().setNotes(onotes);
        destination.getAddress().setNotes(dnotes);
        if( doType.equalsIgnoreCase(AppConfig.KEY_DOSEND)){
            DoSendHelper.getInstance().setPacketRoute(origin, destination);
            DoSendHelper.getInstance().addDoSendOrder(payment.getText(), serviceCode, route.getDistance().getValue(), price);
            //showActivity( DoSendOrderActivity.class );
            //finish();
        }else{
            Toast.makeText(mContext, doType+" : available soon.", LENGTH_SHORT).show();
        }
    }

    @OnClick(R.id.ivSwitchInfo)
    public void OnClick_ivSwitchInfo(){
        showOrderpanel(serviceLayout.isShown());
        orderLayout.setVisibility(View.VISIBLE );
        //serviceLayout.setMinimumHeight(100);
        //buttonAddOrder.setVisibility(View.VISIBLE );
    }
    private void showServicePanel(boolean show){
        showOrderpanel(!show);
        orderLayout.setVisibility(View.VISIBLE );
    }
    private void showOrderpanel(boolean show){
        orderLayout.setVisibility((show ? View.VISIBLE : View.GONE ));
        rvPayment.setVisibility((show ? View.VISIBLE : View.GONE ));
        ivSwitchInfo.setImageResource(show? R.drawable.ic_expand_less_black_18dp: R.drawable.ic_expand_more_black_18dp );
        serviceLayout.setVisibility(show ? View.GONE : View.VISIBLE );
    }

    private void hidepanel(boolean hide) {
        orderLayout.setVisibility((hide ? View.GONE : View.VISIBLE));
        rvPayment.setVisibility((hide ? View.GONE: View.VISIBLE));
        serviceLayout.setVisibility((hide ? View.GONE : View.VISIBLE));
        //buttonAddOrder.setVisibility((hide ? View.GONE : View.VISIBLE));
    }


    private void changeMarkerIcon(){
        locationMarkerLayout.setVisibility(View.VISIBLE);
        imageMarker.setImageResource(destinationMode ? R.drawable.destination_pin : R.drawable.origin_pin );
        mLocationMarkerText.setVisibility(View.VISIBLE);
        mLocationMarkerText.setText("Set "+(destinationMode? "Tujuan Anda" : "Lokasi Anda"));
    }
    public void moveCameraToLocation(LatLng location, float zoom){
        CameraPosition cameraPosition = new CameraPosition.Builder().target(location).zoom(zoom).tilt(AppConfig.DEFAULT_TILT_MAP).build();
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }
    public void moveCameraToLocation(LatLng location){
        moveCameraToLocation(location, AppConfig.DEFAULT_ZOOM_MAP);
    }
    private void showMap() {
        reDrawMarker();
        drawRoute();
    }
    private void refreshMap() {
        reDrawMarker();
        reDrawRoute();
    }

    private void requestAddress(LatLng latLng) {
        //progressBar.setVisibility(View.VISIBLE);
        final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message mesg) {
                throw new RuntimeException("RuntimeException");
            }
        };
        //requestAddress_inBackground(latLng);
        requestAddress(latLng, handler);
        try { Looper.loop(); } catch(RuntimeException e2) { }
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if(!updatePlace){
            originMode = false;
            destinationMode = false;
        }
        //progressBar.setVisibility(View.GONE);
    }

    private void requestAddress_inBackground(LatLng latLng) {
        progressBar.show();
        String url = MapUtils.getGeocodeUrl(latLng);
        addRequest("request_geocode_address", Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
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
                            if(destination.getAddress().getLocation() != null) originMode= false;
                            //originMode= false;
                        }else if(destinationMode){
                            tvDestination.setText(address.getFormattedAddress());
                            address.setLocation(destination.getAddress().getLocation());
                            destination.setAddress( address );
                            //destination = address.getLocation();
                            if(origin.getAddress().getLocation() != null) destinationMode = false;
                            //destinationMode= false;
                        }
                    }
                }catch (Exception e){
                    e.printStackTrace();
                    if(originMode){
                        if(destination.getAddress().getLocation() != null) originMode= false;
                    }else if(destinationMode){
                        if(origin.getAddress().getLocation() != null) destinationMode = false;
                    }
                    Toast.makeText(mContext, "Error "+e.getMessage(), LENGTH_SHORT).show();
                }
                progressBar.dismiss();
                refreshMap();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                volleyError.printStackTrace();
                if(originMode){
                    if(destination.getAddress().getLocation() != null) originMode= false;
                }else if(destinationMode){
                    if(origin.getAddress().getLocation() != null) destinationMode = false;
                }
                Toast.makeText(mContext, "Network Error "+volleyError.getMessage(), LENGTH_SHORT).show();
                progressBar.dismiss();
            }
        }, null, null);
    }

    private void requestAddress(LatLng latLng, final Handler handler) {
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
                            //if(destination.getAddress().getLocation() != null) originMode= false;
                            //originMode= false;
                        }else if(destinationMode){
                            tvDestination.setText(address.getFormattedAddress());
                            address.setLocation(destination.getAddress().getLocation());
                            destination.setAddress( address );
                            //destination = address.getLocation();
                            //if(origin.getAddress().getLocation() != null) destinationMode = false;
                            //destinationMode= false;
                        }
                    }
                }catch (Exception e){
                    e.printStackTrace();
                    if(originMode){
                        if(destination.getAddress().getLocation() != null) originMode= false;
                    }else if(destinationMode){
                        if(origin.getAddress().getLocation() != null) destinationMode = false;
                    }
                    Toast.makeText(mContext, "Error "+e.getMessage(), LENGTH_SHORT).show();
                }
                //refreshMap();
                handler.handleMessage(null);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                volleyError.printStackTrace();
                if(originMode){
                    if(destination.getAddress().getLocation() != null) originMode= false;
                }else if(destinationMode){
                    if(origin.getAddress().getLocation() != null) destinationMode = false;
                }
                Toast.makeText(mContext, "Network Error "+volleyError.getMessage(), LENGTH_SHORT).show();
                handler.handleMessage(null);
            }
        }, null, null);
    }
    private void drawRoute() {
        boolean next = true;
        if(route != null){
            next = handleNext();
            if(next){
                DoSendHelper.getInstance().setPacketRoute(origin, destination);

                DataParser parser = new DataParser();
                String snippet = originMarker.getSnippet();
                originMarker.setSnippet(snippet + "\nEst. Distance :"+ route.getDistance().getText());
                tvDistanceInfo.setText("Harga ( "+route.getDistance().getText()+" ) : ");


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
                    LatLng center = bounds.getCenter();
                    LatLng northwest = move(center, 109, 109);
                    LatLng southwest = move(center, -109, -109);
                    builder.include(northwest);
                    builder.include(southwest);
                    bounds = builder.build();

                    int width = getResources().getDisplayMetrics().widthPixels;
                    int padding = (int) (width * 0.40); // offset from edges of the map 12% of screen

                    CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
                    mMap.animateCamera(cu);
                    //moveCameraToLocation(bounds.getCenter(), 16f);

                }else {
                    LogUtil.logD("drawRoutes","without Polylines drawn");
                }
            }
        }
        if(next) {
            if(canDrawRoute()) requestprice();
            showServicePanel(true);
        }

    }
    private static LatLng move(LatLng startLL, double toNorth, double toEast) {
        double lonDiff = meterToLongitude(toEast, startLL.latitude);
        double latDiff = meterToLatitude(toNorth);
        return new LatLng(startLL.latitude + latDiff, startLL.longitude
                + lonDiff);
    }
    static final double EARTHRADIUS = 6366198;
    private static double meterToLongitude(double meterToEast, double latitude) {
        double latArc = Math.toRadians(latitude);
        double radius = Math.cos(latArc) * EARTHRADIUS;
        double rad = meterToEast / radius;
        return Math.toDegrees(rad);
    }
    private static double meterToLatitude(double meterToNorth) {
        double rad = meterToNorth / EARTHRADIUS;
        return Math.toDegrees(rad);
    }

    public boolean handleNext(){
        int dist = 0;
        try{
            dist = Integer.parseInt(route.getDistance().getValue());
        }catch (Exception e){}
        if(dist > AppConfig.MAX_DOSEND_COVERAGE_KM ){
            if(doType.equalsIgnoreCase(AppConfig.KEY_DOSEND)) {
                //showErrorDialog("Distance Limited.", "Jarak terlalu jauh. Silahkan menggunakan jasa DO-MOVE.");
                Toast.makeText(mContext, "( " + route.getDistance().getText() + " ): Jarak terlalu jauh. Silahkan menggunakan jasa DO-MOVE.", LENGTH_SHORT).show();
                inDoSendCoverageArea = false;
                return inDoSendCoverageArea;
            }else if(doType.equalsIgnoreCase(AppConfig.KEY_DOMOVE)){
                if(dist > AppConfig.MAX_DOMOVE_COVERAGE_KM ){
                    Toast.makeText(mContext, "( " + route.getDistance().getText() + " ): Jarak terlalu jauh. Tidak ada layanan.", LENGTH_SHORT).show();
                    inDoMoveCoverageArea = false;
                    return inDoMoveCoverageArea;
                }
            }else if(doType.equalsIgnoreCase(AppConfig.KEY_DOJEK)){
                if(dist > AppConfig.MAX_DOJEK_COVERAGE_KM ){
                    Toast.makeText(mContext, "( " + route.getDistance().getText() + " ): Jarak terlalu jauh. Silahkan menggunakan jasa DO-CAR.", LENGTH_SHORT).show();
                    inDoJekCoverageArea = false;
                    return inDoJekCoverageArea;
                }
            }else if(doType.equalsIgnoreCase(AppConfig.KEY_DOCAR)){
                if(dist > AppConfig.MAX_DOCAR_COVERAGE_KM ){
                    Toast.makeText(mContext, "( " + route.getDistance().getText() + " ): Jarak terlalu jauh. Tidak ada layanan.", LENGTH_SHORT).show();
                    inDoCarCoverageArea = false;
                    return inDoCarCoverageArea;
                }
            }else{
                if(dist > AppConfig.MAX_DOMOVE_COVERAGE_KM ){
                    Toast.makeText(mContext, "( " + route.getDistance().getText() + " ): Jarak terlalu jauh. Tidak ada layanan.", LENGTH_SHORT).show();
                    inDoMoveCoverageArea = false;
                    return inDoMoveCoverageArea;
                }
            }
        }
        return true;
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
                            route.setDestination(destination.clone());
                            route.setOrigin(origin.clone());
                            DoSendHelper.getInstance().addRoute(route);
                            drawRoute();
                            //handleNext();
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError volleyError) {
                    LogUtil.logE(TAG, "request_direction_route Error: " + volleyError.getMessage());
                    volleyError.printStackTrace();
                }
            }, null, null);

        }
    }

    protected void requestprice() {
        progressBar.setIndeterminate(true);
        progressBar.show();

        GsonBuilder builder = new GsonBuilder();
        builder.setPrettyPrinting(); builder.serializeNulls();
        builder.excludeFieldsWithoutExposeAnnotation();
        Gson gson = builder.create();

        if(DoSendHelper.getInstance().getDestination() == null || DoSendHelper.getInstance().getDestination().getAddress() == null) return;

        Address originAddr = DoSendHelper.getInstance().getOrigin().getAddress() ;
        Address destinationAddr = DoSendHelper.getInstance().getDestination().getAddress() ;

        HashMap<String, String> params = new HashMap();
        params.put("distance", (route ==null || route.getDistance()==null? "1" : route.getDistance().getValue()));
        params.put("origin", gson.toJson(originAddr));
        params.put("destination", gson.toJson(destinationAddr));
        params.put("service_code", serviceCode);
        params.put("do_type", doType);
        params.put("berat_kiriman", ""+beratKiriman);
        params.put("volume", ""+volume);

        price = 0;
        addRequest("request_price_route", Request.Method.POST, AppConfig.URL_CALC_PRICE_KM, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    LogUtil.logD(TAG, "requestprice Response: " + response.toString());
                    JSONObject jObj = new JSONObject(response);
                    String status = jObj.getString("status");
                    boolean OK = "OK".equalsIgnoreCase(status);
                    if(OK){
                        double tariff = jObj.getDouble("tarif");
                        price = tariff;
                        tvPriceInfo.setText(AppConfig.formatCurrency(tariff));
                    }else{
                        Toast.makeText(mContext, ""+status, LENGTH_SHORT).show();
                    }
                }catch (Exception e){
                    e.printStackTrace();
                    Toast.makeText(mContext, "Error "+e.getMessage(), LENGTH_SHORT).show();
                }
                progressBar.dismiss();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                volleyError.printStackTrace();
                Toast.makeText(mContext, "Network Error "+volleyError.getMessage(), LENGTH_SHORT).show();
                progressBar.dismiss();
            }
        }, params, getKurindoHeaders());

    }

    private void requestDistance() {
        String url = MapUtils.getDistancematrixUrl(origin.getAddress().getLocation(), destination.getAddress().getLocation());
        addRequest("request_distance_route", Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //LogUtil.logD(TAG, "drawRoute Response: " + response.toString());

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
        if( destinations.size() > 0){
            int i = 0;
            for (TUser dest : destinations){
                String title = ++i+" Destination";
                String snippet = (dest.getName() != null ? dest.toStringAddressFormatted() : "");
                destinationMarker = mMap.addMarker(
                        new MarkerOptions()
                                .position(dest.getAddress().getLocation())
                                .title(title)
                                .snippet(snippet)
                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.destination_pin)));
            }
        }
        if(destination != null && destination.getAddress() != null && destination.getAddress().getLocation() != null){
            String title = "Destination";
            String snippet = (destination.getName() != null ? destination.toStringAddressFormatted() : "");
            destinationMarker = mMap.addMarker(
                    new MarkerOptions()
                            .position(destination.getAddress().getLocation())
                            .title(title)
                            .snippet(snippet)
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.destination_pin)));
        }

        if(origin != null && origin.getAddress() != null && origin.getAddress().getLocation() != null){
            String title = "Origin";
            String snippet = (origin.getName() != null ? origin.toStringAddressFormatted() : "");
            originMarker = mMap.addMarker(
                    new MarkerOptions()
                            .position(origin.getAddress().getLocation())
                            .title(title)
                            .snippet(snippet)
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.origin_pin)));
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
        LogUtil.logD(TAG, "OnMapReady");
        mMap = googleMap;
        mMap.setOnCameraMoveStartedListener(new GoogleMap.OnCameraMoveStartedListener() {
            @Override
            public void onCameraMoveStarted(int i) {
                if(!canDrawRoute() || originMode || destinationMode){
                    LogUtil.logD("started position change","setOnCameraMoveStartedListener");
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
                //updatePlace = false;

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
        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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
            if(canDrawRoute()) {
                drawRoute();
            }else{
                if(origin.getAddress().getLocation() == null) {
                    origin.getAddress().setLocation( new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude()) );
                    originMode= true;
                    requestAddress_inBackground(origin.getAddress().getLocation());
                }
                changeMap(mLastLocation);
            }
            reDrawMarker();
            showAddressLayout();
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

        mPlaceArrayAdapter.setGoogleApiClient(mGoogleApiClient);

    }


    @Override
    public void onConnectionSuspended(int i) {
        LogUtil.logI(TAG, "Connection suspended");
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

        LogUtil.logD(TAG, "Reaching map" + mMap);


        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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
            Toast.makeText(mContext,
                    "Sorry! unable to create maps", LENGTH_SHORT)
                    .show();
        }

    }

    public void setDoType(String doType) {
        this.doType = doType;
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
                if(destinationMode)
                    tvDestination.setText(place.getAddress());

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
        list.setLayoutManager(new GridLayoutManager(mContext, 1));
        list.setHasFixedSize(true);
        list.setAdapter(tUserAdapter);
        list.addOnItemTouchListener(new RecyclerItemClickListener(mContext, new RecyclerItemClickListener.OnItemClickListener() {
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
                if(destinationMode){
                    destination = p;
                    tvDestination.setText(p.getAddress().toStringFormatted());
                    if(p.getAddress().getNotes() != null && !p.getAddress().getNotes().isEmpty()) {
                        etDestinationNotes.setText(p.getAddress().getNotes());
                        etDestinationNotes.setVisibility(View.VISIBLE);
                    }
                    destinationMode = false;
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
                destinationMode = false;
                originMode = false;
            }
        });
    }


    @Override
    public int getName() {
        return 0;
    }

    public VerificationError verifyStep() {
        if(!inDoSendCoverageArea){
            return new VerificationError("Jarak terlalu jauh untuk DOSEND. Gunakan DOMOVE sebagai alternatif.");
        }
        if(!inDoMoveCoverageArea){
            return new VerificationError("Jarak terlalu jauh. Tidak ada layanan.");
        }

        String onotes = etOriginNotes.getText().toString();
        String dnotes = etDestinationNotes.getText().toString();
        origin.getAddress().setNotes(onotes);
        destination.getAddress().setNotes(dnotes);
        if( doType.equalsIgnoreCase(AppConfig.KEY_DOSEND)) {
            if (!canDrawRoute()) {
                return new VerificationError("Pilih rute lokasi anda.");
            }
            //TODO : check location based service / kedua rute masih dalam 1 kabupaten (dojek, dosend) atau propinsi (domove, docar) dan exception propinsi kecil2

            DoSendHelper.getInstance().setPacketRoute(origin, destination);
            DoSendHelper.getInstance().addDoSendOrder(payment.getText(), serviceCode, (route == null || route.getDistance() == null ? "" : route.getDistance().getValue()), price);
            //showActivity( DoSendOrderActivity.class );
            //finish();
        }else{
            //Toast.makeText(mContext, "Not Available", LENGTH_SHORT).show();
            return new VerificationError(doType+" Not Available");
        }
        return null;
    }

    @Override
    public void onSelected() {
        updateUI();
    }

    protected void updateUI() {

        radioDoSend.setVisibility(View.VISIBLE);
        radioDoMove.setVisibility(View.VISIBLE);
        radioDoJek.setVisibility(View.GONE);
        radioDoCar.setVisibility(View.GONE);

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
    public void onError(@NonNull VerificationError error) {

    }

}
