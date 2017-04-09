package id.co.kurindo.kurindo.wizard.dosend;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.View;

import com.stepstone.stepper.adapter.AbstractStepAdapter;

import id.co.kurindo.kurindo.R;
import id.co.kurindo.kurindo.TOrderShowActivity;
import id.co.kurindo.kurindo.app.AppConfig;
import id.co.kurindo.kurindo.helper.DoSendHelper;
import id.co.kurindo.kurindo.wizard.AbstractStepperActivity;

/**
 * Created by dwim on 2/7/2017.
 */

public class DoSendOrderActivity extends AbstractStepperActivity {
    private static final String TAG = "DoSendOrderActivity";
    int step = -1;
    String doType = AppConfig.KEY_DOSEND;

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
        ((DoSendPinLocationMapFragment)DoSendPinLocationMapFragment.getInstance()).resetAll();
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
                    return DoSendPinLocationMapFragment.newInstance(doType);
                case 1:
                    return new DoSendFormFragment();
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
            if(f instanceof DoSendPinLocationMapFragment){
                if(!((DoSendPinLocationMapFragment) DoSendPinLocationMapFragment.getInstance()).handleBackPressed()) {
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
            if(f instanceof DoSendPinLocationMapFragment){
                if(!((DoSendPinLocationMapFragment) DoSendPinLocationMapFragment.getInstance()).handleBackPressed()) {
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
