package id.co.kurindo.kurindo;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
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
import butterknife.ButterKnife;
import id.co.kurindo.kurindo.app.AppConfig;
import id.co.kurindo.kurindo.app.AppController;

/**
 * Created by DwiM on 11/9/2016.
 */

public class TOrderInProgressFragment extends BaseTOrderMonitoringFragment implements CompoundButton.OnCheckedChangeListener {
    private static final String TAG = "OrderInProgressFragment";

    @Bind(R.id.checkBox1)
    CheckBox doSendChk;
    @Bind(R.id.checkBox2)
    CheckBox doJekChk;
    @Bind(R.id.checkBox3)
    CheckBox doWashChk;
    @Bind(R.id.checkBox4)
    CheckBox doServiceChk;
    @Bind(R.id.checkBox5)
    CheckBox doHijamahChk;
    @Bind(R.id.checkBox6)
    CheckBox doCarChk;
    @Bind(R.id.checkBox7)
    CheckBox doMoveChk;

    String params="";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View x = super.onCreateView(inflater, container,savedInstanceState);
        textView.setText("Order sedang diproses...");
        ButterKnife.bind(getActivity());

        //doSendChk.setOnCheckedChangeListener(this);
        return x;
    }

    public void check_order(){
        check_order("");
    }

    public void check_order(final String... params) {
        if(getActivity() != null){
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    String param = params[0].toString();
                    String URI = AppConfig.URL_TORDER_MYTASKS;
                    //URI = URI.replace("/{filter}", "/"+param);

                    progressBar.setVisibility(View.VISIBLE);
                    refreshBtn.setVisibility(View.GONE);

                    String tag_string_req = "req_monitor_order_inprogress";
                    StringRequest strReq = new StringRequest(Request.Method.POST,
                            URI, new Response.Listener<String>() {

                        @Override
                        public void onResponse(String response) {
                            Log.d(TAG, "MonitorOrder > Check: Response:" + response.toString());
                            //hideDialog();

                            try {
                                JSONObject jObj = new JSONObject(response);
                                boolean error = jObj.getBoolean("error");

                                // Check for error node in json
                                if (!error) {

                                    Bundle bundle = parseOrders(orders, jObj);
                                    adapter.notifyDataSetChanged();

                                } else {
                                    // Error in login. Get the error message
                                    String errorMsg = jObj.getString("message");
                                    Toast.makeText(getContext(), errorMsg, Toast.LENGTH_LONG).show();
                                }
                            } catch (JSONException e) {
                                // JSON error
                                e.printStackTrace();
                                Toast.makeText(getContext(), "Json error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                            }
                            progressBar.setVisibility(View.GONE);
                            refreshBtn.setVisibility(View.VISIBLE);
                        }
                    }, new Response.ErrorListener() {

                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.e(TAG, "MonitorOrder Error: " + error.getMessage());
                            Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_LONG).show();
                            progressBar.setVisibility(View.GONE);
                            refreshBtn.setVisibility(View.VISIBLE);
                        }
                    }) {

                        @Override
                        protected Map<String, String> getParams() {
                            // Posting parameters to  url
                            Map<String, String> params = new HashMap<String, String>();
                            params.put("form-type", "json");
                            params.put("user_agent", "KURINDROID");
                            params.put("job", "INPROGRESS");

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
            });

        }

    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        String params = "";
        switch (buttonView.getId()){
            case R.id.checkBox1:
                if(isChecked)
                    params += AppConfig.KEY_DOSEND;
                else
                    params.replace(AppConfig.KEY_DOSEND,"");
                break;
            case R.id.checkBox2:
                params = AppConfig.KEY_DOJEK;
                break;
            case R.id.checkBox3:
                params = AppConfig.KEY_DOWASH;
                break;
            case R.id.checkBox4:
                params = AppConfig.KEY_DOSERVICE;
                break;
            case R.id.checkBox5:
                params = AppConfig.KEY_DOHIJAMAH;
                break;
            case R.id.checkBox6:
                params = AppConfig.KEY_DOCAR;
                break;
            case R.id.checkBox7:
                params = AppConfig.KEY_DOMOVE;
                break;
            default:
        }
        check_order(params);
    }
}
