package id.co.kurindo.kurindo;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
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
import com.lamudi.phonefield.PhoneInputLayout;


import org.json.JSONArray;
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
import id.co.kurindo.kurindo.adapter.CityAdapter;
import id.co.kurindo.kurindo.adapter.RecipientAdapter;
import id.co.kurindo.kurindo.app.AppConfig;
import id.co.kurindo.kurindo.app.AppController;
import id.co.kurindo.kurindo.base.BaseActivity;
import id.co.kurindo.kurindo.base.BaseFragment;
import id.co.kurindo.kurindo.helper.SessionManager;
import id.co.kurindo.kurindo.model.Address;
import id.co.kurindo.kurindo.model.CartItem;
import id.co.kurindo.kurindo.model.City;
import id.co.kurindo.kurindo.model.Order;
import id.co.kurindo.kurindo.model.Product;
import id.co.kurindo.kurindo.model.Recipient;
import id.co.kurindo.kurindo.model.User;

/**
 * Created by DwiM on 12/12/2016.
 */

public class SimpleOrderFragment extends BaseFragment {
    private static final String TAG = "SimpleOrderFragment";

    Product product;
    @Bind(R.id.ivProductImage)
    ImageView productImage;
    @Bind(R.id.tvProductName)
    TextView productTitle;
    @Bind(R.id.tvProductDesc)
    TextView productDescription;

    @Bind(R.id.input_email)
    EditText input_email;
    @Bind(R.id.input_telepon)
    PhoneInputLayout input_telepon;
    @Bind(R.id.input_alamat)
    EditText input_alamat;
    @Bind(R.id.input_pesan)
    EditText input_pesan;
    @Bind(R.id.input_kota)
    Spinner _cityText;
    @Bind(R.id.gender_spinner)    Spinner _genderSpinner;
    private String gender = "Laki-laki";

    @Bind(R.id.rdogrpInput)
    RadioGroup inputModeRadioGroup;
    @Bind(R.id.radio_inputbaru)
    RadioButton inputNewRadio;
    @Bind(R.id.radio_pilihlist)
    RadioButton pilihListRadio;
    @Bind(R.id.layout_inputbaru)
    LinearLayoutCompat inputBaruLayout;
    @Bind(R.id.layout_pilihlist)
    LinearLayoutCompat pilihListLayout;

    @Bind(R.id.chkAgrement)
    CheckBox chkAgrement;
    @Bind(R.id.ivAgrement)
    ImageView ivAgrement;

    @Bind(R.id.btn_notify)
    AppCompatButton orderBtn;

    @Bind(R.id.list)
    RecyclerView pilihListRecycleView;
    RecipientAdapter mRecipientAdapter;
    ArrayList<Recipient> data = new ArrayList<>();
    Recipient recipient;
    Order order;
    City city;
    List<City> cityList = new ArrayList<>();
    CityAdapter cityAdapter;
    boolean inputBaru= false;
    private ProgressDialog progressDialog;
    SessionManager sessionManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sessionManager = new SessionManager(getContext());
        if(!sessionManager.isLoggedIn()){
            ((BaseActivity)getActivity()).showActivity(LoginActivity.class);
            getActivity().finish();
            return;
        }

        Bundle bundle = getArguments();
        product = (Product) bundle.getParcelable("product");

        data.clear();
        data.addAll(db.getRecipientList());

        progressDialog = new ProgressDialog(getActivity(),R.style.AppTheme);

