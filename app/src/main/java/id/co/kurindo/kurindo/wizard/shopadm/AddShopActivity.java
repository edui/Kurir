
package id.co.kurindo.kurindo.wizard.shopadm;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.View;

import com.stepstone.stepper.adapter.AbstractStepAdapter;

import id.co.kurindo.kurindo.wizard.AbstractStepperActivity;

public class AddShopActivity extends AbstractStepperActivity {
    private static final String TAG = "AddShopActivity";
    int step = -1;
    boolean editMode = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getIntent().getExtras();
        if(bundle != null)
            editMode = bundle.getBoolean("editMode");
    }

    @Override
    protected AbstractStepAdapter getStepperAdapter() {
        return new MyStepperAdapter(getSupportFragmentManager(), editMode);
    }
    protected AbstractStepAdapter getStepperAdapter(int startingStepPosition) {
        stepAdapter = getStepperAdapter();
        mStepperLayout.setAdapter(stepAdapter, startingStepPosition);
        return stepAdapter;
    }
    @Override
    public void onStepSelected(int newStepPosition) {


        step = newStepPosition;
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
        boolean editMode = false;

        MyStepperAdapter(FragmentManager fm, boolean editMode) {
            super(fm);this.editMode = editMode;
        }

        @Override
        public Fragment createStep(int position) {
            switch (position) {
                case 0:
                    return AddShopForm1.newInstance(editMode);
                case 1:
                    return AddShopForm2.newInstance(editMode);
                case 2:
                    return AddShopForm3.newInstance(editMode);
                default:
                    throw new IllegalArgumentException("Unsupported position: " + position);
            }
        }

        @Override
        public int getCount() {
            return 3;
        }
    }
}
