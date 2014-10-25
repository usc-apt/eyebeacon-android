package com.MyInvite.Application;

import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class ViewGuests extends SherlockActivity {
	
	String[] contacts, usernames, results, responses;
	int[] ids;
	int eventid, open, host;
	String data;
	TableLayout table;
	TableLayout.LayoutParams tableRowParams;
	TableRow row = null;
	TextView name = null;
	TextView response = null;
	ProgressDialog progDailog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		
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
			setContentView(R.layout.viewguests);
			ActionBar bar = getSupportActionBar();
			bar.show();
			bar.setTitle("Guests");		
			progDailog = new ProgressDialog(this);
            progDailog.setMessage("Loading...");
            progDailog.setIndeterminate(false);
            progDailog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progDailog.setCancelable(false);
            progDailog.show();
			table = (TableLayout) findViewById(R.id.tlViewGuestsTableLayout);
			tableRowParams = new TableLayout.LayoutParams(TableLayout.LayoutParams.FILL_PARENT,TableLayout.LayoutParams.WRAP_CONTENT);
			int leftMargin = 10;
			int topMargin = 2;
			int rightMargin = 10;
			int bottomMargin = 2;
			tableRowParams.setMargins(leftMargin, topMargin, rightMargin, bottomMargin);
			
			Bundle extras = getIntent().getExtras();
			eventid = extras.getInt("eventid");
			open = extras.getInt("open");
			host = extras.getInt("host");
			
			AsyncTask<String, Void, String> task = new AsyncTask<String, Void, String>() {
				@Override
				protected String doInBackground(String... arg0) {
					HashMap<String, String> args = new HashMap<String, String>();
					args.put("eventid", eventid+"");	
					data = DatabaseAccess.getData(Main.serverName + "ViewGuests.php", args);
					return null;
				}

				@Override
				protected void onPostExecute(String result) {
					try {
						JSONArray jArray = new JSONArray(data);
						
						for (int i = 0; i < jArray.length(); i++) {
							row = new TableRow(ViewGuests.this);
							row.setId(100 + i);

							JSONObject json_data = jArray.getJSONObject(i);
							String guestName = "";
							int virtual = json_data.getInt("virtual");
							String phoneNo = json_data.getString("phone_number");

							if (Main.numbers.containsKey(phoneNo) || virtual == 0) {
								row = new TableRow(ViewGuests.this);
								row.setId(100 + i);
								if (virtual == 1) {
									guestName = Main.numbers.get(phoneNo);
								} else if (virtual == 0) {
									guestName = json_data.getString("name");
								}
								name = new TextView(ViewGuests.this);
								name.setText(guestName);
								name.setTextSize(20);
								//name.setWidth(275);
								name.setTextColor(Color.WHITE);

								response = new TextView(ViewGuests.this);
								if (json_data.getInt("response") == 0) {
									response.setText("No");
									response.setTextColor(Color.RED);
								} else if (json_data.getInt("response") == 1) {
									response.setText("Yes");
									response.setTextColor(Color.GREEN);
								} else {
									response.setText("");
								}
								response.setTextSize(25);

								final int userId = json_data.getInt("id");
								row.setOnClickListener(new OnClickListener() {
									public void onClick(View v) {
										try {
											Class ourClass = Class.forName("com.MyInvite.Application.GuestInfo");
											Intent ourIntent = new Intent(ViewGuests.this, ourClass);
											ourIntent.putExtra("id", userId);
											startActivity(ourIntent);
										} catch (ClassNotFoundException e) {
											e.printStackTrace();
										}
									}
								});
								row.addView(name);
								row.addView(response);
								row.setLayoutParams(tableRowParams);
								table.addView(row);
								table.setStretchAllColumns(true);
							}
						}
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
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
	    if(open == 1 || Main.userid == host) {
	    	inflater.inflate(R.menu.viewguests_open, menu);
	    } else {
	    	super.onCreateOptionsMenu(menu);
	    }
	    return true;
	}
	
	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
        	case R.id.iViewGuestsAdd:
        		Intent openContactList = new Intent("com.MyInvite.Application.ADDGUESTMENU");
    			startActivity(openContactList);
        		return true;
        	default:
                return super.onOptionsItemSelected(item);
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
