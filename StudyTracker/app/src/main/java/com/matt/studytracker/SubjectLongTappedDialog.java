package com.matt.studytracker;

/**
 * Created by matthewdunn on 7/22/15.
 */

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

/**
 * Created by matthewdunn on 7/20/15.
 */
public class SubjectLongTappedDialog extends DialogFragment {
    DialogListener mListener;
    public static final String SUBJECT_SELECTED = "long tapped subject selected";
    private String title;

    public interface DialogListener{
        public void onPositiveClick();
        public void onNegativeClick();
        public void onNeutralClick();
    }

    @Override
    public void setArguments(Bundle args) {
        super.setArguments(args);
        title = args.getString(SUBJECT_SELECTED);

    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        if(savedInstanceState != null){
            title = savedInstanceState.getString(SUBJECT_SELECTED);
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("Select Action:");
        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                mListener.onPositiveClick();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                mListener.onNegativeClick();
            }
        });
        builder.setNeutralButton("Edit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                mListener.onNeutralClick();
            }
        });
        builder.setTitle(title);

        return builder.create();
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try{
            mListener = (DialogListener) activity;
        }catch (ClassCastException e){
            throw new ClassCastException(activity.toString() + " must implement dialog listener");
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString(SUBJECT_SELECTED, title);

        super.onSaveInstanceState(outState);
    }
}
