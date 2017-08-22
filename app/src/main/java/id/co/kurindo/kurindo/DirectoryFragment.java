package id.co.kurindo.kurindo;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import id.co.kurindo.kurindo.adapter.ShopAdapter;
import id.co.kurindo.kurindo.app.AppConfig;
import id.co.kurindo.kurindo.app.AppController;
import id.co.kurindo.kurindo.base.BaseActivity;
import id.co.kurindo.kurindo.base.BaseFragment;
import id.co.kurindo.kurindo.base.RecyclerItemClickListener;
import id.co.kurindo.kurindo.helper.ViewHelper;
import id.co.kurindo.kurindo.map.LocationService;
import id.co.kurindo.kurindo.map.PickAnAddressActivity;
import id.co.kurindo.kurindo.model.Address;
import id.co.kurindo.kurindo.model.Shop;
import id.co.kurindo.kurindo.model.TUser;
import id.co.kurindo.kurindo.task.ListenableAsyncTask;
import id.co.kurindo.kurindo.task.LoadShopTask;
import id.co.kurindo.kurindo.util.DummyContent;
import id.co.kurindo.kurindo.util.LogUtil;
import id.co.kurindo.kurindo.util.ParserUtil;

import static android.app.Activity.RESULT_OK;

/**
 * Created by DwiM on 11/9/2016.
 */
public class DirectoryFragment extends BaseFragment {
    private static final String TAG = "DirectoryFragment";
    private static final int PICKUP_LOCATION = 2;

    ShopAdapter mAdapter;
    RecyclerView mRecyclerView;
    ArrayList<Shop> shops = new ArrayList<>();
    private ListenableAsyncTask loadShopTask;
    //private ListenableAsyncTask loadNewsTask;

    TextView tvLocationPopup;
    TextView tvLocation;
    TextView tvChangeLocation;
    int location = R.id.radio_current;

