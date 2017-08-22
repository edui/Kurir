package id.co.kurindo.kurindo;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
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

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import id.co.kurindo.kurindo.adapter.KurirAdapter;
import id.co.kurindo.kurindo.adapter.TKurirAdapter;
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

public class KurirBaruFragment extends BaseKurirFragment {
    private static final String TAG = "KurirBaruFragment";
    TKurirAdapter userAdapter;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getContext();
        setHasOptionsMenu(true);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = super.onCreateView(inflater,container,savedInstanceState);
        userAdapter = new TKurirAdapter(getContext(), users, new TKurirAdapter.OnItemClickListener() {
            @Override
            public void onApprovedButtonClick(View view, final int position, final HashMap<String, String> fiturMap) {
                final TUser u = users.get(position);
                final Handler handler = new Handler() {
                    @Override
                    public void handleMessage(Message mesg) {
                        throw new RuntimeException();
                    }
                };

                DialogInterface.OnClickListener YesClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        approved_kurir(u, position, handler, fiturMap);
                    }
                };
                DialogInterface.OnClickListener NoClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        handler.handleMessage(null);
                    }
                };

                showConfirmationDialog("Konfirmasi","Anda Yakin akan menjadikan user '"+u.getFirstname()+ " "+u.getLastname()+"' sebagai KURIR  ?", YesClickListener, NoClickListener);

                // loop till a runtime exception is triggered.
                try { Looper.loop(); }
                catch(RuntimeException e2) {}
            }

            @Override
            public void onCallButtonClick(View view, int position) {
                final TUser u = users.get(position);
                String phone = u.getPhone();
                Uri uri = Uri.parse("https://api.whatsapp.com/send?phone="+phone+"&text=Assalamu'alaykum. CALON KURIR "+u.getName());
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }

            @Override
            public void onRemoveButtonClick(View v, final int position) {
                final TUser u = users.get(position);
                final Handler handler = new Handler() {
                    @Override
                    public void handleMessage(Message mesg) {
                        throw new RuntimeException();
                    }
                };

                DialogInterface.OnClickListener YesClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        delete_kurir(u, position, handler);
                    }
                };

                DialogInterface.OnClickListener NoClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        handler.handleMessage(null);
                    }
                };
                showConfirmationDialog("Konfirmasi","Anda Yakin akan menghapus user '"+u.getFirstname()+ " "+u.getLastname()+"' ?", YesClickListener, NoClickListener);

                // loop till a runtime exception is triggered.
                try { Looper.loop(); }
                catch(RuntimeException e2) {}
            }
        });
        daftarKurir.setAdapter(userAdapter);

        check_kurir();

        //textView.setText("Calon Kurir...");

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
                String URI = AppConfig.URL_LIST_NEWKURIR_LOCATIONBASED;
                //URI = URI.replace("/{type}", "/"+param);

                progressBar.setVisibility(View.VISIBLE);
                String tag_string_req = "req_monitor_kurir_new";
                StringRequest strReq = new StringRequest(Request.Method.POST,
                        URI, new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        LogUtil.logD(TAG, "MonitorKurir > Check: Response:" + response.toString());
                        //hideDialog();

                        try {
                            JSONObject jObj = new JSONObject(response);
                            boolean error = jObj.getBoolean("error");

                            // Check for error node in json
                            if (!error) {

                                /*GsonBuilder builder = new GsonBuilder();
                                builder.setPrettyPrinting();
                                Gson gson = builder.create();
                                */

                                JSONArray datas = jObj.getJSONArray("data");
                                if(datas != null && datas.length() > 0){
                                    users.clear();

                                    ParserUtil parser = new ParserUtil();
                                    for (int j = 0; j < datas.length(); j++) {
                                        JSONObject data = datas.optJSONObject(j);
                                        TUser recipient = parser.parserUser(data);
                                        /*
                                        TUser recipient = gson.fromJson(datas.getString(j), TUser.class);
                                        Address addr= gson.fromJson(datas.getString(j), Address.class);
                                        recipient.setAddress(addr);
                                        City city = gson.fromJson(datas.getString(j),City.class);
                                        addr.setCity(city);
                                        */
                                        users.add(recipient);
                                    }
                                    userAdapter.notifyDataSetChanged();
                                }
                            } else {
                                // Error in login. Get the error message
                                String errorMsg = jObj.getString("message");
                                Toast.makeText(context, ""+errorMsg, Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            // JSON error
                            e.printStackTrace();
                            Toast.makeText(context, "Json error " + e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                        progressBar.setVisibility(View.GONE);
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        LogUtil.logE(TAG, "MonitorKurir Error: " + error.getMessage());
                        if(context != null )
                            Toast.makeText(context, "Network Error "+error.getMessage(), Toast.LENGTH_LONG).show();
                        progressBar.setVisibility(View.GONE);
                    }
                }) {

                    @Override
                    protected Map<String, String> getParams() {
                        // Posting parameters to  url
                        Map<String, String> params = new HashMap<String, String>();
                        params.put("form-type", "json");
                        params.put("type", param);

                        params.put("city", city);
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

    private void approved_kurir(final TUser u, final int position, final Handler handler, final HashMap<String, String> fiturMap) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                progressBar.setVisibility(View.VISIBLE);
                String tag_string_req = "req_kurir_new_approval";
                StringRequest strReq = new StringRequest(Request.Method.POST,
                        AppConfig.URL_KURIR_APPROVED, new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        LogUtil.logD(TAG, "KURIRBARU > approval: Response: " + response.toString());
                        //hideDialog();

                        try {
                            JSONObject jObj = new JSONObject(response);
                            boolean error = jObj.getBoolean("error");

                            // Check for error node in json
                            if (!error) {
                                users.remove(position);
                                userAdapter.notifyDataSetChanged();

                                String msg = jObj.getString("message");
                                Toast.makeText(context, ""+msg, Toast.LENGTH_LONG).show();
                                progressBar.setVisibility(View.GONE);
                            } else {
                                //Get the error message
                                String errorMsg = jObj.getString("message");
                                Toast.makeText(context, ""+errorMsg, Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            // JSON error
                            e.printStackTrace();
                            Toast.makeText(context, "Json error " + e.getMessage(), Toast.LENGTH_LONG).show();
                            handler.handleMessage(null);
                            progressBar.setVisibility(View.GONE);
                        }

                        handler.handleMessage(null);
                        progressBar.setVisibility(View.GONE);
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        LogUtil.logE(TAG, "Kurir Approval Error: " + error.getMessage());
                        Toast.makeText(context, "Network Error "+error.getMessage(), Toast.LENGTH_LONG).show();
                        handler.handleMessage(null);
                        progressBar.setVisibility(View.GONE);
                    }
                }) {

                    @Override
                    protected Map<String, String> getParams() {
                        // Posting parameters to  url
                        Map<String, String> params = new HashMap<String, String>();
                        params.put("kurir", u.getPhone());
                        String fiturs = "";
                        Iterator it = fiturMap.entrySet().iterator();
                        int i=0;
                        while (it.hasNext()) {
                            Map.Entry pair = (Map.Entry)it.next();
                            if(i >0)
                                fiturs += ",";
                            fiturs += pair.getKey();
                            i++;
                        }
                        params.put("services", fiturs);

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


    private void delete_kurir(final TUser u, final int position, final Handler handler) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                progressBar.setVisibility(View.VISIBLE);
                String tag_string_req = "req_kurir_new_rejected";
                StringRequest strReq = new StringRequest(Request.Method.POST,
                        AppConfig.URL_KURIR_REJECTED, new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        LogUtil.logD(TAG, "CALON KURIR > updated: Response: " + response.toString());
                        //hideDialog();

                        try {
                            JSONObject jObj = new JSONObject(response);
                            boolean error = jObj.getBoolean("error");

                            // Check for error node in json
                            if (!error) {
                                users.remove(position);
                                userAdapter.notifyDataSetChanged();

                                String msg = jObj.getString("message");
                                Toast.makeText(context, ""+msg, Toast.LENGTH_LONG).show();
                                progressBar.setVisibility(View.GONE);
                            } else {
                                //Get the error message
                                String errorMsg = jObj.getString("message");
                                Toast.makeText(context, ""+errorMsg, Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            // JSON error
                            e.printStackTrace();
                            Toast.makeText(context, "Json error " + e.getMessage(), Toast.LENGTH_LONG).show();
                            //handler.handleMessage(null);
                            //progressBar.setVisibility(View.GONE);
                        }

                        progressBar.setVisibility(View.GONE);
                        handler.handleMessage(null);
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        LogUtil.logE(TAG, "Kurir Rejection Error: " + error.getMessage());
                        Toast.makeText(context, "Network Error "+error.getMessage(), Toast.LENGTH_LONG).show();
                        progressBar.setVisibility(View.GONE);
                        handler.handleMessage(null);
                    }
                }) {

                    @Override
                    protected Map<String, String> getParams() {
                        // Posting parameters to  url
                        Map<String, String> params = new HashMap<String, String>();
                        params.put("kurir", u.getPhone());

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
