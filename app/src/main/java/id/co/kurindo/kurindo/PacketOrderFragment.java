package id.co.kurindo.kurindo;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import net.rimoto.intlphoneinput.IntlPhoneInput;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import id.co.kurindo.kurindo.adapter.CityAdapter;
import id.co.kurindo.kurindo.adapter.PacketServiceAdapter;
import id.co.kurindo.kurindo.adapter.RecipientAdapter;
import id.co.kurindo.kurindo.app.AppConfig;
import id.co.kurindo.kurindo.app.AppController;
import id.co.kurindo.kurindo.base.BaseActivity;
import id.co.kurindo.kurindo.base.BaseFragment;
import id.co.kurindo.kurindo.helper.SessionManager;
import id.co.kurindo.kurindo.model.City;
import id.co.kurindo.kurindo.model.Packet;
import id.co.kurindo.kurindo.model.PacketService;
import id.co.kurindo.kurindo.model.Recipient;
import id.co.kurindo.kurindo.model.StatusHistory;
import id.co.kurindo.kurindo.model.User;

import static android.app.Activity.RESULT_OK;
import static id.co.kurindo.kurindo.R.style.CustomDialog;

/**
 * Created by DwiM on 12/13/2016.
 */

public class PacketOrderFragment extends BaseFragment {
    private static final String TAG = "PacketOrderFragment";
    private static final int REQUEST_PACKET_SHOW = 0;

    @Bind(R.id.input_nama_pengirim)
    EditText _namaPengirimText;
    @Bind(R.id.input_alamat_pengirim) EditText _alamatPengirimText;
    @Bind(R.id.input_telepon_pengirim) IntlPhoneInput _teleponPengirimText;
    @Bind(R.id.input_kota_pengirim)
    Spinner _kotaPengirimText;

    @Bind(R.id.input_nama_penerima) EditText _namaPenerimaText;
    @Bind(R.id.input_alamat_penerima) EditText _alamatPenerimaText;
    @Bind(R.id.input_telepon_penerima) IntlPhoneInput _teleponPenerimaText;
    @Bind(R.id.input_kota_penerima) Spinner _kotaPenerimaText;

    @Bind(R.id.input_berat_barang) EditText _beratBarangText;
    @Bind(R.id.chkViaMobil)
    CheckBox _viaMobilChk;
    @Bind(R.id.input_info_barang) EditText _infoBarangText;
    @Bind(R.id.input_service_code) Spinner _serviceCodeText;
    @Bind(R.id.ButtonAddOrder)
    AppCompatButton _buttonAddOrder;

    @Bind(R.id.priceText)
    TextView priceText;
    @Bind(R.id.pickcontact)
    ImageButton pickContactBtn;

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

    @Bind(R.id.gender_spinner_penerima)    Spinner _genderSpinnerPenerima;
    private String genderPenerima = "Laki-laki";
    @Bind(R.id.gender_spinner_pengirim)    Spinner _genderSpinnerPengirim;
    private String genderPengirim = "Laki-laki";

    RecipientAdapter mPenerimaRecipientAdapter;
    RecipientAdapter mPengirimRecipientAdapter;
    Recipient sender;
    Recipient receiver;

    ProgressDialog progressDialog;
    SessionManager session;

    CityAdapter cityPengirimAdapter;
    CityAdapter cityPenerimaAdapter;
    PacketServiceAdapter packetServiceAdapter;
    ArrayList<Recipient> data = new ArrayList<>();

    private Packet packet ;
    private City kota_pengirim;
    private City kota_penerima;
    private String service_code = "";
    boolean via_mobil = false;
    float berat_kiriman = 1;