    int counter = 0;
    Context context;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.directory_layout, null);
        //loadNews();
        loadDummyShops();

        mRecyclerView = (RecyclerView) view.findViewById(R.id.list);
        mRecyclerView.setLayoutManager(new GridLayoutManager(context, 3));
        mRecyclerView.setHasFixedSize(true);


        tvLocation = (TextView) view.findViewById(R.id.tvLocation);
        tvChangeLocation = (TextView) view.findViewById(R.id.tvChangeLocation);
        tvChangeLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopupWindow("Ganti Lokasi", R.array.pilih_lokasi_array, R.drawable.kirim_dalam_kotaa);
            }
        });
                //mAdapter = new GalleryAdapter(context, data);
        mAdapter = new ShopAdapter(context, shops);
        mRecyclerView.setAdapter(mAdapter);

        mRecyclerView.addOnItemTouchListener(new RecyclerItemClickListener(context,
                new RecyclerItemClickListener.OnItemClickListener() {

                    @Override
                    public void onItemClick(View view, int position) {
                        Shop model = shops.get(position);
                        DummyContent.shop = model;
                        Bundle bundle = new Bundle();
                        //bundle.putSerializable("shop", model);
                        //bundle.putParcelable("shop", model);
                        ViewHelper.getInstance().setShop(model);
                        ((BaseActivity)getActivity()).showActivity(ShopActivity.class, bundle);
                    }
                }));

        //load_shops("1");
        //load_shops_location();
        return  view;
    }

    @Override
    public void onResume() {
        super.onResume();
        //if(counter % 9 == 1) load_shops_location();
        counter++;
        context.registerReceiver(receiver, new IntentFilter(LocationService.LOCATION_CHANGED));
    }

    @Override
    public void onPause() {
        super.onPause();
        context.unregisterReceiver(receiver);
    }
    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle = intent.getExtras();
            if (bundle != null && location == R.id.radio_current) {
                if(counter % 9 == 1) {
                    load_shops_location();
                    counter++;
                }
            }
        }
    };
    private void loadDummyShops() {
        shops.clear();
        shops.addAll(DummyContent.SHOPS);
    }

    private void loadNews() {
        loadShopTask = (ListenableAsyncTask) new LoadShopTask(context);
        loadShopTask.listenWith(new ListenableAsyncTask.AsyncTaskListener() {
            @Override
            public void onPostExecute(Object o) {
                List<Shop> shopList = (List<Shop>)o;
                LogUtil.logI("loadShopTask","shopList size:"+shopList.size());
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

    public void load_shops_location(){
        String URI = AppConfig.URL_SHOP_LOCATIONBASED_LIST;
        Map<String, String> params = new HashMap<String, String>();

        TUser user = db.getUserAddressByType(AppConfig.HOMEBASE);
        Address address = user.getAddress();
        if(location == R.id.radio_current) address= ViewHelper.getInstance().getLastAddress();
        if(location == R.id.radio_others) {
            TUser vuser = ViewHelper.getInstance().getTUser();
            if(vuser != null) {
                address = vuser.getAddress();
            }
        }
        if(address != null){
            params.put("city", address.getKabupaten());
            //params.put("kec", address.getKecamatan());
            //params.put("kab", address.getKabupaten());
            //params.put("prop", address.getPropinsi());
            tvLocation.setText("Area : "+address.toStringKecKab());
        }else{
            String defaultCity = "Kota Balikpapan";
            params.put("city", defaultCity);
            tvLocation.setText("Area: "+defaultCity);
        }
        addRequest("request_locationbased_shop", Request.Method.POST, URI, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                LogUtil.logD(TAG, "request_locationbased_shop Response: " + response.toString());
                try {
                    JSONObject jObj = new JSONObject(response);
                    String message = jObj.getString("message");
                    boolean ok = "OK".equalsIgnoreCase(message);
                    if (ok) {
                        //shops.clear();
                        loadDummyShops();
                        ParserUtil parser= new ParserUtil();

                        JSONArray datas = jObj.getJSONArray("data");
                        for (int j = 0; j < datas.length(); j++) {
                            JSONObject obj = datas.getJSONObject(j);
                            /*
                            int id = obj.getInt("id");
                            String code = obj.getString("code");
                            String name = obj.getString("name");
                            String link = obj.getString("link");
                            String banner = obj.getString("banner");
                            String backdrop = obj.getString("backdrop");
                            //String bannerOnly = obj.getString("bannerOnly");
                            //String backdropOnly = obj.getString("backdropOnly");
                            String status= obj.getString("status");
                            String motto = obj.getString("description");
                            String phone = obj.getString("phone");
                            String alamat= obj.getString("alamat");
                            String city= obj.getString("city");
                            String cityText= obj.getString("cityText");
                            Shop shop = new Shop(id, code, name, motto, banner, backdrop, status);
                            */
                            Shop shop = parser.parserTShop(obj);
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
            public void onErrorResponse(VolleyError volleyError) {
                volleyError.printStackTrace();
                LogUtil.logE(TAG, "request_locationbased_shop Error: " + volleyError.getMessage());
                //Toast.makeText(getActivity(),volleyError.getMessage(), Toast.LENGTH_LONG).show();
            }
        }, params, getKurindoHeaders());

    }

    private void load_shops(String... params){
        String param = params[0].toString();
        String URI = AppConfig.URL_SHOP_LIST;
        URI = URI.replace("{page}", param);
        StringRequest cityReq = new StringRequest(Request.Method.GET,
                URI, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                LogUtil.logD(TAG, "LoadShopTask Response: " + response.toString());

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
                            String status= obj.getString("status");
                            String motto = obj.getString("description");

                            String phone = obj.getString("phone");
                            String alamat= obj.getString("alamat");
                            String city= obj.getString("city");
                            String cityText= obj.getString("cityText");
                            Shop shop = new Shop(id, code, name, motto, banner, backdrop, status);
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
                LogUtil.logE(TAG, "LoadShopTask Error: " + error.getMessage());
                Toast.makeText(getActivity(),""+error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(cityReq);
    }


    Dialog dialog = null;
    protected void showPopupWindow(String title, int arrayResourceId, int imageResourceId) {

        // Create custom dialog object
        dialog = new Dialog(getActivity());
        // Include dialog.xml file
        dialog.setContentView(R.layout.popup_location);
        // Set dialog title
        dialog.setTitle("Popup Dialog");

        //ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(context, arrayResourceId, android.R.layout.simple_spinner_item);
        //adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        tvLocationPopup = (TextView) dialog.findViewById(R.id.tvLocationPopup);
        tvLocationPopup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(location == R.id.radio_others){
                    Intent intent = new Intent(context, PickAnAddressActivity.class);
                    intent.putExtra("type", AppConfig.PICKUP_LOCATION);
                    intent.putExtra("id", ""+1);
                    startActivityForResult(intent, AppConfig.PICKUP_LOCATION);
                }
            }
        });
        RadioGroup rgService = (RadioGroup) dialog.findViewById(R.id.radio_group_service);
        rgService.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                Address address = null;
                switch (checkedId){
                    case R.id.radio_current:
                        location = R.id.radio_current;
                        address= ViewHelper.getInstance().getLastAddress();
                        break;
                    case R.id.radio_homebase:
                        TUser user = db.getUserAddressByType(AppConfig.HOMEBASE);
                        address = user.getAddress();
                        location = R.id.radio_homebase;
                        break;
                    case R.id.radio_others:
                        location = R.id.radio_others;
                        tvLocationPopup.setText("Klik untuk Set Area");
                        tvLocationPopup.setBackgroundColor(Color.GREEN);
                        break;
                }
                if(address != null){
                    tvLocationPopup.setText("Area : "+address.toStringKecKab());
                    tvLocationPopup.setBackgroundColor(Color.CYAN);
                }
            }
        });
        rgService.clearCheck();
        rgService.check(location);

        // set values for custom dialog components - text, image and button
        ImageView image = (ImageView) dialog.findViewById(R.id.imageDialog);
        if(imageResourceId == 0) imageResourceId  = R.drawable.icon_syarat_ketentuan;
        image.setImageResource(imageResourceId);
        TextView textTitleDialog = (TextView) dialog.findViewById(R.id.textTitleDialog);
        if(title != null) textTitleDialog.setText(title);

        dialog.show();

        Button btnChoose = (Button) dialog.findViewById(R.id.btnChoose);
        btnChoose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                load_shops_location();
                // Close dialog
                dialog.dismiss();
            }
        });
        ImageButton declineButton = (ImageButton) dialog.findViewById(R.id.btncancelcat);
        // if decline button is clicked, close the custom dialog
        declineButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                load_shops_location();
                // Close dialog
                dialog.dismiss();
            }
        });

    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (requestCode == AppConfig.PICKUP_LOCATION) {
            if (resultCode == RESULT_OK) {
                TUser origin = ViewHelper.getInstance().getTUser();
                if (origin != null && origin.getAddress() != null) {
                    tvLocationPopup.setText("Area : "+origin.getAddress().toStringKecKab());
                    tvLocationPopup.setBackgroundColor(Color.CYAN);
                    tvLocationPopup.invalidate();
                }
            }
        }
    }
}
