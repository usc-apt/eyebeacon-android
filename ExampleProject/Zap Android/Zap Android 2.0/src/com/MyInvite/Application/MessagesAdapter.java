package com.MyInvite.Application;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class MessagesAdapter extends ArrayAdapter<Message> {

	private ArrayList<Message> messages;

    public MessagesAdapter(Context context, int textViewResourceId, ArrayList<Message> messages) {
            super(context, textViewResourceId, messages);
            this.messages = messages;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
            View v = convertView;
            if (v == null) {
                LayoutInflater vi = (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                v = vi.inflate(R.layout.message, null);
            }
            Message m = messages.get(position);
            if (m != null) {
                    TextView name = (TextView) v.findViewById(R.id.tvName);
                    TextView message = (TextView) v.findViewById(R.id.tvMessage);
                    TextView time = (TextView) v.findViewById(R.id.tvTime);
                    if (name != null) {
                    	name.setText(m.getName());
                    }
                    if(message != null){
                        message.setText(m.getMessage());
                    }
                    if(time != null){
                    	time.setText(m.getTime());
                    }
            }
            return v;
    }
}