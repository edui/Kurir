package id.co.kurindo.kurindo.wizard.shopadm;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.stepstone.stepper.Step;
import com.stepstone.stepper.VerificationError;

import butterknife.Bind;
import butterknife.OnClick;
import id.co.kurindo.kurindo.R;
import id.co.kurindo.kurindo.app.AppConfig;
import id.co.kurindo.kurindo.helper.ShopAdmHelper;
import id.co.kurindo.kurindo.model.Shop;
import id.co.kurindo.kurindo.util.ImageLoadingUtils;
import id.co.kurindo.kurindo.wizard.BaseStepFragment;

import static android.app.Activity.RESULT_OK;
import static id.co.kurindo.kurindo.R.style.CustomDialog;

/**
 * Created by dwim on 3/15/2017.
 */

public class AddShopForm2 extends BaseStepFragment implements Step {
    private static final String TAG = "AddShopForm2";
    VerificationError invalid = null;
    ProgressDialog progressDialog;

    @Bind(R.id.imageViewBrand)
    ImageView imageViewBrand;

    @Bind(R.id.imageViewBackdrop)
    ImageView imageViewBackdrop;

    private int READ_EXTERNAL_STORAGE_REQUEST = 1;
    private int BRAND_PICK_IMAGE_REQUEST = 1;
    private int BACKDROP_PICK_IMAGE_REQUEST = 2;
    private Bitmap bitmapBrand;
    private Bitmap bitmapBackdrop;

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
        View v = inflateAndBind(inflater, container, R.layout.shopadd2_layout);

        progressDialog = new ProgressDialog(getActivity(), CustomDialog);

        imageViewBrand.setImageBitmap(ShopAdmHelper.getInstance().getBitmapBrand());
        imageViewBackdrop.setImageBitmap(ShopAdmHelper.getInstance().getBitmapBackdrop());
        return v;
    }

    @OnClick(R.id.buttonChooseBrand)
    public void buttonChooseBrand_onClick(){
        showFileChooser(BRAND_PICK_IMAGE_REQUEST);
    }

    @OnClick(R.id.buttonChooseBackdrop)
    public void buttonChooseBackdrop_onClick(){
        showFileChooser(BACKDROP_PICK_IMAGE_REQUEST);
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

        if (requestCode == BRAND_PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri filePath = data.getData();
            try {
                //Getting the Bitmap from Gallery
                //bitmapBrand = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), filePath);
                bitmapBrand = ImageLoadingUtils.compressImageToBitmap(filePath.getPath(), AppConfig.BRAND_MAX_WIDTH, AppConfig.BRAND_MAX_HEIGHT);
                //Setting the Bitmap to ImageView
                imageViewBrand.setImageBitmap(bitmapBrand);
                ShopAdmHelper.getInstance().setBitmapBrand(bitmapBrand);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }else
        if (requestCode == BACKDROP_PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri filePath = data.getData();
            try {
                //Getting the Bitmap from Gallery
                //bitmapBackdrop = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), filePath);
                bitmapBackdrop = ImageLoadingUtils.compressImageToBitmap(filePath.getPath(), AppConfig.BACKDROP_MAX_WIDTH, AppConfig.BACKDROP_MAX_HEIGHT);
                //Setting the Bitmap to ImageView
                imageViewBackdrop.setImageBitmap(bitmapBackdrop);
                ShopAdmHelper.getInstance().setBitmapBackdrop(bitmapBackdrop);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    @Override
    public int getName() {
        return R.string.shop_tools;
    }

    @Override
    public VerificationError verifyStep() {
        if(bitmapBrand ==null && bitmapBackdrop ==null) return new VerificationError("Pilih branding image toko Anda");

        ShopAdmHelper.getInstance().setBitmapBrand(bitmapBrand);
        ShopAdmHelper.getInstance().setBitmapBackdrop(bitmapBackdrop);

        return null;
    }

    @Override
    public void onSelected() {
        imageViewBrand.setImageBitmap(ShopAdmHelper.getInstance().getBitmapBrand());
        imageViewBackdrop.setImageBitmap(ShopAdmHelper.getInstance().getBitmapBackdrop());
        if(editMode){
            Shop s = ShopAdmHelper.getInstance().getShop();
            Glide.with(getContext()).load(AppConfig.urlShopImage(s.getBanner()))
                    .thumbnail(0.5f)
                    .crossFade()
                    .placeholder(R.drawable.placeholder)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(imageViewBrand);
            Glide.with(getContext()).load(AppConfig.urlShopImage(s.getBackdrop()))
                    .thumbnail(0.5f)
                    .crossFade()
                    .placeholder(R.drawable.placeholder)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(imageViewBackdrop);
        }
    }

    @Override
    public void onError(@NonNull VerificationError error) {

    }


    public static AddShopForm2 newInstance(boolean editMode) {
        Bundle args = new Bundle();
        args.putBoolean("editMode", editMode);
        AddShopForm2 fragment = new AddShopForm2();
        fragment.setArguments(args);
        return fragment;
    }

}
