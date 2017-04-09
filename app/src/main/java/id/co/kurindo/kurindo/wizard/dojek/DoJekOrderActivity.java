package id.co.kurindo.kurindo.wizard.dojek;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.stepstone.stepper.Step;
import com.stepstone.stepper.VerificationError;
import com.stepstone.stepper.adapter.AbstractStepAdapter;

import id.co.kurindo.kurindo.R;
import id.co.kurindo.kurindo.TOrderShowActivity;
import id.co.kurindo.kurindo.app.AppConfig;
import id.co.kurindo.kurindo.helper.DoSendHelper;
import id.co.kurindo.kurindo.wizard.AbstractStepperActivity;
import id.co.kurindo.kurindo.wizard.BaseStepFragment;

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


    @Override
    protected AbstractStepAdapter getStepperAdapter() {
        return new MyStepperAdapter(getSupportFragmentManager(), doType);
    }

    @Override
    public void onStepSelected(int newStepPosition) {
        if(newStepPosition == 1){
            mCompleteNavigationButton.setText("Confirm Order");
            //mNextNavigationButton.setBackgroundDrawable(getResources().getDrawable(R.drawable.ms_spesial_button_background));
        }else{
            mNextNavigationButton.setBackgroundDrawable(getResources().getDrawable(R.drawable.ms_default_button_background));
        }
        step = newStepPosition;
    }

    @Override
    public boolean providesActivityToolbar() {
        return true;
    }

    @Override
    public void onCompleted(View completeButton) {
        showActivity(TOrderShowActivity.class);
        ((DoJekPinLocationMapFragment)DoJekPinLocationMapFragment.getInstance()).resetAll();
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
                    return DoJekPinLocationMapFragment.newInstance(doType);
                case 1:
                    if(DoSendHelper.getInstance().getDoType().equalsIgnoreCase(AppConfig.KEY_DOCAR))
                        return new DoCarForm1();
                    else     return new DoJekFormFragment();
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
                if(!((DoJekPinLocationMapFragment) DoJekPinLocationMapFragment.getInstance()).handleBackPressed()) {
                    DoSendHelper.getInstance().clearAll();
                    return super.onSupportNavigateUp();
                }
            }else{
                return super.onSupportNavigateUp();
            }
        }
        return super.onSupportNavigateUp();
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
