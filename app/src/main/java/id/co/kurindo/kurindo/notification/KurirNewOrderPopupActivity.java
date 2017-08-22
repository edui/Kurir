package id.co.kurindo.kurindo.notification;

import android.os.Bundle;
import android.view.WindowManager;

import id.co.kurindo.kurindo.base.KurindoActivity;

/**
 * Created by dwim on 8/5/2017.
 */

public class KurirNewOrderPopupActivity extends NewOrderPopupActivity {

    @Override
    public Class getFragmentClass() {
        return KurirNewOrderPopupFragment.class;
    }
}
