package com.matt.studytracker;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TableLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by macbookpro on 4/24/15.
 */
public class HomeFragment extends Fragment
{
    static final String TIMER_RUNNING = "timerRunning";
    static final String START_TIME = "startTime";
    static final String SUBJECT_STRING = "subjectString";

    public ArrayAdapter<String> homeListAdaptor;
    public String[] classArray = { };
    public List<String> classList = new ArrayList<String>(Arrays.asList(classArray));

    protected TextView subject;
    protected TextView timer;
    protected ImageButton stopButton;
    protected TableLayout topView;

    protected boolean timerRunning;
    protected long startedAt;
    protected long stoppedAt;
    protected String subjectString;

    protected boolean syncedWithDB = false;

    protected String stoppedTime;
    protected String stoppedSubject;

    private static long UPDATE_EVERY = 200;

    protected Handler handler;
    protected UpdateTimer updateTimer;

    protected String addedClass;
    protected boolean newClassCalled = false;
    protected boolean removeSubjectCalled = false;

    protected int viewID;
    String subjectToBeRemoved;

    public void newClass(String newClass)
    {
        addedClass = newClass;
        if (homeListAdaptor == null) {
            newClassCalled = true;
        } else{
            homeListAdaptor.add(newClass);
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
        ((MainActivity) getActivity()).setSubjectArray(classList.toArray(new String[classList.size()]));
    }

    StopClicked mCallback;

    public interface StopClicked{
         void sendInfo(String subject, String timeElapsed);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {

        final View rootView = inflater.inflate(R.layout.home_fragment, container, false);
        Log.d("HomeFragment", "OnCreate");

        if(savedInstanceState != null){
            timerRunning = savedInstanceState.getBoolean(TIMER_RUNNING);
            subjectString = savedInstanceState.getString(SUBJECT_STRING);
            startedAt = savedInstanceState.getLong(START_TIME);

        }

        //Checks timer service, syncs if necessary
        if(((MainActivity)getActivity()).getTimerService() != null && ((MainActivity)getActivity()).serviceTiming() && savedInstanceState == null)  {
            timerRunning = true;
            subjectString = ((MainActivity)getActivity()).serviceSubject();
            startedAt = ((MainActivity)getActivity()).serviceTimeStarted();
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

        if(timerRunning){
            topView.setVisibility(View.VISIBLE);
        }

        //for when item in listView is tapped
        ListView listView = (ListView) rootView.findViewById(R.id.listview_classNames);
        listView.setAdapter(homeListAdaptor);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                //Log.d("homeFragment", "list item tapped");
                if(!timerRunning) {
                    subjectString = homeListAdaptor.getItem(position);
                    subject.setText(homeListAdaptor.getItem(position));

                    timerInitiated();
                    handler = new Handler();
                    updateTimer = new UpdateTimer();
                    handler.postDelayed(updateTimer, UPDATE_EVERY);

                    ((MainActivity) getActivity()).startTimerService(subjectString);

                    timer.setText("0:00:00");
                    topView.setVisibility(View.VISIBLE);
                }
            }
        });


        //for when a list item is long-clicked
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener(){
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                Log.d("HomeFragment", "deleteClicked");
                subjectToBeRemoved = homeListAdaptor.getItem(i);
                ((MainActivity) getActivity()).indexToRemove = i;
                ((MainActivity) getActivity()).subjectToRemove = subjectToBeRemoved;

                SubjectLongTappedDialog dialog = new SubjectLongTappedDialog();

                Bundle args = new Bundle();
                args.putString(SubjectLongTappedDialog.SUBJECT_SELECTED, subjectToBeRemoved);
                dialog.setArguments(args);

                dialog.show(getFragmentManager(), String.format("edit/delete subject"));

                return true;
            }
        });

        //for when stop button is tapped
        stopButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                if(timerRunning == true){
                    handler.removeCallbacks(updateTimer);
                    handler = null;
                    stoppedAt = System.currentTimeMillis();

                    stoppedTime = timer.getText().toString();
                    stoppedSubject = subjectString;// subject.getText().toString();

                    Log.d("stoppedSubject", stoppedSubject);
                    Log.d("stopperTime", stoppedTime);

                    mCallback.sendInfo(stoppedSubject, stoppedTime);
                    topView.setVisibility(View.GONE);

                    ((MainActivity) getActivity()).stopTimerService();
                    //((MainActivity) getActivity()).
                }

                timerRunning = false;
            }
        });



        //syncs with data base if not done so already
        if(!syncedWithDB) {
            Cursor cursor = ((MainActivity) getActivity()).myDB.getAllSubjectRows();
            if (cursor.moveToFirst()) {
                do {
                    String subject = cursor.getString(1);
                    homeListAdaptor.add(subject);
                } while (cursor.moveToNext());

                cursor.close();

            }
            syncedWithDB = true;
        }

        if(newClassCalled){
            homeListAdaptor.add(addedClass);
            newClassCalled = false;
        }

        if(removeSubjectCalled){
            homeListAdaptor.remove(subjectToBeRemoved);
            removeSubjectCalled = false;
        }

        setSubjectArray();

        return rootView;
    }

    public void timerInitiated(){
        startedAt = System.currentTimeMillis();
        timerRunning = true;
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
        Log.d("home fragment", "onStop");
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
        //Log.d("home fragment", "onDestroy");
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState){
        Log.d("HomeFragment", "onSaveInstanceState");
        savedInstanceState.putBoolean(TIMER_RUNNING, timerRunning);
        savedInstanceState.putLong(START_TIME, startedAt);
        savedInstanceState.putString(SUBJECT_STRING, subjectString);
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
        /**
         * Updates the counter display and vibrate if needed.
         * Is called at regular intervals.
         */
        public void run() {

            setTimeDisplay();

            if (handler != null) {
                handler.postDelayed(this, UPDATE_EVERY);
            }
        }

    }
}
