package id.co.kurindo.kurindo.wizard.shopadm;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lamudi.phonefield.PhoneInputLayout;
import com.stepstone.stepper.Step;
import com.stepstone.stepper.VerificationError;

import butterknife.Bind;
import id.co.kurindo.kurindo.R;
import id.co.kurindo.kurindo.app.AppConfig;
import id.co.kurindo.kurindo.wizard.BaseStepFragment;

import static id.co.kurindo.kurindo.R.style.CustomDialog;

/**
 * Created by dwim on 3/15/2017.
 */

public class AddShopBranchForm1 extends BaseStepFragment implements Step {
    private static final String TAG = "AddShopBranchForm1";
    VerificationError invalid = null;
    ProgressDialog progressDialog;

    boolean editMode = false;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if(bundle != null) editMode = bundle.getBoolean("editMode");
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflateAndBind(inflater, container, R.layout.fragment_shopbranch_summary);

        progressDialog = new ProgressDialog(getActivity(), CustomDialog);

        return v;
    }
    @Override
    public int getName() {
        return R.string.branch_information;
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

    public static AddShopBranchForm1 newInstance(boolean editMode) {
        Bundle args = new Bundle();
        args.putBoolean("editMode", editMode);
        AddShopBranchForm1 fragment = new AddShopBranchForm1();
        fragment.setArguments(args);
        return fragment;
    }
}
