package id.co.kurindo.kurindo;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.DefaultSliderView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import id.co.kurindo.kurindo.app.AppController;
import id.co.kurindo.kurindo.base.BaseActivity;
import id.co.kurindo.kurindo.model.News;
import id.co.kurindo.kurindo.task.ListenableAsyncTask;
import id.co.kurindo.kurindo.task.LoadNewsTask;
import id.co.kurindo.kurindo.util.DummyContent;

public class NewsFragment extends Fragment implements BaseSliderView.OnSliderClickListener, ListenableAsyncTask.AsyncTaskListener {
    private static final String TAG = "NewsFragment";

    int counter = 0;
    ProgressBar progressBar;

    ArrayList<News> data = new ArrayList<>();
    /*
    NewsAdapter mAdapter;
    RecyclerView mRecyclerView;
    public static String IMGS[] = {
            "http://10.0.2.2/kurindo/img/Iklan-Web_01.png",
            "http://10.0.2.2/kurindo/img/Iklan-Web_02.png",
            "http://10.0.2.2/kurindo/img/Iklan-Web_03.png",
            "http://10.0.2.2/kurindo/img/Iklan-Web_01.png",
            "http://10.0.2.2/kurindo/img/Iklan-Web_02.png",
            "http://10.0.2.2/kurindo/img/Iklan-Web_03.png",
    };*/
    private ListenableAsyncTask loadNewsTask;
    SliderLayout sliderShow1;
    SliderLayout sliderShow2;
    SliderLayout sliderShow3;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.news_layout, container, false);
        /*data.clear();
        for (int i = 0; i < IMGS.length; i++) {
            News model= new News();
            model.setTitle("Image " + i);
            model.setThumbnail(IMGS[i]);
            model.setUrl(IMGS[i]);
            data.add(model);
        }*/

        sliderShow1 = (SliderLayout) view.findViewById(R.id.slider1);
        sliderShow2 = (SliderLayout) view.findViewById(R.id.slider2);
        sliderShow3 = (SliderLayout) view.findViewById(R.id.slider3);

        sliderShow1.setDuration(5000);
        sliderShow2.setDuration(6000);
        sliderShow3.setDuration(7000);

        loadNewsTask = (ListenableAsyncTask) new LoadNewsTask(getContext());
        loadNewsTask.listenWith(this);

        /*
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.list);
        mRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 1));
        mRecyclerView.setHasFixedSize(true);

        mAdapter = new NewsAdapter(getContext(), data);
        mRecyclerView.setAdapter(mAdapter);


        //String url = "http://stacktips.com/?json=get_category_posts&slug=news&count=30";
        //new DownloadTask().execute(url);
        */
        progressBar = (ProgressBar) view.findViewById(R.id.progress);

        counter++;
        if(counter % 9 == 1){
            loadNewsTask.execute("latest");

        }
        progressBar.setVisibility(View.GONE);
        onPostExecute(null);
        return view;
    }

    @Override
    public void onSliderClick(BaseSliderView slider) {
        //News news = (News) slider.getBundle().getParcelable("news");
        //if(news != null) Toast.makeText(getContext(), news.getTitle() + "", Toast.LENGTH_SHORT).show();

        //slider.getBundle().putParcelableArrayList("list", data);
        ((BaseActivity)getActivity()).showActivity(NewsShowActivity.class, slider.getBundle());
    }

    @Override
    public void onPostExecute(Object o) {
        List<News> newsList = (List<News>)o;
        if(newsList == null) newsList = new ArrayList<>();
        Log.i("loadNewsTask","newsList size:"+newsList.size());
        //data.clear();
        sliderShow1.removeAllSliders();
        sliderShow2.removeAllSliders();
        sliderShow3.removeAllSliders();
        //if(newsList != null && newsList.size() > 0){
            //newsList.addAll(DummyContent.NEWS);
        data.clear();
        data.addAll(DummyContent.NEWS);
        data.addAll(newsList);

            int c = Math.round( data.size()/3 );
            Collections.shuffle(data);
            for (int i = 0; i < data.size(); i++) {
                News n = data.get(i);
                //data.add(n);

                DefaultSliderView sliderView = new DefaultSliderView(getContext());
                if(n.getImg() ==null) {
                    sliderView.image(n.getDrawable())
                            .setScaleType(BaseSliderView.ScaleType.FitCenter)
                            .setOnSliderClickListener(this);
                }else {
                    sliderView.image(n.getImg())
                            .setScaleType(BaseSliderView.ScaleType.FitCenter)
                            .setOnSliderClickListener(this);
                }
                sliderView.setOnImageLoadListener(new BaseSliderView.ImageLoadListener() {
                    @Override
                    public void onStart(BaseSliderView baseSliderView) {
                        progressBar.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onEnd(boolean b, BaseSliderView baseSliderView) {
                        progressBar.setVisibility(View.GONE);
                    }
                });
                Bundle bundle = new Bundle();
                bundle.putParcelable("news", n);
                sliderView.bundle(bundle);

                if(i < c) sliderShow1.addSlider(sliderView);
                else if(i < (2*c)) sliderShow2.addSlider(sliderView);
                else sliderShow3.addSlider(sliderView);
            }
        /*}else{
            News n = new News("sample", "sample content news", "http://kurindo.co.id/news", null);
            n.setDrawable(R.drawable.banner_default);
            //data.add(n);
            DefaultSliderView sliderView = new DefaultSliderView(getContext());
            sliderView.image(n.getDrawable())
                    .setScaleType(BaseSliderView.ScaleType.FitCenter)
                    .setOnSliderClickListener(this);
            sliderView.setOnImageLoadListener(new BaseSliderView.ImageLoadListener() {
                @Override
                public void onStart(BaseSliderView baseSliderView) {
                    progressBar.setVisibility(View.VISIBLE);
                }

                @Override
                public void onEnd(boolean b, BaseSliderView baseSliderView) {
                    progressBar.setVisibility(View.GONE);
                }
            });
            sliderView.bundle(new Bundle());
            sliderView.getBundle().putSerializable("extra", n);
            sliderShow1.addSlider(sliderView);
        }*/

    }
/*
    public class DownloadTask extends AsyncTask<String, Void, Integer> {

        @Override
        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected Integer doInBackground(String... params) {
            Integer result = 0;
            HttpURLConnection urlConnection;
            try {
                URL url = new URL(params[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
                int statusCode = urlConnection.getResponseCode();

                // 200 represents HTTP OK
                if (statusCode == 200) {
                    BufferedReader r = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = r.readLine()) != null) {
                        response.append(line);
                    }
                    parseResult(response.toString());
                    result = 1; // Successful
                } else {
                    result = 0; //"Failed to fetch orders!";
                }
            } catch (Exception e) {
                Log.d(TAG, e.getLocalizedMessage());
            }
            return result; //"Failed to fetch orders!";
        }

        @Override
        protected void onPostExecute(Integer result) {
            progressBar.setVisibility(View.GONE);

            if (result == 1) {
                mAdapter = new NewsAdapter(getContext(), data);
                mRecyclerView.setAdapter(mAdapter);
            } else {
                Toast.makeText(getContext(), "Failed to fetch orders!", Toast.LENGTH_SHORT).show();
            }
        }

        private void parseResult(String result) {
            try {
                JSONObject response = new JSONObject(result);
                JSONArray posts = response.optJSONArray("posts");
                data = new ArrayList<>();

                for (int i = 0; i < posts.length(); i++) {
                    JSONObject post = posts.optJSONObject(i);
                    News item = new News();
                    item.setTitle(post.optString("title"));
                    item.setThumbnail(post.optString("thumbnail"));
                    data.add(item);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
    */
}
