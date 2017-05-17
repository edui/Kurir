package id.co.kurindo.kurindo.wizard.docar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.View;

import com.stepstone.stepper.adapter.AbstractStepAdapter;

import id.co.kurindo.kurindo.helper.DoCarHelper;
import id.co.kurindo.kurindo.wizard.AbstractStepperActivity;

/**
 * Created by DwiM on 5/15/2017.
 */

public class DoCarRentalActivity extends AbstractStepperActivity {
    private static final String TAG = "DoCarRentalActivity";

    @Override
    public void onStepSelected(int newStepPosition) {
        super.onStepSelected(newStepPosition);
        mCompleteNavigationButton.setText("Pesan Mobil");

    }

    @Override
    public void onCompleted(View completeButton) {
        //DoCarHelper.getInstance().clearAll();
        finish();
    }

    @Override
    public boolean providesActivityToolbar() {
        return true;
    }

    @Override
    protected AbstractStepAdapter getStepperAdapter() {
        return new MyStepperAdapter(getSupportFragmentManager());
    }

    private static class MyStepperAdapter extends AbstractStepAdapter {
        MyStepperAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment createStep(int position) {
            switch (position) {
                case 0:
                    return new DoCarForm5();
                case 1:
                    return new DoCarForm6();
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
