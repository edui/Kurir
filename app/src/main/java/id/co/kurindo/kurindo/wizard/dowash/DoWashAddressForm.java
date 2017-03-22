package id.co.kurindo.kurindo.wizard.dowash;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.stepstone.stepper.Step;
import com.stepstone.stepper.VerificationError;

import butterknife.Bind;
import id.co.kurindo.kurindo.R;
import id.co.kurindo.kurindo.map.SinglePinLocationMapFragment;
import id.co.kurindo.kurindo.model.Address;
import id.co.kurindo.kurindo.wizard.BaseStepFragment;

import static id.co.kurindo.kurindo.R.style.CustomDialog;

/**
 * Created by dwim on 3/15/2017.
 */

public class DoWashAddressForm extends SinglePinLocationMapFragment {
    private static final String TAG = "DoWashAddressForm";
    VerificationError invalid = null;
    ProgressDialog progressDialog;

    @Bind(R.id.tvAlamat)
    TextView tvAlamat;
    @Bind(R.id.tvKecamatan)
    TextView tvKecamatan;
    @Bind(R.id.tvKabupaten)
    TextView tvKabupaten;
    @Bind(R.id.tvPropinsi)
    TextView tvPropinsi;
    @Bind(R.id.tvNegara)
    TextView tvNegara;

    protected int getLayout() {
        return R.layout.fragment_maps_pin_addressform;
    }

    public void onClick_mLocationMarkerText(){
        super.onClick_mLocationMarkerText();
        displayAddressText();
        hidepanel(false);
        originMode = false;
    }

    private void displayAddressText() {
        if(origin != null && origin.getAddress() != null){
            Address addr = origin.getAddress();
            tvAlamat.setText(addr.getAlamat());
            tvKecamatan.setText(addr.getKecamatan());
            tvKabupaten.setText(addr.getKabupaten());
            tvPropinsi.setText(addr.getPropinsi());
            tvNegara.setText(addr.getNegara());
        }
    }

    @Override
    public int getName() {
        return R.string.dowash_form1;
    }

    @Override
    public VerificationError verifyStep() {
        if(originMode) return new VerificationError("Set Lokasi Anda");
        if(origin ==null || origin.getAddress() == null || origin.getAddress().getLocation() == null) return new VerificationError("Set Lokasi Anda");


        return null;
    }

    @Override
    public void onSelected() {

    }

    @Override
    public void onError(@NonNull VerificationError error) {

    }
}
