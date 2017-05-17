package id.co.kurindo.kurindo.wizard.docar;

import android.os.Bundle;

import id.co.kurindo.kurindo.base.KurindoActivity;

/**
 * Created by DwiM on 4/11/2017.
 */

public class DoCarActivity extends KurindoActivity {
    @Override
    public Class getFragmentClass() {
        return DoCarForm1.class;
    }

    @Override
    public Bundle getBundleParams() {
        return getIntent().getExtras();
    }
}
