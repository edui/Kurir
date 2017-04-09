package id.co.kurindo.kurindo.wizard.doservice;

import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.CardView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import butterknife.Bind;
import butterknife.OnClick;
import id.co.kurindo.kurindo.R;
import id.co.kurindo.kurindo.app.AppConfig;
import id.co.kurindo.kurindo.helper.DoServiceHelper;
import id.co.kurindo.kurindo.model.DoService;
import id.co.kurindo.kurindo.model.TOrder;
import id.co.kurindo.kurindo.model.TPrice;
import id.co.kurindo.kurindo.util.ParserUtil;
import id.co.kurindo.kurindo.wizard.BaseStepFragment;

import static android.view.ViewGroup.LayoutParams.FILL_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
import static id.co.kurindo.kurindo.R.style.CustomDialog;

/**
 * Created by dwim on 3/15/2017.
 */

public class DoServiceForm2 extends BaseStepFragment implements Step {
    private static final String TAG = "DoServiceForm1";
    VerificationError invalid = null;
    ProgressDialog progressDialog;
    @Bind((R.id.llLayout))
    LinearLayout baseLayout;

    List<Spinner> pilihLayanan;
    List<Spinner> pilihTipeAc;
    List<Spinner> pilihLokasi;
    List<EditText> etNotes;

    List<TextView> quantityStr;
    List<AppCompatButton> incrementBtn ;
    List<AppCompatButton> decrementBtn ;
    List<BigDecimal> subTotal;

    List<Button> btnRemoves;

    @Bind(R.id.tvTotalPrice)
    TextView tvTotalPrice;
    @Bind(R.id.btnAddItem)
    Button btnAddItem;

    @Bind(R.id.tvPickupTime)
    TextView tvPickupTime;
    int hour;
    int minute;


    BigDecimal total = new BigDecimal(0);
    HashMap<String, TPrice> priceMaps = new HashMap<>();
    String [] strLayanan = {"", "CUCI", "SERVICE", "CUCISERVICE", "PASANG", "BONGKAR", "RELOKASI"};
    List<Integer> intLayanan ;
    List<Integer> intAcType;
    List<Integer> intQty;
    int item = 0;

    ArrayAdapter<CharSequence> adapter;
    ArrayAdapter<CharSequence> adapter1;
    ArrayAdapter<CharSequence> adapter2;

    Context context;

    boolean next = false;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflateAndBind(inflater, container, R.layout.fragment_doservice2);

