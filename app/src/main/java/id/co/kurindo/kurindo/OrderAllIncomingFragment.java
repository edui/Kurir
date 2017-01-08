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
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import butterknife.Bind;
import butterknife.ButterKnife;
import id.co.kurindo.kurindo.app.AppConfig;
import id.co.kurindo.kurindo.app.AppController;
import id.co.kurindo.kurindo.model.Address;
import id.co.kurindo.kurindo.model.CartItem;
import id.co.kurindo.kurindo.model.City;
import id.co.kurindo.kurindo.model.Order;
import id.co.kurindo.kurindo.model.Packet;
import id.co.kurindo.kurindo.model.Product;
import id.co.kurindo.kurindo.model.Recipient;
import id.co.kurindo.kurindo.model.User;

/**
 * Created by DwiM on 11/9/2016.
 */

public class OrderAllIncomingFragment extends BaseOrderMonitoringFragment implements CompoundButton.OnCheckedChangeListener {
    private static final String TAG = "OrderAllIncomingFragment";

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
    int doSendCount = 0;
    int doJekCount = 0;
    int doWashCount = 0;
    int doServiceCount = 0;
    int doHijamahCount = 0;
    int doCarCount = 0;
    int doMoveCount = 0;
    int doShopCount = 0;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View x = super.onCreateView(inflater, container,savedInstanceState);
        textView.setText("Order Baru...");

        //doSendChk.setOnCheckedChangeListener(this);
        return x;
    }

    public void check_order(){
        check_order("");
    }

    public void check_order(final String... params) {
        doSendCount = doJekCount = doServiceCount = doHijamahCount = doCarCount = doMoveCount = 0;
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {

                String param = params[0].toString();
                String URI = AppConfig.URL_ORDER_REALTIME;
                //URI = URI.replace("/{filter}", "/"+param);

                progressBar.setVisibility(View.VISIBLE);
                refreshBtn.setVisibility(View.GONE);

                String tag_string_req = "req_monitor_order_all";
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
                                if(bundle != null){
                                    doSendCount = bundle.getInt(AppConfig.KEY_DOSEND);
                                    doJekCount = bundle.getInt(AppConfig.KEY_DOJEK);
                                    doWashCount = bundle.getInt(AppConfig.KEY_DOWASH);
                                    doServiceCount = bundle.getInt(AppConfig.KEY_DOSERVICE);
                                    doHijamahCount = bundle.getInt(AppConfig.KEY_DOHIJAMAH);
                                    doCarCount = bundle.getInt(AppConfig.KEY_DOCAR);
                                    doMoveCount = bundle.getInt(AppConfig.KEY_DOMOVE);
                                    doShopCount = bundle.getInt(AppConfig.KEY_DOSHOP);
                                }
                                doSendChk.setText(doSendCount > 0 ? ""+doSendCount : "");
                                doJekChk.setText(doJekCount > 0 ? ""+doJekCount:"");
                                doWashChk.setText(doWashCount > 0 ? ""+doWashCount:"");
                                doServiceChk.setText(doServiceCount > 0 ? ""+doServiceCount:"");
                                doHijamahChk.setText(doHijamahCount > 0 ? ""+doHijamahCount:"");
                                doCarChk.setText(doCarCount > 0 ? ""+doCarCount:"");
                                doMoveChk.setText(doMoveCount > 0 ? ""+doMoveCount:"");
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
