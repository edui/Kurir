package id.co.kurindo.kurindo;

import android.os.Bundle;

import id.co.kurindo.kurindo.base.BaseActivity;
import id.co.kurindo.kurindo.base.KurindoActivity;

/**
 * Created by dwim on 1/6/2017.
 */

public class LoginActivity extends KurindoActivity{
    @Override
    public boolean providesActivityToolbar() {
        return false;
    }

    @Override
    public int getLayout() {
        return R.layout.layout_base_no_toolbar;
    }

    @Override
    public Class getFragmentClass() {
        return TabLoginFragment.class;
    }

    @Override
    public Bundle getBundleParams() {
        return getIntent().getExtras();
    }


}
