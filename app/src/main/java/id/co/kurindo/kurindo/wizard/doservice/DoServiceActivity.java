
package id.co.kurindo.kurindo.wizard.doservice;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.View;

import com.stepstone.stepper.adapter.AbstractStepAdapter;

import id.co.kurindo.kurindo.TOrderShowActivity;
import id.co.kurindo.kurindo.helper.DoServiceHelper;
import id.co.kurindo.kurindo.wizard.AbstractStepperActivity;

public class DoServiceActivity extends AbstractStepperActivity {
    private static final String TAG = "DoServiceActivity";
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
        mCompleteNavigationButton.setText("Confirm Order");

        step = newStepPosition;
    }

    @Override
    public void onCompleted(View completeButton) {
        showActivity(TOrderShowActivity.class);
        DoServiceHelper.getInstance().clearOrder();
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
                    return new DoServiceForm2();
                case 1:
                    return new DoServiceAddressForm();
                default:
                    throw new IllegalArgumentException("Unsupported position: " + position);
            }
        }

        @Override
        public int getCount() {
            return 2;
        }
    }
}
