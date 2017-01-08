package id.co.kurindo.kurindo;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import id.co.kurindo.kurindo.base.BaseFragment;

/**
 * Created by dwim on 1/6/2017.
 */

public abstract class BaseTabFragment extends BaseFragment {

    public static TabLayout tabLayout;
    public static ViewPager viewPager;
    public int[] icons = new int[]{R.drawable.ic_person_white_18dp, R.drawable.ic_person_add_white_18dp};

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        /**
         *Inflate tab_layout and setup Views.
         */
        View x =  inflateAndBind(inflater, container, R.layout.tab_layout);
        //View x =  inflater.inflate(R.layout.tab_layout,null);
        tabLayout = (TabLayout) x.findViewById(R.id.tabs);
        viewPager = (ViewPager) x.findViewById(R.id.viewpager);

        /**
         *Set an Apater for the View Pager
         */
        viewPager.setAdapter(getFragmentPagerAdapter());

        /**
         * Now , this is a workaround ,
         * The setupWithViewPager dose't works without the runnable .
         * Maybe a Support Library Bug .
         */

        tabLayout.post(new Runnable() {
            @Override
            public void run() {
                tabLayout.setupWithViewPager(viewPager);
                if(isShowIcon()){
                    for (int i = 0; i < tabLayout.getTabCount(); i++) {
                        int iconId = -1;
                        iconId = getIcons()[i];
                        tabLayout.getTabAt(i).setIcon(iconId);
                    }
                }
            }
        });

        /*
        // Needed since support libraries version 23.0.0
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                tabLayout.getTabAt(position).select();
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }

        });*/

        tabLayout.setOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(viewPager) {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                super.onTabSelected(tab);
            }
        });
        return x;
    }

    public abstract int [] getIcons();
    public abstract boolean isShowIcon() ;
    public abstract FragmentPagerAdapter getFragmentPagerAdapter() ;
}
