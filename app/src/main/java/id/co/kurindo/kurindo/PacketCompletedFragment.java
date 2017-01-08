package id.co.kurindo.kurindo;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
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

import id.co.kurindo.kurindo.app.AppConfig;
import id.co.kurindo.kurindo.app.AppController;
import id.co.kurindo.kurindo.model.Packet;

/**
 * Created by DwiM on 11/9/2016.
 */
public class PacketCompletedFragment extends BasePacketMonitoringFragment {
    private static final String TAG = "PacketCompletedFragment";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View x = super.onCreateView(inflater, container,savedInstanceState);
        textView.setText("Order Selesai.");
        return x;
    }

    public void check_order() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                progressBar.setVisibility(View.VISIBLE);
                String tag_string_req = "req_monitor_order_completed";
                StringRequest strReq = new StringRequest(Request.Method.POST,
                        AppConfig.URL_PACKET_MYTASKS , new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        Log.d(TAG, "MonitorOrder > Check : Response: " + response.toString());
                        //hideDialog();

                        try {
                            JSONObject jObj = new JSONObject(response);
                            boolean error = jObj.getBoolean("error");

                            // Check for error node in json
                            if (!error) {

                                parsePackets(packets, jObj);
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
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG, "MonitorOrder Error: " + error.getMessage());
                        Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_LONG).show();
                        progressBar.setVisibility(View.GONE);
                    }
                }) {

                    @Override
                    protected Map<String, String> getParams() {
                        // Posting parameters to  url
                        Map<String, String> params = new HashMap<String, String>();
                        params.put("form-type", "json");
                        params.put("user_agent", AppConfig.USER_AGENT);
                        params.put("job", "COMPLETED");

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


    @Override
    public void onPickButtonClick(View view, int position, String status) {
        ImageButton btn = (ImageButton) view;
        Packet p = packets.get(position);
        Toast.makeText(getContext(), p.getResi(), Toast.LENGTH_LONG).show();
    }

}
