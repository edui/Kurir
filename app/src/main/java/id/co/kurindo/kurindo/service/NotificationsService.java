package id.co.kurindo.kurindo.service;

/**
 * Created by dwim on 7/31/2017.
 */

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import id.co.kurindo.kurindo.util.LogUtil;

public class NotificationsService extends Service {
    private static final String TAG = NotificationsService.class.getSimpleName();
    @Override
    public void onCreate() {
        LogUtil.logD(TAG, "service started");
        //AppController.postInitApplication();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public void onDestroy() {
        LogUtil.logD(TAG, "service destroyed");

        //SharedPreferences preferences = AppController.applicationContext.getSharedPreferences("Notifications", MODE_PRIVATE);
        //if (preferences.getBoolean("pushService", true)) {
            Intent intent = new Intent("id.kurindo.start");
            sendBroadcast(intent);
        //}
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        onDestroy();
        super.onTaskRemoved(rootIntent);
    }
}