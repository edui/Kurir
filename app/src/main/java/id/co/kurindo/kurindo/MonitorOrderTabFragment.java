package id.co.kurindo.kurindo;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * Created by dwim on 1/6/2017.
 */

public class MonitorOrderTabFragment extends BaseTabFragment {
    //public int[] icons = new int[]{R.drawable.ic_person_white_18dp, R.drawable.ic_person_add_white_18dp};

    @Override
    public int[] getIcons() {
        return new int[0];
    }

    @Override
    public boolean isShowIcon() {
        return false;
    }

    @Override
    public FragmentPagerAdapter getFragmentPagerAdapter() {
        return new MyAdapter(getChildFragmentManager());
    }

    class MyAdapter extends FragmentPagerAdapter{

        public MyAdapter(FragmentManager fm) {
            super(fm);
        }

        /**
         * Return fragment with respect to Position .
         */

        @Override
        public Fragment getItem(int position)
        {
            switch (position){
                //case 0 : return MyOrderFragment.newInstance("inprogress");
                //case 1 : return MyOrderFragment.newInstance("completed");
                case 0 : return MyTOrderFragment.newInstance("inprogress");
                case 1 : return MyTOrderFragment.newInstance("completed");
            }
            return null;
        }

        @Override
        public int getCount() {
            return 2;
        }

        /**
         * This method returns the title of the tab according to the position.
         */

        @Override
        public CharSequence getPageTitle(int position) {

            switch (position){
                case 0 :
                    return "In Progress";
                case 1 :
                    return "Completed";
            }
            return null;
        }
    }
}
