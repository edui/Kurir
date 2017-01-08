package id.co.kurindo.kurindo;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import id.co.kurindo.kurindo.base.BaseActivity;
import id.co.kurindo.kurindo.base.KurindoActivity;
import id.co.kurindo.kurindo.model.Product;

/**
 * Created by DwiM on 12/12/2016.
 */

public class SimpleOrderActivity extends KurindoActivity{

    @Override
    public boolean providesActivityToolbar() {
        return true;
    }

    @Override
    public Class getFragmentClass() {
        return SimpleOrderFragment.class;
    }

    @Override
    public Bundle getBundleParams() {
        return getIntent().getExtras();
    }
}
