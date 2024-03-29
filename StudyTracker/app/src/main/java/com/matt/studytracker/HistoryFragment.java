package com.matt.studytracker;

import android.app.Activity;
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
import android.widget.TextView;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

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

    protected String[] timeIntervalArray = {"Day", "Week", "Month", "Six Months", "All Time"};
    protected List<String> timeIntervalList;
    protected ArrayAdapter<String> intervalSpinnerAdaptor;

    protected boolean syncedWithDB = false;

    protected View rootView;

    protected int historyIndexToDelete;

    private ListView historyListView;

    protected Spinner spinner;

    private addSessionListener mListener;

    public interface addSessionListener{
        void newSessionClicked(HistoryItem sessionSelected);
    }

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

        //for when history list item is long-tapped
        historyListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                historyIndexToDelete = i;

                HistoryLongTappedDialog historyLongTappedDialog = new HistoryLongTappedDialog();
                historyLongTappedDialog.show(getFragmentManager(), HistoryLongTappedDialog.TAG);

                ((MainActivity) getActivity()).setHistoryItemToBeEdited(historyList.get(i));
                //DeleteHistoryDialog dialog = new DeleteHistoryDialog();
                //dialog.show(getFragmentManager(), DeleteHistoryDialog.TAG);

                return true;
            }
        });

        historyListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                ((MainActivity) getActivity()).setHistoryItemToBeEdited(historyList.get(i));
                ((MainActivity) getActivity()).onHistoryEditClick();
            }
        });

        final AdView mAdView = (AdView) rootView.findViewById(R.id.adView_history);
        AdRequest adRequest = new AdRequest.Builder()
                //.addTestDevice("D4722643407419B51A4C0F49A926C2C0")
                .build();
        mAdView.loadAd(adRequest);

        mAdView.setAdListener(new AdListener() {
            @Override
            public void onAdFailedToLoad(int errorCode) {
                // Save app state before going to the ad overlay.
                mAdView.setVisibility(View.GONE);
            }

            @Override
            public void onAdLoaded() {
                mAdView.setVisibility(View.VISIBLE);
            }
        });


        return rootView;
    }

    public void deleteSession(){
        ((MainActivity) getActivity()).myDB.deleteHistoryRow(historyList.get(historyIndexToDelete).getDataBaseRowID());
        populateListWithDB(spinner.getSelectedItem().toString());
    }

    public void populateListWithDB(String when){
        Cursor cursor = ((MainActivity)getActivity()).myDB.getAllHistoryRows();
        historyList.clear();

        if (cursor.moveToFirst()) {
            historyAdaptor.notifyDataSetChanged();

            DBTimeHelper helper = new DBTimeHelper();
            long formattedTime = helper.getFormattedPastTime(when);
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

                tempHistory.setStartYear(cursor.getInt(DBAdapter.START_YEAR_COLUMN));
                tempHistory.setStartMonth(cursor.getInt(DBAdapter.START_MONTH_COLUMN));
                tempHistory.setStartDay(cursor.getInt(DBAdapter.START_DAY_COLUMN));
                tempHistory.setStartHour(cursor.getInt(DBAdapter.START_HOUR_COLUMN));
                tempHistory.setStartMinute(cursor.getInt(DBAdapter.START_MINUTE_COLUMN));
                tempHistory.setEndYear(cursor.getInt(DBAdapter.END_YEAR_COLUMN));
                tempHistory.setEndMonth(cursor.getInt(DBAdapter.END_MONTH_COLUMN));
                tempHistory.setEndDay(cursor.getInt(DBAdapter.END_DAY_COLUMN));
                tempHistory.setEndHour(cursor.getInt(DBAdapter.END_HOUR_COLUMN));
                tempHistory.setEndMinute(cursor.getInt(DBAdapter.END_MINUTE_COLUMN));

                if(tempHistory.getDBdate() >= formattedTime) {
                    historyList.add(tempHistory);
                }


            } while (cursor.moveToNext() && tempHistory.getDBdate() >= formattedTime);

            cursor.close();
        }
        historyAdaptor.notifyDataSetChanged();

        TextView notifier = (TextView) rootView.findViewById(R.id.no_history_message);
        if(historyList.isEmpty()){
            notifier.setVisibility(View.VISIBLE);
        }else {
            notifier.setVisibility(View.GONE);
        }

        ((MainActivity) getActivity()).updatePieChart();
    }

    public void addHistory(String subject, String timeElapsed, String date, String DBdate, boolean manuallyAdded, String interval,
                           int startYear, int startMonth, int startDay, int startHour, int startMinute,
                           int endYear, int endMonth, int endDay, int endHour, int endMinute){
        stoppedSubject = subject;
        stoppedTime = timeElapsed;

        history = new HistoryItem();

        history.setSubject(subject);
        history.setTimeElapsed(timeElapsed);
        history.setDate(date);
        history.setDBdate(Long.parseLong(DBdate));
        history.setInterval(interval);

        history.setStartYear(startYear);
        history.setStartMonth(startMonth);
        history.setStartDay(startDay);
        history.setStartHour(startHour);
        history.setStartMinute(startMinute);
        history.setEndYear(endYear);
        history.setEndMonth(endMonth);
        history.setEndDay(endDay);
        history.setEndHour(endHour);
        history.setEndMinute(endMinute);

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
    public void onAttach(Activity activity) {
        super.onAttach(activity);
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