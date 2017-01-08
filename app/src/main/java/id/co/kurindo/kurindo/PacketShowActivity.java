package id.co.kurindo.kurindo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import id.co.kurindo.kurindo.base.BaseActivity;
import id.co.kurindo.kurindo.base.KurindoActivity;
import id.co.kurindo.kurindo.model.Packet;

/**
 * Created by DwiM on 11/12/2016.
 */
public class PacketShowActivity extends KurindoActivity{

    @Override
    public boolean providesActivityToolbar() {
        return true;
    }

    @Override
    public Class getFragmentClass() {
        return PacketShowFragment.class;
    }

    @Override
    public Bundle getBundleParams() {
        return getIntent().getExtras();
    }
}
