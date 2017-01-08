package id.co.kurindo.kurindo.base;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.NavUtils;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.android.tonyvu.sc.model.Cart;
import com.android.tonyvu.sc.util.CartHelper;

import java.util.HashMap;

import id.co.kurindo.kurindo.R;
import id.co.kurindo.kurindo.ShoppingCartActivity;

/**
 * Created by DwiM on 12/12/2016.
 */

public abstract class BaseActivity extends AppCompatActivity {
    protected Toolbar actionBarToolbar;
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        setupToolbar();
    }

    protected ActionBar setupToolbar() {
        ActionBar ab =null;
        if(providesActivityToolbar()){
            ab = getActionBarToolbar();
            ab.setHomeAsUpIndicator(R.drawable.ic_menu);
            ab.setDisplayHomeAsUpEnabled(true);
        }
        return ab;
    }

    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }
    /**
     * Provides the action bar instance.
     * @return the action bar.
     */
    protected ActionBar getActionBarToolbar() {
        if (actionBarToolbar == null) {
            actionBarToolbar = (Toolbar) findViewById(R.id.toolbar);
            if (actionBarToolbar != null) {
                actionBarToolbar.setLogo(R.drawable.logo_kurindo);
                actionBarToolbar.setTitle("");
                setSupportActionBar(actionBarToolbar);
            }
        }
        return getSupportActionBar();
    }


    public  void showFragment(Class fragmentClass, int resId){
        Fragment fragment = null;
        try {
            fragment = (Fragment) fragmentClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Insert the fragment by replacing any existing fragment
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(resId, fragment).commit();
    }
    public  void showFragment(Class fragmentClass, Bundle params){
        Fragment fragment = null;
        try {
            fragment = (Fragment) fragmentClass.newInstance();
            fragment.setArguments(params);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Insert the fragment by replacing any existing fragment
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace( R.id.containerView, fragment).commit();
    }

    public  void showFragment(Class fragmentClass, Bundle params, int resId){
        Fragment fragment = null;
        try {
            fragment = (Fragment) fragmentClass.newInstance();
            fragment.setArguments(params);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Insert the fragment by replacing any existing fragment
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace( resId, fragment).commit();
    }

    public  void showFragment(Class fragmentClass){
        Fragment fragment = null;
        try {
            fragment = (Fragment) fragmentClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Insert the fragment by replacing any existing fragment
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace( R.id.containerView, fragment).commit();
    }
    public void showActivity(Class fragmentClass){
        Intent intent = new Intent(getApplicationContext(), fragmentClass);
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivityForResult(intent, 0);
    }
    public void showActivity(Class activityClass, Bundle params){
        Intent intent = new Intent(getApplicationContext(), activityClass);
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        intent.putExtras(params);
        startActivityForResult(intent, 0);
    }

    public abstract boolean providesActivityToolbar();

    public void setToolbar(Toolbar toolbar) {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
}


