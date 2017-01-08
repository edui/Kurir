package id.co.kurindo.kurindo.wizard.checkout;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.stepstone.stepper.Step;
import com.stepstone.stepper.VerificationError;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import id.co.kurindo.kurindo.R;
import id.co.kurindo.kurindo.RecyclerItemClickListener;
import id.co.kurindo.kurindo.adapter.CityAdapter;
import id.co.kurindo.kurindo.adapter.RecipientAdapter;
import id.co.kurindo.kurindo.app.AppConfig;
import id.co.kurindo.kurindo.app.AppController;
import id.co.kurindo.kurindo.helper.CheckoutHelper;
import id.co.kurindo.kurindo.model.Address;
import id.co.kurindo.kurindo.model.City;
import id.co.kurindo.kurindo.model.Recipient;
import id.co.kurindo.kurindo.wizard.OnNavigationBarListener;

/**
 * Created by DwiM on 12/7/2016.
 */

public class StepSelectRecipientFragment extends BaseStepFragment implements Step {
    private static final String LAYOUT_RESOURCE_ID_ARG_KEY = "messageResourceId";
    private static final String TAG = "StepSelectRecipientFragment";

    @Nullable
    private OnNavigationBarListener onNavigationBarListener;

    RecipientAdapter mRecipientAdapter;
    RecyclerView mRecyclerView;
    ArrayList<Recipient> data = new ArrayList<>();

    LinearLayoutCompat inputLayout;
    int tempPosition = -1;
    CityAdapter cityAdapter;

    @Bind(R.id.input_nama_penerima)
    EditText _namaPenerimaText;
    @Bind(R.id.text_telepon_penerima)
    EditText _teleponPenerimaText;
    @Bind(R.id.input_alamat_penerima)
    EditText _alamatPenerimaText;
    @Bind(R.id.input_kota_penerima)
    Spinner _kotaPenerimaText;
    @Bind(R.id.ButtonAddRecipient)
    AppCompatButton _ButtonAddOrder;

    @Bind(R.id.radio_input_satu)
    RadioButton inputSatuRadio;
    @Bind(R.id.radio_input_banyak)
    RadioButton inputBanyakRadio;
    @Bind(R.id.rdogrp)
    RadioGroup inputTypeRadio;

    @Bind(R.id.rdogrpInput)
    RadioGroup inputRadio;
    @Bind(R.id.radio_inputbaru)
    RadioButton inputBaruRadio;
    @Bind(R.id.radio_pilihlist)
    RadioButton pilihListRadio;

    @Bind(R.id.deleteBtn)
    AppCompatButton deleteBtn;

    @Bind(R.id.layout_inputbaru)
    LinearLayoutCompat inputBaruLayout;
    @Bind(R.id.layout_pilihlist)
    LinearLayoutCompat pilihListLayout;

    @Bind(R.id.rdogrpJenisPengiriman)
    RadioGroup jenisKirimanRadio;

    boolean multiple = false;
    boolean inputBaru = false;
    boolean sdsPacket = false;
    private List<City> cityList = new ArrayList<>();

