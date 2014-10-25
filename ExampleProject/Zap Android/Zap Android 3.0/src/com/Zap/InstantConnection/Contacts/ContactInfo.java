package com.Zap.InstantConnection.Contacts;
import com.Zap.InstantConnection.*;

import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.telephony.PhoneStateListener;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class ContactInfo extends SherlockActivity {

	public static int contactId;
	String guestEmail, guestPhoneNumber, response;
	TextView name, username, phone, email;
	ProgressDialog progDailog;
	
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
			setContentView(R.layout.contactinfo);
			ActionBar bar = getSupportActionBar();
			bar.show();
			bar.setTitle("Contact Info");
			progDailog = new ProgressDialog(this);
            progDailog.setMessage("Loading...");
            progDailog.setIndeterminate(false);
            progDailog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progDailog.setCancelable(true);
            progDailog.show();
			name = (TextView) findViewById(R.id.tvContactInfoName);
			username = (TextView) findViewById(R.id.tvContactInfoUsername);
			phone = (TextView) findViewById(R.id.tvContactInfoPhone);
			email = (TextView) findViewById(R.id.tvContactInfoEmail);
			
			Bundle extras = getIntent().getExtras();
			contactId = extras.getInt("id");
			
			AsyncTask<String, Void, String> task = new AsyncTask<String, Void, String>() {
				@Override
				protected String doInBackground(String... arg0) {
					HashMap<String, String> args = new HashMap<String, String>();
					args.put("contactid", contactId + "");
					response = DatabaseAccess.getData(AppUrls.GetContactInfo, args);
					return null;
				}

				@Override
				protected void onPostExecute(String result) {
					try {
						JSONArray jArray = new JSONArray(response);
						JSONObject data = jArray.getJSONObject(0);
						name.setText(data.getString("name"));
						username.setText(data.getString("username"));
						phone.setText(data.getString("phone_number"));
						email.setText(data.getString("email"));
					} catch (JSONException e) {
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
	    inflater.inflate(R.menu.contactinfoitems, menu);
	    return true;
	}
	
	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
            switch(item.getItemId()) {
            	case R.id.iContactInfoCall:
            		try{
        				Intent callIntent = new Intent(Intent.ACTION_CALL);
        				callIntent.setData(Uri.parse("tel:" + phone.getText().toString()));
        				startActivity(callIntent);
        			}catch(ActivityNotFoundException e){
        				System.out.println("calling failed: " + e);
        			}
            		return true;
            	case R.id.iContactInfoMessage:
            		try{
        				Intent textIntent = new Intent(Intent.ACTION_VIEW);
        				textIntent.setData(Uri.fromParts("sms", phone.getText().toString(), null));
        				startActivity(textIntent);
        			}catch(ActivityNotFoundException e){
        				System.out.println("text failed: " + e);
        			}
            		return true;
            	case R.id.iContactInfoEmail:
            		try{
        				Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
        				String[] emails = {email.getText().toString()};
        				emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, emails);
        				//emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Zap");
        				//emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, "Hi, I'm sending an Email from Zap");
        				//startActivity(Intent.createChooser(email, "Choose an Email client :"));
        				emailIntent.setType("plain/text");
        				startActivity(emailIntent); 
        			}catch(ActivityNotFoundException e){
        				System.out.println("mail failed: " + e);
        			}
            		return true;
            	case R.id.iContactInfoRemove:
            		AsyncTask<String, Void, String> task = new AsyncTask<String, Void, String>() {
        				@Override
        				protected String doInBackground(String... arg0) {
        					HashMap<String, String> args = new HashMap<String, String>();
        					args.put("contactid", contactId + "");
        					args.put("userid", Main.userid + "");
        					DatabaseAccess.getData(AppUrls.RemoveContact, args);
        					finish();
        					return null;
        				}
        			};
        			task.execute();
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
