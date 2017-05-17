package id.co.kurindo.kurindo.wizard.dojek;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.RadioGroup;
import com.stepstone.stepper.VerificationError;

import id.co.kurindo.kurindo.R;
import id.co.kurindo.kurindo.app.AppConfig;
import id.co.kurindo.kurindo.helper.DoSendHelper;
import id.co.kurindo.kurindo.wizard.BaseLocationMapFragment;

/**
 * Created by dwim on 2/14/2017.
 */

public class DoJekPinLocationMapFragment extends BaseLocationMapFragment{
    @Override
    public int getLayout() {
        return R.layout.fragment_maps_dojek;
    }

    @Override
    protected void afterOnCreateView() {
        super.afterOnCreateView();
        updateUI();
    }

    @Override
    protected void rgDoType_onCheckedChanged(RadioGroup group, int checkedId) {
        super.rgDoType_onCheckedChanged(group, checkedId);
        FragmentActivity act = getActivity();
        if(act instanceof DoJekOrderActivity){
            ((DoJekOrderActivity) act).doType = doType;
            ((DoJekOrderActivity) act).getStepperAdapter();
        }

    }

    @Override
    public VerificationError verifyStep() {

        if(!inDoJekCoverageArea){
            return new VerificationError("Jarak terlalu jauh untuk "+AppConfig.KEY_DOJEK+". Gunakan "+AppConfig.KEY_DOCAR+" sebagai alternatif.");
        }
        if(!inDoCarCoverageArea){
            return new VerificationError("Jarak terlalu jauh. Tidak ada layanan.");
        }
        if(route ==null || route.getDistance() == null || route.getDistance().getText().isEmpty() || route.getDistance().getText().equalsIgnoreCase("null") ){
            return new VerificationError("Rute dan Jarak tidak diketahui. Silahkan dicoba lagi.");
        }
        String onotes = etOriginNotes.getText().toString();
        String dnotes = etDestinationNotes.getText().toString();
        origin.getAddress().setNotes(onotes);
        destination.getAddress().setNotes(dnotes);
        if (!canDrawRoute()) {
            return new VerificationError("Pilih rute lokasi anda.");
        }

        DoSendHelper.getInstance().setPacketRoute(origin, destination);

        if( doType.equalsIgnoreCase(AppConfig.KEY_DOJEK)) {
            //TODO : check location based service / kedua rute masih dalam 1 kabupaten (dojek, dosend) atau propinsi (domove, docar) dan exception propinsi kecil2

            DoSendHelper.getInstance().addDoJekOrder(payment.getText(), serviceCode, (route == null || route.getDistance() == null ? "" : route.getDistance().getValue()), price);
        }else{
            //Toast.makeText(getContext(), "Not Available", LENGTH_SHORT).show();
            DoSendHelper.getInstance().addDoCarOrder(payment.getText(), serviceCode, (route == null || route.getDistance() == null ? "" : route.getDistance().getValue()), price);
        }
        return null;

    }

    @Override
    protected void updateUI() {
        radioDoSend.setVisibility(View.GONE);
        radioDoMove.setVisibility(View.GONE);
        radioDoJek.setVisibility(View.VISIBLE);
        radioDoCar.setVisibility(View.VISIBLE);

        if(DoSendHelper.getInstance().getDoType() != null){
            this.doType = DoSendHelper.getInstance().getDoType();
        }
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
    public static Fragment newInstance(String keyDo) {
        if (instance == null || !instance.doType.equalsIgnoreCase(keyDo)) {
            Bundle bundle = new Bundle();
            bundle.putString("doType", keyDo);
            instance = new DoJekPinLocationMapFragment();
            instance.setArguments(bundle);
        }
        Bundle bundle = instance.getArguments();
        bundle.putString("doType", keyDo);
        instance.setDoType(keyDo);
        return instance;
    }

    public static DoJekPinLocationMapFragment getInstance() {
        return instance;
    }
}
