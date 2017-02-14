package id.co.kurindo.kurindo;

import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Timer;

import butterknife.Bind;
import id.co.kurindo.kurindo.adapter.UserAdapter;
import id.co.kurindo.kurindo.base.BaseFragment;
import id.co.kurindo.kurindo.model.User;

/**
 * Created by DwiM on 12/12/2016.
 */

public abstract class BaseKurirFragment extends BaseFragment implements View.OnClickListener {
    private static final String TAG = "BaseKurirFragment";

    @Bind(R.id.RefreshBtn)
    AppCompatButton refreshBtn;

    @Bind(R.id.list)
    RecyclerView daftarKurir;

    ArrayList<User> users = new ArrayList<>();
    Timer t ;

    @Bind(R.id.progressBar1)
    ProgressBar progressBar;
    @Bind(R.id.TextViewTitle)
    TextView textView;

    UserAdapter userAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflateAndBind(inflater, container, R.layout.fragment_kurir);

        daftarKurir.setLayoutManager(new GridLayoutManager(getContext(), 1));
        daftarKurir.setHasFixedSize(true);

        userAdapter = new UserAdapter(getContext(), users);
        daftarKurir.setAdapter(userAdapter);

        refreshBtn.setOnClickListener(this);

        check_kurir();
        return rootView;
    }


    @Override
    public void onClick(View v) {
        check_kurir();
    }

    public abstract void check_kurir();
}
