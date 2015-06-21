package com.matt.studytracker;

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
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by macbookpro on 4/24/15.
 */
public class HomeFragment extends Fragment
{
    public ArrayAdapter<String> homeListAdaptor;
    public String[] classArray = { };
    public List<String> classList = new ArrayList<String>(Arrays.asList(classArray));

    protected TextView subject;
    protected TextView timer;
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

    public void newClass(String newClass)
    {
        homeListAdaptor.add(newClass);
    }


    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        final View rootView = inflater.inflate(R.layout.home_fragment, container, false);

        //initializes adaptor
        homeListAdaptor =
                new SubjectListAdapter(
                        getActivity(),
                        R.layout.home_list_item_layout,
                        R.id.home_list_textview,
                        classList
                );


        //for when item in listView is tapped
        ListView listView = (ListView) rootView.findViewById(R.id.listview_classNames);
        listView.setAdapter(homeListAdaptor);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                subject.setText(homeListAdaptor.getItem(position));

                timerInitiated();
                handler = new Handler();
                updateTimer = new UpdateTimer();
                handler.postDelayed(updateTimer, UPDATE_EVERY);

            }
        });

        //for when stop button is tapped
        final ImageButton button = (ImageButton) rootView.findViewById(R.id.stop_button);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                if(timerRunning == true)
                {
                    handler.removeCallbacks(updateTimer);
                    handler = null;
                    stoppedAt = System.currentTimeMillis();

                    stoppedTime = timer.getText().toString();
                    stoppedSubject =  subject.getText().toString();

                    ((MainActivity)getActivity()).addToHistory(stoppedSubject, stoppedTime);
                }

                timerRunning = false;
            }
        });



        subject = (TextView) rootView.findViewById(R.id.subject_field);
        timer = (TextView) rootView.findViewById(R.id.timer);

        //syncs with data base if not done so already
        if(!syncedWithDB){
            Cursor cursor = ((MainActivity)getActivity()).myDB.getAllSubjectRows();

            Log.d("sy", "test1");
            if(cursor.moveToFirst()){
                do{

                    String subject = cursor.getString(1);

                    Log.d("sy","test");

                    homeListAdaptor.add(subject);

                }while(cursor.moveToNext());

                cursor.close();

            }

            syncedWithDB = true;

        }

        return rootView;

    }

    public void timerInitiated()
    {
        startedAt = System.currentTimeMillis();
        timerRunning = true;

    }

    protected void setTimeDisplay ()
    {
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

        if(display == "0:00:00")
            display = " ";

        timer.setText(display);
    }

    @Override
    public void onStart()
    {
        super.onStart();
        Log.d("Home Fragment", "onStart");
        if(timerRunning)
        {
            handler = new Handler();
            updateTimer = new UpdateTimer();
            handler.postDelayed(updateTimer, UPDATE_EVERY);
        }
    }

    @Override
    public void onPause()
    {
        super.onPause();
        subjectString = subject.getText().toString();
        Log.d("Home Fragment", "onPause");
    }

    @Override
    public void onResume()
    {
        super.onResume();
        setTimeDisplay();
        subject.setText(subjectString);

        Log.d("home fragment", "onResume");
    }

    @Override
    public void onStop()
    {
        super.onStop();
        subjectString = subject.getText().toString();
        if(timerRunning)
        {
            handler.removeCallbacks(updateTimer);
            updateTimer = null;
            handler = null;

        }
        Log.d("home fragment", "onStop");
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        Log.d("home fragment", "onDestroy");
    }


    class UpdateTimer implements Runnable {


        /**
         * Updates the counter display and vibrate if needed.
         * Is called at regular intervals.
         */
        public void run() {
            // Log.d(CLASS_NAME, "run")

            setTimeDisplay();

            if (handler != null) {
                handler.postDelayed(this, UPDATE_EVERY);
            }
        }

    }
}
