package id.co.kurindo.kurindo.wizard.dowash;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.stepstone.stepper.Step;
import com.stepstone.stepper.VerificationError;

import butterknife.Bind;
import butterknife.OnClick;
import id.co.kurindo.kurindo.R;
import id.co.kurindo.kurindo.app.AppConfig;
import id.co.kurindo.kurindo.app.AppController;
import id.co.kurindo.kurindo.wizard.BaseStepFragment;

/**
 * Created by dwim on 8/6/2017.
 */

public class DoWashModeFragment extends BaseStepFragment implements Step {
    private static final String TAG = "DoSendFormFragment";
    VerificationError invalid = null;

    @Bind(R.id.btnLaundryKilon)
    Button btnLaundryKilon;
    @Bind(R.id.btnLaundrySatuan)
    Button btnLaundrySatuan;

    public static Fragment newInstance(String doType) {
        Bundle bundle = new Bundle();
        bundle.putString("doType", doType);
        DoWashModeFragment f = new DoWashModeFragment();
        f.setArguments(bundle);
        return f;
    }

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflateAndBind(inflater, container, R.layout.fragment_dowash_mode);

        return v;
    }

    @OnClick(R.id.btnLaundrySatuan)
    public void onBtnLaundrySatuan(){
        Intent intent = new Intent(AppController.applicationContext, DoWashSatuanActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("do_type", AppConfig.KEY_DOWASH);

        startActivity(intent, bundle);
    }
    @OnClick(R.id.btnLaundryKilon)
    public void onBtnLaundryKilon(){
        Intent intent = new Intent(AppController.applicationContext, DoWashActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("do_type", AppConfig.KEY_DOWASH);

        startActivity(intent, bundle);
    }

    @Override
    public int getName() {
        return 0;
    }

    @Override
    public VerificationError verifyStep() {
        return null;
    }

    @Override
    public void onSelected() {

    }

    @Override
    public void onError(@NonNull VerificationError error) {

    }
}
