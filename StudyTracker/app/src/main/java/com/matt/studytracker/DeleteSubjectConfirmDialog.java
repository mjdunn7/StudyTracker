package com.matt.studytracker;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

/**
 * Created by matthewdunn on 7/22/15.
 */
public class DeleteSubjectConfirmDialog extends DialogFragment{
    DeleteSubjectDialogListener mListener;
    public interface DeleteSubjectDialogListener{
        public void onDeleteSubjectConfirmed();
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Warning");
        builder.setMessage("Are you sure? Deleting will remove all recorded sessions corresponding to the subject," +
                " and the subject will no longer be analyzed. ");
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                mListener.onDeleteSubjectConfirmed();
            }
        });

        return builder.create();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try{
            mListener = (DeleteSubjectDialogListener) activity;
        }catch (ClassCastException e){
            throw new ClassCastException(activity.toString() + " must implement dialog listener");
        }
    }
}
