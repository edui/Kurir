package id.co.kurindo.kurindo.wizard.dowash;

import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatButton;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.stepstone.stepper.Step;
import com.stepstone.stepper.VerificationError;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

import butterknife.Bind;
import butterknife.OnClick;
import id.co.kurindo.kurindo.R;
import id.co.kurindo.kurindo.app.AppConfig;
import id.co.kurindo.kurindo.comp.ProgressDialogCustom;
import id.co.kurindo.kurindo.helper.DoServiceHelper;
import id.co.kurindo.kurindo.model.DoService;
import id.co.kurindo.kurindo.model.TOrder;
import id.co.kurindo.kurindo.model.TPrice;
import id.co.kurindo.kurindo.util.LogUtil;
import id.co.kurindo.kurindo.util.ParserUtil;
import id.co.kurindo.kurindo.wizard.BaseStepFragment;

/**
 * Created by dwim on 3/15/2017.
 */

public class DoWashForm1 extends BaseStepFragment implements Step {
    private static final String TAG = "DoWashForm1";
    VerificationError invalid = null;
    ProgressDialog progressBar;

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

    @Bind(R.id.tvMinBeratInfo)
    TextView tvMinBeratInfo;
    @Bind(R.id.tvDay)
    TextView tvDay;

    @Bind(R.id.radio_group_service)
    RadioGroup radioGroupService;

    @Bind(R.id.spLokasi)
    Spinner spLokasi;

    //BigDecimal price1;
    //BigDecimal price2;

/*    @Bind(R.id.radio_regular)
    RadioButton radioRegular;
    @Bind(R.id.radio_flash)
    RadioButton radioFlash;
    @Bind(R.id.radio_express)
    RadioButton radioExpress;
*/

    ArrayAdapter<CharSequence> adapter;

    //private List<TPrice> prices = new ArrayList<>();
    HashMap<String, TPrice> priceMaps = new HashMap<>();

    @Bind(R.id.tvPickupTime)
    TextView tvPickupTime;
    Calendar start;
    Calendar end;
    int hour;
    int minute;
    boolean next = false;
    BigDecimal total = new BigDecimal(0);
    Context context;
    DoService do1;
    DoService do2;
    String layanan;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getContext();
        progressBar = new ProgressDialogCustom(context);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflateAndBind(inflater, container, R.layout.fragment_dowash1);
        start = Calendar.getInstance();
        hour = start.get(Calendar.HOUR_OF_DAY);
        minute = start.get(Calendar.MINUTE);

        createDo1();
        createDo2();
        tvMinBeratInfo.setText("(Min "+AppConfig.MIN_WEIGHT_DOWASH+" Kg)");
        retrieve_price();
        radioGroupService_OnClick();

