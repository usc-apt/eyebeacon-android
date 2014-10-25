package com.Zap.InstantConnection;

import java.util.HashMap;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockActivity;
import com.facebook.Session;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class CreateUserFacebook extends SherlockActivity implements OnClickListener {

	Button createUser;
	EditText phoneNumber;
	String response;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		if(!AppUtils.haveNetworkConnection(this)) {
			AppUtils.showConnectionFailed(this);
		}else{	
			setContentView(R.layout.createuserfacebook);
			ActionBar bar = getSupportActionBar();
			bar.show();
			bar.setTitle("Create User");
			createUser = (Button) findViewById(R.id.bCreateUserFacebookSubmit);
			createUser.setOnClickListener(this);
			phoneNumber = (EditText) findViewById(R.id.etCreateUserPhoneNumber);
			
			getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
		}
	}

	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		switch (arg0.getId()) {
		case R.id.bCreateUserFacebookSubmit:
			if (phoneNumber.length() < 8 || phoneNumber.length() > 11) {
				AppUtils.showAlertDialog(this, AppConstants.invalid_phone_number_title, AppConstants.invalid_phone_number, true);
			} else {				
				AsyncTask<String, Void, String> task = new AsyncTask<String, Void, String>() {
					@Override
					protected String doInBackground(String... arg0) {
						HashMap<String, String> args = new HashMap<String, String>();
						args.put("name", Login.fullName);
						args.put("username", Login.username);
						args.put("password", Login.password);
						args.put("email", Login.email);
						args.put("phone", phoneNumber.getText().toString());
						response = DatabaseAccess.getData(AppUrls.CreateUser, args);						
						//System.out.println(response);
						return null;
					}

					@Override
					protected void onPostExecute(String result) {
						System.out.println(response);
						if (response.charAt(0) == '0') {
							AppUtils.showAlertDialog(CreateUserFacebook.this, AppConstants.create_user_failed_title, AppConstants.create_user_failed_message, true);
							Session.getActiveSession().closeAndClearTokenInformation();
						} else {
							AppUtils.showAlertDialog(CreateUserFacebook.this, AppConstants.create_user_success_title, AppConstants.create_user_success_message, false);
						}
						finish();
					}
				};
				task.execute();
			}
			break;
		}
	}
	
	
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Sign Up Incomplete");
		builder.setMessage("Are you sure you want to go back?");
		builder.setCancelable(false);
		builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				//logout facebook session
				Session.getActiveSession().closeAndClearTokenInformation();
				finish();
			}
		});
		builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				dialog.cancel();
			}
		});
		builder.show();
	}

}
