package com.MyInvite.Application;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONObject;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockListActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

public class ChangeRequests extends SherlockListActivity {

	public ArrayList<Request> requests = new ArrayList<Request>();
	private ChangeRequestsAdapter adapter;
	boolean requested = false;
	int[] ids, responses, changeIds, numYes, numNo;
	TextView message;
	int eventid;
	String response;
	ProgressDialog progDailog;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		if (!haveNetworkConnection()) {
			final AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle("Connection failed");
			builder.setMessage("Make sure you are connected to the internet.");
			builder.setNegativeButton("Ok",	new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					builder.setCancelable(false);
					finish();
				}
			});
			builder.show();
		} else {
			setContentView(R.layout.changerequests);
			ActionBar bar = getSupportActionBar();
			bar.show();
			bar.setTitle("Change Requests");
			progDailog = new ProgressDialog(this);
            progDailog.setMessage("Loading...");
            progDailog.setIndeterminate(false);
            progDailog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progDailog.setCancelable(false);
            progDailog.show();
			message = (TextView) findViewById(R.id.tvChangeRequests);
			
			Bundle extras = getIntent().getExtras();
			eventid = extras.getInt("eventid");
			AsyncTask<String, Void, String> task = new AsyncTask<String, Void, String>() {
				@Override
				protected String doInBackground(String... arg0) {
					HashMap<String, String> args = new HashMap<String, String>();
					args.put("userid", Main.userid + "");
					args.put("eventid", eventid + "");
					response = DatabaseAccess.getData(Main.serverName + "ViewChangeRequests.php", args);
					return null;
				}

				@Override
				protected void onPostExecute(String result) {
					try {
						JSONArray jArray = new JSONArray(response);
						int length = jArray.length();
						requests = new ArrayList<Request>(length);
						ids = new int[length];
						responses = new int[length];
						numYes = new int[length];
						numNo = new int[length];
						changeIds = new int[length];
						for (int i = 0; i < length; i++) {
							JSONObject json_data = jArray.getJSONObject(i);
							ids[i] = json_data.getInt("id");
							
							String fullDate = json_data.getString("date");
							String m = fullDate.substring(5, 7);
							String d = fullDate.substring(8, 10);
							String y = fullDate.substring(0, 4);
							String date = m + "-" + d + "-" + y;
							
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
							requests.add(new Request(json_data.getString("name"),
									date, time,	json_data.getString("location")));
							responses[i] = json_data.getInt("response");
							changeIds[i] = json_data.getInt("changeid");
							numYes[i] = json_data.getInt("yes");
							numNo[i] = json_data.getInt("no");
							if(Main.userid == ids[i]){
								requested = true;
							}
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
					adapter = new ChangeRequestsAdapter(getApplicationContext(), R.layout.requestcell,	requests);
					setListAdapter(adapter);
					if (requests.size() == 0) {
						message.setText("No change requests have been made.\nClick the plus button to make a change request.");
					}else{
						message.setText("Click On Request To Vote");
					}
					progDailog.dismiss();
				}
			};
			task.execute();
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    MenuInflater inflater = getSupportMenuInflater();
	    inflater.inflate(R.menu.changerequestsitems, menu);
	    return true;
	}
	
	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
            switch(item.getItemId()) {
            	case R.id.iChangeRequestsAdd:
            		if(requested){
        				final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        				builder.setTitle("Can't Add Request");
        				builder.setMessage("Each guest can only make one change request.");
        				builder.setNegativeButton("Ok",	new DialogInterface.OnClickListener() {
        					public void onClick(DialogInterface dialog, int which) {
        						builder.setCancelable(false);
        						//finish();
        					}
        				});
        				builder.show();
        			}else{
        				Intent openMakeRequest = new Intent("com.MyInvite.Application.MAKEREQUEST");
        				openMakeRequest.putExtra("eventid", eventid);
        				startActivity(openMakeRequest);
        			}
            		return true;
            	default:
                    return super.onOptionsItemSelected(item);
            }
    }

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		// TODO Auto-generated method stub
		super.onListItemClick(l, v, position, id);
		try{
			Class ourClass = Class.forName("com.MyInvite.Application.ChangeRequestVote");
			Intent ourIntent = new Intent(ChangeRequests.this, ourClass);
			ourIntent.putExtra("eventid", eventid);
			ourIntent.putExtra("request", requests.get(position));
			ourIntent.putExtra("changeid", changeIds[position]);
			ourIntent.putExtra("response", responses[position]);
			ourIntent.putExtra("numYes", numYes[position]);
			ourIntent.putExtra("numNo", numNo[position]);
			startActivity(ourIntent);
		}catch(ClassNotFoundException e){
			e.printStackTrace();
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
