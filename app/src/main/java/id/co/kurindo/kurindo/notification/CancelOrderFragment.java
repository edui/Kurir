package id.co.kurindo.kurindo.notification;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import butterknife.Bind;
import butterknife.OnClick;
import id.co.kurindo.kurindo.R;
import id.co.kurindo.kurindo.app.AppConfig;
import id.co.kurindo.kurindo.base.BaseFragment;
import id.co.kurindo.kurindo.helper.ViewHelper;
import id.co.kurindo.kurindo.model.DoMart;
import id.co.kurindo.kurindo.model.Notification;
import id.co.kurindo.kurindo.model.TOrder;
import id.co.kurindo.kurindo.model.TPacket;
import id.co.kurindo.kurindo.util.LogUtil;
import id.co.kurindo.kurindo.util.ParserUtil;

import static android.widget.Toast.LENGTH_SHORT;

/**
 * Created by dwim on 8/10/2017.
 */

public class CancelOrderFragment extends BaseFragment {
    private static final String TAG = "CancelOrderFragment";

    @Bind(R.id.radio_group_reason)
    RadioGroup rdgReason;

    @Bind(R.id.input_reason)
    EditText reasonEt;
    @Bind(R.id.inlay_reason)
    TextInputLayout reasonLayout;

    @Bind(R.id.tvOrderDetail)
    TextView tvOrder;

