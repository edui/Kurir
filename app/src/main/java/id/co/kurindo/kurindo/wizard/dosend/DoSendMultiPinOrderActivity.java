package id.co.kurindo.kurindo.wizard.dosend;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.View;

import com.stepstone.stepper.adapter.AbstractStepAdapter;

import id.co.kurindo.kurindo.TOrderShowActivity;
import id.co.kurindo.kurindo.app.AppConfig;
import id.co.kurindo.kurindo.helper.DoSendHelper;
import id.co.kurindo.kurindo.util.LogUtil;
import id.co.kurindo.kurindo.wizard.AbstractStepperActivity;

/**
 * Created by dwim on 2/7/2017.
 */

public class DoSendMultiPinOrderActivity extends AbstractStepperActivity {
    private static final String TAG = "DoSendOrderActivity";
    int step = -1;
    String doType = AppConfig.KEY_DOSEND;
    String doMoveType = AppConfig.DOMOVE_PICKUP_BAK;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Bundle bundle = getIntent().getExtras();
        if(bundle != null) {
            doType = bundle.getString("do_type");
            doMoveType = bundle.getString("domove_type");

        }
        DoSendHelper.getInstance().clearAll();

        DoSendHelper.getInstance().setDoType(doType);
        DoSendHelper.getInstance().setDoMoveType(doMoveType);
        super.onCreate(savedInstanceState);
    }

    protected AbstractStepAdapter getStepperAdapter(int startingStepPosition) {
        stepAdapter = getStepperAdapter();
        mStepperLayout.setAdapter(stepAdapter, startingStepPosition);
        return stepAdapter;
    }
    @Override
    protected AbstractStepAdapter getStepperAdapter() {
        return new MyStepperAdapter(getSupportFragmentManager(), doType);
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
                    return DoSendMultiPinLocationMapFragment.newInstance(doType);
                case 1:
                    return DoSendMultiPinFormFragment.newInstance(doType);
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
            if(f instanceof DoSendMultiPinLocationMapFragment){
                if(!((DoSendMultiPinLocationMapFragment) DoSendMultiPinLocationMapFragment.getInstance()).handleBackPressed()) {
                    DoSendHelper.getInstance().clearAll();
                    return super.onSupportNavigateUp();
                }
            }else if(f instanceof DoSendFormFragment){
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
        Fragment f = getStepperAdapter().getItem(step);
        if(f != null){
            if(f instanceof DoSendMultiPinLocationMapFragment){
                if(!((DoSendMultiPinLocationMapFragment) DoSendMultiPinLocationMapFragment.getInstance()).handleBackPressed()) {
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
