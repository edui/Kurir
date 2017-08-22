package id.co.kurindo.kurindo.notification;

import android.os.Bundle;

import id.co.kurindo.kurindo.base.KurindoActivity;

/**
 * Created by dwim on 8/10/2017.
 */

public class CancelOrderActivity  extends KurindoActivity{
    @Override
    public Class getFragmentClass() {
        return CancelOrderFragment.class;
    }

    @Override
    public Bundle getBundleParams() {
        return null;
    }


}
