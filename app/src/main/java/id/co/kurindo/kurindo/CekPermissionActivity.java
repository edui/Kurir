package id.co.kurindo.kurindo;

import android.app.Application;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;

import id.co.kurindo.kurindo.app.AppController;
import id.co.kurindo.kurindo.helper.SessionManager;
import id.co.kurindo.kurindo.util.BaseUtil;
import id.co.kurindo.kurindo.util.LogUtil;
import id.co.kurindo.kurindo.util.MIUIHelper;

import static java.lang.Thread.sleep;

/**
 * Created by aspire on 3/28/2017.
 */

public class CekPermissionActivity extends AppCompatActivity{
    private static final String TAG = CekPermissionActivity.class.getSimpleName();

    private static final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION= 2;
    private static final int MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION= 3;
    boolean done =false;
    private SessionManager session;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        session = AppController.session;
        if (ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);

            ActivityCompat.requestPermissions(this,
                    new String[]{ android.Manifest.permission.ACCESS_COARSE_LOCATION},
                    MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION);

            Thread timer = new Thread(new Runnable() {
                @Override
                public void run() {
                    int c=0;
                    while(!done){
                        try {
                            sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        done =
                                ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;
                        if(c > 10) finish();
                        c++;
                    }
                    showScreen();
                }
            });
            timer.start();
            return;
        }

        if(!session.isLoggedIn()){
            if(!checkAutoStart()) return;
        }

        showScreen();
    }

    private boolean checkAutoStart() {
        Intent intent = null;
        //Intent intent  = BaseUtil.getAutoStartIntent(getApplicationContext());
        if(intent != null){
            startActivityForResult(intent, 1);
            return false;
        }
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LogUtil.logD(TAG, "onDestroy");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        LogUtil.logD(TAG, "onActivityResult "+requestCode +" "+resultCode);
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1){
            showScreen();
        }
    }

    private  void showScreen(){
        Intent intent = new Intent(getApplicationContext(), SplashScreenActivity.class);
        startActivity(intent);
        finish();
    }
}
