package com.matt.studytracker;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TableLayout;
import android.widget.TextView;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

/**
 * Created by macbookpro on 4/24/15.
 */
public class HomeFragment extends Fragment
{
    static final String TIMER_RUNNING = "timerRunning";
    static final String REVERSE_TIMER_RUNNING = "reverse timer runnin";
    static final String START_TIME = "startTime";
    static final String SUBJECT_STRING = "subjectString";
    static final String HUMAN_START_TIME = "human end time";
    static final String WILL_END_TIME = "will end time";

    public static final String START_YEAR = "start year";
    public static final String START_MONTH = "start month";
    public static final String START_DAY = "start day";
    public static final String START_HOUR = "start hour";
    public static final String START_MINUTE = "start minute";

    public static final String END_YEAR = "end year";
    public static final String END_MONTH = "end month";
    public static final String END_DAY = "end day";
    public static final String END_HOUR = "end hour";
    public static final String END_MINUTE = "end minute";

    public SubjectListAdapter homeListAdaptor;
    public Subject[] classArray = { };
    public List<Subject> classList = new ArrayList<Subject>(Arrays.asList(classArray));

    protected TextView subject;
    protected TextView timer;
    protected ImageButton stopButton;
    protected TableLayout topView;

    protected boolean reverseTimerRunning;
    protected boolean timerRunning;
    protected long startedAt;
    protected long stoppedAt;
    protected long willStopAt;
    protected String subjectString;

    protected String startHumanTime;
    protected String endHumanTime;

    protected boolean syncedWithDB = false;

    protected String stoppedTime;
    protected String stoppedSubject;

    private static long UPDATE_EVERY = 200;

    protected Handler handler;
    protected Handler reverseHandler;
    protected UpdateTimer updateTimer;
    protected UpdateReverseTimer updateReverseTimer;

    protected Subject addedClass;
    protected boolean newClassCalled = false;
    protected boolean removeSubjectCalled = false;

    protected int viewID;
    protected Subject subjectToBeRemoved;

    private int startYear;
    private int startMonth;
    private int startDay;
    private int startHour;
    private int startMinute;
    private int endYear;
    private int endMonth;
    private int endDay;
    private int endHour;
    private int endMinute;

    protected String readableTimeElapsed;

    public void newClass(String newClass, String creditHours, String difficulty, int intCredits, int intDifficulty)
    {
        addedClass = new Subject();

        addedClass.setSubject(newClass);
        addedClass.setCreditHours(creditHours);
        addedClass.setDifficulty(difficulty);
        addedClass.setIntCredits(intCredits);
        addedClass.setIntDifficulty(intDifficulty);

        if (homeListAdaptor == null) {
            newClassCalled = true;
        } else{
            homeListAdaptor.add(addedClass);
            newClassCalled = false;
            setSubjectArray();

        }

    }

    public void removeSubject(int index){
        if (homeListAdaptor == null){
            removeSubjectCalled = true;
        } else{
            homeListAdaptor.remove(subjectToBeRemoved);
            setSubjectArray();
        }
    }

    public void setSubjectArray(){
        String[] subjectArray = new String[classList.size()];
        for(int i = 0; i < classList.size(); ++i){
            subjectArray[i] = classList.get(i).getSubject();
        }
        ((MainActivity) getActivity()).setSubjectArray(subjectArray);
    }

    StopClicked mCallback;

