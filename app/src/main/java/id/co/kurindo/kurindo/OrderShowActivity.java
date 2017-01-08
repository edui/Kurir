package id.co.kurindo.kurindo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import id.co.kurindo.kurindo.base.KurindoActivity;
import id.co.kurindo.kurindo.model.Packet;

/**
 * Created by DwiM on 11/12/2016.
 */
public class OrderShowActivity extends KurindoActivity {
    private static final String TAG = "PacketShowActivity";

    @Override
    public Class getFragmentClass() {
        return OrderShowFragment.class;
    }

    @Override
    public Bundle getBundleParams() {
        return getIntent().getExtras();
    }
}
