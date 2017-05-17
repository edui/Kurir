package id.co.kurindo.kurindo.wizard.domove;

import android.os.Bundle;

import id.co.kurindo.kurindo.base.KurindoActivity;
import id.co.kurindo.kurindo.wizard.docar.DoCarForm1;

/**
 * Created by DwiM on 4/11/2017.
 */

public class DoMoveActivity extends KurindoActivity {
    @Override
    public Class getFragmentClass() {
        return DoMoveForm1.class;
    }

    @Override
    public Bundle getBundleParams() {
        return getIntent().getExtras();
    }
}
