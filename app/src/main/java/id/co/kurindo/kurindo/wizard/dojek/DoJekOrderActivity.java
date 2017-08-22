package id.co.kurindo.kurindo.wizard.dojek;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.View;

import com.stepstone.stepper.adapter.AbstractStepAdapter;

import id.co.kurindo.kurindo.R;
import id.co.kurindo.kurindo.TOrderShowActivity;
import id.co.kurindo.kurindo.app.AppConfig;
import id.co.kurindo.kurindo.helper.DoSendHelper;
import id.co.kurindo.kurindo.notification.NewOrderPopupActivity;
import id.co.kurindo.kurindo.wizard.AbstractStepperActivity;
import id.co.kurindo.kurindo.wizard.docar.DoCarForm2;

/**
 * Created by dwim on 2/7/2017.
 */

public class DoJekOrderActivity extends AbstractStepperActivity {
    private static final String TAG = "DoJekOrderActivity";
    int step = -1;
    String doType = AppConfig.KEY_DOJEK;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Bundle bundle = getIntent().getExtras();
        if(bundle != null) doType = bundle.getString("do_type");
        DoSendHelper.getInstance().clearAll();

        DoSendHelper.getInstance().setDoType(doType);
        super.onCreate(savedInstanceState);
    }

    protected AbstractStepAdapter getStepperAdapter(int startingStepPosition) {
        stepAdapter = getStepperAdapter();
        mStepperLayout.setAdapter(stepAdapter, startingStepPosition);
        return stepAdapter;
    }

    protected AbstractStepAdapter getStepperAdapter() {
        stepAdapter = new MyStepperAdapter(getSupportFragmentManager(), doType);
        return stepAdapter;
    }

    @Override
    public void onStepSelected(int newStepPosition) {
        mCompleteNavigationButton.setText("Order "+doType);

        step = newStepPosition;
    }

    @Override
    public boolean providesActivityToolbar() {
        return true;
    }

    @Override
    public void onCompleted(View completeButton) {
        showActivity(TOrderShowActivity.class);
        showActivity(NewOrderPopupActivity.class);
        DoJekPinLocationMapFragment.getInstance().resetAll();
        finish();
    }

    public static class MyStepperAdapter extends AbstractStepAdapter {
        public String doType;
        public MyStepperAdapter(FragmentManager fm, String doType) {
            super(fm);
            this.doType = doType;
        }

        @Override
        public Fragment createStep(int position) {
            switch (position) {
                case 0:
                    return DoJekPinLocationMapFragment.newInstance(doType);
                case 1:
                    return DoJekFormFragment.newInstance(doType);
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
            if(f instanceof DoJekPinLocationMapFragment){
                if(!DoJekPinLocationMapFragment.getInstance().handleBackPressed()) {
                    DoSendHelper.getInstance().clearAll();
                    return super.onSupportNavigateUp();
                }
            }else if(f instanceof DoJekFormFragment){
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
        Log.d(TAG,"@@@@@@ back stack entry count : " + getSupportFragmentManager().getBackStackEntryCount());
        Fragment f = getStepperAdapter().getItem(step);
        if(f != null){
            if(f instanceof DoJekPinLocationMapFragment){
                if(!((DoJekPinLocationMapFragment) DoJekPinLocationMapFragment.getInstance()).handleBackPressed()) {
                    DoSendHelper.getInstance().clearAll();
                    super.onBackPressed();
                    finish();
                }
            }else{
                super.onBackPressed();
            }
        }
    }

}
