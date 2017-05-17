
package id.co.kurindo.kurindo.wizard.shopadm;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.View;

import com.stepstone.stepper.adapter.AbstractStepAdapter;

import id.co.kurindo.kurindo.wizard.AbstractStepperActivity;

public class AddProductActivity extends AbstractStepperActivity {
    private static final String TAG = "AddProductActivity";
    int step = -1;
    boolean editMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getIntent().getExtras();
        if(bundle != null){
            editMode = bundle.getBoolean("editMode");
        }
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
        mCompleteNavigationButton.setText("Simpan Produk");
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
        boolean editModeStep;
        MyStepperAdapter(FragmentManager fm, boolean editMode) {
            super(fm);
            this.editModeStep = editMode;
        }

        @Override
        public Fragment createStep(int position) {
            switch (position) {
                case 0:
                    return AddProductForm1.newInstance(editModeStep);
                default:
                    throw new IllegalArgumentException("Unsupported position: " + position);
            }
        }

        @Override
        public int getCount() {
            return 1;
        }
    }
}
