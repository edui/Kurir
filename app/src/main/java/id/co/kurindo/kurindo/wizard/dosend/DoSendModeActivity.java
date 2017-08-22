package id.co.kurindo.kurindo.wizard.dosend;

import android.os.Bundle;

import id.co.kurindo.kurindo.base.KurindoActivity;

/**
 * Created by dwim on 8/6/2017.
 */

public class DoSendModeActivity extends KurindoActivity {
    @Override
    public Class getFragmentClass() {
        return DoSendModeFragment.class;
    }

    @Override
    public Bundle getBundleParams() {
        return null;
    }
}
