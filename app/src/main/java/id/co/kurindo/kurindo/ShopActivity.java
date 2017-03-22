package id.co.kurindo.kurindo;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;

import id.co.kurindo.kurindo.base.BaseActivity;
import id.co.kurindo.kurindo.base.KurindoActivity;
import id.co.kurindo.kurindo.base.KurindoBaseDrawerActivity;
import id.co.kurindo.kurindo.model.Shop;


public class ShopActivity extends KurindoActivity {
/*
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop);

        // Show the Up button in the action bar.
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        //Bundle data = getIntent().getBundleExtra("data");
        //Shop shop = (Shop) data.getSerializable("shop");
        Shop shop = getIntent().getParcelableExtra("shop");
        ShopDetailFragment fragment =  ShopDetailFragment.newInstance(shop);

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.shop_detail_container, fragment).commit();
    }
*/
    public boolean providesActivityToolbar() {
        return true;
    }

    @Override
    public Class getFragmentClass() {
        Bundle bundle = getIntent().getExtras();
        boolean viewCity = bundle.getBoolean("viewCity");
        if(viewCity) return ShopCityTabFragment.class;

        return ShopDetailFragment.class;
    }

    public Bundle getBundleParams() {
        return getIntent().getExtras();
    }

}
