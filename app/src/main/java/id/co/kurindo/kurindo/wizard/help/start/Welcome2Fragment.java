package id.co.kurindo.kurindo.wizard.help.start;

import android.support.annotation.NonNull;

import com.stepstone.stepper.Step;
import com.stepstone.stepper.VerificationError;

import id.co.kurindo.kurindo.wizard.BaseStepFragment;

/**
 * Created by DwiM on 5/16/2017.
 */

public class Welcome2Fragment extends BaseStepFragment implements Step {
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
