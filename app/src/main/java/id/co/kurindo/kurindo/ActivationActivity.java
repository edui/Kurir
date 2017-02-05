package id.co.kurindo.kurindo;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

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

/**
 * Created by DwiM on 11/10/2016.
 */
public class ActivationActivity extends AppCompatActivity {
    private static final String TAG = "ActivationActivity";
    private static final int REQUEST_WAITINGAPPROVAL = 2;

    private SessionManager session;
    private SQLiteHandler db;
    ProgressDialog progressDialog;

    @Nullable @Bind(R.id.input_email) EditText _emailText;
    @Bind(R.id.input_activation_code) EditText _activationCodeText;
    @Bind(R.id.input_password) EditText _passwordText;
    @Bind(R.id.input_password2) EditText _password2Text;

    @Bind(R.id.btn_signup)    Button _signupButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_activation);
        ButterKnife.bind(this);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        db = new SQLiteHandler(getApplicationContext());
        session = new SessionManager(getApplicationContext());
        progressDialog = new ProgressDialog(ActivationActivity.this,R.style.AppTheme);

        String email = getIntent().getStringExtra("email");
        _emailText.setText(email);

        _signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activate();
            }
        });

    }

    private void activate() {
        Log.d(TAG, "Activation");

        if (!validate()) {
            onSignupFailed();
            return;
        }

        _signupButton.setEnabled(false);

        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Processing Account activation....");
        progressDialog.show();

        activation_process();
    }

    private void activation_process() {
        String tag_string_req = "req_activation";

        final String email = _emailText.getText().toString();
        final String activationCode = _activationCodeText.getText().toString();
        final String password = _passwordText.getText().toString();

        StringRequest strReq = new StringRequest(Request.Method.POST, AppConfig.URL_ACCOUNT_ACTIVATION, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Activation Response: " + response.toString());
                //hideDialog();

                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");
                    if (!error) {
                        // User successfully stored in MySQL
                       //store_user_information(jObj);
                        // Show Validation Page
                        Toast.makeText(getApplicationContext(), "User successfully activated!", Toast.LENGTH_LONG).show();

                        boolean active = jObj.getBoolean("active");
                        if(active){
                            db.activatedUsers(email);
                        }
                        /*boolean approved = jObj.getBoolean("approved");
                        if(!approved) {
                            showWaitingApprovalPage();
                        }else{
                            db.userApproved(email);
                        }*/
                        // Launch Home activity
                        onSignupSuccess();

                    } else {

                        // Error occurred in registration. Get the error
                        // message
                        String errorMsg = jObj.getString("message");
                        Toast.makeText(getApplicationContext(),
                                errorMsg, Toast.LENGTH_LONG).show();
                        _signupButton.setEnabled(true);
                        progressDialog.dismiss();

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Json error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    _signupButton.setEnabled(true);
                    progressDialog.dismiss();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Activation Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_LONG).show();
                _signupButton.setEnabled(true);
                progressDialog.dismiss();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting params to register url
                Map<String, String> params = new HashMap<String, String>();
                params.put("form-email", email);
                params.put("form-activation", activationCode);
                params.put("form-password", password);
                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }
    private void showWaitingApprovalPage() {
        Intent intent = new Intent(getApplicationContext(), WaitingApprovalActivity.class);
        intent.putExtra("email", _emailText.getText().toString());
        startActivityForResult(intent, REQUEST_WAITINGAPPROVAL);
        finish();
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
            String created_at = user.getString("created");
            String phone = user.getString("phone");
            String gender = user.getString("gender");

            boolean active = jObj.getBoolean("active");
            boolean approved = jObj.getBoolean("approved");


            // Inserting row in users table
            db.addUser(firstname, lastname, email, phone, gender, uid, role, city, api_key, active, approved, created_at);

            // Create login session
            //session.setLogin(true);
            //session.setLoginData(role, city);
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "Json error: " + e.getMessage(), Toast.LENGTH_LONG).show();
            _signupButton.setEnabled(true);
            progressDialog.dismiss();
        }
    }

    public void onSignupSuccess() {
        _signupButton.setEnabled(true);
        getIntent().putExtra("email", _emailText.getText().toString());
        setResult(RESULT_OK, getIntent());
        finish();
    }
    private boolean validate() {
        boolean valid = true;

        String email = _emailText.getText().toString();
        String activationCode = _activationCodeText.getText().toString();
        String password = _passwordText.getText().toString();
        String password2 = _password2Text.getText().toString();

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _emailText.setError("enter a valid email address");
            valid = false;
        } else {
            _emailText.setError(null);
        }
        if (activationCode.isEmpty() || activationCode.length() != 4) {
            _activationCodeText.setError("contains 4 numeric");
            valid = false;
        } else {
            _activationCodeText.setError(null);
        }

        if (password.isEmpty() || password.length() < 4) {
            _passwordText.setError("at least 4 characters");
            valid = false;
        }else if(!password.equalsIgnoreCase(password2)){
            _passwordText.setError("Passwords doesn't matched.");
            _password2Text.setError("Passwords doesn't matched.");
            valid = false;
        } else {
            _passwordText.setError(null);
        }

        return valid;
    }

    private void onSignupFailed() {
    }
}
