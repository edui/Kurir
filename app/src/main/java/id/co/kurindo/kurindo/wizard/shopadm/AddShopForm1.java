package id.co.kurindo.kurindo.wizard.shopadm;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.lamudi.phonefield.PhoneInputLayout;
import com.stepstone.stepper.Step;
import com.stepstone.stepper.VerificationError;

import java.util.Arrays;
import java.util.List;

import butterknife.Bind;
import id.co.kurindo.kurindo.R;
import id.co.kurindo.kurindo.app.AppConfig;
import id.co.kurindo.kurindo.helper.ShopAdmHelper;
import id.co.kurindo.kurindo.model.Address;
import id.co.kurindo.kurindo.model.Shop;
import id.co.kurindo.kurindo.model.TUser;
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

    String telepon;
    TUser pic;

    boolean editMode = false;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if(bundle != null)
            editMode = bundle.getBoolean("editMode");

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

        return v;
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

        shop.setPic(pic);
        shop.setAddress(pic.getAddress());

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
