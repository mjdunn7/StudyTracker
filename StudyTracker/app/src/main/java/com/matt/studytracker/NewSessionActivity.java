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
    public static final int ACTIVITY_ID = 2;

    public static final String SUBJECT = "subject chosen for added session";
    public static final String IS_EDIT = "activity was launched in order to edit a session";
    public static final String START_DATE = "start date chosen for added session";
    public static final String END_DATE = "end date chosen for added session";
    public static final String DB_ID = "db id of edited session";

    public static final String DATA_BASE_DATE = "date formatted for database";

    public static final String ELAPSED_TIME = "elapsed time";
    public static final String INTERVAL = "time interval";

    public static final String START_YEAR = "start_year";
    public static final String START_MONTH = "start_month";
    public static final String START_DAY = "start_day";
    public static final String START_HOUR = "start_hour";
    public static final String START_MINUTE = "start_minute";

    public static final String END_YEAR = "end_year";
    public static final String END_MONTH = "end_month";
    public static final String END_DAY = "end_day";
    public static final String END_HOUR = "end_hour";
    public static final String END_MINUTE = "end_minute";

    public static final String[] MONTHS = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
    public static final String[] DAYS = {"", "Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};

    private String elapsedTime = "0:00";

    private String[] subjectArray = {};

    private String startDate;
    private String endDate;
    private String dateDBform;
    private String  subject = "not given";

    private int startYear;
    private int startMonth;
    private int startDay;
    private int startHour;
    private int startMinute;
    private int endYear;
    private int endMonth;
    private int endDay;
    private int endHour;
    private int endMinute;

    private  int dataBaseID;

    private boolean isEdit = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //preserves date if screen is flipped
        if(savedInstanceState != null){
            subject = savedInstanceState.getString(SUBJECT);
            endDate = savedInstanceState.getString(END_DATE);
            startDate = savedInstanceState.getString(START_DATE);
            dateDBform = savedInstanceState.getString(DATA_BASE_DATE);

            startYear = savedInstanceState.getInt(START_YEAR);
            startMonth = savedInstanceState.getInt(START_MONTH);
            startDay = savedInstanceState.getInt(START_DAY);
            startHour = savedInstanceState.getInt(START_HOUR);
            startMinute = savedInstanceState.getInt(START_MINUTE);

            endYear = savedInstanceState.getInt(END_YEAR);
            endMonth = savedInstanceState.getInt(END_MONTH);
            endDay = savedInstanceState.getInt(END_DAY);
            endHour = savedInstanceState.getInt(END_HOUR);
            endMinute = savedInstanceState.getInt(END_MINUTE);

            elapsedTime = savedInstanceState.getString(ELAPSED_TIME);

            isEdit = savedInstanceState.getBoolean(IS_EDIT);

            dataBaseID = savedInstanceState.getInt(DB_ID);

        }
        //gets subject array passed from main activity, contains subject list
        Intent intent = getIntent();
        subjectArray = intent.getStringArrayExtra(MainActivity.SUBJECT_ARRAY);

        //if the activity was launched to edit a session, populate the values with the initial session data
        if(intent.getBooleanExtra(IS_EDIT, false) && savedInstanceState == null){
            isEdit = true;

            subject = intent.getExtras().getString(SUBJECT);

            startYear = intent.getExtras().getInt(START_YEAR);
            startMonth = intent.getExtras().getInt(START_MONTH);
            startDay = intent.getExtras().getInt(START_DAY);
            startHour = intent.getExtras().getInt(START_HOUR);
            startMinute = intent.getExtras().getInt(START_MINUTE);

            endYear = intent.getExtras().getInt(END_YEAR);
            endMonth = intent.getExtras().getInt(END_MONTH);
            endDay = intent.getExtras().getInt(END_DAY);
            endHour = intent.getExtras().getInt(END_HOUR);
            endMinute = intent.getExtras().getInt(END_MINUTE);

            elapsedTime = intent.getExtras().getString(ELAPSED_TIME);

            dateDBform = getGivenDBDate(endYear, endMonth, endDay, endHour, endMinute);

            dataBaseID = intent.getIntExtra(DB_ID, 0);

            startDate = getHumanDate(startYear, startMonth, startDay);
            endDate = getHumanDate(endYear, endMonth, endDay);

        } else{
            //otherwise, sets default start and end dates to current date
            Calendar calendar = Calendar.getInstance();
            int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
            startDate = DAYS[dayOfWeek];
            startDate += ", " + DateFormat.getDateInstance().format(new Date());
            endDate = startDate;

            //set default start and end hours and minutes to current hour and minute
            startYear = calendar.get(Calendar.YEAR);
            startMonth = calendar.get(Calendar.MONTH);
            startDay = calendar.get(Calendar.DAY_OF_MONTH);
            startHour = calendar.get(Calendar.HOUR_OF_DAY);
            startMinute = calendar.get(Calendar.MINUTE);

            endYear = startYear;
            endMonth = startMonth;
            endDay = startDay;
            endHour = startHour;
            endMinute = startMinute;

            dateDBform = getCurrentDBDate();
        }

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


    public void updateElapsedTime(){
        Calendar startCalendar = Calendar.getInstance();
        Calendar endCalendar = Calendar.getInstance();
        startCalendar.set(startYear, startMonth, startDay, startHour, startMinute);
        endCalendar.set(endYear, endMonth, endDay, endHour, endMinute);

        long startTimeMillis = startCalendar.getTimeInMillis();
        long endTimeMillis = endCalendar.getTimeInMillis();

        long timeDiff = Math.abs(endTimeMillis - startTimeMillis);

        int hours = (int) timeDiff/3600000;
        int minutes = (int) (timeDiff - (hours * 3600000)) / 60000;

        elapsedTime = Integer.toString(hours) + ":";
        if(minutes < 10){
            elapsedTime += "0" + Integer.toString(minutes);
        } else {
            elapsedTime += Integer.toString(minutes);
        }

        TextView textView = (TextView) findViewById(R.id.start_end_diff);
        textView.setText(elapsedTime);
    }

    //for when the user taps the button to change the start time
    public void  changeStartTimeClicked(View v){
        TimePickerFragment timePickerFragment = new TimePickerFragment();

        Bundle bundle = new Bundle();
        bundle.putString(TimePickerFragment.START_OR_END, "start");
        bundle.putInt(TimePickerFragment.HOUR, startHour);
        bundle.putInt(TimePickerFragment.MINUTE, startMinute);
        timePickerFragment.setArguments(bundle);

        timePickerFragment.show(getSupportFragmentManager(), TimePickerFragment.TAG);
    }


    //for when the user taps the button to change the end time
    public void changeEndTimeClicked(View v){
        TimePickerFragment timePickerFragment = new TimePickerFragment();

        Bundle bundle = new Bundle();
        bundle.putString(TimePickerFragment.START_OR_END, "end");
        bundle.putInt(TimePickerFragment.HOUR, endHour);
        bundle.putInt(TimePickerFragment.MINUTE, endMinute);
        timePickerFragment.setArguments(bundle);

        timePickerFragment.show(getSupportFragmentManager(), TimePickerFragment.TAG);
    }

    public void changeStartDateClicked(View v){
        DatePickerFragment datePickerFragment = new DatePickerFragment();

        Bundle bundle = new Bundle();
        bundle.putString(DatePickerFragment.START_OR_END, "start");
        bundle.putInt(DatePickerFragment.YEAR, startYear);
        bundle.putInt(DatePickerFragment.MONTH, startMonth);
        bundle.putInt(DatePickerFragment.DAY, startDay);
        datePickerFragment.setArguments(bundle);

        datePickerFragment.show(getSupportFragmentManager(), DatePickerFragment.TAG);
    }

    public void changeEndDateClicked(View v){
        DatePickerFragment datePickerFragment = new DatePickerFragment();

        Bundle bundle = new Bundle();
        bundle.putString(DatePickerFragment.START_OR_END, "end");
        bundle.putInt(DatePickerFragment.YEAR, endYear);
        bundle.putInt(DatePickerFragment.MONTH, endMonth);
        bundle.putInt(DatePickerFragment.DAY, endDay);
        datePickerFragment.setArguments(bundle);

        datePickerFragment.show(getSupportFragmentManager(), DatePickerFragment.TAG);
    }

    public void donePressed(View v){
        Spinner spinner = (Spinner) findViewById(R.id.subjects_spinner);
        String subject = spinner.getSelectedItem().toString();

        TextView textView = (TextView) findViewById(R.id.start_end_diff);
        String elapsedTime = (String) textView.getText();

        Intent intent = new Intent(NewSessionActivity.this, MainActivity.class)
                .putExtra(SUBJECT, subject)
                .putExtra(ELAPSED_TIME, elapsedTime)
                .putExtra(START_DATE, startDate)
                .putExtra(END_DATE, endDate)
                .putExtra(INTERVAL, getIntervalString())
                .putExtra(DATA_BASE_DATE, dateDBform)
                .putExtra(START_YEAR, startYear)
                .putExtra(START_MONTH, startMonth)
                .putExtra(START_DAY, startDay)
                .putExtra(START_HOUR, startHour)
                .putExtra(START_MINUTE, startMinute)
                .putExtra(END_YEAR, endYear)
                .putExtra(END_MONTH, endMonth)
                .putExtra(END_DAY, endDay)
                .putExtra(END_HOUR, endHour)
                .putExtra(END_MINUTE, endMinute)
                .putExtra(IS_EDIT, isEdit)
                .putExtra(DB_ID, dataBaseID);


        setResult(Activity.RESULT_OK, intent);
        finish();

    }

    private String getIntervalString(){
        TextView textView = (TextView) findViewById(R.id.start_time_textview);
        String start = (String) textView.getText();

        textView = (TextView) findViewById(R.id.end_time_textview);
        String end = (String) textView.getText();

        return start + " - " + end;
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

            dateDBform = getGivenDBDate(endYear, endMonth, endDay, endHour, endMinute);
        }
        updateElapsedTime();
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
                startTime += Integer.toString(minute);
            }
            startTime += " PM";
        }
        return startTime;
    }

    private String getHumanDate(int year, int month, int day){
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day);
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);

        String date;

        date = DAYS[dayOfWeek];
        date += ", " + NewSessionActivity.MONTHS[month];
        date += " " + Integer.toString(day) + ",";
        date += " " + Integer.toString(year);

        return date;
    }

    @Override
    public void onDateChosen(int year, int month, int day, String qualifier) {
        String date = getHumanDate(year, month, day);

        if(qualifier.equals("start")) {
            TextView startDateTextView = (TextView) findViewById(R.id.start_date);
            startDateTextView.setText(date);
            startDate = date;

            startYear = year;
            startMonth = month;
            startDay = day;
        }
        if(qualifier.equals("end")){
            TextView endDateTextView = (TextView) findViewById(R.id.end_date);
            endDateTextView.setText(date);
            endDate = date;
            dateDBform = getGivenDBDate(year, month, day, endHour, endMinute);

            endYear = year;
            endMonth = month;
            endDay = day;
        }
        updateElapsedTime();
    }

    private String getGivenDBDate(int year, int month, int day, int hour, int minute){
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

        if(hour < 10){
            givenDate += "0" + Integer.toString(hour);
        }else {
            givenDate += Integer.toString(hour);
        }

        if(minute < 10){
            givenDate += "0" + Integer.toString(minute);
        }else {
            givenDate += Integer.toString(minute);
        }

        if(Calendar.getInstance().get(Calendar.SECOND) < 10){
            givenDate += "0" + Integer.toString(Calendar.getInstance().get(Calendar.SECOND));
        }else {
            givenDate += Integer.toString(Calendar.getInstance().get(Calendar.SECOND));
        }

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

        if(Calendar.getInstance().get(Calendar.HOUR_OF_DAY) < 10){
            currentDate += "0" + Integer.toString(Calendar.getInstance().get(Calendar.HOUR_OF_DAY));
        }else {
            currentDate += Integer.toString(Calendar.getInstance().get(Calendar.HOUR_OF_DAY));
        }

        if(Calendar.getInstance().get(Calendar.MINUTE) < 10){
            currentDate += "0" + Integer.toString(Calendar.getInstance().get(Calendar.MINUTE));
        }else {
            currentDate += Integer.toString(Calendar.getInstance().get(Calendar.MINUTE));
        }

        if(Calendar.getInstance().get(Calendar.SECOND) < 10){
            currentDate += "0" + Integer.toString(Calendar.getInstance().get(Calendar.SECOND));
        }else {
            currentDate += Integer.toString(Calendar.getInstance().get(Calendar.SECOND));
        }

        return currentDate;

    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putString(END_DATE, endDate);
        savedInstanceState.putString(START_DATE, startDate);
        savedInstanceState.putString(DATA_BASE_DATE, dateDBform);

        savedInstanceState.putInt(START_YEAR, startYear);
        savedInstanceState.putInt(START_MONTH, startMonth);
        savedInstanceState.putInt(START_DAY, startDay);
        savedInstanceState.putInt(START_HOUR, startHour);
        savedInstanceState.putInt(START_MINUTE, startMinute);

        savedInstanceState.putInt(END_YEAR, endYear);
        savedInstanceState.putInt(END_MONTH, endMonth);
        savedInstanceState.putInt(END_DAY, endDay);
        savedInstanceState.putInt(END_HOUR, endHour);
        savedInstanceState.putInt(END_MINUTE, endMinute);

        savedInstanceState.putString(ELAPSED_TIME, elapsedTime);
        savedInstanceState.putString(SUBJECT, subject);

        savedInstanceState.putBoolean(IS_EDIT, isEdit);

        savedInstanceState.putInt(DB_ID, dataBaseID);
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

            String subject = ((NewSessionActivity)getActivity()).subject;
            spinner.setSelection(0);
            for(int i = 0; i < classList.size(); ++i){
                if(subject.equals(classList.get(i))){
                    spinner.setSelection(i);
                    i = classList.size();
                }
            }

            //TODO: determine whether or not activity was started from editing or adding

            //Sets dates and times to whatever is stored in the main activity
            TextView textView = (TextView) rootView.findViewById(R.id.start_date);
            textView.setText(((NewSessionActivity) getActivity()).startDate);

            textView = (TextView) rootView.findViewById(R.id.end_date);
            textView.setText(((NewSessionActivity) getActivity()).endDate);

            //sets start time textview
            textView = (TextView) rootView.findViewById(R.id.start_time_textview);
            int hour = ((NewSessionActivity)getActivity()).startHour;
            int minute = ((NewSessionActivity)getActivity()).startMinute;
            textView.setText(((NewSessionActivity) getActivity()).getReadableTime(hour, minute));

            //sets end time textview
            textView = (TextView) rootView.findViewById(R.id.end_time_textview);
            hour = ((NewSessionActivity) getActivity()).endHour;
            minute = ((NewSessionActivity) getActivity()).endMinute;
            textView.setText(((NewSessionActivity) getActivity()).getReadableTime(hour, minute));

            textView = (TextView) rootView.findViewById(R.id.start_end_diff);
            textView.setText(((NewSessionActivity) getActivity()).elapsedTime);

            return rootView;
        }
    }
}
