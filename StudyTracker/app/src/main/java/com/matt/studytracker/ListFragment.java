package com.matt.studytracker;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Created by macbookpro on 4/24/15.
 */
public class ListFragment extends Fragment {
    protected String stoppedSubject;
    protected String stoppedTime;
    protected String stoppedDate;
    protected boolean addHistoryCalled;
    protected HistoryListAdapter historyAdaptor;
    protected HistoryItem[] historyArray = {};
    protected List<HistoryItem> historyList = new ArrayList<HistoryItem>(Arrays.asList(historyArray));

    protected boolean syncedWithDB = false;

    protected View rootView;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        rootView = inflater.inflate(R.layout.list_fragment, container, false);

        //stoppedSubject = getArguments().getString("subject");

        historyAdaptor =
                new HistoryListAdapter(
                        getActivity(),
                        R.layout.history_list_item_layout,
                        R.id.history_list_textview,
                        historyList
                );

        ListView historyList = (ListView) rootView.findViewById(R.id.history_list);
        historyList.setAdapter(historyAdaptor);

        if(!syncedWithDB){
            Cursor cursor = ((MainActivity)getActivity()).myDB.getAllHistoryRows();

            Log.d("sy", "test1");
            if(cursor.moveToFirst()){
                do{

                    String subject = cursor.getString(1);
                    String date = cursor.getString(2);
                    String time = cursor.getString(3);

                    Log.d("sy","test");
                    //Log.d("sy", isSubject);

                    //subject = String.format(subject) + String.format("    ") + String.format(time) + String.format("    ") + String.format(date);
                    HistoryItem history = new HistoryItem();
                    history.setSubject(subject);
                    history.setDate(date);
                    history.setTimeElapsed(time);

                    historyAdaptor.add(history);

                }while(cursor.moveToNext());

                cursor.close();

            }

            syncedWithDB = true;

        }

       // historyAdaptor.add(stoppedSubject);

        return rootView;


    }

    public void addHistory(String subject, String timeElapsed)
    {
        stoppedSubject = subject;
        stoppedTime = timeElapsed;
        addHistoryCalled = true;

        stoppedDate = DateFormat.getDateInstance().format(new Date());

        //stoppedSubject = String.format(stoppedSubject) + String.format("    ") + String.format(stoppedTime) + String.format("    ") + String.format(stoppedDate);
        HistoryItem history = new HistoryItem();

        history.setSubject(subject);
        history.setTimeElapsed(timeElapsed);
        history.setDate(stoppedDate);

        historyAdaptor.add(history);
        addHistoryCalled = false;

    }

}