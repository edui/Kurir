package id.co.kurindo.kurindo.wizard.shopadm;

import android.app.Dialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.lamudi.phonefield.PhoneInputLayout;
import com.stepstone.stepper.VerificationError;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;

import butterknife.Bind;
import butterknife.OnClick;
import id.co.kurindo.kurindo.R;
import id.co.kurindo.kurindo.app.AppConfig;
import id.co.kurindo.kurindo.base.RecyclerItemClickListener;
import id.co.kurindo.kurindo.helper.ViewHelper;
import id.co.kurindo.kurindo.map.SinglePinLocationMapFragment;
import id.co.kurindo.kurindo.model.TUser;
import id.co.kurindo.kurindo.util.LogUtil;
import id.co.kurindo.kurindo.util.ParserUtil;

/**
 * Created by dwim on 2/14/2017.
 */

public class ShopAdmPinLocationMapFragment extends SinglePinLocationMapFragment {
    private static final String TAG = "ShopAdmPinLocationMapFragment";

    @Bind(R.id.input_telepon)
    PhoneInputLayout inputTelepon;
    @Bind(R.id.input_levels)
    Spinner inputLevel;

    String level;
    TUser pic;

    @Override
    protected int getLayout() {
        return R.layout.fragment_maps_shopadm_pin_location;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        hidepanel(true);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),R.array.levels_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        inputLevel.setAdapter(adapter);
        inputLevel.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selected = (String) parent.getItemAtPosition(position);
                level = selected;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        inputLevel.setSelection(1);


        inputTelepon.setDefaultCountry(AppConfig.DEFAULT_COUNTRY);
        inputTelepon.setHint(R.string.telepon_pengelola);

        return view;
    }

    @Override
    public void onClick_mLocationMarkerText() {
        super.onClick_mLocationMarkerText();

        hidepanel(false);
    }

    @OnClick(R.id.btnPilihPengelola)
    public void onClick(){
        showPopupWindow("Daftar Calon Pengelola", R.drawable.kerjasama_berlangganan);
    }

    protected void showPopupWindow(String title, int imageResourceId) {
        data.clear();
        final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message mesg) {
                throw new RuntimeException();
            }
        };

        requestDataPengelola(handler);

        // loop till a runtime exception is triggered.
        try { Looper.loop(); }
        catch(RuntimeException e2) { }

        // Create custom dialog object
        final Dialog dialog = new Dialog(getContext());
        // Include dialog.xml file
        dialog.setContentView(R.layout.popup_list);
        // Set dialog title
        dialog.setTitle("Popup Dialog");

        RecyclerView list = (RecyclerView) dialog.findViewById(R.id.popupList);
        list.setLayoutManager(new GridLayoutManager(getContext(), 1));
        list.setHasFixedSize(true);
        list.setAdapter(tUserAdapter);
        list.addOnItemTouchListener(new RecyclerItemClickListener(getContext(), new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                TUser p = data.get(position);
                inputTelepon.setPhoneNumber(p.getPhone());
                showAddressLayout();
                dialog.dismiss();

                refreshMap();
            }
        }));

        // set values for custom dialog components - text, image and button
        //TextView text = (TextView) dialog.findViewById(R.id.textDialog);
        //text.setText(content);
        ImageView image = (ImageView) dialog.findViewById(R.id.imageDialog);
        if(imageResourceId == 0) imageResourceId  = R.drawable.icon_syarat_ketentuan;
        image.setImageResource(imageResourceId);
        TextView textTitleDialog = (TextView) dialog.findViewById(R.id.textTitleDialog);
        if(title != null) textTitleDialog.setText(title);

        dialog.show();

        ImageButton declineButton = (ImageButton) dialog.findViewById(R.id.btncancelcat);
        // if decline button is clicked, close the custom dialog
        declineButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Close dialog
                dialog.dismiss();
                originMode = false;
            }
        });
    }

    private void requestDataPengelola(final Handler handler) {
        HashMap<String, String> params = new HashMap();
        params.put("shop_id", ""+ViewHelper.getInstance().getShop().getId());

        addRequest("request_data_pengelola", Request.Method.POST, AppConfig.URL_SHOP_PREPIC_LIST, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    LogUtil.logD(TAG, "requestDataPengelola Response: " + response.toString());
                    JSONObject jObj = new JSONObject(response);
                    String message = jObj.getString("status");
                    boolean OK = "OK".equalsIgnoreCase(message);
                    if(OK){
                        JSONArray datas = jObj.getJSONArray("data");
                        if(datas != null && datas.length() > 0) {
                            data.clear();
                            ParserUtil parser = new ParserUtil();
                            for (int i = 0; i < datas.length(); i++) {
                                TUser user = parser.parserUser(datas.getJSONObject(i));
                                data.add(user);
                            }
                            tUserAdapter.notifyDataSetChanged();
                        }
                    }
                    handler.handleMessage(null);
                }catch (Exception e){
                    handler.handleMessage(null);
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                handler.handleMessage(null);
                volleyError.printStackTrace();
            }
        }, params, getKurindoHeaders());
    }


    @Override
    public VerificationError verifyStep() {

        if(!inputTelepon.isValid()){
            return new VerificationError("Masukkan nomor telepon Pengelola");
        }

        return null;

    }

    static ShopAdmPinLocationMapFragment instance;
    public static ShopAdmPinLocationMapFragment newInstance() {
        if (instance == null) {
            instance = new ShopAdmPinLocationMapFragment();
        }
        return instance;
    }
    public static ShopAdmPinLocationMapFragment getInstance() {
        return instance;
    }



}
