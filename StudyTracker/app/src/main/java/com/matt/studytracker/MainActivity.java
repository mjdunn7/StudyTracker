package com.matt.studytracker;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBar.Tab;
import android.support.v7.app.ActionBarActivity;
import android.view.View;

import java.text.DateFormat;
import java.util.Date;


public class MainActivity extends ActionBarActivity implements ActionBar.TabListener {
    String stoppedSubject;

    public DBAdapter myDB;
    //public DBAdapter myHistoryDB;
    private ViewPager viewPager;
    private TabsPagerAdapter mAdapter;
    private ActionBar actionbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        openDataBase();
        setContentView(R.layout.activity_main);


        actionbar = getSupportActionBar();
        actionbar.setNavigationMode(actionbar.NAVIGATION_MODE_TABS);
        viewPager = (ViewPager) findViewById(R.id.pager);
        mAdapter = new TabsPagerAdapter(getSupportFragmentManager());

        viewPager.setAdapter(mAdapter);

        ActionBar.Tab HomeTab = actionbar.newTab().setIcon(R.drawable.home);
        ActionBar.Tab ListTab = actionbar.newTab().setIcon(R.drawable.history);
        ActionBar.Tab ChartTab = actionbar.newTab().setIcon(R.drawable.analytics);

        HomeTab.setTabListener(this);
        ListTab.setTabListener(this);
        ChartTab.setTabListener(this);

        actionbar.addTab(HomeTab);
        actionbar.addTab(ListTab);
        actionbar.addTab(ChartTab);

        actionbar.setDisplayShowTitleEnabled(false);
        actionbar.setDisplayShowHomeEnabled(false);

        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {
                // on changing the page
                // make respected tab selected
                actionbar.setSelectedNavigationItem(position);
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
            }

            @Override
            public void onPageScrollStateChanged(int arg0) {
            }
        });
    }

    public void openDataBase(){
        myDB = new DBAdapter(this);
        myDB.open();

        //myHistoryDB = new DBAdapter(this);
        //myHistoryDB.open();

    }
    public void addButtonClick(View v)
    {
        Intent intent = new Intent(MainActivity.this, addClass.class);
        MainActivity.this.startActivityForResult(intent, 1);
    }

    public void addToHistory(String subject, String timeElapsed)
    {
        mAdapter.Fragment2.addHistory(subject, timeElapsed);

        stoppedSubject = String.format(subject);
        addHistoryToDataBase(stoppedSubject, timeElapsed);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1)
        {
            if (resultCode == Activity.RESULT_OK)
            {
                String className = data.getExtras().getString("class_name");
                if (className.trim().length() > 0) {
                    mAdapter.Fragment1.newClass(className);
                    addSubjectToDataBase(className);
                }

            }
        }
    }

    public void addSubjectToDataBase(String subject){
        myDB.insertSubjectRow(subject);
    }

    public void addHistoryToDataBase(String history, String time){
        String date  = DateFormat.getDateInstance().format(new Date());
        myDB.insertHistoryRow(history, time, date);
    }

    @Override
    public void onTabSelected(Tab tab, FragmentTransaction fragmentTransaction) {
        viewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(Tab tab, FragmentTransaction fragmentTransaction) {

    }

    @Override
    public void onTabReselected(Tab tab, FragmentTransaction fragmentTransaction) {

    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        myDB.close();
        //myHistoryDB.close();
    }

}