    public static StepSelectRecipientFragment newInstance(@LayoutRes int layoutResId) {
        Bundle args = new Bundle();
        args.putInt(LAYOUT_RESOURCE_ID_ARG_KEY, layoutResId);
        StepSelectRecipientFragment fragment = new StepSelectRecipientFragment();
        fragment.setArguments(args);
        return fragment;
    }
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnNavigationBarListener) {
            onNavigationBarListener = (OnNavigationBarListener) context;
        }
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //View v = inflateAndBind(inflater, container, R.layout.fragment_select_recipient);
        View v = inflater.inflate(R.layout.fragment_select_recipient, container, false);

        setupToolbar(v);

        data.clear();
        data.addAll(db.getRecipientList());

        /*Address addr1 = new Address();
        addr1.setAlamat("Jl Sumber Agung");
        addr1.setCity(new City("BPN03","Balikpapan xx"));
        Recipient r1 = new Recipient("SomeONe1", "012121111",addr1);
        data.add(r1);*/

        setup_radio_group(v);
        setup_radio_list(v);
        setup_recipient_form(v);

        //setup_spinner(v);

        updateNavigationBar(true);

        return v;
    }

    private void updateNavigationBar(boolean valid) {
        if (onNavigationBarListener != null) {
            onNavigationBarListener.onChangeEndButtonsEnabled(valid);
        }
    }
    @Override
    @StringRes
    public int getName() {
        return R.string.select_recipient;
    }

    @Override
    public VerificationError verifyStep() {
        if(!multiple){
            if(inputBaru){
                if(!checkdata()){
                    return  new VerificationError("Incomplete Form : Inputkan alamat baru atau Pilih salah satu Alamat dari daftar.");
                }
            }else{
                if(CheckoutHelper.getInstance().getRecipients() == null || CheckoutHelper.getInstance().getRecipients().size() == 0){
                    return  new VerificationError("Incomplete Form : Pilih salah satu Alamat dari daftar atau input baru.");
                }
            }
        }else{
            //for (Integer i : mRecipientAdapter.getSelectedItems() ) {
            //    CheckoutHelper.getInstance().getRecipients().add(data.get(i.intValue()));
            //selected.add(data.get(i.intValue()));
            // }
            if(CheckoutHelper.getInstance().getRecipients().size() == 0) {
                return  new VerificationError("Incomplete Form : Pilih salah satu Alamat dari daftar.");
            }
        }
        return null;
    }

    @Override
    public void onSelected() {
    }

    @Override
    public void onError(@NonNull VerificationError error) {
    }


    private void setup_radio_group(View v) {
        jenisKirimanRadio= (RadioGroup)v.findViewById(R.id.rdogrpJenisPengiriman);
        jenisKirimanRadio.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                sdsPacket =  checkedId == R.id.radio_sds;
                CheckoutHelper.getInstance().setPacketType(sdsPacket ? AppConfig.PACKET_SDS : AppConfig.PACKET_NDS);
            }
        });
        CheckoutHelper.getInstance().setPacketType(sdsPacket ? AppConfig.PACKET_SDS : AppConfig.PACKET_NDS);

        //*
        inputTypeRadio = (RadioGroup)v.findViewById(R.id.rdogrp);
        inputSatuRadio = (RadioButton)v.findViewById(R.id.radio_input_satu);
        inputBanyakRadio = (RadioButton)v.findViewById(R.id.radio_input_banyak);
        //*/

        inputTypeRadio.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                mRecipientAdapter.clearSelections();
                multiple = checkedId == R.id.radio_input_banyak;
                if(multiple){
                    _ButtonAddOrder.setVisibility(View.VISIBLE);
                }else{
                    _ButtonAddOrder.setVisibility(View.GONE);
                }
                mRecipientAdapter.setMultipleMode(multiple);
            }
        });

        inputBaruLayout = (LinearLayoutCompat) v.findViewById(R.id.layout_inputbaru);
        pilihListLayout = (LinearLayoutCompat) v.findViewById(R.id.layout_pilihlist);
        inputBaruRadio= (RadioButton) v.findViewById(R.id.radio_inputbaru);
        pilihListRadio= (RadioButton) v.findViewById(R.id.radio_pilihlist);
        inputRadio = (RadioGroup)v.findViewById(R.id.rdogrpInput);
        inputRadio.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                inputBaru =  checkedId == R.id.radio_inputbaru;
                inputBaruLayout.setVisibility( inputBaru? View.VISIBLE: View.GONE);
                pilihListLayout.setVisibility( inputBaru? View.GONE: View.VISIBLE);
                if(inputBaru){
                    CheckoutHelper.getInstance().getRecipients().clear();
                    mRecipientAdapter.clearSelections();
                    mRecipientAdapter.selected(-1);
                }
                mRecipientAdapter.setSelection(inputBaru? -1 : tempPosition);
            }
        });
    }

    private void setup_recipient_form(View v) {
        //
        deleteBtn = (AppCompatButton) v.findViewById(R.id.deleteBtn);
        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckoutHelper.getInstance().getSelectedItems().clear();
                CheckoutHelper.getInstance().getRecipients().clear();
                data.clear();
                mRecipientAdapter.clearSelections();
            }
        });

//*
        _namaPenerimaText = (EditText) v.findViewById(R.id.input_nama_penerima);
         _teleponPenerimaText= (EditText) v.findViewById(R.id.input_telepon_penerima);
        _alamatPenerimaText = (EditText) v.findViewById(R.id.input_alamat_penerima);
        _kotaPenerimaText = (Spinner) v.findViewById(R.id.input_kota_penerima);
        _ButtonAddOrder = (AppCompatButton) v.findViewById(R.id.ButtonAddRecipient);
