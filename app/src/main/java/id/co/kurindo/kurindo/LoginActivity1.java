package id.co.kurindo.kurindo;

/**
 * Created by DwiM on 10/17/2016.
 */

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatCheckBox;
import android.util.Log;

import android.content.Intent;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import id.co.kurindo.kurindo.app.AppConfig;
import id.co.kurindo.kurindo.app.AppController;
import id.co.kurindo.kurindo.helper.SQLiteHandler;
import id.co.kurindo.kurindo.helper.SessionManager;
import id.co.kurindo.kurindo.model.User;

public class LoginActivity1 extends AppCompatActivity {
    private static final String TAG = "LoginActivity1";
    private static final int REQUEST_SIGNUP = 0;
    private static final int REQUEST_ACTIVATION = 1;
    private static final int REQUEST_WAITINGAPPROVAL = 2;

    @Nullable @Bind(R.id.input_email) EditText _emailText;
    @Nullable @Bind(R.id.input_password) EditText _passwordText;
    @Nullable @Bind(R.id.btn_login)    AppCompatButton _loginButton;
    @Nullable @Bind(R.id.link_signup) TextView _signupLink;
    @Nullable @Bind(R.id.chkAutoLogin)    AppCompatCheckBox _chkAutoLogin;

    @Nullable @Bind(R.id.link_recover) TextView _recoverLink;
    @Nullable @Bind(R.id.layout_recovery) LinearLayout _recoveryLayout;
    @Nullable @Bind(R.id.layout_login) LinearLayout _loginLayout;
    @Nullable @Bind(R.id.btn_recover)    AppCompatButton _recoverButton;
    @Nullable @Bind(R.id.input_email_recovery) EditText _recoveryEmailText;

    private SessionManager session;
    private SQLiteHandler db;
    private ProgressDialog progressDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        // SQLite database handler
        db = new SQLiteHandler(getApplicationContext());
        //db.onUpgrade(db.getWritableDatabase(), 0, 1);
        // Session manager
        session = new SessionManager(getApplicationContext());
        //session.setLogin(false);

        // Check if user is already logged in or not
        if (session.isLoggedIn()) {
            //check account status
            checkStatusAccount();

            // User is already logged in. Take him to main activity
            onAlreadyLogin();
        }

        User user = db.toUser(db.getUserDetails() );
        if(user != null)
            _emailText.setText(user.getEmail());

        progressDialog = new ProgressDialog(LoginActivity1.this,R.style.AppTheme);

        //checkStatusAccount();

