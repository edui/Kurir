/*
Copyright 2016 StepStone Services

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 */

package id.co.kurindo.kurindo.wizard;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.View;

import com.android.tonyvu.sc.util.CartHelper;
import com.stepstone.stepper.adapter.AbstractStepAdapter;

import id.co.kurindo.kurindo.R;
import id.co.kurindo.kurindo.wizard.checkout.StepSelectRecipientFragment;
import id.co.kurindo.kurindo.wizard.checkout.StepSelectPaymentFragment;
import id.co.kurindo.kurindo.wizard.checkout.StepConfirmShopCheckoutFragment;
import id.co.kurindo.kurindo.wizard.checkout.StepSummaryFragment;

public class ShopCheckoutActivity extends AbstractStepperActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    protected AbstractStepAdapter getStepperAdapter() {
        return new MyStepperAdapter(getSupportFragmentManager());
    }

    @Override
    public void onStepSelected(int newStepPosition) {
        //Toast.makeText(this, "onStepSelected! -> " + newStepPosition, Toast.LENGTH_SHORT).show();
        if(newStepPosition == 2){
            mNextNavigationButton.setText("Confirm Order");
            mNextNavigationButton.setBackgroundDrawable(getResources().getDrawable(R.drawable.ms_spesial_button_background));
        }else{
            mNextNavigationButton.setBackgroundDrawable(getResources().getDrawable(R.drawable.ms_default_button_background));
        }

        if(newStepPosition == 3){
            mBackNavigationButton.setVisibility(View.GONE);
        }
    }

    @Override
    public void onCompleted(View completeButton) {
        CartHelper.getCart().clear();
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
                    return new StepSelectRecipientFragment();
                case 1:
                    return new StepSelectPaymentFragment();
                case 2:
                    return new StepConfirmShopCheckoutFragment();
                case 3:
                    return new StepSummaryFragment();
                default:
                    throw new IllegalArgumentException("Unsupported position: " + position);
            }
        }

        @Override
        public int getCount() {
            return 4;
        }
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
