package com.Zap.InstantConnection.Events;
import com.Zap.InstantConnection.*;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class UpcomingEvent extends SherlockActivity implements OnClickListener {

	Button yes, no, viewMap, addGuests, viewGuests, viewMessages, viewChangeRequests;
	TextView title, location, dateDisplay, timeDisplay, hostName, notes;
	public static int notificationUserId;
	int userId, responseInt, open, requested, host, notified;
	private AsyncTask<String, Void, String> task;
	String response;
	HashMap<String, String> args;
	ProgressDialog progDialog;
	NotificationManager nm;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);	
		if(!AppUtils.haveNetworkConnection(this)) {
			AppUtils.showConnectionFailed(this);
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
			addGuests = (Button) findViewById(R.id.bUpcomingEventAddGuests);
			viewMap = (Button) findViewById(R.id.bUpcomingEventViewMap);
			viewGuests = (Button) findViewById(R.id.bUpcomingEventViewGuests);
			viewMessages = (Button) findViewById(R.id.bUpcomingEventViewMessages);
			viewChangeRequests = (Button) findViewById(R.id.bUpcomingEventViewChangeRequests);
			
			title = (TextView) findViewById(R.id.tvUpcomingEventTitle);
			location = (TextView) findViewById(R.id.tvUpcomingEventLocation);
			dateDisplay = (TextView) findViewById(R.id.tvUpcomingEventDate);
			timeDisplay = (TextView) findViewById(R.id.tvUpcomingEventTime);
			hostName = (TextView) findViewById(R.id.tvUpcomingEventHost);
			notes = (TextView) findViewById(R.id.tvUpcomingEventNote);
			
			Bundle extras = getIntent().getExtras();
			args = new HashMap<String, String>();
			Main.eventid = extras.getInt("eventid");
			//System.out.println("upcoming event id: " + Main.eventid);
			notified = extras.getInt("notified");
			open = extras.getInt("open");
			host = extras.getInt("host");
			
			progDialog = new ProgressDialog(UpcomingEvent.this);
            progDialog.setMessage("Loading...");
            progDialog.setIndeterminate(false);
            progDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progDialog.setCancelable(false);
            progDialog.show();
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
						response = DatabaseAccess.getData(AppUrls.SetEventToNotified, args);
					}
					response = DatabaseAccess.getData(AppUrls.GetUpcomingEventInfo, args);
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
						
						String dateStr = data.getString("date");
						String timeStr = data.getString("time");
		                try {
		                	SimpleDateFormat sdf =  new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		                    Date date = sdf.parse(dateStr);
		                    sdf = new SimpleDateFormat("MM-dd-yyyy");
		                    dateDisplay.setText(sdf.format(date));
		                    
		                    sdf =  new SimpleDateFormat("HH:mm:ss");
		                    date = sdf.parse(timeStr);
		                    sdf = new SimpleDateFormat("h:mm a");		                    
		                    timeDisplay.setText(sdf.format(date));
		                    
		                } catch (Exception e) {
		                    e.printStackTrace();
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
						
					} catch (JSONException e) {
						e.printStackTrace();
					}
					
					if(open == 1 || Main.userid == host) {
				    	addGuests.setVisibility(View.VISIBLE);
				    } else {
				    	addGuests.setVisibility(View.GONE);
				    }
					
					if(responseInt == 1){
						yes.setBackgroundResource(R.drawable.button_green);
						no.setBackgroundResource(R.drawable.button);
					}else if(responseInt == 0){
						yes.setBackgroundResource(R.drawable.button);
						no.setBackgroundResource(R.drawable.button_red);
					}else{
						//yes.setBackgroundColor(Color.WHITE);
						//no.setBackgroundColor(Color.WHITE);
					}
					
//					boolean validLocation = AppUtils.checkLocation(UpcomingEvent.this, location.getText().toString());
//					if(validLocation){
//						viewMap.setVisibility(View.VISIBLE);
//						viewMap.setOnClickListener(UpcomingEvent.this);
//					}else{
//						viewMap.setVisibility(View.INVISIBLE);
//					}
					
					
					
