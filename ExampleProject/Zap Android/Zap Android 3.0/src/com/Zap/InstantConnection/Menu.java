package com.Zap.InstantConnection;

import java.util.HashMap;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;

import com.actionbarsherlock.app.SherlockFragment;

public class Menu extends SherlockFragment implements OnClickListener {
	
	Button groups, contacts, aboutus, logout;
	String response;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		View v = inflater.inflate(R.layout.menu, container, false);
		
		LinearLayout mLinearLayout = (LinearLayout)v;
		groups = (Button)v.findViewById(R.id.bMenuGroups);
		contacts = (Button)v.findViewById(R.id.bMenuContactList);
		aboutus = (Button)v.findViewById(R.id.bMenuAboutUs);
		logout = (Button)v.findViewById(R.id.bMenuLogout);
		groups.setOnClickListener(this);
		contacts.setOnClickListener(this);
		aboutus.setOnClickListener(this);
		logout.setOnClickListener(this);
		return v;
	}

	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		switch (arg0.getId()){
		case R.id.bMenuGroups:
			Intent openMyGroups = new Intent("com.Zap.InstantConnection.Groups.MYGROUPS");
			startActivity(openMyGroups);
	        break;
		case R.id.bMenuContactList:
			Intent openContactList = new Intent("com.Zap.InstantConnection.Contacts.CONTACTLIST");
			startActivity(openContactList);
	        break;
		case R.id.bMenuAboutUs:
			Intent openAboutUs = new Intent("com.Zap.InstantConnection.ABOUTUS");
			startActivity(openAboutUs);
	        break;
		case R.id.bMenuLogout:
			LocalDatabase db = new LocalDatabase(getActivity());
			db.open();
			db.deleteEntry();
			db.close();
			AsyncTask<String, Void, String> logout = new AsyncTask<String, Void, String>() {
				@Override
				protected String doInBackground(String... arg0) {
					HashMap<String, String> args = new HashMap<String, String>();
	    			args.put("userid", Main.userid + "");
	    			response = DatabaseAccess.getData(AppUrls.Logout, args);
	    			response = DatabaseAccess.getData(AppUrls.RegisterGCMId, args);
					return null;
				}
			};
			logout.execute();
			SharedPreferences prefs = getActivity().getSharedPreferences("com.Zap.InstantConnection", Context.MODE_PRIVATE);
			prefs.edit().remove("userid");
			Intent openLogin = new Intent(getActivity(), Login.class);
			getActivity().finish();
			startActivity(openLogin);
	        break;
		}
	}

}
