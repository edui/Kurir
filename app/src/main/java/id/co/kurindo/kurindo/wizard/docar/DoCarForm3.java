package id.co.kurindo.kurindo.wizard.docar;

/**
 * Created by aspire on 3/26/2017.
 */

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.stepstone.stepper.Step;
import com.stepstone.stepper.VerificationError;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

import butterknife.Bind;
import butterknife.OnClick;
import id.co.kurindo.kurindo.R;
import id.co.kurindo.kurindo.app.AppConfig;
import id.co.kurindo.kurindo.comp.ProgressDialogCustom;
import id.co.kurindo.kurindo.helper.DoCarHelper;
import id.co.kurindo.kurindo.helper.ViewHelper;
import id.co.kurindo.kurindo.model.Address;
import id.co.kurindo.kurindo.model.DoCarRental;
import id.co.kurindo.kurindo.wizard.BaseStepFragment;

import static com.android.volley.Request.Method.POST;

/**
 * Created by dwim on 3/15/2017.
 */

public class DoCarForm3 extends BaseStepFragment implements Step {
    private static final String TAG = "DoCarForm3";
    VerificationError invalid = null;

    @Bind(R.id.pilihLokasi)
    Spinner pilihLokasi;

    @Bind(R.id.pilihTujuan)
    Spinner pilihTujuan;

    @Bind(R.id.tvTglMulai)
    TextView tvTglMulai;
    @Bind(R.id.tvTglSelesai)
    TextView tvTglSelesai;

    @Bind(R.id.ivInfoDurasi)
    ImageButton ivInfoDurasi;
    @Bind(R.id.ivInfoFasilitas)
    ImageButton ivInfoFasilitas;

    @Bind(R.id.radio_group_durasi)
    RadioGroup rdGroupDurasi;
    @Bind(R.id.radio_group_fasilitas)
    RadioGroup rdGroupFasilitas;

    ArrayAdapter<CharSequence> adapter;
    ArrayAdapter<CharSequence> adapter2;

    String [] cities = new String[0];

    Context context;
    ProgressDialog progressBar;

    Calendar start;
    Calendar end;
    private boolean mulai;
    private boolean selesai;
    SimpleDateFormat sdf = AppConfig.getDateFormat();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getContext();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflateAndBind(inflater, container, R.layout.fragment_docar3);
        progressBar = new ProgressDialogCustom(context);

