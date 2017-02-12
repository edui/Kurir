package id.co.kurindo.kurindo;

import android.os.Bundle;

import id.co.kurindo.kurindo.base.KurindoActivity;

/**
 * Created by DwiM on 11/12/2016.
 */
public class TOrderShowActivity extends KurindoActivity {
    private static final String TAG = "PacketShowActivity";

    @Override
    public Class getFragmentClass() {
        return TOrderShowFragment.class;
    }

    @Override
    public Bundle getBundleParams() {
        return getIntent().getExtras();
    }
}
