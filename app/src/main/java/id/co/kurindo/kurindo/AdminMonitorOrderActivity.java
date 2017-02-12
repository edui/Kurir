package id.co.kurindo.kurindo;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

import butterknife.ButterKnife;
import id.co.kurindo.kurindo.base.KurindoActivity;

/**
 * Created by DwiM on 11/14/2016.
 */
public class AdminMonitorOrderActivity extends KurindoActivity {

/*
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monitor_order);
        ButterKnife.bind(this);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        showFragment(AdminMonitorOrderTabFragment.class);
    }*/

    @Override
    public Class getFragmentClass() {
        return AdminMonitorTOrderTabFragment.class;
    }

    @Override
    public Bundle getBundleParams() {
        return null;
    }

}
