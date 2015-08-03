package com.matt.studytracker;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by matthewdunn on 7/27/15.
 */
public class NewSessionActivity extends ActionBarActivity implements DatePickerFragment.OnDateSetListener, TimePickerFragment.OnTimeSetListener{
    private String[] subjectArray = {};

    private String startDate;
    private String endDate;
    private String dateDBform;

    public static final String SUBJECT = "subject chosen for added session";
    public static final String START_DATE = "start date chosen for added session";
    public static final String END_DATE = "end date chosen for added session";
    public static final String START_HOUR = "start hour";
    public static final String START_MINUTE = "start minute";
    public static final String END_HOUR = "end hour";
    public static final String END_MINUTE = "end minute";
    public static final String DATA_BASE_DATE = "date formatted for database";


    public static final String[] MONTHS = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
    public static final String[] DAYS = {"", "Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};

    public static final int ACTIVITY_ID = 2;

    private int startHour = 0;
    private int startMinute = 0;
    private int endHour = 0;
    private int endMinute = 0;
    private Calendar startCalendar;
    private Calendar endCalendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //preserves date if screen is flipped
        if(savedInstanceState != null){
            endDate = savedInstanceState.getString(END_DATE);
            dateDBform = savedInstanceState.getString(DATA_BASE_DATE);
        }else {
            //otherwise, sets default start and end dates to current date
            Calendar calendar = Calendar.getInstance();
            int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
            startDate = DAYS[dayOfWeek];
            startDate += ", " + DateFormat.getDateInstance().format(new Date());
            endDate = startDate;

            //set default start and end hours and minutes to current hour and minute
            startHour = calendar.get(Calendar.HOUR_OF_DAY);
            startMinute = calendar.get(Calendar.MINUTE);

            endHour = startHour;
            endMinute = startMinute;

            startCalendar = Calendar.getInstance();
            endCalendar = Calendar.getInstance();


            dateDBform = getCurrentDBDate();
        }

        //gets subject array passed from main activity, contains subject list
        Intent intent = getIntent();
        subjectArray = intent.getStringArrayExtra(MainActivity.SUBJECT_ARRAY);

        setContentView(R.layout.activity_new_session);
        getSupportFragmentManager().beginTransaction()
                .add(R.id.new_session_container, new NewSessionFragment())
                .commit();

        ActionBar actionbar = getSupportActionBar();

        actionbar.setDisplayShowTitleEnabled(false);
        actionbar.setDisplayShowHomeEnabled(false);
    }

    public String[] getSubjectArray(){
        return subjectArray;
    }


    //for when the user taps the button to change the start time
    public void  changeStartTimeClicked(View v){
        TimePickerFragment timePickerFragment = new TimePickerFragment();

        Bundle bundle = new Bundle();
        bundle.putString(TimePickerFragment.START_OR_END, "start");
        timePickerFragment.setArguments(bundle);

        timePickerFragment.show(getSupportFragmentManager(), TimePickerFragment.TAG);
    }


    //for when the user taps the button to change the end time
    public void changeEndTimeClicked(View v){
        TimePickerFragment timePickerFragment = new TimePickerFragment();

        Bundle bundle = new Bundle();
        bundle.putString(TimePickerFragment.START_OR_END, "end");
        timePickerFragment.setArguments(bundle);

        timePickerFragment.show(getSupportFragmentManager(), TimePickerFragment.TAG);
    }

    public void changeStartDateClicked(View v){
        DatePickerFragment datePickerFragment = new DatePickerFragment();

        Bundle bundle = new Bundle();
        bundle.putString(DatePickerFragment.START_OR_END, "start");
        datePickerFragment.setArguments(bundle);

        datePickerFragment.show(getSupportFragmentManager(), DatePickerFragment.TAG);
    }

    public void changeEndDateClicked(View v){
        DatePickerFragment datePickerFragment = new DatePickerFragment();

        Bundle bundle = new Bundle();
        bundle.putString(DatePickerFragment.START_OR_END, "end");
        datePickerFragment.setArguments(bundle);

        datePickerFragment.show(getSupportFragmentManager(), DatePickerFragment.TAG);
    }

    public void donePressed(View v){
        Spinner spinner = (Spinner) findViewById(R.id.subjects_spinner);
        String subject = spinner.getSelectedItem().toString();

        //NumberPicker hoursPicker = (NumberPicker) findViewById(R.id.hours_picker);
        int hours =  0; //hoursPicker.getValue();

        //NumberPicker minutesPicker = (NumberPicker) findViewById(R.id.minutes_picker);
        int minutes =  0; //minutesPicker.getValue();

        Intent intent = new Intent(NewSessionActivity.this, MainActivity.class)
                .putExtra(SUBJECT, subject)
                .putExtra(HOURS, hours)
                .putExtra(MINUTES, minutes)
                .putExtra(START_DATE, startDate)
                .putExtra(END_DATE, endDate)
                .putExtra(DATA_BASE_DATE, dateDBform);

        setResult(Activity.RESULT_OK, intent);
        finish();

    }

