package id.co.kurindo.kurindo;

import android.content.Intent;
import android.os.Bundle;

import id.co.kurindo.kurindo.app.AppConfig;
import id.co.kurindo.kurindo.base.KurindoBaseDrawerActivity;

public class MainDrawerActivity extends KurindoBaseDrawerActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        showFragment(TabFragment.class);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        boolean autoLogin = session.isAutoLoggedIn(); //autoLogin=false;
        session.setLogin(autoLogin);
        finish();
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