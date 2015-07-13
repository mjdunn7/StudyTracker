package com.matt.studytracker;

//import info.androidhive.tabsswipe.GamesFragment;
//import info.androidhive.tabsswipe.MoviesFragment;
//import info.androidhive.tabsswipe.TopRatedFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class TabsPagerAdapter extends FragmentPagerAdapter {

    public HomeFragment Fragment1 = new HomeFragment();
    public ListFragment Fragment2 = new ListFragment();
    public ChartFragment Fragment3 = new ChartFragment();

    public TabsPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int index) {

        switch (index) {
            case 0:
                // Top Rated fragment activity
                return Fragment1;
            case 1:
                // Games fragment activity
                return Fragment2;
            case 2:
                // Movies fragment activity
                return Fragment3;
        }

        return null;
    }

    @Override
    public int getCount() {
        // get item count - equal to number of tabs
        return 3;
    }

}