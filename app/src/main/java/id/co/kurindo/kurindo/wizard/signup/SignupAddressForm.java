package id.co.kurindo.kurindo.wizard.signup;

import android.app.ProgressDialog;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.stepstone.stepper.VerificationError;

import org.json.JSONObject;

import java.util.HashMap;

import butterknife.Bind;
import id.co.kurindo.kurindo.R;
import id.co.kurindo.kurindo.app.AppConfig;
import id.co.kurindo.kurindo.map.SinglePinLocationMapFragment;
import id.co.kurindo.kurindo.model.Address;
import id.co.kurindo.kurindo.model.TUser;
import id.co.kurindo.kurindo.util.ParserUtil;

import static android.app.Activity.RESULT_OK;

/**
 * Created by dwim on 3/15/2017.
 */

public class SignupAddressForm extends SinglePinLocationMapFragment {
    private static final String TAG = "SignupAddressForm";
    VerificationError invalid = null;
    ProgressDialog progressDialog;

    @Bind(R.id.tvAlamat)
    TextView tvAlamat;
    @Bind(R.id.tvKecamatan)
    TextView tvKecamatan;
    @Bind(R.id.tvKabupaten)
    TextView tvKabupaten;
    @Bind(R.id.tvPropinsi)
    TextView tvPropinsi;
    @Bind(R.id.tvNegara)
    TextView tvNegara;

    protected int getLayout() {
        return R.layout.fragment_maps_pin_addressform;
    }

    public void onClick_mLocationMarkerText(){
        super.onClick_mLocationMarkerText();
        displayAddressText();
        hidepanel(false);
    }

    private void displayAddressText() {
        if(origin != null && origin.getAddress() != null){
            Address addr = origin.getAddress();
            tvAlamat.setText(addr.getAlamat());
            tvKecamatan.setText(addr.getKecamatan());
            tvKabupaten.setText(addr.getKabupaten());
            tvPropinsi.setText(addr.getPropinsi());
            tvNegara.setText(addr.getNegara());
        }
    }


    @Override
    public VerificationError verifyStep() {
        if(originMode) return new VerificationError("Set Lokasi Anda");
        if(origin ==null || origin.getAddress() == null || origin.getAddress().getKecamatan() == null) return new VerificationError("Set Lokasi Anda");

        update_city();
        return invalid;
    }

    private void update_city() {
        final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message mesg) {
                throw new RuntimeException("RuntimeException");
            }
        };

        update_city(handler);

        //loop till a runtime exception is triggered.
        try { Looper.loop(); }
        catch(RuntimeException e2) {}

    }

    private void update_city(final Handler handler) {
        String url = AppConfig.URL_USER_CITY_UPDATE;
        HashMap params= new HashMap();
        GsonBuilder builder = new GsonBuilder();
        builder.setPrettyPrinting();
        builder.serializeNulls();
        final Gson gson = builder.create();

        params.put("address", gson.toJson(origin.getAddress()));
        addRequest("request_update_city", Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "request_update_city Response: " + response.toString());
                try {
                    JSONObject jObj = new JSONObject(response);
                    String message = jObj.getString("message");
                    if("OK".equalsIgnoreCase(message)){

                        TUser tuser = gson.fromJson(jObj.getString("data"), TUser.class);
                        if(tuser.getPhone() ==null || tuser.getPhone().isEmpty()){
                            JSONObject user = jObj.getJSONObject("data");
                            String firstname = user.getString("firstname");
                            String lastname = user.getString("lastname");
                            String email = user.getString("email");
                            String created_at = user.getString("created");
                            String phone = user.getString("phone");
                            String gender = user.getString("gender");

                            tuser.setFirstname(firstname);
                            tuser.setLastname(lastname);
                            tuser.setEmail(email);
                            tuser.setPhone(phone);
                            tuser.setGender(gender);
                            tuser.setCreated_at(created_at);
                            tuser.setAddress(origin.getAddress());
                        }

                        //update city
                        String city = jObj.getString("city");

                        if(city == null || city.isEmpty() || city.equalsIgnoreCase("null")){
                        }else{
                            if(tuser != null){
                                db.onUpgrade(db.getWritableDatabase(), 0, 1);
                                //db.onCreateTableRecipient(db.getWritableDatabase());
                                db.onUpgradeTableRecipient(db.getWritableDatabase(),0,1);

                                db.onUpgradeUserAddress(db.getWritableDatabase(), 0, 1);

                                db.addUser(tuser);
                                //db.updateUserCityP(tuser.getPhone(), city);
                                //tuser.setAddress(origin.getAddress());
                                db.addAddress(tuser,"HOMEBASE");
                                getActivity().setResult(RESULT_OK);
                            }
                        }
                    }
                }catch (Exception e){e.printStackTrace();}
                handler.handleMessage(null);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                volleyError.printStackTrace();
                invalid = new VerificationError("VolleyError : " + volleyError.getMessage());
                handler.handleMessage(null);
            }
        }, params, getKurindoHeaders());
    }

    @Override
    public void onSelected() {

    }

    @Override
    public void onError(@NonNull VerificationError error) {

    }
}
