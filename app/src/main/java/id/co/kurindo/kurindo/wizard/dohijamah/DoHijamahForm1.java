package id.co.kurindo.kurindo.wizard.dohijamah;

import android.app.ProgressDialog;
import android.content.Context;
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
import id.co.kurindo.kurindo.comp.ProgressDialogCustom;
import id.co.kurindo.kurindo.wizard.BaseStepFragment;

/**
 * Created by dwim on 3/15/2017.
 */

public class DoHijamahForm1 extends BaseStepFragment implements Step {
    private static final String TAG = "DoHijamahForm1";
    VerificationError invalid = null;
    Context context;
    ProgressDialog progressBar;

    @Bind(R.id.pilihLayanan)
    Spinner pilihLayanan;
    @Bind(R.id.pilihLokasi)
    Spinner pilihLokasi;

    @Bind(R.id.quantityStr)
    TextView quantityStr;
    @Bind(R.id.incrementBtn)
    AppCompatButton incrementBtn;
    @Bind(R.id.decrementBtn) AppCompatButton decrementBtn;

    @Bind(R.id.tvTotalPrice)
    TextView tvTotalPrice;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getContext();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflateAndBind(inflater, container, R.layout.fragment_dohijamah1);
        progressBar = new ProgressDialogCustom(context);

        return v;
    }
    private void setup_spinner() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(), R.array.dohijamah_layanan_array, android.R.layout.simple_spinner_item);
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
        return R.string.dohijamah_form;
    }

    @Override
    public VerificationError verifyStep() {
        return null;
    }

    @Override
    public void onSelected() {
        setup_spinner();
        setup_button();
    }

    @Override
    public void onError(@NonNull VerificationError error) {

    }
}
