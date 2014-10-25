package com.MyInvite.Application;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

import android.app.AlertDialog;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class UpcomingEvent extends SherlockActivity implements OnClickListener {

	Button yes, no, viewMap, addGuest;
	TextView title, location, date, time, hostName, notes;
	public static int notificationUserId;
	int userId, responseInt, open, requested, host, notified;
	private AsyncTask<String, Void, String> task;
	String response;
	HashMap<String, String> args;
	ProgressDialog progDailog;
	NotificationManager nm;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);	
		if(!haveNetworkConnection()){
			final AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle("Connection failed");
			builder.setMessage("Make sure you are connected to the internet.");
			builder.setNegativeButton("Ok", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					builder.setCancelable(false);
					finish();
				}
			});
			builder.show();
		}else{
			setContentView(R.layout.upcomingevent);	
			ActionBar bar = getSupportActionBar();
			bar.show();
			bar.setTitle("Upcoming Event");
			
			nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
			
			if(Main.userid!=0){
				userId = Main.userid;
			}
			else{
				userId = UpcomingEvent.notificationUserId;
			}
			
			yes = (Button) findViewById(R.id.bUpcomingEventYes);
			no = (Button) findViewById(R.id.bUpcomingEventNo);
			addGuest = (Button) findViewById(R.id.bUpcomingEventViewGuest);
			viewMap = (Button) findViewById(R.id.bUpcomingEventViewMap);
			
			title = (TextView) findViewById(R.id.tvUpcomingEventTitle);
			location = (TextView) findViewById(R.id.tvUpcomingEventLocation);
			date = (TextView) findViewById(R.id.tvUpcomingEventDate);
			time = (TextView) findViewById(R.id.tvUpcomingEventTime);
			hostName = (TextView) findViewById(R.id.tvUpcomingEventHost);
			notes = (TextView) findViewById(R.id.tvUpcomingEventNote);
			
			Bundle extras = getIntent().getExtras();
			args = new HashMap<String, String>();
			Main.eventid = extras.getInt("eventid");
			System.out.println("upcoming event id: " + Main.eventid);
			notified = extras.getInt("notified");
			open = extras.getInt("open");
			host = extras.getInt("host");
			
			progDailog = new ProgressDialog(UpcomingEvent.this);
            progDailog.setMessage("Loading...");
            progDailog.setIndeterminate(false);
            progDailog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progDailog.setCancelable(false);
            progDailog.show();
			task = new AsyncTask<String, Void, String>() {
				@Override
		        protected void onPreExecute() {
		            super.onPreExecute();
		        }
				
				@Override
				protected String doInBackground(String... arg0) {
					args.put("eventid", Main.eventid + "");
					args.put("userid", Main.userid+"");
					if(notified != 1){
						response = DatabaseAccess.getData(Main.serverName + "SetNotified.php", args);
					}
					response = DatabaseAccess.getData(Main.serverName + "GetUpcomingEventInfo.php", args);
					return null;
				}
			
				@Override
				protected void onPostExecute(String result) {
					System.out.println("Upcoming Event: " + response);
					try {
						JSONArray jArray = new JSONArray(response);
						JSONObject data = jArray.getJSONObject(0);
						title.setText(data.getString("title"));
						location.setText(data.getString("location"));
						
						String fullDate = data.getString("date");
						String m = fullDate.substring(5, 7);
						String d = fullDate.substring(8, 10);
						String y = fullDate.substring(0, 4);
						date.setText(m + "-" + d + "-" + y);
						
						String fullTime = data.getString("time");
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
			  			if(pm){
			  				time.setText(hour + ":" + min + " PM");
			  			}else{
			  				time.setText(hour + ":" + min + " AM");
			  			}
						
						hostName.setText(data.getString("name"));
						host = data.getInt("host");
						String note = data.getString("notes");
						if(note == ""){
							notes.setText("none");
						}else{
							notes.setText(note);
						}
						responseInt = data.getInt("response");
						open = data.getInt("open");
						//System.out.println(open);
					} catch (JSONException e) {
						e.printStackTrace();
					}
					
					if(responseInt == 1){
						yes.setBackgroundColor(Color.GREEN);
						no.setBackgroundColor(Color.WHITE);
					}else if(responseInt == 0){
						yes.setBackgroundColor(Color.WHITE);
						no.setBackgroundColor(Color.RED);
					}else{
						//yes.setBackgroundColor(Color.WHITE);
						//no.setBackgroundColor(Color.WHITE);
					}
					
					Geocoder geocoder = new Geocoder(UpcomingEvent.this, Locale.getDefault());
					//GeoPoint g = null;
					try {
					    List<Address> geoResults = geocoder.getFromLocationName(location.getText().toString(), 1);
					    System.out.println("geoResult: " + geoResults);
					    if(geoResults == null){
					    	viewMap.setVisibility(View.INVISIBLE);
					    }
					    if (geoResults.size()>0) {
					        Address addr = geoResults.get(0);
					        System.out.println(addr.toString());
					        if(addr.toString().equals("0")){
					        	viewMap.setVisibility(View.INVISIBLE);
					        }
					    }
					} catch (Exception e) {
						System.out.print("check location failed");
					    System.out.print(e.getMessage());
					}

					if(location.length() <= 6){
						viewMap.setVisibility(View.INVISIBLE);
					}
					
					progDailog.dismiss();
					nm.cancel(Main.eventid);
					yes.setOnClickListener(UpcomingEvent.this);
					no.setOnClickListener(UpcomingEvent.this);
					viewMap.setOnClickListener(UpcomingEvent.this);
					addGuest.setOnClickListener(UpcomingEvent.this);
				}
			};
			task.execute();
		}
	}

	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    MenuInflater inflater = getSupportMenuInflater();
	    if(open == 1 || Main.userid == host) {
	    	inflater.inflate(R.menu.upcomingeventitems_open, menu);
	    } else {
	    	inflater.inflate(R.menu.upcomingeventitems_closed, menu);
	    }
	    return true;
	}
	
	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
        	case R.id.iUpcomingEventViewGuests:
    			Intent openViewGuests = new Intent("com.MyInvite.Application.VIEWGUESTS");
    			openViewGuests.putExtra("eventid", Main.eventid);
    			openViewGuests.putExtra("open", open);
    			openViewGuests.putExtra("host", host);
    			startActivity(openViewGuests);
        		return true;
        	case R.id.iUpcomingEventAddGuests:
        		Intent openContactList = new Intent("com.MyInvite.Application.ADDGUESTMENU");
    			startActivity(openContactList);
        		return true;
        	case R.id.iUpcomingEventChat:
        		Intent openChat = new Intent("com.MyInvite.Application.MESSAGES");
    			openChat.putExtra("eventid", Main.eventid);
    			startActivity(openChat);
        		return true;
        	case R.id.iUpcomingEventChangeRequests:
    			Intent openChangeRequests = new Intent("com.MyInvite.Application.CHANGEREQUESTS");
    			openChangeRequests.putExtra("eventid", Main.eventid);
    			openChangeRequests.putExtra("requested", requested);
    			startActivity(openChangeRequests);
        		return true;
        	case R.id.iUpcomingEventRemove:
        		task = new AsyncTask<String, Void, String>() {
    				@Override
    				protected String doInBackground(String... arg0) {
    					args = new HashMap<String, String>();
    	    			args.put("userid", userId + "");
    	    			args.put("eventid", Main.eventid + "");
    	    			DatabaseAccess.getData(Main.serverName + "RemoveUpcomingEvent.php", args);
    					return null;
    				}
    				@Override
    				protected void onPostExecute(String result) {
    					finish();
    				}
    			};
    			task.execute();
        		return true;
        	default:
                return super.onOptionsItemSelected(item);
        }
    }

	public void onClick(View arg0) {
		switch (arg0.getId()) {
		case R.id.bUpcomingEventYes:
			yes.setBackgroundColor(Color.GREEN);
			no.setBackgroundColor(Color.WHITE);
			task = new AsyncTask<String, Void, String>() {
				@Override
				protected String doInBackground(String... arg0) {
					args = new HashMap<String, String>();
					args.put("userid", userId + "");
					args.put("eventid", Main.eventid + "");
					args.put("response", "1");
					DatabaseAccess.getData(Main.serverName + "RSVP.php", args);
					return null;
				}
				@Override
				protected void onPostExecute(String result) {}
			};
			task.execute();
			break;

		case R.id.bUpcomingEventNo:		
			yes.setBackgroundColor(Color.WHITE);
			no.setBackgroundColor(Color.RED);
			task = new AsyncTask<String, Void, String>() {
				@Override
				protected String doInBackground(String... arg0) {
					args = new HashMap<String, String>();
					args.put("userid", userId + "");
					args.put("eventid", Main.eventid + "");
					args.put("response", "0");
					DatabaseAccess.getData(Main.serverName + "RSVP.php", args);
					return null;
				}
				@Override
				protected void onPostExecute(String result) {}
			};
			task.execute();
			break;
			
		case R.id.bUpcomingEventViewMap:
			String uri = "http://maps.google.co.in/maps?q=" + location.getText().toString();
			Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
			startActivity(intent);
			break;
			
		case R.id.bUpcomingEventViewGuest:
			Intent openViewGuests = new Intent("com.MyInvite.Application.VIEWGUESTS");
			openViewGuests.putExtra("eventid", Main.eventid);
			openViewGuests.putExtra("open", open);
			openViewGuests.putExtra("host", host);
			startActivity(openViewGuests);
			break;
		}
	}
	
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
	}
	
	public boolean haveNetworkConnection() {
	    boolean haveConnectedWifi = false;
	    boolean haveConnectedMobile = false;

	    ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
	    NetworkInfo[] netInfo = cm.getAllNetworkInfo();
	    for (NetworkInfo ni : netInfo) {
	        if (ni.getTypeName().equalsIgnoreCase("WIFI"))
	            if (ni.isConnected())
	                haveConnectedWifi = true;
	        if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
	            if (ni.isConnected())
	                haveConnectedMobile = true;
	    }
	    return haveConnectedWifi || haveConnectedMobile;
	}

}
