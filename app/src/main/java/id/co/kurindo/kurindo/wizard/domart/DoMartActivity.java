
package id.co.kurindo.kurindo.wizard.domart;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.View;

import com.stepstone.stepper.adapter.AbstractStepAdapter;

import id.co.kurindo.kurindo.TOrderShowActivity;
import id.co.kurindo.kurindo.app.AppConfig;
import id.co.kurindo.kurindo.helper.DoMartHelper;
import id.co.kurindo.kurindo.notification.NewOrderPopupActivity;
import id.co.kurindo.kurindo.wizard.AbstractStepperActivity;

import static id.co.kurindo.kurindo.util.LogUtil.makeLogTag;

public class DoMartActivity extends AbstractStepperActivity {
    private static final String TAG = makeLogTag(DoMartActivity.class);
    int step = -1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    protected AbstractStepAdapter getStepperAdapter(int startingStepPosition) {
        stepAdapter = getStepperAdapter();
        mStepperLayout.setAdapter(stepAdapter, startingStepPosition);
        return stepAdapter;
    }

    protected AbstractStepAdapter getStepperAdapter() {
        stepAdapter = new MyStepperAdapter(getSupportFragmentManager());
        return stepAdapter;
    }
    @Override
    public void onStepSelected(int newStepPosition) {

        mCompleteNavigationButton.setText("Order "+ AppConfig.KEY_DOMART);
        step = newStepPosition;
    }

    @Override
    public void onCompleted(View completeButton) {
        showActivity(TOrderShowActivity.class);
        showActivity(NewOrderPopupActivity.class);
        DoMartHelper.getInstance().clearOrder();
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
                    return new DoMartForm1();
                case 1:
                    return new DoMartForm2();
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
