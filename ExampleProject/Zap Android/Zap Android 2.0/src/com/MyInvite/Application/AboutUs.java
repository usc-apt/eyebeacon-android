package com.MyInvite.Application;

import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockActivity;

public class AboutUs extends SherlockActivity implements OnClickListener{
	
	TextView email, website;
	
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
			setContentView(R.layout.aboutus);
			ActionBar bar = getSupportActionBar();
			bar.show();
			bar.setTitle("About Us");			
			email = (TextView) findViewById(R.id.tvAboutUsEmail);
			email.setOnClickListener(this);
			website = (TextView) findViewById(R.id.tvAboutUsWebsite);
			website.setOnClickListener(this);
		}
	}
	
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		switch (arg0.getId()){
		case R.id.tvAboutUsEmail:
			try{
				Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
				String[] emails = {email.getText().toString()};
				emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, emails);
				emailIntent.setType("plain/text");
				startActivity(emailIntent); 
			}catch(ActivityNotFoundException e){
				System.out.println("mail failed: " + e);
			}
			break;
		case R.id.tvAboutUsWebsite:
			System.out.println(website.getText().toString());
			Intent openWebsite = new Intent(Intent.ACTION_VIEW, Uri.parse("http://"+website.getText().toString().substring(11)));
			startActivity(openWebsite);
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
