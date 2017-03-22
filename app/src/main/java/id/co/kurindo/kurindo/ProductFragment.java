package id.co.kurindo.kurindo;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatButton;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.tonyvu.sc.model.Cart;
import com.android.tonyvu.sc.util.CartHelper;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.DefaultSliderView;

import java.util.HashMap;

import butterknife.Bind;
import id.co.kurindo.kurindo.app.AppConfig;
import id.co.kurindo.kurindo.app.AppController;
import id.co.kurindo.kurindo.base.BaseFragment;
import id.co.kurindo.kurindo.helper.DoShopHelper;
import id.co.kurindo.kurindo.helper.ViewHelper;
import id.co.kurindo.kurindo.model.Address;
import id.co.kurindo.kurindo.model.Product;
import id.co.kurindo.kurindo.model.Shop;
import id.co.kurindo.kurindo.util.DummyContent;

/**
 * Created by DwiM on 11/9/2016.
 */
public class ProductFragment extends BaseFragment {
    private String TAG = "ProductFragment";
    Product product;
    Shop shop;

    @Bind(R.id.tvProductName)TextView tvProductName;
    @Bind(R.id.tvProductDesc) TextView tvProductDesc;
    @Bind(R.id.ivProductImage)
    ImageView ivProductImage;
    //@Bind(R.id.spQuantity) Spinner spQuantity;
    @Bind(R.id.bOrder)
    AppCompatButton bOrder;
    @Bind(R.id.quantityStr) TextView quantityStr;
    @Bind(R.id.incrementBtn) AppCompatButton incrementBtn;
    @Bind(R.id.decrementBtn) AppCompatButton decrementBtn;

    @Bind(R.id.tvProductPrice) TextView tvProductPrice;
    @Bind(R.id.tvProductStatus) TextView tvProductStatus;
    @Bind(R.id.tvProductPromotion) TextView tvProductPromotion;
    @Bind(R.id.ivSdsSupport) ImageView ivSdsSupport;
    @Bind(R.id.ivNdsSupport) ImageView ivNdsSupport;
    @Bind(R.id.input_special_request) EditText inputSpecialRequest;

    @Bind(R.id.slider1) SliderLayout sliderShow1;
    @Bind(R.id.progress) ProgressBar progressBar;

    boolean viewOnly = false;
    boolean editMode = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = getArguments();
        if(bundle != null){
            editMode = bundle.getBoolean("editMode");
            viewOnly = bundle.getBoolean("viewOnly");
        }
        /*
        product = getArguments().getParcelable("product");
        try {
            shop = getArguments().getParcelable("shop");
        }catch (Exception e){}
        */
        product = ViewHelper.getInstance().getProduct();
        shop = ViewHelper.getInstance().getShop();

        if(shop == null){
            String shopid = getArguments().getString("shopid");
            shop = DummyContent.SHOP_MAP.get(shopid);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflateAndBind(inflater, container, R.layout.product_fragment);

        //Retrieve views
        retrieveViews();

        //Set product properties
        setProductProperties();

        //Initialize quantity
        //initializeQuantity();

        HashMap<String, String > params = new HashMap<>();
        params.put("form-user", db.getUserPhone());
        params.put("form-type", "PRODUCT"+(editMode?"-ADM":""));
        params.put("form-tag", product.getCode());
        params.put("form-activity", "View "+product.getName());
        Address addr = ViewHelper.getInstance().getLastAddress();
        params.put("form-lat", (addr.getLocation()==null? "0" : ""+addr.getLocation().latitude) );
        params.put("form-lng", (addr.getLocation() == null ? "0" : ""+addr.getLocation().longitude));
        addRequest("req_logger", Request.Method.POST, AppConfig.URL_LOGGING, new Response.Listener() {
            @Override
            public void onResponse(Object o) {
                Log.d(TAG, "req_logger Response: " + o.toString());

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                volleyError.printStackTrace();
            }
        }, params, getKurindoHeaders());


        return  view;
    }

