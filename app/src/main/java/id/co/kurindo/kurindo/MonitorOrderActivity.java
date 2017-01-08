package id.co.kurindo.kurindo;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

import butterknife.ButterKnife;
import id.co.kurindo.kurindo.base.BaseActivity;
import id.co.kurindo.kurindo.base.KurindoActivity;

/**
 * Created by DwiM on 11/14/2016.
 */
public class MonitorOrderActivity extends KurindoActivity {

    @Override
    public boolean providesActivityToolbar() {
        return true;
    }

    @Override
    public Class getFragmentClass() {
        return MonitorOrderTabFragment.class;
    }

    @Override
    public Bundle getBundleParams() {
        return getIntent().getExtras();
    }
}
