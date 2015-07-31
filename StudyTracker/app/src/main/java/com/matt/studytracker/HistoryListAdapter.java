package com.matt.studytracker;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by matthewdunn on 6/20/15.
 */
public class HistoryListAdapter extends ArrayAdapter<HistoryItem> {
    List<HistoryItem> history;
    public static final String TAG = "HistoryListAdapter";

    public HistoryListAdapter(Context context, int resource, int textViewResourceId, HistoryItem[] objects) {
        super(context, resource, textViewResourceId, objects);
    }

    public HistoryListAdapter(Context context, int resource, int resource2, List<HistoryItem> items) {
        super(context, resource, resource2, items);
        history = items;
    }

    @Override
    public void add(HistoryItem object) {
        super.add(object);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        //Log.d(TAG, "get view called with position " + Integer.toString(position));

        if (convertView == null) {
            LayoutInflater vi;
            vi = LayoutInflater.from(getContext());
            convertView = vi.inflate(R.layout.history_list_item_layout, null);

            TextView subject = (TextView) convertView.findViewById(R.id.history_list_textview);
            TextView date = (TextView) convertView.findViewById(R.id.history_list_date);
            TextView timeElapsed = (TextView) convertView.findViewById(R.id.history_list_time);

            subject.setText(history.get(position).getSubject());
            date.setText(history.get(position).getDate());
            timeElapsed.setText(history.get(position).getTimeElapsed());

            return convertView;
        } else {
            TextView subject = (TextView) convertView.findViewById(R.id.history_list_textview);
            TextView date = (TextView) convertView.findViewById(R.id.history_list_date);
            TextView timeElapsed = (TextView) convertView.findViewById(R.id.history_list_time);

            subject.setText(history.get(position).getSubject());
            date.setText(history.get(position).getDate());
            timeElapsed.setText(history.get(position).getTimeElapsed());

            return convertView;
        }


    }
}
