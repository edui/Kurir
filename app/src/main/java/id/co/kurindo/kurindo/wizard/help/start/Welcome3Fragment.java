package id.co.kurindo.kurindo.wizard.help.start;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.stepstone.stepper.Step;
import com.stepstone.stepper.VerificationError;

import butterknife.Bind;
import id.co.kurindo.kurindo.R;
import id.co.kurindo.kurindo.wizard.BaseStepFragment;

/**
 * Created by DwiM on 5/16/2017.
 */

public class Welcome3Fragment extends BaseStepFragment implements Step {
    @Bind(R.id.ivWelcome)
    ImageView ivWelcome;
    @Bind(R.id.tvWelcomeContent1)
    TextView tvWelcomeContent1;
    @Bind(R.id.tvWelcomeContent2)
    TextView tvWelcomeContent2;
    @Bind(R.id.tvWelcomeContent3)
    TextView tvWelcomeContent3;
    @Bind(R.id.tvWelcomeTitle1)
    TextView tvWelcomeTitle1;
    @Bind(R.id.tvWelcomeTitle2)
    TextView tvWelcomeTitle2;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflateAndBind(inflater, container, R.layout.fragment_help_welcome1);
        ivWelcome.setImageResource(R.drawable.welcome3);

        tvWelcomeTitle1.setText("MONITORING");
        tvWelcomeTitle2.setText("( EFEKTIF & AMAN )");
        tvWelcomeContent1.setText("Status pengantaran terpantau \nberdasarkan \nlokasi yang dituju.");
        tvWelcomeContent2.setVisibility(View.GONE);
        tvWelcomeContent3.setVisibility(View.GONE);
        return v;
    }

    @Override
    public int getName() {
        return 0;
    }

    @Override
    public VerificationError verifyStep() {
        return null;
    }

    @Override
    public void onSelected() {

    }

    @Override
    public void onError(@NonNull VerificationError error) {

    }
}
