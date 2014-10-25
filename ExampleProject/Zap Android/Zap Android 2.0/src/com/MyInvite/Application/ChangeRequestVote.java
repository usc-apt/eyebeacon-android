package com.MyInvite.Application;

import java.util.HashMap;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class ChangeRequestVote extends SherlockActivity implements OnClickListener {

	Button yes, no, home;
	int eventid, changeid, response, numYes, numNo;
	Request r;
	View buttons, responses;
	TextView requester, date, time, location, numYesText, numNoText;
	String data;
	HashMap<String, String> args;
	AsyncTask<String, Void, String> task;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
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
			setContentView(R.layout.changerequestvote);
			ActionBar bar = getSupportActionBar();
			bar.show();
			bar.setTitle("View Request");
			Bundle extras = getIntent().getExtras();
			eventid = extras.getInt("eventid");
			changeid = extras.getInt("changeid");
			response = extras.getInt("response");
			numYes = extras.getInt("numYes");
			numNo = extras.getInt("numNo");
			r = extras.getParcelable("request");
			
			requester = (TextView) findViewById(R.id.tvRequester);
			requester.setText(r.getRequester());
			date = (TextView) findViewById(R.id.tvDate);
			date.setText(r.getDate());
			time = (TextView) findViewById(R.id.tvTime);
			time.setText(r.getTime());
			location = (TextView) findViewById(R.id.tvLocation);
			location.setText(r.getLocation());
			numYesText = (TextView) findViewById(R.id.tvYes);
			numNoText = (TextView) findViewById(R.id.tvNo);
			
			buttons = findViewById(R.id.ChangeRequestVoteButtons);
			responses = findViewById(R.id.ChangeRequestVoteResponses);
			
			yes = (Button) findViewById(R.id.bRequestYes);
			no = (Button) findViewById(R.id.bRequestNo);
			if (response != 0) {
				//responded
				yes.setVisibility(View.GONE);
				no.setVisibility(View.GONE);
				numYesText.setText(numYes + "");
				numYesText.setTextColor(Color.GREEN);
				numNoText.setText(numNo + "");
				numNoText.setTextColor(Color.RED);
			}else{
				yes.setOnClickListener(this);
				no.setOnClickListener(this);
				responses.setVisibility(View.INVISIBLE);
			}
		}
	}
	
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()){
		case R.id.bRequestYes:
			buttons.setVisibility(View.GONE);
			task = new AsyncTask<String, Void, String>() {
				@Override
				protected String doInBackground(String... arg0) {
					args = new HashMap<String, String>();
					args.put("userid", Main.userid + "");			
					args.put("changeid", changeid + "");
					args.put("response", "1");
					data = DatabaseAccess.getData(Main.serverName + "ChangeRequestVote.php", args);
					return null;
				}

				@Override
				protected void onPostExecute(String result) {
					responses.setVisibility(View.VISIBLE);
					numYesText = (TextView) findViewById(R.id.tvYes);
					numNoText = (TextView) findViewById(R.id.tvNo);
					numYesText.setText((numYes+1)+"");
					numYesText.setTextColor(Color.GREEN);
					numNoText.setText(numNo+"");
					numNoText.setTextColor(Color.RED);
				}
			};
			task.execute();	
			break;
		case R.id.bRequestNo:
			buttons.setVisibility(View.GONE);
			task = new AsyncTask<String, Void, String>() {
				@Override
				protected String doInBackground(String... arg0) {
					args = new HashMap<String, String>();
					args.put("userid", Main.userid + "");			
					args.put("changeid", changeid + "");
					args.put("response", "2");
					data = DatabaseAccess.getData(Main.serverName + "ChangeRequestVote.php", args);
					return null;
				}

				@Override
				protected void onPostExecute(String result) {
					responses.setVisibility(View.VISIBLE);
					numYesText = (TextView) findViewById(R.id.tvYes);
					numNoText = (TextView) findViewById(R.id.tvNo);
					numYesText.setText(numYes+"");
					numYesText.setTextColor(Color.GREEN);
					numNoText.setText((numNo+1)+"");
					numNoText.setTextColor(Color.RED);
				}
			};
			task.execute();
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
