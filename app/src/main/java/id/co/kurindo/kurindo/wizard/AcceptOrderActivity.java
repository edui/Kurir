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
import android.support.v7.app.ActionBar;
import android.view.View;

import com.android.tonyvu.sc.util.CartHelper;
import com.stepstone.stepper.adapter.AbstractStepAdapter;

import id.co.kurindo.kurindo.R;
import id.co.kurindo.kurindo.helper.OrderHelper;
import id.co.kurindo.kurindo.model.Order;
import id.co.kurindo.kurindo.wizard.checkout.StepConfirmShopCheckoutFragment;
import id.co.kurindo.kurindo.wizard.checkout.StepSelectPaymentFragment;
import id.co.kurindo.kurindo.wizard.checkout.StepSelectRecipientFragment;
import id.co.kurindo.kurindo.wizard.checkout.StepSummaryFragment;
import id.co.kurindo.kurindo.wizard.dashboard.StepAcceptOrderFragment;

public class AcceptOrderActivity extends AbstractStepperActivity {
    Order order;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getIntent().getExtras();
        //order = (Order) bundle.getParcelable("order");
        order = OrderHelper.getInstance().getOrder();

    }
    protected ActionBar setupToolbar() {
        ActionBar ab = super.setupToolbar();
        if(ab != null) ab.setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_18dp);
        return  ab;
    }
    @Override
    protected AbstractStepAdapter getStepperAdapter() {
        return new MyStepperAdapter(getSupportFragmentManager());
    }

    @Override
    public void onStepSelected(int newStepPosition) {
        //Toast.makeText(this, "onStepSelected! -> " + newStepPosition, Toast.LENGTH_SHORT).show();
        //if(newStepPosition == 0){

            mCompleteNavigationButton.setText("Assign");
            //mNextNavigationButton.setBackgroundDrawable(getResources().getDrawable(R.drawable.ms_spesial_button_background));
        //}else{
        //    mNextNavigationButton.setBackgroundDrawable(getResources().getDrawable(R.drawable.ms_default_button_background));
        //}

        //if(newStepPosition == 3){
        //    mBackNavigationButton.setVisibility(View.GONE);
        //}
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
                    return new StepAcceptOrderFragment();
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
    public void onBackPressed() {
        finish();
    }

    public Order getOrder(){
        return order;
    }
}
