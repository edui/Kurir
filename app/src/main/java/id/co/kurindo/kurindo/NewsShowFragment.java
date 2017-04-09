package id.co.kurindo.kurindo;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;

import butterknife.Bind;
import id.co.kurindo.kurindo.app.AppConfig;
import id.co.kurindo.kurindo.base.BaseFragment;
import id.co.kurindo.kurindo.model.News;

/**
 * Created by aspire on 12/20/2016.
 */

public class NewsShowFragment extends BaseFragment {

    @Bind(R.id.tvPageTitle)
    TextView pageTitle;
    @Bind(R.id.ivNews)
    ImageView ivNews;
    @Bind(R.id.tvNewsDescription)
    TextView newsDescription;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflateAndBind(inflater, container, R.layout.fragment_news_show);

        Bundle bundle = getArguments();
        News news = bundle.getParcelable("news");
        if(news != null){
            if(news.getImg() == null){
                ivNews.setImageResource(news.getDrawable());
            } else {
                Glide.with(getContext()).load(news.getImg())
                        .thumbnail(0.5f)
                        //.override(200,200)
                        .crossFade()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(ivNews);
            }
            pageTitle.setText(news.getTitle());
            /*try {
                //File myFile = new File("/sdcard/filename.txt");
                //FileInputStream iStr = new FileInputStream(myFile);
                BufferedReader fileReader = new BufferedReader(new InputStreamReader(getContext().getResources().openRawResource(R.raw.news) ));
                String TextLine= "";
                String TextBuffer = "";
                boolean read = false;
                while ((TextLine= fileReader.readLine()) != null) {
                    if(TextLine.contains("==="+news.getTitle()+"==end===")) {
                        read = false;
                        break;
                    }
                    if(read) TextBuffer += TextLine+ "\n";
                    if(TextLine.contains("==="+news.getTitle()+"==begin===")) read = true;
                }
                //viewText.setText(Html.fromHtml(TextBuffer));
                newsDescription.setText(TextBuffer);
                fileReader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }*/

            //if(newsDescription.getText() == null || newsDescription.getText().equals("")){
                newsDescription.setText(news.getContent());
            //}

            HashMap<String, String > params = new HashMap<>();
            params.put("form-user", db.getUserPhone());
            params.put("form-type", "NEWS");
            params.put("form-tag", news.getTitle());
            params.put("form-activity", "View "+news.getTitle());
            addRequest("req_logger", Request.Method.POST, AppConfig.URL_LOGGING, new Response.Listener() {
                @Override
                public void onResponse(Object o) {

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError volleyError) {

                }
            }, params, getKurindoHeaders());
        }
        return view;
    }
}
