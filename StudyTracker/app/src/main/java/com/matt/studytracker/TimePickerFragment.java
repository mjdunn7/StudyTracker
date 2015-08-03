package com.matt.studytracker;

import android.app.Activity;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.widget.TimePicker;

/**
 * Created by matthewdunn on 8/2/15.
 */
public class TimePickerFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener {
    OnTimeSetListener mListener;

    public static final String TAG = "TimePickerFragment";
    public static final String START_OR_END = "Start or end time";

    private String qualifier;

    public interface OnTimeSetListener{
        void onTimeChosen(int hour, int minute, String startOrEnd);
    }

    @Override
    public void setArguments(Bundle args) {
        super.setArguments(args);
        qualifier = args.getString(START_OR_END);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return super.onCreateDialog(savedInstanceState);
    }

    @Override
    public void onTimeSet(TimePicker timePicker, int hour, int minute) {
        mListener.onTimeChosen(hour, minute, qualifier);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try{
            mListener = (OnTimeSetListener) activity;
        }catch (ClassCastException e){
            throw new ClassCastException(activity.toString() + " must implement TimePickerDialog listener");
        }
    }
}
