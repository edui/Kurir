package id.co.kurindo.kurindo.wizard.dojek;

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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import id.co.kurindo.kurindo.wizard.BaseStepFragment;
import id.co.kurindo.kurindo.wizard.dosend.DoSendPinLocationMapFragment;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;
import static android.widget.Toast.LENGTH_SHORT;

/**
 * Created by dwim on 2/14/2017.
 */

public class DoJekPinLocationMapFragment extends DoSendPinLocationMapFragment{

    @Override
    public VerificationError verifyStep() {

        if(!inDoSendCoverageArea){
            return new VerificationError("Jarak terlalu jauh untuk DOJEK. Gunakan DOCAR sebagai alternatif.");
        }
        if(!inDoMoveCoverageArea){
            return new VerificationError("Jarak terlalu jauh. Tidak ada layanan.");
        }

        String onotes = etOriginNotes.getText().toString();
        String dnotes = etDestinationNotes.getText().toString();
        origin.getAddress().setNotes(onotes);
        destination.getAddress().setNotes(dnotes);
        if( doType.equalsIgnoreCase(AppConfig.KEY_DOJEK)) {
            if (!canDrawRoute()) {
                return new VerificationError("Pilih rute lokasi anda.");
            }
            DoSendHelper.getInstance().setPacketRoute(origin, destination);
            DoSendHelper.getInstance().addDoJekOrder(payment.getText(), serviceCode, (route == null || route.getDistance() == null ? "" : route.getDistance().getValue()), price);
            //showActivity( DoSendOrderActivity.class );
            //finish();
        }else{
            //Toast.makeText(getContext(), "Not Available", LENGTH_SHORT).show();
            return new VerificationError(doType+" Not Available");
        }
        return null;

    }

    @Override
    protected void updateUI() {
        radioDoSend.setVisibility(View.GONE);
        radioDoMove.setVisibility(View.GONE);
        radioDoJek.setVisibility(View.VISIBLE);
        radioDoCar.setVisibility(View.VISIBLE);

        switch (this.doType){
            case AppConfig.KEY_DOJEK:
                rgDoType.check(R.id.radio_dojek);
                break;
            case AppConfig.KEY_DOCAR:
                rgDoType.check(R.id.radio_docar);
                break;
        }
    }
    static DoJekPinLocationMapFragment instance;
    public static Fragment newInstance(String keyDosend) {
        if (instance == null) {
            instance = new DoJekPinLocationMapFragment();
        }
        instance.setDoType(keyDosend);
        return instance;
    }
    public static Fragment getInstance() {
        return instance;
    }

}
