package com.matt.studytracker;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Vector;

/**
 * Created by macbookpro on 4/24/15.
 */
public class ChartFragment extends Fragment {
    protected TextView textView;
    protected int historyCount = 0;
    protected Vector<String> times = new Vector<String>();
    protected Vector<String> uniqueSubjects = new Vector<String>();

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.chart_fragment, container, false);
        textView = (TextView) rootView.findViewById(R.id.analytics);
        textView.setText(calculateTotals());

        return rootView;
    }

    public String calculateTotals() {
        Cursor historyCursor = ((MainActivity) getActivity()).myDB.getAllHistoryRows();
        Cursor subjectCursor = ((MainActivity) getActivity()).myDB.getAllSubjectRows();

        int subjectSize = subjectCursor.getCount();
        int historySize = historyCursor.getCount();
        Log.d("historySize", Integer.toString(historyCursor.getCount()));
        String[][] uniqueSubjects;
        uniqueSubjects = new String[subjectSize][historySize + 1];


        int subjectLocation = 0;

        //Shoves all of the subjects in the database into the string array on the x-axis
        if (subjectCursor.moveToFirst()) {
            do {

                String subject = subjectCursor.getString(1);
                uniqueSubjects[subjectLocation][0] = subject;
                ++subjectLocation;

            } while (subjectCursor.moveToNext());
        }

        //Matches the times in the history database with the corresponding subjects in the y-axis
        int[] amountOfTimes;
        amountOfTimes = new int[subjectSize];
        int timeLocations = 1;
        for(int i = 0; i < (subjectSize); ++i) {
            if (historyCursor.moveToFirst()) {
                do {
                    if(historyCursor.getString(1).replaceAll("\\s+","").equals(uniqueSubjects[i][0].replaceAll("\\s+",""))){
                        uniqueSubjects[i][timeLocations] = historyCursor.getString(3);
                        ++timeLocations;
                        Log.d("timeLocations", Integer.toString(timeLocations));
                        Log.d("US[i][timeLocations]", uniqueSubjects[i][timeLocations - 1]);
                        Log.d("US[i][0]", uniqueSubjects[i][0]);
                    }
                } while (historyCursor.moveToNext());
                amountOfTimes[i] = timeLocations;
                timeLocations = 1;
            }
        }

        //Calculates the amount of hours in numerical form from the array of strings for each respective subject.
        String totalTimes = "";
        double minutes = 0;
        for(int i = 0; i < subjectSize; ++i){
            totalTimes += String.format(uniqueSubjects[i][0]);
            for(int j = 1; j < amountOfTimes[i]; ++j){
                minutes += (Character.getNumericValue(uniqueSubjects[i][j].charAt(0)) * 60);
                minutes += (Character.getNumericValue(uniqueSubjects[i][j].charAt(2)) * 10);
                minutes += (Character.getNumericValue(uniqueSubjects[i][j].charAt(3)));
            }
            totalTimes += String.format("     ") + String.format(Double.toString(minutes)) + String.format("     ");
            minutes = 0;
        }

        subjectCursor.close();
        historyCursor.close();

        return totalTimes;


    }


    public String calculatePercentages() {
        String percentages = "";
        return percentages;
    }
}