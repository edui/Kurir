package id.co.kurindo.kurindo;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatCheckBox;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.lamudi.phonefield.PhoneInputLayout;
import com.stepstone.stepper.VerificationError;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.OnClick;
import id.co.kurindo.kurindo.app.AppConfig;
import id.co.kurindo.kurindo.app.AppController;
import id.co.kurindo.kurindo.base.BaseFragment;
import id.co.kurindo.kurindo.comp.ProgressDialogCustom;
import id.co.kurindo.kurindo.helper.SQLiteHandler;
import id.co.kurindo.kurindo.helper.SessionManager;
import id.co.kurindo.kurindo.map.DataParser;
import id.co.kurindo.kurindo.map.LocationService;
import id.co.kurindo.kurindo.map.MapUtils;
import id.co.kurindo.kurindo.model.Address;
import id.co.kurindo.kurindo.model.TUser;
import id.co.kurindo.kurindo.model.User;
import id.co.kurindo.kurindo.util.LogUtil;
import id.co.kurindo.kurindo.util.ParserUtil;
import id.co.kurindo.kurindo.wizard.signup.SignupWizardActivity;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

/**
 * Created by dwim on 1/6/2017.
 */

public class LoginPhoneFragment extends BaseFragment {
    private static final String TAG = "LoginPhoneFragment";

    private static final int REQUEST_SIGNUP = 0;
    private static final int REQUEST_ACTIVATION = 1;
    private static final int REQUEST_WAITINGAPPROVAL = 2;
    private static final int REQUEST_SignupWizardActivity = 3;

    @Nullable
    @Bind(R.id.input_phone)
    PhoneInputLayout phoneInput;
    @Nullable @Bind(R.id.input_password) EditText _passwordText;
    @Nullable @Bind(R.id.btn_login)
    AppCompatButton _loginButton;
    @Nullable @Bind(R.id.link_signup)
    TextView _signupLink;
    @Nullable @Bind(R.id.chkAutoLogin)
    AppCompatCheckBox _chkAutoLogin;

    @Nullable @Bind(R.id.link_recover) TextView _recoverLink;
    @Nullable @Bind(R.id.layout_recovery)
    LinearLayout _recoveryLayout;
    @Nullable @Bind(R.id.layout_login) LinearLayout _loginLayout;
    @Nullable @Bind(R.id.btn_recover)    AppCompatButton _recoverButton;
    @Nullable @Bind(R.id.input_phone_recovery) PhoneInputLayout _recoveryPhoneText;

    @Nullable @Bind(R.id.layout_activation)
    LinearLayout _activationLayout;
    @Nullable @Bind(R.id.input_activation_phone) PhoneInputLayout _activationPhoneText;
    @Nullable @Bind(R.id.input_activation_code) EditText _activationCodeText;
    @Nullable @Bind(R.id.input_activation_password) EditText _activationPasswordText;
    @Nullable @Bind(R.id.input_activation_password2) EditText _activationPassword2Text;
    @Bind(R.id.btn_activation) AppCompatButton _activationButton;

    private SessionManager session;
    private SQLiteHandler db;
    private ProgressDialog progressBar;
    LatLng location;
    Context context;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getContext();

