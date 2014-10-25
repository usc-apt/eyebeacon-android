package com.MyInvite.Application;

import java.util.HashMap;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class AddUser extends SherlockActivity implements OnClickListener{

	Button addUser;
	EditText username;
	String response;
	
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
			setContentView(R.layout.adduser);
			ActionBar bar = getSupportActionBar();
			bar.show();
			bar.setTitle("Add Guest");
			addUser = (Button) findViewById(R.id.bAddUserSubmit);
			addUser.setOnClickListener(this);
			username = (EditText) findViewById(R.id.etAddUsername);
		}
	}
	
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		switch (arg0.getId()) {
		case R.id.bAddUserSubmit:
			AsyncTask<String, Void, String> task = new AsyncTask<String, Void, String>() {
				@Override
				protected String doInBackground(String... arg0) {
					//for MyEvent
					HashMap<String, String> args = new HashMap<String, String>();
					args.put("eventid", Main.eventid + "");
					args.put("username", username.getText().toString());
					response = DatabaseAccess.getData(Main.serverName + "InviteUser.php", args);
					return null;
				}

				@Override
				protected void onPostExecute(String result) {
					System.out.println("response: " + response);
					if (response.charAt(0) != '0') {
						// username exists
						String message = username.getText().toString() + " added to event";
						Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
//						Intent openViewGuests = new Intent("com.MyInvite.Application.VIEWGUESTS");
//						openViewGuests.putExtra("eventid", Main.eventid);
//						startActivity(openViewGuests);
						finish();
					} else {
						final AlertDialog.Builder builder = new AlertDialog.Builder(AddUser.this);
						builder.setTitle("Add User Failed");
						builder.setMessage("Username does not exist or user has already been invited.");
						builder.setNegativeButton("Ok",	new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,	int which) {
								// TODO Auto-generated method stub
								builder.setCancelable(true);
								username.setText("");
							}
						});
						builder.show();
					}
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
