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
import android.widget.EditText;


public class AddClass extends ActionBarActivity {
    public static final int ACTIVITY_ID = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_class);
//        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
//        }

        ActionBar actionbar = getSupportActionBar();

        actionbar.setDisplayShowTitleEnabled(false);
        actionbar.setDisplayShowHomeEnabled(false);
    }

    public void doneButtonPressed(View v)
    {
       // Sets string to whatever was entered in the box
        EditText text = (EditText)findViewById(R.id.className);
        String className = text.getText().toString();

        Intent intent = new Intent(AddClass.this, MainActivity.class).putExtra("class_name", className);
        setResult(Activity.RESULT_OK, intent);
        finish();
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
            return rootView;
        }
    }
}
