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
import id.co.kurindo.kurindo.model.Address;
import id.co.kurindo.kurindo.model.City;
import id.co.kurindo.kurindo.model.TUser;
import id.co.kurindo.kurindo.model.User;
import id.co.kurindo.kurindo.util.LogUtil;
import id.co.kurindo.kurindo.util.ParserUtil;

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

                final String param = params[0].toString();
                progressBar.setVisibility(View.VISIBLE);

                String URI = AppConfig.URL_LIST_KURIR_LOCATIONBASED;
                //URI = URI.replace("/{type}", "/"+param);

                final String tag_string_req = "req_monitor_kurir_old";
                HashMap<String, String > maps = new HashMap<>();
                maps.put("form-type", "json");
                maps.put("type", param);
                addRequest(tag_string_req , Request.Method.POST, URI, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        LogUtil.logD(TAG, "MonitorKurir > Check: Response:" + response.toString());
                        //hideDialog();

                        try {
                            JSONObject jObj = new JSONObject(response);
                            boolean error = jObj.getBoolean("error");

                            // Check for error node in json
                            if (!error) {

                                JSONArray datas = jObj.getJSONArray("data");
                                if(datas != null && datas.length() > 0){
                                    users.clear();
                                    ParserUtil parser = new ParserUtil();
                                    for (int j = 0; j < datas.length(); j++) {
                                        TUser recipient = parser.parserUser(datas.getJSONObject(j));
                                        /*
                                        TUser recipient = gson.fromJson(datas.getString(j), TUser.class);
                                        Address addr= gson.fromJson(datas.getString(j), Address.class);
                                        recipient.setAddress(addr);
                                        City city = gson.fromJson(datas.getString(j),City.class);
                                        addr.setCity(city);
                                        */
                                        users.add(recipient);
                                    }

                                    /*for (int i = 0; i < datas.length(); i++) {
                                        JSONObject data = datas.optJSONObject(i);

                                        User user = gson.fromJson(data.toString(), User.class);
                                        users.add(user);
                                    }*/
                                    userAdapter.notifyDataSetChanged();
                                }
                            } else {
                                // Error in login. Get the error message
                                String errorMsg = jObj.getString("message");
                                Toast.makeText(getContext(), ""+errorMsg, Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            // JSON error
                            e.printStackTrace();
                            Toast.makeText(getContext(), "Json error " + e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                        progressBar.setVisibility(View.GONE);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        LogUtil.logD(tag_string_req, "Network Error "+volleyError.getMessage());
                    }
                }, maps, getKurindoHeaders());
            }
        });

    }


}