    public interface StopClicked{
         void addToHistory(String subject, String timeElapsed, String interval, int startYear,
                           int startMonth, int startDay, int startHour, int startMinute,
                           int endYear, int endMonth, int endDay, int endHour, int endMinute);
    }

    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {

        final View rootView = inflater.inflate(R.layout.home_fragment, container, false);

        addedClass = new Subject();

        if(savedInstanceState != null){
            timerRunning = savedInstanceState.getBoolean(TIMER_RUNNING);
            reverseTimerRunning = savedInstanceState.getBoolean(REVERSE_TIMER_RUNNING);
            willStopAt = savedInstanceState.getLong(WILL_END_TIME);

            subjectString = savedInstanceState.getString(SUBJECT_STRING);
            startedAt = savedInstanceState.getLong(START_TIME);
            startHumanTime = savedInstanceState.getString(HUMAN_START_TIME);

            startYear = savedInstanceState.getInt(START_YEAR);
            startMonth = savedInstanceState.getInt(START_MONTH);
            startDay = savedInstanceState.getInt(START_DAY);
            startHour = savedInstanceState.getInt(START_HOUR);
            startMinute = savedInstanceState.getInt(START_MINUTE);

            endYear = savedInstanceState.getInt(END_YEAR);
            endMonth = savedInstanceState.getInt(END_MONTH);
            endDay = savedInstanceState.getInt(END_DAY);
            endHour = savedInstanceState.getInt(END_HOUR);
            endMinute = savedInstanceState.getInt(END_MINUTE);

        }


        //Checks timer service, syncs if necessary
        if(((MainActivity)getActivity()).getTimerService() != null && ((MainActivity)getActivity()).serviceTiming() && savedInstanceState == null)  {
            timerRunning = true;
            subjectString = ((MainActivity)getActivity()).getServiceSubject();
            startedAt = ((MainActivity)getActivity()).getServiceTimeStarted();
            startHumanTime = ((MainActivity)getActivity()).getServiceHumanStartTime();

            int[] tempStartTimes = ((MainActivity)getActivity()).getServiceStartTimesHolder();

            startYear = tempStartTimes[0];
            startMonth = tempStartTimes[1];
            startDay = tempStartTimes[2];
            startHour = tempStartTimes[3];
            startMinute = tempStartTimes[4];
        }

        if(((MainActivity)getActivity()).getTimerService() != null && ((MainActivity)getActivity()).serviceReverseTiming() && savedInstanceState == null)  {
            reverseTimerRunning = true;
            subjectString = ((MainActivity)getActivity()).getServiceSubject();
            startedAt = ((MainActivity)getActivity()).getServiceTimeStarted();
            startHumanTime = ((MainActivity)getActivity()).getServiceHumanStartTime();

            willStopAt = ((MainActivity)getActivity()).getServiceWillStopAt();

            int[] tempStartTimes = ((MainActivity)getActivity()).getServiceStartTimesHolder();

            startYear = tempStartTimes[0];
            startMonth = tempStartTimes[1];
            startDay = tempStartTimes[2];
            startHour = tempStartTimes[3];
            startMinute = tempStartTimes[4];
        }

        //Initializes adaptor
        homeListAdaptor =
                new SubjectListAdapter(
                        getActivity(),
                        R.layout.home_list_item_layout,
                        R.id.home_list_textview,
                        classList
                );

        //Initialization of UI elements
        subject = (TextView) rootView.findViewById(R.id.subject_field);
        timer = (TextView) rootView.findViewById(R.id.timer);
        stopButton = (ImageButton) rootView.findViewById(R.id.stop_button);
        topView = (TableLayout) rootView.findViewById(R.id.topLayout);

        if(timerRunning || reverseTimerRunning){
            topView.setVisibility(View.VISIBLE);
        }

        //for when item in listView is tapped
        ListView listView = (ListView) rootView.findViewById(R.id.listview_classNames);
        listView.setAdapter(homeListAdaptor);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                if(!timerRunning && !reverseTimerRunning) {
                    subjectString = homeListAdaptor.getItem(position).getSubject();
                    subject.setText(homeListAdaptor.getItem(position).getSubject());

                    Switch countdownMode = (Switch) rootView.findViewById(R.id.switch1);
                    if(countdownMode.isChecked()) {
                        //backwardsTimerInitiated(0, 20);
                        CountdownTimeDialog dialog = new CountdownTimeDialog();
                        dialog.show(getFragmentManager(), CountdownTimeDialog.TAG);
                    }
                    else {

                        timerInitiated();
                    }

                }
            }
        });


        //for when a list item is long-clicked
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener(){
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                subjectToBeRemoved = homeListAdaptor.getItem(i);
                ((MainActivity) getActivity()).indexToRemove = i;
                ((MainActivity) getActivity()).subjectToRemove = subjectToBeRemoved.getSubject();

                SubjectLongTappedDialog dialog = new SubjectLongTappedDialog();

                Bundle args = new Bundle();
                args.putString(SubjectLongTappedDialog.SUBJECT_SELECTED, subjectToBeRemoved.getSubject());
                args.putInt(SubjectLongTappedDialog.CREDIT_HOURS, subjectToBeRemoved.getIntCredits());
                args.putInt(SubjectLongTappedDialog.DIFFICULTY_RATING, subjectToBeRemoved.getIntDifficulty());
                args.putInt(SubjectLongTappedDialog.DATA_BASE_ID, subjectToBeRemoved.getDataBaseID());

                dialog.setArguments(args);

                dialog.show(getFragmentManager(), String.format("edit/delete subject"));

                return true;
            }
        });

        //for when stop button is tapped
        stopButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                stopTimer();
            }
        });

        //syncs with data base if not done so already
        if(!syncedWithDB) {
            populateListWithDB();
            syncedWithDB = true;

        }

        //displays a message if no subjects have been added
        TextView notifier = (TextView) rootView.findViewById(R.id.no_subjects_message);
        if(classList.isEmpty()){
            notifier.setVisibility(View.VISIBLE);
            ((MainActivity) getActivity()).noSubjects = true;
        }else {
            notifier.setVisibility(View.GONE);
            ((MainActivity) getActivity()).noSubjects = false;
        }

        if(newClassCalled){
            homeListAdaptor.add(addedClass);
            newClassCalled = false;

            //removes message saying that no subjects have been added if message exists
            notifier = (TextView) rootView.findViewById(R.id.no_subjects_message);
            notifier.setVisibility(View.GONE);
            ((MainActivity) getActivity()).noSubjects = false;
        }

        if(removeSubjectCalled){
            homeListAdaptor.remove(subjectToBeRemoved);
            removeSubjectCalled = false;

            //displays a message if last subject has been removed
            notifier = (TextView) rootView.findViewById(R.id.no_subjects_message);
            if(classList.isEmpty()){
                notifier.setVisibility(View.VISIBLE);
                ((MainActivity) getActivity()).noSubjects = true;
            }else {
                notifier.setVisibility(View.GONE);
                ((MainActivity) getActivity()).noSubjects = false;
            }
        }

        setSubjectArray();



        final AdView mAdView = (AdView) rootView.findViewById(R.id.adView);

        AdRequest adRequest = new AdRequest.Builder()
               // .addTestDevice("D4722643407419B51A4C0F49A926C2C0")
                .build();

        mAdView.loadAd(adRequest);

        mAdView.setAdListener(new AdListener() {
            @Override
            public void onAdFailedToLoad(int errorCode) {
                // Save app state before going to the ad overlay.
                mAdView.setVisibility(View.GONE);
            }

            @Override
            public void onAdLoaded(){
                mAdView.setVisibility(View.VISIBLE);
            }
        });


        return rootView;
    }

    public void stopTimer() {
        if(timerRunning == true || reverseTimerRunning == true) {
            if (timerRunning) {
                handler.removeCallbacks(updateTimer);
                handler = null;
            }
            if (reverseTimerRunning) {
                reverseHandler.removeCallbacks(updateReverseTimer);
                reverseHandler = null;
            }
            stoppedAt = System.currentTimeMillis();

            String tempStoppedTime = readableTimeElapsed;
            stoppedTime = "";

            boolean firstColonHit = false;

            for (int i = 0; i < tempStoppedTime.length(); ++i) {

                if (tempStoppedTime.charAt(i) == ':' && firstColonHit == true) {
                    i = tempStoppedTime.length();
                } else if (tempStoppedTime.charAt(i) == ':' && firstColonHit == false) {
                    firstColonHit = true;
                    stoppedTime += Character.toString(tempStoppedTime.charAt(i));
                } else {
                    stoppedTime += Character.toString(tempStoppedTime.charAt(i));
                }
            }

            stoppedSubject = subjectString;

            int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
            int minute = Calendar.getInstance().get(Calendar.MINUTE);
            DBTimeHelper helper = new DBTimeHelper();
            endHumanTime = helper.getReadableTime(hour, minute);

            String interval = startHumanTime + " - " + endHumanTime;

            Calendar calendar = Calendar.getInstance();
            endYear = calendar.get(Calendar.YEAR);
            endMonth = calendar.get(Calendar.MONTH);
            endDay = calendar.get(Calendar.DAY_OF_MONTH);
            endHour = calendar.get(Calendar.HOUR_OF_DAY);
            endMinute = calendar.get(Calendar.MINUTE);

            mCallback.addToHistory(stoppedSubject, stoppedTime, interval, startYear, startMonth, startDay,
                    startHour, startMinute, endYear, endMonth, endDay, endHour, endMinute);

            Animation animation = AnimationUtils.loadAnimation(getActivity(), R.anim.abc_fade_out);
            animation.setDuration(500);
            topView.startAnimation(animation);
            topView.postDelayed(new Runnable() {
                @Override
                public void run() {
                    topView.setVisibility(View.GONE);
                }
            }, 500);

            ((MainActivity) getActivity()).stopTimerService();

            timerRunning = false;
            reverseTimerRunning = false;
        }
    }

    public void populateListWithDB(){
        Cursor cursor = ((MainActivity) getActivity()).myDB.getAllSubjectRows();
        if (cursor.moveToFirst()) {
            homeListAdaptor.clear();
            do {
                String subject = cursor.getString(DBAdapter.SUBJECT_COLUMN);
                String creditHours = cursor.getString(DBAdapter.CREDIT_HOURS_COLUMN);
                String difficulty = cursor.getString(DBAdapter.DIFFICULTY_RATING_COLUMN);

                Subject subjectItem = new Subject();
                subjectItem.setSubject(subject);
                subjectItem.setCreditHours(creditHours);
                subjectItem.setDifficulty(difficulty);

                subjectItem.setIntDifficulty(cursor.getInt(DBAdapter.INT_DIFFICULTY_COLUMN));
                subjectItem.setIntCredits(cursor.getInt(DBAdapter.INT_CREDIT_HOURS_COLUMN));

                subjectItem.setDataBaseID(cursor.getInt(DBAdapter.ROW_ID_COLUMN));

                homeListAdaptor.add(subjectItem);
            } while (cursor.moveToNext());

            cursor.close();

        }
    }

    public void timerInitiated(){
        startedAt = System.currentTimeMillis();

        int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        int minute = Calendar.getInstance().get(Calendar.MINUTE);
        DBTimeHelper helper = new DBTimeHelper();
        startHumanTime = helper.getReadableTime(hour, minute);

        Calendar calendar = Calendar.getInstance();
        startYear = calendar.get(Calendar.YEAR);
        startMonth = calendar.get(Calendar.MONTH);
        startDay = calendar.get(Calendar.DAY_OF_MONTH);
        startHour = calendar.get(Calendar.HOUR_OF_DAY);
        startMinute = calendar.get(Calendar.MINUTE);

        timerRunning = true;

        handler = new Handler();
        updateTimer = new UpdateTimer();
        handler.postDelayed(updateTimer, UPDATE_EVERY);

        ((MainActivity) getActivity()).startTimerService(subjectString);

        timer.setText("0:00:00");
        topView.setVisibility(View.VISIBLE);
        Animation animation = AnimationUtils.loadAnimation(getActivity(), R.anim.abc_slide_in_bottom);
        topView.startAnimation(animation);
    }

    protected void setTimeDisplay(){
        String display;
        long timeNow;
        long diff;
        long seconds;
        long minutes;
        long hours;

        if(timerRunning)
            timeNow = System.currentTimeMillis();
        else
            timeNow = stoppedAt;

        diff = timeNow - startedAt;

        if(diff < 0 )
            diff = 0;

        seconds = diff/1000;
        minutes = seconds/60;
        hours = minutes/60;
        seconds = seconds % 60;
        minutes = minutes % 60;

        display = String.format("%d",hours) + ":"
                + String.format("%02d", minutes) + ":"
                + String.format("%02d", seconds);

        readableTimeElapsed = display;

        timer.setText(display);
    }

    @Override
    public void onStart(){
        super.onStart();
        if(timerRunning){
            handler = new Handler();
            updateTimer = new UpdateTimer();
            handler.postDelayed(updateTimer, UPDATE_EVERY);
        }

        if(reverseTimerRunning){
            reverseHandler = new Handler();
            updateReverseTimer = new UpdateReverseTimer();
            reverseHandler.postDelayed(updateReverseTimer, UPDATE_EVERY);
        }
    }

    @Override
    public void onStop(){
        super.onStop();
        subjectString = subject.getText().toString();
        if(timerRunning){
            handler.removeCallbacks(updateTimer);
            updateTimer = null;
            handler = null;

        }

        if(reverseTimerRunning){
            reverseHandler.removeCallbacks(updateReverseTimer);
            updateReverseTimer = null;
            reverseHandler = null;
        }
    }

    @Override
    public void onPause(){
        subjectString = subject.getText().toString();
        super.onPause();
    }

    @Override
    public void onResume(){
        super.onResume();
        setTimeDisplay();
        subject.setText(subjectString);
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState){
        savedInstanceState.putBoolean(TIMER_RUNNING, timerRunning);
        savedInstanceState.putBoolean(REVERSE_TIMER_RUNNING, reverseTimerRunning);
        savedInstanceState.putLong(WILL_END_TIME, willStopAt);
        savedInstanceState.putLong(START_TIME, startedAt);
        savedInstanceState.putString(SUBJECT_STRING, subjectString);
        savedInstanceState.putString(HUMAN_START_TIME, startHumanTime);

        savedInstanceState.putInt(START_YEAR, startYear);
        savedInstanceState.putInt(START_MONTH, startMonth);
        savedInstanceState.putInt(START_DAY, startDay);
        savedInstanceState.putInt(START_HOUR, startHour);
        savedInstanceState.putInt(START_MINUTE, startMinute);

        savedInstanceState.putInt(END_YEAR, endYear);
        savedInstanceState.putInt(END_MONTH, endMonth);
        savedInstanceState.putInt(END_DAY, endDay);
        savedInstanceState.putInt(END_HOUR, endHour);
        savedInstanceState.putInt(END_MINUTE, endMinute);

        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try{
            mCallback = (StopClicked) activity;
        }catch (ClassCastException e){
            throw new ClassCastException(activity.toString() + "must implement StopClicked");
        }
    }

    class UpdateTimer implements Runnable {
        public void run() {

            setTimeDisplay();

            if (handler != null) {
                handler.postDelayed(this, UPDATE_EVERY);
            }
        }

    }

    private long getFutureTime(int hours, int minutes){
        long timeNow = System.currentTimeMillis();

        long millisTime = (long) (hours * 3600000) + (minutes * 60000);

        long futureTime = timeNow + millisTime;

        return futureTime;
    }

    public void backwardsTimerInitiated(int hours, int minutes, boolean vibrate){
        startedAt = System.currentTimeMillis();
        willStopAt = getFutureTime(hours, minutes);

        int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        int minute = Calendar.getInstance().get(Calendar.MINUTE);
        DBTimeHelper helper = new DBTimeHelper();
        startHumanTime = helper.getReadableTime(hour, minute);

        Calendar calendar = Calendar.getInstance();
        startYear = calendar.get(Calendar.YEAR);
        startMonth = calendar.get(Calendar.MONTH);
        startDay = calendar.get(Calendar.DAY_OF_MONTH);
        startHour = calendar.get(Calendar.HOUR_OF_DAY);
        startMinute = calendar.get(Calendar.MINUTE);

        reverseTimerRunning = true;

        reverseHandler = new Handler();
        updateReverseTimer = new UpdateReverseTimer();
        reverseHandler.postDelayed(updateReverseTimer, UPDATE_EVERY);

        ((MainActivity) getActivity()).startReverseTimerService(subjectString, hours, minutes, vibrate);

        timer.setText("0:00:00");
        topView.setVisibility(View.VISIBLE);
        Animation animation = AnimationUtils.loadAnimation(getActivity(), R.anim.abc_slide_in_bottom);
        topView.startAnimation(animation);
    }

    protected void setCountdownTimeDisplay(){
        String display;
        long timeNow;
        long diff;
        long seconds;
        long minutes;
        long hours;

        if(reverseTimerRunning)
            timeNow = System.currentTimeMillis();
        else
            timeNow = stoppedAt;

        diff = willStopAt - timeNow;

        if(diff < 0 )
            diff = 0;

        seconds = diff/1000;
        minutes = seconds/60;
        hours = minutes/60;
        seconds = seconds % 60;
        minutes = minutes % 60;

        display = String.format("%d",hours) + ":"
                + String.format("%02d", minutes) + ":"
                + String.format("%02d", seconds);

        if(diff <= 0)
            display = "0:00:00";

        if(!reverseTimerRunning)
            display = "0:00:00";

        timer.setText(display);

        long timeElapsed = timeNow - startedAt;

        seconds = timeElapsed/1000;
        minutes = seconds/60;
        hours = minutes/60;
        seconds = seconds % 60;
        minutes = minutes % 60;

        readableTimeElapsed = String.format("%d",hours) + ":"
                + String.format("%02d", minutes) + ":"
                + String.format("%02d", seconds);

        if( diff == 0){
            //stopTimer();
            reverseTimerRunning = false;
            Animation animation = AnimationUtils.loadAnimation(getActivity(), R.anim.abc_fade_out);
            animation.setDuration(500);
            topView.startAnimation(animation);
            topView.postDelayed(new Runnable() {
                @Override
                public void run() {
                    topView.setVisibility(View.GONE);
                }
            }, 500);
        }

    }

    class UpdateReverseTimer implements Runnable {
        public void run() {

            setCountdownTimeDisplay();

            if (reverseHandler != null) {
                reverseHandler.postDelayed(this, UPDATE_EVERY);
            }
        }

    }



}
