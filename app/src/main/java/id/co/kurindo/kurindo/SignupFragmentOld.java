package id.co.kurindo.kurindo;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.firebase.iid.FirebaseInstanceId;
import com.lamudi.phonefield.PhoneInputLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import id.co.kurindo.kurindo.adapter.CityAdapter;
import id.co.kurindo.kurindo.app.AppConfig;
import id.co.kurindo.kurindo.app.AppController;
import id.co.kurindo.kurindo.base.BaseFragment;
import id.co.kurindo.kurindo.helper.SQLiteHandler;
import id.co.kurindo.kurindo.helper.SessionManager;
import id.co.kurindo.kurindo.helper.SignUpHelper;
import id.co.kurindo.kurindo.model.City;
import id.co.kurindo.kurindo.util.LogUtil;

/**
 * Created by dwim on 1/6/2017.
 */

public class SignupFragmentOld extends BaseFragment {
    private static final String TAG = "SignupFragment";

    @Bind(R.id.input_firstname)
    EditText _firstnameText;
    @Bind(R.id.input_lastname) EditText _lastnameText;
    @Bind(R.id.input_email) EditText _emailText;

    @Bind(R.id.input_phone)
    PhoneInputLayout _phoneText;
    @Bind(R.id.city_spinner)
    Spinner _citySpinner;
    @Bind(R.id.gender_spinner)
    Spinner _genderSpinner;

    @Bind(R.id.radio_group_role)
    RadioGroup roleRadioGroup;

    @Bind(R.id.btn_signup)
    Button _signupButton;
    @Bind(R.id.link_login)
    TextView _loginLink;

    @Bind(R.id.chkAgrement)
    CheckBox chkAgrement;
    @Bind(R.id.ivAgrement)
    ImageView ivAgrement;
    @Bind(R.id.ivAgrement2)
    ImageView ivAgrement2;

    @Bind(R.id.input_simc)
    EditText _simcText;
    @Bind(R.id.input_nik)
    EditText _nikText;
    @Bind(R.id.input_layout_kurir)
    LinearLayoutCompat _kurirLayout;

    private SessionManager session;
    private SQLiteHandler db;
    private ProgressDialog progressDialog;
    CityAdapter cityAdapter;
    private String role = AppConfig.KEY_PELANGGAN;
    private String city = "ID";
    private String gender = "Laki-laki";

    private List<City> cityList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflateAndBind(inflater, container, R.layout.activity_signup_old);


