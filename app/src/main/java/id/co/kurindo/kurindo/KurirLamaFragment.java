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
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import id.co.kurindo.kurindo.app.AppConfig;
import id.co.kurindo.kurindo.app.AppController;
import id.co.kurindo.kurindo.model.User;

/**
 * Created by DwiM on 12/12/2016.
 */

public class KurirLamaFragment extends BaseKurirFragment{
    private static final String TAG = "KurirLamaFragment";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = super.onCreateView(inflater,container,savedInstanceState);

        textView.setText("Kurir...");


        return rootView;
    }

    public void check_kurir() {
        check_kurir("KURIR");
    }
    public void check_kurir(final String... params) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {

                String param = params[0].toString();
                String URI = AppConfig.URL_LIST_KURIR;
                URI = URI.replace("/{type}", "/"+param);

                progressBar.setVisibility(View.VISIBLE);
                String tag_string_req = "req_monitor_kurir_old";
                StringRequest strReq = new StringRequest(Request.Method.GET,
                        URI, new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        Log.d(TAG, "MonitorKurir > Check: Response:" + response.toString());
                        //hideDialog();

                        try {
                            JSONObject jObj = new JSONObject(response);
                            boolean error = jObj.getBoolean("error");

                            // Check for error node in json
                            if (!error) {

                                GsonBuilder builder = new GsonBuilder();
                                builder.setPrettyPrinting();
                                Gson gson = builder.create();

                                JSONArray datas = jObj.getJSONArray("data");
                                if(datas != null && datas.length() > 0){
                                    users.clear();

                                    for (int i = 0; i < datas.length(); i++) {
                                        JSONObject data = datas.optJSONObject(i);

                                        User user = gson.fromJson(data.toString(), User.class);
                                        users.add(user);
                                    }
                                    userAdapter.notifyDataSetChanged();
                                }
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
                        Log.e(TAG, "MonitorKurir Error: " + error.getMessage());
                        Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_LONG).show();
                        progressBar.setVisibility(View.GONE);
                    }
                }) {

                    @Override
                    protected Map<String, String> getParams() {
                        // Posting parameters to  url
                        Map<String, String> params = new HashMap<String, String>();
                        params.put("form-type", "json");

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
