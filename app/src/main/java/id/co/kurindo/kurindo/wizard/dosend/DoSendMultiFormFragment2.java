package id.co.kurindo.kurindo.wizard.dosend;

import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.lamudi.phonefield.PhoneInputLayout;
import com.stepstone.stepper.Step;
import com.stepstone.stepper.VerificationError;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import butterknife.Bind;
import butterknife.OnClick;
import id.co.kurindo.kurindo.LoginActivity;
import id.co.kurindo.kurindo.R;
import id.co.kurindo.kurindo.adapter.PacketServiceAdapter;
import id.co.kurindo.kurindo.app.AppConfig;
import id.co.kurindo.kurindo.base.BaseActivity;
import id.co.kurindo.kurindo.comp.ProgressDialogCustom;
import id.co.kurindo.kurindo.helper.DoSendHelper;
import id.co.kurindo.kurindo.helper.SessionManager;
import id.co.kurindo.kurindo.helper.ViewHelper;
import id.co.kurindo.kurindo.model.Address;
import id.co.kurindo.kurindo.model.City;
import id.co.kurindo.kurindo.model.PacketService;
import id.co.kurindo.kurindo.model.TOrder;
import id.co.kurindo.kurindo.model.TPacket;
import id.co.kurindo.kurindo.model.TUser;
import id.co.kurindo.kurindo.util.LogUtil;
import id.co.kurindo.kurindo.wizard.BaseStepFragment;

import static android.widget.Toast.LENGTH_SHORT;

/**
 * Created by dwim on 2/7/2017.
 */

public class DoSendMultiFormFragment2 extends BaseStepFragment implements Step {
    private static final String TAG = "DoSendFormFragment";
    VerificationError invalid = null;

    @Bind(R.id.tvOrigin) TextView tvOrigin;
    @Bind(R.id.rvDestinationList) RecyclerView rvDestinationList;

    @Bind(R.id.input_info_barang) EditText _infoBarangText;
    @Bind(R.id.input_service_code) Spinner _serviceCodeText;
    @Bind(R.id.input_cod) EditText codText;

    @Bind(R.id.input_berat_barang) EditText _beratBarangText;
    @Bind(R.id.incrementBtn) AppCompatButton incrementBtn;
    @Bind(R.id.decrementBtn) AppCompatButton decrementBtn;

    @Bind(R.id.priceText)
    TextView priceText;
    double tariff;
    @Bind(R.id.TextViewTitle)
    TextView TextViewTitle;
    @Bind(R.id.ivProductImage)
    ImageView ivProductImage;

    @Bind(R.id.tvPickupTime)
    TextView tvPickupTime;
    @Bind(R.id.tvPickupTimeText)
    TextView tvPickupTimeText;
    @Bind(R.id.tvDropTimeText)
    TextView tvDropTimeText;
    @Bind(R.id.swChooseTime)
    Switch swChooseTime;

    @Bind(R.id.tvBayarOngkir)
    TextView tvBayarOngkir;
    @Bind(R.id.tvPengirimBayarText)
    TextView tvPengirimBayarText;
    @Bind(R.id.tvPenerimaBayarText)
    TextView tvPenerimaBayarText;
    @Bind(R.id.swChooseBayarOngkir)
    Switch swChooseBayarOngkir;

    @Bind(R.id.chkAgrement)
    CheckBox chkAgrement;
    @Bind(R.id.ivAgrement)
    ImageView ivAgrement;


    ProgressDialog progressBar;

    PacketServiceAdapter packetServiceAdapter;

    private TPacket packet ;
    BigDecimal berat_kiriman = new BigDecimal(1);
    protected float volume = 0;

    private List<PacketService> packetServiceList;
    private boolean inputBaruPengirim;
    private boolean inputBaruPenerima;

