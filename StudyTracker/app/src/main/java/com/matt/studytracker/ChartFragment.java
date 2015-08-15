package com.matt.studytracker;

import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.PercentFormatter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by macbookpro on 4/24/15.
 */
public class ChartFragment extends Fragment {
    protected TextView textView;
    private PieChart pieChart;

    private String[] xData;
    private float[] yData;

    private  String[] timeIntervalArray = {"Day", "Week", "Month", "Six Months", "All Time"};
    private List<String> timeIntervalList;
    private ArrayAdapter<String> pieChartSpinnerAdapter;

    private String selectedInterval = "Week";

    public static final String SELECTED_INTERVAL = "selectedInterval";

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.chart_fragment, container, false);
        //textView = (TextView) rootView.findViewById(R.id.minutes_info);
        //textView.setText(getMinutesString());

        if(savedInstanceState != null){
            selectedInterval = savedInstanceState.getString(SELECTED_INTERVAL);
        }

        timeIntervalList = new ArrayList<String>(Arrays.asList(timeIntervalArray));
        pieChartSpinnerAdapter =
                new ArrayAdapter<String>(
                        this.getActivity(),
                        R.layout.subject_chooser,
                        R.id.subject_chooser_textview,
                        timeIntervalList
                );

        Spinner pieChartSpinner = (Spinner) rootView.findViewById(R.id.percent_time_frame_spinner);
        pieChartSpinner.setAdapter(pieChartSpinnerAdapter);
        pieChartSpinner.setSelection(1);

        //for when a different time interval to analyse is selected
        pieChartSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selectedInterval = pieChartSpinnerAdapter.getItem(i);
                updatePieChart();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        pieChart = (PieChart) rootView.findViewById(R.id.pieChart);

        updatePieChart();

        return rootView;
    }

    public void updatePieChart(){
        String howFarBack = selectedInterval;
        pieChart.setUsePercentValues(true);

        pieChart.setDrawHoleEnabled(false);
        pieChart.setDescription("");

        xData = getSubjectsArray();
        yData = getPercentages(getTotalMinutesArray(howFarBack), xData.length);

        pieChart.setDrawHoleEnabled(true);
        pieChart.setHoleColorTransparent(true);
        pieChart.setHoleRadius(7);
        pieChart.setTransparentCircleRadius(10);

        pieChart.setVisibility(pieChart.VISIBLE);

        pieChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {

            @Override
            public void onValueSelected(Entry e, int dataSetIndex, Highlight h) {
                // display msg when value selected
                if (e == null)
                    return;
            }

            @Override
            public void onNothingSelected() {

            }
        });

        //pieChart.setRotation(0);
        pieChart.setRotationEnabled(false);

        addData();

        Legend l = pieChart.getLegend();
        l.setPosition(Legend.LegendPosition.LEFT_OF_CHART);
        l.setXEntrySpace(7);
        l.setYEntrySpace(5);
        l.setTextSize(12);
        l.setFormSize(15);
        l.setEnabled(false);
    }

    private void addData(){

        //int[]

        ArrayList<Entry> yValsList = new ArrayList<Entry>();
        for (int i = 0; i < yData.length; ++i){
            yValsList.add(new Entry(yData[i], i));
        }

        ArrayList<String> xValsList = new ArrayList<String>();
        for(int i = 0; i < xData.length; ++i){
            xValsList.add(xData[i]);
        }

        PieDataSet dataSet = new PieDataSet(yValsList, "subjects");
        dataSet.setSliceSpace(3);
        dataSet.setSelectionShift(5);

        ArrayList<Integer> colors = new ArrayList<Integer>();

        for(int c : ColorTemplate.COLORFUL_COLORS){
            colors.add(c);
        }

        for(int c : ColorTemplate.VORDIPLOM_COLORS){
            colors.add(c);
        }

        dataSet.setColors(colors);

        PieData data = new PieData(xValsList, dataSet);
        data.setValueFormatter(new PercentFormatter());
        data.setValueTextSize(11f);
        data.setValueTextColor(Color.BLACK);

        pieChart.setData(data);

        pieChart.highlightValues(null);

        pieChart.invalidate();
        pieChart.notifyDataSetChanged();
    }

    public int[] getTotalMinutesArray(String howFarBack) {
        Cursor historyCursor = ((MainActivity) getActivity()).myDB.getAllHistoryRows();
        Cursor subjectCursor = ((MainActivity) getActivity()).myDB.getAllSubjectRows();
        int subjectSize = subjectCursor.getCount();

        int historySize = historyCursor.getCount();
        String[] subjectArray = getSubjectsArray();

        DBTimeHelper helper = new DBTimeHelper();
        long formattedDate = helper.getFormattedPastTime(howFarBack);

        //Matches the times in the history database with the corresponding subjects in the y-axis
        int minutes = 0;
        int[] totalMinutes = new int [subjectSize];
        int timeLocations = 1;
        for(int i = 0; i < (subjectSize); ++i) {
            if (historyCursor.moveToFirst()) {
                do {
                    if(historyCursor.getString(1).replaceAll("\\s+", "").equals(subjectArray[i].replaceAll("\\s+", ""))){
                        String timeElapsed = historyCursor.getString(DBAdapter.H_TIME_ELAPSED_COLUMN);
                        ++timeLocations;

                        minutes += (Character.getNumericValue(timeElapsed.charAt(0)) * 60);
                        minutes += (Character.getNumericValue(timeElapsed.charAt(2)) * 10);
                        minutes += (Character.getNumericValue(timeElapsed.charAt(3)));
                    }
                } while (historyCursor.moveToNext() && historyCursor.getLong(DBAdapter.H_DATE_COLUMN) >= formattedDate);
                totalMinutes[i] = minutes;
                minutes = 0;
            }
        }
        return totalMinutes;
    }


    private String[] getSubjectsArray(){
        Cursor subjectCursor = ((MainActivity) getActivity()).myDB.getAllSubjectRows();

        int subjectSize = subjectCursor.getCount();
        String[] subjectArray = new String[subjectSize];

        //Shoves all of the subjects in the database into the string array on the x-axis
        int subjectLocation = 0;
        if (subjectCursor.moveToFirst()) {
            do {

                String subject = subjectCursor.getString(1);
                subjectArray[subjectLocation] = subject;
                ++subjectLocation;

            } while (subjectCursor.moveToNext());
        }

        return subjectArray;
    }

    private float[] getPercentages( int[] totalMinutesArray, int amountOfSubjects) {
        float[] percentages = new float[amountOfSubjects];

        float totalMinutes = 0;
        for (int i = 0; i < amountOfSubjects; ++i){
            totalMinutes += totalMinutesArray[i];
        }

        float percentage;
        for(int i = 0; i < amountOfSubjects; ++i){
            percentage = totalMinutesArray[i]/totalMinutes;
            percentages[i] = percentage;
        }

        return percentages;
    }

    private String getMinutesString(){
        String[] subjects = getSubjectsArray();
        float[] minutes = {0}; //getPercentages(getTotalMinutesArray(), subjects.length);

        String info = "";

        for (int i = 0; i < subjects.length; ++i){
            info += subjects[i] + "   " + String.format("%.1f",minutes[i]*100) + "\n";
        }

        return info;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString(SELECTED_INTERVAL, selectedInterval);
        super.onSaveInstanceState(outState);
    }
}