    Notification data;
    Context context;
    private ProgressDialog progressBar;
    String reason;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getContext();
        Bundle bundle = getArguments();
        if(bundle != null){
            data = bundle.getParcelable("data");
        }else{
            TOrder order = ViewHelper.getInstance().getOrder();
            if(order != null){
                fill_data(order);
            }
        }
    }

    private void fill_data(TOrder order) {
        data = new Notification();
        data.setAwb(order.getAwb());
        data.setCod(order.getCod().toString());
        data.setPrice(order.getTotalPrice().toString());
        data.setMessage(order.getStatusText());
        data.setStatus(order.getStatus());
        data.setTag(order.getService_type());
        if(order.getDocar() != null && order.getDocar().getUser() != null ){
            data.setKotaPengirim(order.getDocar().getUser().toStringAddressFormatted());
            data.setKotaPenerima(order.getDocar().getUser().toStringAddressFormatted());
        } else if(order.getService_type().equalsIgnoreCase(AppConfig.KEY_DOSEND)
                || order.getService_type().equalsIgnoreCase(AppConfig.KEY_DOJEK)
                || order.getService_type().equalsIgnoreCase(AppConfig.KEY_DOCAR)
                || order.getService_type().equalsIgnoreCase(AppConfig.KEY_DOMOVE) ){
            if(order.getPackets() != null){
                for (TPacket p : order.getPackets()){
                    data.setKotaPengirim(p.getOrigin().toStringAddressFormatted());
                    data.setKotaPenerima(p.getDestination().toStringAddressFormatted());
                    break;
                }
            }
        } else if(order.getService_type().equalsIgnoreCase(AppConfig.KEY_DOMART) ){
            if(order.getMarts() != null){
                for(DoMart m : order.getMarts()){
                    data.setKotaPengirim(m.getOrigin().toStringAddressFormatted());
                    break;
                }
            }
            data.setKotaPenerima(order.getPlace().toStringAddressFormatted());
        } else if(order.getService_type().equalsIgnoreCase(AppConfig.KEY_DOSERVICE) ||
                order.getService_type().equalsIgnoreCase(AppConfig.KEY_DOWASH)  ||
                order.getService_type().equalsIgnoreCase(AppConfig.KEY_DOHIJAMAH)){
            data.setKotaPengirim(order.getPlace().toStringAddressFormatted());
            data.setKotaPenerima(order.getPlace().toStringAddressFormatted());
        }
    }

    public void onWindowFocused(){
        if(data == null){
            Bundle bundle = getArguments();
            String awb = bundle.getString("awb");
            if(awb != null && !awb.isEmpty()){
                Handler handler = new Handler() {
                    @Override
                    public void handleMessage(Message mesg) {
                        throw new RuntimeException("RuntimeException");
                    }
                };

                retrieve_order_awb(awb, handler);

                // loop till a runtime exception is triggered.
                try { Looper.loop(); }
                catch(RuntimeException e2) { e2.printStackTrace();}

                fill_order_detail();
            }
        }
    }

    private void retrieve_order_awb(final String awb, final Handler handler) {
        HashMap<String,String> params =new HashMap<>();
        params.put("awb", awb);
        String url = AppConfig.URL_TORDER_RETRIEVE_AWB;
        url = url +"/"+awb;
        addRequest("retrieve_order_awb", Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jObj = new JSONObject(response);
                    String message =jObj.getString("message");
                    if(message.equalsIgnoreCase("OK")){
                        JSONArray datas = jObj.getJSONArray("data");
                        if(datas != null && datas.length() > 0) {
                            ParserUtil util = new ParserUtil();
                            TOrder order = null;
                            for (int i = 0; i < datas.length(); i++) {
                                JSONObject data = datas.optJSONObject(i);
                                order = util.parseTOrder(data);
                            }
                            ViewHelper.getInstance().setOrder(order);
                            fill_data(order);
                        }
                    }else{
                        Toast.makeText(context, ""+message, Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(context, "Error "+e.getMessage(), Toast.LENGTH_SHORT).show();
                }

                handler.handleMessage(null);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                volleyError.printStackTrace();
                Toast.makeText(context,"Network Error "+volleyError.getMessage(), LENGTH_SHORT).show();
                handler.handleMessage(null);
            }
        }, params, getKurindoHeaders());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflateAndBind(inflater, container, R.layout.fragment_order_canceled);

        rdgReason.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                reasonEt.setVisibility(View.GONE);
                switch (checkedId){
                    case R.id.radio_reason1:
                        reason = getString(R.string.label_reasonp1);
                        break;
                    case R.id.radio_reason2:
                        reason = getString(R.string.label_reasonp2);
                        break;
                    case R.id.radio_reason3:
                        reason = getString(R.string.label_reasonp3);
                        break;
                    case R.id.radio_reason4:
                        reason = getString(R.string.label_reasonp4);
                        break;
                    case R.id.radio_reason5:
                        reasonEt.setVisibility(View.VISIBLE);
                        reason = null;
                        break;
                }
            }
        });
        rdgReason.check(R.id.radio_reason1);
        fill_order_detail();
        return v;
    }

    private void fill_order_detail() {
        if(data != null){
            String text = "AWB : "+data.getAwb();
            text += "\nPrice : "+data.getPrice()+", COD: "+data.getCod();
            text += "Status: "+data.getMessage();
            tvOrder.setText(text);
        }
    }

    @OnClick(R.id.btnCancel)
    public void OnBtnCancel_Clicked(){
        if(data == null){
            Toast.makeText(context, "Order tidak valid.", Toast.LENGTH_SHORT).show();
            return;
        }
        if(reason == null || reason.isEmpty()){
            Toast.makeText(context, "Tuliskan alasan anda.", Toast.LENGTH_SHORT).show();
            return;
        }

        final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message mesg) {
                throw new RuntimeException("RuntimeException");
            }
        };

        final boolean[] yes = {true};
        DialogInterface.OnClickListener YesClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
              req_order_canceled(handler);
            }
        };
        DialogInterface.OnClickListener NoClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                yes[0] = false;
               handler.handleMessage(null);
            }
        };
        showConfirmationDialog("Konfirmasi","Pesanan ini akan diCANCEL. Anda yakin?", YesClickListener, NoClickListener);

        try { Looper.loop(); }
        catch(RuntimeException e2) {}

        if(yes[0]) getActivity().finish();
    }

    private void req_order_canceled(final Handler handler) {
        HashMap<String, String> params = new HashMap<>();
        params.put("awb", data.getAwb());
        params.put("reason", reason);
        addRequest("req_order_canceled", Request.Method.POST, AppConfig.URL_TORDER_CANCELED, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jObj = new JSONObject(response);
                    String message =jObj.getString("message");
                    if(message.equalsIgnoreCase("OK")){
                        LogUtil.logD(TAG, "Order "+data.getAwb()+" canceled.");
                    }else{
                        Toast.makeText(context, ""+message, Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(context, "Error "+e.getMessage(), Toast.LENGTH_SHORT).show();
                }
                handler.handleMessage(null);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                volleyError.printStackTrace();
                Toast.makeText(context,"Network Error "+volleyError.getMessage(), LENGTH_SHORT).show();
                handler.handleMessage(null);
            }
        }, params, getKurindoHeaders());

    }
}