        DoCarHelper.getInstance().addRental();
        setupAdapter();
        setupDate();
        setupRadio();
        return v;
    }

    private void setupRadio() {
        rdGroupDurasi.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                String durasi = getString(R.string.label_8jam);
                switch (checkedId){
                    case R.id.radio_8jam:
                        durasi = getString(R.string.label_8jam);
                        break;
                    case R.id.radio_12jam:
                        durasi = getString(R.string.label_12jam);
                        break;
                    case R.id.radio_allin:
                        durasi = getString(R.string.label_fullday);
                        break;
                }
                DoCarHelper.getInstance().getRental().setDurasi(durasi);
            }
        });
        rdGroupDurasi.check(R.id.radio_12jam);

        rdGroupFasilitas.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                String fasilitas = getString(R.string.label_tanpabbm);
                switch (checkedId){
                    case R.id.radio_tanpabbm:
                        fasilitas = getString(R.string.label_tanpabbm);
                        break;
                    case R.id.radio_bbminclude:
                        fasilitas = getString(R.string.label_bbminclude);
                        break;
                    case R.id.radio_allin:
                        fasilitas = getString(R.string.label_allin);
                        break;
                }
                DoCarHelper.getInstance().getRental().setFasilitas(fasilitas);
            }
        });
        rdGroupFasilitas.check(R.id.radio_tanpabbm);
    }

    private void setupDate() {
        start = Calendar.getInstance();
        start.add(Calendar.DATE, 1);
        tvTglMulai.setText(sdf.format(start.getTime()));

        end = Calendar.getInstance();
        end.add(Calendar.DATE, 1);
        //b.add(Calendar.DATE, 2);
        tvTglSelesai.setText(sdf.format(end.getTime()));

        DoCarRental docar = DoCarHelper.getInstance().addRental();
        docar.setDateRange(start.getTime(), end.getTime());
    }

    @OnClick(R.id.tvTglMulai)
    public void OnTvTglMulai_Click(){
        mulai = true;
        DatePickerDialog date = new DatePickerDialog(context, datePickerListener, start.get(Calendar.YEAR), start.get(Calendar.MONTH), start.get(Calendar.DAY_OF_MONTH));
        date.show();
    }

    @OnClick(R.id.tvTglSelesai)
    public void OntvTglSelesai_Click(){
        selesai = true;
        DatePickerDialog date = new DatePickerDialog(context, datePickerListener, end.get(Calendar.YEAR), end.get(Calendar.MONTH), end.get(Calendar.DAY_OF_MONTH));
        date.show();
    }

    private DatePickerDialog.OnDateSetListener datePickerListener
            = new DatePickerDialog.OnDateSetListener() {

        // when dialog box is closed, below method will be called.
        public void onDateSet(DatePicker view, int selectedYear,
                              int selectedMonth, int selectedDay) {
            Calendar a = Calendar.getInstance();
            a.set(selectedYear, selectedMonth, selectedDay);
            Calendar b = Calendar.getInstance();
            b.add(Calendar.DATE, 1);
            if(a.before(b)) a = b;
            if(mulai){
                tvTglMulai.setText(sdf.format(a.getTime()));
                mulai = false;
                start = a;
            }else if(selesai) {
                tvTglSelesai.setText(sdf.format(a.getTime()));
                selesai = false;
                end = a;
            }
            DoCarHelper.getInstance().getRental().setDateRange(start.getTime(), end.getTime());

        }
    };

    private void setupAdapter() {
        adapter = ArrayAdapter.createFromResource(context,R.array.docar_tujuan_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        pilihTujuan.setAdapter(adapter);
        pilihTujuan.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String [] tujuan = getResources().getStringArray(R.array.docar_tujuan_array);
                DoCarHelper.getInstance().getRental().setActivity(tujuan[position]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        pilihTujuan.setSelection(0);

        request_cities();

        adapter2 = new ArrayAdapter<CharSequence>(context, android.R.layout.simple_spinner_item);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        adapter2.addAll(cities);
        pilihLokasi.setAdapter(adapter2);
        pilihLokasi.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(cities.length >= position)
                    DoCarHelper.getInstance().getRental().setCity(cities[position]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void request_cities() {
        progressBar.show();

        String tag_request = "request_cities";
        String url = AppConfig.URL_RETRIEVE_BRANCHES;
        HashMap<String, String> params = new HashMap();
        params.put("do_type", AppConfig.KEY_DOCAR);
        addRequest(tag_request, POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jObj = new JSONObject(response);
                    String message =jObj.getString("message");
                    if(message.equalsIgnoreCase("OK")){
                        Address addr = ViewHelper.getInstance().getLastAddress();
                        adapter2.clear();
                        JSONArray jArr = jObj.getJSONArray("data");
                        cities = new String[jArr.length()];
                        for (int i = 0; i < jArr.length(); i++) {
                            cities[i] = jArr.getString(i);
                            if(addr != null){
                                if(jArr.getString(i).equalsIgnoreCase(addr.getKecamatan())
                                        || jArr.getString(i).equalsIgnoreCase(addr.getKabupaten())
                                        || jArr.getString(i).equalsIgnoreCase(addr.getPropinsi())) {
                                    pilihLokasi.setSelection(i);
                                }
                            }
                        }
                        adapter2.addAll(cities);
                        adapter2.notifyDataSetChanged();
                    }else{
                        Toast.makeText(context, ""+message, Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(context, "Error "+e.getMessage(), Toast.LENGTH_SHORT).show();
                }
                progressBar.dismiss();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Toast.makeText(context, "Network Error ."+volleyError.getMessage(), Toast.LENGTH_SHORT).show();
                progressBar.dismiss();
            }
        }, params, getKurindoHeaders());
    }

    @Override
    public int getName() {
        return R.string.docar_form;
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