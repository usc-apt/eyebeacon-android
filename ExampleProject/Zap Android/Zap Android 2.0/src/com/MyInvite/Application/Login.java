package com.MyInvite.Application;

import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.actionbarsherlock.app.SherlockActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class Login extends SherlockActivity implements OnClickListener {

	Button login, createUser, forgotPassword;
	EditText username, password;
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
			setContentView(R.layout.login);
			getSupportActionBar().show();
			
			LocalDatabase entry = new LocalDatabase(Login.this);
			entry.open();
			if (entry.ifExists()){
				Main.userid = entry.getData();
				Intent openMain = new Intent("com.MyInvite.Application.MAIN");
				startActivity(openMain);
				finish();
			}
			entry.close();
			
			login = (Button) findViewById(R.id.bLoginLogin);
			createUser = (Button) findViewById(R.id.bLoginCreateUser);
			forgotPassword = (Button) findViewById(R.id.bLoginForgotPassword);
			login.setOnClickListener(this);
			createUser.setOnClickListener(this);
			forgotPassword.setOnClickListener(this);
			
			username = (EditText) findViewById(R.id.etLoginUsername);
			password = (EditText) findViewById(R.id.etLoginPassword);
		}
	}
	
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		switch(arg0.getId()){
		case R.id.bLoginLogin:
			AsyncTask<String, Void, String> task = new AsyncTask<String, Void, String>() {
				@Override
				protected String doInBackground(String... arg0) {
					HashMap<String, String> args = new HashMap<String, String>();
					args.put("username", username.getText().toString());
					args.put("password", password.getText().toString());
					args.put("salt", DatabaseAccess.reverseString(username.getText().toString()));
					response = DatabaseAccess.getData(Main.serverName + "LoginSecure.php", args);
					return null;
				}

				@Override
				protected void onPostExecute(String result) {
					if(response.equals("-1")) {
						final AlertDialog.Builder builder = new AlertDialog.Builder(Login.this);
						builder.setTitle("Connection failed");
						builder.setMessage("Make sure you are connected to the internet.");
						builder.setNegativeButton("Ok", new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int which) {
								builder.setCancelable(true);
								finish();
							}
						});
						builder.show();
					} else if(response.charAt(0) != '0' && password.getText().length() != 0 && username.getText().length() != 0) {
						JSONArray jArray;
						try {
							jArray = new JSONArray(response);
							JSONObject data = jArray.getJSONObject(0);
							Main.userid = data.getInt("id");
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						
						String user = username.getText().toString();
						String pass = password.getText().toString();
						
						LocalDatabase entry = new LocalDatabase(Login.this);
						entry.open();
						entry.createEntry(user, pass, Main.userid);
						entry.close();
						
						Intent openMain = new Intent("com.MyInvite.Application.MAIN");
						startActivity(openMain);
						finish();
					}else {
						final AlertDialog.Builder builder = new AlertDialog.Builder(Login.this);
						builder.setTitle("Failed Login");
						builder.setMessage("Incorrect username or password");
						builder.setNegativeButton("Ok", new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog, int which) {
										// TODO Auto-generated method stub
										builder.setCancelable(true);
										//username.setText("");
										password.setText("");
									}
								});
						builder.show();
					}
				}
			};
			task.execute();
			break;
		
		case R.id.bLoginCreateUser:
			Intent openCreateUser = new Intent("com.MyInvite.Application.CREATEUSER");
			startActivity(openCreateUser);
			break;
			
		case R.id.bLoginForgotPassword:
			Intent openResetPassword = new Intent("com.MyInvite.Application.RESETPASSWORD");
			startActivity(openResetPassword);
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

}
