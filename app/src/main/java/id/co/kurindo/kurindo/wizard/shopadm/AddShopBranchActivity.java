
package id.co.kurindo.kurindo.wizard.shopadm;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.stepstone.stepper.adapter.AbstractStepAdapter;

import id.co.kurindo.kurindo.R;
import id.co.kurindo.kurindo.wizard.AbstractStepperActivity;

public class AddShopBranchActivity extends AbstractStepperActivity {
    private static final String TAG = "AddShopBranchActivity";
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

    @Override
    public void onStepSelected(int newStepPosition) {

        if(newStepPosition == 1){
            mCompleteNavigationButton.setText("Simpan");
            //mNextNavigationButton.setBackgroundDrawable(getResources().getDrawable(R.drawable.ms_spesial_button_background));
        }else{
            mNextNavigationButton.setBackgroundDrawable(getResources().getDrawable(R.drawable.ms_default_button_background));
        }
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
            //pilih pics from list
            switch (position) {
                case 0:
                    return new ShopAdmPinLocationMapFragment(); //information
                case 1:
                    return AddShopBranchForm1.newInstance(editModeStep); //confirmation
                default:
                    throw new IllegalArgumentException("Unsupported position: " + position);
            }
        }

        @Override
        public int getCount() {
            return 2;
        }
    }
}