        // Check if user is already logged in or not
        //if (session.isLoggedIn()) {
            // User is already logged in. Take him to main activity
        //    onSignupSuccess();
        //}
        _signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signup();
            }
        });

        _loginLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Finish the registration screen and return to the Login activity
                getActivity().finish();
            }
        });

        //PhoneNumberFormattingTextWatcher phoneWatcher = new PhoneNumberFormattingTextWatcher();
        roleRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener(){
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch(checkedId){
                    case R.id.radio_kurir:
                        role = AppConfig.KEY_KURIR;
                        show_kurir_layout(true);
                        break;
                    case R.id.radio_client:
                        role = AppConfig.KEY_PELANGGAN;
                        show_kurir_layout(false);
                        break;
                    case R.id.radio_agent:
                        role = AppConfig.KEY_AGENT;
                        show_kurir_layout(false);
                        break;
                }
            }
        });

        setup_gender_list();
        setup_city_list();

        ivAgrement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopupWindow("Kurindo \nEnd User License Agreement", R.raw.syarat_penggunaan, R.drawable.icon_syarat_ketentuan);
            }
        });
        ivAgrement2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopupWindow("Kurindo \nPrivacy Policy", R.raw.privacy_policy, R.drawable.icon_syarat_ketentuan);
            }
        });

        return v;
    }

    private void show_kurir_layout(boolean b) {
        _kurirLayout.setVisibility(b?View.VISIBLE:View.GONE);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // SQLite database handler
        db = new SQLiteHandler(getContext());

        // Session manager
        session = new SessionManager(getContext());
        progressDialog = new ProgressDialog(getActivity(),R.style.AppTheme);

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


    private void setup_city_list() {

        cityAdapter = new CityAdapter(getContext(), cityList);
        _citySpinner.setAdapter(cityAdapter);
        _citySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                city = ((City) parent.getItemAtPosition(position)).getCode();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        request_city("KEC");
        //new LoadCityTask().execute();


    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        hidePDialog();
    }

    private void hidePDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
            progressDialog = null;
        }
    }

    public void signup() {
        LogUtil.logD(TAG, "Signup");

        if (!validate()) {
            onSignupFailed();
            return;
        }

        _signupButton.setEnabled(false);

        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Creating Account...");
        progressDialog.show();

        String firstname = _firstnameText.getText().toString();
        String lastname = _lastnameText.getText().toString();
        String email = _emailText.getText().toString();
        //String password = _passwordText.getText().toString();
        String phone =  _phoneText.getPhoneNumber();
        //if(phone.startsWith("0")){ phone = _phoneText.getPhoneNumber().getCountryCode()+""+phone;}
        // TODONE: Implement your own signup logic here.
        signup_process(firstname, lastname, email, phone, role);
        /*new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        // On complete call either onSignupSuccess or onSignupFailed
                        // depending on success
                        onSignupSuccess();
                        // onSignupFailed();
                        progressDialog.dismiss();
                    }
                }, 3000);*/
    }

    private void signup_process(final String firstname, final String lastname, final String email, final String phone, final String role){
        String tag_string_req = "req_signup";
        /*FirebaseInstanceId instanceId = FirebaseInstanceId.getInstance();
        try {
            instanceId.deleteInstanceId();
        } catch (IOException e) {
            e.printStackTrace();
        }*/
        final String token = FirebaseInstanceId.getInstance().getToken();
        LogUtil.logD(TAG, "Token: " + token);

        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_REGISTER, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                LogUtil.logD(TAG, "Register Response: " + response.toString());
                //hideDialog();

                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");
                    if (!error) {
                        db.onUpgrade(db.getWritableDatabase(), 0, 1);
                        session.setActive(false);

                        // User successfully stored in MySQL
                        store_user_information(jObj);

                        // Show Validation Page
                        Toast.makeText(getContext(), "User successfully registered. Check notification or your email for activation code.!", Toast.LENGTH_LONG).show();

                        // Launch Validation activity
                        onSignupSuccess();

                    } else {

                        // Error occurred in registration. Get the error
                        // message
                        String errorMsg = jObj.getString("message");
                        Toast.makeText(getContext(),
                                errorMsg, Toast.LENGTH_LONG).show();
                        _signupButton.setEnabled(true);

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getContext(), "Json error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    _signupButton.setEnabled(true);
                }

                progressDialog.dismiss();
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                LogUtil.logE(TAG, "Registration Error: " + error.getMessage());
                Toast.makeText(getContext(),error.getMessage(), Toast.LENGTH_LONG).show();
                error.printStackTrace();
                _signupButton.setEnabled(true);
                progressDialog.dismiss();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting params to register url
                Map<String, String> params = new HashMap<String, String>();
                params.put("form-firstname", firstname);
                params.put("form-lastname", lastname);
                params.put("form-email", email);
                params.put("form-phone", phone);
                params.put("form-role", role);
                params.put("form-city", city);
                params.put("form-gender", gender);
                params.put("form-token", token == null ? "":token);
                if(role.equalsIgnoreCase(AppConfig.KEY_KURIR)){
                    params.put("form-nik", _nikText.getText().toString());
                    params.put("form-simc", _simcText.getText().toString());
                /*}else{
                    params.put("form-nik", "");
                    params.put("form-simc", ""); */
                }
                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    private void store_user_information(JSONObject jObj) {
        try {
            // Now store the user in sqlite
            String uid = jObj.getString("user");
            String role = jObj.getString("role");
            String city = jObj.getString("city");
            String api_key = jObj.getString("api_key");

            JSONObject user = jObj.getJSONObject("data");
            String firstname = user.getString("firstname");
            String lastname = user.getString("lastname");
            String email = user.getString("email");
            String gender = user.getString("gender");
            String created_at = user.getString("created");
            String phone = user.getString("phone");

            boolean active = jObj.getBoolean("active");
            boolean approved = jObj.getBoolean("approved");


            // Inserting row in users table
            db.addUser(firstname, lastname, email, phone, gender, uid, role, city, api_key, active, approved, created_at);

            // Create login session
            //session.setLogin(true);
            //session.setLoginData(role, city);
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "Json error: " + e.getMessage(), Toast.LENGTH_LONG).show();
            _signupButton.setEnabled(true);
            progressDialog.dismiss();
        }
    }

    public void onSignupSuccess() {
        clearForm();
        _signupButton.setEnabled(true);
        ((TabLoginFragment)getParentFragment()).tabLayout.getTabAt(0).select();

        //TabHost host = (TabHost) getActivity().findViewById(android.R.id.tabs);
        //host.setCurrentTab(0);

        //getActivity().getIntent().putExtra("email", _emailText.getText().toString());
        //getActivity().setResult(getActivity().RESULT_OK, getActivity().getIntent());
        //getActivity().finish();
    }

    private void clearForm() {
        _firstnameText.setText("");
        _lastnameText.setText("");
        _emailText.setText("");
        _phoneText.setDefaultCountry("ID");
        _phoneText.setPhoneNumber("");
    }

    public void onSignupFailed() {
        Toast.makeText(getContext(), "Login failed", Toast.LENGTH_LONG).show();

        _signupButton.setEnabled(true);
        progressDialog.dismiss();
    }
    public void onRadioButtonClicked(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch(view.getId()) {
            case R.id.radio_kurir:
                if (checked)
                    role = AppConfig.KEY_KURIR;
                break;
            case R.id.radio_client:
                if (checked)
                    role = AppConfig.KEY_PELANGGAN;
                break;
            case R.id.radio_agent:
                if (checked)
                    role = AppConfig.KEY_AGENT;
                break;
        }
    }
    public boolean validate() {
        boolean valid = true;

        String name = _firstnameText.getText().toString();
        String email = _emailText.getText().toString();
        //String phone = _phoneText.getText().toString();
        String nik = _nikText.getText().toString();
        String simc  = _simcText.getText().toString();

        if (name.isEmpty() || name.length() < 3) {
            _firstnameText.setError("at least 3 characters");
            valid = false;
        } else {
            _firstnameText.setError(null);
        }

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _emailText.setError("enter a valid email address");
            valid = false;
        } else {
            _emailText.setError(null);
        }

        if(!_phoneText.isValid()){
            valid = false;
            Toast.makeText(getContext(), "Invalid Phone number.", Toast.LENGTH_SHORT).show();
        }

        if (city == null || city.isEmpty() || city.length() < 5) {
            Toast.makeText(getContext(), "Select your City", Toast.LENGTH_SHORT).show();
            valid = false;
        }
        if(role.equalsIgnoreCase(AppConfig.KEY_KURIR)){
            if(nik.isEmpty() || nik.length() < 4){
                valid = false;
                _nikText.setError("NIK harus diisi");
            }else{
                _nikText.setError(null);
            }
            if(simc.isEmpty() || simc.length() < 4){
                valid = false;
                _simcText.setError("SIM C harus diisi");
            }else{
                _simcText.setError(null);
            }
        }
        if(valid){
            if(!chkAgrement.isChecked()) {
                Toast.makeText(getActivity(), "Anda belum menyetujui syarat dan ketentuan kami.", Toast.LENGTH_SHORT).show();
                valid = false;
            }
        }

        return valid;
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
        //progressDialog.show();

        StringRequest cityReq = new StringRequest(Request.Method.GET,
                URI, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                LogUtil.logD(TAG, "Register Response: " + response.toString());
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
                //progressDialog.dismiss();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                LogUtil.logE(TAG, "Registration Error: " + error.getMessage());
                //Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_LONG).show();

            }
        });

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(cityReq);
    }


}
