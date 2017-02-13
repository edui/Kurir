package id.co.kurindo.kurindo.wizard.doshop;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.GridLayoutManager;
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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
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
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import butterknife.Bind;
import butterknife.OnClick;
import id.co.kurindo.kurindo.LoginActivity;
import id.co.kurindo.kurindo.R;
import id.co.kurindo.kurindo.adapter.CityAdapter;
import id.co.kurindo.kurindo.adapter.PacketServiceAdapter;
import id.co.kurindo.kurindo.adapter.TUserAdapter;
import id.co.kurindo.kurindo.app.AppConfig;
import id.co.kurindo.kurindo.base.BaseActivity;
import id.co.kurindo.kurindo.base.RecyclerItemClickListener;
import id.co.kurindo.kurindo.helper.OrderViaMapHelper;
import id.co.kurindo.kurindo.helper.SessionManager;
import id.co.kurindo.kurindo.model.City;
import id.co.kurindo.kurindo.model.PacketService;
import id.co.kurindo.kurindo.model.TOrder;
import id.co.kurindo.kurindo.model.TPacket;
import id.co.kurindo.kurindo.model.TUser;
import id.co.kurindo.kurindo.wizard.BaseStepFragment;

import static id.co.kurindo.kurindo.R.style.CustomDialog;

/**
 * Created by dwim on 2/7/2017.
 */

public class DoShopFormFragment extends BaseStepFragment implements Step {
    private static final String TAG = "DoSendFormFragment";
    VerificationError invalid = null;

    @Bind(R.id.input_nama_pengirim)    EditText _namaPengirimText;
    @Bind(R.id.input_alamat_pengirim) EditText _alamatPengirimText;
    @Bind(R.id.input_telepon_pengirim)    PhoneInputLayout _teleponPengirimText;

    @Bind(R.id.input_nama_penerima) EditText _namaPenerimaText;
    @Bind(R.id.input_alamat_penerima) EditText _alamatPenerimaText;
    @Bind(R.id.input_telepon_penerima) PhoneInputLayout _teleponPenerimaText;

    @Bind(R.id.input_info_barang) EditText _infoBarangText;
    @Bind(R.id.input_service_code) Spinner _serviceCodeText;

    @Bind(R.id.input_berat_barang) EditText _beratBarangText;
    @Bind(R.id.incrementBtn) AppCompatButton incrementBtn;
    @Bind(R.id.decrementBtn) AppCompatButton decrementBtn;

    @Bind(R.id.priceText)
    TextView priceText;
    @Bind(R.id.TextViewTitle)
    TextView TextViewTitle;


    @Bind(R.id.chkAgrement)
    CheckBox chkAgrement;
    @Bind(R.id.ivAgrement)
    ImageView ivAgrement;

    @Bind(R.id.rdogrpInputPenerima)
    RadioGroup inputModePenerimaRadioGroup;
    @Bind(R.id.rdogrpInputPengirim)
    RadioGroup inputModePengirimRadioGroup;

    @Bind(R.id.layout_inputbaru_penerima)
    LinearLayoutCompat inputBaruPenerimaLayout;
    @Bind(R.id.layout_pilihlist_penerima)
    LinearLayoutCompat pilihListPenerimaLayout;

    @Bind(R.id.layout_inputbaru_pengirim)
    LinearLayoutCompat inputBaruPengirimLayout;
    @Bind(R.id.layout_pilihlist_pengirim)
    LinearLayoutCompat pilihListPengirimLayout;

    @Bind(R.id.listPengirim)
    RecyclerView pilihListPengirim;
    @Bind(R.id.listPenerima)
    RecyclerView pilihListPenerima;

    TUserAdapter mPenerimaRecipientAdapter;
    TUserAdapter mPengirimRecipientAdapter;
    TUser sender;
    TUser receiver;

    ProgressDialog progressDialog;
    SessionManager session;

    CityAdapter cityPengirimAdapter;
    CityAdapter cityPenerimaAdapter;
    PacketServiceAdapter packetServiceAdapter;
    ArrayList<TUser> data = new ArrayList<>();

