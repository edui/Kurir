package id.co.kurindo.kurindo.map;

import android.os.Bundle;

import id.co.kurindo.kurindo.base.KurindoActivity;
import id.co.kurindo.kurindo.wizard.dosend.KurirMapFragment;

/**
 * Created by DwiM on 7/4/2017.
 */

public class MapUserActivity extends KurindoActivity {
    @Override
    public Class getFragmentClass() {
        return KurirMapFragment.class;
    }

    @Override
    public Bundle getBundleParams() {
        return null;
    }
}
