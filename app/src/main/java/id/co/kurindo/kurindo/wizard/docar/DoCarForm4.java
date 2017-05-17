package id.co.kurindo.kurindo.wizard.docar;

/**
 * Created by aspire on 3/26/2017.
 */

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.stepstone.stepper.Step;
import com.stepstone.stepper.VerificationError;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import id.co.kurindo.kurindo.R;
import id.co.kurindo.kurindo.adapter.ItemAdapter;
import id.co.kurindo.kurindo.adapter.VehicleItemAdapter;
import id.co.kurindo.kurindo.app.AppConfig;
import id.co.kurindo.kurindo.base.BaseActivity;
import id.co.kurindo.kurindo.helper.DoCarHelper;
import id.co.kurindo.kurindo.helper.ViewHelper;
import id.co.kurindo.kurindo.map.DataParser;
import id.co.kurindo.kurindo.map.LocationMapViewsActivity;
import id.co.kurindo.kurindo.model.Address;
import id.co.kurindo.kurindo.model.DoCarRental;
import id.co.kurindo.kurindo.model.Vehicle;
import id.co.kurindo.kurindo.util.LogUtil;
import id.co.kurindo.kurindo.util.ParserUtil;
import id.co.kurindo.kurindo.wizard.AbstractStepperActivity;
import id.co.kurindo.kurindo.wizard.BaseStepFragment;

/**
 * Created by dwim on 3/15/2017.
 */

public class DoCarForm4 extends BaseStepFragment implements Step {
    private static final String TAG = "DoCarForm4";
    VerificationError invalid = null;
    Context context;
    ProgressDialog progressDialog;

    @Bind(R.id.list)
    RecyclerView list;

    VehicleItemAdapter adapter;

    List<Vehicle> datas = new ArrayList<>();
    boolean next = false;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflateAndBind(inflater, container, R.layout.fragment_docar4);
        context = getContext();
        progressDialog = new ProgressDialog(context);

        list.setLayoutManager(new GridLayoutManager(context, 1));
        list.setHasFixedSize(true);
        datas = AppConfig.getDoCarRentalServices();
        adapter = new VehicleItemAdapter(context, datas, new VehicleItemAdapter.OnItemClickListener() {
            @Override
            public void onButtonViewPesanClick(View view, int position) {
                DoCarHelper.getInstance().getRental().setVehicle(datas.get(position));
                //((BaseActivity) getActivity()).showActivity(DoCarRentalActivity.class);
                AbstractStepperActivity activity= (AbstractStepperActivity) getActivity();
                activity.mStepperLayout.onTabClicked(activity.mStepperLayout.getCurrentStepPosition()+1);
            }
        });
        list.setAdapter(adapter);
        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        //adapter.notifyDataSetChanged();
    }

    private void retrieve_cars() {
        DoCarRental rental = DoCarHelper.getInstance().getRental();
        if(rental != null){
            String city = rental.getCity();
            if(city == null){
                Address addr = ViewHelper.getInstance().getLastAddress();
                if(addr != null){
                    city = addr.getKabupaten();
                }
            }
            if(city != null){
                Map<String, String> params = new HashMap<String, String>();
                params.put("city", city);
                params.put("type", AppConfig.KEY_DOCAR);

                String url = AppConfig.URL_RETRIEVE_CAR_VEHICLES;
                String tag_string_req = "retrieve_cars";
                addRequest(tag_string_req, Request.Method.POST , url, new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        LogUtil.logD(TAG, "retrieve_cars : " + response.toString());

                        try {
                            JSONObject jObj = new JSONObject(response);
                            String message = jObj.getString("message");
                            int status = jObj.getInt("status");
                            if (status == 200) {
                                JSONArray data = jObj.getJSONArray("data");
                                if(data != null){
                                    datas.clear();
                                    ParserUtil parser = new ParserUtil();
                                    for (int i = 0; i < data.length(); i++) {
                                        Vehicle v = parser.parseVehicle(data.getJSONObject(i));
                                        datas.add(v);
                                    }
                                    adapter.notifyDataSetChanged();
                                }
                            } else {
                                Toast.makeText(context, ""+message, Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            // JSON error
                            e.printStackTrace();
                            LogUtil.logE(TAG, "onResponse Error: " + e.getMessage());
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        LogUtil.logE(TAG, "logToServer Error: " + error.getMessage());
                    }
                }, params, getKurindoHeaders());
            }
        }
    }

    @Override
    public int getName() {
        return R.string.docar_form;
    }

    @Override
    public VerificationError verifyStep() {

        next = true;
        return null;
    }

    @Override
    public void onSelected() {
        if(!next) retrieve_cars();

    }

    @Override
    public void onError(@NonNull VerificationError error) {

    }
}