        _loginButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                login();
            }
        });

        _signupLink.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // Start the Signup activity
                Intent intent = new Intent(getApplicationContext(), SignupActivity.class);
                intent.putExtra("email", _emailText.getText().toString());
                startActivityForResult(intent, REQUEST_SIGNUP);
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
    }

    private void recovery_account() {
        boolean valid = true;
        final String email = _recoveryEmailText.getText().toString();
        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _recoveryEmailText.setError("enter a valid email address");
            valid = false;
        } else {
            _recoveryEmailText.setError("");
        }

        if(valid){
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    final String token = FirebaseInstanceId.getInstance().getToken();

                    String tag_string_req = "req_recovery_account";
                    StringRequest strReq = new StringRequest(Request.Method.POST,
                            AppConfig.URL_ACCOUNT_RECOVERY, new Response.Listener<String>() {

                        @Override
                        public void onResponse(String response) {
                            Log.d(TAG, "RecoveryAccount Response: " + response.toString());

                            try {
                                JSONObject jObj = new JSONObject(response);
                                boolean error = jObj.getBoolean("error");

                                // Check for error node in json
                                if (!error) {

                                }
                            } catch (JSONException e) {
                                // JSON error
                                e.printStackTrace();
                                Toast.makeText(getApplicationContext(), "Json error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                                _recoverButton.setEnabled(true);
                            }
                            progressDialog.dismiss();
                        }
                    }, new Response.ErrorListener() {

                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.e(TAG, "Recovery Error: " + error.getMessage());
                            Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();

                            _recoverButton.setEnabled(true);
                            progressDialog.dismiss();
                        }
                    }) {
                        @Override
                        protected Map<String, String> getParams() {
                            // Posting parameters to  url
                            Map<String, String> params = new HashMap<String, String>();
                            params.put("form-type", "json");
                            params.put("form-email", email);
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

    private void checkStatusAccount() {
        String email = "";//_emailText.getText().toString();
        boolean active = true;
        boolean approved = true;
        String role = null;
       //if(email == null || email.isEmpty()){
            HashMap users = db.getUserDetails();
            if(users != null){
                Object o = users.get("email");
                if(o != null){
                    _emailText.setText(o.toString());
                    email = o.toString();
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
            showActivationPage();
        }else
        if(!approved ) {
            if(!role.equalsIgnoreCase(AppConfig.KEY_PELANGGAN)){
                request_checkStatusAccount(email);
            }
        }
    }

    private void request_checkStatusAccount(final String email){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
        String tag_string_req = "req_status_account";
        StringRequest strReq = new StringRequest(Request.Method.GET,
                AppConfig.URL_ACCOUNT_STATUS + "/"+email, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Login > CheckStatus Response: " + response.toString());
                //hideDialog();

                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");

                    // Check for error node in json
                    if (!error) {
                        // Now store the user in SQLite
                        boolean approved = jObj.getBoolean("approved");
                        boolean active = jObj.getBoolean("active");
                        String role = jObj.getString("role");

                        if(!active){
                            showActivationPage();
                        }else {
                            if (approved) {
                                //update user detail
                                db.updateUser(email, role, active, approved);
                            }
                            showMainPage();
                        }
                    } else {
                        // Error in login. Get the error message
                        String errorMsg = jObj.getString("message");
                        Toast.makeText(getApplicationContext(), errorMsg, Toast.LENGTH_LONG).show();
                        _loginButton.setEnabled(true);
                    }
                } catch (JSONException e) {
                    // JSON error
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Json error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    _loginButton.setEnabled(true);
                }

                progressDialog.dismiss();
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Login Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();

                _loginButton.setEnabled(true);
                progressDialog.dismiss();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to  url
                Map<String, String> params = new HashMap<String, String>();
                params.put("form-type", "json");

                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);

            }
        });
    }

    private void showMainPage() {
        Intent intent = new Intent(getApplicationContext(), MainDrawerActivity.class);
        startActivity(intent);
        finish();
    }

    private void showWaitingApprovalPage() {
        Intent intent = new Intent(getApplicationContext(), WaitingApprovalActivity.class);
        intent.putExtra("email", _emailText.getText().toString());
        startActivityForResult(intent, REQUEST_WAITINGAPPROVAL);
        finish();
    }

    private void showActivationPage(){
        Intent intent = new Intent(getApplicationContext(), ActivationActivity.class);
        intent.putExtra("email", _emailText.getText().toString());
        startActivityForResult(intent, REQUEST_ACTIVATION);
    }
    private void updateFromParam(){
        String email = getIntent().getStringExtra("email");
        if(email !=null && !email.isEmpty()) _emailText.setText(email);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //updateFromParam();
    }

    public void login() {
        Log.d(TAG, "Login");

        if (!validate()) {
            onLoginFailed();
            return;
        }

        _loginButton.setEnabled(false);

        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Authenticating...");
        progressDialog.show();

        String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();

        //Implementation authentication logic here.
        checkLogin(email, password);

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

    private void checkLogin(final String email, final String password){
        String tag_string_req = "req_login";
        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_LOGIN, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Login Response: " + response.toString());
                //hideDialog();

                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");

                    // Check for error node in json
                    if (!error) {
                        // user successfully logged in
                        db.onUpgrade(db.getWritableDatabase(), 0, 1);
                        db.onCreateTableRecipient(db.getWritableDatabase());
                        //db.onUpgradeTableRecipient(db.getWritableDatabase(),0,1);

                        // Now store the user in SQLite
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

                        boolean active = jObj.getBoolean("active");
                        boolean approved = jObj.getBoolean("approved");
                        String api_key = jObj.getString("api_key");

                        // Inserting row in users table
                        db.addUser(firstname, lastname, email, phone, gender, uid, role, city, api_key, active, approved, created_at);

                        // Create login session
                        session.setLogin(true);
                        session.setLoginData(role, city);
                        session.setAutoLogin(_chkAutoLogin.isChecked());

                        // Launch main activity
                        onAlreadyLogin();

                    } else {
                        boolean active= jObj.getBoolean("active");
                        boolean approved= jObj.getBoolean("approved");

                        // Error in login. Get the error message
                        String errorMsg = jObj.getString("message");
                        Toast.makeText(getApplicationContext(), errorMsg, Toast.LENGTH_LONG).show();
                        _loginButton.setEnabled(true);

                        if(!active){
                            showActivationPage();
                        }
                        /*else
                        if(!approved){
                            showWaitingApprovalPage();
                        }*/
                    }
                } catch (JSONException e) {
                    // JSON error
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Json error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    _loginButton.setEnabled(true);
                }

                progressDialog.dismiss();
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Login Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();

                _loginButton.setEnabled(true);
                progressDialog.dismiss();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();
                params.put("form-email", email);
                params.put("form-password", password);
                params.put("form-type", "json");

                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_SIGNUP) {
            if (resultCode == RESULT_OK) {

                // TODONE: Implement successful signup logic here
                if(session.isLoggedIn()){
                    // By default we just finish the Activity and log them in automatically
                    this.finish();
                }else{
                    checkStatusAccount();
                }
            }
        } else if (requestCode == REQUEST_ACTIVATION) {
            if (resultCode == RESULT_OK) {
                //showMainPage();
                //this.finish();
            }
        }
    }

    @Override
    public void onBackPressed() {
        // disable going back to the MainDrawerActivity
        //moveTaskToBack(true);
        super.onBackPressed();
        finish();
    }

    public void onAlreadyLogin() {
        //if auto login -> finish -> show MainDrawerActivity
        Intent intent = new Intent(getApplicationContext(), MainDrawerActivity.class);
        startActivityForResult(intent, 0);
        onLoginSuccess();
    }

    public void onLoginSuccess() {
        Toast.makeText(getBaseContext(), "Login Successfully.", Toast.LENGTH_LONG).show();

        _loginButton.setEnabled(true);
        finish();
    }

    public void onLoginFailed() {
        Toast.makeText(getBaseContext(), "Login failed", Toast.LENGTH_LONG).show();

        _loginButton.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;

        String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _emailText.setError("enter a valid email address");
            valid = false;
        } else {
            _emailText.setError(null);
        }

        if (password.isEmpty() || password.length() < 4 ) {
            _passwordText.setError("more than 4 alphanumeric characters");
            valid = false;
        } else {
            _passwordText.setError(null);
        }

        return valid;
    }
}