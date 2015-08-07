package com.matt.studytracker;

/**
 * Created by matthewdunn on 8/6/15.
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
public class HistoryLongTappedDialog extends DialogFragment {
    DialogListener mListener;
    public static final String HISTORY_SELECTED = "long tapped history selected";
    public static final String TAG = "HistoryLongTappedDialog";

    public interface DialogListener{
        public void onHistoryDeleteClick();
        public void onHistoryCancelClick();
        public void onHistoryEditClick();
    }

    @Override
    public void setArguments(Bundle args) {
        super.setArguments(args);

    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        if(savedInstanceState != null){

        }

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Session Selected");
        builder.setMessage("What would you like to do with this session?");
        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                mListener.onHistoryDeleteClick();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                mListener.onHistoryCancelClick();
            }
        });
        builder.setNeutralButton("Edit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                mListener.onHistoryEditClick();
            }
        });

        return builder.create();
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try{
            mListener = (DialogListener) activity;
        }catch (ClassCastException e){
            throw new ClassCastException(activity.toString() + " must implement historyLongTapped dialog listener");
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {

        super.onSaveInstanceState(outState);
    }
}

