package id.co.kurindo.kurindo;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by dwim on 1/6/2017.
 */

public class TabLoginFragment  extends Fragment {

    public static TabLayout tabLayout;
    public static ViewPager viewPager;
    public static int int_items = 2 ;

    LoginPhoneFragment loginFragment;
    SignupFragment signupFragment;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        /**
         *Inflate tab_layout and setup Views.
         */
        View x =  inflater.inflate(R.layout.login_tab_layout,null);
        tabLayout = (TabLayout) x.findViewById(R.id.tabs);
        viewPager = (ViewPager) x.findViewById(R.id.viewpager);

        /**
         *Set an Apater for the View Pager
         */
        loginFragment = new LoginPhoneFragment();
        signupFragment = new SignupFragment();

        loginFragment.setArguments(getArguments());

        viewPager.setAdapter(new MyAdapter(getChildFragmentManager(), loginFragment, signupFragment));

        /**
         * Now , this is a workaround ,
         * The setupWithViewPager dose't works without the runnable .
         * Maybe a Support Library Bug .
         */

        tabLayout.post(new Runnable() {
            @Override
            public void run() {
                tabLayout.setupWithViewPager(viewPager);
/*
                for (int i = 0; i < tabLayout.getTabCount(); i++) {
                    int iconId = -1;
                    switch (i) {
                        case 0:
                            iconId = R.drawable.ic_person_black;
                            break;
                        case 1:
                            iconId = R.drawable.ic_person_add_black_18dp;
                            break;
                    }
                    tabLayout.getTabAt(i).setIcon(iconId);
                }
*/
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

    class MyAdapter extends FragmentPagerAdapter {
        LoginPhoneFragment loginFragment;
        SignupFragment signupFragment;
        public MyAdapter(FragmentManager fm) {
            super(fm);
        }

        public MyAdapter(FragmentManager fm, LoginPhoneFragment loginFragment, SignupFragment signupFragment) {
            super(fm);
            this.loginFragment = loginFragment;
            this.signupFragment = signupFragment;
        }

        /**
         * Return fragment with respect to Position .
         */

        @Override
        public Fragment getItem(int position)
        {
            switch (position){
                case 0 : return loginFragment;
                case 1 : return signupFragment;
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
                    return "Login";
                case 1 :
                    return "Daftar";
            }
            return null;
        }

    }

}

