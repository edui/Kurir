package id.co.kurindo.kurindo.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import id.co.kurindo.kurindo.app.AppController;
import id.co.kurindo.kurindo.util.AndroidUtilities;
import id.co.kurindo.kurindo.util.LogUtil;

/**
 * Created by dwim on 7/31/2017.
 */

public class AppStartReceiver extends BroadcastReceiver {
    private static final String TAG = AppStartReceiver.class.getSimpleName();

    public void onReceive(Context context, Intent intent) {
        LogUtil.logD(TAG, "onReceive");
        /*AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public void run() {
                AppController.startPushService();
            }
        });*/
    }
}