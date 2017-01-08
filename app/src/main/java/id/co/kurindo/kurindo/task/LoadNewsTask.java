package id.co.kurindo.kurindo.task;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import id.co.kurindo.kurindo.app.AppConfig;
import id.co.kurindo.kurindo.app.AppController;
import id.co.kurindo.kurindo.model.City;
import id.co.kurindo.kurindo.model.News;

/**
 * Created by DwiM on 11/14/2016.
 */
public class LoadNewsTask extends ListenableAsyncTask<Object, Void, List>{
    private static final String TAG = "LoadNewsTask";
    private List<News> newsList;
    private Context context;

    public LoadNewsTask(Context context){
        this.context = context;
        this.newsList = new ArrayList<>();
    }
    @Override
    protected List doInBackground(Object... params) {
        String param = params[0].toString();
        StringRequest cityReq = new StringRequest(Request.Method.GET,
                AppConfig.URL_NEWS + "/"+param, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "LoadNewsTask Response: " + response.toString());

                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean success = jObj.getBoolean("success");
                    if (success) {
                        JSONArray datas = jObj.getJSONArray("data");
                        for (int j = 0; j < datas.length(); j++) {
                            JSONObject obj = datas.getJSONObject(j);
                            String title = obj.getString("title");
                            String content = obj.getString("content");
                            String link = obj.getString("link");
                            String img = obj.getString("img");
                            News news = new News(title, content, link, img);
                            newsList.add(news);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                onPostExecute(newsList);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "LoadNewsTask Error: " + error.getMessage());
                Toast.makeText(context,error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(cityReq);
        return newsList;
    }
}
