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
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import id.co.kurindo.kurindo.app.AppConfig;
import id.co.kurindo.kurindo.app.AppController;
import id.co.kurindo.kurindo.helper.SQLiteHandler;
import id.co.kurindo.kurindo.model.News;
import id.co.kurindo.kurindo.task.ListenableAsyncTask;
import id.co.kurindo.kurindo.task.LoadNewsTask;

/**
 * Created by DwiM on 11/16/2016.
 */

public class SplashScreenActivity extends AppCompatActivity {
    private static final String TAG = "SplashScreenActivity";

    boolean done = false;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                //Intent intent = new Intent(getApplicationContext(), MainDrawerActivity.class);
                startActivity(intent);
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

    private void update_token() {
        if(AppConfig.FCM_TOKEN != null){
            String tag_string_req = "req_sendRegistrationToServer";

            final SQLiteHandler db = new SQLiteHandler(getApplicationContext());

            StringRequest strReq = new StringRequest(Request.Method.POST,
                    AppConfig.URL_REGISTER_FCM, new Response.Listener<String>() {

                @Override
                public void onResponse(String response) {
                    Log.d(TAG, "update_token Response: " + response.toString());

                    try {
                        JSONObject jObj = new JSONObject(response);

                        String message= jObj.getString("message");
                        Log.i(TAG, "message : "+ message);
                        AppConfig.FCM_TOKEN = null;

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
            }) {

                @Override
                protected Map<String, String> getParams() {
                    // Posting parameters to login url
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("form-token", AppConfig.FCM_TOKEN);

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
    }

}
