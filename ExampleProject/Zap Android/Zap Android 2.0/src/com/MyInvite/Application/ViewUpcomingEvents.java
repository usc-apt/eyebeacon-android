package com.MyInvite.Application;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.actionbarsherlock.app.SherlockListFragment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ListView;
import android.widget.TextView;

public class ViewUpcomingEvents extends SherlockListFragment implements	OnClickListener {

	public ArrayList<UpcomingEventCell> events = new ArrayList<UpcomingEventCell>();
	private ViewUpcomingEventsAdapter adapter;
	TextView message;
	int ids[], open[], notified[], hosts[];
	String response;
	ProgressDialog progDailog;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		View v = inflater.inflate(R.layout.viewupcomingevents, container, false);
		return v;
	}

	@Override
	public void onResume() {
		super.onResume();
		
		AsyncTask<String, Void, String> task = new AsyncTask<String, Void, String>() {
			@Override
	        protected void onPreExecute() {
	            super.onPreExecute();
	            progDailog = new ProgressDialog(getActivity());
	            progDailog.setMessage("Loading...");
	            progDailog.setIndeterminate(false);
	            progDailog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
	            progDailog.setCancelable(true);
	            progDailog.show();
	        }
			
			@Override
			protected String doInBackground(String... arg0) {
				HashMap<String, String> args = new HashMap<String, String>();
				args.put("userid", Main.userid + "");
				response = DatabaseAccess.getData(Main.serverName + "GetUpcomingEventsVer2.php", args);
				System.out.println("ViewUpcomingEvents response: " + response);
				return null;
			}

			@Override
			protected void onPostExecute(String result) {
				System.out.println("ViewUpcomingEvents response in onPostExecute: " + response);
				//if(response != null){
				events.clear();
				try {
					JSONArray jArray = new JSONArray(response);
					int length = jArray.length();
					events = new ArrayList<UpcomingEventCell>(length);
					ids = new int[length];
					open = new int[length];
					notified = new int[length];
					hosts = new int[length];
					for (int i = 0; i < length; i++) {
						JSONObject json_data = jArray.getJSONObject(i);
						ids[i] = json_data.getInt("id");
						open[i] = json_data.getInt("open");

						String fullDate = json_data.getString("date");
						String m = fullDate.substring(5, 7);
						String d = fullDate.substring(8, 10);
						String y = fullDate.substring(0, 4);

						String strDateFormat = "EEEE";
						SimpleDateFormat sdf = new SimpleDateFormat(strDateFormat);
						String day = sdf.format(new Date(Integer.parseInt(y) - 1900, Integer.parseInt(m) - 1, Integer.parseInt(d))).substring(0, 3);

						// get date
						String date = d;

						// get time
						String fullTime = json_data.getString("time");
						int hourOfDay = Integer.parseInt(fullTime.substring(0, 2));
						int minute = Integer.parseInt(fullTime.substring(3, 5));
						String hour, min;
						boolean pm = true;
						if (minute < 10) {
							min = "0" + String.valueOf(minute);
						} else {
							min = String.valueOf(minute);
						}
						if (hourOfDay == 0) {
							hour = "12";
							pm = false;
						} else if (hourOfDay == 12) {
							hour = "12";
						} else if (hourOfDay > 12) {
							hour = String.valueOf(hourOfDay - 12);
						} else {
							hour = String.valueOf(hourOfDay);
							pm = false;
						}
						String time;
						if (pm) {
							time = hour + ":" + min + " PM";
						} else {
							time = hour + ":" + min + " AM";
						}
						
						//set notification
						notified[i] = json_data.getInt("notified");
						String notificationIcon;
						if(notified[i] == 3){
							notificationIcon = "message";
						}else if(notified[i] == 1){
							notificationIcon = "none";
						}else{
							notificationIcon = "event";
						}
						
						hosts[i] = json_data.getInt("host");
						events.add(new UpcomingEventCell(json_data.getString("title"), day, date, time, json_data.getString("name"), notificationIcon));
					}
					
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					setListAdapter(null);
					e.printStackTrace();
				} catch (NullPointerException e) {
					setListAdapter(null);
					e.printStackTrace();
				}catch (Exception e) {
					setListAdapter(null);
					e.printStackTrace();
				}
				adapter = new ViewUpcomingEventsAdapter(getActivity(), R.layout.upcomingeventcell, events);
				adapter.notifyDataSetChanged();
				setListAdapter(adapter);
				message = (TextView) getView().findViewById(R.id.tvViewUpcomingEvents);
				//System.out.println("upcoming events size: " + events.size());
				if (events.size() == 0) {
					message.setVisibility(View.VISIBLE);
					message.setText("No Upcoming Events");
				}else{
					message.setVisibility(View.GONE);
				}
				progDailog.dismiss();
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
			Class ourClass = Class.forName("com.MyInvite.Application.UpcomingEvent");
			Intent ourIntent = new Intent(getActivity(), ourClass);
			ourIntent.putExtra("notified", notified[position]);
			ourIntent.putExtra("eventid", ids[position]);
			ourIntent.putExtra("open", open[position]);
			ourIntent.putExtra("host", hosts[position]);
			startActivity(ourIntent);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

}
