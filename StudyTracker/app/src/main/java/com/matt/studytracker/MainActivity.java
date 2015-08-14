package com.matt.studytracker;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
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
import android.widget.Spinner;
import android.widget.TextView;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;


public class MainActivity extends ActionBarActivity implements ActionBar.TabListener,
        HomeFragment.StopClicked,
        SubjectLongTappedDialog.DialogListener,
        DeleteSubjectConfirmDialog.DeleteSubjectDialogListener,
        DeleteHistoryDialog.DeleteHistoryDialogListener,
        HistoryLongTappedDialog.DialogListener{
    public static final String SUBJECT_ARRAY = "main activity subject array";

    public DBAdapter myDB;
    private ViewPager viewPager;
    private TabsPagerAdapter mAdapter;
    private ActionBar actionbar;
    private TimerService timerService;
    private boolean boundToTimer = false;

    private String[] subjectArray = {};

    private ServiceConnection mConnection;

    private HistoryItem historyItemToBeEdited;

    String subjectToRemove;
    protected int indexToRemove;

    protected boolean noSubjects = true;

    public TimerService getTimerService(){
        return timerService;
    }

    public void setSubjectArray(String[] array){
        subjectArray = array;
    }

    public String[] getSubjectArray(){
        return subjectArray;
    }

    private String oldSubject;

    @Override
    public void onDeleteSubjectConfirmed() {
        HomeFragment homeFrag = (HomeFragment) getSupportFragmentManager().findFragmentByTag(mAdapter.getHomeTag());
        if(homeFrag != null) {
            homeFrag.removeSubject(indexToRemove);

            //displays a message if last subject has been removed
            TextView notifier = (TextView) findViewById(R.id.no_subjects_message);
            if(homeFrag.classList.isEmpty()){
                notifier.setVisibility(View.VISIBLE);
                noSubjects = true;
            }else {
                notifier.setVisibility(View.GONE);
                noSubjects = false;
            }
        }else{
            HomeFragment newFragment = new HomeFragment();

            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.pager, newFragment, mAdapter.getHomeTag());
            transaction.commit();
            newFragment.removeSubject(indexToRemove);

        }
        myDB.deleteSubjectRow(subjectToRemove);
        myDB.deleteHistoryRows(subjectToRemove);

        HistoryFragment listFrag = (HistoryFragment) getSupportFragmentManager().findFragmentByTag(mAdapter.getListTag());
        if(listFrag != null) {
            listFrag.populateListWithDB(listFrag.spinner.getSelectedItem().toString());

        }

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
    public void onSubjectEditClick(String subjectName, int subjectCreditHours, int subjectDifficulty, int dataBaseID) {
        oldSubject = subjectName;

        Intent intent = new Intent(MainActivity.this, AddClass.class);

        intent.putExtra(AddClass.IS_EDIT, true);
        intent.putExtra(AddClass.CLASS_NAME, subjectName);
        intent.putExtra(AddClass.CREDIT_HOURS, subjectCreditHours);
        intent.putExtra(AddClass.DIFFICULTY_RATING, subjectDifficulty);
        intent.putExtra(AddClass.DATA_BASE_ID, dataBaseID);

        Log.d("MainActivity", "Data base id: " + Integer.toString(dataBaseID));

        MainActivity.this.startActivityForResult(intent, AddClass.ACTIVITY_ID);
    }

    @Override
    public void addToHistory(String subject, String timeElapsed, String interval,
                             int startYear, int startMonth, int startDay, int startHour, int startMinute,
                             int endYear, int endMonth, int endDay, int endHour, int endMinute) {
        Calendar calendar = Calendar.getInstance();
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);

        String date = NewSessionActivity.DAYS[dayOfWeek];
        date += ", " + DateFormat.getDateInstance().format(new Date());
        String DBdate = getCurrentDBdate();
        newHistoryEntry(subject, timeElapsed, date, DBdate, false, interval, startYear,
                startMonth, startDay, startHour, startMinute, endYear, endMonth, endDay,
                endHour, endMinute);
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

    private void newHistoryEntry(String subject, String timeElapsed, String date, String DBdate, boolean manuallyAdded,
                                 String interval, int startYear, int startMonth, int startDay, int startHour, int startMinute,
                                 int endYear, int endMonth, int endDay, int endHour, int endMinute) {
        HistoryFragment listFrag = (HistoryFragment) getSupportFragmentManager().findFragmentByTag(mAdapter.getListTag());
        if(listFrag != null) {
            //Log.d("MainActivity", "listFrag is not null");
            addHistoryToDataBase(subject, timeElapsed, DBdate, date, interval, startYear, startMonth, startDay, startHour,
                    startMinute, endYear, endMonth, endDay, endHour, endMinute);

            listFrag.addHistory(subject, timeElapsed, date, DBdate, manuallyAdded, interval, startYear,
                    startMonth, startDay, startHour, startMinute, endYear, endMonth, endDay,
                    endHour, endMinute);
        }else{
           // Log.d("MainActivity", "listFrag is null");
            addHistoryToDataBase(subject, timeElapsed, DBdate, date, interval, startYear, startMonth, startDay, startHour,
                    startMinute, endYear, endMonth, endDay, endHour, endMinute);

            HistoryFragment newFragment = new HistoryFragment();

            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.pager, newFragment);
            transaction.commit();
            newFragment.addHistory(subject, timeElapsed, date, DBdate, manuallyAdded, interval, startYear,
                    startMonth, startDay, startHour, startMinute, endYear, endMonth, endDay,
                    endHour, endMinute);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if(savedInstanceState != null){
            subjectArray = savedInstanceState.getStringArray(SUBJECT_ARRAY);
            oldSubject = savedInstanceState.getString("OldSubject");
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
        intent.putExtra(AddClass.IS_EDIT, "false");
        MainActivity.this.startActivityForResult(intent, AddClass.ACTIVITY_ID);
    }

    public void addSessionButton(View v){
        if(!noSubjects) {
            Intent intent = new Intent(MainActivity.this, NewSessionActivity.class);
            intent.putExtra(SUBJECT_ARRAY, subjectArray);
            intent.putExtra(NewSessionActivity.IS_EDIT, false);
            MainActivity.this.startActivityForResult(intent, NewSessionActivity.ACTIVITY_ID);
        }else{
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("No Subjects Added");
                builder.setMessage("No existing subjects to add a session for. To get started, tap the Add Subject button in the" +
                        " Home Tab.");
                builder.setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                });

                builder.create().show();
        }
    }

    public void editSessionActivityLaunch(HistoryItem history){
        Intent intent = new Intent(MainActivity.this, NewSessionActivity.class);
        intent.putExtra(SUBJECT_ARRAY, subjectArray);
        intent.putExtra(NewSessionActivity.IS_EDIT, true);

        intent.putExtra(NewSessionActivity.SUBJECT, history.getSubject());

        intent.putExtra(NewSessionActivity.START_YEAR, history.getStartYear());
        intent.putExtra(NewSessionActivity.START_MONTH, history.getStartMonth());
        intent.putExtra(NewSessionActivity.START_DAY, history.getStartDay());
        intent.putExtra(NewSessionActivity.START_HOUR, history.getStartHour());
        intent.putExtra(NewSessionActivity.START_MINUTE, history.getStartMinute());

        intent.putExtra(NewSessionActivity.END_YEAR, history.getEndYear());
        intent.putExtra(NewSessionActivity.END_MONTH, history.getEndMonth());
        intent.putExtra(NewSessionActivity.END_DAY, history.getEndDay());
        intent.putExtra(NewSessionActivity.END_HOUR, history.getEndHour());
        intent.putExtra(NewSessionActivity.END_MINUTE, history.getEndMinute());

        intent.putExtra(NewSessionActivity.ELAPSED_TIME, history.getTimeElapsed());

        intent.putExtra(NewSessionActivity.DB_ID, history.getDataBaseRowID());

        MainActivity.this.startActivityForResult(intent, NewSessionActivity.ACTIVITY_ID);
    }

    public void setHistoryItemToBeEdited(HistoryItem history){
        historyItemToBeEdited = history;
    }

    public void startTimerService(String subject){
        timerService.startTimer(subject);

    }

    public boolean serviceTiming(){
        return timerService.timerRunning;
    }

    public String getServiceSubject(){
        return timerService.timedSubject;
    }

    public String getServiceHumanStartTime(){ return timerService.humanStartTime; }

    public long getServiceTimeStarted(){
        return timerService.startedAt;
    }

    public int[] getServiceStartTimesHolder(){
        return timerService.getStartTimesHolder();
    }


    public void stopTimerService(){
        timerService.stop();
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);

        //for when "add class" activity returns
        if(requestCode == AddClass.ACTIVITY_ID && resultCode == Activity.RESULT_OK) {

            String className = data.getExtras().getString(AddClass.CLASS_NAME);
            String creditHours;
            int intCreditHours;
            int intDifficulty;
            if(data.getExtras().getBoolean(AddClass.IS_CHECKED)){
                creditHours = "";
                intCreditHours = 0;
            }else {
                creditHours = Integer.toString(data.getExtras().getInt(AddClass.CREDIT_HOURS)) + " credits";
                intCreditHours = data.getExtras().getInt(AddClass.CREDIT_HOURS);
            }
            intDifficulty = data.getExtras().getInt(AddClass.DIFFICULTY_RATING);
            String difficulty = "Difficulty Rating: " + Integer.toString(data.getExtras().getInt(AddClass.DIFFICULTY_RATING)) + "/5";

            if(data.getExtras().getBoolean(AddClass.IS_EDIT)){
                //
                // myDB.deleteSubjectRow(data.getExtras().getString(AddClass.CLASS_NAME));
                myDB.updateSubjectRow(data.getExtras().getInt(AddClass.DATA_BASE_ID),
                        className, creditHours, difficulty, intCreditHours, intDifficulty);
                myDB.updateHistoryRowsWithSubject(oldSubject, className);
            }else {
                addSubjectToDataBase(className, creditHours, difficulty, intCreditHours, intDifficulty);
            }

            HomeFragment homeFrag = (HomeFragment) getSupportFragmentManager().findFragmentByTag(mAdapter.getHomeTag());
            if (homeFrag != null) {
                //adds subject to ListView on Home Tab
                homeFrag.populateListWithDB();

                //displays a message if last subject has been removed
                TextView notifier = (TextView) findViewById(R.id.no_subjects_message);
                if(homeFrag.classList.isEmpty()){
                    notifier.setVisibility(View.VISIBLE);
                    noSubjects = true;
                }else {
                    notifier.setVisibility(View.GONE);
                    noSubjects = false;
                }
            }

            HistoryFragment listFrag = (HistoryFragment) getSupportFragmentManager().findFragmentByTag(mAdapter.getListTag());
            if(listFrag != null) {
                Spinner spinner = (Spinner) findViewById(R.id.time_frame_spinner);
                listFrag.populateListWithDB(spinner.getSelectedItem().toString());
            }
        }

        //for when "add session" activity returns
        if(requestCode == NewSessionActivity.ACTIVITY_ID && resultCode == Activity.RESULT_OK){
            String date = data.getExtras().getString(NewSessionActivity.END_DATE);
            String DBdate = data.getExtras().getString(NewSessionActivity.DATA_BASE_DATE);
            String time = data.getExtras().getString(NewSessionActivity.ELAPSED_TIME);
            String interval = data.getExtras().getString(NewSessionActivity.INTERVAL);
            String subject = data.getExtras().getString(NewSessionActivity.SUBJECT);
            int startYear = data.getExtras().getInt(NewSessionActivity.START_YEAR);
            int startMonth = data.getExtras().getInt(NewSessionActivity.START_MONTH);
            int startDay = data.getExtras().getInt(NewSessionActivity.START_DAY);
            int startHour = data.getExtras().getInt(NewSessionActivity.START_HOUR);
            int startMinute = data.getExtras().getInt(NewSessionActivity.START_MINUTE);
            int endYear = data.getExtras().getInt(NewSessionActivity.END_YEAR);
            int endMonth = data.getExtras().getInt(NewSessionActivity.END_MONTH);
            int endDay = data.getExtras().getInt(NewSessionActivity.END_DAY);
            int endHour = data.getExtras().getInt(NewSessionActivity.END_HOUR);
            int endMinute = data.getExtras().getInt(NewSessionActivity.END_MINUTE);

            if(data.getExtras().getBoolean(NewSessionActivity.IS_EDIT)){
                myDB.deleteHistoryRow(data.getExtras().getInt(NewSessionActivity.DB_ID));
            }

            newHistoryEntry(subject, time, date, DBdate, true, interval, startYear, startMonth,
                    startDay, startHour, startMinute, endYear, endMonth, endDay, endHour, endMinute);
        }
    }

    public void addSubjectToDataBase(String subject, String creditHours, String difficultyRating, int intCredits, int intDifficulty){
        myDB.insertSubjectRow(subject, creditHours, difficultyRating, intCredits, intDifficulty);
    }



    public void addHistoryToDataBase(String subject, String time, String date, String humanDate, String interval,
                                     int startYear, int startMonth, int startDay, int startHour, int startMinute,
                                     int endYear, int endMonth, int endDay, int endHour, int endMinute) {
       myDB.insertHistoryRow(subject, time, date, humanDate, interval, startYear, startMonth, startDay, startHour,
               startMinute, endYear, endMonth, endDay, endHour, endMinute);
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
                TimerService.LocalBinder binder = (TimerService.LocalBinder) iBinder;
                timerService = binder.getService();
                boundToTimer = true;

            }

            @Override
            public void onServiceDisconnected(ComponentName componentName) {
                boundToTimer = false;
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
        savedInstanceState.putString("oldSubject", oldSubject);
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

    @Override
    public void onHistoryDeleteClick() {
        DeleteHistoryDialog dialog = new DeleteHistoryDialog();
        dialog.show(getSupportFragmentManager(), DeleteHistoryDialog.TAG);
    }

    @Override
    public void onHistoryCancelClick() {

    }

    @Override
    public void onHistoryEditClick() {
        editSessionActivityLaunch(historyItemToBeEdited);
    }
}

