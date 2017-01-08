package id.co.kurindo.kurindo;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;

import butterknife.ButterKnife;
import id.co.kurindo.kurindo.base.BaseActivity;

/**
 * Created by DwiM on 11/14/2016.
 */
public class MonitorPacketActivity extends BaseActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monitor_order);
        ButterKnife.bind(this);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        showFragment(MonitorPacketTabFragment.class);
    }

    @Override
    public boolean providesActivityToolbar() {
        return true;
    }

    @Override
    protected ActionBar setupToolbar() {
        ActionBar ab = super.setupToolbar();
        ab.setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_18dp);
        return  ab;
    }
}
