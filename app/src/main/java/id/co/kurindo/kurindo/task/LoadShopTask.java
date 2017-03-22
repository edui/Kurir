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
import id.co.kurindo.kurindo.model.News;
import id.co.kurindo.kurindo.model.Shop;

/**
 * Created by DwiM on 11/14/2016.
 */
public class LoadShopTask extends ListenableAsyncTask<Object, Void, List>{
    private static final String TAG = "LoadShopTask";
    private List<Shop> dataList;
    private Context context;

    public LoadShopTask(Context context){
        this.context = context;
        this.dataList = new ArrayList<>();
    }
    @Override
    protected List doInBackground(Object... params) {
        String param = params[0].toString();
        String URI = AppConfig.URL_SHOP_LIST;
        URI = URI.replace("{page}", param);
        StringRequest cityReq = new StringRequest(Request.Method.GET,
                URI, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "LoadShopTask Response: " + response.toString());

                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean success = jObj.getBoolean("success");
                    if (success) {
                        JSONArray datas = jObj.getJSONArray("data");
                        for (int j = 0; j < datas.length(); j++) {
                            JSONObject obj = datas.getJSONObject(j);
                            int id = obj.getInt("id");
                            String code = obj.getString("code");
                            String name = obj.getString("name");
                            String link = obj.getString("link");
                            String banner = obj.getString("banner");
                            String backdrop = obj.getString("backdrop");
                            String status= obj.getString("status");
                            String motto = obj.getString("motto");

                            String phone = obj.getString("phone");
                            String alamat= obj.getString("alamat");
                            String city= obj.getString("city");
                            String cityText= obj.getString("cityText");
                            //Shop shop = new Shop(id, code, name, motto, banner, backdrop, phone, alamat, status, city, cityText);
                            Shop shop = new Shop(id, code, name, motto, banner, backdrop, status);
                            dataList.add(shop);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                onPostExecute(dataList);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "LoadShopTask Error: " + error.getMessage());
                Toast.makeText(context,error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(cityReq);
        return dataList;
    }
}
