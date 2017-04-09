package id.co.kurindo.kurindo.task;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import id.co.kurindo.kurindo.app.AppConfig;
import id.co.kurindo.kurindo.app.AppController;
import id.co.kurindo.kurindo.helper.SQLiteHandler;
import id.co.kurindo.kurindo.helper.ViewHelper;
import id.co.kurindo.kurindo.map.DataParser;
import id.co.kurindo.kurindo.map.MapUtils;
import id.co.kurindo.kurindo.model.Address;
import id.co.kurindo.kurindo.model.News;
import id.co.kurindo.kurindo.model.TUser;

/**
 * Created by DwiM on 11/14/2016.
 */
public class RequestAddressTask extends ListenableAsyncTask<Object, Void, List>{
    private static final String TAG = "RequestAddressTask";
    private Context context;
    private List<String> list;
    SQLiteHandler db;

    public RequestAddressTask(Context context){
        this.context = context;
        this.db = new SQLiteHandler(context);

    }

    @Override
    protected List doInBackground(Object... params) {
        String param = params[0].toString();
        requestAddress(param);
        return null;
    }

    private void logger() {
        Map<String, String> headers= new HashMap<String, String>();
        headers.put("Api",  db.getUserApi());
        HashMap<String, String > params = new HashMap<>();
        params.put("form-user", db.getUserPhone());
        params.put("form-type", "APPS");
        params.put("form-tag", "KURINDO");
        params.put("form-activity", "Starting MobileApps");
        Address addr = ViewHelper.getInstance().getLastAddress();
        params.put("form-lat", (addr == null || addr.getLocation()==null? "0" : ""+addr.getLocation().latitude) );
        params.put("form-lng", (addr == null || addr.getLocation() == null ? "0" : ""+addr.getLocation().longitude));
        addRequest("req_logger", Request.Method.POST, AppConfig.URL_LOGGING, new Response.Listener() {
            @Override
            public void onResponse(Object o) {
                Log.d("req_logger","response: "+o.toString());
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                volleyError.printStackTrace();
            }
        }, params, headers);
    }

    private void requestAddress(String url) {
        addRequest("request_geocode_address", Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //Log.d(TAG, "requestAddress Response: " + response.toString());
                List<List<HashMap<String, String>>> routes = null;
                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean OK = "OK".equalsIgnoreCase(jObj.getString("status"));
                    if (OK) {
                        DataParser parser = new DataParser();
                        Address address = parser.parseAddress(jObj);
                        ViewHelper.getInstance().setLastAddress(address);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (ViewHelper.getInstance().getLastAddress() == null) {
                    TUser u = db.getUser();
                    if (u != null) {
                        ViewHelper.getInstance().setLastAddress(u.getAddress());
                    }
                }
                logger();
                onPostExecute(list);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                volleyError.printStackTrace();
                onPostExecute(list);
            }
        }, null, null);
        onPostExecute(list);
    }

    public void addRequest(final String tag_string_req, int method, String url, Response.Listener responseListener, Response.ErrorListener errorListener, final Map<String, String> params, final Map<String, String> headers){
            final StringRequest strReq = new StringRequest(method,url, responseListener, errorListener){
                protected Map<String, String> getParams() throws AuthFailureError {
                    if(params == null) return super.getParams();
                    return params;
                }
                public Map<String, String> getHeaders() throws AuthFailureError{
                    if(headers == null) return super.getHeaders();
                    return headers;
                }
            };
            AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }
 }