    private void retrieveViews() {
        if( (product == null || product.getQuantity() ==0) || shop.getStatus()==null || shop.getStatus().equalsIgnoreCase(AppConfig.CLOSED) || viewOnly){
            bOrder.setBackgroundColor(Color.GRAY );
            bOrder.setEnabled(false);
            incrementBtn.setEnabled(false);
            decrementBtn.setEnabled(false);
        }else{
            bOrder.setBackgroundResource(R.color.colorPrimary);
            bOrder.setEnabled(true);
            incrementBtn.setEnabled(true);
            decrementBtn.setEnabled(true);
        }
        incrementBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {increment(v);}
        });
        decrementBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                decrement(v);
            }
        });
        bOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Cart cart = CartHelper.getCart();
                Log.d(TAG, "Adding product: " + product.getName());
                if(inputSpecialRequest.getText() != null && inputSpecialRequest.getText().length() > 0){
                    product.setNotes(inputSpecialRequest.getText().toString());
                }
                //cart.add(product, Integer.valueOf(spQuantity.getSelectedItem().toString()));
                cart.add(product, Integer.parseInt(quantityStr.getText().toString()));
                //Intent intent = new Intent(getActivity(), CartDrawerActivity.class);
                DoShopHelper.getInstance().addShop(shop);
                Intent intent = new Intent(getActivity(), ShoppingCartActivity.class);
                startActivity(intent);
            }
        });
    }

    private void setProductProperties() {
        tvProductName.setText(product.getName());
        tvProductDesc.setText(product.getDescription());
        tvProductPrice.setText(AppConfig.formatCurrency( product.getPrice().doubleValue() ));
        //*
        ivProductImage.setVisibility(View.VISIBLE);
        sliderShow1.setVisibility(View.GONE);
        progressBar.setVisibility(View.GONE);

        if(product.getImageName() !=null){
            int resId = this.getResources().getIdentifier(product.getImageName().substring(0,product.getImageName().length()-4), "drawable", this.getActivity().getPackageName());
            if(resId == 0){
                Glide.with(getContext()).load(AppConfig.urlShopImage(product.getImageName() ))
                        .thumbnail(0.5f)
                        .crossFade()
                        .placeholder(R.drawable.placeholder)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(ivProductImage);
            }else{
                ivProductImage.setImageResource(resId);
            }
        }
        else{
            ivProductImage.setImageResource(product.getDrawable());
        }

        //*/


        //setup_slide();

    }

    private void setup_slide() {
        sliderShow1.setDuration(5000);
        sliderShow1.removeAllSliders();
        DefaultSliderView sliderView = new DefaultSliderView(getContext());
        sliderView.setOnImageLoadListener(new BaseSliderView.ImageLoadListener() {
            @Override
            public void onStart(BaseSliderView baseSliderView) {
                progressBar.setVisibility(View.VISIBLE);
            }

            @Override
            public void onEnd(boolean b, BaseSliderView baseSliderView) {
                progressBar.setVisibility(View.GONE);
            }
        });
        if(product.getImages() != null) {
            sliderShow1.setVisibility(View.VISIBLE);
            ivProductImage.setVisibility(View.GONE);
            for (int i = 0; i < product.getImages().size(); i++) {
                String s = (String) product.getImages().get(i);
                sliderView.image(AppConfig.urlShopImage(s)).setScaleType(BaseSliderView.ScaleType.FitCenter);
            }
        }
    }

    public void increment(View view){
        int q = 0;
        try{ q = Integer.parseInt(quantityStr.getText().toString());}catch (Exception e){};
        q++;
        quantityStr.setText(""+q);
    }
    public void decrement(View view){
        int q = 1;
        try{ q = Integer.parseInt(quantityStr.getText().toString());}catch (Exception e){};
        q--;
        if(q < 1) q =1;
        quantityStr.setText(""+q);
    }
    public static ProductFragment newInstance(Product product) {
        ProductFragment fragment = new ProductFragment();
        fragment.product = product;
        Bundle args = new Bundle();
        //args.putSerializable("product",product);
        args.putParcelable("product", product);
        fragment.setArguments(args);
        return fragment;
    }

}
