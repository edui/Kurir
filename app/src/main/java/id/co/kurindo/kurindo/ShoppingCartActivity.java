package id.co.kurindo.kurindo;

import android.os.Bundle;

import id.co.kurindo.kurindo.base.KurindoActivity;

public class ShoppingCartActivity extends KurindoActivity {
    private static final String TAG = "ShoppingCartActivity";

    @Override
    public boolean providesActivityToolbar() {
        return true;
    }

    @Override
    public Class getFragmentClass() {
        return ShoppingCartFragment.class;
    }

    @Override
    public Bundle getBundleParams() {
        return getIntent().getExtras();
    }
}
