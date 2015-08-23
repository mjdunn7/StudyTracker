package com.matt.studytracker;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.NumberPicker;

/**
 * Created by matthewdunn on 8/21/15.
 */
public class CountdownTimeDialog extends DialogFragment {
    public static final String TAG = "CountdownTimeDialog";

    CountdownDialogListener mListener;
    public interface CountdownDialogListener{
        public void onCountdownStarted(int hours, int minutes, boolean vibrate);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Countdown Mode On");

        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View rootView = inflater.inflate(R.layout.countdown_dialog, null);

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(rootView);

        NumberPicker hoursPicker1 = (NumberPicker) rootView.findViewById(R.id.countdown_hours_picker);
        NumberPicker minutesPicker1 = (NumberPicker) rootView.findViewById(R.id.countdown_minutes_picker);

        hoursPicker1.setMaxValue(24);
        hoursPicker1.setMinValue(0);

        minutesPicker1.setMaxValue(59);
        minutesPicker1.setMinValue(0);


        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        builder.setPositiveButton("Start", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                NumberPicker hoursPicker = (NumberPicker) ((AlertDialog) dialogInterface).findViewById(R.id.countdown_hours_picker);
                NumberPicker minutesPicker = (NumberPicker) ((AlertDialog) dialogInterface).findViewById(R.id.countdown_minutes_picker);

                int hours = hoursPicker.getValue();
                int minutes = minutesPicker.getValue();

                CheckBox checkBox = (CheckBox) ((AlertDialog) dialogInterface).findViewById(R.id.countdown_checkbox);

                mListener.onCountdownStarted(hours, minutes, checkBox.isChecked());
            }
        });

        return builder.create();
    }



    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try{
            mListener = (CountdownDialogListener) activity;
        }catch (ClassCastException e){
            throw new ClassCastException(activity.toString() + " must implement CountdownTimeDialog listener");
        }
    }
}