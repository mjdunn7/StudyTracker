package com.matt.studytracker;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by matthewdunn on 7/25/15.
 */
public class SubjectChooserDialog extends DialogFragment {
    public static final String SUBJECT_ARRAY = "subject array";

    private String[] subjectArray;

    public ArrayAdapter<String> subjectSelectionAdaptor;
    public List<String> classList;

    @Override
    public void setArguments(Bundle args) {
        super.setArguments(args);
        subjectArray = args.getStringArray(SUBJECT_ARRAY);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        if(savedInstanceState != null){
            subjectArray = savedInstanceState.getStringArray(SUBJECT_ARRAY);
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();

        final View v = inflater.inflate(R.layout.add_session, null);
        builder.setView(v);

        classList = new ArrayList<String>(Arrays.asList(subjectArray));
        subjectSelectionAdaptor = new ArrayAdapter<String>(getActivity(),
                R.layout.subject_chooser,
                R.id.subject_chooser_textview,
                classList);

        Spinner spinner = (Spinner) v.findViewById(R.id.subjects_spinner);
        spinner.setAdapter(subjectSelectionAdaptor);


                /*.setTitle("Choose Corresponding Subject")
                .setItems(classList, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // The 'which' argument contains the index position
                        // of the selected item
                    }
                });*/

        return builder.create();
    }

    private ArrayAdapter<String> getAdapter(){
        classList = new ArrayList<String>(Arrays.asList(subjectArray));
        return new ArrayAdapter<String>(getActivity(),
                R.layout.add_session,
                R.id.subject_chooser_textview,
                classList);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putStringArray(SUBJECT_ARRAY, subjectArray);

        super.onSaveInstanceState(outState);

    }
}
