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
import android.widget.Button;
import android.widget.NumberPicker;
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
public class NewSessionActivity extends ActionBarActivity implements DatePickerFragment.OnDateSetListener{
    private String[] subjectArray = {};

    private String userSetDate;
    private String dateDBform;

    public static final String MINUTES = "amount of minutes chosen for added session";
    public static final String HOURS = "amount of hours chosen for added session";
    public static final String SUBJECT = "subject chosen for added session";
    public static final String DATE = "date chosen for added session";
    public static final String DATA_BASE_DATE = "date formatted for database";


    public static final String[] MONTHS = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};

    public static final int ACTIVITY_ID = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //preserves date if screen is flipped
        if(savedInstanceState != null){
            userSetDate = savedInstanceState.getString(DATE);
            dateDBform = savedInstanceState.getString(DATA_BASE_DATE);
        }else {
            userSetDate = DateFormat.getDateInstance().format(new Date());
            dateDBform = getCurrentDBDate();
        }

        //gets subject array passed from main activity
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

    public void donePressed(View v){
        Spinner spinner = (Spinner) findViewById(R.id.subjects_spinner);
        String subject = spinner.getSelectedItem().toString();

        NumberPicker hoursPicker = (NumberPicker) findViewById(R.id.hours_picker);
        int hours = hoursPicker.getValue();

        NumberPicker minutesPicker = (NumberPicker) findViewById(R.id.minutes_picker);
        int minutes = minutesPicker.getValue();

        Intent intent = new Intent(NewSessionActivity.this, MainActivity.class)
                .putExtra(SUBJECT, subject)
                .putExtra(HOURS, hours)
                .putExtra(MINUTES, minutes)
                .putExtra(DATE, userSetDate)
                .putExtra(DATA_BASE_DATE, dateDBform);

        setResult(Activity.RESULT_OK, intent);
        finish();

    }

    @Override
    public void onDateChosen(int year, int month, int day) {
        userSetDate = NewSessionActivity.MONTHS[month];
        userSetDate += " " + Integer.toString(day) + ",";
        userSetDate += " " + Integer.toString(year);

        dateDBform = getGivenDBDate(year, month, day);

        TextView newSessionDate = (TextView) findViewById(R.id.new_session_date);
        newSessionDate.setText(userSetDate);
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

        givenDate += "0200";

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

        currentDate += "0200";

        return currentDate;

    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState){
        savedInstanceState.putString(DATE, userSetDate);
        savedInstanceState.putString(DATA_BASE_DATE, dateDBform);
    }


    public static class NewSessionFragment extends Fragment {
        public ArrayAdapter<String> subjectSelectionAdaptor;
        public List<String> classList;
        private String[] subjectsArray = {};

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

            NumberPicker hoursPicker = (NumberPicker) rootView.findViewById(R.id.hours_picker);
            NumberPicker minutesPicker = (NumberPicker) rootView.findViewById(R.id.minutes_picker);

            hoursPicker.setMaxValue(9);
            hoursPicker.setMinValue(0);
            minutesPicker.setMaxValue(59);
            minutesPicker.setMinValue(0);

            //Sets date to current date
            TextView newSessionDate = (TextView) rootView.findViewById(R.id.new_session_date);
            newSessionDate.setText(((NewSessionActivity) getActivity()).userSetDate);

            Button dateChanger = (Button) rootView.findViewById(R.id.session_date_changer);
            dateChanger.setOnClickListener(new Button.OnClickListener() {

                @Override
                public void onClick(View view) {
                    DatePickerFragment datePickerFragment = new DatePickerFragment();
                    datePickerFragment.show(getFragmentManager(), "new session date picker");
                }
            });



            return rootView;
        }
    }
}
