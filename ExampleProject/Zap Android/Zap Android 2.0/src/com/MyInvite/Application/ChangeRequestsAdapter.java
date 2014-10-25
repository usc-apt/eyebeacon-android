package com.MyInvite.Application;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class ChangeRequestsAdapter extends ArrayAdapter<Request> {

	private ArrayList<Request> requests;

    public ChangeRequestsAdapter(Context context, int textViewResourceId, ArrayList<Request> requests) {
            super(context, textViewResourceId, requests);
            this.requests = requests;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
            View v = convertView;
            if (v == null) {
                LayoutInflater vi = (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                v = vi.inflate(R.layout.requestcell, null);
            }
            Request r = requests.get(position);
            if (r != null) {
                    TextView requester = (TextView) v.findViewById(R.id.tvRequester);
                    TextView date = (TextView) v.findViewById(R.id.tvDate);
                    TextView time = (TextView) v.findViewById(R.id.tvTime);
                    TextView location = (TextView) v.findViewById(R.id.tvLocation);
                    if (requester != null) {
                    	requester.setText(r.getRequester());
                    }
                    if(date != null){
                        date.setText(r.getDate());
                    }
                    if(time != null){
                    	time.setText(r.getTime());
                    }
                    if(location != null){
                        location.setText("Place: " + r.getLocation());
                    }    
            }
            return v;
    }
}