        // SQLite database handler
        db = new SQLiteHandler(context);
        //db.onUpgrade(db.getWritableDatabase(), 0, 1);
        // Session manager
        session = new SessionManager(context);
        //session.setLogin(false);
    }

    private void handleArguments() {
        final Bundle bundle = getArguments();
        if(bundle != null) {
            boolean activation = bundle.getBoolean("activation");
            if (activation) {
                String phone = bundle.getString("phone");
                String code = bundle.getString("code");
                showActivationLayout(phone);
                _activationPhoneText.setPhoneNumber(phone);
                _activationCodeText.setText(code);
                _activationButton.setEnabled(true);
            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View v = inflateAndBind(inflater, container, R.layout.fragment_login_phone);
        progressBar = new ProgressDialogCustom(context);

        phoneInput.setDefaultCountry(AppConfig.DEFAULT_COUNTRY);
        phoneInput.setHint(R.string.nomor_telepon);
        _activationPhoneText.setDefaultCountry(AppConfig.DEFAULT_COUNTRY);
        _activationPhoneText.setHint(R.string.nomor_telepon);
        _recoveryPhoneText.setDefaultCountry(AppConfig.DEFAULT_COUNTRY);
        _recoveryPhoneText.setHint(R.string.nomor_telepon);

        // Check if user is already logged in or not
        if (session.isLoggedIn()) {
            //check account status
            checkStatusAccount();

            // User is already logged in. Take him to main activity
            //onAlreadyLogin();
        }
//session.setActive(true);
        User user = db.toUser(db.getUserDetails() );
        if(user != null){
            phoneInput.setPhoneNumber(user.getPhone());
            _activationPhoneText.setPhoneNumber(user.getPhone());
            _recoveryPhoneText.setPhoneNumber(user.getPhone());
            //if(!session.isActivated()){
            //    showActivationLayout(user.getPhone());
            //}
        }

        //checkStatusAccount();

        _loginButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                login();
            }
        });


        _recoverLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _loginLayout.setVisibility(_recoveryLayout.getVisibility());
                if(_recoveryLayout.getVisibility() == View.GONE) {
                    _recoveryLayout.setVisibility(View.VISIBLE);
                    _recoverLink.setText("Form Login? Klik disini.");
                }
                else{
                    _recoveryLayout.setVisibility(View.GONE);
                    _recoverLink.setText("Lupa Password? Klik disini.");
                }
            }
        });
        _recoverButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recovery_account();
            }
        });

        handleArguments();

        return v;

    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        if (isVisibleToUser){
            if(phoneInput != null ){
                User user = db.toUser(db.getUserDetails() );
                if(user != null){
                    phoneInput.setPhoneNumber(user.getPhone());
                        _activationPhoneText.setPhoneNumber(user.getPhone());
                    if(!session.isActivated()){
                        showActivationLayout(user.getPhone());
                    }else{
                        showLoginLayout();
                    }
                }
            }
        }
    }

    @OnClick(R.id.btn_recover)
    public void recovery_account() {
        boolean valid = true;
        final String phone = _recoveryPhoneText.getPhoneNumber();
        if (_recoveryPhoneText.isValid()) {
            _recoveryPhoneText.setError(null);
        } else {
            _recoveryPhoneText.setError(getString( R.string.enter_valid_phone ));
            valid = false;
        }

        if(valid){
            progressBar.show();
            if(getActivity() != null){
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        /*FirebaseInstanceId instanceId = FirebaseInstanceId.getInstance();
                        try {
                            instanceId.deleteInstanceId();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }*/
                        final String token = FirebaseInstanceId.getInstance().getToken();

                        String tag_string_req = "req_recovery_account";
                        StringRequest strReq = new StringRequest(Request.Method.POST,
                                AppConfig.URL_BACKEND_RECOVERY_PHONE, new Response.Listener<String>() {

                            @Override
                            public void onResponse(String response) {
                                LogUtil.logD(TAG, "RecoveryAccount Response: " + response.toString());

                                try {
                                    JSONObject jObj = new JSONObject(response);
                                    String message = jObj.getString("message");
                                    boolean ok = "OK".equalsIgnoreCase(message);

                                    // Check for error node in json
                                    if (ok) {
                                        session.setActive(false);
                                        showActivationLayout(phone);;
                                    }else{
                                        Toast.makeText(context,message, Toast.LENGTH_SHORT).show();
                                    }
                                } catch (JSONException e) {
                                    // JSON error
                                    e.printStackTrace();
                                    Toast.makeText(context, "Json error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    _recoverButton.setEnabled(true);
                                }
                                progressBar.dismiss();
                            }
                        }, new Response.ErrorListener() {

                            @Override
                            public void onErrorResponse(VolleyError error) {
                                LogUtil.logE(TAG, "Recovery Error: " + error.getMessage());
                                Toast.makeText(context, error.getMessage(), Toast.LENGTH_SHORT).show();

                                _recoverButton.setEnabled(true);
                                progressBar.dismiss();
                            }
                        }) {
                            @Override
                            protected Map<String, String> getParams() {
                                // Posting parameters to  url
                                Map<String, String> params = new HashMap<String, String>();
                                params.put("form-type", "json");
                                params.put("form-phone", phone);
                                params.put("form-token", token);

                                return params;
                            }
                        };

                        // Adding request to request queue
                        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);

                    }
                });
            }
        }
    }

    private void checkStatusAccount() {
        String phone = "";//_emailText.getText().toString();
        boolean active = true;
        boolean approved = true;
        String role = null;
        String city = null;
        //if(email == null || email.isEmpty()){
        HashMap users = db.getUserDetails();
        if(users != null){
            Object o = users.get("phone");
            if(o != null){
                phoneInput.setPhoneNumber(o.toString());
                phone = o.toString();
            }
            Object ro = users.get("role");
            if(ro != null){
                role = ro.toString();
            }
            Object activeObj = users.get("active");
            Object approvedObj= users.get("approved");
            try { active = Boolean.parseBoolean(activeObj.toString());}catch (Exception e) {}
            try { approved = Boolean.parseBoolean(approvedObj.toString());}catch (Exception e) {}

        }
        //}
        if(!active){
            showActivationLayout(phone);
        }else if(!approved ) {
            if(!role.equalsIgnoreCase(AppConfig.KEY_PELANGGAN)){
                request_checkStatusAccount(phone);
            }
        }else {
            request_checkStatusAccount(phone);
            /*
            Object cityObj = users.get("city");
            if(cityObj != null){
                city = cityObj.toString();
            }
            if(city ==null || city.isEmpty()){
                showPinAddressForm();
            }*/
        }
    }

    private void request_checkStatusAccount(final String phone){
        FragmentActivity activity=  getActivity();
        if(activity != null){
            progressBar.setIndeterminate(true);
            progressBar.setMessage("Loading....");
            progressBar.show();

            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    String tag_string_req = "req_status_account";
                    StringRequest strReq = new StringRequest(Request.Method.POST,
                            AppConfig.URL_ACCOUNT_STATUS_P, new Response.Listener<String>() {

                        @Override
                        public void onResponse(String response) {
                            LogUtil.logD(TAG, "Login > CheckStatus Response: " + response.toString());

                            try {
                                JSONObject jObj = new JSONObject(response);
                                String message = jObj.getString("message");
                                //boolean error = jObj.getBoolean("error");
                                boolean ok = "OK".equalsIgnoreCase(message);
                                // Check for error node in json
                                if (ok) {
                                    // Now store the user in SQLite
                                    boolean approved = jObj.getBoolean("approved");
                                    boolean active = jObj.getBoolean("active");
                                    String role = jObj.getString("role");
                                    String city = jObj.getString("city");

                                    if(!active){
                                        showActivationLayout(phone);
                                    }else {
                                        if (approved) {
                                            String api= jObj.getString("api");
                                            //update user detail
                                            db.updateUserPhone(phone, role, city, api, active, approved);
                                        }
                                        if(city ==null || city.isEmpty() || city.equalsIgnoreCase("null")){
                                            //showPinAddressForm();
                                            AppController.getInstance().city = null;
                                        }else {
                                            AppController.getInstance().city = city;
                                        }
                                        showMainPage();
                                        //}
                                    }
                                } else {
                                    // Error in login. Get the error message
                                    //String errorMsg = jObj.getString("message");
                                    Toast.makeText(context, ""+message, Toast.LENGTH_SHORT).show();
                                    _loginButton.setEnabled(true);
                                }
                            } catch (JSONException e) {
                                // JSON error
                                e.printStackTrace();
                                Toast.makeText(context, "Json error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                _loginButton.setEnabled(true);
                            }

                            progressBar.dismiss();
                        }
                    }, new Response.ErrorListener() {

                        @Override
                        public void onErrorResponse(VolleyError error) {
                            LogUtil.logE(TAG, "Login Error: " + error.getMessage());
                            Toast.makeText(context, "Login Error: "+error.getMessage(), Toast.LENGTH_SHORT).show();

                            _loginButton.setEnabled(true);
                            progressBar.dismiss();
                        }
                    }) {

                        @Override
                        protected Map<String, String> getParams() {
                            // Posting parameters to  url
                            Map<String, String> params = new HashMap<String, String>();
                            params.put("form-type", "json");
                            params.put("form-phone", phone);

                            return params;
                        }

                        @Override
                        public Map<String, String> getHeaders() throws AuthFailureError {
                            return getKurindoHeaders();
                        }
                    };

                    // Adding request to request queue
                    AppController.getInstance().addToRequestQueue(strReq, tag_string_req);

                }
            });

        }
    }

    private void showMainPage() {
        Intent intent = new Intent(getActivity(), MainDrawerActivity.class);
        startActivity(intent);
        getActivity().finish();
    }

    private void showWaitingApprovalPage() {
        Intent intent = new Intent(context, WaitingApprovalActivity.class);
        intent.putExtra("phone", phoneInput.getPhoneNumber());
        startActivityForResult(intent, REQUEST_WAITINGAPPROVAL);
        getActivity().finish();
    }

    private void showActivationPage(){
        Intent intent = new Intent(context, ActivationActivity.class);
        intent.putExtra("phone", phoneInput.getPhoneNumber());
        startActivityForResult(intent, REQUEST_ACTIVATION);
    }
    public void showActivationLayout(String phone) {
        _activationPhoneText.setPhoneNumber(phone);
        _activationCodeText.setText(null);
        _activationPasswordText.setText(null);
        _activationPassword2Text.setText(null);

        _activationLayout.setVisibility(View.VISIBLE);
        _loginLayout.setVisibility(View.GONE);
        _recoveryLayout.setVisibility(View.GONE);
        _recoverLink.setVisibility(View.GONE);

        _activationButton.setEnabled(true);
        _activationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activate();
            }
        });
    }

    private void showLoginLayout() {
        if(_loginLayout != null){
            _activationLayout.setVisibility(View.GONE);
            _loginLayout.setVisibility(View.VISIBLE);
            _recoveryLayout.setVisibility(View.GONE);
            _recoverLink.setVisibility(View.VISIBLE);

            _loginButton.setEnabled(true);
        }
    }

    private void updateFromParam(){
        String phone = getActivity().getIntent().getStringExtra("phone");
        if(phone !=null && !phone.isEmpty()) phoneInput.setPhoneNumber(phone);
    }

    @OnClick(R.id.btn_activation)
    public void activate() {
        Log.d(TAG, "Activation");

        if (!validateActivation()) {
            onActivationFailed();
            return;
        }

        _activationButton.setEnabled(false);

        progressBar.setIndeterminate(true);
        progressBar.setMessage("Processing activation....");
        progressBar.show();
        final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message mesg) {
                throw new RuntimeException("RuntimeException");
            }
        };
        activation_process(handler);
        try { Looper.loop(); }
        catch(RuntimeException e2) { }
    }

    private boolean validateActivation() {
        boolean valid = true;

        String activationCode = _activationCodeText.getText().toString();
        String password = _activationPasswordText.getText().toString();
        String password2 = _activationPassword2Text.getText().toString();

        if (_activationPhoneText.isValid()) {
            _activationPhoneText.setError(null);
        } else {
            _activationPhoneText.setError(getString(R.string.enter_valid_phone));
            valid = false;
        }
        if (activationCode.isEmpty() || activationCode.length() != 4) {
            _activationCodeText.setError("4 angka unik");
            valid = false;
        } else {
            _activationCodeText.setError(null);
        }

        if (password.isEmpty() || password.length() < 4) {
            _activationPasswordText.setError("minimal 4 karakter.");
            valid = false;
        }else if(!password.equalsIgnoreCase(password2)){
            _activationPasswordText.setError("Passwords tidak cocok.");
            _activationPassword2Text.setError("Passwords tidak cocok.");
            valid = false;
        } else {
            _activationPasswordText.setError(null);
        }

        return valid;
    }

    private void onActivationFailed() {
        Toast.makeText(context, "Activation failed", Toast.LENGTH_LONG).show();
        _activationButton.setEnabled(true);
    }

    private void activation_process(final Handler handler) {
        String tag_string_req = "req_activation";

        final String phone = _activationPhoneText.getPhoneNumber();
        final String activationCode = _activationCodeText.getText().toString();
        final String password = _activationPasswordText.getText().toString();

        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_BACKEND_ACTIVATION_PHONE, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                LogUtil.logD(TAG, "Activation Response: " + response.toString());

                try {
                    JSONObject jObj = new JSONObject(response);
                    String message = jObj.getString("message");
                    boolean ok = "OK".equalsIgnoreCase(message);
                    if (ok) {
                        // User successfully stored in MySQL
                        //store_user_information(jObj);
                        // Show Validation Page
                        Toast.makeText(context, "User successfully activated!", Toast.LENGTH_LONG).show();

                        boolean active = jObj.getBoolean("active");
                        if(active){
                            db.onUpgrade(db.getWritableDatabase(), 0, 1);
                            //db.onCreateTableRecipient(db.getWritableDatabase());
                            db.onUpgradeTableRecipient(db.getWritableDatabase(),0,1);

                            db.onUpgradeUserAddress(db.getWritableDatabase(), 0, 1);
                            //db.activatedUsers(email);

                            ParserUtil parser = new ParserUtil();
                            TUser tuser = parser.parserUser(jObj);
                            db.addUser(tuser);
                            String city = null;
                            try {
                                city = tuser.getAddress().getCity().getCode();
                            }catch (Exception e){}

                            // Now store the user in SQLite
                            /*
                            String uid = jObj.getString("user");
                            String role = jObj.getString("role");
                            String city = jObj.getString("city");

                            JSONObject user = jObj.getJSONObject("data");
                            String firstname = user.getString("firstname");
                            String lastname = user.getString("lastname");
                            String email = user.getString("email");
                            String created_at = user.getString("created");
                            String phone = user.getString("phone");
                            String gender = user.getString("gender");

                            boolean approved = jObj.getBoolean("approved");
                            String api_key = jObj.getString("api_key");

                            // Inserting row in users table
                            db.addUser(firstname, lastname, email, phone, gender, uid, role, city, api_key, active, approved, created_at);
                            */
                            // Create login session
                            session.setLogin(true);
                            session.setLoginData(tuser.getRole(), city);
                            session.setAutoLogin(_chkAutoLogin.isChecked());
                            session.setActive(active);

                            if(city ==null || city.isEmpty()){
                                //showPinAddressForm();
                                //onLoginSuccess();
                                AppController.getInstance().city = null;
                            }else{
                                db.addAddress(tuser, "HOMEBASE");
                                AppController.getInstance().city = (tuser.getAddress()==null ? null: tuser.getAddress().getKecamatan());
                            }
                                // Launch main activity
                                Toast.makeText(context, "Login Successfully.", Toast.LENGTH_LONG).show();
                                onAlreadyLogin();
                            //}
                        }else{
                            showActivationLayout(phone);
                        }
                        /*boolean approved = jObj.getBoolean("approved");
                        if(!approved) {
                            showWaitingApprovalPage();
                        }else{
                            db.userApproved(email);
                        }*/

                    } else {

                        // Error occurred in registration. Get the error
                        // message
                        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
                        _activationButton.setEnabled(true);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(context, "Json error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    _activationButton.setEnabled(true);
                }
                progressBar.dismiss();
                handler.handleMessage(null);
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                LogUtil.logE(TAG, "Activation Error: " + error.getMessage());
                Toast.makeText(context,error.getMessage(), Toast.LENGTH_LONG).show();
                _activationButton.setEnabled(true);
                progressBar.dismiss();
                handler.handleMessage(null);
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting params to register url
                Map<String, String> params = new HashMap<String, String>();
                params.put("form-phone", phone);
                params.put("form-activation", activationCode);
                params.put("form-password", password);
                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    private void showPinAddressForm() {
        Intent intent = new Intent(context, SignupWizardActivity.class);
        getActivity().startActivityForResult(intent, REQUEST_SignupWizardActivity);

    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        //updateFromParam();
        User user = db.toUser(db.getUserDetails() );
        if(user != null){
            phoneInput.setPhoneNumber(user.getPhone());
            _activationPhoneText.setPhoneNumber(user.getPhone());
        }
    }

    @OnClick(R.id.btn_login)
    public void login() {
        LogUtil.logD(TAG, "Login");

        if (!validate()) {
            onLoginFailed();
            return;
        }

        _loginButton.setEnabled(false);

        progressBar.setIndeterminate(true);
        progressBar.setMessage("Authenticating...");
        progressBar.show();

        String phone = phoneInput.getPhoneNumber();
        String password = _passwordText.getText().toString();

        //Implementation authentication logic here.
        checkLogin(phone, password);

        /*new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        // On complete call either onLoginSuccess or onLoginFailed
                        onLoginSuccess();
                        // onLoginFailed();
                        progressDialog.dismiss();
                    }
                }, 3000);*/
    }

    private void checkLogin(final String phone, final String password){
        String tag_string_req = "req_login";
        //final String token = FirebaseInstanceId.getInstance().getToken();
        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_BACKEND_SIGNIN, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                LogUtil.logD(TAG, "Login Response: " + response.toString());
                //hideDialog();

                try {
                    JSONObject jObj = new JSONObject(response);
                    String message = jObj.getString("message");
                    boolean ok = "OK".equalsIgnoreCase(message);

                    // Check for error node in json
                    if (ok) {
                        _loginButton.setEnabled(true);

                        //TODO: Remove sqllite table user to user_address
                        // user successfully logged in
                        db.onUpgrade(db.getWritableDatabase(), 0, 1);
                        //db.onCreateTableRecipient(db.getWritableDatabase());
                        db.onUpgradeTableRecipient(db.getWritableDatabase(),0,1);

                        db.onUpgradeUserAddress(db.getWritableDatabase(), 0, 1);

                        // Now store the user in SQLite
                        ParserUtil parser = new ParserUtil();
                        JSONObject user = jObj.getJSONObject("data");
                        TUser tuser = parser.parserUser(user);

                        // Inserting row in users table
                        db.addUser(tuser);

                        String city = tuser.getAddress().getCity().getCode();
                        // Create login session
                        session.setLogin(true);
                        session.setLoginData(tuser.getRole(), city);
                        session.setAutoLogin(_chkAutoLogin.isChecked());

                        if(city ==null || city.isEmpty() || city.equalsIgnoreCase("null") ){
                            //showPinAddressForm();
                            AppController.getInstance().city = null;
                        }else {
                            if(tuser.getAddress() != null ){
                                db.addAddress(tuser, "HOMEBASE");
                                AppController.getInstance().city = tuser.getAddress().getKecamatan();
                            }
                        }
                            // Launch main activity
                            Toast.makeText(context, "Login Successfully.", Toast.LENGTH_LONG).show();
                            onAlreadyLogin();
                        //}
                        update_token(tuser.getApi_key());
                    } else if(message.equalsIgnoreCase("WRONG")){
                        Toast.makeText(context, "Incorrect Account or Password.", Toast.LENGTH_LONG).show();
                        _loginButton.setEnabled(true);
                    } else {
                        boolean active = false;
                        boolean approved = false;
                        boolean _null = false;
                        try {
                            active= jObj.getBoolean("active");
                            approved= jObj.getBoolean("approved");
                        }catch (Exception e){
                            _null = true;
                        }

                        // Error in login. Get the error message
                        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
                        _loginButton.setEnabled(true);

                        if(_null){
                        }else if(!active && !message.equalsIgnoreCase("Invalid Account.")){
                            showActivationLayout(phone);
                        }
                        /*else
                        if(!approved){
                            showWaitingApprovalPage();
                        }*/
                    }
                } catch (JSONException e) {
                    // JSON error
                    e.printStackTrace();
                    Toast.makeText(context, "Json error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    _loginButton.setEnabled(true);
                }

                progressBar.dismiss();
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                LogUtil.logE(TAG, "Login Error: " + error.getMessage());
                Toast.makeText(context, error.getMessage(), Toast.LENGTH_LONG).show();

                _loginButton.setEnabled(true);
                progressBar.dismiss();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();
                params.put("form-phone", phone);
                params.put("form-password", password);
                params.put("form-type", "json");
                //params.put("form-token", token);

                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_SIGNUP) {
            if (resultCode == getActivity().RESULT_OK) {

                // TODONE: Implement successful signup logic here
                if(session.isLoggedIn()){
                    // By default we just finish the Activity and log them in automatically
                    getActivity().finish();
                }else{
                    checkStatusAccount();
                }
            }
        } else if (requestCode == REQUEST_ACTIVATION) {
            if (resultCode == getActivity().RESULT_OK) {
                //showMainPage();
                //this.finish();
            }
        } else if (requestCode == REQUEST_SignupWizardActivity) {
            if (resultCode == getActivity().RESULT_OK) {
                Toast.makeText(context, "Login Successfully.", Toast.LENGTH_LONG).show();
                onAlreadyLogin();
            }else{
                showLoginLayout();
            }
        }
    }

    public void onBackPressed() {
        // disable going back to the MainDrawerActivity
        //moveTaskToBack(true);
        getActivity().onBackPressed();
        getActivity().finish();
    }

    public void onAlreadyLogin() {
        //if auto login -> finish -> show MainDrawerActivity
        Intent intent = new Intent(context, MainDrawerActivity.class);
        startActivity(intent);
        onLoginSuccess();
    }

    public void onLoginSuccess() {
        showLoginLayout();
        getActivity().finish();
    }

    public void onLoginFailed() {
        Toast.makeText(context, "Login failed", Toast.LENGTH_LONG).show();

        _loginButton.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;
        String password = _passwordText.getText().toString();

        if (phoneInput.isValid()) {
            phoneInput.setError(null);
        } else {
            phoneInput.setError(getString(R.string.enter_valid_phone));
            valid = false;
        }

        if (password.isEmpty() || password.length() < 4 ) {
            _passwordText.setError("more than 4 alphanumeric characters");
            valid = false;
        } else {
            _passwordText.setError(null);
        }

        return valid;
    }

    private void update_token(final String api_key) {
        if(AppConfig.FCM_TOKEN != null){
            String tag_string_req = "req_sendRegistrationToServer";

            final SQLiteHandler db = new SQLiteHandler(context);

            StringRequest strReq = new StringRequest(Request.Method.POST,
                    AppConfig.URL_REGISTER_FCM, new Response.Listener<String>() {

                @Override
                public void onResponse(String response) {
                    LogUtil.logD(TAG, "update_token Response: " + response.toString());

                    try {
                        JSONObject jObj = new JSONObject(response);

                        boolean error = jObj.getBoolean("error");
                        String message= jObj.getString("message");
                        Log.i(TAG, "message : "+ message);
                        if(!error) AppConfig.FCM_TOKEN = null;

                    } catch (JSONException e) {
                        // JSON error
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    LogUtil.logE(TAG, "update_token Error: " + error.getMessage());
                }
            }) {

                @Override
                protected Map<String, String> getParams() {
                    // Posting parameters to login url
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("form-token", AppConfig.FCM_TOKEN);

                    return params;
                }

                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> params = new HashMap<String, String>();
                    //String api = db.getUserApi();
                    params.put("Api", api_key);
                    params.put("Authorization", api_key);

                    return params;
                }
            };

            // Adding request to request queue
            AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
        }
    }

}
