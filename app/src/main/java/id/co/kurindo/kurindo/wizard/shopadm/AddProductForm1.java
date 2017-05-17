package id.co.kurindo.kurindo.wizard.shopadm;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.stepstone.stepper.Step;
import com.stepstone.stepper.VerificationError;

import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.HashMap;

import butterknife.Bind;
import butterknife.OnClick;
import id.co.kurindo.kurindo.R;
import id.co.kurindo.kurindo.app.AppConfig;
import id.co.kurindo.kurindo.helper.ShopAdmHelper;
import id.co.kurindo.kurindo.helper.ViewHelper;
import id.co.kurindo.kurindo.model.Product;
import id.co.kurindo.kurindo.util.ImageLoadingUtils;
import id.co.kurindo.kurindo.util.LogUtil;
import id.co.kurindo.kurindo.wizard.BaseStepFragment;

import static android.app.Activity.RESULT_OK;
import static id.co.kurindo.kurindo.R.string.stock;
import static id.co.kurindo.kurindo.R.style.CustomDialog;

/**
 * Created by dwim on 3/15/2017.
 */

public class AddProductForm1 extends BaseStepFragment implements Step {
    private static final String TAG = "AddProductForm1";
    private static final int PRODUCT_PICK_IMAGE_REQUEST = 1;
    VerificationError invalid = null;
    boolean editMode = false;

    @Bind(R.id.input_product_name)
    EditText inputProductName;
    @Bind(R.id.input_product_desc1)
    EditText inputProductDesc1;
    @Bind(R.id.input_product_price)
    EditText inputProductPrice;
    @Bind(R.id.input_product_stock)
    EditText inputProductStock;
    @Bind(R.id.input_product_weight)
    EditText inputProductWeight;


    @Bind(R.id.btnAddImage)
    Button btnAddImage;
    @Bind(R.id.ivProductImage)
    ImageView productImage;

    @Bind(R.id.pilihTipe)
    Spinner pilihTipe;
    String tipeProduk = "P";

    @Bind(R.id.pilihKategori)
    Spinner pilihKategori;

    private int READ_EXTERNAL_STORAGE_REQUEST = 1;
    private Bitmap bitmapProduct;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if(bundle != null) editMode = bundle.getBoolean("editMode");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflateAndBind(inflater, container, R.layout.fragment_product_add1);

