package id.co.kurindo.kurindo.wizard.domove;

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
import id.co.kurindo.kurindo.wizard.dosend.DoSendOrderActivity;

/**
 * Created by dwim on 3/15/2017.
 */

public class DoMoveForm1 extends BaseStepFragment implements Step {
    private static final String TAG = "DoMoveForm1";
    VerificationError invalid = null;

    @Bind(R.id.list)
    RecyclerView list;

    ItemAdapter adapter;

    List<Vehicle> datas = new ArrayList<>();
    private RecyclerView.OnItemTouchListener listItemClickListener;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflateAndBind(inflater, container, R.layout.fragment_domove1);
        list.setLayoutManager(new GridLayoutManager(getContext(), 1));
        list.setHasFixedSize(true);
        datas = AppConfig.getDoMoveServices();
        adapter = new ItemAdapter(getContext(), datas);
        list.setAdapter(adapter);
        list.addOnItemTouchListener(getListItemClickListener());
        return v;
    }

    @Override
    public int getName() {
        return R.string.domove_form;
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
                        bundle.putString("domove_type", AppConfig.DOMOVE_PICKUP_BAK);
                        break;
                    case 1:
                        bundle.putString("domove_type", AppConfig.DOMOVE_BLIND_VAN);
                        break;
                    case 2:
                        bundle.putString("domove_type", AppConfig.DOMOVE_ENGKEL_BAK);
                        break;
                    case 3:
                        bundle.putString("domove_type", AppConfig.DOMOVE_ENGKEL_BOX);
                        break;
                }
                bundle.putString("do_type", AppConfig.KEY_DOMOVE);
                ((BaseActivity)getActivity()).showActivity(DoSendOrderActivity.class, bundle);
            }
        });
    }
}