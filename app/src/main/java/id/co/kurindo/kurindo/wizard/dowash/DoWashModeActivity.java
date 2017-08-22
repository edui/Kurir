package id.co.kurindo.kurindo.wizard.dowash;

import android.os.Bundle;

import id.co.kurindo.kurindo.base.KurindoActivity;

/**
 * Created by dwim on 8/6/2017.
 */

public class DoWashModeActivity extends KurindoActivity {
    @Override
    public Class getFragmentClass() {
        return DoWashModeFragment.class;
    }

    @Override
    public Bundle getBundleParams() {
        return null;
    }
}
