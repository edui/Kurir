
package id.co.kurindo.kurindo.wizard.signup;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.View;

import com.stepstone.stepper.adapter.AbstractStepAdapter;

import id.co.kurindo.kurindo.SignupFragment;
import id.co.kurindo.kurindo.wizard.AbstractStepperActivity;
import id.co.kurindo.kurindo.wizard.shopadm.AddShopForm1;
import id.co.kurindo.kurindo.wizard.shopadm.AddShopForm2;
import id.co.kurindo.kurindo.wizard.shopadm.AddShopForm3;

public class SignupWizardActivity extends AbstractStepperActivity {
    private static final String TAG = "SignupWizardActivity";
    int step = -1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected AbstractStepAdapter getStepperAdapter() {
        return new MyStepperAdapter(getSupportFragmentManager());
    }
    protected AbstractStepAdapter getStepperAdapter(int startingStepPosition) {
        stepAdapter = getStepperAdapter();
        mStepperLayout.setAdapter(stepAdapter, startingStepPosition);
        return stepAdapter;
    }
    @Override
    public void onStepSelected(int newStepPosition) {
        mCompleteNavigationButton.setText("Set HomeBase");
        step = newStepPosition;
    }

    @Override
    public void onCompleted(View completeButton) {
        //setResult(RESULT_OK);
        finish();
    }

    @Override
    public boolean providesActivityToolbar() {
        return true;
    }

    private static class MyStepperAdapter extends AbstractStepAdapter {

        MyStepperAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment createStep(int position) {
            switch (position) {
                case 0:
                    return new SignupAddressForm();
                default:
                    throw new IllegalArgumentException("Unsupported position: " + position);
            }
        }

        @Override
        public int getCount() {
            return 1;
        }
    }
}
