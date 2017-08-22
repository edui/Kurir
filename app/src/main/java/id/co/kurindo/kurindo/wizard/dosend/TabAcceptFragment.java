package id.co.kurindo.kurindo.wizard.dosend;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.model.LatLng;
import com.stepstone.stepper.Step;
import com.stepstone.stepper.VerificationError;

import id.co.kurindo.kurindo.R;
import id.co.kurindo.kurindo.app.AppConfig;
import id.co.kurindo.kurindo.helper.ViewHelper;
import id.co.kurindo.kurindo.model.DoMart;
import id.co.kurindo.kurindo.model.TOrder;
import id.co.kurindo.kurindo.model.TPacket;
import id.co.kurindo.kurindo.wizard.BaseStepFragment;

import static android.R.attr.data;

/**
 * Created by dwim on 1/6/2017.
 */

public class TabAcceptFragment extends BaseStepFragment implements Step {

    public static TabLayout tabLayout;
    public static ViewPager viewPager;
    public static int int_items = 2 ;

    StepAcceptTOrderFragment acceptFragment;
    KurirMapFragment kurirMapFragment;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        /**
         *Inflate tab_layout and setup Views.
         */
        View x =  inflater.inflate(R.layout.layout_tab_accept,null);
        tabLayout = (TabLayout) x.findViewById(R.id.tabs);
        viewPager = (ViewPager) x.findViewById(R.id.viewpager);

        /**
         *Set an Apater for the View Pager
         */
        acceptFragment = new StepAcceptTOrderFragment();

        String doType = null;
        LatLng latlng = new LatLng(0,0);
        TOrder order = ViewHelper.getInstance().getOrder();
        if(order != null){
            if(order.getDocar() != null && order.getDocar().getUser() != null ){
                latlng = order.getDocar().getUser().getAddress().getLocation();
            } else if(order.getService_type().equalsIgnoreCase(AppConfig.KEY_DOSEND)
                    || order.getService_type().equalsIgnoreCase(AppConfig.KEY_DOJEK)
                    || order.getService_type().equalsIgnoreCase(AppConfig.KEY_DOCAR)
                    || order.getService_type().equalsIgnoreCase(AppConfig.KEY_DOMOVE) ){
                if(order.getPackets() != null){
                    Object [] arr = order.getPackets().toArray();
                    if(arr.length > 0){
                        TPacket p = (TPacket) arr[0];
                        latlng = p.getOrigin().getAddress().getLocation();
                    }
                }
            } else if(order.getService_type().equalsIgnoreCase(AppConfig.KEY_DOMART) ){
                if(order.getMarts() != null){
                    Object [] arr = order.getPackets().toArray();
                    if(arr.length > 0){
                        DoMart p = (DoMart) arr[0];
                        latlng = p.getOrigin().getAddress().getLocation();
                    }
                }
            } else if(order.getService_type().equalsIgnoreCase(AppConfig.KEY_DOSERVICE) ||
                    order.getService_type().equalsIgnoreCase(AppConfig.KEY_DOWASH)  ||
                    order.getService_type().equalsIgnoreCase(AppConfig.KEY_DOHIJAMAH)){
                latlng = order.getPlace().getAddress().getLocation();
            }


            doType = order.getService_type();
        }
        kurirMapFragment = KurirMapFragment.newInstance(latlng, doType);

        viewPager.setAdapter(new MyAdapter(getChildFragmentManager(), acceptFragment, kurirMapFragment));

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

    @Override
    public int getName() {
        return 0;
    }

    @Override
    public VerificationError verifyStep() {
        return acceptFragment.verifyStep();
    }

    @Override
    public void onSelected() {
        acceptFragment.onSelected();
    }

    @Override
    public void onError(@NonNull VerificationError error) {

    }

    class MyAdapter extends FragmentPagerAdapter {
        StepAcceptTOrderFragment acceptFragment;
        KurirMapFragment kurirMapFragment;
        public MyAdapter(FragmentManager fm) {
            super(fm);
        }

        public MyAdapter(FragmentManager fm, StepAcceptTOrderFragment loginFragment, KurirMapFragment signupFragment) {
            super(fm);
            this.acceptFragment = loginFragment;
            this.kurirMapFragment = signupFragment;
        }

        /**
         * Return fragment with respect to Position .
         */

        @Override
        public Fragment getItem(int position)
        {
            switch (position){
                case 0 : return acceptFragment;
                case 1 : return kurirMapFragment;
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
                    return "List";
                case 1 :
                    return "Map";
            }
            return null;
        }

    }

}

