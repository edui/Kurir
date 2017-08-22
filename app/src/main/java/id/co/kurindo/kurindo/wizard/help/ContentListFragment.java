package id.co.kurindo.kurindo.wizard.help;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.stepstone.stepper.Step;
import com.stepstone.stepper.VerificationError;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import id.co.kurindo.kurindo.DetailImageActivity;
import id.co.kurindo.kurindo.R;
import id.co.kurindo.kurindo.adapter.HelpItemAdapter;
import id.co.kurindo.kurindo.app.AppConfig;
import id.co.kurindo.kurindo.base.BaseFragment;
import id.co.kurindo.kurindo.base.RecyclerItemClickListener;
import id.co.kurindo.kurindo.comp.ProgressDialogCustom;
import id.co.kurindo.kurindo.model.Help;
import id.co.kurindo.kurindo.model.ImageModel;
import id.co.kurindo.kurindo.util.DummyContent;
import id.co.kurindo.kurindo.wizard.BaseStepFragment;
import id.co.kurindo.kurindo.wizard.help.start.WelcomeActivity;
import id.co.kurindo.kurindo.wizard.help.start.WelcomeHelpActivity;

/**
 * Created by DwiM on 5/16/2017.
 */

public class ContentListFragment extends BaseFragment {
    @Bind(R.id.list)
    RecyclerView list;
    HelpItemAdapter adapter;
    List<Help> datas = new ArrayList<>();

    Context context;
    ProgressDialog progressDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getContext();
        progressDialog = new ProgressDialogCustom(context);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflateAndBind(inflater, container, R.layout.fragment_help_contentlist);
        datas = DummyContent.HELPS;

        adapter = new HelpItemAdapter(context, datas, new HelpItemAdapter.OnItemClickListener() {
            @Override
            public void onButtonViewPesanClick(View view, int position) {
                onItemClickListener(position);
            }
        });
        list.setAdapter(adapter);
        list.setLayoutManager(new GridLayoutManager(context, 1));
        list.setHasFixedSize(true);
        list.addOnItemTouchListener(new RecyclerItemClickListener(context, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                onItemClickListener(position);
            }
        }));

        return v;
    }

    public void onItemClickListener(int position){
            Intent intent = null;
            switch (position){
                case 0:
                    intent = new Intent(context, WelcomeHelpActivity.class);
                    break;
                case 1:
                    List<ImageModel> data = new ArrayList<>();
                    data.add(new ImageModel("banner_dosend.png", AppConfig.KEY_DOSEND));
                    data.add(new ImageModel("banner_dojek.png", AppConfig.KEY_DOJEK));
                    data.add(new ImageModel("banner_docar.png", AppConfig.KEY_DOCAR));
                    data.add(new ImageModel("banner_domove.png", AppConfig.KEY_DOMOVE));
                    data.add(new ImageModel("banner_dowash.png", AppConfig.KEY_DOWASH));
                    data.add(new ImageModel("banner_doservice.png", AppConfig.KEY_DOSERVICE));
                    data.add(new ImageModel("banner_dohijamah.png", AppConfig.KEY_DOHIJAMAH));
                    data.add(new ImageModel("banner_doclient.png", AppConfig.KEY_DOCLIENT));
                    intent = new Intent(getContext(), DetailImageActivity.class);
                    intent.putParcelableArrayListExtra("orders", (ArrayList<? extends Parcelable>) data);
                    intent.putExtra("pos", 0);
                    break;
                case 2:
                    intent = new Intent(Intent.ACTION_VIEW, Uri.parse(AppConfig.API_HOST+"/help/faq"));
                    break;
                case 3:
                    intent = new Intent(Intent.ACTION_VIEW, Uri.parse(AppConfig.API_HOST+"/help/user_guide"));
                    break;
            }
            getActivity().startActivity(intent);
    }
}
