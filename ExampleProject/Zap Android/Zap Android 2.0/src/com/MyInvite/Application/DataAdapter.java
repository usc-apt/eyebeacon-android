package com.MyInvite.Application;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckedTextView;

public class DataAdapter extends BaseAdapter {

    // store the context (as an inflated layout)
    private LayoutInflater inflater;
    // store the resource (typically list_item.xml)
    private int resource;
    // store (a reference to) the data
    private String[] data;
    private int[] toggle;
    
    /**
     * Default constructor. Creates the new Adapter object to
     * provide a ListView with data.
     * @param context
     * @param resource
     * @param data
     */
    public DataAdapter(Context context, int resource, String[] data) {
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.resource = resource;
        this.data = data;
    }
    
    public DataAdapter(Context context, int resource, ArrayList<String> arrayData){
    	this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.resource = resource;
        data = new String[arrayData.size()];
        for (int i = 0; i < arrayData.size(); i++) {
        	data[i] = arrayData.get(i);
        }
    }
    
    /**
     * Return the size of the data set.
     */
    public int getCount() {
    	return data.length;
    }
    
    /**
     * Return an object in the data set.
     */
    public Object getItem(int position) {
        return data[position];
    }
    
    /**
     * Return the position provided.
     */
    public long getItemId(int position) {
    	return position;
    }

    /**
     * Return a generated view for a position.
     */
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;

        if ( convertView == null ) {
            /* There is no view at this position, we create a new one. 
               In this case by inflating an xml layout */
            convertView = inflater.inflate(resource, null);
            holder = new ViewHolder();
            holder.checkBox = (CheckedTextView) convertView.findViewById(android.R.id.text1);
            convertView.setTag(holder);
        } else {
            /* We recycle a View that already exists */
            holder = (ViewHolder)convertView.getTag();           
        }

        // Once we have a reference to the View we are returning, we set its values.
        holder.checkBox.setText(data[position]);
        // Here is where you should set the ToggleButton value for this item!!!
        if(toggle[position] == 1) {
        	holder.checkBox.setChecked(true);
        } else {
        	holder.checkBox.setChecked(false);
        }

        return convertView;
    }
    
    static class ViewHolder{
        CheckedTextView checkBox;
    }
    
    public void setToggleList(int [] list) {
        this.toggle = list;
        notifyDataSetChanged();
    }
    
}