package com.MyInvite.Application;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.actionbarsherlock.app.SherlockListFragment;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ListView;
import android.widget.TextView;

public class ViewMyEvents extends SherlockListFragment implements OnClickListener {

	public ArrayList<MyEventCell> events = new ArrayList<MyEventCell>();
	private ViewMyEventsAdapter adapter;
	int ids[];
	String response;
	TextView message;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		View v = inflater.inflate(R.layout.viewmyevents, container, false);
		return v;
	}

	@Override
	public void onResume() {
		super.onResume();
		
		AsyncTask<String, Void, String> task = new AsyncTask<String, Void, String>() {
			@Override
			protected String doInBackground(String... arg0) {
				HashMap<String, String> args = new HashMap<String, String>();
				args.put("host", Main.userid+"");
				response = DatabaseAccess.getData(Main.serverName + "GetMyEventsVer2.php", args);
				System.out.println("ViewMyEvents response: " + response);
				return null;
			}

			@Override
			protected void onPostExecute(String result) {
				System.out.println("ViewMyEvents response in onPostExecute: " + response);
				//if(response != null){
				events.clear();
				try {
					JSONArray jArray = new JSONArray(response);
					int length = jArray.length();
					events = new ArrayList<MyEventCell>(length);
					//events = new String[length];
					ids = new int[length];
					for (int i = 0; i < length; i++){
		                JSONObject json_data = jArray.getJSONObject(i);
		                ids[i] = json_data.getInt("id");
		                
		                String fullDate = json_data.getString("date");
		                String m = fullDate.substring(5, 7);
						String d = fullDate.substring(8, 10);
						String y = fullDate.substring(0, 4);
	
						String strDateFormat = "EEEE";
					    SimpleDateFormat sdf = new SimpleDateFormat(strDateFormat);
					    String day = sdf.format(new Date(Integer.parseInt(y)-1900, Integer.parseInt(m)-1, Integer.parseInt(d))).substring(0,3);
		                
		                //get date
		                String date = d;
		                
						//get time
						String fullTime = json_data.getString("time");
						int hourOfDay = Integer.parseInt(fullTime.substring(0, 2));
						int minute = Integer.parseInt(fullTime.substring(3, 5));
						String hour, min;
			  			boolean pm = true;
			  			if(minute < 10){
			  				min = "0" + String.valueOf(minute);
			  			}else{
			  				min = String.valueOf(minute);
			  			}
			  			if(hourOfDay == 0){
			  				hour = "12";
			  				pm = false;
			  			}else if(hourOfDay == 12){
			  				hour = "12";
			  			}else if(hourOfDay > 12){
			  				hour = String.valueOf(hourOfDay - 12);
			  			}else{
			  				hour = String.valueOf(hourOfDay);	
			  				pm = false;
			  			}
			  			String time;
			  			if(pm){
			  				time = hour + ":" + min + " PM";
			  			}else{
			  				time = hour + ":" + min + " AM";
			  			}	
		                events.add(new MyEventCell(json_data.getString("title"), day,
								date, time,	json_data.getString("location")));
			        }
	
				} catch (JSONException e) {
					setListAdapter(null);
					e.printStackTrace();
				} catch (NullPointerException e) {
					setListAdapter(null);
					e.printStackTrace();
				} catch (Exception e) {
					setListAdapter(null);
					e.printStackTrace();
				}
				adapter = new ViewMyEventsAdapter(getActivity(), R.layout.myeventcell, events);
				adapter.notifyDataSetChanged();
				setListAdapter(adapter);
				message = (TextView) getView().findViewById(R.id.tvViewMyEvents);
				//System.out.println("my events size: " + events.size());
				if (events.size() == 0) {
					message.setVisibility(View.VISIBLE);
					message.setText("No Current Events");
				}else{
					message.setVisibility(View.GONE);
				}
				//}
			}
		};
		task.execute();
		
	}
	
	public void onClick(View arg0) {
		// TODO Auto-generated method stub

	}
	
	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		// TODO Auto-generated method stub
		super.onListItemClick(l, v, position, id);
		try {
			Class ourClass = Class.forName("com.MyInvite.Application.MyEvent");
			Intent ourIntent = new Intent(getActivity(), ourClass);
			ourIntent.putExtra("eventid", ids[position]);
			startActivity(ourIntent);
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}

