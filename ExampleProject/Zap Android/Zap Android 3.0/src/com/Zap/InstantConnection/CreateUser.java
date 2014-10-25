package com.Zap.InstantConnection;

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
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class CreateUser extends SherlockActivity implements OnClickListener {

	Button createUser, cancel;
	EditText username, password, confirmPassword, firstName, lastName, emailAddress, phoneNumber;
	String fullName, response;
	HashMap<String, String> args;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		if(!AppUtils.haveNetworkConnection(this)) {
			AppUtils.showConnectionFailed(this);
		}else{	
			setContentView(R.layout.createuser);
			ActionBar bar = getSupportActionBar();
			bar.show();
			bar.setTitle("Create User");
			createUser = (Button) findViewById(R.id.bCreateUserSubmit);
			createUser.setOnClickListener(this);
			cancel = (Button) findViewById(R.id.bCreateUserCancel);
			cancel.setOnClickListener(this);
	
			username = (EditText) findViewById(R.id.etCreateUsername);
			password = (EditText) findViewById(R.id.etCreatePassword);
			confirmPassword = (EditText) findViewById(R.id.etCreateUserConfirmPassword);
			firstName = (EditText) findViewById(R.id.etCreateUserFirstName);
			lastName = (EditText) findViewById(R.id.etCreateUserLastName);
			emailAddress = (EditText) findViewById(R.id.etCreateUserEmailAddress);
			phoneNumber = (EditText) findViewById(R.id.etCreateUserPhoneNumber);
			
			getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
		}
	}

	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		switch (arg0.getId()) {
		case R.id.bCreateUserSubmit:
			if (firstName.length() <= 0) {
				AppUtils.showAlertDialog(this, AppConstants.invalid_first_name_title, AppConstants.invalid_first_name, true);
			} else if (lastName.length() <= 0) {
				AppUtils.showAlertDialog(this, AppConstants.invalid_last_name_title, AppConstants.invalid_last_name, true);
			} else if (username.length() < 3) {
				AppUtils.showAlertDialog(this, AppConstants.invalid_username_title, AppConstants.invalid_username, true);
			} else if (password.length() < 5) {
				final AlertDialog.Builder builder = new AlertDialog.Builder(CreateUser.this);
				builder.setTitle(AppConstants.invalid_password_title);
				builder.setMessage(AppConstants.invalid_password_length);
				builder.setCancelable(false);
				builder.setNegativeButton("Ok",	new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						password.setText("");
						confirmPassword.setText("");
					}
				});
				builder.show();
			} else if (!password.getText().toString().equals(confirmPassword.getText().toString())) {
				final AlertDialog.Builder builder = new AlertDialog.Builder(CreateUser.this);
				builder.setTitle(AppConstants.invalid_password_title);
				builder.setMessage(AppConstants.invalid_password_mismatch);
				builder.setCancelable(false);
				builder.setNegativeButton("Ok",	new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						password.setText("");
						confirmPassword.setText("");
					}
				});
				builder.show();
			} else if (phoneNumber.length() < 8 || phoneNumber.length() > 11) {
				AppUtils.showAlertDialog(this, AppConstants.invalid_phone_number_title, AppConstants.invalid_phone_number, true);
			} else {
				args = new HashMap<String, String>();
				args.put("username", username.getText().toString());
				fullName = firstName.getText().toString() + " " + lastName.getText().toString();
				args.put("name", fullName);
				args.put("password", password.getText().toString());
				args.put("salt", DatabaseAccess.reverseString(username.getText().toString()));
				args.put("email", emailAddress.getText().toString());
				args.put("phone", phoneNumber.getText().toString());
				AsyncTask<String, Void, String> task = new AsyncTask<String, Void, String>() {
					@Override
					protected String doInBackground(String... arg0) {
						
						response = DatabaseAccess.getData(AppUrls.CreateUser, args);
						//System.out.println(response);
						return null;
					}

					@Override
					protected void onPostExecute(String result) {
						System.out.println(response);
						if (response.charAt(0) == '0') {
							AppUtils.showAlertDialog(CreateUser.this, AppConstants.create_user_failed_title, AppConstants.create_user_failed_message, true);
						} else {
							AppUtils.showAlertDialog(CreateUser.this, AppConstants.create_user_success_title, AppConstants.create_user_success_message, false);
						}
					}
				};
				task.execute();
			}
			break;

		case R.id.bCreateUserCancel:
			finish();
			break;
		}
	}
	
	
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		AppUtils.showYesNoAlertDialog(this, "Sign Up Incomplete", "Are you sure you want to go back?");
	}

}
