package com.matt.studytracker;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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
    protected Vector<String> uniqueSubjects = new Vector<String>();

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.chart_fragment, container, false);
    }

    public String calculateTotals() {
        Cursor historyCursor = ((MainActivity) getActivity()).myDB.getAllHistoryRows();
        Cursor subjectCursor = ((MainActivity) getActivity()).myDB.getAllSubjectRows();

        int subjectSize = subjectCursor.getCount();
        int historySize = historyCursor.getCount();
        String[][] uniqueSubjects;
        uniqueSubjects = new String[subjectSize][historySize];


        String totals = "";
        boolean vectorAccessed = false;
        int subjectLocation = 0;

        if (subjectCursor.moveToFirst()) {
            do {

                String subject = subjectCursor.getString(1);
                uniqueSubjects[subjectLocation][0] = subject;
                ++subjectLocation;

            } while (subjectCursor.moveToNext());

            subjectCursor.close();
        }

        int[] sizes;
        sizes = new int[subjectSize];
        if (historyCursor.moveToFirst()) {
            do {

            } while (historyCursor.moveToNext());

            historyCursor.close();
        }
        return totals;
    }

    private String getHours(Vector<String> rawTimes ){
        String hours = "";

        for(int i = 0; i < rawTimes.size(); ++i){
            
        }

        return hours;
    }


    public String calculatePercentages() {
        String percentages = "";
        return percentages;
    }
}