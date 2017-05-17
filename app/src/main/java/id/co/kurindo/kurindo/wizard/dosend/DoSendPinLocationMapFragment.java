package id.co.kurindo.kurindo.wizard.dosend;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.AppCompatButton;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;
import android.widget.TextView;
import com.stepstone.stepper.VerificationError;

import java.math.BigDecimal;

import butterknife.Bind;
import butterknife.OnClick;
import id.co.kurindo.kurindo.R;
import id.co.kurindo.kurindo.app.AppConfig;
import id.co.kurindo.kurindo.helper.DoSendHelper;
import id.co.kurindo.kurindo.wizard.BaseLocationMapFragment;
import id.co.kurindo.kurindo.wizard.dojek.DoJekOrderActivity;

/**
 * Created by dwim on 2/14/2017.
 */

public class DoSendPinLocationMapFragment extends BaseLocationMapFragment {


    @Bind(R.id.input_berat_barang) protected TextView _beratBarangText;
    @Bind(R.id.incrementBtn)
    protected AppCompatButton incrementBtn;
    @Bind(R.id.decrementBtn) protected AppCompatButton decrementBtn;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    public int getLayout() {
        return R.layout.fragment_maps_dosend;
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
        if(act instanceof DoSendOrderActivity){
            ((DoSendOrderActivity) act).doType = doType;
            ((DoSendOrderActivity) act).getStepperAdapter();
        }
    }

    @OnClick(R.id.incrementBtn)
    public void onClick_incrementBtn(){
        //float q = 0;
        //try{ q = Float.parseFloat(_beratBarangText.getText().toString());}catch (Exception e){};
        //q +=0.1;
        BigDecimal b = new BigDecimal(_beratBarangText.getText().toString());
        b = b.add(new BigDecimal(0.1));
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
        b = b.subtract(new BigDecimal(0.1));
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

            Handler handler = new Handler(Looper.getMainLooper());
            Runnable runnable;
            @Override
            public void afterTextChanged(final Editable v) {
                handler.removeCallbacks(runnable);
                runnable =new Runnable() {
                    @Override
                    public void run() {
                        try {
                            beratKiriman = Float.parseFloat(v.toString());
                        } catch (Exception e) {
                        }

                        if(beratKiriman >= AppConfig.MIN_WEIGHT_DOSEND) {
                            requestprice();
                        }
                    }
                };
                handler.postDelayed(runnable, 800);
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
        if( doType.equalsIgnoreCase(AppConfig.KEY_DOSEND)) {
            //TODO : check location based service / kedua rute masih dalam 1 kabupaten (dojek, dosend) atau propinsi (domove, docar) dan exception propinsi kecil2

            DoSendHelper.getInstance().addDoSendOrder(payment.getText(), serviceCode, (route == null || route.getDistance() == null ? "" : route.getDistance().getValue()), price, beratKiriman);
            //showActivity( DoSendOrderActivity.class );
            //finish();
        }else{
            //Toast.makeText(getContext(), "Not Available", LENGTH_SHORT).show();
            DoSendHelper.getInstance().addDoMoveOrder(payment.getText(), serviceCode, (route == null || route.getDistance() == null ? "" : route.getDistance().getValue()), price, beratKiriman);
            //return new VerificationError(doType+" Not Available");
        }
        return null;
    }

    protected void updateUI() {

        setup_berat_barang();

        radioDoSend.setVisibility(View.VISIBLE);
        radioDoMove.setVisibility(View.VISIBLE);
        radioDoJek.setVisibility(View.GONE);
        radioDoCar.setVisibility(View.GONE);

        if(DoSendHelper.getInstance().getDoType() != null){
            this.doType = DoSendHelper.getInstance().getDoType();
        }
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
    protected static Fragment newInstance(String keyDo) {
        Bundle bundle = new Bundle();
        if (instance == null || !instance.doType.equalsIgnoreCase(keyDo)) {
            instance = new DoSendPinLocationMapFragment();
            instance.setArguments(bundle);
        }
        bundle = instance.getArguments();
        bundle.putString("doType", keyDo);
        instance.setDoType(keyDo);
        return instance;
    }

    protected static Fragment getInstance() {
        return instance;
    }

}


