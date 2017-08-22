package id.co.kurindo.kurindo;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Timer;

import butterknife.Bind;
import butterknife.OnClick;
import id.co.kurindo.kurindo.adapter.TKurirAdapter;
import id.co.kurindo.kurindo.adapter.TUserAdapter;
import id.co.kurindo.kurindo.adapter.UserAdapter;
import id.co.kurindo.kurindo.app.AppConfig;
import id.co.kurindo.kurindo.app.AppController;
import id.co.kurindo.kurindo.base.BaseFragment;
import id.co.kurindo.kurindo.helper.ViewHelper;
import id.co.kurindo.kurindo.map.PickAnAddressActivity;
import id.co.kurindo.kurindo.model.Address;
import id.co.kurindo.kurindo.model.TUser;
import id.co.kurindo.kurindo.model.User;
import id.co.kurindo.kurindo.util.LogUtil;

import static android.app.Activity.RESULT_OK;

/**
 * Created by DwiM on 12/12/2016.
 */

public abstract class BaseKurirFragment extends BaseFragment {
    private static final String TAG = "BaseKurirFragment";

    @Bind(R.id.RefreshBtn)
    AppCompatButton refreshBtn;

    @Bind(R.id.list)
    RecyclerView daftarKurir;

    ArrayList<TUser> users = new ArrayList<>();
    Timer t ;

    @Bind(R.id.progressBar1)
    ProgressBar progressBar;

    //@Bind(R.id.TextViewTitle)
    //TextView textView;

    @Bind(R.id.tvLocation)
    protected TextView tvLocation;
    protected String city = "ALL";
    @Bind(R.id.btnLocation)
    AppCompatButton btnLocation;

    TKurirAdapter userAdapter;
    protected Context context;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getContext();
        setHasOptionsMenu(true);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflateAndBind(inflater, container, R.layout.fragment_kurir);

        btnLocation.setVisibility(View.GONE);
        if(session.isSuperAdministrator()){
            btnLocation.setVisibility(View.VISIBLE);
        }
        daftarKurir.setLayoutManager(new GridLayoutManager(getContext(), 1));
        daftarKurir.setHasFixedSize(true);

