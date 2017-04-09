package id.co.kurindo.kurindo;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class TabFragment extends Fragment {

    public static TabLayout tabLayout;
    public static ViewPager viewPager;
    public static int int_items = 3 ;
    HomeFragment homeFragment;
    DirectoryFragment directoryFragment;
    NewsFragment newsFragment;
    int counter = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        /**
         *Inflate tab_layout and setup Views.
         */
            View x =  inflater.inflate(R.layout.tab_layout,null);
            tabLayout = (TabLayout) x.findViewById(R.id.tabs);
            viewPager = (ViewPager) x.findViewById(R.id.viewpager);

        HomeFragment homeFragment  = new HomeFragment();
        final DirectoryFragment directoryFragment = new DirectoryFragment();
        NewsFragment newsFragment = new NewsFragment();
        /**
         *Set an Apater for the View Pager
         */
        viewPager.setAdapter(new MyAdapter(getChildFragmentManager(), homeFragment, directoryFragment, newsFragment));

        /**
         * Now , this is a workaround ,
         * The setupWithViewPager dose't works without the runnable .
         * Maybe a Support Library Bug .
         */

        tabLayout.post(new Runnable() {
            @Override
            public void run() {
                    tabLayout.setupWithViewPager(viewPager);
                   }
        });

        // Needed since support libraries version 23.0.0
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                if(tabLayout.getTabCount() >= position)
                    tabLayout.getTabAt(position).select();
                if(position == 1){
                    if(counter % 4 == 1){
                        directoryFragment.load_shops_location();
                        counter = 1;
                    }
                    counter++;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }

        });

        tabLayout.setOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(viewPager) {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                super.onTabSelected(tab);
            }
        });
        return x;

    }

    class MyAdapter extends FragmentPagerAdapter{
        HomeFragment homeFragment;
        DirectoryFragment directoryFragment;
        NewsFragment newsFragment;

        public MyAdapter(FragmentManager fm, HomeFragment homeFragment, DirectoryFragment directoryFragment, NewsFragment newsFragment) {
            super(fm);
            this.homeFragment = homeFragment;
            this.directoryFragment = directoryFragment;
            this.newsFragment = newsFragment;
        }

        /**
         * Return fragment with respect to Position .
         */

        @Override
        public Fragment getItem(int position)
        {
          switch (position){
              case 0 : return homeFragment;
              case 1 : return directoryFragment;
              case 2 : return newsFragment;
          }
        return null;
        }

        @Override
        public int getCount() {
            return int_items;

        }

        /**
         * This method returns the title of the tab according to the position.
         */

        @Override
        public CharSequence getPageTitle(int position) {

            switch (position){
                case 0 :
                    return "Home";
                case 1 :
                    return "Shop";
                case 2 :
                    return "News";
            }
                return null;
        }
    }

}