        setHasOptionsMenu(true);
    }

    public static SimpleOrderFragment newInstance(Product product) {
        SimpleOrderFragment fragment = new SimpleOrderFragment();
        fragment.product = product;
        Bundle args = new Bundle();
        //args.putSerializable("product",product);
        args.putParcelable("product", product);
        fragment.setArguments(args);
        return fragment;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflateAndBind(inflater, container, R.layout.fragment_simpleorder);

        setup_form();
        setup_gender_list();
        load_city();

        return rootView;
    }
    private void setup_gender_list() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),R.array.genders_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        _genderSpinner.setAdapter(adapter);
        _genderSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selected = (String) parent.getItemAtPosition(position);
                gender = selected;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void load_city() {
        cityAdapter = new CityAdapter(getActivity(), cityList);
        _cityText.setAdapter(cityAdapter);
        _cityText.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                city = (City) parent.getItemAtPosition(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        request_city("KEC");
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

    private void setup_form() {
        if(product != null){
            productImage.setImageResource( product.getDrawable() );
            productTitle.setText(product.getName());
            productDescription.setText(product.getDescription());
        }
        orderBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validate()){
                    Log.d(TAG, "orderBtn place_order");
                    final Handler handler = new Handler() {
                        @Override
                        public void handleMessage(Message mesg) {
                            throw new RuntimeException();
                        }
                    };

                    DialogInterface.OnClickListener YesClickListener = new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            place_order(handler);
                        }
                    };
                    showConfirmationDialog("Confirm Order","Anda yakin akan memesan layanan ini?", YesClickListener, null);

                    // loop till a runtime exception is triggered.
                    try { Looper.loop(); }
                    catch(RuntimeException e2) {}
                }
            }
        });


        pilihListRecycleView.setLayoutManager(new GridLayoutManager(getContext(), 1));
        pilihListRecycleView.setHasFixedSize(true);

        mRecipientAdapter = new RecipientAdapter(getContext(), data);
        pilihListRecycleView.setAdapter(mRecipientAdapter);
        pilihListRecycleView.addOnItemTouchListener(new RecyclerItemClickListener(getContext(), new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                addSelection(position);
            }
        }));

        inputModeRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                inputBaru = checkedId == R.id.radio_inputbaru;
                inputBaruLayout.setVisibility( inputBaru? View.VISIBLE: View.GONE);
                pilihListLayout.setVisibility( inputBaru? View.GONE: View.VISIBLE);
                if(inputBaru){
                    mRecipientAdapter.clearSelections();
                    recipient = null;
                }
            }
        });

        if(data.size() > 0){
            inputBaru = false;
            inputModeRadioGroup.check(pilihListRadio.getId());
        }else{
            inputBaru = true;
            inputModeRadioGroup.check(inputNewRadio.getId());
        }
        inputBaruLayout.setVisibility( data.size() > 0? View.GONE : View.VISIBLE );
        pilihListLayout.setVisibility( data.size() > 0? View.VISIBLE : View.GONE );

        ivAgrement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopupWindow("Kurindo \nService Agreement", R.raw.snk_file, R.drawable.icon_syarat_ketentuan);
            }
        });
    }

    private void addSelection(int position) {
        mRecipientAdapter.selected(position);
        mRecipientAdapter.setSelection(position);
        recipient = data.get(position);

    }

    private void place_order(final Handler handler) {
        String tag_string_req = "req_place_order_kurindo";
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Processing...");
        progressDialog.show();

        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_PLACE_ORDER_SHOP, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Process_order Response: " + response.toString());
                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");
                    if (!error) {
                        if(order==null) {
                            order = new Order();
                        }
                        order.setAwb(jObj.getString("awb"));
                        order.setStatus(jObj.getString("status"));
                        order.setStatusText(jObj.getString("statusText"));

                        Bundle bundle = new Bundle();
                        bundle.putParcelable("order", order);
                        ((BaseActivity)getActivity()).showActivity(OrderShowActivity.class, bundle);
                        progressDialog.dismiss();
                        Toast.makeText(getContext(),"Pesanan sudah kami terima. Terima kasih atas kepercayaan anda.", Toast.LENGTH_SHORT).show();
                        getActivity().finish();
                        handler.handleMessage(null);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    progressDialog.dismiss();
                    handler.handleMessage(null);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Process_order Error: " + error.getMessage());
                progressDialog.dismiss();
                handler.handleMessage(null);
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting params to register url
                HashMap<String, String> params = new HashMap<>();
                GsonBuilder builder = new GsonBuilder();
                builder.setPrettyPrinting();
                //builder.serializeNulls();
                Gson gson = builder.create();

                order= new Order();

                User user = db.toUser(db.getUserDetails());
                Address addr = new Address();
                //addr.setAlamat(input_alamat.getText().toString());
                addr.setCity(city);
                //user.setAddress(addr);
                order.setBuyer(user);

                Set<Product> datas = new LinkedHashSet<>();
                datas.add(product);
                Set<Recipient> recipients = new LinkedHashSet<>();
                recipients.add(recipient);

                Set<CartItem> cartItems= new LinkedHashSet<>();
                CartItem cartItem = new CartItem();
                cartItem.setProduct(product);
                cartItem.setQuantity(1);
                cartItems.add(cartItem);

                order.setRecipients(recipients);
                order.setItems(cartItems);
                order.setPayment("COD");
                order.setTotalPrice(new BigDecimal(0));

                String pembeli =  gson.toJson(user);
                String penerima = gson.toJson(recipients);
                String products = gson.toJson(datas);
                String remarks = input_pesan.getText().toString();


                params.put("pembeli",pembeli);
                params.put("penerima", penerima);
                params.put("produk", products);
                params.put("shipping", "[]");
                params.put("totalPrice", ""+order.getTotalPrice().doubleValue());
                params.put("user_agent", AppConfig.USER_AGENT);
                params.put("payment",order.getPayment());
                params.put("type", product.getCode());
                params.put("remarks", remarks);
                Log.d("PARAMS",gson.toJson(params));
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


    private boolean validate() {
        boolean valid = true;

        if(inputBaru){
            String email = input_email.getText().toString();
            //String telepon = input_telepon.getText().toString();
            String alamat = input_alamat.getText().toString();
            String pesanan = input_pesan.getText().toString();
            if (email == null || email.isEmpty() ){//|| !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                input_email.setError("Tuliskan Nama anda");
                valid = false;
            }else{
                input_email.setError(null);
            }

            if(!input_telepon.isValid()){
                Toast.makeText(getContext(), "Invalid Phone number.", Toast.LENGTH_SHORT).show();
                valid = false;
            }
            if(alamat == null || alamat.isEmpty()){
                input_alamat.setError("Tuliskan Alamat anda");
                valid = false;
            }else{
                input_alamat.setError(null);
            }
            if(pesanan == null || pesanan.isEmpty()){
                input_alamat.setError("Tuliskan Alamat anda");
                valid = false;
            }else{
                input_alamat.setError(null);
            }
            if(valid) {
                Recipient recipient = new Recipient();
                recipient.setName(email);
                String telepon =  input_telepon.getPhoneNumber();
                //if(telepon.startsWith("0")){ telepon = input_telepon.getPhoneNumber().getCountryCode()+""+telepon; }
                recipient.setTelepon(telepon);
                recipient.setGender(gender);
                Address addr = new Address();
                addr.setCity(city);
                addr.setAlamat(alamat);
                recipient.setAddress(addr);
                this.recipient = recipient;
                data.add(recipient);
                //add to DB
                //String name= email;
                db.addRecipient(recipient);

            }
        }else{
            if(recipient == null){
                Toast.makeText(getActivity(), "Data Pengguna tidak boleh kosong. Pilih dari daftar.", Toast.LENGTH_SHORT).show();
                valid = false;
            }
        }
        String pesanan = input_pesan.getText().toString();
        if( pesanan == null || pesanan.isEmpty()){
            input_pesan.setError("Tuliskan pesanan anda.");
            valid = false;
        }
        if(valid){
            if(!chkAgrement.isChecked()) {
                Toast.makeText(getActivity(), "Anda belum menyetujui syarat dan ketentuan kami.", Toast.LENGTH_SHORT).show();
                valid = false;
            }
        }

        return valid;
    }


}
