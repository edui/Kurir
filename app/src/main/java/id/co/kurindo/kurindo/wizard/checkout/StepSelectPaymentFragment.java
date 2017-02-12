package id.co.kurindo.kurindo.wizard.checkout;

import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;

import com.stepstone.stepper.Step;
import com.stepstone.stepper.VerificationError;

import id.co.kurindo.kurindo.R;
import id.co.kurindo.kurindo.helper.CheckoutHelper;
import id.co.kurindo.kurindo.wizard.BaseStepFragment;


/**
 * Created by aspire on 11/30/2016.
 */

public class StepSelectPaymentFragment extends BaseStepFragment implements Step {
    private static final String TAG = "StepSelectPaymentFragment";
    RadioGroup inputPaymentRadio;


    private static final String LAYOUT_RESOURCE_ID_ARG_KEY = "messageResourceId";
    public static StepSelectPaymentFragment newInstance(@LayoutRes int layoutResId) {
        Bundle args = new Bundle();
        args.putInt(LAYOUT_RESOURCE_ID_ARG_KEY, layoutResId);
        StepSelectPaymentFragment fragment = new StepSelectPaymentFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_select_pembayaran, container, false);
        inputPaymentRadio = (RadioGroup)v.findViewById(R.id.rdogrp);
        inputPaymentRadio.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if(checkedId == R.id.radio_input_cod){
                    CheckoutHelper.getInstance().setPayment("COD");
                }else{
                    CheckoutHelper.getInstance().setPayment("TRANSFER");
                }
            }
        });
        return v;
    }


    @Override
    public int getName() {
        return R.string.select_payment;
    }

    @Override
    public VerificationError verifyStep() {
        if(CheckoutHelper.getInstance().getPayment() == null || CheckoutHelper.getInstance().getPayment().isEmpty()) {
            return  new VerificationError("Incomplete Form : Pilih salah satu cara Pembayaran.");
        }
        return null;
    }

    @Override
    public void onSelected() {

    }

    @Override
    public void onError(@NonNull VerificationError error) {

    }
}
