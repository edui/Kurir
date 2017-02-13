package id.co.kurindo.kurindo.base;

import android.content.DialogInterface;
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
import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

import id.co.kurindo.kurindo.R;
import id.co.kurindo.kurindo.ShoppingCartActivity;
import id.co.kurindo.kurindo.app.AppController;

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
    public void addRequest(final String tag_string_req, int method, String url, Response.Listener responseListener, Response.ErrorListener errorListener, final Map<String, String> params, final Map<String, String> headers){
        final StringRequest strReq = new StringRequest(method,url, responseListener, errorListener){
            protected Map<String, String> getParams() throws AuthFailureError {
                if(params == null) return super.getParams();
                return params;
            }
            public Map<String, String> getHeaders() throws AuthFailureError{
                if(headers == null) return super.getHeaders();
                return headers;
            }
        };
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    protected void showErrorDialog(String title, String message){
        android.support.v7.app.AlertDialog.Builder alertDialogBuilder = new android.support.v7.app.AlertDialog.Builder(getApplicationContext());

        // set title
        alertDialogBuilder.setTitle(title);
        alertDialogBuilder
                .setMessage(message)
                .setCancelable(true)
                .setPositiveButton("OK",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                    }
                });

        // create alert dialog
        android.support.v7.app.AlertDialog alertDialog = alertDialogBuilder.create();
        // show it
        alertDialog.show();
    }
}