    @Override
    public void onTimeChosen(int hour, int minute, String startOrEnd) {
        if(startOrEnd.equals("start")){
            startHour = hour;
            startMinute = minute;

            String startTime = getReadableTime(hour, minute);
            TextView textView = (TextView) findViewById(R.id.start_time_textview);
            textView.setText(startTime);
        }
        if(startOrEnd.equals("end")){
            endHour = hour;
            endMinute = minute;

            String endTime = getReadableTime(hour, minute);
            TextView textView = (TextView) findViewById(R.id.end_time_textview);
            textView.setText(endTime);
        }
    }

    private String getReadableTime(int hour, int minute){
        String startTime;
        if(hour <= 12){
            startTime = Integer.toString(hour) + ":";
            if(minute < 10){
                startTime += "0" + Integer.toString(minute);
            }else {
                startTime += Integer.toString(minute);
            }
            if(hour == 12){
                startTime += " PM";
            }else {
                startTime += " AM";
            }
        }else{
            hour = hour - 12;
            startTime = Integer.toString(hour) + ":";
            if (minute < 10){
                startTime += "0" + Integer.toString(minute);
            }else {
                startTime = Integer.toString(minute);
            }
            startTime += " PM";
        }
        return startTime;
    }

    @Override
    public void onDateChosen(int year, int month, int day, String qualifier) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day);
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);

        String date;

        date = DAYS[dayOfWeek];
        date += ", " + NewSessionActivity.MONTHS[month];
        date += " " + Integer.toString(day) + ",";
        date += " " + Integer.toString(year);



        if(qualifier.equals("start")) {
            TextView startDateTextView = (TextView) findViewById(R.id.start_date);
            startDateTextView.setText(date);
            startDate = date;
        }
        if(qualifier.equals("end")){
            TextView endDateTextView = (TextView) findViewById(R.id.end_date);
            endDateTextView.setText(date);
            endDate = date;
            dateDBform = getGivenDBDate(year, month, day);
        }
    }

    private String getGivenDBDate(int year, int month, int day){
        String givenDate;

        givenDate = Integer.toString(year);

        if(month < 10){
            givenDate += "0" + Integer.toString(month);
        }else {
            givenDate += Integer.toString(month);
        }

        if(day < 10){
            givenDate += "0" + Integer.toString(day);
        }else {
            givenDate += Integer.toString(day);
        }


        //TODO: Change hour and minute figure to value given by user
        givenDate += "2357";

        givenDate += Calendar.getInstance().get(Calendar.SECOND);

        return givenDate;
    }

    private String getCurrentDBDate(){
        String currentDate;
        currentDate = Integer.toString(Calendar.getInstance().get(Calendar.YEAR));

        if(Calendar.getInstance().get(Calendar.MONTH) < 10){
            currentDate += "0" + Integer.toString(Calendar.getInstance().get(Calendar.MONTH));
        }else {
            currentDate += Integer.toString(Calendar.getInstance().get(Calendar.MONTH));
        }

        if(Calendar.getInstance().get(Calendar.DAY_OF_MONTH) < 10){
            currentDate += "0" + Integer.toString(Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
        }else {
            currentDate += Integer.toString(Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
        }

        //TODO: Change hour and minute figure to value given by user
        currentDate += "2357";

        currentDate += Calendar.getInstance().get(Calendar.SECOND);

        return currentDate;

    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState){
        savedInstanceState.putString(END_DATE, endDate);
        savedInstanceState.putString(START_DATE, startDate);
        savedInstanceState.putInt(START_HOUR, startHour);
        savedInstanceState.putInt(START_MINUTE, startMinute);
        savedInstanceState.putInt(END_HOUR, endHour);
        savedInstanceState.putInt(END_MINUTE, endMinute);
        savedInstanceState.putString(DATA_BASE_DATE, dateDBform);
    }


    public static class NewSessionFragment extends Fragment {
        public ArrayAdapter<String> subjectSelectionAdaptor;
        public List<String> classList;
        private String[] subjectsArray = {""};

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.add_session, container, false);

            subjectsArray = ((NewSessionActivity) getActivity()).getSubjectArray();

            classList = new ArrayList<String>(Arrays.asList(subjectsArray));
            subjectSelectionAdaptor = new ArrayAdapter<String>(getActivity(),
                    R.layout.subject_chooser,
                    R.id.subject_chooser_textview,
                    classList);

            Spinner spinner = (Spinner) rootView.findViewById(R.id.subjects_spinner);
            spinner.setAdapter(subjectSelectionAdaptor);


            //TODO: determine whether or not activity was started from editing or adding
           // NumberPicker hoursPicker = (NumberPicker) rootView.findViewById(R.id.hours_picker);
           // NumberPicker minutesPicker = (NumberPicker) rootView.findViewById(R.id.minutes_picker);

           // hoursPicker.setMaxValue(9);
           // hoursPicker.setMinValue(0);
           // minutesPicker.setMaxValue(59);
           // minutesPicker.setMinValue(0);

            //Sets date to current date
            TextView newSessionDate = (TextView) rootView.findViewById(R.id.new_session_date);
            newSessionDate.setText(((NewSessionActivity) getActivity()).userSetDate);





            return rootView;
        }
    }
}
