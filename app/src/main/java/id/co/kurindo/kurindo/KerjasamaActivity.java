package id.co.kurindo.kurindo;

import android.os.Bundle;

import id.co.kurindo.kurindo.base.KurindoActivity;

/**
 * Created by aspire on 12/20/2016.
 */

public class KerjasamaActivity extends KurindoActivity {
    @Override
    public Class getFragmentClass() {
        Bundle bundle = getIntent().getExtras();
        String clazz = bundle.getString("class");
        if(clazz.equalsIgnoreCase("kerjasama"))
            return KerjasamaFragment.class;
        else if(clazz.equalsIgnoreCase("aboutus"))
            return AboutUsFragment.class;
        else if(clazz.equalsIgnoreCase("snk"))
            return SnKFragment.class;
        else
            return KerjasamaFragment.class;
    }

    @Override
    public Bundle getBundleParams() {
        return null;
    }
}
