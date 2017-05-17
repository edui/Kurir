package id.co.kurindo.kurindo;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatButton;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import id.co.kurindo.kurindo.app.AppConfig;
import id.co.kurindo.kurindo.app.AppController;
import id.co.kurindo.kurindo.base.BaseFragment;
import id.co.kurindo.kurindo.util.LogUtil;

/**
 * Created by DwiM on 11/14/2016.
 */
public class KerjasamaFragment extends BaseFragment {
    private static final String TAG = "KerjasamaFragment";

    private ProgressDialog progressDialog;

    @Bind(R.id.input_email)
    EditText _emailText;
    @Bind(R.id.input_pesan)
    EditText _pesanText;

    @Bind(R.id.btn_notify)
    Button _notifyButton;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflateAndBind(inflater, container, R.layout.fragment_kerjasama);

        _notifyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                send_message();
            }
        });

        progressDialog = new ProgressDialog(getActivity(),R.style.AppTheme);

        return rootView ;
    }

    private void send_message() {
        if (!validate()) {
            onSignupFailed();
            return;
        }


        _notifyButton.setEnabled(false);

        final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message mesg) {
                throw new RuntimeException();
            }
        };

        DialogInterface.OnClickListener YesClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                send_message_process();
                handler.handleMessage(null);
            }
        };
        showConfirmationDialog("Konfirmasi","Anda Yakin data anda sudah benar? Data anda akan dikirimkan sebagai permintaan kerjasama?", YesClickListener, null);

        // loop till a runtime exception is triggered.
        try { Looper.loop(); }
        catch(RuntimeException e2) {}
    }

    private void send_message_process() {
        String tag_string_req = "req_send_kerjasama";

        final String email = _emailText.getText().toString();
        final String pesan = _pesanText.getText().toString();

        progressDialog.show();

        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_SENT_KERJASAMA, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                LogUtil.logD(TAG, "Sending Message Response: " + response.toString());
                //hideDialog();

                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");
                    if (!error) {

                        Toast.makeText(getContext(), "Message successfully sent.!", Toast.LENGTH_LONG).show();
                        ((MainDrawerActivity)getActivity()).showFragment(HomeFragment.class);
                    } else {

                        // message
                        String errorMsg = jObj.getString("message");
                        Toast.makeText(getContext(),
                                errorMsg, Toast.LENGTH_LONG).show();
                        _notifyButton.setEnabled(true);

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getContext(), "Json error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    _notifyButton.setEnabled(true);
                }

                progressDialog.dismiss();
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                LogUtil.logE(TAG, "Sending Message Error: " + error.getMessage());
                Toast.makeText(getContext(),
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

            @Override
            public Map<String, String> getHeaders() {
                return getKurindoHeaders();
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
