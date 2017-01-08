package id.co.kurindo.kurindo;

import android.os.Bundle;

import id.co.kurindo.kurindo.base.KurindoBaseDrawerActivity;

/**
 * Created by aspire on 11/25/2016.
 */

public class CartDrawerActivity extends KurindoBaseDrawerActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        showFragment(ShoppingCartFragment.class);
    }
}