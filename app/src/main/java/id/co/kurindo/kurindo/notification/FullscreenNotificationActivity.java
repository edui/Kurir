package id.co.kurindo.kurindo.notification;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.WindowManager;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import id.co.kurindo.kurindo.R;
import id.co.kurindo.kurindo.TOrderShowActivity;
import id.co.kurindo.kurindo.adapter.NotificationAdapter;
import id.co.kurindo.kurindo.app.AppConfig;
import id.co.kurindo.kurindo.app.AppController;
import id.co.kurindo.kurindo.helper.ViewHelper;
import id.co.kurindo.kurindo.model.Notification;

/**
 * Created by DwiM on 7/13/2017.
 */

public class FullscreenNotificationActivity extends AppCompatActivity{

    @Bind(R.id.list)
    RecyclerView list;
    List<Notification> datas = new ArrayList<>();

    NotificationAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        int flags = WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_FULLSCREEN;

        getWindow().addFlags(flags);
        AppConfig.taskID = getTaskId();

        Bundle bundle = getIntent().getExtras();
        if(bundle != null){
            Notification data = bundle.getParcelable("notification");
            if(data != null){
                datas.add(data);
            }
        }

        setContentView(R.layout.activity_notif_message);
        ButterKnife.bind(this);

        adapter = new NotificationAdapter(AppController.applicationContext, datas, new NotificationAdapter.OnItemClickListener() {
            @Override
            public void onActionButtonClick(View view, int position) {
                Intent intent = new Intent(AppController.applicationContext, TOrderShowActivity.class);
                Notification dt = datas.get(position);
                intent.putExtra("awb", dt.getAwb());
                intent.putExtra("load", true);
                startActivity(intent);
                onDismissButtonClick(view, position);
                finish();
            }

            @Override
            public void onDismissButtonClick(View view, int position) {
                datas.remove(position);
                adapter.notifyDataSetChanged();
                if(datas.size() == 0) finish();
            }
        });
        list.setAdapter(adapter);
        list.setLayoutManager(new GridLayoutManager(AppController.applicationContext, 1));
        list.setHasFixedSize(true);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        datas.clear();
        AppConfig.taskID = 0;
    }
}
