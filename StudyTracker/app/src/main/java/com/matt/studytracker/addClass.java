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
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.TextView;


public class AddClass extends ActionBarActivity {
    public static final int ACTIVITY_ID = 1;

    public static final String CLASS_NAME = "class name";
    public static final String IS_EDIT = "add class was called to edit subject";
    public static final String DIFFICULTY_RATING = "difficulty rating";
    public static final String CREDIT_HOURS = "credit hours";
    public static final String IS_CHECKED = "is checked";
    public static final String DATA_BASE_ID = "data base id";

    private boolean isChecked = false;
    private boolean isEdit = false;

    private String subjectName = "";
    private int creditHours = 1;
    private int difficulty = 1;
    private int dataBaseID = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null) {

        }else{
            isEdit = savedInstanceState.getBoolean(IS_EDIT);
            isChecked = savedInstanceState.getBoolean(IS_CHECKED);

            subjectName = savedInstanceState.getString(CLASS_NAME);
            creditHours = savedInstanceState.getInt(CREDIT_HOURS);
            difficulty = savedInstanceState.getInt(DIFFICULTY_RATING);
            dataBaseID = savedInstanceState.getInt(DATA_BASE_ID);
        }

        Intent intent = getIntent();
        isEdit = intent.getExtras().getBoolean(IS_EDIT);

        if(isEdit){
            subjectName = intent.getExtras().getString(CLASS_NAME);
            creditHours = intent.getExtras().getInt(CREDIT_HOURS);
            difficulty = intent.getExtras().getInt(DIFFICULTY_RATING);

            dataBaseID = intent.getExtras().getInt(DATA_BASE_ID);
        }
        setContentView(R.layout.activity_add_class);

        //prevents keyboard from automatically popping up
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        ActionBar actionbar = getSupportActionBar();

        getSupportFragmentManager().beginTransaction()
                .add(R.id.container, new PlaceholderFragment())
                .commit();

        actionbar.setDisplayShowTitleEnabled(false);
        actionbar.setDisplayShowHomeEnabled(false);
    }

    public void doneButtonPressed(View v)
    {
       // Sets string to whatever was entered in the box
        EditText text = (EditText)findViewById(R.id.className);
        String className = text.getText().toString();

        NumberPicker difficultyPicker = (NumberPicker) findViewById(R.id.difficulty_rating_picker);
        NumberPicker creditHoursPicker = (NumberPicker) findViewById(R.id.credit_hours_picker);

        Intent intent = new Intent(AddClass.this, MainActivity.class);


        intent.putExtra(IS_EDIT, isEdit);
        intent.putExtra(IS_CHECKED, isChecked);

        intent.putExtra(CLASS_NAME, className);
        intent.putExtra(DIFFICULTY_RATING, difficultyPicker.getValue());
        intent.putExtra(CREDIT_HOURS, creditHoursPicker.getValue());
        intent.putExtra(DATA_BASE_ID, dataBaseID);

        setResult(Activity.RESULT_OK, intent);
        finish();
    }

    public void onCheckboxClicked(View v){
        isChecked = true;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putBoolean(IS_CHECKED, isChecked);
        outState.putBoolean(IS_EDIT, isEdit);

        outState.putString(CLASS_NAME, subjectName);
        outState.putInt(DIFFICULTY_RATING, difficulty);
        outState.putInt(CREDIT_HOURS, creditHours);
        outState.putInt(DATA_BASE_ID, dataBaseID);

        super.onSaveInstanceState(outState);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_add_class, container, false);

            NumberPicker creditHoursPicker = (NumberPicker) rootView.findViewById(R.id.credit_hours_picker);
            creditHoursPicker.setMinValue(1);
            creditHoursPicker.setMaxValue(6);
            creditHoursPicker.setValue(((AddClass) getActivity()).creditHours);

            NumberPicker difficultyPicker = (NumberPicker) rootView.findViewById(R.id.difficulty_rating_picker);
            difficultyPicker.setMinValue(1);
            difficultyPicker.setMaxValue(5);
            difficultyPicker.setValue(((AddClass) getActivity()).difficulty);

            EditText editText = (EditText) rootView.findViewById(R.id.className);
            editText.setText(((AddClass)getActivity()).subjectName, TextView.BufferType.EDITABLE);
            return rootView;
        }
    }
}
