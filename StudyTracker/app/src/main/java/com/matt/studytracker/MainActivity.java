package com.matt.studytracker;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBar.Tab;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;


public class MainActivity extends ActionBarActivity implements ActionBar.TabListener,
        HomeFragment.StopClicked,
        SubjectLongTappedDialog.DialogListener,
        DeleteSubjectConfirmDialog.DeleteSubjectDialogListener,
        DeleteHistoryDialog.DeleteHistoryDialogListener{
    public static final String SUBJECT_ARRAY = "main activity subject array";

    public DBAdapter myDB;
    private ViewPager viewPager;
    private TabsPagerAdapter mAdapter;
    private ActionBar actionbar;
    private TimerService timerService;
    private boolean boundToTimer = false;

    private String[] subjectArray = {};

    private ServiceConnection mConnection;

    String subjectToRemove;
    protected int indexToRemove;

    public TimerService getTimerService(){
        return timerService;
    }

    public void setSubjectArray(String[] array){
        subjectArray = array;
    }

    public String[] getSubjectArray(){
        return subjectArray;
    }

    @Override
    public void onDeleteSubjectConfirmed() {
        HomeFragment homeFrag = (HomeFragment) getSupportFragmentManager().findFragmentByTag(mAdapter.getHomeTag());
        if(homeFrag != null) {
            //Log.d("MainActivity", "homeFrag is not null");
            homeFrag.removeSubject(indexToRemove);
        }else{
            //Log.d("MainActivity", "homeFrag is null");
            HomeFragment newFragment = new HomeFragment();

            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.pager, newFragment, mAdapter.getHomeTag());
            transaction.commit();
            newFragment.removeSubject(indexToRemove);

        }
        myDB.deleteSubjectRow(subjectToRemove);

    }

    @Override
    public void onSubjectDeleteClick() {
        DeleteSubjectConfirmDialog dialogFragment = new DeleteSubjectConfirmDialog();
        dialogFragment.show(getSupportFragmentManager(), String.format("delete subject"));
    }

    @Override
    public void onSubjectCancelClick() {

    }

    @Override
    public void onSubjectEditClick() {

    }

    @Override
    public void addToHistory(String subject, String timeElapsed, String interval) {
        Calendar calendar = Calendar.getInstance();
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);

        String date = NewSessionActivity.DAYS[dayOfWeek];
        date += ", " + DateFormat.getDateInstance().format(new Date());
        String DBdate = getCurrentDBdate();
        newHistoryEntry(subject, timeElapsed, date, DBdate, false, interval);
    }

    private String getCurrentDBdate(){
        String currentDate;
        currentDate = Integer.toString(Calendar.getInstance().get(Calendar.YEAR));

        if(Calendar.getInstance().get(Calendar.MONTH) < 10){
            currentDate += "0" + Integer.toString(Calendar.getInstance().get(Calendar.MONTH));
        }else {
            currentDate += Integer.toString(Calendar.getInstance().get(Calendar.MONTH));
        }

        if(Calendar.getInstance().get(Calendar.DAY_OF_MONTH) < 10){
            currentDate += "0" + Integer.toString(Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
        }else {
            currentDate += Integer.toString(Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
        }

        if (Calendar.getInstance().get(Calendar.HOUR_OF_DAY) < 10){
            currentDate += "0" + Integer.toString(Calendar.getInstance().get(Calendar.HOUR_OF_DAY));
        }else{
            currentDate += Integer.toString(Calendar.getInstance().get(Calendar.HOUR_OF_DAY));
        }

        if(Calendar.getInstance().get(Calendar.MINUTE) < 10){
            currentDate += "0" + Integer.toString(Calendar.getInstance().get(Calendar.MINUTE));
        }else{
            currentDate += Integer.toString(Calendar.getInstance().get(Calendar.MINUTE));
        }

        if(Calendar.getInstance().get(Calendar.SECOND) < 10){
            currentDate += "0" + Integer.toString(Calendar.getInstance().get(Calendar.SECOND));
        }else{
            currentDate += Integer.toString(Calendar.getInstance().get(Calendar.SECOND));
        }


        return currentDate;
    }

    private void newHistoryEntry(String subject, String timeElapsed,
                                 String date, String DBdate, boolean manuallyAdded,
                                 String interval){
        HistoryFragment listFrag = (HistoryFragment) getSupportFragmentManager().findFragmentByTag(mAdapter.getListTag());
        if(listFrag != null) {
            //Log.d("MainActivity", "listFrag is not null");
            addHistoryToDataBase(subject, timeElapsed, DBdate, date, interval);
            listFrag.addHistory(subject, timeElapsed, date, DBdate, manuallyAdded, interval);
        }else{
           // Log.d("MainActivity", "listFrag is null");
            addHistoryToDataBase(subject, timeElapsed, DBdate, date, interval);

            HistoryFragment newFragment = new HistoryFragment();

            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.pager, newFragment);
            transaction.commit();
            newFragment.addHistory(subject, timeElapsed, date, DBdate, manuallyAdded, interval);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if(savedInstanceState != null){
            subjectArray = savedInstanceState.getStringArray(SUBJECT_ARRAY);
        }

        //Log.d("MainActivity", "onCreate");

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
    }

    public void addButtonClick(View v){
        Intent intent = new Intent(MainActivity.this, AddClass.class);
        MainActivity.this.startActivityForResult(intent, AddClass.ACTIVITY_ID);
    }

    public void addSessionButton(View v){
        Intent intent = new Intent(MainActivity.this, NewSessionActivity.class);
        intent.putExtra(SUBJECT_ARRAY, subjectArray);
        MainActivity.this.startActivityForResult(intent, NewSessionActivity.ACTIVITY_ID);
    }

    public void startTimerService(String subject){
        timerService.startTimer(subject);

    }

    public boolean serviceTiming(){
        return timerService.timerRunning;
    }

    public String serviceSubject(){
        return timerService.timedSubject;
    }

    public long serviceTimeStarted(){
        return timerService.startedAt;
    }


    public void stopTimerService(){
        timerService.stop();
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);

        //for when "add class" activity returns
        if(requestCode == AddClass.ACTIVITY_ID && resultCode == Activity.RESULT_OK) {
            String className = data.getExtras().getString("class_name");
            if (className.trim().length() > 0) {
                HomeFragment homeFrag = (HomeFragment) getSupportFragmentManager().findFragmentByTag(mAdapter.getHomeTag());
                if (homeFrag != null) {
                    Log.d("MainActivity", "homeFrag is not null");
                    homeFrag.newClass(className);
                } else {
                    Log.d("MainActivity", "homeFrag is null");
                    HomeFragment newFragment = new HomeFragment();

                    FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                    transaction.replace(R.id.pager, newFragment, mAdapter.getHomeTag());
                    transaction.commit();
                    newFragment.newClass(className);
                }
                addSubjectToDataBase(className);
            }
        }

        //for when "add session" activity returns
        if(requestCode == NewSessionActivity.ACTIVITY_ID && resultCode == Activity.RESULT_OK){
            String date = data.getExtras().getString(NewSessionActivity.END_DATE);
            String DBdate = data.getExtras().getString(NewSessionActivity.DATA_BASE_DATE);
            String time = data.getExtras().getString(NewSessionActivity.ELAPSED_TIME);
            String interval = data.getExtras().getString(NewSessionActivity.INTERVAL);

            String subject = data.getExtras().getString(NewSessionActivity.SUBJECT);

            newHistoryEntry(subject, time, date, DBdate, true, interval);
        }
    }

    public void addSubjectToDataBase(String subject){
        myDB.insertSubjectRow(subject);
    }



    public void addHistoryToDataBase(String subject, String time, String date, String humanDate, String interval){
       myDB.insertHistoryRow(subject, time, date, humanDate, interval);
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
        Log.d("Main activity", "onDestroy");
    }

    @Override
    public void onStart(){
        super.onStart();
        Intent intent = new Intent(this, TimerService.class);
        startService(intent);


        //Binds activity to timer service
        mConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
                Log.d("MainActivity", "onServiceConnected");
                TimerService.LocalBinder binder = (TimerService.LocalBinder) iBinder;
                timerService = binder.getService();
                boundToTimer = true;

            }

            @Override
            public void onServiceDisconnected(ComponentName componentName) {
                boundToTimer = false;
                Log.d("MainActivity", "onServiceDisconnected");
            }
        };

        Intent intent2 = new Intent(this, TimerService.class);
        bindService(intent2, mConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    public void onStop(){
        super.onStop();
        Intent intent = new Intent(this, TimerService.class);
        unbindService(mConnection);
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState){
        savedInstanceState.putStringArray(SUBJECT_ARRAY, subjectArray);
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onDeleteHistoryConfirmed() {
        HistoryFragment listFrag = (HistoryFragment) getSupportFragmentManager().findFragmentByTag(mAdapter.getListTag());
        if(listFrag != null) {
            listFrag.deleteSession();
        }else{
            HistoryFragment newFragment = new HistoryFragment();

            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.pager, newFragment);
            transaction.commit();
            newFragment.deleteSession();
        }
    }
}

