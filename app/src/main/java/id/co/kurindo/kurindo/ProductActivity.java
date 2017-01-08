package id.co.kurindo.kurindo;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.ImageView;
import com.android.tonyvu.sc.model.Cart;
import com.android.tonyvu.sc.util.CartHelper;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import butterknife.Bind;
import butterknife.ButterKnife;
import id.co.kurindo.kurindo.app.Constant;
import id.co.kurindo.kurindo.base.BaseActivity;
import id.co.kurindo.kurindo.base.KurindoActivity;
import id.co.kurindo.kurindo.model.Product;

public class ProductActivity extends KurindoActivity {
    private static final String TAG = "ProductActivity";

/*
    Product product;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_product);

        ButterKnife.bind(this);

        Bundle data = getIntent().getExtras();
        //product = (Product) data.getSerializable("product");
        product = (Product) data.getParcelable("product");

        ProductFragment fragment =  ProductFragment.newInstance(product);
        getSupportFragmentManager().beginTransaction().replace(R.id.containerView, fragment).commit();

        Log.d(TAG, "Product hashCode: " + product.hashCode());

    }
*/

    @Override
    public boolean providesActivityToolbar() {
        return true;
    }

    @Override
    public Class getFragmentClass() {
        return ProductFragment.class;
    }

    @Override
    public Bundle getBundleParams() {
        return getIntent().getExtras();
    }

}
