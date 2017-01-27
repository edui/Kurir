package id.co.kurindo.kurindo.wizard.dashboard;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.stepstone.stepper.Step;
import com.stepstone.stepper.VerificationError;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import id.co.kurindo.kurindo.R;
import id.co.kurindo.kurindo.RecyclerItemClickListener;
import id.co.kurindo.kurindo.adapter.UserAdapter;
import id.co.kurindo.kurindo.app.AppConfig;
import id.co.kurindo.kurindo.app.AppController;
import id.co.kurindo.kurindo.helper.CheckoutHelper;
import id.co.kurindo.kurindo.helper.OrderHelper;
import id.co.kurindo.kurindo.model.Address;
import id.co.kurindo.kurindo.model.City;
import id.co.kurindo.kurindo.model.Order;
import id.co.kurindo.kurindo.model.User;
import id.co.kurindo.kurindo.wizard.AcceptOrderActivity;
import id.co.kurindo.kurindo.wizard.checkout.BaseStepFragment;

import static android.app.Activity.RESULT_OK;

/**
 * Created by aspire on 12/15/2016.
 */

public class StepAcceptOrderFragment extends BaseStepFragment implements Step {
    private static final String TAG = "StepAcceptOrderFragment";
    @Bind(R.id.tv_formTitle)
    TextView tvformTitle;

    @Bind(R.id.list)
    RecyclerView mRecyclerView;
    UserAdapter mUserAdapter;
    ArrayList<User> data = new ArrayList<>();
    User user;
    Order order;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflateAndBind(inflater, container, R.layout.fragment_accept_order);

        //order = ((AcceptOrderActivity)getActivity()).getOrder();
        order = OrderHelper.getInstance().getOrder();

        tvformTitle.setText("Accept Order --> Pilih Kurir");

        setup_list();
        return v;
    }

    private void setup_list() {
        mRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 1));
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setVisibility(View.VISIBLE);
        mUserAdapter = new UserAdapter(getContext(), data);
        mRecyclerView.setAdapter(mUserAdapter);
        mRecyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getContext(),
                new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        mUserAdapter.selected(position);
                        mUserAdapter.setSelection(position);
                        user = data.get(position);
                    }
                }));
        data.clear();
        request_list_kurir("KURIR");
        request_list_kurir("ADMIN");
    }

    private void request_list_kurir(String...params) {
        final String param = params[0].toString();
        //String param2 = null;
        //if(params.length > 1) param2 = params[1].toString();
        String URI = AppConfig.URL_LIST_KURIR;
        URI = URI.replace("{type}", param);

        StringRequest cityReq = new StringRequest(Request.Method.GET,
                URI, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "list KURIR Response: " + response.toString());
                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean success = jObj.getBoolean("success");
                    if (success) {


                        GsonBuilder builder = new GsonBuilder();
                        builder.setPrettyPrinting();
                        Gson gson = builder.create();

                        JSONArray datas = jObj.getJSONArray("data");
                        for (int j = 0; j < datas.length(); j++) {
                            User recipient = gson.fromJson(datas.getString(j), User.class);
                            Address addr= gson.fromJson(datas.getString(j), Address.class);
                            recipient.setAddress(addr);
                            City city = gson.fromJson(datas.getString(j),City.class);
                            addr.setCity(city);
                            data.add(recipient);
                        }
                        mUserAdapter.notifyDataSetChanged();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "list KURIR Error: " + error.getMessage());
                Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_LONG).show();

            }
        });

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(cityReq);

    }

    @Override
    public int getName() {
        return 0;
    }

    @Override
    public VerificationError verifyStep() {
        if(user == null){
            return new VerificationError("Error: Pilih salah satu kurir.");
        }
        final VerificationError[] verify = {null};

        // make a handler that throws a runtime exception when a message is received
        final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message mesg) {
                throw new RuntimeException();
            }
        };

        DialogInterface.OnClickListener YesClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                verify[0] = place_order(handler);
            }
        };
        showConfirmationDialog("Konfirmasi","Pesanan ini akan di-delegasikan ke Kurir tersebut?", YesClickListener, null);

        // loop till a runtime exception is triggered.
        try { Looper.loop(); }
        catch(RuntimeException e2) {}


        return verify[0];
    }

    private VerificationError place_order(final Handler handler) {
        String tag_string_req = "req_place_order";
        final VerificationError[] verify = {null};

        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_ORDER_ADDPIC, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Process_order Response: " + response.toString());
                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");
                    String message = jObj.getString("message");
                    if (!error) {
                        //success
                        order.setStatus(AppConfig.KEY_KUR200);
                        order.setPic(user);
                        Intent intent = new Intent();
                        //intent.putExtra("order", order);
                        getActivity().setResult(RESULT_OK, intent);
                        verify[0] = null;
                    }else{
                        verify[0] = new VerificationError("Error: "+message);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    verify[0] = new VerificationError("Error"+e.getMessage());
                }
                handler.handleMessage(null);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Process_order Error: " + error.getMessage());
                verify[0] = new VerificationError("Error"+error.getMessage());
                handler.handleMessage(null);
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting params to register url
                HashMap<String, String> params = new HashMap<>();

                params.put("awb",(order ==null? null : order.getAwb()));
                params.put("pic", user.getEmail());
                params.put("user_agent", AppConfig.USER_AGENT);

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

        return verify[0];
    }

    @Override
    public void onSelected() {
    }

    @Override
    public void onError(@NonNull VerificationError error) {

    }
}