        radioGroupService.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                radioGroupService_OnClick();
                calculate_price();
            }
        });

        adapter = ArrayAdapter.createFromResource(context,R.array.doservice_lokasi_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spLokasi.setAdapter(adapter);


        return v;
    }


    private void createDo1() {
        if(do1 == null){
            do1 = new DoService();
            do1.setJenis_barang(getString(R.string.dowash_type1));
            int q = 0;
            try{ q = Integer.parseInt(quantityStr.getText().toString());}catch (Exception e){};
            do1.setQty(q);
            do1.setKode_layanan(layanan);
            do1.setUnit("Kg");
        }
    }
    private void createDo2() {
        if(do2 == null){
            do2 = new DoService();
            do2.setJenis_barang(getString(R.string.dowash_type2));
            int q = 0;
            try{ q = Integer.parseInt(quantityStr2.getText().toString());}catch (Exception e){};
            do2.setQty(q);
            do2.setKode_layanan(layanan);
            do2.setUnit("Kg");
        }
    }

    private void retrieve_price() {
        progressBar.show();
        final String tag_string_Req = "retrieve_price";
        String url = AppConfig.URL_PRICE_REQUEST;
        final HashMap<String, String> params = new HashMap();
        params.put("do-type", AppConfig.KEY_DOWASH);
        addRequest(tag_string_Req, Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                LogUtil.logD(TAG, tag_string_Req+" Response: " + response.toString());
                try {
                    JSONObject jObj = new JSONObject(response);
                    String message = jObj.getString("message");
                    boolean OK = "OK".equalsIgnoreCase(message);
                    if(OK){
                        JSONArray datas = jObj.getJSONArray("data");
                        if(datas != null && datas.length() > 0) {
                            //prices.clear();
                            priceMaps.clear();
                            ParserUtil parser = new ParserUtil();
                            for (int i = 0; i < datas.length(); i++) {
                                TPrice price = parser.parserTPrice(datas.getJSONObject(i));
                                //prices.add(price);
                                priceMaps.put(price.getService_code(), price);
                            }
                        }
                    }
                }catch (JSONException e){
                    e.printStackTrace();
                    Toast.makeText(context, "Daftar harga tidak tersedia.", Toast.LENGTH_SHORT).show();
                    //invalid = new VerificationError("Json error: " + e.getMessage());
                }
                progressBar.dismiss();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError e) {
                e.printStackTrace();
                Toast.makeText(context, "Network Error. Gagal menyiapkan harga.", Toast.LENGTH_SHORT).show();
                progressBar.dismiss();
            }
        }, params, getKurindoHeaders());
    }

    private void setup_button() {
        incrementBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createDo1();
                increment(v);
                calculate_price();}
        });
        decrementBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createDo1();
                decrement(v);
                calculate_price();
            }
        });

        incrementBtn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createDo2();
                increment2(v);
                calculate_price();}
        });
        decrementBtn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createDo2();
                decrement2(v);
                calculate_price();
            }
        });
    }


    private BigDecimal calculate_price() {
        BigDecimal qty1 = new BigDecimal(0);
        BigDecimal qty2 = new BigDecimal(0);
        total = new BigDecimal(0);

        try {
            qty1 = new BigDecimal(quantityStr.getText().toString());
        }catch (Exception e){}
        try {
            qty2 = new BigDecimal(quantityStr2.getText().toString());
        }catch (Exception e){}

        BigDecimal priceInfo1 = do1.getPrice_unit().multiply(qty1);
        BigDecimal priceInfo2 = do2.getPrice_unit().multiply(qty2);
        total = priceInfo1.add(priceInfo2);
        tvTotalPrice.setText("Biaya : "+AppConfig.formatCurrency( total.intValue() ));

        do1.setQty(qty1.intValue());
        do2.setQty(qty2.intValue());
        do1.setPrice(priceInfo1);
        do2.setPrice(priceInfo2);


        return total;
    }

    @OnClick(R.id.radio_group_service)
    public void radioGroupService_OnClick(){
        start = Calendar.getInstance();
        //hour = start.get(Calendar.HOUR_OF_DAY);
        //minute = start.get(Calendar.MINUTE);
        start.set(Calendar.HOUR_OF_DAY, hour);
        start.set(Calendar.MINUTE, minute);

        cek_layanan();
    }

    private void cek_layanan() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy HH:mm");
        end = Calendar.getInstance();
        String waktu = "";
        TPrice price = null;
        switch (radioGroupService.getCheckedRadioButtonId()){
            case R.id.radio_regular:
                layanan = getString( R.string.label_regular );
                cek_time();
                end.add(Calendar.DATE, 3);
                waktu = "3 hari";
                price = priceMaps.get(layanan);
                if(price == null){
                    price = new TPrice();
                    price.setPrice1(new BigDecimal( 6000 ));
                    price.setPrice2(new BigDecimal(30000 ));
                }
                /*if(price1 == null){
                    price1 = new BigDecimal(6000);
                }
                if(price2 == null){
                    price2 = new BigDecimal(30000);
                }*/
                break;
            case R.id.radio_express:

                layanan = getString( R.string.label_express);
                cek_time();
                end.add(Calendar.DATE, 1);
                waktu = "1 hari";
                price = priceMaps.get(layanan);
                if(price == null){
                    price = new TPrice();
                    price.setPrice1(new BigDecimal(9000));
                    price.setPrice2(new BigDecimal(40000));
                }

                /*if(price1 == null){
                    price1 = new BigDecimal(9000);
                }
                if(price2 == null){
                    price2 = new BigDecimal(40000);
                }*/
                break;
            case R.id.radio_flash:

                layanan = getString( R.string.label_flash );
                cek_time();
                if(hour+6 > AppConfig.START_ENS) {
                    end = Calendar.getInstance();
                    end.add(Calendar.DATE, 1);
                    end.set(Calendar.HOUR_OF_DAY, AppConfig.START_SDS);
                }else{
                    end.add(Calendar.HOUR, 6);
                }

                waktu = "6 jam";
                price = priceMaps.get(layanan);
                if(price == null){
                    price = new TPrice();
                    price.setPrice1(new BigDecimal(12000));
                    price.setPrice2(new BigDecimal(50000));
                }
                /*if(price1 == null){
                    price1 = new BigDecimal(12000);
                }
                if(price2 == null){
                    price2 = new BigDecimal(50000);
                }*/
                break;
        }

        tvLayanan.setText("Waktu Proses selama "+waktu+". \nTanggal Antar : "+sdf.format(end.getTime()));
        tvPriceInfo.setText("Rp "+ (price==null ? 0 : price.getPrice1().intValue())+" /"+do1.getUnit());
        tvPriceInfo2.setText("Rp "+(price==null ? 0 : price.getPrice2().intValue())+" /"+do2.getUnit());

        do1.setKode_layanan(layanan);
        do2.setKode_layanan(layanan);
        do1.setPrice_unit(price.getPrice1());
        do2.setPrice_unit(price.getPrice2());
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


    private void cek_with_calendar_time() {
        Calendar c = Calendar.getInstance();
        hour = c.get(Calendar.HOUR_OF_DAY);
        minute = c.get(Calendar.MINUTE);
        cek_time();
    }

    private int cek_time() {
        int add = 1;
        /*if(layanan.equalsIgnoreCase(getString( R.string.label_flash ))){
            add = 6;
        }*/
        int now = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        if(hour < now) hour = now;
        //tvDay.setText("Hari Ini");

        if(hour + add < AppConfig.START_SDS) {
            hour = AppConfig.START_SDS;
            start.set(Calendar.HOUR_OF_DAY, hour);
        } else if(hour+add > AppConfig.START_ENS) {
            hour = AppConfig.START_SDS;
            start = Calendar.getInstance();
            start.set(Calendar.HOUR_OF_DAY, hour);
            start.add(Calendar.DATE, 1);
            tvDay.setText("Besok");
        }
        end = start;
        tvPickupTime.setText(AppConfig.pad(hour)+":"+AppConfig.pad(minute));
        return hour;
    }

    private TimePickerDialog.OnTimeSetListener timePickerListener = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int selectedHour, int selectedMinute) {
            if(selectedHour < AppConfig.START_SDS || selectedHour >= AppConfig.START_ENS){
                Toast.makeText(context, "Jam pelayanan "+AppConfig.KEY_DOWASH+" antara "+AppConfig.START_SDS +" - "+AppConfig.START_ENS, Toast.LENGTH_SHORT).show();
                return;
            }
            start = Calendar.getInstance();
            start.set(Calendar.HOUR_OF_DAY, selectedHour);
            start.set(Calendar.MINUTE, selectedMinute);
            hour = selectedHour;
            minute = selectedMinute;
            //tvPickupTime.setText(AppConfig.pad(hour)+":"+AppConfig.pad(minute));
            cek_layanan();
        }
    };

    @OnClick(R.id.tvPickupTime)
    public void onClick_tvPickupTime(){
        TimePickerDialog d = new TimePickerDialog(context, timePickerListener, hour, minute, true);
        d.show();
    }

    @Override
    public int getName() {
        return R.string.dowash_form1;
    }

    @Override
    public VerificationError verifyStep() {

        if(do1.getQty() < 3) return new VerificationError("DO-WASH melayani minimal 3 Kg cucian.");

        DoServiceHelper.getInstance().addOrder(total, AppConfig.KEY_DOWASH);
        TOrder order = DoServiceHelper.getInstance().getOrder();
        order.setTotalQuantity(do1.getQty()+do2.getQty());
        order.setPickup(AppConfig.getDateTimeServerFormat().format( start.getTime() ));
        order.setDroptime(AppConfig.getDateTimeServerFormat().format( end.getTime() ));
        if(layanan.equalsIgnoreCase(getString( R.string.label_flash ))){
            order.setService_code(AppConfig.PACKET_SDS);
        }
        DoServiceHelper.getInstance().addService(do1);
        if(do2.getQty() > 0)
            DoServiceHelper.getInstance().addService(do2);

        do1.setLocation(spLokasi.getSelectedItem().toString());
        do2.setLocation(spLokasi.getSelectedItem().toString());

        return null;
    }

    @Override
    public void onSelected() {
        if(!next) {
            setup_button();
            DoServiceHelper.getInstance().clearOrder();
        }
    }


    @Override
    public void onError(@NonNull VerificationError error) {

    }

    public static Fragment newInstance() {
        Bundle bundle = new Bundle();
        DoWashForm1 f = new DoWashForm1();
        f.setArguments(bundle);
        return  f;
    }
}
