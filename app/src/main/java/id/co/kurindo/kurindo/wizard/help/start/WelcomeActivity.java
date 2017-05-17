package id.co.kurindo.kurindo.wizard.help.start;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.View;

import com.stepstone.stepper.adapter.AbstractStepAdapter;

import id.co.kurindo.kurindo.R;
import id.co.kurindo.kurindo.wizard.AbstractStepperActivity;

/**
 * Created by DwiM on 5/16/2017.
 */

public class WelcomeActivity extends AbstractStepperActivity {
    @Override
    protected AbstractStepAdapter getStepperAdapter() {
        return new MyStepperAdapter(getSupportFragmentManager());
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_styled_dot;
    }


    @Override
    public void onCompleted(View completeButton) {

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
                    return new Welcome1Fragment();
                case 1:
                    return new Welcome2Fragment();
                case 2:
                    return new Welcome3Fragment();
                default:
                    throw new IllegalArgumentException("Unsupported position: " + position);
            }
        }

        @Override
        public int getCount() {
            return 3;
        }
    }
}
