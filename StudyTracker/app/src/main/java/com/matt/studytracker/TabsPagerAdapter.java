package com.matt.studytracker;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;
import android.view.ViewGroup;

public class TabsPagerAdapter extends FragmentPagerAdapter {

    protected String homeTag;
    protected String listTag;
    protected String chartTag;

    public String getHomeTag(){
        return homeTag;
    }

    public String getListTag(){
        return listTag;
    }

    public String getChartTag(){
        return  chartTag;
    }

    public TabsPagerAdapter(FragmentManager fm) {
        super(fm);
    }


    @Override
    public Fragment getItem(int index) {
        switch (index) {
            case 0:
                Log.d("Adapter", "returning new homeFragment");
                return new HomeFragment();
            case 1:
                return new ListFragment();
            case 2:
                return new ChartFragment();
        }

        return null;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        Fragment createdFragment = (Fragment) super.instantiateItem(container, position);

        switch (position){
            case 0:
                homeTag = createdFragment.getTag();
                break;
            case 1:
                listTag = createdFragment.getTag();
                break;
            case 2:
                chartTag = createdFragment.getTag();
                break;
        }

        return createdFragment;
    }

    @Override
    public int getCount() {

        return 3;
    }

}