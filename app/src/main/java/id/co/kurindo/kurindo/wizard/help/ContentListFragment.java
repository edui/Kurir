package id.co.kurindo.kurindo.wizard.help;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.stepstone.stepper.Step;
import com.stepstone.stepper.VerificationError;

import butterknife.Bind;
import id.co.kurindo.kurindo.R;
import id.co.kurindo.kurindo.base.BaseFragment;
import id.co.kurindo.kurindo.wizard.BaseStepFragment;

/**
 * Created by DwiM on 5/16/2017.
 */

public class ContentListFragment extends BaseFragment {
    @Bind(R.id.list)
    RecyclerView list;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflateAndBind(inflater, container, R.layout.fragment_help_contentlist);

        return v;
    }

}
