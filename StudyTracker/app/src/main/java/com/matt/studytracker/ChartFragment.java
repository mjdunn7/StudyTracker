package com.matt.studytracker;

import android.content.Context;
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
import android.widget.Toast;

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
    private  String[] averagesTimeIntervalArray = {"Day", "Week", "Month", "Six Months" };
    private List<String> timeIntervalList;
    private List<String> averageTimeIntervalList;
    private ArrayAdapter<String> pieChartSpinnerAdapter;
    private ArrayAdapter<String> averagesSpinnerAdapter;

    private String selectedInterval = "Week";
    private String selectedAveragesInterval  = "Week";

    public static final String SELECTED_INTERVAL = "selectedInterval";
    public static final String SELECTED_AVERAGES_INTERVALS = "selectedAveragesInterval";

    private ArrayList<String> xValsList;
    private ArrayList<Entry> yValsList;

    private static Toast toast;

    private TextView hoursBreakdown;
    private TextView subjectBreakdown;

    private TextView averages;
    private TextView averagesSubjects;

    private int[] pieMinutes;
    private int[] averagesMinutes;

    private boolean setSelectionJustCalled = false;


    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.chart_fragment, container, false);
        pieChart = (PieChart) rootView.findViewById(R.id.pieChart);

        if(savedInstanceState != null){
            selectedInterval = savedInstanceState.getString(SELECTED_INTERVAL);
            selectedAveragesInterval = savedInstanceState.getString(SELECTED_AVERAGES_INTERVALS);
        }

        timeIntervalList = new ArrayList<String>(Arrays.asList(timeIntervalArray));
        pieChartSpinnerAdapter =
                new ArrayAdapter<String>(
                        this.getActivity(),
                        R.layout.subject_chooser,
                        R.id.subject_chooser_textview,
                        timeIntervalList
                );

        //initializes hours breakdown textview
        hoursBreakdown = (TextView) rootView.findViewById(R.id.hours_breakdown_hours);
        subjectBreakdown = (TextView) rootView.findViewById(R.id.hours_breakdown_subjects);

        //initializes averages textviews
        averages = (TextView) rootView.findViewById(R.id.averages_hours);
        averagesSubjects = (TextView) rootView.findViewById(R.id.averages_subjects);


        Spinner pieChartSpinner = (Spinner) rootView.findViewById(R.id.percent_time_frame_spinner);
        pieChartSpinner.setAdapter(pieChartSpinnerAdapter);
        setSelectionJustCalled = true;
        pieChartSpinner.setSelection(1);


        //for when a different time interval to analyse is selected
        pieChartSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selectedInterval = pieChartSpinnerAdapter.getItem(i);

                if(!setSelectionJustCalled) {
                    pieMinutes = getTotalMinutesArray(selectedInterval);
                    updatePieChart();
                }
                setSelectionJustCalled = false;

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        final Spinner averagesTimeIntervalSpinner = (Spinner) rootView.findViewById(R.id.average_time_frame_spinner);
        averageTimeIntervalList = new ArrayList<String>(Arrays.asList(averagesTimeIntervalArray));
        averagesSpinnerAdapter =
                new ArrayAdapter<String>(
                        this.getActivity(),
                        R.layout.subject_chooser,
                        R.id.subject_chooser_textview,
                        averageTimeIntervalList
                );

        averagesTimeIntervalSpinner.setAdapter(averagesSpinnerAdapter);
        setSelectionJustCalled = true;
        averagesTimeIntervalSpinner.setSelection(1);


        //for when a different time interval to determine average times is selected
        averagesTimeIntervalSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selectedAveragesInterval = averagesTimeIntervalSpinner.getSelectedItem().toString();

                if(!setSelectionJustCalled) {
                    averagesMinutes = getTotalMinutesArray(selectedAveragesInterval);
                    updateAverages();
                }
                setSelectionJustCalled = false;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        updatePieChart();

        return rootView;
    }

    public void updateHoursBreakdown(){
        hoursBreakdown.setText(getHoursBreakdownString());
        subjectBreakdown.setText(getSubjectsBreakdownString());
    }

    public void updateAverages(){
        averages.setText(getAveragesString(selectedAveragesInterval));
        averagesSubjects.setText(getSubjectsBreakdownString());
    }

    public void updatePieChart(){
        Runnable runnable = new RenderPieChart();
        runnable.run();
    }

    public void updatePieChartThread(){
        pieMinutes = getTotalMinutesArray(selectedInterval);

        String howFarBack = selectedInterval;
        pieChart.setUsePercentValues(true);

        pieChart.setDrawHoleEnabled(false);
        pieChart.setDescription("");

        xData = getSubjectsArray();
        yData = getPercentages(pieMinutes, xData.length);

        pieChart.setDrawHoleEnabled(true);
        pieChart.setHoleColorTransparent(true);
        pieChart.setHoleRadius(7);
        pieChart.setTransparentCircleRadius(10);

        pieChart.setVisibility(pieChart.VISIBLE);

        pieChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {

            @Override
            public void onValueSelected(Entry e, int dataSetIndex, Highlight h) {
                // display msg when value selected
                String data = xValsList.get(e.getXIndex()) + " " + String.format("%.1f", e.getVal() * 100) + " %";

                Context context = ((MainActivity) getActivity());
                int duration = Toast.LENGTH_SHORT;

                if (toast != null)
                    toast.cancel();

                toast = Toast.makeText(context, data, duration);
                toast.show();
            }

            @Override
            public void onNothingSelected() {

            }
        });

        //pieChart.setRotation(0);
        pieChart.setRotationEnabled(false);

        addData();

        Legend l = pieChart.getLegend();
        l.setEnabled(false);

        updateHoursBreakdown();
    }

    private void addData(){
        xValsList = new ArrayList<String>();
        for(int i = 0; i < xData.length; ++i){
            xValsList.add(xData[i]);
        }

        yValsList = new ArrayList<Entry>();
        int entryIndex = 0;
        for (int i = 0; i < yData.length; ++i){
            if(yData[i] != 0) {
                yValsList.add(new Entry(yData[i], entryIndex));
                entryIndex++;
            }
            else
                xValsList.remove(entryIndex);
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
                    if(historyCursor.getString(1).replaceAll("\\s+", "").equals(subjectArray[i].replaceAll("\\s+", ""))
                            && historyCursor.getLong(DBAdapter.H_DATE_COLUMN) >= formattedDate){
                        String timeElapsed = historyCursor.getString(DBAdapter.H_TIME_ELAPSED_COLUMN);
                        ++timeLocations;

                        //TODO enable more than 10 hours
                        int position = 0;
                        String hours = "";
                        while(!Character.toString(timeElapsed.charAt(position)).equals(":")) {
                            hours += Character.toString(timeElapsed.charAt(position));
                            position++;
                        }
                        minutes += (Integer.parseInt(hours) * 60);
                        position++;
                        minutes += (Character.getNumericValue(timeElapsed.charAt(position)) * 10);
                        position++;
                        minutes += (Character.getNumericValue(timeElapsed.charAt(position)));
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

    private String getSubjectsBreakdownString(){
        String[] subjects = getSubjectsArray();

        String subject = "";

        for(int i = 0; i < subjects.length; i++){
            subject += subjects[i] + ":" + "\n";
        }

        subject += "Total:";

        return subject;
    }

    private String getHoursBreakdownString() {
        String[] subjects = getSubjectsArray();
        int[] intMinutes = pieMinutes;

        float[] minutes = new float[intMinutes.length];

        for (int i = 0; i < minutes.length; i++) {
            minutes[i] = (float) intMinutes[i];
        }

        String info = "";
        float total = 0;
        for (int i = 0; i < subjects.length; ++i) {
            total += minutes[i] / 60;
            info += String.format("%.1f", minutes[i] / 60) + " hours" + "\n";
        }

        info += String.format("%.1f", total) + " hours";

        return info;
    }

    private int getAmountOfDays(){

        int difference = 1;
        Cursor historyCursor = ((MainActivity) getActivity()).myDB.getAllHistoryRows();

        if(historyCursor.moveToFirst()){
            DBTimeHelper helper = new DBTimeHelper();
            String firstEntry = helper.getCurrentDBdate();

            historyCursor.moveToLast();
            String lastEntry = Long.toString(historyCursor.getLong(DBAdapter.H_DATE_COLUMN));

            int firstYear = Integer.parseInt(firstEntry.substring(0, 4));
            int firstMonth = Integer.parseInt(firstEntry.substring(4, 6));
            int firstDay = Integer.parseInt(firstEntry.substring(6, 8));

            int lastYear = Integer.parseInt(lastEntry.substring(0, 4));
            int lastMonth = Integer.parseInt(lastEntry.substring(4, 6));
            int lastDay = Integer.parseInt(lastEntry.substring(6, 8));

            difference = (firstYear - lastYear) * 365;
            difference += (firstMonth - lastMonth) * 31;
            difference += (firstDay - lastDay);
        }

        return difference;
    }

    private String getAveragesString(String when){
        double denominator = 1;
        if(when.equals("Day"))
            denominator = 1;
        if(when.equals("Week"))
            denominator = 7;
        if(when.equals("Month"))
            denominator = 31;
        if(when.equals("Six Months"))
            denominator = 182;

        double amountOfDays =  (double) getAmountOfDays();
        double timeSpan = amountOfDays/denominator;

        if(timeSpan < 1)
            timeSpan = 1;

        int[] totalMinutes = getTotalMinutesArray("All Time");
        String[] subjects = getSubjectsArray();

        String averages = "";
        double total = 0;

        for(int i = 0; i < subjects.length; ++i){
            double doubleTotalHours = (double) totalMinutes[i]/60;
            total += doubleTotalHours/timeSpan;
            averages += String.format("%.1f", doubleTotalHours/timeSpan) + " hours" + "\n";
        }

        averages += String.format("%.1f", total) + " hours" + "\n";

        return averages;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString(SELECTED_INTERVAL, selectedInterval);
        outState.putString(SELECTED_AVERAGES_INTERVALS, selectedAveragesInterval);
        super.onSaveInstanceState(outState);
    }

    public class RenderPieChart implements Runnable {


        @Override
        public void run() {
            // Moves the current Thread into the background
            android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND);

            updatePieChartThread();
        }
    }

    public void notifyDataSetChanged(){
        pieMinutes = getTotalMinutesArray(selectedInterval);
        updatePieChart();
    }

}