        userAdapter = new TKurirAdapter(getContext(), users, new TKurirAdapter.OnItemClickListener() {
            @Override
            public void onApprovedButtonClick(View view, final int position, final HashMap fiturMap) {
                final TUser u = users.get(position);
                final Handler handler = new Handler() {
                    @Override
                    public void handleMessage(Message mesg) {
                        throw new RuntimeException();
                    }
                };

                DialogInterface.OnClickListener YesClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        approved_kurir(u, position, handler, fiturMap);
                    }
                };

                DialogInterface.OnClickListener NoClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        handler.handleMessage(null);
                    }
                };
                showConfirmationDialog("Konfirmasi","Anda Yakin akan memperbarui SKILL user '"+u.getFirstname()+ " "+u.getLastname()+"' ?", YesClickListener, NoClickListener);

                // loop till a runtime exception is triggered.
                try { Looper.loop(); }
                catch(RuntimeException e2) {}
            }

            @Override
            public void onCallButtonClick(View view, int position) {
                final TUser u = users.get(position);
                String phone = u.getPhone();
                Uri uri = Uri.parse("https://api.whatsapp.com/send?phone="+phone+"&text=Assalamu'alaykum. KURIR "+u.getName());
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }

            @Override
            public void onRemoveButtonClick(View v, final int position) {
                final TUser u = users.get(position);
                final Handler handler = new Handler() {
                    @Override
                    public void handleMessage(Message mesg) {
                        throw new RuntimeException();
                    }
                };

                DialogInterface.OnClickListener YesClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        delete_kurir(u, position, handler);
                    }
                };

                DialogInterface.OnClickListener NoClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        handler.handleMessage(null);
                    }
                };
                showConfirmationDialog("Konfirmasi","Anda Yakin akan menghapus user '"+u.getFirstname()+ " "+u.getLastname()+"' ?", YesClickListener, NoClickListener);

                // loop till a runtime exception is triggered.
                try { Looper.loop(); }
                catch(RuntimeException e2) {}
            }
        });
        daftarKurir.setAdapter(userAdapter);

        return rootView;
    }

    private void delete_kurir(final TUser u, final int position, final Handler handler) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                progressBar.setVisibility(View.VISIBLE);
                String tag_string_req = "req_kurir_new_approval";
                StringRequest strReq = new StringRequest(Request.Method.POST,
                        AppConfig.URL_KURIR_DELETED, new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        LogUtil.logD(TAG, "KURIRLAMA > updated: Response: " + response.toString());
                        //hideDialog();

                        try {
                            JSONObject jObj = new JSONObject(response);
                            boolean error = jObj.getBoolean("error");

                            // Check for error node in json
                            if (!error) {
                                users.remove(position);
                                userAdapter.notifyDataSetChanged();

                                String msg = jObj.getString("message");
                                Toast.makeText(context, ""+msg, Toast.LENGTH_LONG).show();
                                progressBar.setVisibility(View.GONE);
                            } else {
                                //Get the error message
                                String errorMsg = jObj.getString("message");
                                Toast.makeText(context, ""+errorMsg, Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            // JSON error
                            e.printStackTrace();
                            Toast.makeText(context, "Json error " + e.getMessage(), Toast.LENGTH_LONG).show();
                            //handler.handleMessage(null);
                            //progressBar.setVisibility(View.GONE);
                        }

                        progressBar.setVisibility(View.GONE);
                        handler.handleMessage(null);
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        LogUtil.logE(TAG, "Kurir Approval Error: " + error.getMessage());
                        Toast.makeText(context, "Network Error "+error.getMessage(), Toast.LENGTH_LONG).show();
                        progressBar.setVisibility(View.GONE);
                        handler.handleMessage(null);
                    }
                }) {

                    @Override
                    protected Map<String, String> getParams() {
                        // Posting parameters to  url
                        Map<String, String> params = new HashMap<String, String>();
                        params.put("kurir", u.getPhone());

                        return params;
                    }
                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        return getKurindoHeaders();
                    }

                };

                // Adding request to request queue
                AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
            }
        });
    }

    private void approved_kurir(final TUser u, final int position, final Handler handler, final HashMap<String, String> fiturMap) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                progressBar.setVisibility(View.VISIBLE);
                String tag_string_req = "req_kurir_updated";
                StringRequest strReq = new StringRequest(Request.Method.POST,
                        AppConfig.URL_KURIR_UPDATED, new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        LogUtil.logD(TAG, "KURIRLAMA > updated: Response: " + response.toString());
                        //hideDialog();

                        try {
                            JSONObject jObj = new JSONObject(response);
                            boolean error = jObj.getBoolean("error");

                            // Check for error node in json
                            if (!error) {
                                userAdapter.notifyDataSetChanged();

                                String msg = jObj.getString("message");
                                Toast.makeText(context, ""+msg, Toast.LENGTH_LONG).show();
                                progressBar.setVisibility(View.GONE);
                            } else {
                                //Get the error message
                                String errorMsg = jObj.getString("message");
                                Toast.makeText(context, ""+errorMsg, Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            // JSON error
                            e.printStackTrace();
                            Toast.makeText(context, "Json error " + e.getMessage(), Toast.LENGTH_LONG).show();
                            //handler.handleMessage(null);
                            //progressBar.setVisibility(View.GONE);
                        }

                        progressBar.setVisibility(View.GONE);
                        handler.handleMessage(null);
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        LogUtil.logE(TAG, "Kurir Approval Error: " + error.getMessage());
                        Toast.makeText(context, "Network Error "+error.getMessage(), Toast.LENGTH_LONG).show();
                        progressBar.setVisibility(View.GONE);
                        handler.handleMessage(null);
                    }
                }) {

                    @Override
                    protected Map<String, String> getParams() {
                        // Posting parameters to  url
                        Map<String, String> params = new HashMap<String, String>();
                        params.put("kurir", u.getPhone());
                        String fiturs = "";
                        Iterator it = fiturMap.entrySet().iterator();
                        int i=0;
                        while (it.hasNext()) {
                            Map.Entry pair = (Map.Entry)it.next();
                            if(i >0)
                                fiturs += ",";
                            fiturs += pair.getKey();
                            i++;
                        }
                        params.put("services", fiturs);

                        return params;
                    }
                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        return getKurindoHeaders();
                    }

                };

                // Adding request to request queue
                AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
            }
        });
    }

    @OnClick(R.id.btnLocation)
    public void onBtnLocation_Clicked(View v) {
        showPopupWindow("Ganti Lokasi", R.array.pilih_lokasi_array, R.drawable.kirim_dalam_kotaa);
    }
    @OnClick(R.id.RefreshBtn)
    public void onRefreshBtn_Clicked(View v) {
        check_kurir();
    }

    public abstract void check_kurir();

    TextView tvLocationPopup;
    protected int location = R.id.radio_current;
    Dialog dialog = null;
    protected void showPopupWindow(String title, int arrayResourceId, int imageResourceId) {

        // Create custom dialog object
        dialog = new Dialog(getActivity());
        // Include dialog.xml file
        dialog.setContentView(R.layout.popup_location_all);
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
                    case R.id.radio_all:
                        location = R.id.radio_all;
                        address= null;
                        city = "ALL";
                        tvLocationPopup.setText("ALL");
                        tvLocationPopup.setBackgroundColor(Color.WHITE);
                        break;
                    case R.id.radio_current:
                        location = R.id.radio_current;
                        address= ViewHelper.getInstance().getLastAddress();
                        city = address.getKabupaten();
                        break;
                    case R.id.radio_homebase:
                        TUser user = db.getUserAddressByType(AppConfig.HOMEBASE);
                        address = user.getAddress();
                        city = address.getKabupaten();
                        location = R.id.radio_homebase;
                        break;
                    case R.id.radio_others:
                        location = R.id.radio_others;
                        tvLocationPopup.setText("Klik untuk Set Area");
                        tvLocationPopup.setBackgroundColor(Color.GREEN);
                        city = "ALL";
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
                tvLocation.setText(tvLocationPopup.getText());
                check_kurir();
                // Close dialog
                dialog.dismiss();
            }
        });
        ImageButton declineButton = (ImageButton) dialog.findViewById(R.id.btncancelcat);
        // if decline button is clicked, close the custom dialog
        declineButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tvLocation.setText(tvLocationPopup.getText());
                check_kurir();
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
                    city = origin.getAddress().getKabupaten();
                    tvLocationPopup.setBackgroundColor(Color.CYAN);
                    tvLocationPopup.invalidate();
                }
            }
        }
    }

}
