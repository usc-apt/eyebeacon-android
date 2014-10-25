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

public class AddByEmail extends SherlockActivity implements OnClickListener {

	Button addContact;
	EditText email;
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
			setContentView(R.layout.addbyemail);
			ActionBar bar = getSupportActionBar();
			bar.show();
			bar.setTitle("Add Contact");
			addContact = (Button) findViewById(R.id.bAddByEmailAdd);
			addContact.setOnClickListener(this);
			email = (EditText) findViewById(R.id.etAddByEmailEmail);
		}
	}
	
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		switch (arg0.getId()) {
		case R.id.bAddByEmailAdd:
			AsyncTask<String, Void, String> task = new AsyncTask<String, Void, String>() {
				@Override
				protected String doInBackground(String... arg0) {
					HashMap<String,String>args = new HashMap<String,String>();
					args.put("userid", Main.userid+"");
					args.put("contactEmail", email.getText().toString());
					response = DatabaseAccess.getData(Main.serverName + "AddEmailToContacts.php", args);
					return null;
				}

				@Override
				protected void onPostExecute(String result) {
					if (response.charAt(0) != '0') {
						String message = email.getText().toString() + " added to your contact list";
						Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
						finish();
					}else{
						final AlertDialog.Builder builder = new AlertDialog.Builder(AddByEmail.this);
						builder.setTitle("Add User to Contact List Failed");
						builder.setMessage("User with given email does not exist.");
						builder.setNegativeButton("Ok",	new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int which) {
								// TODO Auto-generated method stub
								builder.setCancelable(true);
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
