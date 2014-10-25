package com.MyInvite.Application;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class AddGuestMenu extends SherlockActivity implements OnClickListener{

	Button addNewUser, contactList, addGroup;

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
			setContentView(R.layout.addguestmenu);
			ActionBar bar = getSupportActionBar();
			bar.show();
			bar.setTitle("Add Guest By");
			addNewUser = (Button) findViewById(R.id.bAddGuestMenuAddNewUser);
			addNewUser.setOnClickListener(this);
			contactList = (Button) findViewById(R.id.bAddGuestMenuContactList);
			contactList.setOnClickListener(this);
			addGroup = (Button) findViewById(R.id.bAddGuestMenuAddGroup);
			addGroup.setOnClickListener(this);
		}
	}


	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		switch (arg0.getId()){
		case R.id.bAddGuestMenuAddNewUser:
			Intent openAddUser = new Intent("com.MyInvite.Application.ADDUSER");
			startActivity(openAddUser);
			finish();
			break;
		case R.id.bAddGuestMenuContactList:
			Intent openAddContact = new Intent("com.MyInvite.Application.ADDCONTACT");
			startActivity(openAddContact);
			finish();
			break;
		case R.id.bAddGuestMenuAddGroup:
			Intent openAddGroup = new Intent("com.MyInvite.Application.ADDGROUP");
			startActivity(openAddGroup);
			finish();
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
