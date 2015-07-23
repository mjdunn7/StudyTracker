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
    static final String SUBJECT = "stopped subject";
    static final String TIME = "stopped time";

    protected String stoppedSubject;
    protected String stoppedTime;
    protected String stoppedDate;

    protected boolean addHistoryCalled = false;

    protected HistoryListAdapter historyAdaptor;
    protected HistoryItem[] historyArray = {};
    protected List<HistoryItem> historyList;
    protected HistoryItem history = new HistoryItem();


    protected boolean syncedWithDB = false;

    protected View rootView;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){


        rootView = inflater.inflate(R.layout.list_fragment, container, false);
        Log.d("ListFragment", "onCreateView");

        historyList = new ArrayList<HistoryItem>(Arrays.asList(historyArray));
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
            if(cursor.moveToFirst()){
                do{

                    String subject = cursor.getString(1);
                    String date = cursor.getString(2);
                    String time = cursor.getString(3);

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

        if(addHistoryCalled){
            historyAdaptor.add(history);
            historyAdaptor.notifyDataSetChanged();
        }
        return rootView;


    }

    public void addHistory(String subject, String timeElapsed)
    {
        stoppedSubject = subject;
        stoppedTime = timeElapsed;

        stoppedDate = DateFormat.getDateInstance().format(new Date());

        history.setSubject(subject);
        history.setTimeElapsed(timeElapsed);
        history.setDate(stoppedDate);

        if(historyAdaptor == null){
            Log.d("ListFragment", "historyAdaptor is null");
            addHistoryCalled = true;
        }else{
            historyAdaptor.add(history);
            addHistoryCalled = false;
        }



    }

    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    public void onStop() {
        super.onStop();
    }
}