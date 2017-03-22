package id.co.kurindo.kurindo.wizard.doservice;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.stepstone.stepper.Step;
import com.stepstone.stepper.VerificationError;

import butterknife.Bind;
import id.co.kurindo.kurindo.R;
import id.co.kurindo.kurindo.wizard.BaseStepFragment;

import static id.co.kurindo.kurindo.R.style.CustomDialog;

/**
 * Created by dwim on 3/15/2017.
 */

public class DoServiceForm1 extends BaseStepFragment implements Step {
    private static final String TAG = "DoServiceForm1";
    VerificationError invalid = null;
    ProgressDialog progressDialog;
    @Bind(R.id.pilihLayanan)
    Spinner pilihLayanan;
    @Bind(R.id.pilihTipeAc)
    Spinner pilihTipeAc;
    @Bind(R.id.pilihLokasi)
    Spinner pilihLokasi;

    @Bind(R.id.quantityStr)
    TextView quantityStr;
    @Bind(R.id.incrementBtn)
    AppCompatButton incrementBtn;
    @Bind(R.id.decrementBtn) AppCompatButton decrementBtn;

    @Bind(R.id.tvTotalPrice)
    TextView tvTotalPrice;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflateAndBind(inflater, container, R.layout.fragment_doservice1);

        progressDialog = new ProgressDialog(getActivity(), CustomDialog);

        setup_price();
        return v;
    }

    private void setup_price() {
        //request price list to server

    }

    private void setup_spinner() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),R.array.doservice_layanan_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        pilihLayanan.setAdapter(adapter);
        pilihLayanan.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        ArrayAdapter<CharSequence> adapter1 = ArrayAdapter.createFromResource(getContext(),R.array.doservice_tipeac_array, android.R.layout.simple_spinner_item);
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        pilihTipeAc.setAdapter(adapter1);
        pilihTipeAc.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(getContext(),R.array.doservice_lokasi_array, android.R.layout.simple_spinner_item);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        pilihLokasi.setAdapter(adapter2);
    }
    private void setup_button() {
        incrementBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                increment(v);
                calculate_price();}
        });
        decrementBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                decrement(v);
                calculate_price();
            }
        });

    }

    private void calculate_price() {

    }


    public void increment(View view){
        int q = 0;
        try{ q = Integer.parseInt(quantityStr.getText().toString());}catch (Exception e){};
        q++;
        quantityStr.setText(""+q);
    }
    public void decrement(View view){
        int q = 0;
        try{ q = Integer.parseInt(quantityStr.getText().toString());}catch (Exception e){};
        q--;
        if(q < 0) q =0;
        quantityStr.setText(""+q);
    }


    @Override
    public int getName() {
        return R.string.doservice_form1;
    }

    @Override
    public VerificationError verifyStep() {
        return null;
    }

    @Override
    public void onSelected() {
        setup_spinner();

    }

    @Override
    public void onError(@NonNull VerificationError error) {

    }
}
