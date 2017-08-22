package id.co.kurindo.kurindo.wizard.doservice;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatButton;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.stepstone.stepper.Step;
import com.stepstone.stepper.VerificationError;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashMap;

import butterknife.Bind;
import id.co.kurindo.kurindo.R;
import id.co.kurindo.kurindo.app.AppConfig;
import id.co.kurindo.kurindo.helper.DoServiceHelper;
import id.co.kurindo.kurindo.model.TPrice;
import id.co.kurindo.kurindo.util.LogUtil;
import id.co.kurindo.kurindo.util.ParserUtil;
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

    BigDecimal total = new BigDecimal(0);
    HashMap<String, TPrice> priceMaps = new HashMap<>();
    String [] strLayanan = {"", "CUCI", "SERVICE", "CUCISERVICE", "PASANG", "BONGKAR", "RELOKASI"};
    int intLayanan;
    int intAcType;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflateAndBind(inflater, container, R.layout.fragment_doservice1);

        progressDialog = new ProgressDialog(getActivity(), CustomDialog);

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
                LogUtil.logD(TAG, tag_string_Req+" Response: " + response.toString());
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
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),R.array.doservice_layanan_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        pilihLayanan.setAdapter(adapter);
        pilihLayanan.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //price = (position == 0 ? null : priceMaps.get(strLayanan[position-1]));
                intLayanan =  position;
                calculate_price();
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
                intAcType = position;
                calculate_price();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(getContext(),R.array.doservice_lokasi_array, android.R.layout.simple_spinner_item);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        pilihLokasi.setAdapter(adapter2);

        if(DoServiceHelper.getInstance().getDoService() != null){
            pilihLayanan.setSelection(Arrays.asList(strLayanan).indexOf(DoServiceHelper.getInstance().getDoService().getKode_layanan() ));
            pilihTipeAc.setSelection( Arrays.asList(getResources().getStringArray(R.array.doservice_tipeac_array)).indexOf(DoServiceHelper.getInstance().getDoService().getJenis_barang()));
            pilihLokasi.setSelection( Arrays.asList(getResources().getStringArray(R.array.doservice_lokasi_array)).indexOf(DoServiceHelper.getInstance().getDoService().getLocation()));
        }
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
        if(intLayanan > 0 && intAcType > 0){
            BigDecimal qty1 = new BigDecimal(0);

            TPrice price = (intLayanan == 0 ? null : priceMaps.get(strLayanan[intLayanan]));
            try {
                qty1 = new BigDecimal(quantityStr.getText().toString());
            }catch (Exception e){}
            if(price != null){
                total = qty1.multiply(
                        (intAcType == 1? price.getPrice1():
                                (intAcType == 2? price.getPrice2():
                                        (intAcType == 3? price.getPrice3():
                                                (intAcType == 4? price.getPrice4() :
                                                        (intAcType == 5? price.getPrice5() :
                                                                (intAcType == 6? price.getPrice6() :
                                                                    (intAcType == 7? price.getPrice7() : price.getPrice8())
                                                                )
                                                        )
                                                )
                                        )
                                )
                        )
                );
            }
            tvTotalPrice.setText("TOTAL : "+AppConfig.formatCurrency( total.intValue() ));
        }
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
        if(intLayanan == 0 && intAcType == 0){
            return new VerificationError("Pilih layanan dan tipe AC");
        }
        int q = 0;
        try{ q = Integer.parseInt(quantityStr.getText().toString());}catch (Exception e){};
        if(q == 0){
            return new VerificationError("Berapa unit AC yang akan di-"+strLayanan[intLayanan]+"?");
        }

        //DoServiceHelper.getInstance().addOrder(strLayanan[intLayanan], pilihTipeAc.getSelectedItem().toString(), q, pilihLokasi.getSelectedItem().toString(), "", total );

        return null;
    }

    @Override
    public void onSelected() {
        retrieve_price();
        setup_spinner();
        setup_button();
    }

    @Override
    public void onError(@NonNull VerificationError error) {

    }
}
