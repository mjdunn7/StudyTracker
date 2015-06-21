package com.matt.studytracker;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.Vector;

/**
 * Created by macbookpro on 4/24/15.
 */
public class ChartFragment extends Fragment {
    protected int historyCount = 0;
    protected Vector<String> times = new Vector<String>();

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.chart_fragment, container, false);
    }

    public String calculateTotals() {
        Cursor cursor = ((MainActivity) getActivity()).myDB.getAllHistoryRows();

        String totals = "";

        Log.d("sy", "test1");
        if (cursor.moveToFirst()) {
            do {

                String time = cursor.getString(3);

                times.add(time);
                ++historyCount;
            } while (cursor.moveToNext());

            cursor.close();


        }
        return totals;
    }

    private String getHours(Vector<String> rawTimes ){
        String hours = "";

        for(int i = 0; i < rawTimes.size(); ++i){
            
        }

        return hours;
    }


    public String calculatenPercentages() {
        String percentages = "";
        return percentages;
    }
}