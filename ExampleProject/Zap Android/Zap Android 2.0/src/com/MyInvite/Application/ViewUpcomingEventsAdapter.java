package com.MyInvite.Application;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ViewUpcomingEventsAdapter extends ArrayAdapter<UpcomingEventCell> {

	private ArrayList<UpcomingEventCell> events;

    public ViewUpcomingEventsAdapter(Context context, int textViewResourceId, ArrayList<UpcomingEventCell> e) {
        super(context, textViewResourceId, e);
        this.events = e;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        if (v == null) {
            LayoutInflater vi = (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(R.layout.upcomingeventcell, null);
        }
        UpcomingEventCell e = events.get(position);
        if (e != null) {
        	 TextView eventName = (TextView) v.findViewById(R.id.tvEventName);
                TextView day = (TextView) v.findViewById(R.id.tvDayOfTheWeek);
                TextView date = (TextView) v.findViewById(R.id.tvDate);
                TextView time = (TextView) v.findViewById(R.id.tvTime);
                TextView host = (TextView) v.findViewById(R.id.tvHost);
                ImageView message = (ImageView) v.findViewById(R.id.iUnreadMessage);
                ImageView event = (ImageView) v.findViewById(R.id.iUnreadEvent);
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
                if(host != null){
                	host.setText("Host: " + e.getHost());
                }
                String notificationType = e.getNotification();
                if(notificationType.equals("message")){
					event.setVisibility(View.GONE);
				}else if(notificationType.equals("event")){
					message.setVisibility(View.GONE);
				}else{
					event.setVisibility(View.GONE);
					message.setVisibility(View.GONE);
				}
        }
        return v;
    }
 
}

