package com.MyInvite.Application;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class ViewMyEventsAdapter extends ArrayAdapter<MyEventCell> {

	private ArrayList<MyEventCell> events;

    public ViewMyEventsAdapter(Context context, int textViewResourceId, ArrayList<MyEventCell> e) {
            super(context, textViewResourceId, e);
            this.events = e;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
            View v = convertView;
            if (v == null) {
                LayoutInflater vi = (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                v = vi.inflate(R.layout.myeventcell, null);
            }
            MyEventCell e = events.get(position);
            if (e != null) {
            	 TextView eventName = (TextView) v.findViewById(R.id.tvEventName);
                    TextView day = (TextView) v.findViewById(R.id.tvDayOfTheWeek);
                    TextView date = (TextView) v.findViewById(R.id.tvDate);
                    TextView time = (TextView) v.findViewById(R.id.tvTime);
                    TextView location = (TextView) v.findViewById(R.id.tvLocation);
                    if (eventName != null) {
                    	eventName.setText(e.getEventName());
                    }
                    if(day != null){
                        day.setText(e.getDay());
                    }
                    if(date != null){
                        date.setText(e.getDate());
                    }
                    if(time != null){
                    	time.setText("Time: " + e.getTime());
                    }
                    if(location != null){
                        location.setText("Location: " + e.getLocation());
                    }      
            }
            return v;
    }
}

