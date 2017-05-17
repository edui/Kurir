package id.co.kurindo.kurindo.wizard.help;

import android.os.Bundle;

import id.co.kurindo.kurindo.base.KurindoActivity;

/**
 * Created by DwiM on 5/12/2017.
 */

public class HowToUseActivity extends KurindoActivity{

    @Override
    public Class getFragmentClass() {
        return ContentListFragment.class;
    }

    @Override
    public Bundle getBundleParams() {
        return null;
    }
}
