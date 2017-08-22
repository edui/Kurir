package id.co.kurindo.kurindo.wizard.dosend;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.View;

import com.stepstone.stepper.adapter.AbstractStepAdapter;

import id.co.kurindo.kurindo.TOrderShowActivity;
import id.co.kurindo.kurindo.app.AppConfig;
import id.co.kurindo.kurindo.wizard.AbstractStepperActivity;

/**
 * Created by dwim on 8/8/2017.
 */

public class DoSendMultiOrderActivity extends AbstractStepperActivity {
    String doType = AppConfig.KEY_DOSEND;
    @Override
    protected AbstractStepAdapter getStepperAdapter() {
        return new MyStepperAdapter(getSupportFragmentManager(), doType);
    }

    @Override
    public boolean providesActivityToolbar() {
        return true;
    }

    @Override
    public void onCompleted(View completeButton) {
        showActivity(TOrderShowActivity.class);

        finish();
    }
    private static class MyStepperAdapter extends AbstractStepAdapter {
        String doType;
        MyStepperAdapter(FragmentManager fm, String doType) {
            super(fm);
            this.doType = doType;
        }

        @Override
        public Fragment createStep(int position) {
            switch (position) {
                case 0:
                    return DoSendMultiFormFragment.newInstance(doType);
                case 1:
                    return DoSendMultiFormFragment2.newInstance(doType);
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
