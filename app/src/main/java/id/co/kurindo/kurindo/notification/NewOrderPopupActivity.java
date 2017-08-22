package id.co.kurindo.kurindo.notification;

import android.os.Bundle;
import android.view.WindowManager;

import id.co.kurindo.kurindo.app.AppConfig;
import id.co.kurindo.kurindo.base.KurindoActivity;

/**
 * Created by dwim on 8/5/2017.
 */

public class NewOrderPopupActivity extends KurindoActivity {


    @Override
    public boolean providesActivityToolbar() {
        return false;
    }

    @Override
    public Class getFragmentClass() {
        return NewOrderPopupFragment.class;
    }

    @Override
    public Bundle getBundleParams() {
        return getIntent().getExtras();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        int flags = WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_FULLSCREEN;

        getWindow().addFlags(flags);

        AppConfig.taskID = getTaskId();
        //setContentView(R.layout.activity_notif_order_search);
        //ButterKnife.bind(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        AppConfig.taskID = 0;
    }
}
