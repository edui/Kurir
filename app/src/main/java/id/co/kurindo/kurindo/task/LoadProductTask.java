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
import id.co.kurindo.kurindo.model.Product;
import id.co.kurindo.kurindo.model.Shop;

/**
 * Created by DwiM on 11/14/2016.
 */
public class LoadProductTask extends ListenableAsyncTask<Object, Void, List>{
    private static final String TAG = "LoadProductTask";
    private List<Product> dataList;
    private Context context;

    public LoadProductTask(Context context){
        this.context = context;
        this.dataList = new ArrayList<>();
    }
    @Override
    protected List doInBackground(Object... params) {
        final String param = params[0].toString();
        final String param2 = params[1].toString();
        String URI = AppConfig.URL_SHOP_PRODUCTS;
        URI = URI.replace("{shop_id}", param);
        URI = URI.replace("{page}", param2);
        StringRequest cityReq = new StringRequest(Request.Method.GET,
                URI, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "LoadProductTask Response: "+param+" , "+param2+" = " + response.toString());

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
                            String description = obj.getString("description");
                            String image = obj.getString("image");
                            String price= obj.getString("price");
                            String stock = obj.getString("stock");
                            String discount= obj.getString("discount");
                            String status= obj.getString("status");
                            String created = obj.getString("created_date");
                            Product product= new Product (id, code, name, description, image, price, stock, discount, status,created);
                            dataList.add(product);
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
