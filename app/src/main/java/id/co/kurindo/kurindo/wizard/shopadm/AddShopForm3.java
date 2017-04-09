package id.co.kurindo.kurindo.wizard.shopadm;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.stepstone.stepper.Step;
import com.stepstone.stepper.VerificationError;

import org.json.JSONObject;

import java.util.HashMap;

import butterknife.Bind;
import id.co.kurindo.kurindo.R;
import id.co.kurindo.kurindo.app.AppConfig;
import id.co.kurindo.kurindo.helper.ShopAdmHelper;
import id.co.kurindo.kurindo.model.Shop;
import id.co.kurindo.kurindo.wizard.BaseStepFragment;

import static id.co.kurindo.kurindo.R.style.CustomDialog;

/**
 * Created by dwim on 3/15/2017.
 */

public class AddShopForm3 extends BaseStepFragment implements Step {
    private static final String TAG = "AddShopForm3";
    VerificationError invalid = null;
    ProgressDialog progressDialog;

    @Bind(R.id.imageViewBanner)
    ImageView imageViewBanner;

    @Bind(R.id.imageViewBackdrop)
    ImageView imageViewBackdrop;

    @Bind(R.id.tvShopName)
    TextView tvShopName;
    @Bind(R.id.tvShopDescription)
    TextView tvShopDescription;
    @Bind(R.id.tvShopContact)
    TextView tvShopContact;
    @Bind(R.id.tvShopAddress)
    TextView tvShopAddress;

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
        View v = inflateAndBind(inflater, container, R.layout.shopadd_summary_layout);

        progressDialog = new ProgressDialog(getActivity(), CustomDialog);

        return v;
    }

    @Override
    public int getName() {
        return R.string.confirmation;
    }

    @Override
    public VerificationError verifyStep() {
        final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message mesg) {
                throw new RuntimeException();
            }
        };

        DialogInterface.OnClickListener YesClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                add_shop(handler);
            }
        };
        showConfirmationDialog("Konfirmasi Data","Anda Yakin akan menambahkan Toko ini?", YesClickListener, null);

        //loop till a runtime exception is triggered.
        try { Looper.loop(); }
        catch(RuntimeException e2) {}

        return null;
    }

    private void add_shop(final Handler handler) {

        HashMap params= new HashMap();
        params.put("imageBanner",ShopAdmHelper.getInstance().getBitmapBrandString());
        params.put("imageBackdrop",ShopAdmHelper.getInstance().getBitmapBackdropString());
        params.put("shop",ShopAdmHelper.getInstance().getShopJson());

        Log.d("gson", ShopAdmHelper.getInstance().getShopJson() );

        String url = AppConfig.URL_SHOP_ADD;
        if(editMode){
            url = AppConfig.URL_SHOP_UPDATE;
        }
            addRequest("request_add_shop", Request.Method.POST, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Log.d(TAG, "request_add_shop Response: " + response.toString());
                    try {
                        JSONObject jObj = new JSONObject(response);
                        String message = jObj.getString("message");
                        if("OK".equalsIgnoreCase(message)){
                            getActivity().setResult(Activity.RESULT_OK);
                        }
                    }catch (Exception e){e.printStackTrace();}
                    handler.handleMessage(null);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError volleyError) {
                    volleyError.printStackTrace();
                    invalid = new VerificationError("NetworkError : " + volleyError.getMessage());
                    handler.handleMessage(null);
                }
            }, params, getKurindoHeaders());
    }

    @Override
    public void onSelected() {
        //progressDialog.show();
        //ImageLoadingUtils util = new ImageLoadingUtils(getContext());
        imageViewBanner.setImageBitmap(ShopAdmHelper.getInstance().getBitmapBrand());
        imageViewBackdrop.setImageBitmap(ShopAdmHelper.getInstance().getBitmapBackdrop());

        Shop shop = ShopAdmHelper.getInstance().getShop();
        if(shop != null){
            tvShopName.setText(shop.getName());
            tvShopDescription.setText(shop.getMotto());

            tvShopContact.setText(shop.getPic().toStringFormatted());
            tvShopAddress.setText(shop.getPic().getAddress().toStringFormatted());
        }
        //progressDialog.dismiss();
    }

    @Override
    public void onError(@NonNull VerificationError error) {

    }


    public static AddShopForm3 newInstance(boolean editMode) {
        Bundle args = new Bundle();
        args.putBoolean("editMode", editMode);
        AddShopForm3 fragment = new AddShopForm3();
        fragment.setArguments(args);
        return fragment;
    }
}
