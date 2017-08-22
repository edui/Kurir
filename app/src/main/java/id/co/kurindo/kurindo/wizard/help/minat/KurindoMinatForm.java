package id.co.kurindo.kurindo.wizard.help.minat;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatButton;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import butterknife.Bind;
import butterknife.OnClick;
import id.co.kurindo.kurindo.R;
import id.co.kurindo.kurindo.app.AppConfig;
import id.co.kurindo.kurindo.comp.ProgressDialogCustom;
import id.co.kurindo.kurindo.helper.ViewHelper;
import id.co.kurindo.kurindo.model.Shop;
import id.co.kurindo.kurindo.util.LogUtil;
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


    @Bind(R.id.chkDoSend)
    CheckBox chkDoSend;
    @Bind(R.id.chkDoJek)
    CheckBox chkDoJek;
    @Bind(R.id.chkDoMart)
    CheckBox chkDoMart;
    @Bind(R.id.chkDoShop)
    CheckBox chkDoShop;
    @Bind(R.id.chkDoCar)
    CheckBox chkDoCar;
    @Bind(R.id.chkDoMove)
    CheckBox chkDoMove;
    @Bind(R.id.chkDoWash)
    CheckBox chkDoWash;
    @Bind(R.id.chkDoService)
    CheckBox chkDoService;

    String role = AppConfig.KEY_SHOPPIC;
    protected ProgressDialog progressBar;
    Context context;

    boolean process = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if(bundle != null)
            role= bundle.getString("role");
        context = getContext();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflateAndBind(inflater, container, R.layout.fragment_kurindominat);
        progressBar = new ProgressDialogCustom(context);

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
        setup_checkbox(v);
        return v;
    }

    private void setup_checkbox(View v) {
        chkDoSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onCheckboxClicked(v);
            }
        });
        chkDoJek.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onCheckboxClicked(v);
            }
        });
        chkDoMart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onCheckboxClicked(v);
            }
        });
        chkDoShop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onCheckboxClicked(v);
            }
        });
        chkDoCar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onCheckboxClicked(v);
            }
        });
        chkDoMove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onCheckboxClicked(v);
            }
        });
        chkDoWash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onCheckboxClicked(v);
            }
        });
        chkDoService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onCheckboxClicked(v);
            }
        });

        chkDoSend.setChecked(true);
        chkDoJek.setChecked(true);

    }

    HashMap<String,String> fiturMap = new HashMap<>();

    public void onCheckboxClicked(View view) {
        // Is the view now checked?
        boolean checked = ((CheckBox) view).isChecked();
        switch (view.getId()){
            case R.id.chkDoSend:
                if(checked)
                    fiturMap.put(AppConfig.KEY_DOSEND, AppConfig.KEY_DOSEND);
                else
                    fiturMap.remove(AppConfig.KEY_DOSEND);
                break;
            case R.id.chkDoJek:
                if(checked)
                    fiturMap.put(AppConfig.KEY_DOJEK, AppConfig.KEY_DOJEK);
                else
                    fiturMap.remove(AppConfig.KEY_DOJEK);
                break;
            case R.id.chkDoMart:
                if(checked)
                    fiturMap.put(AppConfig.KEY_DOMART, AppConfig.KEY_DOMART);
                else
                    fiturMap.remove(AppConfig.KEY_DOMART);
                break;
            case R.id.chkDoShop:
                if(checked)
                    fiturMap.put(AppConfig.KEY_DOSHOP, AppConfig.KEY_DOSHOP);
                else
                    fiturMap.remove(AppConfig.KEY_DOSHOP);
                break;
            case R.id.chkDoCar:
                if(checked)
                    fiturMap.put(AppConfig.KEY_DOCAR, AppConfig.KEY_DOCAR);
                else
                    fiturMap.remove(AppConfig.KEY_DOCAR);
                break;
            case R.id.chkDoMove:
                if(checked)
                    fiturMap.put(AppConfig.KEY_DOMOVE, AppConfig.KEY_DOMOVE);
                else
                    fiturMap.remove(AppConfig.KEY_DOMOVE);
                break;
            case R.id.chkDoWash:
                if(checked)
                    fiturMap.put(AppConfig.KEY_DOWASH, AppConfig.KEY_DOWASH);
                else
                    fiturMap.remove(AppConfig.KEY_DOWASH);
                break;
            case R.id.chkDoService:
                if(checked)
                    fiturMap.put(AppConfig.KEY_DOSERVICE, AppConfig.KEY_DOSERVICE);
                else
                    fiturMap.remove(AppConfig.KEY_DOSERVICE);
                break;
        }
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
        maps.put("services", fiturs);
        process = true;
        progressBar.setMessage("sedang mengirimkan minat anda....");
        progressBar.show();
        addRequest(tag_string_req , Request.Method.POST, URI, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                LogUtil.logD(TAG, tag_string_req +" > Response:" + response.toString());
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
                progressBar.dismiss();
                process = false;
                if(ok) getActivity().finish();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                LogUtil.logD(tag_string_req, ""+volleyError.getMessage());
                progressBar.dismiss();
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
