package id.co.kurindo.kurindo.wizard.shopadm;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.lamudi.phonefield.PhoneInputLayout;
import com.stepstone.stepper.Step;
import com.stepstone.stepper.VerificationError;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import butterknife.Bind;
import id.co.kurindo.kurindo.R;
import id.co.kurindo.kurindo.adapter.TUserSpAdapter;
import id.co.kurindo.kurindo.app.AppConfig;
import id.co.kurindo.kurindo.helper.ShopAdmHelper;
import id.co.kurindo.kurindo.model.Address;
import id.co.kurindo.kurindo.model.City;
import id.co.kurindo.kurindo.model.Shop;
import id.co.kurindo.kurindo.model.TUser;
import id.co.kurindo.kurindo.util.ParserUtil;
import id.co.kurindo.kurindo.wizard.BaseStepFragment;

import static id.co.kurindo.kurindo.R.style.CustomDialog;

/**
 * Created by dwim on 3/15/2017.
 */

public class AddShopForm1 extends BaseStepFragment implements Step {
    private static final String TAG = "AddShopForm1";
    ProgressDialog progressDialog;

    @Bind(R.id.input_shopname)
    EditText inputShopName;
    @Bind(R.id.input_shopdescription)
    EditText inputShopDescription;
    @Bind(R.id.input_shopwebsite)
    EditText inputShopWebsite;
    @Bind(R.id.input_category)
    Spinner inputCategory;
    @Bind(R.id.tvAlamat)
    TextView tvAlamat;
    @Bind(R.id.tvPhone)
    TextView tvPhone;
    @Bind(R.id.tvEmail)
    TextView tvEmail;
    @Bind(R.id.pengelola_spinner)
    Spinner pilihPengelola;
    @Bind(R.id.tvInfoPilih)
    TextView tvInfoPilih;

    String telepon;
    TUser pic;
    TUser selectedPilih;
    TUserSpAdapter userAdapter;
    List<TUser> users = new ArrayList<>();

    boolean editMode = false;
    Context context;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if(bundle != null)
            editMode = bundle.getBoolean("editMode");
        context = getContext();

    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflateAndBind(inflater, container, R.layout.shopadd1_layout);

        progressDialog = new ProgressDialog(getActivity(), CustomDialog);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),R.array.shop_categories_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        inputCategory.setAdapter(adapter);
        inputCategory.setSelection(0);

        pilihPengelola.setVisibility(View.GONE);
        tvInfoPilih.setVisibility(View.GONE);
        TUser u = db.getUser();
        if(u.getRole().equalsIgnoreCase(AppConfig.KEY_ADMINISTRATOR) || u.getRole().equalsIgnoreCase(AppConfig.KEY_ADMINKEC) || u.getRole().equalsIgnoreCase(AppConfig.KEY_ADMINKAB) || u.getRole().equalsIgnoreCase(AppConfig.KEY_ADMINPROP) || u.getRole().equalsIgnoreCase(AppConfig.KEY_ADMINNEG)){
            pilihPengelola.setVisibility(View.VISIBLE);
            tvInfoPilih.setVisibility(View.VISIBLE);

            userAdapter = new TUserSpAdapter(getContext(), users);
            pilihPengelola.setAdapter(userAdapter);
            pilihPengelola.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    selectedPilih = users.get(position);
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });

            load_user();
        }
        return v;
    }

    private void load_user() {
        String URI = AppConfig.URL_LIST_KURIR_LOCATIONBASED;
        //URI = URI.replace("/{type}", "/"+param);

        final String tag_string_req = "req_shoppic_list";
        HashMap<String, String > maps = new HashMap<>();
        maps.put("form-type", "json");
        maps.put("type", "SHOPPIC");
        addRequest(tag_string_req , Request.Method.POST, URI, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, tag_string_req +" > Response:" + response.toString());
                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");

                    // Check for error node in json
                    if (!error) {

                        JSONArray datas = jObj.getJSONArray("data");
                        if(datas != null && datas.length() > 0){
                            ParserUtil parser = new ParserUtil();
                            users.clear();
                            users.add(new TUser());
                            for (int j = 0; j < datas.length(); j++) {
                                /*TUser recipient = gson.fromJson(datas.getString(j), TUser.class);
                                Address addr= gson.fromJson(datas.getString(j), Address.class);
                                recipient.setAddress(addr);
                                City city = gson.fromJson(datas.getString(j),City.class);
                                addr.setCity(city);
                                */
                                TUser user = parser.parserUser(datas.getJSONObject(j));
                                users.add(user);
                            }
                            userAdapter.notifyDataSetChanged();
                        }
                    } else {
                        // Error in login. Get the error message
                        String errorMsg = jObj.getString("message");
                        Toast.makeText(context, errorMsg, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    // JSON error
                    e.printStackTrace();
                    Toast.makeText(context, "Json error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Log.d(tag_string_req, ""+volleyError.getMessage());
            }
        }, maps, getKurindoHeaders());

    }

    @Override
    public int getName() {
        return R.string.shop_information;
    }

    @Override
    public VerificationError verifyStep() {
        boolean valid = true;
        String name = inputShopName.getText().toString();
        if(name.isEmpty()) {
            inputShopName.setError("Nama toko harus diisi.");
            valid = false;
        }else {
            inputShopName.setError(null);
        }

        if(!valid){
            return new VerificationError("Form belum lengkap diisi.");
        }
        Shop shop = new Shop();
        if(editMode) shop = ShopAdmHelper.getInstance().getShop();
        shop.setName(inputShopName.getText().toString());
        shop.setMotto(inputShopDescription.getText().toString());
        shop.setCategory(inputCategory.getSelectedItem().toString());

        if(selectedPilih == null) {
            shop.setPic(pic);
            shop.setAddress(pic.getAddress());
        }
        else {
            shop.setPic(selectedPilih);
            shop.setAddress(selectedPilih.getAddress());
        }

        ShopAdmHelper.getInstance().setShop(shop);
        return null;
    }

    @Override
    public void onSelected() {
        pic = db.getUser();
        if(pic != null){
            tvPhone.setText(pic.getPhone());
            tvEmail.setText(pic.getEmail());

           pic = db.getUserAddress(pic.getPhone());
            if(pic != null){
                tvAlamat.setText(pic.getAddress().toStringFormatted());
            }
        }

        if(editMode){
            Shop s = ShopAdmHelper.getInstance().getShop();
            inputShopName.setText(s.getName());
            inputShopDescription.setText(s.getMotto());
            inputShopWebsite.setText(s.getLink());
            List<String> categories = Arrays.asList(getResources().getStringArray(R.array.shop_categories_array));
            inputCategory.setSelection(categories.indexOf(s.getCategory()));
        }
    }

    @Override
    public void onError(@NonNull VerificationError error) {

    }


    public static AddShopForm1 newInstance(boolean editMode) {
        Bundle args = new Bundle();
        args.putBoolean("editMode", editMode);
        AddShopForm1 fragment = new AddShopForm1();
        fragment.setArguments(args);
        return fragment;
    }

}