    String serviceCode;
    int hour;
    int minute;
    Context context;
    String doType = AppConfig.KEY_DOSEND;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getContext();
        session = new SessionManager(context);
        if(!session.isLoggedIn()){
            ((BaseActivity)getActivity()).showActivity(LoginActivity.class);
            getActivity().finish();
            return ;
        }
        Bundle bundle = getArguments();
        if(bundle != null){
            String d = bundle.getString("doType");
            if(d != null) doType = d;
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflateAndBind(inflater, container, R.layout.fragment_dosend_form_multi2);
        progressBar = new ProgressDialogCustom(context);


        ivAgrement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopupWindow("Kurindo \nService Agreement", R.raw.snk_file, R.drawable.icon_syarat_ketentuan);
            }
        });

        swChooseTime.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                boolean on = isChecked;
                if(on)
                {
                    tvPickupTimeText.setBackgroundResource(R.color.cardview_light_background);
                    tvDropTimeText.setBackgroundResource(R.color.orange);
                }
                else
                {
                    tvPickupTimeText.setBackgroundResource(R.color.orange);
                    tvDropTimeText.setBackgroundResource(R.color.cardview_light_background);
                }
            }
        });

        swChooseBayarOngkir.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                boolean on = isChecked;
                if(on)
                {
                    tvPengirimBayarText.setBackgroundResource(R.color.cardview_light_background);
                    tvPenerimaBayarText.setBackgroundResource(R.color.orange);
                }
                else
                {
                    tvPengirimBayarText.setBackgroundResource(R.color.orange);
                    tvPenerimaBayarText.setBackgroundResource(R.color.cardview_light_background);
                }
            }
        });
        return v;
    }

    private void cek_with_calendar_time() {
        Calendar c = Calendar.getInstance();
        hour = c.get(Calendar.HOUR_OF_DAY);
        minute = c.get(Calendar.MINUTE);
        cek_time();
    }
    private void cek_time() {
        if(serviceCode != null){
            if(AppConfig.isNightService(serviceCode)){
                if(hour+1 >= AppConfig.START_ENS) {
                    hour++;
                } else if(hour+1 >= AppConfig.END_ENS) {
                    hour = AppConfig.START_SDS +2;
                    _serviceCodeText.setSelection(2); //nds
                } else {
                    hour = AppConfig.START_ENS;
                }
            }else if(serviceCode.equalsIgnoreCase(AppConfig.PACKET_SDS)){
                if(hour + 1 < AppConfig.START_SDS) {
                    hour = AppConfig.START_SDS +2;
                    _serviceCodeText.setSelection(0); //sds
                } else if(hour+1 >= AppConfig.END_ENS) {
                    hour = AppConfig.START_SDS +2;
                    _serviceCodeText.setSelection(2); //nds
                } else if(hour+1 >= AppConfig.START_ENS) {
                    hour++;
                    _serviceCodeText.setSelection(2); //nds
                }else{
                    hour++;
                    _serviceCodeText.setSelection(0); //sds
                }
            }else if(serviceCode.equalsIgnoreCase(AppConfig.PACKET_NDS)){
                if(hour + 1 < AppConfig.START_SDS) {
                    hour = AppConfig.START_SDS +2;
                    _serviceCodeText.setSelection(2); //nds
                } else if(hour+1 >= AppConfig.END_ENS) {
                    hour = AppConfig.START_SDS +2;
                } else if(hour+1 >= AppConfig.START_ENS) {
                    hour = AppConfig.START_SDS +2;
                    //hour++;
                    //_serviceCodeText.setSelection(3);
                }
            }
        }
        tvPickupTime.setText(AppConfig.pad(hour)+":"+AppConfig.pad(minute));
    }

    private void setup_time() {
        serviceCode = DoSendHelper.getInstance().getServiceCode();
        _serviceCodeText.setSelection((serviceCode==AppConfig.PACKET_NDS? 0 : (serviceCode==AppConfig.PACKET_SDS ? 1 : (serviceCode==AppConfig.PACKET_ENS ? 2 : 3))));

        cek_with_calendar_time();
        checkTarif();
    }

    private TimePickerDialog.OnTimeSetListener timePickerListener = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int selectedHour, int selectedMinute) {
            if(selectedHour < AppConfig.START_SDS || selectedHour > AppConfig.END_ENS){
                Toast.makeText(context, "Jam pelayanan antara "+AppConfig.START_SDS +" - "+AppConfig.END_ENS, Toast.LENGTH_SHORT).show();
                return;
            }
            /*
            if(selectedHour >= AppConfig.START_ENS && !serviceCode.equalsIgnoreCase(AppConfig.PACKET_ENS)){
                _serviceCodeText.setSelection(2);
                Toast.makeText(context, "Tarif ENS berlaku", Toast.LENGTH_SHORT).show();
            }else if(selectedHour < AppConfig.START_ENS ){
                _serviceCodeText.setSelection(serviceCode.equalsIgnoreCase(AppConfig.PACKET_SDS) ? 1 : 0);
            }*/
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
        //*
    @OnClick(R.id.incrementBtn)
    public void onClick_incrementBtn(){
        //float q = 0;
        //try{ q = Float.parseFloat(_beratBarangText.getText().toString());}catch (Exception e){};
        //q +=0.1;
        BigDecimal b = new BigDecimal(_beratBarangText.getText().toString());
        b = b.add(new BigDecimal(0.1));
        _beratBarangText.setText(""+b.floatValue());

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
        if(b.intValue() < 1) b = new BigDecimal(1);
        _beratBarangText.setText(""+b.floatValue());

        //if(q % 0.5 == 0 && q > 1){checkTarif();        }
    }//*/
    private void setup_service() {
        packetServiceList = getPacketServiceList();
        packetServiceAdapter = new PacketServiceAdapter(context, packetServiceList);
        _serviceCodeText.setAdapter(packetServiceAdapter);
        _serviceCodeText.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                serviceCode= packetServiceList.get(position).getCode();
                DoSendHelper.getInstance().setServiceCode( serviceCode);
                cek_with_calendar_time();
                checkTarif();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void setup_berat_barang() {

        //_beratBarangText.setText("1");
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
                            berat_kiriman = new BigDecimal(v.toString());
                        } catch (Exception e) {
                        }

                        if(berat_kiriman.floatValue() >= AppConfig.MIN_WEIGHT_DOSEND) {
                            checkTarif();
                        }
                    }
                };
                handler.postDelayed(runnable, 800);

            }
        });
    }
    public List<PacketService> getPacketServiceList() {
        if(packetServiceList ==null){
            packetServiceList = AppConfig.getPacketServiceList(doType);
        }
        return packetServiceList;
    }
    private void checkTarif(){
        progressBar.setMessage("Loading. Please wait....");
        progressBar.show();

        GsonBuilder builder = new GsonBuilder();
        builder.setPrettyPrinting(); builder.serializeNulls();
        builder.excludeFieldsWithoutExposeAnnotation();
        Gson gson = builder.create();

        Address originAddr = DoSendHelper.getInstance().getOrigin().getAddress() ;
        Address destinationAddr = DoSendHelper.getInstance().getDestination().getAddress() ;
        HashMap<String, String> params = new HashMap();

        params.put("distance", ""+ DoSendHelper.getInstance().getPacket().getDistance());
        params.put("origin", gson.toJson(originAddr));
        params.put("destination", gson.toJson(destinationAddr));
        params.put("service_code", DoSendHelper.getInstance().getOrder().getService_code());
        params.put("do_type", DoSendHelper.getInstance().getOrder().getService_type());
        params.put("berat_kiriman", ""+berat_kiriman);
        params.put("volume", ""+volume);

        tariff = 0;
        addRequest("request_price_route", Request.Method.POST, AppConfig.URL_CALC_PRICE_KM, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    Log.d(TAG, "requestprice Response: " + response.toString());
                    JSONObject jObj = new JSONObject(response);
                    String status = jObj.getString("status");
                    boolean OK = "OK".equalsIgnoreCase(status);
                    if(OK){
                        tariff = jObj.getDouble("tarif");
                        DoSendHelper.getInstance().getOrder().setTotalPrice( new BigDecimal( tariff ) ) ;
                        priceText.setText(AppConfig.formatCurrency(tariff));
                    }else{
                        Toast.makeText(context, ""+status, LENGTH_SHORT).show();
                    }
                }catch (Exception e){
                    e.printStackTrace();
                    Toast.makeText(context, "Error "+e.getMessage(), LENGTH_SHORT).show();
                }
                progressBar.dismiss();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                volleyError.printStackTrace();
                Toast.makeText(context, "Error "+volleyError.getMessage(), LENGTH_SHORT).show();
                progressBar.dismiss();
            }
        }, params, getKurindoHeaders());

    }

    private void place_an_order(Handler handler) {
        LogUtil.logD(TAG, "place_an_order");

        progressBar.setIndeterminate(true);
        progressBar.setMessage("Sedang memproses pesanan anda....");
        progressBar.show();

        TOrder order = DoSendHelper.getInstance().getOrder();
        DoSendHelper.getInstance().addNewProduct(order.getService_code(), order.getTotalPrice().doubleValue());
        String beratBarang = _beratBarangText.getText().toString();
        String infoBarang = _infoBarangText.getText().toString();

        //String price = priceText.getText().toString();
        //order.setTotalPrice(new BigDecimal(price));

        BigDecimal beratBarangAsli = new BigDecimal(0);
        try{
            beratBarangAsli = new BigDecimal(beratBarang);
        }catch (Exception e){}
        packet = DoSendHelper.getInstance().getPacket();
        packet.setBerat_asli(beratBarangAsli);
        packet.setBerat_kiriman(beratBarangAsli.intValue());
        packet.setIsi_kiriman(infoBarang);
        packet.setBiaya(order.getTotalPrice());
        packet.setDestination( DoSendHelper.getInstance().getDestination());
        packet.setOrigin( DoSendHelper.getInstance().getOrigin());
        packet.setCatatan(packet.getCatatan() +" "+DoSendHelper.getInstance().getDoMoveType());
        Set packets = new LinkedHashSet();
        packets.add(packet);
        order.setPackets(packets);

        if(swChooseTime.isChecked()){
            order.setDroptime(AppConfig.formatPickup(hour, minute, serviceCode));
            order.setPickup(null);
        }else{
            order.setPickup(AppConfig.formatPickup(hour, minute, serviceCode));
            order.setDroptime(null);
        }
        if(swChooseBayarOngkir.isChecked()){
            order.setDibayar(AppConfig.PENERIMA);
        }else{
            order.setDibayar(AppConfig.PENGIRIM);
        }

        TUser user = db.toTUser(db.getUserDetails());
        order.setBuyer(user);

        BigDecimal cod = new BigDecimal(0);
        try{
            cod = new BigDecimal(codText.getText().toString());
        }catch (Exception e){}
        order.setCod(cod);

        Map<String, String> params = packet.getAsParams();
        params.put("user_agent", AppConfig.USER_AGENT);

        GsonBuilder builder = new GsonBuilder();
        builder.setPrettyPrinting(); builder.serializeNulls();
        Gson gson = builder.create();

        String orderStr = gson.toJson(DoSendHelper.getInstance().getOrder());
        LogUtil.logD(TAG, "place_an_order: "+orderStr);
        params.put("order", orderStr);

        process_order(params, handler);
    }

    private void process_order(Map<String, String> params, final Handler handler) {
        String url = AppConfig.URL_DOSEND_ORDER;
        addRequest("request_dosend_order", Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                LogUtil.logD(TAG, "request_dosend_order Response: " + response.toString());
                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean OK = "OK".equalsIgnoreCase(jObj.getString("status"));
                    if(OK){
                        String awb = jObj.getString("awb");
                        String status = jObj.getString("order_status");
                        String statusText = jObj.getString("order_status_text");

                        DoSendHelper.getInstance().getOrder().setAwb(awb);
                        DoSendHelper.getInstance().getOrder().setStatus(status);
                        DoSendHelper.getInstance().getOrder().setStatusText(statusText);

                        ViewHelper.getInstance().setOrder(DoSendHelper.getInstance().getOrder());

                        if(inputBaruPengirim) db.addAddress(DoSendHelper.getInstance().getOrigin());
                        if(inputBaruPenerima) db.addAddress(DoSendHelper.getInstance().getDestination());
                        invalid = null;
                    }
                }catch (JSONException e){
                    e.printStackTrace();
                    invalid = new VerificationError("Json error: " + e.getMessage());
                }
                progressBar.dismiss();
                handler.handleMessage(null);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                volleyError.printStackTrace();
                invalid = new VerificationError("NetworkError : " + volleyError.getMessage());
                progressBar.dismiss();
                handler.handleMessage(null);
            }
        }, params, getKurindoHeaders());
    }

    @Override
    public int getName() {
        return 0;
    }

    @Override
    public VerificationError verifyStep() {

        boolean valid = validate();
        if(!valid){
            return new VerificationError("Invalid Data. Please check.");
        }
        if(tariff == 0){
            return new VerificationError("Invalid Data. Tarif belum terhitung.");
        }

        final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message mesg) {
                throw new RuntimeException("RuntimeException");
            }
        };

        DialogInterface.OnClickListener YesClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                place_an_order(handler);
            }
        };
        showConfirmationDialog("Konfirmasi","Anda akan memesan layanan "+doType+"?", YesClickListener, null);

        // loop till a runtime exception is triggered.
        try { Looper.loop(); }
        catch(RuntimeException e2) { }

        return invalid;
    }

    private boolean validate() {
        boolean valid = true;


        String beratBarang = _beratBarangText.getText().toString();
        String infoBarang = _infoBarangText.getText().toString();

        if (beratBarang.isEmpty() || beratBarang.length() < 1 ) {
            _beratBarangText.setError("Tuliskan berat barang");
            valid = false;
        } else {
            _beratBarangText.setError(null);
        }

        if (infoBarang.isEmpty() || infoBarang.length() < 2 ) {
            _infoBarangText.setError("Tuliskan informasi barang");
            valid = false;
        } else {
            _infoBarangText.setError(null);
        }

        if(valid){
            if(!chkAgrement.isChecked()) {
                Toast.makeText(getActivity(), "Anda belum menyetujui syarat dan ketentuan kami.", Toast.LENGTH_SHORT).show();
                valid = false;
            }
        }
        return valid;
    }

    @Override
    public void onSelected() {
        setup_berat_barang();
        setup_service();
        setup_time();
        updateUI();
    }
    private void updateUI() {
        swChooseTime.setChecked(true);
        swChooseTime.toggle();

        swChooseBayarOngkir.setChecked(true);
        swChooseBayarOngkir.toggle();

        doType = DoSendHelper.getInstance().getDoType();
        ivProductImage.setImageResource((doType.equalsIgnoreCase(AppConfig.KEY_DOSEND)? R.drawable.do_send_icon : R.drawable.do_move_icon));
        TextViewTitle.setText(""+doType+" ( Jarak: "+ AppConfig.formatKm( (DoSendHelper.getInstance().getPacket()==null? 0 :DoSendHelper.getInstance().getPacket().getDistance() ))+")");

        TUser destination = DoSendHelper.getInstance().getDestination();

        TUser origin = DoSendHelper.getInstance().getOrigin();

        BigDecimal berat = DoSendHelper.getInstance().getPacket().getBerat_asli();
        if(berat != null){
            berat_kiriman = berat;
            _beratBarangText.setText(""+berat_kiriman.floatValue());
        }
    }

    @Override
    public void onError(@NonNull VerificationError error) {

    }

    public static Fragment newInstance(String doType) {
        Bundle bundle = new Bundle();
        bundle.putString("doType", doType);
        DoSendMultiFormFragment2 f = new DoSendMultiFormFragment2();
        f.setArguments(bundle);
        return f;
    }

}
