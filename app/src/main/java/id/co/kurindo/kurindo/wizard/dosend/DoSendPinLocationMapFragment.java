package id.co.kurindo.kurindo.wizard.dosend;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
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
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
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
import com.stepstone.stepper.Step;
import com.stepstone.stepper.VerificationError;

import org.json.JSONArray;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;
import id.co.kurindo.kurindo.R;
import id.co.kurindo.kurindo.adapter.PacketServiceAdapter;
import id.co.kurindo.kurindo.adapter.PaymentAdapter;
import id.co.kurindo.kurindo.adapter.TUserAdapter;
import id.co.kurindo.kurindo.app.AppConfig;
import id.co.kurindo.kurindo.base.RecyclerItemClickListener;
import id.co.kurindo.kurindo.helper.DoSendHelper;
import id.co.kurindo.kurindo.map.DataParser;
import id.co.kurindo.kurindo.map.MapUtils;
import id.co.kurindo.kurindo.map.PlaceArrayAdapter;
import id.co.kurindo.kurindo.model.Address;
import id.co.kurindo.kurindo.model.PacketService;
import id.co.kurindo.kurindo.model.Payment;
import id.co.kurindo.kurindo.model.Route;
import id.co.kurindo.kurindo.model.TUser;
import id.co.kurindo.kurindo.wizard.BaseLocationMapFragment;
import id.co.kurindo.kurindo.wizard.BaseStepFragment;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;
import static android.widget.Toast.LENGTH_SHORT;

/**
 * Created by dwim on 2/14/2017.
 */

public class DoSendPinLocationMapFragment extends BaseLocationMapFragment {


    @Bind(R.id.input_berat_barang) TextView _beratBarangText;
    @Bind(R.id.incrementBtn)
    AppCompatButton incrementBtn;
    @Bind(R.id.decrementBtn) AppCompatButton decrementBtn;


    public int getLayout() {
        return R.layout.fragment_maps_dosend;
    }

    @Override
    protected void afterOnCreateView() {
        super.afterOnCreateView();
        setup_berat_barang();
    }

    @OnClick(R.id.incrementBtn)
    public void onClick_incrementBtn(){
        //float q = 0;
        //try{ q = Float.parseFloat(_beratBarangText.getText().toString());}catch (Exception e){};
        //q +=0.1;
        BigDecimal b = new BigDecimal(_beratBarangText.getText().toString());
        b = b.add(new BigDecimal(0.25));
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
        b = b.subtract(new BigDecimal(0.25));
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

            @Override
            public void afterTextChanged(Editable v) {
                try {
                    beratKiriman = Float.parseFloat(v.toString());
                } catch (Exception e) {
                }
                ;
                if(beratKiriman > 0 && (beratKiriman % 0.25 == 0) ) requestprice();
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
            DoSendHelper.getInstance().addDoSendOrder(payment.getText(), serviceCode, (route == null || route.getDistance() == null ? "" : route.getDistance().getValue()), price, beratKiriman);
            //showActivity( DoSendOrderActivity.class );
            //finish();
        }else{
            //Toast.makeText(getContext(), "Not Available", LENGTH_SHORT).show();
            return new VerificationError(doType+" Not Available");
        }
        return null;
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

    static DoSendPinLocationMapFragment instance ;
    protected static Fragment newInstance(String keyDosend) {
        if (instance == null) {
            instance = new DoSendPinLocationMapFragment();
        }
        instance.setDoType(keyDosend);
        return instance;
    }

    protected static Fragment getInstance() {
        return instance;
    }

}


