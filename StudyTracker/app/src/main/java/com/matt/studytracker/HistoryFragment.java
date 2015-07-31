package com.matt.studytracker;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by macbookpro on 4/24/15.
 */
public class HistoryFragment extends Fragment {
    static final String SUBJECT = "stopped subject";
    static final String TIME = "stopped time";

    protected String stoppedSubject;
    protected String stoppedTime;
    protected String stoppedDate;

    protected boolean addHistoryCalled = false;

    protected HistoryListAdapter historyAdaptor;
    protected HistoryItem[] historyArray = {};
    protected List<HistoryItem> historyList;
    protected HistoryItem history;

    protected boolean syncedWithDB = false;

    protected View rootView;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){


        rootView = inflater.inflate(R.layout.list_fragment, container, false);
       // Log.d("HistoryFragment", "onCreateView");

        historyList = new ArrayList<HistoryItem>(Arrays.asList(historyArray));
        historyAdaptor =
                new HistoryListAdapter(
                        getActivity(),
                        R.layout.history_list_item_layout,
                        R.id.history_list_textview,
                        historyList
                );

        ListView historyListView = (ListView) rootView.findViewById(R.id.history_list);
        historyListView.setAdapter(historyAdaptor);

        if(!syncedWithDB){
                populateListWithDB();

            }

            syncedWithDB = true;

        if(addHistoryCalled){
            historyList.add(0, history);
            historyAdaptor.notifyDataSetChanged();
        }

        return rootView;


    }

    private void populateListWithDB(){
        Cursor cursor = ((MainActivity)getActivity()).myDB.getAllHistoryRows();
        if(cursor.moveToFirst()) {
            historyList.clear();
            historyAdaptor.notifyDataSetChanged();
            //historyAdaptor.clear();
            do {

                String subject = cursor.getString(DBAdapter.H_SUBJECT_COLUMN);
                String humanDate = cursor.getString(DBAdapter.H_HUMAN_DATE_COLUMN);
                String time = cursor.getString(DBAdapter.H_TIME_ELAPSED_COLUMN);

                HistoryItem history = new HistoryItem();
                history.setSubject(subject);
                history.setDate(humanDate);
                history.setTimeElapsed(time);

                historyList.add(history);
                historyAdaptor.notifyDataSetChanged();

            } while (cursor.moveToNext());

            cursor.close();
        }
    }

    public void addHistory(String subject, String timeElapsed, String date, boolean manuallyAdded)
    {
        stoppedSubject = subject;
        stoppedTime = timeElapsed;

        history = new HistoryItem();

        history.setSubject(subject);
        history.setTimeElapsed(timeElapsed);
        history.setDate(date);

        if(manuallyAdded && historyAdaptor != null){
            populateListWithDB();
        }else {
            if (historyAdaptor == null) {
                //Log.d("HistoryFragment", "historyAdaptor is null");
                addHistoryCalled = true;
            } else {
                historyList.add(0, history);
                //historyAdaptor.add(history);
                historyAdaptor.notifyDataSetChanged();
                addHistoryCalled = false;
            }
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