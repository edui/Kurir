package id.co.kurindo.kurindo.wizard.dosend;

import android.app.ProgressDialog;
import android.content.Context;
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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.stepstone.stepper.Step;
import com.stepstone.stepper.VerificationError;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import butterknife.Bind;
import id.co.kurindo.kurindo.R;
import id.co.kurindo.kurindo.adapter.TKurirAdapter;
import id.co.kurindo.kurindo.app.AppConfig;
import id.co.kurindo.kurindo.app.AppController;
import id.co.kurindo.kurindo.base.RecyclerItemClickListener;
import id.co.kurindo.kurindo.comp.ProgressDialogCustom;
import id.co.kurindo.kurindo.helper.ViewHelper;
import id.co.kurindo.kurindo.model.TOrder;
import id.co.kurindo.kurindo.model.TPacket;
import id.co.kurindo.kurindo.model.TUser;
import id.co.kurindo.kurindo.util.LogUtil;
import id.co.kurindo.kurindo.util.ParserUtil;
import id.co.kurindo.kurindo.wizard.BaseStepFragment;

import static android.app.Activity.RESULT_OK;

/**
 * Created by aspire on 12/15/2016.
 */

public class StepAcceptTOrderFragment extends BaseStepFragment implements Step {
    private static final String TAG = "StepAcceptTOrderFragment";
    @Bind(R.id.tv_formTitle)
    TextView tvformTitle;
    @Bind(R.id.layoutReject)
    LinearLayout layoutReject;
    @Bind(R.id.list)
    RecyclerView mRecyclerView;
    TKurirAdapter mUserAdapter;
    ArrayList<TUser> data = new ArrayList<>();
    TUser user;
    TOrder order;

    Context context;
    private ProgressDialog progressBar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getContext();
        progressBar = new ProgressDialogCustom(context);
        progressBar.show();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflateAndBind(inflater, container, R.layout.fragment_accept_order);
        //order = ((AcceptOrderActivity)getActivity()).getOrder();
        order = ViewHelper.getInstance().getOrder();
        layoutReject.setVisibility(View.GONE);
        tvformTitle.setText("Accept Order --> Pilih Kurir");

        setup_list();
        return v;
    }
    private void setup_list() {
        data.clear();

        mRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 1));
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setVisibility(View.VISIBLE);
        mUserAdapter = new TKurirAdapter(getContext(), data, null);
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

        /*final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message mesg) {
                throw new RuntimeException();
            }
        };*/
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                request_list_kurir("KURIR,ADMIN");
            }
        });

        /*try { Looper.loop(); }
        catch(RuntimeException e2) {}
        */

    }

    private void request_list_kurir(String param) {

        String URI = AppConfig.URL_LIST_USER_SKILLLOCATIONBASED;
        //URI = URI.replace("{type}", param);
        final String tag_string_req = "req_list_kurir";
        HashMap<String, String > maps = new HashMap<>();
        maps.put("form-type", "json");
        maps.put("type", param);
        maps.put("location", order.getLocationStr());
        maps.put("do-type", order.getService_type());
        addRequest(tag_string_req , Request.Method.POST, URI, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                LogUtil.logD(TAG, "list KURIR Response: " + response.toString());
                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean success = jObj.getBoolean("success");
                    if (success) {
                        ParserUtil parser = new ParserUtil();
                        JSONArray datas = jObj.getJSONArray("data");
                        for (int j = 0; j < datas.length(); j++) {
                            TUser recipient = parser.parserUser(datas.getJSONObject(j));
                            /*
                            TUser recipient = gson.fromJson(datas.getString(j), TUser.class);
                            Address addr= gson.fromJson(datas.getString(j), Address.class);
                            recipient.setAddress(addr);
                            City city = gson.fromJson(datas.getString(j),City.class);
                            addr.setCity(city);
                            */
                            boolean add = true;
                            if(order.getService_type().equalsIgnoreCase(AppConfig.KEY_DOJEK)){
                                if(recipient.getGender() != null) {
                                    Set<TPacket> packets = order.getPackets();
                                    if(packets != null){
                                        for(TPacket p : packets){
                                            if(p.getOrigin() != null && !recipient.getGender().equalsIgnoreCase(p.getOrigin().getGender())){
                                                add = false;
                                            }
                                        }
                                    }
                                }
                            }

                            if(add){
                                data.add(recipient);
                            }
                        }
                        mUserAdapter.notifyDataSetChanged();

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                progressBar.dismiss();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                LogUtil.logD(tag_string_req, ""+volleyError.getMessage());
                progressBar.dismiss();
            }
        }, maps, getKurindoHeaders());

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
        DialogInterface.OnClickListener NoClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                handler.handleMessage(null);
            }
        };
        showConfirmationDialog("Konfirmasi","Pesanan ini akan di-delegasikan ke Kurir tersebut?", YesClickListener, NoClickListener);

        // loop till a runtime exception is triggered.
        try { Looper.loop(); }
        catch(RuntimeException e2) {}


        return verify[0];
    }

    private VerificationError place_order(final Handler handler) {
        progressBar.show();

        String tag_string_req = "req_place_order";
        final VerificationError[] verify = {null};

        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_TORDER_ADDPIC, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                LogUtil.logD(TAG, "Process_order Response: " + response.toString());
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
                progressBar.dismiss();
                handler.handleMessage(null);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                LogUtil.logE(TAG, "Process_order Error: " + error.getMessage());
                verify[0] = new VerificationError("Error"+error.getMessage());
                progressBar.dismiss();
                handler.handleMessage(null);
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting params to register url
                HashMap<String, String> params = new HashMap<>();

                params.put("awb",(order ==null? null : order.getAwb()));
                params.put("pic", user.getPhone());
                params.put("user_agent", AppConfig.USER_AGENT);

                return params;
            }
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return getKurindoHeaders();
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
