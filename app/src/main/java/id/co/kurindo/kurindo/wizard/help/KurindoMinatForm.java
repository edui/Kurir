package id.co.kurindo.kurindo.wizard.help;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatButton;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.stepstone.stepper.Step;
import com.stepstone.stepper.VerificationError;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;

import butterknife.Bind;
import butterknife.OnClick;
import id.co.kurindo.kurindo.R;
import id.co.kurindo.kurindo.app.AppConfig;
import id.co.kurindo.kurindo.helper.ViewHelper;
import id.co.kurindo.kurindo.model.Shop;
import id.co.kurindo.kurindo.model.TUser;
import id.co.kurindo.kurindo.util.ParserUtil;
import id.co.kurindo.kurindo.wizard.BaseStepFragment;

/**
 * Created by dwim on 3/15/2017.
 */

public class KurindoMinatForm extends BaseStepFragment implements Step {
    private static final String TAG = "KurindoMinatForm";

    @Bind(R.id.input_nik)
    EditText inputNik;
    @Bind(R.id.input_simc)
    EditText inputSimc;

    @Bind(R.id.radio_group_role)
    RadioGroup roleRadioGroup;
    @Bind(R.id.radio_shoppic)
    RadioButton radioShoppic;

    @Bind(R.id.tvShopSelected)
    TextView tvShopSelected;
    @Bind(R.id.TextViewTitle)
    TextView TextViewTitle;

    @Bind(R.id.input_layout_kurir)
    LinearLayout layoutKurir;
    @Bind(R.id.input_layout_shoppic)
    LinearLayout layoutShoppic;
    @Bind(R.id.input_layout_mitra)
    LinearLayout layoutMitra;
    @Bind(R.id.input_layout_shopres)
    LinearLayout layoutShopres;

    @Bind(R.id.btn_signup)
    AppCompatButton btnSignup;

    @Bind(R.id.chkAgrement)
    CheckBox chkAgrement;
    @Bind(R.id.ivAgrement)
    ImageView ivAgrement;
    @Bind(R.id.ivAgrement2)
    ImageView ivAgrement2;

    String role = AppConfig.KEY_SHOPPIC;
    ProgressDialog progressDialog;

    boolean process = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if(bundle != null)
            role= bundle.getString("role");

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflateAndBind(inflater, container, R.layout.fragment_kurindominat);
        progressDialog = new ProgressDialog(getActivity(),R.style.AppTheme);

