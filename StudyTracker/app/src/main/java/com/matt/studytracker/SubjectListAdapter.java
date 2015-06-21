package com.matt.studytracker;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by matthewdunn on 6/5/15.
 */
public class SubjectListAdapter extends ArrayAdapter <String> {
    private List<String> subjects;

    public SubjectListAdapter(Context context, int resource) {
        super(context, resource);
    }

    public SubjectListAdapter(Context context, int resource, int resource2, List<String> items) {
        super(context, resource, resource2, items);
        subjects = items;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        View v = convertView;

        if (v == null) {
            LayoutInflater vi;
            vi = LayoutInflater.from(getContext());
            v = vi.inflate(R.layout.home_list_item_layout, null);
        }

        ImageView start = (ImageView) v.findViewById(R.id.home_list_imageview);
        start.setImageResource(R.drawable.start);

        TextView subject = (TextView) v.findViewById(R.id.home_list_textview);
        subject.setText(subjects.get(position));

        return v;
    }
}
