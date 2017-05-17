package id.co.kurindo.kurindo;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import id.co.kurindo.kurindo.app.AppConfig;
import id.co.kurindo.kurindo.base.KurindoBaseDrawerActivity;

public class MainDrawerActivity extends KurindoBaseDrawerActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        showFragment(TabFragment.class);
    }

    protected int counter = 0;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        boolean autoLogin = session.isAutoLoggedIn(); //autoLogin=false;
        session.setLogin(autoLogin);
        finish();

        /*
        if(counter == 0){
            DialogInterface.OnClickListener YesClickListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    boolean autoLogin = session.isAutoLoggedIn(); //autoLogin=false;
                    session.setLogin(autoLogin);
                    finish();
                }
            };
            DialogInterface.OnClickListener NoClickListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    counter++;
                }
            };
            showConfirmationDialog("Exit","Yakin anda akan keluar dari aplikasi ?", YesClickListener, NoClickListener);
        }else{
            boolean autoLogin = session.isAutoLoggedIn(); //autoLogin=false;
            session.setLogin(autoLogin);
            finish();
        }
        */
    }

    @Override
    protected int getSelfNavDrawerItem() {
        return R.id.nav_item_home;
    }

    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (intent.hasExtra("click_action")) {
            AppConfig.startActivity(intent.getStringExtra("click_action"), intent.getExtras(), this);
        }

    }
}