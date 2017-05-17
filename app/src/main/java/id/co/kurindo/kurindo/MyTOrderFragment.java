package id.co.kurindo.kurindo;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

import butterknife.ButterKnife;
import id.co.kurindo.kurindo.app.AppConfig;
import id.co.kurindo.kurindo.app.AppController;
import id.co.kurindo.kurindo.util.LogUtil;

/**
 * Created by dwim on 1/6/2017.
 */

public class MyTOrderFragment extends BaseTOrderMonitoringFragment {
    private static final String TAG = "MyOrderFragment";

    String task = "new";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        task = bundle.getString("task");
    }

    public static MyTOrderFragment newInstance(String task) {
        MyTOrderFragment fragment = new MyTOrderFragment();
        Bundle args = new Bundle();
        args.putString("task", task);
        fragment.setArguments(args);
        return fragment;
    }
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View x = super.onCreateView(inflater, container,savedInstanceState);
        ButterKnife.bind(getActivity());

        return x;
    }

    public void onPickButtonClick(View view, final int position, final String status) {

    }
    @Override
    public void check_order() {
        check_order("");
    }

    public void check_order(final String... params) {
        if(getActivity() != null){
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    String param = params[0].toString();
                    String URI = AppConfig.URL_TORDER_MYORDERS;
                    //URI = URI.replace("/{filter}", "/"+param);

                    progressBar.setVisibility(View.VISIBLE);
                    refreshBtn.setVisibility(View.GONE);

                    String tag_string_req = "req_monitor_order_my_inprogress";
                    StringRequest strReq = new StringRequest(Request.Method.POST,
                            URI, new Response.Listener<String>() {

                        @Override
                        public void onResponse(String response) {
                            LogUtil.logD(TAG, "MonitorOrder > Check: Response:" + response.toString());
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
                            LogUtil.logE(TAG, "MonitorOrder Error: " + error.getMessage());
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
                            params.put("job", task.toUpperCase());

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

}
