package com.Zap.InstantConnection;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.actionbarsherlock.app.SherlockActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.Signature;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class Login extends SherlockActivity implements OnClickListener {

	Button login, createUser, forgotPassword, loginWithFacebook;
	EditText usernameET, passwordET;
	public static String fullName, username, email, password; 
	String response;
	ProgressDialog progDailog;
	SharedPreferences prefs;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		// Add code to print out the key hash
	    try {
	        PackageInfo info = getPackageManager().getPackageInfo(
	                "com.facebook.samples.hellofacebook", 
	                PackageManager.GET_SIGNATURES);
	        for (Signature signature : info.signatures) {
	            MessageDigest md = MessageDigest.getInstance("SHA");
	            md.update(signature.toByteArray());
	            System.out.println("KeyHash: "+ Base64.encodeToString(md.digest(), Base64.DEFAULT));
	            }
	    } catch (NameNotFoundException e) {

	    } catch (NoSuchAlgorithmException e) {

	    }
	    prefs = this.getSharedPreferences("com.Zap.InstantConnection", Context.MODE_PRIVATE);
	    
		if(!AppUtils.haveNetworkConnection(this)) {
			AppUtils.showConnectionFailed(this);
		}else{	
			setContentView(R.layout.login);
			getSupportActionBar().show();
			
			LocalDatabase entry = new LocalDatabase(Login.this);
			entry.open();
			if (entry.ifExists()){
				Main.userid = entry.getData();
				prefs.edit().putInt("userid", Main.userid).commit();
				Intent openMain = new Intent("com.Zap.InstantConnection.MAIN");
				startActivity(openMain);
				finish();
			}
			entry.close();
			
			getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
			login = (Button) findViewById(R.id.bLoginLogin);
			createUser = (Button) findViewById(R.id.bLoginCreateUser);
			forgotPassword = (Button) findViewById(R.id.bLoginForgotPassword);
			loginWithFacebook = (Button) findViewById(R.id.bLoginFacebook);
			login.setOnClickListener(this);
			createUser.setOnClickListener(this);
			forgotPassword.setOnClickListener(this);
			loginWithFacebook.setOnClickListener(this);
			
			usernameET = (EditText) findViewById(R.id.etLoginUsername);
			passwordET = (EditText) findViewById(R.id.etLoginPassword);
		}
	}
	
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		switch(arg0.getId()){
		case R.id.bLoginLogin:
			progDailog = new ProgressDialog(this);
            progDailog.setMessage("Loggin In...");
            progDailog.setIndeterminate(false);
            progDailog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progDailog.setCancelable(false);
            progDailog.show();
			AsyncTask<String, Void, String> task = new AsyncTask<String, Void, String>() {
				@Override
				protected String doInBackground(String... arg0) {
					HashMap<String, String> args = new HashMap<String, String>();
					args.put("username", usernameET.getText().toString());
					args.put("password", passwordET.getText().toString());
					args.put("salt", DatabaseAccess.reverseString(usernameET.getText().toString()));
					response = DatabaseAccess.getData(AppUrls.Login, args);
					return null;
				}

				@Override
				protected void onPostExecute(String result) {
					if(response.charAt(0) != '0' && passwordET.getText().length() != 0 && usernameET.getText().length() != 0) {
						JSONArray jArray;
						try {
							jArray = new JSONArray(response);
							JSONObject data = jArray.getJSONObject(0);
							Main.userid = data.getInt("id");
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						
						String user = usernameET.getText().toString();
						String pass = passwordET.getText().toString();
						
						LocalDatabase entry = new LocalDatabase(Login.this);
						entry.open();
						entry.createEntry(user, pass, Main.userid);
						entry.close();
						
						progDailog.dismiss();
						
						Intent openMain = new Intent("com.Zap.InstantConnection.MAIN");
						startActivity(openMain);
						finish();
					}else {
						progDailog.dismiss();
						final AlertDialog.Builder builder = new AlertDialog.Builder(Login.this);
						builder.setTitle(AppConstants.login_failed_title);
						builder.setMessage(AppConstants.login_failed_message);
						builder.setCancelable(false);
						builder.setNegativeButton("Ok", new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int which) {
								passwordET.setText("");
							}
						});
						builder.show();
					}
				}
			};
			task.execute();
			break;
		
		case R.id.bLoginCreateUser:
			Intent openCreateUser = new Intent("com.Zap.InstantConnection.CREATEUSER");
			startActivity(openCreateUser);
			break;
			
		case R.id.bLoginForgotPassword:
			Intent openResetPassword = new Intent("com.Zap.InstantConnection.RESETPASSWORD");
			startActivity(openResetPassword);
			break;
			
		case R.id.bLoginFacebook:
			//Intent intent = new Intent(this, FacebookLoginFragment.class);
            //startActivity(intent);
			break;

		}
	}

}