    private List<City> cityList = new ArrayList<>();
    private List<PacketService> packetServiceList;
    private boolean inputBaruPengirim;
    private boolean inputBaruPenerima;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        session = new SessionManager(getContext());
        if(!session.isLoggedIn()){
            ((BaseActivity)getActivity()).showActivity(LoginActivity1.class);
            getActivity().finish();
            return ;
        }
    }

    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflateAndBind(inflater, container, R.layout.fragment_packet_order);

        progressDialog = new ProgressDialog(getActivity(), CustomDialog);

        pickContact();

        data.clear();
        data.addAll(db.getRecipientList());

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
                if(berat_kiriman > 0)
                    checkTarif();
            }
        });

        _viaMobilChk.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                via_mobil = isChecked;
                checkTarif();
            }
        });
        _buttonAddOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!validate()) {
                    onProcessOrderFailed();
                    return;
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
                        place_an_order();
                        handler.handleMessage(null);
                    }
                };
                showConfirmationDialog("Confirm Order","Pastikan data anda sudah benar. Anda Yakin akan memesan jasa KURIR kami?", YesClickListener, null);

                // loop till a runtime exception is triggered.
                try { Looper.loop(); }
                catch(RuntimeException e2) {}
            }
        });

        ivAgrement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopupWindow("Kurindo \nService Agreement", R.raw.snk_file, R.drawable.icon_syarat_ketentuan);
            }
        });

        setup_radio();
        setup_city_list();
        setup_gender_list();
        return rootView;
    }

    private void setup_gender_list() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),R.array.genders_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        _genderSpinnerPenerima.setAdapter(adapter);
        _genderSpinnerPenerima.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selected = (String) parent.getItemAtPosition(position);
                genderPenerima = selected;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        _genderSpinnerPengirim.setAdapter(adapter);
        _genderSpinnerPengirim.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selected = (String) parent.getItemAtPosition(position);
                genderPengirim = selected;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }
    private void pickContact() {
        pickContactBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityCompat.requestPermissions(getActivity(),new String[]{android.Manifest.permission.READ_CONTACTS},1);

                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE);
                startActivityForResult(intent, 1);
            }
        });
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);

        //if(requestCode == 1){
            if(resultCode == RESULT_OK){
                Uri contactData = data.getData();

                ContentResolver cr = getActivity().getContentResolver();
                Cursor cursor = cr.query(contactData,
                        null, null, null, null);
                if (cursor != null && cursor.moveToFirst()) {

                    do {
                        // get the contact's information
                        String id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
                        String name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                        Integer hasPhone = cursor.getInt(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));

                        // get the user's email address
                        String email = null;
                        Cursor ce = cr.query(ContactsContract.CommonDataKinds.Email.CONTENT_URI, null,
                                ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = ?", new String[]{id}, null);
                        if (ce != null && ce.moveToFirst()) {
                            email = ce.getString(ce.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));
                            ce.close();
                        }

                        // get the user's phone number
                        String phone = null;
                        if (hasPhone > 0) {
                            Cursor cp = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?", new String[]{id}, null);
                            if (cp != null && cp.moveToFirst()) {
                                phone = cp.getString(cp.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                                cp.close();
                            }
                        }

                        // if the user user has an email or phone then add it to contacts
                        if ((!TextUtils.isEmpty(email) && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
                                && !email.equalsIgnoreCase(name)) || (!TextUtils.isEmpty(phone))) {
                            Toast.makeText(getContext(), "Name: " + name + ", Phone No: " + phone, Toast.LENGTH_SHORT).show();
                        }
                            /*Contact contact = new Contact();
                            contact.name = name;
                            contact.email = email;
                            contact.phoneNumber = phone;
                            contacts.add(contact);*/


                    } while (cursor.moveToNext());
                }

                    //Cursor cursor =  managedQuery(contactData, null, null, null, null);
                //cursor.moveToFirst();

                //String number =       cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.NUMBER));

                //contactName.setText(name);
                //contactNumber.setText(number);
                //contactEmail.setText(email);
            }
        //}
    }

    private void setup_radio() {
        pilihListPenerima.setLayoutManager(new GridLayoutManager(getContext(), 1));
        pilihListPenerima.setHasFixedSize(true);

        mPenerimaRecipientAdapter = new RecipientAdapter(getContext(), data);
        pilihListPenerima.setAdapter(mPenerimaRecipientAdapter);
        pilihListPenerima.addOnItemTouchListener(new RecyclerItemClickListener(getContext(), new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                mPenerimaRecipientAdapter.selected(position);
                mPenerimaRecipientAdapter.setSelection(position);
                receiver = data.get(position);
                kota_penerima= receiver.getAddress().getCity();
                checkTarif();
            }
        }));
        pilihListPengirim.setLayoutManager(new GridLayoutManager(getContext(), 1));
        pilihListPengirim.setHasFixedSize(true);

        mPengirimRecipientAdapter = new RecipientAdapter(getContext(), data);
        pilihListPengirim.setAdapter(mPengirimRecipientAdapter);
        pilihListPengirim.addOnItemTouchListener(new RecyclerItemClickListener(getContext(), new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                mPengirimRecipientAdapter .selected(position);
                mPengirimRecipientAdapter .setSelection(position);
                sender = data.get(position);
                kota_pengirim = sender.getAddress().getCity();
                checkTarif();
            }
        }));

        inputModePengirimRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                inputBaruPengirim = checkedId == R.id.radio_inputbaru_pengirim;
                inputBaruPengirimLayout.setVisibility(inputBaruPengirim? View.VISIBLE : View.GONE);
                pilihListPengirimLayout.setVisibility(inputBaruPengirim? View.GONE: View.VISIBLE);

                if(inputBaruPengirim) sender = null;
            }
        });
        inputModePenerimaRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                inputBaruPenerima = checkedId == R.id.radio_inputbaru_penerima;
                inputBaruPenerimaLayout.setVisibility(inputBaruPenerima? View.VISIBLE : View.GONE);
                pilihListPenerimaLayout.setVisibility(inputBaruPenerima? View.GONE: View.VISIBLE);
                if(inputBaruPenerima) receiver= null;
            }
        });

        if(data.size() > 0){
            inputBaruPenerima = false;
            inputBaruPengirim = false;
            inputModePenerimaRadioGroup.check(pilihListPenerima.getId());
            inputModePengirimRadioGroup.check(pilihListPengirim.getId());
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
    }


    private void place_an_order() {
        Log.d(TAG, "place_an_order");

        _buttonAddOrder.setEnabled(false);

        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Processing your Order....");
        progressDialog.show();

        packet = new Packet();
        User user = db.toUser(db.getUserDetails());

        if(inputBaruPengirim){
            String namaPengirim = _namaPengirimText.getText().toString();
            //String teleponPengirim = _teleponPengirimText.getText().toString();
            String alamatPengirim = _alamatPengirimText.getText().toString();
            String teleponPengirim =  _teleponPengirimText.getNumber();
            if(teleponPengirim.startsWith("0")){
                teleponPengirim = _teleponPengirimText.getPhoneNumber().getCountryCode()+""+teleponPengirim;
            }
            packet.setNamaPengirim(namaPengirim);
            packet.setGenderPengirim(genderPengirim);
            packet.setAlamatPengirim(alamatPengirim);
            packet.setTeleponPengirim(teleponPengirim);

            db.addRecipient(namaPengirim,teleponPengirim,genderPengirim,alamatPengirim,kota_pengirim.getCode(), kota_pengirim.getText());
        }else{
            packet.setNamaPengirim(sender.getName());
            packet.setGenderPengirim(sender.getGender());
            packet.setAlamatPengirim(sender.getAddress().getAlamat());
            packet.setTeleponPengirim(sender.getTelepon());
            kota_pengirim = (sender.getAddress() != null ? sender.getAddress().getCity(): new City(user.getCity(),user.getCity()));
        }
        packet.setKotaPengirim(kota_pengirim.getCode());
        packet.setKotaPengirimText(kota_pengirim.getText());

        if(inputBaruPenerima){
            String namaPenerima= _namaPenerimaText.getText().toString();
            //String teleponPenerima = _teleponPenerimaText.getText().toString();
            String alamatPenerima = _alamatPenerimaText.getText().toString();
            String teleponPenerima =  _teleponPengirimText.getNumber();
            if(teleponPenerima.startsWith("0")){
                teleponPenerima = _teleponPenerimaText.getPhoneNumber().getCountryCode()+""+teleponPenerima;
            }
            packet.setNamaPenerima(namaPenerima);
            packet.setGenderPenerima(genderPenerima);
            packet.setAlamatPenerima(alamatPenerima);
            packet.setTeleponPenerima(teleponPenerima);
            db.addRecipient(namaPenerima,teleponPenerima, genderPenerima, alamatPenerima,kota_penerima.getCode(), kota_penerima.getText());
        }else{
            packet.setNamaPenerima(receiver.getName());
            packet.setGenderPenerima(receiver.getGender());
            packet.setAlamatPenerima(receiver.getAddress().getAlamat());
            packet.setTeleponPenerima(receiver.getTelepon());

            kota_penerima= (receiver.getAddress() != null ? receiver.getAddress().getCity(): new City(user.getCity(),user.getCity()));

        }
        packet.setKotaPenerima(kota_penerima.getCode());
        packet.setKotaPenerimaText(kota_penerima.getText());


        String beratBarang = _beratBarangText.getText().toString();
        String infoBarang = _infoBarangText.getText().toString();
        boolean viaMobil = _viaMobilChk.isChecked();

        int beratBarangInt = 0;
        try{ beratBarangInt = Integer.parseInt(beratBarang);}catch (Exception e){}
        packet.setBerat(beratBarangInt);
        packet.setInfoPaket(infoBarang);
        packet.setViaMobil(viaMobil);
        packet.setServiceCode(service_code);

        Map<String, String> params = packet.getAsParams();
        params.put("agen_awal", session.isLoggedIn() ? db.getUserDetails().get("uid") : AppConfig.USER_AGENT);
        params.put("user_agent", AppConfig.USER_AGENT);

        GsonBuilder builder = new GsonBuilder();
        builder.setPrettyPrinting();
        Gson gson = builder.create();
        String pembeli =  gson.toJson(user);
        params.put("pembeli", pembeli);
        process_order(params);
    }

    private void process_order(final Map<String, String> params) {
        String tag_string_req = "req_place_order";
        AppController.getInstance().cancelPendingRequests(tag_string_req);

        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_PLACE_ORDER, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Process_order Response: " + response.toString());

                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");
                    if (!error) {
                        GsonBuilder builder = new GsonBuilder();
                        builder.setPrettyPrinting();
                        Gson gson = builder.create();

                        String no_resi = jObj.getString("awb_number");
                        double totalPrice= jObj.getDouble("totalPrice");
                        packet.setResi(no_resi);
                        packet.setBiaya(totalPrice);
                        JSONArray jArr = jObj.getJSONArray("histories");
                        for (int i = 0; i < jArr.length(); i++) {
                            JSONObject jObjAr = jArr.getJSONObject(i);
                            String status = jObjAr.getString("status");
                            String remarks = jObjAr.getString("remarks");
                            String location = jObjAr.getString("location");
                            String createdByStr = jObjAr.getString("created_by");
                            String picStr = jObjAr.getString("pic");
                            StatusHistory hist = gson.fromJson(jObj.toString(), StatusHistory.class);
                            hist.setStatus(status);
                            hist.setRemarks(remarks);
                            hist.setLocation(location);
                            packet.setStatus(status);
                            packet.setStatusText(remarks);
                            //*/
                            packet.getStatusHistoryList().add(hist);
                        }
                        ArrayList<Packet> list = new ArrayList<Packet>();
                        list.add(packet);
                        Bundle bundle = new Bundle();
                        bundle.putParcelableArrayList("packets", list);
                        Intent intent = new Intent(getActivity(), PacketShowActivity.class);
                        intent.putExtras(bundle);
                        startActivityForResult(intent, REQUEST_PACKET_SHOW);
                        Toast.makeText(getContext(),"Pesanan sudah kami terima. Terima kasih atas kepercayaan anda.", Toast.LENGTH_SHORT).show();
                        onProcessOrderSuccess();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getContext(), "Json error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    _buttonAddOrder.setEnabled(true);
                }
                progressDialog.dismiss();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Process_order Error: " + error.getMessage());
                Toast.makeText(getContext(),
                        error.getMessage(), Toast.LENGTH_LONG).show();
                _buttonAddOrder.setEnabled(true);
                progressDialog.dismiss();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting params to register url
                return params;
            }
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                String api = db.getUserApi();
                params.put("Api", api);

                return params;
            }


        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    private void onProcessOrderSuccess() {
        _buttonAddOrder.setEnabled(true);
        progressDialog.dismiss();
        getActivity().finish();
    }

    private void onProcessOrderFailed() {
        Toast.makeText(getContext(), "Data tidak valid. Silahkan dilengkapi.", Toast.LENGTH_LONG).show();
        progressDialog.dismiss();
        _buttonAddOrder.setEnabled(true);
    }

    private void setup_city_list() {
        packetServiceList = getPacketServiceList();
        packetServiceAdapter = new PacketServiceAdapter(getContext(), packetServiceList);
        _serviceCodeText.setAdapter(packetServiceAdapter);
        _serviceCodeText.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                service_code = ((PacketService) parent.getItemAtPosition(position)).getCode();
                checkTarif();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        cityPengirimAdapter = new CityAdapter(getContext(), cityList);
        _kotaPengirimText.setAdapter(cityPengirimAdapter);
        _kotaPengirimText.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                kota_pengirim = ((City) parent.getItemAtPosition(position));
                checkTarif();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //cityPenerimaAdapter = new CityAdapter(getApplicationContext(), cityList);
        _kotaPenerimaText.setAdapter(cityPengirimAdapter);
        _kotaPenerimaText.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                kota_penerima = ((City) parent.getItemAtPosition(position));
                checkTarif();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        request_city("KEC");

    }



    private void checkTarif(){
        //kota_pengirim, kota_penerima
        if(kota_pengirim !=null){
            if(kota_penerima != null ){
                progressDialog.setTitle("Checking Tarif");
                progressDialog.show();

                String tag_string_req = "req_packet_price";
                StringRequest strReq = new StringRequest(Request.Method.POST,
                        AppConfig.URL_PACKET_PRICE, new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        Log.d(TAG, "Price Check Response: " + response.toString());

                        try {
                            JSONObject jObj = new JSONObject(response);
                            //boolean error = jObj.getBoolean("error");
                            //if (!error) {
                            //TODO handle JSON response
                            double price = jObj.getDouble("tarif");
                            String priceStr = ""+-1;
                            priceStr = AppConfig.formatCurrency( price );

                            priceText.setText(priceStr);

                            //}else{
                            //    String message= jObj.getString("message");
                            //Toast.makeText(getApplicationContext(), "Error: " + message, Toast.LENGTH_LONG).show();
                            //}
                            _buttonAddOrder.setEnabled(true);
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(getContext(), "Json error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                            _buttonAddOrder.setEnabled(true);
                        }
                        progressDialog.dismiss();
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        Log.e(TAG, "Price Check Error: " + error.getMessage());
                        //Toast.makeText(getApplicationContext(),error.getMessage(), Toast.LENGTH_LONG).show();
                        _buttonAddOrder.setEnabled(true);
                        progressDialog.dismiss();
                    }
                }) {

                    @Override
                    protected Map<String, String> getParams() {
                        // Posting params to register url
                        Map<String,String> params = new HashMap<>();
                        User user = db.toUser(db.getUserDetails());
                        if(sender != null) {
                            kota_pengirim = (sender.getAddress() != null ? sender.getAddress().getCity(): new City(user.getCity(),user.getCity()));
                        }
                        if(receiver!= null) {
                            kota_penerima = (receiver.getAddress() != null ? receiver.getAddress().getCity(): new City(user.getCity(),user.getCity()));
                        }
                        params.put("kota_pengirim", kota_pengirim.getCode());
                        params.put("kota_penerima", kota_penerima.getCode());
                        params.put("service_code", service_code);
                        params.put("berat_kiriman", ""+berat_kiriman);
                        params.put("via_mobil", ""+via_mobil);
                        return params;
                    }

                };

                // Adding request to request queue
                AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
            }
        }
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

/*        if (kotaPengirim.isEmpty() || kotaPengirim.length() < 4 ) {
            _kotaPengirimText.setError("Tuliskan kota Pengirim");
            valid = false;
        } else {
            _kotaPengirimText.setError(null);
        }
*/
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

    public List<PacketService> getPacketServiceList() {
        if(packetServiceList ==null){
            packetServiceList = new ArrayList<>();
            PacketService service2 = new PacketService("NDS", "Next Day Service", "Kiriman Ekonomis untuk Esok hari");
            packetServiceList.add(service2);

            PacketService service = new PacketService("SDS", "Same Day Service", "Kiriman Express (hari yang sama)");
            packetServiceList.add(service);

            PacketService service3 = new PacketService("ENS", "Extra Night Service", "Kiriman Express setelah jam 6 malam");
            packetServiceList.add(service3);

        }
        return packetServiceList;
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
        progressDialog.show();

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
                        cityPengirimAdapter.notifyDataSetChanged();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                progressDialog.dismiss();
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
