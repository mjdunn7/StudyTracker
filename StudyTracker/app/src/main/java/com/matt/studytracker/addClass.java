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


public class AddClass extends ActionBarActivity {
    public static final int ACTIVITY_ID = 1;

    public static final String CLASS_NAME = "class name";
    public static final String IS_EDIT = "add class was called to edit subject";
    public static final String DIFFICULTY_RATING = "difficulty rating";
    public static final String CREDIT_HOURS = "credit hours";
    public static final String IS_CHECKED = "is checked";

    private boolean isChecked = false;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_add_class);

        //prevents keyboard from automatically popping up
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }

        ActionBar actionbar = getSupportActionBar();

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

        Intent intent = new Intent(AddClass.this, MainActivity.class).putExtra(CLASS_NAME, className);

        intent.putExtra(IS_CHECKED, isChecked);

        intent.putExtra(DIFFICULTY_RATING, difficultyPicker.getValue() );
        intent.putExtra(CREDIT_HOURS, creditHoursPicker.getValue());

        setResult(Activity.RESULT_OK, intent);
        finish();
    }

    public void onCheckboxClicked(View v){
        isChecked = true;
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

            NumberPicker difficultyPicker = (NumberPicker) rootView.findViewById(R.id.difficulty_rating_picker);
            difficultyPicker.setMinValue(1);
            difficultyPicker.setMaxValue(5);
            return rootView;
        }
    }
}
