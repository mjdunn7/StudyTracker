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
    public static final String CREDIT_HOURS = "long tapped subject credit hours";
    public static final String DIFFICULTY_RATING = "long tapped subject difficulty rating";
    public static final String DATA_BASE_ID = "long tapped subject db ID";

    private String title;
    private int creditHours;
    private int difficulty;
    private int dataBaseID;

    public interface DialogListener{
        public void onSubjectDeleteClick();
        public void onSubjectCancelClick();
        public void onSubjectEditClick(String subject, int creditHours, int difficultyRating, int dataBaseID);
    }

    @Override
    public void setArguments(Bundle args) {
        super.setArguments(args);
        title = args.getString(SUBJECT_SELECTED);
        creditHours = args.getInt(CREDIT_HOURS);
        difficulty = args.getInt(DIFFICULTY_RATING);
        dataBaseID = args.getInt(DATA_BASE_ID);

    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        if(savedInstanceState != null){
            title = savedInstanceState.getString(SUBJECT_SELECTED);
            creditHours = savedInstanceState.getInt(CREDIT_HOURS);
            difficulty = savedInstanceState.getInt(DIFFICULTY_RATING);
            dataBaseID = savedInstanceState.getInt(DATA_BASE_ID);
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setMessage("What would you like to do with this subject?");
        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                mListener.onSubjectDeleteClick();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                mListener.onSubjectCancelClick();
            }
        });
        builder.setNeutralButton("Edit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                mListener.onSubjectEditClick(title, creditHours, difficulty, dataBaseID);
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
            throw new ClassCastException(activity.toString() + " must implement subjectLongTapped dialog listener");
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString(SUBJECT_SELECTED, title);
        outState.putInt(CREDIT_HOURS, creditHours);
        outState.putInt(DIFFICULTY_RATING, difficulty);
        outState.putInt(DATA_BASE_ID, dataBaseID);

        super.onSaveInstanceState(outState);
    }
}
