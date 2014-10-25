package com.Zap.InstantConnection.Contacts;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CheckedTextView;
import android.widget.TextView;

import com.Zap.InstantConnection.R;

public class ContactsAdapter extends ArrayAdapter<Contact> {

	private ArrayList<Contact> contacts;
	private int[] toggle;
	private int resource;

    public ContactsAdapter(Context context, int textViewResourceId, ArrayList<Contact> contacts) {
    	super(context, textViewResourceId, contacts);
        this.contacts = contacts;
        this.resource = textViewResourceId;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        ViewHolder holder = null;
        
        if (v == null) {
            LayoutInflater vi = (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(R.layout.contactcell, null);
            holder = new ViewHolder();
            holder.checkBox = (CheckedTextView) v.findViewById(R.id.tvContactName);
            holder.number = (TextView) v.findViewById(R.id.tvContactNumber);
            v.setTag(holder);
        }else{
        	holder = (ViewHolder)v.getTag(); 
        }
        Contact c = contacts.get(position);
        if (c != null) {
            //set name field
            if(c.getName().charAt(0) == '*'){
            	holder.checkBox.setText(contacts.get(position).getName().substring(1));
            }else{
            	holder.checkBox.setText(contacts.get(position).getName());
            }
            //set number field
            if(c.getVirtual() == 0){
            	holder.number.setText("Zap Contact");
            }else{
            	holder.number.setText(c.getNumber());
            }
            //set checkbox field
            if(toggle[position] == 1) {
            	holder.checkBox.setChecked(true);
            } else {
            	holder.checkBox.setChecked(false);
            }
        }
        return v;
    }
    
    static class ViewHolder{
        CheckedTextView checkBox;
        TextView number;
    }
    
    public void setToggleList(int [] list) {
        this.toggle = list;
        notifyDataSetChanged();
    }
}