        ivAgrement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopupWindow("Kurindo \nEnd User License Agreement", R.raw.syarat_penggunaan, R.drawable.icon_syarat_ketentuan);
            }
        });
        ivAgrement2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopupWindow("Kurindo \nPrivacy Policy", R.raw.privacy_policy, R.drawable.icon_syarat_ketentuan);
            }
        });

        return v;
    }

    private void setup_radio() {
        roleRadioGroup.setEnabled(true);
        roleRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener(){
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch(checkedId){
                    case R.id.radio_shoppic:
                        if(ViewHelper.getInstance().getSelectedShop()){
                            role = AppConfig.KEY_SHOPRESS;
                            show_shopres_layout();
                        }else{
                            role = AppConfig.KEY_SHOPPIC;
                            show_shoppic_layout();
                        }
                        break;
                    case R.id.radio_kurir:
                        role = AppConfig.KEY_KURIR;
                        show_kurir_layout();
                        break;
                    case R.id.radio_mitra:
                        role = AppConfig.KEY_MITRA;
                        show_mitra_layout();
                        break;
                }
            }
        });
        if(role == AppConfig.KEY_SHOPPIC || role == AppConfig.KEY_SHOPRESS){
            roleRadioGroup.check(R.id.radio_shoppic);

            if(ViewHelper.getInstance().getSelectedShop()){
                role = AppConfig.KEY_SHOPRESS;
                show_shopres_layout();
            }else{
                role = AppConfig.KEY_SHOPPIC;
                show_shoppic_layout();
            }
        }
        else if(role == AppConfig.KEY_KURIR){
            roleRadioGroup.check(R.id.radio_kurir);
            role = AppConfig.KEY_KURIR;
            show_kurir_layout();
        }
        else if(role == AppConfig.KEY_MITRA){
            roleRadioGroup.check(R.id.radio_mitra);
            role = AppConfig.KEY_MITRA;
            show_mitra_layout();
        }
        roleRadioGroup.setOnCheckedChangeListener(null);
        roleRadioGroup.setVisibility(View.GONE);
    }

    private void show_shoppic_layout() {
        TextViewTitle.setText("Daftar sebagai "+ getString( R.string.label_shoppic));
        layoutShoppic.setVisibility(View.VISIBLE);
        layoutMitra.setVisibility(View.GONE);
        layoutKurir.setVisibility(View.GONE);
        layoutShopres.setVisibility(View.GONE);
    }
    private void show_shopres_layout() {
        TextViewTitle.setText("Daftar sebagai "+getString(R.string.label_shopres));
        Shop shop = ViewHelper.getInstance().getShop();
        if(shop != null){
            tvShopSelected.setText(shop.getName()+(shop.getMotto()==null? (shop.getAddress()==null? "": shop.getAddress().toStringFormatted()):"\n"+shop.getMotto()));
        }
        radioShoppic.setText(getString(R.string.label_shopres));
        layoutShopres.setVisibility(View.VISIBLE);
        layoutShoppic.setVisibility(View.GONE);
        layoutMitra.setVisibility(View.GONE);
        layoutKurir.setVisibility(View.GONE);
    }
    private void show_kurir_layout() {
        TextViewTitle.setText("Daftar sebagai "+getString(R.string.label_kurir));
        layoutKurir.setVisibility(View.VISIBLE);
        layoutMitra.setVisibility(View.GONE);
        layoutShoppic.setVisibility(View.GONE);
        layoutShopres.setVisibility(View.GONE);
    }
    private void show_mitra_layout() {
        TextViewTitle.setText("Daftar sebagai "+getString(R.string.label_mitra));
        layoutMitra.setVisibility(View.VISIBLE);
        layoutKurir.setVisibility(View.GONE);
        layoutShoppic.setVisibility(View.GONE);
        layoutShopres.setVisibility(View.GONE);
    }

    @OnClick(R.id.btn_signup)
    public void btnSignup_onClick(){
        if(!validate()) return;
        String URI = AppConfig.URL_MINAT_FORM;
        final String tag_string_req = "req_minat_form";
        HashMap<String, String > maps = new HashMap<>();
        maps.put("role", role);
        maps.put("simc", ""+inputSimc.getText().toString());
        maps.put("nik", ""+inputNik.getText().toString());
        if(ViewHelper.getInstance().getSelectedShop() && ViewHelper.getInstance().getShop() != null){
            maps.put("shopid", ""+ViewHelper.getInstance().getShop().getId());
        }
        process = true;
        progressDialog.show();
        addRequest(tag_string_req , Request.Method.POST, URI, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, tag_string_req +" > Response:" + response.toString());
                boolean ok = false;
                try {
                    JSONObject jObj = new JSONObject(response);
                    String message = jObj.getString("message");
                    if(message.equalsIgnoreCase("OK")){
                        if(getContext() != null)
                            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();

                        ok = true;
                    }else{
                        if(getContext() != null)
                            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
                    }
                    clear_form();
                } catch (JSONException e) {
                    e.printStackTrace();
                    if(getContext() != null)
                        Toast.makeText(getContext(), "Json error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
                progressDialog.dismiss();
                process = false;
                if(ok) getActivity().finish();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Log.d(tag_string_req, ""+volleyError.getMessage());
                progressDialog.dismiss();
                process = false;
            }
        }, maps, getKurindoHeaders());
    }

    private void clear_form() {
        chkAgrement.setChecked(false);
        roleRadioGroup.check(R.id.radio_shoppic);
        inputNik.setText(null);
        inputSimc.setText(null);
    }

    private boolean validate() {
        boolean valid =true;
        if(role.equalsIgnoreCase(AppConfig.KEY_SHOPPIC)){

        }else if(role.equalsIgnoreCase(AppConfig.KEY_KURIR)){
            String nik = inputNik.getText().toString();
            String simc = inputSimc.getText().toString();

            if(nik==null || (nik.isEmpty() && nik.length() < 4)){
                inputNik.setError("Inputkan nomor NIK anda");
                valid = false;
            }else{
                inputNik.setError(null);
            }

            if(simc==null || (simc.isEmpty() && simc.length() < 4)){
                inputSimc.setError("Inputkan nomor SIM C anda");
                valid = false;
            }else{
                inputSimc.setError(null);
            }
        }else if(role.equalsIgnoreCase(AppConfig.KEY_MITRA)){

        }

        if(valid){
            if(!chkAgrement.isChecked()) {
                Toast.makeText(getActivity(), "Anda belum menyetujui syarat dan ketentuan kami.", Toast.LENGTH_SHORT).show();
                valid = false;
            }
        }
        return valid;
    }

    @Override
    public int getName() {
        return R.string.kurir_open_form;
    }

    @Override
    public VerificationError verifyStep() {

        if(process) new VerificationError("Ada proses lain sedang bekerja.");
        return null;
    }

    @Override
    public void onSelected() {
        setup_radio();

    }

    @Override
    public void onError(@NonNull VerificationError error) {

    }

    public static KurindoMinatForm newInstance(String role) {
        Bundle args = new Bundle();
        args.putString("role", role);
        KurindoMinatForm fragment = new KurindoMinatForm();
        fragment.setArguments(args);
        return fragment;
    }

}