        progressDialog = new ProgressDialog(getActivity(), CustomDialog);
        context = getContext();
        retrieve_price();
        return v;
    }


    private void retrieve_price() {
        final String tag_string_Req = "retrieve_price";
        String url = AppConfig.URL_PRICE_REQUEST;
        final HashMap<String, String> params = new HashMap();
        params.put("do-type", AppConfig.KEY_DOSERVICE);
        addRequest(tag_string_Req, Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, tag_string_Req+" Response: " + response.toString());
                try {
                    JSONObject jObj = new JSONObject(response);
                    String message = jObj.getString("message");
                    boolean OK = "OK".equalsIgnoreCase(message);
                    if(OK){
                        JSONArray datas = jObj.getJSONArray("data");
                        if(datas != null && datas.length() > 0) {
                            priceMaps.clear();
                            ParserUtil parser = new ParserUtil();
                            for (int i = 0; i < datas.length(); i++) {
                                TPrice price = parser.parserTPrice(datas.getJSONObject(i));
                                priceMaps.put(price.getService_code(), price);
                            }
                        }
                    }
                }catch (JSONException e){
                    e.printStackTrace();
                    //invalid = new VerificationError("Json error: " + e.getMessage());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError e) {
                e.printStackTrace();
            }
        }, params, getKurindoHeaders());
    }


    private void setup_spinner() {
        adapter = ArrayAdapter.createFromResource(context,R.array.doservice_layanan_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        adapter1 = ArrayAdapter.createFromResource(context,R.array.doservice_tipeac_array, android.R.layout.simple_spinner_item);
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        adapter2 = ArrayAdapter.createFromResource(context,R.array.doservice_lokasi_array, android.R.layout.simple_spinner_item);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

    }

    private void calculate_price() {
        TOrder order =  DoServiceHelper.getInstance().getOrder();
        Set<DoService> services = order.getServices();
        if(services != null){
            Object[] datas  = services.toArray();
            total = new BigDecimal(0);
            BigDecimal totalQty = new BigDecimal(0);
            for (int i = 0; i < datas.length; i++) {
                DoService data = (DoService) datas[i];
                BigDecimal sub = new BigDecimal(0);
                BigDecimal qty1 = new BigDecimal(0);
                TPrice price = priceMaps.get(data.getKode_layanan());
                try {
                    qty1 = new BigDecimal(data.getQty());
                }catch (Exception e){}
                if(price != null){
                    BigDecimal unitPrice = new BigDecimal(0);
                    switch (data.getInt_ac_type()){
                        case 1:
                            unitPrice = price.getPrice1();
                            break;
                        case 2:
                            unitPrice = price.getPrice2();
                            break;
                        case 3:
                            unitPrice = price.getPrice3();
                            break;
                        case 4:
                            unitPrice = price.getPrice4();
                            break;
                        case 5:
                            unitPrice = price.getPrice5();
                            break;
                        case 6:
                            unitPrice = price.getPrice6();
                            break;
                        case 7:
                            unitPrice = price.getPrice7();
                            break;
                        case 8:
                            unitPrice = price.getPrice8();
                            break;
                    }
                    sub = qty1.multiply(unitPrice);
                    total = total.add(sub);
                    totalQty = totalQty.add(qty1);
                    data.setPrice_unit(unitPrice);
                    data.setPrice(sub);
                }
            }
            order.setTotalPrice(total);
            order.setTotalQuantity(totalQty.intValue());
        }
        tvTotalPrice.setText("TOTAL : "+AppConfig.formatCurrency( total.intValue() ));
        next = false;
    }

    public void increment(View view, TextView tv, DoService data){
        int q = 0;
        try{ q = Integer.parseInt(tv.getText().toString());}catch (Exception e){};
        q++;
        tv.setText(""+q);
        data.setQty(q);
    }
    public void decrement(View view, TextView tv,  DoService data){
        int q = 0;
        try{ q = Integer.parseInt(tv.getText().toString());}catch (Exception e){};
        q--;
        if(q < 0) q =0;
        tv.setText(""+q);
        data.setQty(q);
    }

    public LinearLayout generateElementLayanan(final DoService data){
        LinearLayout lin = new LinearLayout(getContext());
        lin.setOrientation(LinearLayout.HORIZONTAL);
        TextView tv = new TextView(getContext());
        tv.setText("Layanan");
        LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT);
        params1.weight = 0.5f;
        //params1.setMargins(5, 5, 5, 5);
        tv.setLayoutParams(params1);
        lin.addView(tv);
        Spinner pilihan1 = new Spinner(getContext());
        pilihan1.setAdapter(adapter);

        lin.addView(pilihan1);
        pilihan1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                data.setKode_layanan(strLayanan[position]);
                data.setInt_layanan(position);

                calculate_price();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        //intLayanan.add(count, 0);

        return lin;
    }
    public LinearLayout generateElementAcType(final DoService data){
        LinearLayout lin = new LinearLayout(getContext());
        lin.setOrientation(LinearLayout.HORIZONTAL);
        TextView tv = new TextView(getContext());
        tv.setText("Tipe AC");
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT);
        params.weight = 0.5f;
        tv.setLayoutParams(params);
        lin.addView(tv);

        final Spinner pilihan1 = new Spinner(getContext());
        pilihan1 .setAdapter(adapter1);
        lin.addView(pilihan1 );
        pilihan1 .setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                data.setJenis_barang(pilihan1.getSelectedItem()==null? null : pilihan1.getSelectedItem().toString());
                data.setInt_ac_type(position);
                calculate_price();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //intAcType.add(count, 0);
        return lin;
    }

    public LinearLayout generateElementJumlah(final DoService data){
        LinearLayout lin = new LinearLayout(getContext());
        lin.setOrientation(LinearLayout.HORIZONTAL);
        TextView tv = new TextView(getContext());
        tv.setText("Quantity ");
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT);
        params.weight = 0.5f;
        tv.setLayoutParams(params);
        lin.addView(tv);

        LinearLayout lin2 = new LinearLayout(getContext());
        lin2.setWeightSum(1);
        LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT);
        params1.gravity = Gravity.CENTER;
        params1.weight = 0.5f;
        lin2.setLayoutParams(params1);
        lin2.setOrientation(LinearLayout.HORIZONTAL);
        AppCompatButton dBtn = new AppCompatButton(getContext());
        AppCompatButton iBtn = new AppCompatButton(getContext());

        LinearLayout.LayoutParams params2 = new LinearLayout.LayoutParams(80, WRAP_CONTENT);
        dBtn.setLayoutParams(params2);
        iBtn.setLayoutParams(params2);

        dBtn.setText("-");
        iBtn.setText("+");

        final TextView pilihan1 = new TextView(getContext());
        pilihan1.setText("0");
        LinearLayout.LayoutParams params3 = new LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT);
        params3.gravity = Gravity.CENTER;
        params3.weight = 0.6f;
        pilihan1.setLayoutParams(params3);
        pilihan1.setGravity(Gravity.CENTER);

        iBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                increment(v, pilihan1, data);
                calculate_price();
            }
        });
        dBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                decrement(v, pilihan1, data);
                calculate_price();
            }
        });


        lin2.addView(dBtn);
        lin2.addView(pilihan1);
        lin2.addView(iBtn);

        lin.addView(lin2);
        return lin;
    }

    public LinearLayout generateElementLokasi(final DoService data){
        LinearLayout lin = new LinearLayout(getContext());
        lin.setOrientation(LinearLayout.HORIZONTAL);
        TextView tv = new TextView(getContext());
        tv.setText("Lokasi ");
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT);
        params.weight = 0.5f;
        tv.setLayoutParams(params);
        lin.addView(tv);

        final Spinner pilihan1 = new Spinner(getContext());
        pilihan1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                data.setLocation(pilihan1.getSelectedItem() == null ? null : pilihan1.getSelectedItem().toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        pilihan1.setAdapter(adapter2);
        LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT);
        params1.weight = 0.5f;
        pilihan1.setLayoutParams(params1);
        lin.addView(pilihan1 );
        return lin;
    }

    public LinearLayout generateElementNotes(final DoService data){
        LinearLayout lin = new LinearLayout(getContext());
        lin.setOrientation(LinearLayout.HORIZONTAL);
        TextView tv = new TextView(getContext());
        tv.setText("Problem / Catatan");
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT);
        params.weight = 0.5f;
        tv.setLayoutParams(params);
        lin.addView(tv);
        EditText tvNote = new EditText(getContext());
        LinearLayout.LayoutParams params2 = new LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT);
        params2.weight = 0.5f;
        tvNote.setLayoutParams(params2);
        tvNote.setGravity(Gravity.LEFT);
        tvNote.setMinLines(1);
        tvNote.setMaxLines(5);
        tvNote.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                data.setNotes(s.toString());
            }
        });
        lin.addView(tvNote);
        return lin;
    }
    private View generateRemoveButton(final CardView cardView, final DoService data) {
        LinearLayout lin = new LinearLayout(getContext());
        lin.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT);
        params.weight = 0.2f;
        params.setMargins(0,0,0, 20);
        Button btnRemove = new Button(getContext());
        btnRemove.setText("Remove");
        btnRemove.setLayoutParams(params);
        btnRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeForm(cardView, data);
            }
        });
        lin.addView(btnRemove);

        TextView tvNote = new TextView(getContext());
        LinearLayout.LayoutParams params2 = new LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT);
        params2.weight = 0.8f;
        tvNote.setLayoutParams(params2);
        lin.addView(tvNote);
        return lin;
    }

    private void removeForm(final CardView cardView, DoService data) {
        baseLayout.removeView(cardView);
        DoServiceHelper.getInstance().removeService(data);
        calculate_price();

    }

    public void generateForm(final int count){
            CardView cv = new CardView(getContext());
            LinearLayout ll = new LinearLayout(new ContextThemeWrapper(context, R.style.Widget_CardContent));
            cv.addView(ll);
            baseLayout.addView(cv);

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            int margin = 8;
            params.setMargins(margin, margin+2, margin, margin);
            cv.setLayoutParams(params);

            DoServiceHelper.getInstance().addOrder(total, AppConfig.KEY_DOSERVICE);
            DoService s = DoServiceHelper.getInstance().addService();

            if(count > 0)
             ll.addView(generateRemoveButton(cv, s));

            ll.addView(generateElementLayanan(s));
            ll.addView(generateElementAcType(s));
            ll.addView(generateElementJumlah(s));
            ll.addView(generateElementLokasi(s));
            ll.addView(generateElementNotes(s));
    }


    @OnClick(R.id.btnAddItem)
    public void btnAddItem_OnClick(){
        baseLayout.removeView(btnAddItem);
        generateForm(item++);
        baseLayout.addView(btnAddItem);
    }

    private void cek_with_calendar_time() {
        Calendar c = Calendar.getInstance();
        hour = c.get(Calendar.HOUR_OF_DAY);
        minute = c.get(Calendar.MINUTE);
        cek_time();
    }

    private void cek_time() {
        if(hour + 1 < AppConfig.START_SDS) {
            hour = AppConfig.START_SDS;
        } else if(hour+1 >= AppConfig.START_ENS) {
            hour = AppConfig.START_SDS;
        }
        tvPickupTime.setText(AppConfig.pad(hour)+":"+AppConfig.pad(minute));
    }

    private TimePickerDialog.OnTimeSetListener timePickerListener = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int selectedHour, int selectedMinute) {
            if(selectedHour < AppConfig.START_SDS || selectedHour >= AppConfig.START_ENS){
                Toast.makeText(context, "Jam pelayanan "+AppConfig.KEY_DOSERVICE+" antara "+AppConfig.START_SDS +" - "+AppConfig.START_ENS, Toast.LENGTH_SHORT).show();
                return;
            }
            hour = selectedHour;
            minute = selectedMinute;
            //tvPickupTime.setText(AppConfig.pad(hour)+":"+AppConfig.pad(minute));
            cek_time();
        }
    };

    @OnClick(R.id.tvPickupTime)
    public void onClick_tvPickupTime(){
        TimePickerDialog d = new TimePickerDialog(context, timePickerListener, hour, minute, true);
        d.show();
    }

    @Override
    public int getName() {
        return R.string.doservice_form1;
    }

    @Override
    public VerificationError verifyStep() {
        if(total.doubleValue() == 0){
            return new VerificationError("Anda belum memilih layanan dan banyaknya tipe AC yang akan diservice");
        }
        TOrder order = DoServiceHelper.getInstance().getOrder();
        Calendar start = Calendar.getInstance();
        start.add(Calendar.DATE, 1);
        start.set(Calendar.HOUR_OF_DAY, hour);
        start.set(Calendar.MINUTE, minute);
        order.setPickup(AppConfig.getSimpleDateFormat().format( start.getTime() ));
        order.setDroptime(null);

        next = true;
        return null;
    }

    @Override
    public void onSelected() {
        if(!next){
            baseLayout.removeView(btnAddItem);
            cek_with_calendar_time();
            setup_spinner();
            DoServiceHelper.getInstance().clearOrder();
            generateForm(item++);
            baseLayout.addView(btnAddItem);
        }
    }

    @Override
    public void onError(@NonNull VerificationError error) {

    }
}
