package id.co.kurindo.kurindo.wizard;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import id.co.kurindo.kurindo.R;
import id.co.kurindo.kurindo.base.BaseFragment;
import id.co.kurindo.kurindo.helper.SQLiteHandler;

/**
 * Created by DwiM on 12/5/2016.
 */

public class BaseStepFragment extends BaseFragment {
    protected Toolbar actionBarToolbar;
    protected void setupToolbar(View v) {
        final ActionBar ab = getActionBarToolbar(v);
        ab.setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_18dp);
        ab.setDisplayHomeAsUpEnabled(true);
    }
    protected ActionBar getActionBarToolbar(View v) {
        if (actionBarToolbar == null) {
            actionBarToolbar = (Toolbar) v.findViewById(R.id.toolbar);
            if (actionBarToolbar != null) {
                actionBarToolbar.setLogo(R.drawable.logo_kurindo);
                actionBarToolbar.setTitle("");
                ((AppCompatActivity) getActivity()).setSupportActionBar(actionBarToolbar);
            }
        }
        return ((AppCompatActivity) getActivity()).getSupportActionBar();
    }

}
