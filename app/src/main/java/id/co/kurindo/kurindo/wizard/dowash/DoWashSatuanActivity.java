
package id.co.kurindo.kurindo.wizard.dowash;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.View;

import com.stepstone.stepper.adapter.AbstractStepAdapter;

import id.co.kurindo.kurindo.TOrderShowActivity;
import id.co.kurindo.kurindo.app.AppConfig;
import id.co.kurindo.kurindo.helper.DoServiceHelper;
import id.co.kurindo.kurindo.wizard.AbstractStepperActivity;

public class DoWashSatuanActivity extends AbstractStepperActivity {
    private static final String TAG = "DoWashSatuanActivity";
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

        mCompleteNavigationButton.setText("Order "+ AppConfig.KEY_DOWASH);

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
                    return new DoWashAddressForm();
                case 1:
                    return DoWashSatuanForm.newInstance();
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
        Fragment f = getStepperAdapter().getItem(step);
        if(f != null){
            if(f instanceof DoWashAddressForm){
                DoServiceHelper.getInstance().clearOrder();
                super.onBackPressed();
                finish();
                return true;
            }else if(f instanceof DoWashForm1){
                super.onBackPressed();
                return true;
            }else{
                return super.onSupportNavigateUp();
            }
        }
        return false;
    }


    @Override
    public void onBackPressed() {
        //LogUtil.logD(TAG,"@@@@@@ back stack entry count : " + getSupportFragmentManager().getBackStackEntryCount());
        Fragment f = getStepperAdapter().getItem(step);
        if(f != null){
            if(f instanceof DoWashAddressForm){
                DoServiceHelper.getInstance().clearOrder();
                super.onBackPressed();
                finish();
            }else{
                super.onBackPressed();
            }
        }
    }
}