    private TPacket packet ;
    //private City kota_pengirim;
    //private City kota_penerima;
    float berat_kiriman = 1;

    private List<City> cityList = new ArrayList<>();
    private List<PacketService> packetServiceList;
    private boolean inputBaruPengirim;
    private boolean inputBaruPenerima;
    private Map<String, String> headers;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        session = new SessionManager(getContext());
        if(!session.isLoggedIn()){
            ((BaseActivity)getActivity()).showActivity(LoginActivity.class);
            getActivity().finish();
            return ;
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflateAndBind(inflater, container, R.layout.fragment_order_dosend);

        progressDialog = new ProgressDialog(getActivity(), CustomDialog);
        ivAgrement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopupWindow("Kurindo \nService Agreement", R.raw.snk_file, R.drawable.icon_syarat_ketentuan);
            }
        });

        setup_berat_barang();
        setup_radio();
        setup_service();
        return v;
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
        if(b.floatValue() < 1) b = new BigDecimal(1);
        _beratBarangText.setText(""+b.floatValue());

        //if(q % 0.5 == 0 && q > 1){checkTarif();        }
    }//*/
    private void setup_service() {
        packetServiceList = getPacketServiceList();
        packetServiceAdapter = new PacketServiceAdapter(getContext(), packetServiceList);
        _serviceCodeText.setAdapter(packetServiceAdapter);
        _serviceCodeText.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String serviceCode= packetServiceList.get(position).getCode();
                OrderViaMapHelper.getInstance().setServiceCode( serviceCode);
                checkTarif();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void setup_radio() {
        data.clear();
        data.addAll(db.getAddressList());

        mPenerimaRecipientAdapter = new TUserAdapter(getActivity(), data);
        pilihListPenerima.setLayoutManager(new GridLayoutManager(getContext(), 1));
        pilihListPenerima.setHasFixedSize(true);
        pilihListPenerima.setAdapter(mPenerimaRecipientAdapter);
        pilihListPenerima.addOnItemTouchListener(new RecyclerItemClickListener(getContext(), new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                mPenerimaRecipientAdapter.selected(position);
                mPenerimaRecipientAdapter.setSelection(position);
                receiver = data.get(position);
                checkTarif();
            }
        }));
        int idx = data.indexOf(OrderViaMapHelper.getInstance().getDestination());
        if(idx >= 0) {
            mPenerimaRecipientAdapter.selected(idx);
            mPenerimaRecipientAdapter.setSelection(idx);
        }

        mPengirimRecipientAdapter = new TUserAdapter(getActivity(), data);
        pilihListPengirim.setLayoutManager(new GridLayoutManager(getContext(), 1));
        pilihListPengirim.setHasFixedSize(true);
        pilihListPengirim.setAdapter(mPengirimRecipientAdapter);
        pilihListPengirim.addOnItemTouchListener(new RecyclerItemClickListener(getContext(), new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                mPengirimRecipientAdapter .selected(position);
                mPengirimRecipientAdapter .setSelection(position);
                sender = data.get(position);
                checkTarif();
            }
        }));
        idx = data.indexOf(OrderViaMapHelper.getInstance().getOrigin());
        if(idx >= 0) {
            mPengirimRecipientAdapter.selected(idx);
            mPengirimRecipientAdapter.setSelection(idx);
        }

        inputModePengirimRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                inputBaruPengirim = checkedId == R.id.radio_inputbaru_pengirim;
                inputBaruPengirimLayout.setVisibility(inputBaruPengirim? View.VISIBLE : View.GONE);
                pilihListPengirimLayout.setVisibility(inputBaruPengirim? View.GONE: View.VISIBLE);

                if(inputBaruPengirim) {
                    sender = null;
                    TUser pengirim = OrderViaMapHelper.getInstance().getOrigin();
                    _alamatPengirimText.setText(pengirim.getAddress().toStringFormatted());
                }
            }
        });
        inputModePenerimaRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                inputBaruPenerima = checkedId == R.id.radio_inputbaru_penerima;
                inputBaruPenerimaLayout.setVisibility(inputBaruPenerima? View.VISIBLE : View.GONE);
                pilihListPenerimaLayout.setVisibility(inputBaruPenerima? View.GONE: View.VISIBLE);
                if(inputBaruPenerima) {
                    receiver= null;
                    TUser user = OrderViaMapHelper.getInstance().getDestination();
                    _alamatPenerimaText.setText(user.getAddress().toStringFormatted());
                }
            }
        });

        if(data.size() > 0){
            inputBaruPenerima = false;
            inputBaruPengirim = false;
            inputModePenerimaRadioGroup.check(R.id.radio_pilihlist_penerima);
            inputModePengirimRadioGroup.check(R.id.radio_pilihlist_pengirim);
        }else{
            inputBaruPenerima = true;
            inputBaruPengirim = true;
            inputModePenerimaRadioGroup.check(R.id.radio_inputbaru_penerima);
            inputModePengirimRadioGroup.check(R.id.radio_inputbaru_pengirim);
        }
        inputBaruPenerimaLayout.setVisibility( data.size() > 0? View.GONE : View.VISIBLE );
        pilihListPenerimaLayout.setVisibility( data.size() > 0? View.VISIBLE : View.GONE );
        inputBaruPengirimLayout.setVisibility( data.size() > 0? View.GONE : View.VISIBLE );
        pilihListPengirimLayout.setVisibility( data.size() > 0? View.VISIBLE : View.GONE );

        _teleponPengirimText.setDefaultCountry(AppConfig.DEFAULT_COUNTRY);
        _teleponPenerimaText.setDefaultCountry(AppConfig.DEFAULT_COUNTRY);
    }
    private void setup_berat_barang() {

        _beratBarangText.setText("1");
        _beratBarangText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable v) {
                try {
                    berat_kiriman = Float.parseFloat(v.toString());
                } catch (Exception e) {
                }
                ;
                if(berat_kiriman > 0 && (berat_kiriman % 0.25 == 0) ) checkTarif();
            }
        });

        TextViewTitle.setText("Manifest Paket ( Jarak: "+ AppConfig.formatKm( OrderViaMapHelper.getInstance().getPacket().getDistance() )+")");
    }
    public List<PacketService> getPacketServiceList() {
        if(packetServiceList ==null){
            packetServiceList = AppConfig.getPacketServiceList();
        }
        return packetServiceList;
    }
    private void checkTarif(){
        HashMap<String, String> params = new HashMap();

        params.put("distance", ""+OrderViaMapHelper.getInstance().getPacket().getDistance());
        params.put("origin", (inputBaruPengirim? (sender ==null? "": sender.getAddress().getKecamatan()) : OrderViaMapHelper.getInstance().getOrigin().getAddress().getKecamatan() ));
        params.put("destination", (inputBaruPenerima? (receiver ==null? "": receiver.getAddress().getKecamatan()) : OrderViaMapHelper.getInstance().getDestination().getAddress().getKecamatan() ));
        params.put("service_code", OrderViaMapHelper.getInstance().getOrder().getService_code());
        params.put("do_type", OrderViaMapHelper.getInstance().getOrder().getService_type());
        params.put("berat_kiriman", _beratBarangText.getText().toString());
        params.put("volume", "0");

        addRequest("request_price_route", Request.Method.POST, AppConfig.URL_PRICE_KM, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    Log.d(TAG, "requestprice Response: " + response.toString());
                    JSONObject jObj = new JSONObject(response);
                    boolean OK = "OK".equalsIgnoreCase(jObj.getString("status"));
                    if(OK){
                        double tariff = jObj.getDouble("tarif");
                        OrderViaMapHelper.getInstance().getOrder().setTotalPrice( new BigDecimal( tariff ) ) ;
                        priceText.setText(AppConfig.formatCurrency(tariff));
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                volleyError.printStackTrace();
            }
        }, params, null);

    }

    private void place_an_order(Handler handler) {
        Log.d(TAG, "place_an_order");

        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Processing your Order....");
        progressDialog.show();

        if(inputBaruPengirim){
            String namaPengirim = _namaPengirimText.getText().toString();
            //String alamatPengirim = _alamatPengirimText.getText().toString();
            String teleponPengirim =  _teleponPengirimText.getPhoneNumber();
            //if(teleponPengirim.startsWith("0")){ teleponPengirim = _teleponPengirimText.getPhoneNumber().getCountryCode()+""+teleponPengirim;            }
            TUser origin = OrderViaMapHelper.getInstance().getOrigin();
            if(origin == null) origin = new TUser();
            origin.setFirstname(namaPengirim);
            origin.setPhone(teleponPengirim);
            OrderViaMapHelper.getInstance().setOrigin(origin);

            //db.addRecipient(namaPengirim,teleponPengirim,genderPengirim,alamatPengirim,kota_pengirim.getCode(), kota_pengirim.getText());
        }else{
            OrderViaMapHelper.getInstance().setOrigin(sender);
        }

        if(inputBaruPenerima){
            String namaPenerima= _namaPenerimaText.getText().toString();
            //String alamatPenerima = _alamatPenerimaText.getText().toString();
            String teleponPenerima =  _teleponPengirimText.getPhoneNumber();
            //if(teleponPenerima.startsWith("0")){ teleponPenerima = _teleponPenerimaText.getPhoneNumber().getCountryCode()+""+teleponPenerima; }
            TUser destination = OrderViaMapHelper.getInstance().getDestination();
            if(destination == null) destination = new TUser();
            destination.setFirstname(namaPenerima);
            destination.setPhone(teleponPenerima);

            OrderViaMapHelper.getInstance().setDestination(destination);
            //db.addRecipient(namaPenerima,teleponPenerima, genderPenerima, alamatPenerima,kota_penerima.getCode(), kota_penerima.getText());
        }else{
            OrderViaMapHelper.getInstance().setDestination(receiver);
        }

        TOrder order = OrderViaMapHelper.getInstance().getOrder();

        String beratBarang = _beratBarangText.getText().toString();
        String infoBarang = _infoBarangText.getText().toString();

        //String price = priceText.getText().toString();
        //order.setTotalPrice(new BigDecimal(price));

        BigDecimal beratBarangAsli = new BigDecimal(0);
        try{
            beratBarangAsli = new BigDecimal(beratBarang);
        }catch (Exception e){}
        packet = OrderViaMapHelper.getInstance().getPacket();
        packet.setBerat_asli(beratBarangAsli);
        packet.setBerat_kiriman(beratBarangAsli.intValue());
        packet.setIsi_kiriman(infoBarang);
        packet.setBiaya(order.getTotalPrice());
        Set packets = new LinkedHashSet();
        packets.add(packet);
        order.setPackets(packets);

        TUser user = db.toTUser(db.getUserDetails());
        order.setBuyer(user);


        Map<String, String> params = packet.getAsParams();
        params.put("user_agent", AppConfig.USER_AGENT);

        GsonBuilder builder = new GsonBuilder();
        builder.setPrettyPrinting(); builder.serializeNulls();
        Gson gson = builder.create();

        String orderStr = gson.toJson(OrderViaMapHelper.getInstance().getOrder());
        Log.d(TAG, "place_an_order: "+orderStr);
        params.put("order", orderStr);

        process_order(params, handler);
    }

    private void process_order(Map<String, String> params, final Handler handler) {
        String url = AppConfig.URL_DOSEND_ORDER;
        addRequest("request_dosend_order", Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "request_dosend_order Response: " + response.toString());
                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean OK = "OK".equalsIgnoreCase(jObj.getString("status"));
                    if(OK){
                        String awb = jObj.getString("awb");
                        String status = jObj.getString("order_status");
                        String statusText = jObj.getString("order_status_text");

                        OrderViaMapHelper.getInstance().getOrder().setAwb(awb);
                        OrderViaMapHelper.getInstance().getOrder().setStatus(status);
                        OrderViaMapHelper.getInstance().getOrder().setStatusText(statusText);

                        if(inputBaruPenerima) db.addAddress(OrderViaMapHelper.getInstance().getOrigin());
                        if(inputBaruPengirim) db.addAddress(OrderViaMapHelper.getInstance().getDestination());
                    }
                }catch (JSONException e){
                    e.printStackTrace();
                    invalid = new VerificationError("Json error: " + e.getMessage());
                }
                progressDialog.dismiss();
                handler.handleMessage(null);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                volleyError.printStackTrace();
                invalid = new VerificationError("VolleyError : " + volleyError.getMessage());
                progressDialog.dismiss();
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

        final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message mesg) {
                throw new RuntimeException();
            }
        };

        DialogInterface.OnClickListener YesClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                place_an_order(handler);
            }
        };
        showConfirmationDialog("Confirm Order","Anda Yakin akan membeli produk tersebut?", YesClickListener, null);

        // loop till a runtime exception is triggered.
        try { Looper.loop(); }
        catch(RuntimeException e2) { }

        return invalid;
    }

    private boolean validate() {
        boolean valid = true;

        if(inputBaruPengirim){
            String namaPengirim = _namaPengirimText.getText().toString();
            //String teleponPengirim = _teleponPengirimText.getText().toString();
            String alamatPengirim = _alamatPengirimText.getText().toString();
            //String kotaPengirim = _kotaPengirimText.getText().toString();

            if (namaPengirim.isEmpty() || namaPengirim.length() < 4 ) {
                _namaPengirimText.setError("Tuliskan nama Pengirim");
                valid = false;
            } else {
                _namaPengirimText.setError(null);
            }

            if (!_teleponPengirimText.isValid() ) {
                Toast.makeText(getContext(), "Invalid Telepon Pengirim.", Toast.LENGTH_SHORT).show();
                valid = false;
            }

            if (alamatPengirim.isEmpty() || alamatPengirim.length() < 4 ) {
                _alamatPengirimText.setError("Tuliskan alamat Pengirim");
                valid = false;
            } else {
                _alamatPengirimText.setError(null);
            }

        }else{
            if(sender ==null){
                Toast.makeText(getContext(),"Pilih Pengirim dari daftar atau inputkan baru.",Toast.LENGTH_LONG);
                valid=false;
            }
        }

        if(inputBaruPenerima){
            String namaPenerima= _namaPenerimaText.getText().toString();
            //String teleponPenerima = _teleponPenerimaText.getText().toString();
            String alamatPenerima = _alamatPenerimaText.getText().toString();
            //String kotaPenerima = _kotaPenerimaText.getText().toString();

            if (namaPenerima.isEmpty() || namaPenerima.length() < 4 ) {
                _namaPenerimaText.setError("Tuliskan nama Penerima");
                valid = false;
            } else {
                _namaPenerimaText.setError(null);
            }

            if (!_teleponPenerimaText.isValid()) {
                Toast.makeText(getContext(), "Invalid Telepon Penerima.", Toast.LENGTH_SHORT).show();
                valid = false;
            }

            if (alamatPenerima.isEmpty() || alamatPenerima.length() < 4 ) {
                _alamatPenerimaText.setError("Tuliskan alamat Penerima");
                valid = false;
            } else {
                _alamatPenerimaText.setError(null);
            }
/*
        if (kotaPenerima.isEmpty() || kotaPenerima.length() < 4 ) {
            _kotaPenerimaText.setError("Tuliskan kota Penerima");
            valid = false;
        } else {
            _kotaPenerimaText.setError(null);
        }
*/
        }else{
            if(receiver ==null){
                Toast.makeText(getContext(),"Pilih Penerima dari daftar atau inputkan baru.",Toast.LENGTH_LONG);
                valid=false;
            }
        }

        String beratBarang = _beratBarangText.getText().toString();
        String infoBarang = _infoBarangText.getText().toString();

        if (beratBarang.isEmpty() || beratBarang.length() < 1 ) {
            _beratBarangText.setError("Tuliskan berat barang");
            valid = false;
        } else {
            _beratBarangText.setError(null);
        }

        if (infoBarang.isEmpty() || infoBarang.length() < 4 ) {
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

    }

    @Override
    public void onError(@NonNull VerificationError error) {

    }

}
