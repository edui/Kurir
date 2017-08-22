package id.co.kurindo.kurindo;

import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import id.co.kurindo.kurindo.base.KurindoActivity;

/**
 * Created by aspire on 11/21/2016.
 */

public class SettingsActivity extends KurindoActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
    }

    @Override
    public Class getFragmentClass() {
        return null;
    }

    @Override
    public Bundle getBundleParams() {
        return null;
    }

    public static class SettingsFragment extends PreferenceFragment {
        public SettingsFragment() {}

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.settings_prefs);
        }
    }

}
