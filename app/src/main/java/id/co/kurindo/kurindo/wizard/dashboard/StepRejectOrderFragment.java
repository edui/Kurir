package id.co.kurindo.kurindo.wizard.dashboard;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.stepstone.stepper.Step;
import com.stepstone.stepper.VerificationError;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import id.co.kurindo.kurindo.R;
import id.co.kurindo.kurindo.RecyclerItemClickListener;
import id.co.kurindo.kurindo.adapter.UserAdapter;
import id.co.kurindo.kurindo.app.AppConfig;
import id.co.kurindo.kurindo.app.AppController;
import id.co.kurindo.kurindo.model.Address;
import id.co.kurindo.kurindo.model.City;
import id.co.kurindo.kurindo.model.Order;
import id.co.kurindo.kurindo.model.User;
import id.co.kurindo.kurindo.wizard.AcceptOrderActivity;
import id.co.kurindo.kurindo.wizard.RejectOrderActivity;
import id.co.kurindo.kurindo.wizard.checkout.BaseStepFragment;

import static android.app.Activity.RESULT_OK;

/**
 * Created by aspire on 12/15/2016.
 */

public class StepRejectOrderFragment extends BaseStepFragment implements Step {
    private static final String TAG = "StepAcceptOrderFragment";

    @Bind(R.id.input_reason)
    EditText reasonEt;
    @Bind(R.id.inlay_reason)
    TextInputLayout reasonLayout;
    Order order;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflateAndBind(inflater, container, R.layout.fragment_accept_order);

        order = ((RejectOrderActivity)getActivity()).getOrder();

        reasonLayout.setVisibility(View.VISIBLE);
        return v;
    }



    @Override
    public int getName() {
        return 0;
    }

    @Override
    public VerificationError verifyStep() {
        String reason = reasonEt.getText().toString();
        if(reason == null || reason.isEmpty()){
            return new VerificationError("Error: Tulisan alasannya.");
        }
        final VerificationError[] verify = {null};

        // make a handler that throws a runtime exception when a message is received
        final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message mesg) {
                throw new RuntimeException();
            }
        };

        DialogInterface.OnClickListener YesClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                verify[0] = place_order(handler);
            }
        };
        showConfirmationDialog("Konfirmasi","Pesanan ini akan ditolak. Anda yakin?", YesClickListener, null);

        // loop till a runtime exception is triggered.
        try { Looper.loop(); }
        catch(RuntimeException e2) {}


        return null;
    }

    private VerificationError place_order(final Handler handler) {
        String tag_string_req = "req_place_order";
        final VerificationError[] verify = {null};

        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_ORDER_REJECT, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Process_order Response: " + response.toString());
                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");
                    String message = jObj.getString("message");
                    if (!error) {
                        //success
                        order.setStatus(AppConfig.KEY_KUR999);
                        Intent intent = new Intent();
                        intent.putExtra("order", order);
                        getActivity().setResult(RESULT_OK, intent);
                        verify[0] = null;
                    }else{
                        verify[0] = new VerificationError("Error: "+message);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    verify[0] = new VerificationError("Error: "+e.getMessage());
                }
                handler.handleMessage(null);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Process_order Error: " + error.getMessage());
                verify[0] = new VerificationError("Error: "+error.getMessage());
                handler.handleMessage(null);
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting params to register url
                HashMap<String, String> params = new HashMap<>();

                params.put("awb",(order ==null? null : order.getAwb()));
                params.put("reason", reasonEt.getText().toString());
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

        return verify[0];
    }

    @Override
    public void onSelected() {
    }

    @Override
    public void onError(@NonNull VerificationError error) {

    }
}
