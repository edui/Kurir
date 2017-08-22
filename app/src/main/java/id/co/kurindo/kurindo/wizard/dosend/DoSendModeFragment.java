package id.co.kurindo.kurindo.wizard.dosend;

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

public class DoSendModeFragment extends BaseStepFragment implements Step {
    private static final String TAG = "DoSendFormFragment";
    VerificationError invalid = null;

    @Bind(R.id.btnSatuAlamat)
    Button btnSatuAlamat;
    @Bind(R.id.btnBanyakAlamat)
    Button btnBanyakAlamat;

    public static Fragment newInstance(String doType) {
        Bundle bundle = new Bundle();
        bundle.putString("doType", doType);
        DoSendModeFragment f = new DoSendModeFragment();
        f.setArguments(bundle);
        return f;
    }

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflateAndBind(inflater, container, R.layout.fragment_dosend_mode);

        return v;
    }

    @OnClick(R.id.btnSatuAlamat)
    public void onBtnSatuAlamat(){
        Intent intent = new Intent(AppController.applicationContext, DoSendOrderActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("do_type", AppConfig.KEY_DOSEND);

        startActivity(intent, bundle);
    }
    @OnClick(R.id.btnBanyakAlamat)
    public void onBtnBanyakAlamat(){
        Intent intent = new Intent(AppController.applicationContext, DoSendMultiOrderActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("do_type", AppConfig.KEY_DOSEND);

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
