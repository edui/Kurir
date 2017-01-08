package id.co.kurindo.kurindo;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.util.List;

import id.co.kurindo.kurindo.app.AppController;
import id.co.kurindo.kurindo.model.News;
import id.co.kurindo.kurindo.task.ListenableAsyncTask;
import id.co.kurindo.kurindo.task.LoadNewsTask;

/**
 * Created by DwiM on 11/16/2016.
 */

public class SplashScreenActivity extends AppCompatActivity {
    boolean done = false;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Thread timer = new Thread() {
            public void run(){
                while(!done){
                    try {
                        sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                //Intent intent = new Intent(getApplicationContext(), MainDrawerActivity.class);
                startActivity(intent);
                finish();
            }
        };
        ListenableAsyncTask loadNewsTask = (ListenableAsyncTask) new LoadNewsTask(this);
        loadNewsTask.listenWith(new ListenableAsyncTask.AsyncTaskListener() {
            @Override
            public void onPostExecute(Object o) {
                List<News> newsList = (List<News>)o;
                Log.i("loadNewsTask","newsList size:"+newsList.size());
                if(newsList != null && newsList.size() > 0){
                    AppController.getInstance().banners = newsList;
                }
                done = true;
            }
        });
        timer.start();
        loadNewsTask.execute("promo");
    }

}
