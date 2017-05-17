package id.co.kurindo.kurindo.wizard.docar;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.View;

import com.stepstone.stepper.adapter.AbstractStepAdapter;

import id.co.kurindo.kurindo.R;
import id.co.kurindo.kurindo.TOrderShowActivity;
import id.co.kurindo.kurindo.app.AppConfig;
import id.co.kurindo.kurindo.helper.DoCarHelper;
import id.co.kurindo.kurindo.helper.DoSendHelper;
import id.co.kurindo.kurindo.wizard.AbstractStepperActivity;
import id.co.kurindo.kurindo.wizard.dojek.DoJekFormFragment;
import id.co.kurindo.kurindo.wizard.dojek.DoJekPinLocationMapFragment;
import id.co.kurindo.kurindo.wizard.dosend.DoSendFormFragment;

/**
 * Created by dwim on 2/7/2017.
 */

public class DoCarFormActivity extends AbstractStepperActivity {
    private static final String TAG = "DoCarFormActivity";
    int step = -1;
    String doType = AppConfig.KEY_DOCAR;
    String doCarType = AppConfig.KEY_RENTAL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Bundle bundle = getIntent().getExtras();
        if(bundle != null) {
            doType = (bundle.getString("do_type") !=null? bundle.getString("do_type") : AppConfig.KEY_DOCAR);
            doCarType = (bundle.getString("docar_type") !=null? bundle.getString("docar_type") : AppConfig.KEY_RENTAL);
        }
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
        if(doCarType.equalsIgnoreCase(AppConfig.KEY_RENTAL)){
            return new DoCarRentalStepperAdapter(getSupportFragmentManager());
        }else
        if(doCarType.equalsIgnoreCase(AppConfig.KEY_WISATA)){
            return new DoCarWisataStepperAdapter(getSupportFragmentManager());
        }

        return new MyStepperAdapter(getSupportFragmentManager(), doType);
    }

    @Override
    public void onStepSelected(int newStepPosition) {
        mCompleteNavigationButton.setText("Order "+doType);
        //mCompleteNavigationButton.setVisibility(View.GONE);
        if(newStepPosition == 0) mNextNavigationButton.setText("Search");
        if(newStepPosition == 1) mNextNavigationButton.setVisibility(View.GONE);
        step = newStepPosition;
    }

    @Override
    public boolean providesActivityToolbar() {
        return true;
    }

    @Override
    public void onCompleted(View completeButton) {
        DoCarHelper.getInstance().clearAll();
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
                case 2:
                    if(DoSendHelper.getInstance().getDoType().equalsIgnoreCase(AppConfig.KEY_DOCAR))
                        return new DoCarForm2();
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


    private static class DoCarRentalStepperAdapter extends AbstractStepAdapter {
        DoCarRentalStepperAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment createStep(int position) {
            switch (position) {
                case 0:
                    return new DoCarForm3();
                case 1:
                    return new DoCarForm4();
                case 2:
                    return new DoCarForm5();
                case 3:
                    return new DoCarForm6();
                default:
                    throw new IllegalArgumentException("Unsupported position: " + position);
            }
        }

        @Override
        public int getCount() {
            return 4;
        }
    }

    private static class DoCarWisataStepperAdapter extends AbstractStepAdapter {
        DoCarWisataStepperAdapter (FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment createStep(int position) {
            switch (position) {
                case 0:
                    return new DoCarForm7();
                default:
                    throw new IllegalArgumentException("Unsupported position: " + position);
            }
        }

        @Override
        public int getCount() {
            return 1;
        }
    }


    @Override
    public boolean onSupportNavigateUp() {
        Fragment f = getStepperAdapter().getItem(step);
        if(f != null){
            if(f instanceof DoJekPinLocationMapFragment) {
                if (!DoJekPinLocationMapFragment.getInstance().handleBackPressed()) {
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
                if(!DoJekPinLocationMapFragment.getInstance().handleBackPressed()) {
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
