package id.co.kurindo.kurindo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

import id.co.kurindo.kurindo.adapter.ShopAdapter;
import id.co.kurindo.kurindo.app.AppConfig;
import id.co.kurindo.kurindo.app.AppController;
import id.co.kurindo.kurindo.model.Shop;
import id.co.kurindo.kurindo.task.ListenableAsyncTask;
import id.co.kurindo.kurindo.task.LoadShopTask;
import id.co.kurindo.kurindo.util.DummyContent;

/**
 * Created by DwiM on 11/9/2016.
 */
public class DirectoryFragment extends Fragment {
    private static final String TAG = "DirectoryFragment";

    ShopAdapter mAdapter;
    RecyclerView mRecyclerView;
    ArrayList<Shop> shops = new ArrayList<>();
    private ListenableAsyncTask loadShopTask;
    private ListenableAsyncTask loadNewsTask;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.directory_layout, null);
        //loadNews();
        loadNewsDummy();

        mRecyclerView = (RecyclerView) view.findViewById(R.id.list);
        mRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));
        mRecyclerView.setHasFixedSize(true);


        //mAdapter = new GalleryAdapter(getContext(), data);
        mAdapter = new ShopAdapter(getContext(), shops);
        mRecyclerView.setAdapter(mAdapter);

        mRecyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getContext(),
                new RecyclerItemClickListener.OnItemClickListener() {

                    @Override
                    public void onItemClick(View view, int position) {
                        Shop model = shops.get(position);
                        DummyContent.shop = model;
                        Bundle bundle = new Bundle();
                        //bundle.putSerializable("shop", model);
                        bundle.putParcelable("shop", model);
                        ((MainDrawerActivity)getActivity()).showActivity(ShopActivity.class, bundle);
                    }
                }));

        load_shops("1");
        return  view;
    }

    private void loadNewsDummy() {
        shops.clear();
        shops.addAll(DummyContent.SHOPS);
    }

    private void loadNews() {
        loadShopTask = (ListenableAsyncTask) new LoadShopTask(getContext());
        loadShopTask.listenWith(new ListenableAsyncTask.AsyncTaskListener() {
            @Override
            public void onPostExecute(Object o) {
                List<Shop> shopList = (List<Shop>)o;
                Log.i("loadShopTask","shopList size:"+shopList.size());
                if(shopList != null && shopList.size() > 0){
                    shops.clear();
                    for (int i = 0; i < shopList.size(); i++) {
                        Shop shop = shopList.get(i);
                        shops.add(shop);
                    }
                }
                mAdapter.notifyDataSetChanged();
            }
        });
        //loadShopTask.execute("1");
        load_shops("1");
    }

    private void load_shops(String... params){
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
                        shops.clear();
                        shops.addAll(DummyContent.SHOPS);
                        JSONArray datas = jObj.getJSONArray("data");
                        for (int j = 0; j < datas.length(); j++) {
                            JSONObject obj = datas.getJSONObject(j);
                            int id = obj.getInt("id");
                            String code = obj.getString("code");
                            String name = obj.getString("name");
                            String link = obj.getString("link");
                            String banner = obj.getString("banner");
                            String backdrop = obj.getString("backdrop");
                            //String bannerOnly = obj.getString("bannerOnly");
                            //String backdropOnly = obj.getString("backdropOnly");
                            String phone = obj.getString("phone");
                            String alamat= obj.getString("alamat");
                            String status= obj.getString("status");
                            String motto = obj.getString("description");
                            String city= obj.getString("city");
                            String cityText= obj.getString("cityText");
                            Shop shop = new Shop(id, code, name, motto, banner, backdrop, phone, alamat, status, city, cityText);
                            shops.add(shop);
                        }
                        mAdapter.notifyDataSetChanged();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "LoadShopTask Error: " + error.getMessage());
                Toast.makeText(getContext(),error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(cityReq);
    }
}
