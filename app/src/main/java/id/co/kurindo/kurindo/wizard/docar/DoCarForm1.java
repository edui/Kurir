package id.co.kurindo.kurindo.wizard.docar;

/**
 * Created by aspire on 3/26/2017.
 */

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.stepstone.stepper.Step;
import com.stepstone.stepper.VerificationError;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import id.co.kurindo.kurindo.R;
import id.co.kurindo.kurindo.adapter.ItemAdapter;
import id.co.kurindo.kurindo.app.AppConfig;
import id.co.kurindo.kurindo.base.BaseActivity;
import id.co.kurindo.kurindo.base.RecyclerItemClickListener;
import id.co.kurindo.kurindo.model.Vehicle;
import id.co.kurindo.kurindo.wizard.BaseStepFragment;
import id.co.kurindo.kurindo.wizard.dojek.DoJekOrderActivity;

/**
 * Created by dwim on 3/15/2017.
 */

public class DoCarForm1 extends BaseStepFragment implements Step {
    private static final String TAG = "DoCarForm1";
    VerificationError invalid = null;

    @Bind(R.id.list)
    RecyclerView list;

    ItemAdapter adapter;

    List<Vehicle> datas = new ArrayList<>();
    private RecyclerView.OnItemTouchListener listItemClickListener;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflateAndBind(inflater, container, R.layout.fragment_docar1);
        list.setLayoutManager(new GridLayoutManager(getContext(), 1));
        list.setHasFixedSize(true);
        datas = AppConfig.getDoCarServices();
        adapter = new ItemAdapter(getContext(), datas);
        list.setAdapter(adapter);
        list.addOnItemTouchListener(getListItemClickListener());
        return v;
    }

    @Override
    public int getName() {
        return R.string.docar_form;
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

    public RecyclerView.OnItemTouchListener getListItemClickListener() {
        return new RecyclerItemClickListener(getContext(), new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Bundle bundle = new Bundle();
                switch (position){
                    case 0:
                        bundle.putString("do_type", AppConfig.KEY_DOCAR);
                        ((BaseActivity)getActivity()).showActivity(DoJekOrderActivity.class, bundle);
                        break;
                    case 1:
                        bundle.putString("docar_type", AppConfig.KEY_RENTAL);
                        ((BaseActivity)getActivity()).showActivity(DoCarFormActivity.class, bundle);
                        break;
                    case 2:
                        bundle.putString("docar_type", AppConfig.KEY_WISATA);
                        ((BaseActivity)getActivity()).showActivity(DoCarFormActivity.class, bundle);
                        break;
                }
            }
        });
    }
}