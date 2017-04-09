
package id.co.kurindo.kurindo.wizard.dohijamah;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.View;

import com.stepstone.stepper.adapter.AbstractStepAdapter;

import id.co.kurindo.kurindo.wizard.AbstractStepperActivity;
import id.co.kurindo.kurindo.wizard.domart.DoMartForm1;

public class DoHijamahActivity extends AbstractStepperActivity {
    private static final String TAG = "DoHijamahActivity";
    int step = -1;
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
        mCompleteNavigationButton.setText("Dalam Pengembangan");

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

        MyStepperAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment createStep(int position) {
            switch (position) {
                case 0:
                    return new DoHijamahForm1();
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
