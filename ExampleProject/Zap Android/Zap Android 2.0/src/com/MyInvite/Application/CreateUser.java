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
				final AlertDialog.Builder builder = new AlertDialog.Builder(CreateUser.this);
				builder.setTitle("Invalid First Name");
				builder.setMessage("You did not enter a  first name");
				builder.setNegativeButton("Ok",	new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog,	int which) {
						// TODO Auto-generated method stub
						builder.setCancelable(true);
						//firstName.setText("");
						//username.setText("");
						//password.setText("");
						//emailAddress.setText("");
					}
				});
				builder.show();
			} else if (lastName.length() <= 0) {
				final AlertDialog.Builder builder = new AlertDialog.Builder(CreateUser.this);
				builder.setTitle("Invalid Last Name");
				builder.setMessage("You did not enter a last name");
				builder.setNegativeButton("Ok",	new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog,	int which) {
						// TODO Auto-generated method stub
						builder.setCancelable(true);
						//lastName.setText("");
						//username.setText("");
						//password.setText("");
					}
				});
				builder.show();
			} else if (username.length() < 3) {
				final AlertDialog.Builder builder = new AlertDialog.Builder(CreateUser.this);
				builder.setTitle("Invalid Username");
				builder.setMessage("Username must be longer than 3 characters");
				builder.setNegativeButton("Ok",	new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						builder.setCancelable(true);
						//username.setText("");
						//password.setText("");
					}
				});
				builder.show();
			} else if (password.length() < 5) {
				final AlertDialog.Builder builder = new AlertDialog.Builder(CreateUser.this);
				builder.setTitle("Invalid Password");
				builder.setMessage("Password must be longer than 5 characters");
				builder.setNegativeButton("Ok",	new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						builder.setCancelable(true);
						password.setText("");
						confirmPassword.setText("");
					}
				});
				builder.show();
			} else if (!password.getText().toString().equals(confirmPassword.getText().toString())) {
				final AlertDialog.Builder builder = new AlertDialog.Builder(CreateUser.this);
				builder.setTitle("Invalid Password");
				builder.setMessage("Passwords don't match");
				builder.setNegativeButton("Ok",	new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						builder.setCancelable(true);
						password.setText("");
						confirmPassword.setText("");
					}
				});
				builder.show();
			} else if (phoneNumber.length() < 7 || phoneNumber.length() > 11) {
				final AlertDialog.Builder builder = new AlertDialog.Builder(CreateUser.this);
				builder.setTitle("Invalid Phone Number");
				builder.setMessage("Please enter a valid phone number.");
				builder.setNegativeButton("Ok",	new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						builder.setCancelable(true);
						//phoneNumber.setText("");
					}
				});
			} else {
				args = new HashMap<String, String>();
				args.put("username", username.getText().toString());
				fullName = firstName.getText().toString() + " " + lastName.getText().toString();
				args.put("name", fullName);
				args.put("password", password.getText().toString());
				args.put("salt", DatabaseAccess.reverseString(username.getText().toString()));
				args.put("email", emailAddress.getText().toString());
				args.put("phone", phoneNumber.getText().toString());
				System.out.println(username.getText().toString());
				System.out.println(fullName);
				System.out.println(emailAddress.getText().toString());
				System.out.println(phoneNumber.getText().toString());
				AsyncTask<String, Void, String> task = new AsyncTask<String, Void, String>() {
					@Override
					protected String doInBackground(String... arg0) {
						
						response = DatabaseAccess.getData(Main.serverName + "CreateUserWithConfirmation.php", args);
						//System.out.println(response);
						return null;
					}

					@Override
					protected void onPostExecute(String result) {
						System.out.println(response);
						if (response.charAt(0) == '0') {
							final AlertDialog.Builder builder = new AlertDialog.Builder(CreateUser.this);
							builder.setTitle("Create User Failed");
							builder.setMessage("Username has already been taken or email has been used.");
							builder.setNegativeButton("Ok",	new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog, int which) {
									// TODO Auto-generated method stub
									builder.setCancelable(true);
									//username.setText("");
									//password.setText("");
								}
							});
							builder.show();
						} else {
							final AlertDialog.Builder builder = new AlertDialog.Builder(CreateUser.this);
							builder.setTitle("Create User Success");
							builder.setMessage("Please check your text messages and click the link to verify your account.");
							builder.setNegativeButton("Ok",	new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog, int which) {
									// TODO Auto-generated method stub
									builder.setCancelable(true);
									finish();
								}
							});
							builder.show();
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
	
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		final AlertDialog.Builder builder = new AlertDialog.Builder(CreateUser.this);
		builder.setTitle("Sign Up Incomplete");
		builder.setMessage("Are you sure you want to go back?");
		builder.setCancelable(false);
		builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
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