        return v;
    }
    private void setup_pilihan() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),R.array.tipe_product_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        pilihTipe.setAdapter(adapter);
        pilihTipe.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position){
                    case 0:
                        tipeProduk = "P";
                        break;
                    case 1:
                        tipeProduk = "A";
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        /*
        ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(getContext(),R.array.shop_categories_array, android.R.layout.simple_spinner_item);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        pilihKategori.setAdapter(adapter2);
        */

    }

    @OnClick(R.id.btnAddImage)
    public void btnAddImage_onClick(){
        showFileChooser(PRODUCT_PICK_IMAGE_REQUEST);
    }

    private void showFileChooser(int type) {
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ) {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    READ_EXTERNAL_STORAGE_REQUEST);

            return;
        }

        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), type);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PRODUCT_PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri filePath = data.getData();
            try {
                //Getting the Bitmap from Gallery
                //bitmapBrand = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), filePath);
                bitmapProduct = ImageLoadingUtils.compressImageToBitmap(filePath.getPath(), AppConfig.BRAND_MAX_WIDTH, AppConfig.BRAND_MAX_HEIGHT);
                //Setting the Bitmap to ImageView
                productImage.setImageBitmap(bitmapProduct);
                ShopAdmHelper.getInstance().setBitmapProduct(bitmapProduct);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public int getName() {
        return R.string.product_information;
    }

    @Override
    public VerificationError verifyStep() {
        boolean valid = true;

        if(!validate()){
            return new VerificationError("Form belum lengkap diisi.");
        }
        //if(bitmapProduct ==null ) return new VerificationError("Pilih branding image product Anda");

        BigDecimal weight = new BigDecimal( inputProductWeight.getText().toString() );
        String desc1 = inputProductDesc1.getText().toString();

        Product p = new Product();
        if(editMode){
            p = ShopAdmHelper.getInstance().getProduct();
        }
        p.setType(tipeProduk);
        p.setName(inputProductName.getText().toString());
        p.setDescription(desc1);
        p.setPrice(new BigDecimal( inputProductPrice.getText().toString() ));
        p.setQuantity(new BigDecimal( inputProductStock.getText().toString() ).intValue());
        p.setWeight(weight);
        if(ViewHelper.getInstance().getShop() != null){
            p.setShopid(ViewHelper.getInstance().getShop().getId());
        }

        ShopAdmHelper.getInstance().setProduct(p);
        ShopAdmHelper.getInstance().setBitmapProduct(bitmapProduct);
        ShopAdmHelper.getInstance().setShop(ViewHelper.getInstance().getShop());

        final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message mesg) {
                throw new RuntimeException();
            }
        };

        DialogInterface.OnClickListener YesClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                add_product(handler);
            }
        };
        String update_str = (editMode? "memperbarui" : "menambahkan");
        showConfirmationDialog("Konfirmasi Data","Anda Yakin akan "+update_str+" produk ini?", YesClickListener, null);

        //loop till a runtime exception is triggered.
        try { Looper.loop(); }
        catch(RuntimeException e2) {}
        return null;
    }

    private boolean validate() {
        boolean valid = true;
        String name = inputProductName.getText().toString();
        BigDecimal price = new BigDecimal( inputProductPrice.getText().toString() );
        BigDecimal stock = new BigDecimal( inputProductStock.getText().toString() );
        if(name.isEmpty()) {
            inputProductName.setError("Nama product harus diisi.");
            valid = false;
        }else {
            inputProductName.setError(null);
        }
        if(price.intValue() < 0){
            inputProductPrice.setError("Harga produk harus diisi angka.");
            valid = false;
        }else {
            inputProductPrice.setError(null);
        }
        if(stock.intValue() < 0){
            inputProductStock.setError("Stock produk harus diisi angka.");
            valid = false;
        }else {
            inputProductStock.setError(null);
        }

        return valid;
    }

    private void add_product(final Handler handler) {
        HashMap params= new HashMap();
        params.put("imageProduct",ShopAdmHelper.getInstance().getBitmapProductString());
        params.put("product",ShopAdmHelper.getInstance().getProductJson());

        //Log.d("product",ShopAdmHelper.getInstance().getProductJson());

        if(editMode){
            String url = AppConfig.URL_SHOP_PRODUCT_UPDATE;
            addRequest("request_update_product", Request.Method.POST, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    LogUtil.logD(TAG, "request_update_product Response: " + response.toString());
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
                    invalid = new VerificationError("VolleyError : " + volleyError.getMessage());
                    handler.handleMessage(null);
                }
            }, params, getKurindoHeaders());
        }else{
            String url = AppConfig.URL_SHOP_PRODUCT_ADD;
            addRequest("request_add_product", Request.Method.POST, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    LogUtil.logD(TAG, "request_add_product Response: " + response.toString());
                    try {
                        JSONObject jObj = new JSONObject(response);
                        String message = jObj.getString("message");
                        if("OK".equalsIgnoreCase(message)){
                            JSONObject data = jObj.getJSONObject("data");
                            String code = data.getString("code");
                            String image = data.getString("image");
                            ShopAdmHelper.getInstance().getProduct().setCode(code);
                            ShopAdmHelper.getInstance().getProduct().setImageName(image);
                            getActivity().setResult(Activity.RESULT_OK);
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
    }

    @Override
    public void onSelected() {
        setup_pilihan();
        if(editMode){
            Product p = ShopAdmHelper.getInstance().getProduct();
            inputProductName.setText(p.getName());
            inputProductDesc1.setText(p.getDescription());
            inputProductStock.setText(p.getQuantity());
            inputProductPrice.setText(p.getPrice().toString());
            inputProductWeight.setText(p.getWeight().toString());
            pilihTipe.setSelection((p.getType().equalsIgnoreCase("P")? 0 : 1));
        }
    }


    @Override
    public void onError(@NonNull VerificationError error) {

    }


    public static AddProductForm1 newInstance(boolean editMode) {
        Bundle args = new Bundle();
        args.putBoolean("editMode", editMode);
        AddProductForm1 fragment = new AddProductForm1();
        fragment.setArguments(args);
        return fragment;
    }
}
