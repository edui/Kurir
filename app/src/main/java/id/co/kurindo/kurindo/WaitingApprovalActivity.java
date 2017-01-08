package id.co.kurindo.kurindo;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
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

/**
 * Created by DwiM on 11/10/2016.
 */
public class WaitingApprovalActivity extends AppCompatActivity {
    private static final String TAG = "WaitingApprovalActivity";

    ProgressDialog progressDialog;

    @Bind(R.id.input_email)
    EditText _emailText;
    @Bind(R.id.input_pesan) EditText _pesanText;

    @Bind(R.id.btn_notify)
    AppCompatButton _notifyButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_waiting_approval);
        ButterKnife.bind(this);
        updateFromParam();
        progressDialog = new ProgressDialog(WaitingApprovalActivity.this,R.style.AppTheme);

        _notifyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                send_message();
            }
        });
    }
    private void updateFromParam(){
        String email = getIntent().getStringExtra("email");
        if(email !=null && !email.isEmpty()) _emailText.setText(email);
    }
    private void send_message() {
        if (!validate()) {
            onSignupFailed();
            return;
        }


        _notifyButton.setEnabled(false);

        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Creating Account...");
        progressDialog.show();

        send_message_process();
    }

    private void send_message_process() {
        String tag_string_req = "req_send_message";

        final String email = _emailText.getText().toString();
        final String pesan = _pesanText.getText().toString();

        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_SENT_MESSAGE, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Sending Message Response: " + response.toString());
                //hideDialog();

                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");
                    if (!error) {

                        Toast.makeText(getApplicationContext(), "Message successfully sent.!", Toast.LENGTH_LONG).show();
                        finish();

                    } else {

                        // message
                        String errorMsg = jObj.getString("message");
                        Toast.makeText(getApplicationContext(),
                                errorMsg, Toast.LENGTH_LONG).show();
                        _notifyButton.setEnabled(true);
                        progressDialog.dismiss();

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Json error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    _notifyButton.setEnabled(true);
                    progressDialog.dismiss();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Sending Message Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_LONG).show();
                progressDialog.dismiss();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting params to register url
                Map<String, String> params = new HashMap<String, String>();
                params.put("form-email", email);
                params.put("form-pesan", pesan);
                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    private boolean validate() {
        boolean valid = true;
        String email = _emailText.getText().toString();
        String pesan =_pesanText.getText().toString();
        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _emailText.setError("enter a valid email address");
            valid = false;
        } else {
            _emailText.setError(null);
        }

        if (pesan.isEmpty() || pesan.length() < 10) {
            _pesanText.setError("at least 10 charactes");
            valid = false;
        } else {
            _pesanText.setError(null);
        }

        return valid;
    }

    private void onSignupFailed() {

    }

}
