package com.matt.studytracker;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.widget.DatePicker;

/**
 * Created by matthewdunn on 7/30/15.
 */
public class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {
    OnDateSetListener mListener;

    public static final String TAG = "DatePickerFragment";
    public static final String START_OR_END = "Start or end date";
    public static final String YEAR = "year to give picker";
    public static final String MONTH = "month to give picker";
    public static final String DAY = "day to give picker";

    private String startOrEnd;
    private int year;
    private int month;
    private int day;

    public interface OnDateSetListener{
        void onDateChosen(int year, int month, int day, String qualifier);
    }

    @Override
    public void setArguments(Bundle args) {
        super.setArguments(args);
        startOrEnd = args.getString(START_OR_END);
        year = args.getInt(YEAR);
        month = args.getInt(MONTH);
        day = args.getInt(DAY);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current date as the default date in the picker
        if(savedInstanceState != null) {
            startOrEnd = savedInstanceState.getString(START_OR_END);
        }

        // Create a new instance of DatePickerDialog and return it
        return new DatePickerDialog(getActivity(), this, year, month, day);
    }


    @Override
    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
        mListener.onDateChosen(i, i1, i2, startOrEnd);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try{
            mListener = (OnDateSetListener) activity;
        }catch (ClassCastException e){
            throw new ClassCastException(activity.toString() + " must implement DatePickerDialog listener");
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(START_OR_END, startOrEnd);
    }
}
