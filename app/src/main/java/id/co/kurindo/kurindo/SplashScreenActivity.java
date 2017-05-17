package id.co.kurindo.kurindo;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import id.co.kurindo.kurindo.app.AppConfig;
import id.co.kurindo.kurindo.app.AppController;
import id.co.kurindo.kurindo.helper.SQLiteHandler;
import id.co.kurindo.kurindo.helper.SessionManager;
import id.co.kurindo.kurindo.map.LocationService;
import id.co.kurindo.kurindo.model.News;
import id.co.kurindo.kurindo.task.ListenableAsyncTask;
import id.co.kurindo.kurindo.task.LoadNewsTask;
import id.co.kurindo.kurindo.util.LogUtil;
import id.co.kurindo.kurindo.wizard.help.start.WelcomeActivity;

/**
 * Created by DwiM on 11/16/2016.
 */

public class SplashScreenActivity extends AppCompatActivity {
    private static final String TAG = "SplashScreenActivity";
    private SessionManager session;

    boolean done = false;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        session = new SessionManager(this);
        Intent locationService = new Intent(getApplicationContext(), LocationService.class);
        getApplicationContext().startService(locationService);

        Thread timer = new Thread() {
            public void run(){
                while(!done){
                    try {
                        sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                update_token();
                if(session.isLoggedIn()){
                    Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                    startActivity(intent);
                }else{
                    Intent intent = new Intent(getApplicationContext(), WelcomeActivity.class);
                    startActivity(intent);
                }
                finish();
            }
        };
        ListenableAsyncTask loadNewsTask = (ListenableAsyncTask) new LoadNewsTask(this);
        loadNewsTask.listenWith(new ListenableAsyncTask.AsyncTaskListener() {
            @Override
            public void onPostExecute(Object o) {
                List<News> newsList = (List<News>)o;
                Log.i("loadNewsTask","newsList size:"+newsList.size());
                if(newsList != null && newsList.size() > 0){
                    AppController.getInstance().banners = newsList;
                }
                done = true;
            }
        });
        timer.start();
        loadNewsTask.execute("promo");
    }
    /*
    private BroadcastReceiver receiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle = intent.getExtras();
            if (bundle != null) {

            }
        }
    };
    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(receiver, new IntentFilter(LocationService.LOCATION_CHANGED));
    }
    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(receiver);
    }*/

    private void update_token() {
        if(AppConfig.FCM_TOKEN != null){
            String tag_string_req = "req_sendRegistrationToServer";

            final SQLiteHandler db = new SQLiteHandler(getApplicationContext());
            String api = db.getUserApi();

            Map<String, String> headers= new HashMap<String, String>();
            headers.put("Api", api);
            headers.put("Authorization", api);

            Map<String, String> params = new HashMap<String, String>();
            params.put("form-token", AppConfig.FCM_TOKEN);
            addRequest(tag_string_req, Request.Method.POST, AppConfig.URL_REGISTER_FCM, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    LogUtil.logD(TAG, "update_token Response: " + response.toString());

                    try {
                        JSONObject jObj = new JSONObject(response);

                        String message= jObj.getString("message");
                        Log.i(TAG, "message : "+ message);
                        if(message.equalsIgnoreCase("OK")){
                            AppConfig.FCM_TOKEN = null;
                        }

                    } catch (JSONException e) {
                        // JSON error
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e(TAG, "update_token Error: " + error.getMessage());
                }
            }, params, headers);

        }
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
