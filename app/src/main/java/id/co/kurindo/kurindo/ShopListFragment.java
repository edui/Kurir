package id.co.kurindo.kurindo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.stepstone.stepper.VerificationError;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.OnClick;
import id.co.kurindo.kurindo.adapter.ShopAdapter;
import id.co.kurindo.kurindo.adapter.ShopManageAdapter;
import id.co.kurindo.kurindo.app.AppConfig;
import id.co.kurindo.kurindo.app.AppController;
import id.co.kurindo.kurindo.base.BaseActivity;
import id.co.kurindo.kurindo.base.BaseFragment;
import id.co.kurindo.kurindo.base.RecyclerItemClickListener;
import id.co.kurindo.kurindo.helper.ShopAdmHelper;
import id.co.kurindo.kurindo.helper.ViewHelper;
import id.co.kurindo.kurindo.model.Shop;
import id.co.kurindo.kurindo.util.DummyContent;
import id.co.kurindo.kurindo.util.LogUtil;
import id.co.kurindo.kurindo.util.ParserUtil;
import id.co.kurindo.kurindo.wizard.shopadm.AddShopActivity;

/**
 * Created by dwim on 3/12/2017.
 */

public class ShopListFragment extends BaseFragment {
    private static final String TAG = "ShopListFragment";
    private static final int REQUEST_ADD_SHOP = 1;

    ShopManageAdapter mAdapter;
    ArrayList<Shop> shops = new ArrayList<>();
    @Bind(R.id.list)
    RecyclerView mRecyclerView;
    @Bind(R.id.btnAddShop) AppCompatButton btnAddShop;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflateAndBind(inflater, container, R.layout.shoplist_layout);

        //if admin loadNewsDummy();
        shops.addAll(DummyContent.SHOPS);

        mRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));
        mRecyclerView.setHasFixedSize(true);

        mAdapter = new ShopManageAdapter(getContext(), shops, new ShopManageAdapter.OnItemClickListener() {
            @Override
            public void onViewButtonClick(View view, int position) {
                Shop model = shops.get(position);
                DummyContent.shop = model;
                Bundle bundle = new Bundle();
                bundle.putBoolean("viewCity", true);
                //bundle.putParcelable("shop", model);
                ViewHelper.getInstance().setShop(model);
                ((BaseActivity)getActivity()).showActivity(ShopActivity.class, bundle);
            }

            @Override
            public void onEditButtonClick(View view, int position) {
                Bundle bundle = new Bundle();
                bundle.putBoolean("editMode", true);
                Intent intent = new Intent(getContext(), AddShopActivity.class);
                startActivityForResult(intent, REQUEST_ADD_SHOP, bundle);
            }

            @Override
            public void onDeleteButtonClick(View view, int position) {

            }

            @Override
            public void onActivateButtonClick(View view, int position) {

            }
        });
        mRecyclerView.setAdapter(mAdapter);
    /*
        mRecyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getContext(),
                new RecyclerItemClickListener.OnItemClickListener() {

                    @Override
                    public void onItemClick(View view, int position) {
                        Shop model = shops.get(position);
                        DummyContent.shop = model;
                        Bundle bundle = new Bundle();
                        bundle.putBoolean("viewCity", true);
                        //bundle.putParcelable("shop", model);
                        ViewHelper.getInstance().setShop(model);
                        ((BaseActivity)getActivity()).showActivity(ShopActivity.class, bundle);
                    }
                }));
    */
        load_shops();
        return  view;
    }

    @OnClick(R.id.btnAddShop)
    public void btnAddShop_onClick(){
        Intent intent = new Intent(getContext(), AddShopActivity.class);
        startActivityForResult(intent, REQUEST_ADD_SHOP);
    }
    private void load_shops(){
        String URI = AppConfig.URL_MY_SHOP_LIST;
        Map<String, String> params = new HashMap<String, String>();

        addRequest("request_my_shop", Request.Method.POST, URI, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                LogUtil.logD(TAG, "request_my_shop Response: " + response.toString());
                try {
                    JSONObject jObj = new JSONObject(response);
                    String message = jObj.getString("message");
                    boolean ok = "OK".equalsIgnoreCase(message);
                    if (ok) {
                        shops.clear();
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
                LogUtil.logE(TAG, "LoadShopTask Error: " + volleyError.getMessage());
                Toast.makeText(getActivity(),volleyError.getMessage(), Toast.LENGTH_LONG).show();
            }
        }, params, getKurindoHeaders());

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_ADD_SHOP) {
            if (resultCode == getActivity().RESULT_OK) {
                if(ShopAdmHelper.getInstance().getShop()!= null){
                    shops.add(ShopAdmHelper.getInstance().getShop());
                    mAdapter.notifyDataSetChanged();
                }
                ShopAdmHelper.getInstance().setShop(null);
            }
        }
    }
}
