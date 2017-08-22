package id.co.kurindo.kurindo;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Service;
import android.os.Build;
import android.os.Bundle;

import java.util.ArrayList;
import java.util.List;

import id.co.kurindo.kurindo.app.AppConfig;
import id.co.kurindo.kurindo.app.AppController;
import id.co.kurindo.kurindo.base.KurindoActivity;

/**
 * Created by DwiM on 11/12/2016.
 */
public class TOrderShowActivity extends KurindoActivity {
    private static final String TAG = "PacketShowActivity";

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        boolean reset = false;
        Bundle bundle = getIntent().getExtras();
        if(bundle != null){
            try {
                reset = bundle.getBoolean("reset");
            }catch (Exception e){}
        }

        ActivityManager activityManager = (ActivityManager) AppController.applicationContext.getSystemService(Service.ACTIVITY_SERVICE);
        if(reset){
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                List<ActivityManager.AppTask> apps = activityManager.getAppTasks();
                if (apps.size() > 0) {
                    if (apps.get(0).getTaskInfo().id != AppConfig.taskID) {
                        for(ActivityManager.AppTask a : apps){
                            if(a.getTaskInfo().id == AppConfig.taskID) {
                                apps.remove(a);
                                break;
                            }
                        }
                    }
                }
            }else {
                List<ActivityManager.RecentTaskInfo> tasks = activityManager.getRecentTasks(20, 0);
                if (tasks.size() > 0) {
                    if (tasks.get(0).id != AppConfig.taskID) {
                        for (ActivityManager.RecentTaskInfo a : tasks) {
                            if (a.id == AppConfig.taskID) {
                                tasks.remove(a);
                                break;
                            }
                        }
                    }
                }
            }
        }else{
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                List<ActivityManager.AppTask> apps = activityManager.getAppTasks();
                if (apps.size() > 0) {
                    if (apps.get(0).getTaskInfo().id != AppConfig.taskID) {
                        activityManager.moveTaskToFront(AppConfig.taskID, 0);
                    }
                }
            }else{
                List<ActivityManager.RecentTaskInfo> tasks = activityManager.getRecentTasks(20, 0);
                if (tasks.size() > 0) {
                    if (tasks.get(0).id != AppConfig.taskID) {
                        activityManager.moveTaskToFront(AppConfig.taskID, 0);
                    }
                }
            }
        }
    }

    @Override
    public Class getFragmentClass() {
        return TOrderShowFragment.class;
    }

    @Override
    public Bundle getBundleParams() {
        return getIntent().getExtras();
    }
}
