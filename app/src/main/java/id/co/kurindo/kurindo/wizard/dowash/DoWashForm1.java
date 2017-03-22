package id.co.kurindo.kurindo.wizard.dowash;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.stepstone.stepper.Step;
import com.stepstone.stepper.VerificationError;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import butterknife.Bind;
import butterknife.OnClick;
import id.co.kurindo.kurindo.R;
import id.co.kurindo.kurindo.wizard.BaseStepFragment;

import static id.co.kurindo.kurindo.R.style.CustomDialog;

/**
 * Created by dwim on 3/15/2017.
 */

public class DoWashForm1 extends BaseStepFragment implements Step {
    private static final String TAG = "DoWashForm1";
    VerificationError invalid = null;
    ProgressDialog progressDialog;

    @Bind(R.id.quantityStr)
    TextView quantityStr;
    @Bind(R.id.incrementBtn)
    AppCompatButton incrementBtn;
    @Bind(R.id.decrementBtn) AppCompatButton decrementBtn;

    @Bind(R.id.quantityStr2) TextView quantityStr2;
    @Bind(R.id.incrementBtn2) AppCompatButton incrementBtn2;
    @Bind(R.id.decrementBtn2) AppCompatButton decrementBtn2;

    @Bind(R.id.tvLayanan)
    TextView tvLayanan;
    @Bind(R.id.tvPriceInfo)
    TextView tvPriceInfo;
    @Bind(R.id.tvPriceInfo2)
    TextView tvPriceInfo2;
    @Bind(R.id.tvTotalPrice)
    TextView tvTotalPrice;

    @Bind(R.id.radio_group_service)
    RadioGroup radioGroupService;
    BigDecimal price1;
    BigDecimal price2;

/*    @Bind(R.id.radio_regular)
    RadioButton radioRegular;
    @Bind(R.id.radio_flash)
    RadioButton radioFlash;
    @Bind(R.id.radio_express)
    RadioButton radioExpress;
*/

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflateAndBind(inflater, container, R.layout.fragment_dowash1);

        progressDialog = new ProgressDialog(getActivity(), CustomDialog);
        radioGroupService_OnClick();

        radioGroupService.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                radioGroupService_OnClick();
                calculate_price();
            }
        });
        setup_button();
        return v;
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

        incrementBtn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                increment2(v);
                calculate_price();}
        });
        decrementBtn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                decrement2(v);
                calculate_price();
            }
        });

    }


    private void calculate_price() {
        BigDecimal qty1 = new BigDecimal(0);
        BigDecimal qty2 = new BigDecimal(0);
        BigDecimal total = new BigDecimal(0);
        try {
            qty1 = new BigDecimal(quantityStr.getText().toString());
        }catch (Exception e){}
        try {
            qty2 = new BigDecimal(quantityStr2.getText().toString());
        }catch (Exception e){}

        BigDecimal priceInfo1 = price1.multiply(qty1);
        BigDecimal priceInfo2 = price2.multiply(qty2);
        total = priceInfo1.add(priceInfo2);
        tvTotalPrice.setText("TOTAL : Rp "+total.intValue());
    }

    @OnClick(R.id.radio_group_service)
    public void radioGroupService_OnClick(){
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy HH:mm");
        Calendar c = Calendar.getInstance();
        String waktu = "";
        switch (radioGroupService.getCheckedRadioButtonId()){
            case R.id.radio_regular:
                c.add(Calendar.DATE, 3);
                waktu = "3 hari";
                price1 = new BigDecimal(6000);
                price2 = new BigDecimal(30000);
                break;
            case R.id.radio_express:
                c.add(Calendar.DATE, 1);
                waktu = "1 hari";
                price1 = new BigDecimal(9000);
                price2 = new BigDecimal(40000);
                break;
            case R.id.radio_flash:
                c.add(Calendar.HOUR, 6);
                waktu = "6 jam";
                price1 = new BigDecimal(12000);
                price2 = new BigDecimal(50000);
                break;
        }
        tvLayanan.setText("Waktu Proses selama "+waktu+". \nTanggal Pengambilan : "+sdf.format(c.getTime()));
        tvPriceInfo.setText("Rp "+price1.intValue()+" /Kg");
        tvPriceInfo2.setText("Rp "+price2.intValue()+" /Kg");

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

    public void increment2(View view){
        int q = 0;
        try{ q = Integer.parseInt(quantityStr2.getText().toString());}catch (Exception e){};
        q++;
        quantityStr2.setText(""+q);
    }
    public void decrement2(View view){
        int q = 0;
        try{ q = Integer.parseInt(quantityStr2.getText().toString());}catch (Exception e){};
        q--;
        if(q < 0) q =0;
        quantityStr2.setText(""+q);
    }

    @Override
    public int getName() {
        return R.string.dowash_form1;
    }

    @Override
    public VerificationError verifyStep() {
        return null;
    }

    @Override
    public void onSelected() {

    }


    @Override
    public void onError(@NonNull VerificationError error) {

    }
}
