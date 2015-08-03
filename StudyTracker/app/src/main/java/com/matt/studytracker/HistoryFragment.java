package com.matt.studytracker;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
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

    protected String[] timeIntervalArray = {"Day", "Week", "Month", "Six Months", "All Time"};
    protected List<String> timeIntervalList;
    protected ArrayAdapter<String> intervalSpinnerAdaptor;

    protected boolean syncedWithDB = false;

    protected View rootView;

    protected int historyIndexToDelete;

    private ListView historyListView;

    protected Spinner spinner;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){


        rootView = inflater.inflate(R.layout.history_fragment, container, false);
       // Log.d("HistoryFragment", "onCreateView");

        timeIntervalList = new ArrayList<>(Arrays.asList(timeIntervalArray));
        intervalSpinnerAdaptor =
                new ArrayAdapter<String>(
                        this.getActivity(),
                        R.layout.subject_chooser,
                        R.id.subject_chooser_textview,
                        timeIntervalList
                );

        spinner = (Spinner) rootView.findViewById(R.id.time_frame_spinner);
        spinner.setAdapter(intervalSpinnerAdaptor);
        spinner.setSelection(1);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                populateListWithDB(intervalSpinnerAdaptor.getItem(i));
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        historyList = new ArrayList<HistoryItem>(Arrays.asList(historyArray));
        historyAdaptor =
                new HistoryListAdapter(
                        this.getActivity(),
                        R.layout.history_list_item_layout,
                        R.id.history_list_textview,
                        historyList
                );

        historyListView = (ListView) rootView.findViewById(R.id.history_list);
        historyListView.setAdapter(historyAdaptor);

        if(!syncedWithDB){
                populateListWithDB(spinner.getSelectedItem().toString());
                syncedWithDB = true;
            }

        historyListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                historyIndexToDelete = i;
                DeleteHistoryDialog dialog = new DeleteHistoryDialog();
                dialog.show(getFragmentManager(), DeleteHistoryDialog.TAG);

                return true;
            }
        });

        return rootView;


    }

    public void deleteSession(){
        ((MainActivity) getActivity()).myDB.deleteHistoryRow(historyList.get(historyIndexToDelete).getDataBaseRowID());
        populateListWithDB(spinner.getSelectedItem().toString());
    }

    private void populateListWithDB(String when){
        Cursor cursor = ((MainActivity)getActivity()).myDB.getAllHistoryRows();
        historyList.clear();

        if (cursor.moveToFirst()) {
            historyAdaptor.notifyDataSetChanged();

            long formattedTime = getFormattedPastTime(when);
            HistoryItem tempHistory;
            do {

                String subject = cursor.getString(DBAdapter.H_SUBJECT_COLUMN);
                String humanDate = cursor.getString(DBAdapter.H_HUMAN_DATE_COLUMN);
                String time = cursor.getString(DBAdapter.H_TIME_ELAPSED_COLUMN);
                String interval = cursor.getString(DBAdapter.H_TIME_INTERVALS_COLUMN);
                int id = cursor.getInt(DBAdapter.ROW_ID_COLUMN);

                tempHistory = new HistoryItem();
                tempHistory.setSubject(subject);
                tempHistory.setDate(humanDate);
                tempHistory.setTimeElapsed(time);
                tempHistory.setDBdate(Long.parseLong(cursor.getString(DBAdapter.H_DATE_COLUMN)));
                tempHistory.setDataBaseRowID(id);
                tempHistory.setInterval(interval);

                if(tempHistory.getDBdate() >= formattedTime) {
                    historyList.add(tempHistory);
                }


            } while (cursor.moveToNext() && tempHistory.getDBdate() >= formattedTime);

            cursor.close();
        }
        historyAdaptor.notifyDataSetChanged();
    }

    private long getFormattedPastTime(String when){

        Calendar calendar = Calendar.getInstance();
        long time = 0;

        if(when.equals("Day")){
            calendar.add(Calendar.DAY_OF_YEAR, -1);
            time = Long.parseLong(getFormattedDate(calendar));
        }

        if(when.equals("Week")){
            calendar.add(Calendar.DAY_OF_YEAR, -7);
            time = Long.parseLong(getFormattedDate(calendar));
        }

        if(when.equals("Month")){
            calendar.add(Calendar.MONTH, -1);
            time = Long.parseLong(getFormattedDate(calendar));
        }

        if(when.equals("Six Months")){
            calendar.add(Calendar.MONTH, -6);
            time = Long.parseLong(getFormattedDate(calendar));
        }

        if(when.equals("All Time")){
            time = 0;
        }

        return time;
    }

    private String getFormattedDate(Calendar calendar){
        String currentDate;
        currentDate = Integer.toString(Calendar.getInstance().get(Calendar.YEAR));

        if(calendar.get(Calendar.MONTH) < 10){
            currentDate += "0" + calendar.get(Calendar.MONTH);
        }else {
            currentDate += Integer.toString(calendar.get(Calendar.MONTH));
        }

        if(calendar.get(Calendar.DAY_OF_MONTH) < 10){
            currentDate += "0" + Integer.toString(calendar.get(Calendar.DAY_OF_MONTH));
        }else {
            currentDate += Integer.toString(calendar.get(Calendar.DAY_OF_MONTH));
        }

        currentDate += "235900";
        return currentDate;
    }

    public void addHistory(String subject, String timeElapsed, String date, String DBdate, boolean manuallyAdded, String interval) {
        stoppedSubject = subject;
        stoppedTime = timeElapsed;

        history = new HistoryItem();

        history.setSubject(subject);
        history.setTimeElapsed(timeElapsed);
        history.setDate(date);
        history.setDBdate(Long.parseLong(DBdate));
        history.setInterval(interval);

        //To be executed if the user manually added the session
        if (manuallyAdded && historyAdaptor != null) {

            populateListWithDB(spinner.getSelectedItem().toString());
            addHistoryCalled = false;
        } else {
            if (historyAdaptor == null) {
                syncedWithDB = false;
            } else {
                populateListWithDB(spinner.getSelectedItem().toString());
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