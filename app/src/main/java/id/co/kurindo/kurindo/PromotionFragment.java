package id.co.kurindo.kurindo;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.daimajia.slider.library.Animations.DescriptionAnimation;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.TextSliderView;
import com.daimajia.slider.library.Tricks.ViewPagerEx;

import java.util.List;

import id.co.kurindo.kurindo.model.ImageModel;
import id.co.kurindo.kurindo.model.News;
import id.co.kurindo.kurindo.task.ListenableAsyncTask;
import id.co.kurindo.kurindo.task.LoadNewsTask;
import id.co.kurindo.kurindo.util.LogUtil;

/**
 * Created by DwiM on 11/9/2016.
 */
public class PromotionFragment extends Fragment implements BaseSliderView.OnSliderClickListener,ViewPagerEx.OnPageChangeListener,ListenableAsyncTask.AsyncTaskListener{

    SliderLayout sliderShow;
    ListenableAsyncTask loadNewsTask;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.promotion_layout, container, false);

        sliderShow = (SliderLayout) rootView.findViewById(R.id.slider);
       /*
        for (int i = 0; i < DirectoryFragment.IMGS.length; i++) {
            TextSliderView textSliderView = new TextSliderView(rootView.getContext());
            textSliderView
                    .image(DirectoryFragment.IMGS[i])
                    .setScaleType(BaseSliderView.ScaleType.FitCenter)
                    .setOnSliderClickListener(this);
            textSliderView.bundle(new Bundle());
            textSliderView.getBundle().putString("extra", "Image " + i);
            sliderShow.addSlider(textSliderView);
        }*/

        supplySlideData();
        return rootView;
    }

    private void supplySlideData() {
        loadNewsTask = (ListenableAsyncTask) new LoadNewsTask(getContext());
        loadNewsTask.listenWith(this);

    }
    public void onPostExecute(Object o) {
        List<News> newsList = (List<News>)o;
        LogUtil.logI("loadNewsTask","newsList size:"+newsList.size());
        if(newsList != null && newsList.size() > 0){
            sliderShow.removeAllSliders();
            for (int i = 0; i < newsList.size(); i++) {
                News n = newsList.get(i);
                TextSliderView textSliderView = new TextSliderView(getContext());
                textSliderView
                        .image(n.getImg())
                        .setScaleType(BaseSliderView.ScaleType.FitCenter)
                        .setOnSliderClickListener(this);
                textSliderView.bundle(new Bundle());
                textSliderView.getBundle().putString("extra", n.getTitle());
                sliderShow.addSlider(textSliderView);
            }
            sliderShow.setPresetTransformer(SliderLayout.Transformer.Accordion);
            sliderShow.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom);
            sliderShow.setCustomAnimation(new DescriptionAnimation());
            sliderShow.setDuration(4000);
            sliderShow.addOnPageChangeListener(this);
            sliderShow.startAutoCycle();
        }
    }
    @Override
    public void onSliderClick(BaseSliderView slider) {
        Toast.makeText(getContext(), slider.getBundle().get("extra") + "", Toast.LENGTH_SHORT).show();
    }
    public void onStop() {
        sliderShow.stopAutoCycle();
        super.onStop();
    }

    @Override
    public void onPause() {
        sliderShow.stopAutoCycle();
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        sliderShow.startAutoCycle();
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        Log.d("Slider Demo", "Page Changed: " + position);
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
}
