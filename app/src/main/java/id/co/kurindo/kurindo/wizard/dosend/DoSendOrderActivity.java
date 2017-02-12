package id.co.kurindo.kurindo.wizard.dosend;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.View;

import com.stepstone.stepper.adapter.AbstractStepAdapter;

import id.co.kurindo.kurindo.R;
import id.co.kurindo.kurindo.helper.OrderViaMapHelper;
import id.co.kurindo.kurindo.wizard.AbstractStepperActivity;

/**
 * Created by dwim on 2/7/2017.
 */

public class DoSendOrderActivity extends AbstractStepperActivity {
    private static final String TAG = "DoSendOrderActivity";

    @Override
    protected AbstractStepAdapter getStepperAdapter() {
        return new MyStepperAdapter(getSupportFragmentManager());
    }

    @Override
    public void onStepSelected(int newStepPosition) {
        if(newStepPosition == 0){
            mNextNavigationButton.setText("Confirm Order");
            mNextNavigationButton.setBackgroundDrawable(getResources().getDrawable(R.drawable.ms_spesial_button_background));
        }else{
            mNextNavigationButton.setBackgroundDrawable(getResources().getDrawable(R.drawable.ms_default_button_background));
        }

        if(newStepPosition == 1){
            mBackNavigationButton.setVisibility(View.GONE);
        }
    }

    @Override
    public boolean providesActivityToolbar() {
        return true;
    }

    @Override
    public void onCompleted(View completeButton) {
        super.onCompleted(completeButton);
        OrderViaMapHelper.getInstance().clearAll();
        finish();
    }

    private static class MyStepperAdapter extends AbstractStepAdapter {

        MyStepperAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment createStep(int position) {
            switch (position) {
                case 0:
                    return new DoSendFormFragment();
                case 1:
                    return new StepSummaryFragment();
                default:
                    throw new IllegalArgumentException("Unsupported position: " + position);
            }
        }

        @Override
        public int getCount() {
            return 2;
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        OrderViaMapHelper.getInstance().clearAll();
        return super.onSupportNavigateUp();
    }


    @Override
    public void onBackPressed() {
        OrderViaMapHelper.getInstance().clearAll();
        finish();
    }
}
