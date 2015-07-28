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
import android.widget.DatePicker;
import android.widget.NumberPicker;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by matthewdunn on 7/27/15.
 */
public class NewSessionActivity extends ActionBarActivity {
    private String[] subjectArray = {};

    public static final String MINUTES = "amount of minutes chosen for added session";
    public static final String HOURS = "amount of hours chosen for added session";
    public static final String SUBJECT = "subject chosen for added session";
    public static final String DAY = "day chosen for added session";
    public static final String MONTH = "month chosen for added session";
    public static final String YEAR = "year chosen for added session";

    public static final String[] MONTHS = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};

    public static final int ACTIVITY_ID = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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

        DatePicker datePicker = (DatePicker) findViewById(R.id.date_picker);
        int year = datePicker.getYear();
        int day = datePicker.getDayOfMonth();
        int month = datePicker.getMonth();

        Intent intent = new Intent(NewSessionActivity.this, MainActivity.class)
                .putExtra(SUBJECT, subject)
                .putExtra(HOURS, hours)
                .putExtra(MINUTES, minutes)
                .putExtra(DAY, day)
                .putExtra(YEAR, year)
                .putExtra(MONTH, month);

        setResult(Activity.RESULT_OK, intent);
        finish();

    }
    public static class NewSessionFragment extends Fragment{
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
            minutesPicker.setMinValue(1);


            return rootView;
        }
    }
}
