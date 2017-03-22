package id.co.kurindo.kurindo;

import android.os.Bundle;

import id.co.kurindo.kurindo.base.KurindoActivity;

/**
 * Created by dwim on 3/12/2017.
 */

public class ShopListActivity extends KurindoActivity {
    @Override
    public Class getFragmentClass() {
        return ShopListFragment.class;
    }

    @Override
    public Bundle getBundleParams() {
        return null;
    }
}