//*/
        _ButtonAddOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(validate()){
                    Recipient r = new Recipient();
                    r.setName(_namaPenerimaText.getText().toString());
                    r.setTelepon(_teleponPenerimaText.getText().toString());
                    Address addr = new Address();
                    addr.setAlamat(_alamatPenerimaText.getText().toString());
                    addr.setCity(CheckoutHelper.getInstance().getCity());
                    r.setAddress(addr);
                    db.addRecipient(r.getName(), r.getTelepon(), r.getAddress().getAlamat(), r.getAddress().getCity().getCode(), r.getAddress().getCity().getText());
                    data.add(r);
                    mRecipientAdapter.notifyDataSetChanged();
                    addSelection(data.indexOf(r));
                    pilihListRadio.setChecked(true);
                    clear_recipient_form();
                }
            }
        });

        cityAdapter = new CityAdapter(getActivity(), cityList);
        _kotaPenerimaText.setAdapter(cityAdapter);
        _kotaPenerimaText.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                City city = ((City) parent.getItemAtPosition(position));
                CheckoutHelper.getInstance().setCity(city);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        request_city("KEC");
        //cityList.add(new City("BPN01","Balikpapan 01"));
        //cityList.add(new City("BPN02","Balikpapan 02"));
    }

    private void clear_recipient_form() {
        _namaPenerimaText.setText(null);
        _teleponPenerimaText.setText(null);
        _alamatPenerimaText.setText(null);
        _kotaPenerimaText.setSelected(false);
    }

    private boolean validate() {
        boolean valid = true;
        if(_namaPenerimaText.getText() == null || _namaPenerimaText.getText().toString().isEmpty()){
            _namaPenerimaText.setError("Tuliskan nama Penerima");
            valid = false;
        }else{
            _namaPenerimaText.setError(null);
        }

        if (_teleponPenerimaText.getText() == null || _teleponPenerimaText.getText().toString().isEmpty() || _teleponPenerimaText.length() < 4 ) {
            _teleponPenerimaText.setError("Tuliskan telepon Penerima");
            valid = false;
        } else {
            _teleponPenerimaText.setError(null);
        }

        if (_alamatPenerimaText.getText() == null ||_alamatPenerimaText.getText().toString().isEmpty() || _alamatPenerimaText.length() < 4 ) {
            _alamatPenerimaText.setError("Tuliskan alamat Penerima");
            valid = false;
        } else {
            _alamatPenerimaText.setError(null);
        }
        return valid;
    }
    private boolean checkdata() {
        boolean valid = true;
        if(_namaPenerimaText.getText() == null || _namaPenerimaText.getText().toString().isEmpty()){
            valid = false;
        }

        if (_teleponPenerimaText.getText() == null || _teleponPenerimaText.getText().toString().isEmpty() || _teleponPenerimaText.length() < 4 ) {
            valid = false;
        }

        if (_alamatPenerimaText.getText() == null ||_alamatPenerimaText.getText().toString().isEmpty() || _alamatPenerimaText.length() < 4 ) {
            valid = false;
        }
        Address addr= new Address();
        addr.setAlamat(_alamatPenerimaText.getText().toString());
        addr.setCity(CheckoutHelper.getInstance().getCity());
        Recipient r = new Recipient(_namaPenerimaText.getText().toString(), _teleponPenerimaText.getText().toString(), addr);
        db.addRecipient(r);
        CheckoutHelper.getInstance().addRecipient(r);
        return valid;
    }

   @Override
    public void onResume() {
        super.onResume();
        Object[] selecteds  = CheckoutHelper.getInstance().getSelectedItems().toArray();
        for (int i = 0; i < CheckoutHelper.getInstance().getSelectedItems().size(); i++) {
            int key = Integer.parseInt( selecteds[i].toString() );
            mRecipientAdapter.selected(key);
            mRecipientAdapter.setSelection(key);
            Log.e("--------",""+key);
        }
    }


    private void setup_radio_list(View v) {
        mRecyclerView = (RecyclerView) v.findViewById(R.id.list);
        mRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 1));
        mRecyclerView.setHasFixedSize(true);

        mRecipientAdapter = new RecipientAdapter(getContext(), data);
        mRecyclerView.setAdapter(mRecipientAdapter);
        mRecyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getContext(),
                new RecyclerItemClickListener.OnItemClickListener() {

                    @Override
                    public void onItemClick(View view, int position) {
                        addSelection(position);
                    }
                }));

        mRecipientAdapter.clearSelections();
        Object[] selecteds  = CheckoutHelper.getInstance().getSelectedItems().toArray();
        for (int i = 0; i < CheckoutHelper.getInstance().getSelectedItems().size(); i++) {
            int key = Integer.parseInt( selecteds[i].toString() );
            mRecipientAdapter.selected(key);
            mRecipientAdapter.setSelection(key);
        }
    }

    private void addSelection(int position) {
        mRecipientAdapter.selected(position);
        mRecipientAdapter.setSelection(position);
        Recipient r = data.get(position);
        if(!multiple){
            CheckoutHelper.getInstance().getRecipients().clear();
            CheckoutHelper.getInstance().clearSelections();
        }
        CheckoutHelper.getInstance().getRecipients().add(r);
        CheckoutHelper.getInstance().setCity(r.getAddress().getCity());
        CheckoutHelper.getInstance().setSelection(position);
    }

    private void request_city(String... params) {
        final String param = params[0].toString();
        String param2 = null;
        if(params.length > 1) param2 = params[1].toString();
        String URI = AppConfig.URL_LIST_CITY;
        URI = URI.replace("{type}", param);
        if(param2 == null || param2.isEmpty()){
            URI = URI.replace("/{parent}", "");
        }else{
            URI = URI.replace("{parent}", param2);
        }

        StringRequest cityReq = new StringRequest(Request.Method.GET,
                URI, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Register Response: " + response.toString());
                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean success = jObj.getBoolean("success");
                    if (success) {
                        JSONArray cities = jObj.getJSONArray("data");
                        for (int j = 0; j < cities.length(); j++) {
                            City city = new City(cities.getJSONObject(j));
                            cityList.add(city);
                        }
                        cityAdapter.notifyDataSetChanged();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Registration Error: " + error.getMessage());
                Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_LONG).show();

            }
        });

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(cityReq);
    }

}