//					Geocoder geocoder = new Geocoder(UpcomingEvent.this, Locale.getDefault());
//					//GeoPoint g = null;
//					try {
//					    List<Address> geoResults = geocoder.getFromLocationName(location.getText().toString(), 1);
//					    System.out.println("geoResult: " + geoResults);
//					    if(geoResults == null){
//					    	viewMap.setVisibility(View.INVISIBLE);
//					    }
//					    if (geoResults.size()>0) {
//					        Address addr = geoResults.get(0);
//					        System.out.println(addr.toString());
//					        if(addr.toString().equals("0")){
//					        	viewMap.setVisibility(View.INVISIBLE);
//					        }
//					    }
//					} catch (Exception e) {
//						System.out.print("check location failed");
//					    System.out.print(e.getMessage());
//					}

//					if(location.length() <= 6){
//						viewMap.setVisibility(View.INVISIBLE);
//					}
					
					viewMap.setVisibility(View.VISIBLE);
					viewMap.setOnClickListener(UpcomingEvent.this);
					yes.setOnClickListener(UpcomingEvent.this);
					no.setOnClickListener(UpcomingEvent.this);
					addGuests.setOnClickListener(UpcomingEvent.this);
					viewGuests.setOnClickListener(UpcomingEvent.this);
					viewMessages.setOnClickListener(UpcomingEvent.this);
					viewChangeRequests.setOnClickListener(UpcomingEvent.this);
					nm.cancel(Main.eventid);
					progDialog.dismiss();
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
    			Intent openViewGuests = new Intent("com.Zap.InstantConnection.Guests.VIEWGUESTS");
    			openViewGuests.putExtra("eventid", Main.eventid);
    			openViewGuests.putExtra("open", open);
    			openViewGuests.putExtra("host", host);
    			startActivity(openViewGuests);
        		return true;
        	case R.id.iUpcomingEventAddGuests:
        		Intent openContactList = new Intent("com.Zap.InstantConnection.Guests.ADDGUESTMENU");
    			startActivity(openContactList);
        		return true;
        	case R.id.iUpcomingEventChat:
        		Intent openChat = new Intent("com.Zap.InstantConnection.Messages.MESSAGES");
    			openChat.putExtra("eventid", Main.eventid);
    			startActivity(openChat);
        		return true;
        	case R.id.iUpcomingEventChangeRequests:
    			Intent openChangeRequests = new Intent("com.Zap.InstantConnection.ChangeRequests.VIEWCHANGEREQUESTS");
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
    	    			DatabaseAccess.getData(AppUrls.RemoveUpcomingEvent, args);
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
			yes.setBackgroundResource(R.drawable.button_green);
			no.setBackgroundResource(R.drawable.button);
			task = new AsyncTask<String, Void, String>() {
				@Override
				protected String doInBackground(String... arg0) {
					args = new HashMap<String, String>();
					args.put("userid", userId + "");
					args.put("eventid", Main.eventid + "");
					args.put("response", "1");
					DatabaseAccess.getData(AppUrls.RSVP, args);
					return null;
				}
				@Override
				protected void onPostExecute(String result) {}
			};
			task.execute();
			break;

		case R.id.bUpcomingEventNo:		
			yes.setBackgroundResource(R.drawable.button);
			no.setBackgroundResource(R.drawable.button_red);
			task = new AsyncTask<String, Void, String>() {
				@Override
				protected String doInBackground(String... arg0) {
					args = new HashMap<String, String>();
					args.put("userid", userId + "");
					args.put("eventid", Main.eventid + "");
					args.put("response", "0");
					DatabaseAccess.getData(AppUrls.RSVP, args);
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
			
		case R.id.bUpcomingEventAddGuests:
			Intent openContactList = new Intent("com.Zap.InstantConnection.Guests.ADDGUESTMENU");
			startActivity(openContactList);
			break;
			
		case R.id.bUpcomingEventViewGuests:
			Intent openViewGuests = new Intent("com.Zap.InstantConnection.Guests.VIEWGUESTS");
			openViewGuests.putExtra("eventid", Main.eventid);
			openViewGuests.putExtra("open", open);
			openViewGuests.putExtra("host", host);
			startActivity(openViewGuests);
			break;
		case R.id.bUpcomingEventViewMessages:
			Intent openChat = new Intent("com.Zap.InstantConnection.Messages.MESSAGES");
			openChat.putExtra("eventid", Main.eventid);
			startActivity(openChat);
			break;
		case R.id.bUpcomingEventViewChangeRequests:
			Intent openChangeRequests = new Intent("com.Zap.InstantConnection.ChangeRequests.VIEWCHANGEREQUESTS");
			openChangeRequests.putExtra("eventid", Main.eventid);
			openChangeRequests.putExtra("requested", requested);
			startActivity(openChangeRequests);
			break;
		}
	}
	
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
	}

}
