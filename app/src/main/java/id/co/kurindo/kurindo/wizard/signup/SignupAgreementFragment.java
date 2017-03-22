package id.co.kurindo.kurindo.wizard.signup;

import android.os.Bundle;
import android.support.annotation.NonNull;

import com.stepstone.stepper.Step;
import com.stepstone.stepper.VerificationError;

import id.co.kurindo.kurindo.R;
import id.co.kurindo.kurindo.wizard.BaseStepFragment;

/**
 * Created by dwim on 3/15/2017.
 */

public class SignupAgreementFragment extends BaseStepFragment implements Step {
    private static final String TAG = "SignupAgreementFragment";
    VerificationError invalid = null;
    boolean editMode = false;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if(bundle != null) editMode = bundle.getBoolean("editMode");
    }

    @Override
    public int getName() {
        return R.string.end_user_agreement;
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

    public static SignupAgreementFragment newInstance(boolean editMode) {
        Bundle args = new Bundle();
        args.putBoolean("editMode", editMode);
        SignupAgreementFragment fragment = new SignupAgreementFragment();
        fragment.setArguments(args);
        return fragment;
    }
}
