package com.Zap.InstantConnection.Messages;
import com.Zap.InstantConnection.*;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.TimeZone;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockListActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class Messages extends SherlockListActivity implements OnClickListener {
	
	public ArrayList<Message> messages = new ArrayList<Message>();
	private MessagesAdapter adapter;
	int eventid;
	String response;
	TextView message;
	ProgressDialog progDailog;
	EditText messageBox;
	Button send;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public void onResume() {
		super.onResume();
		if(!AppUtils.haveNetworkConnection(this)) {
			AppUtils.showConnectionFailed(this);
		} else {
		setContentView(R.layout.messages);
		ActionBar bar = getSupportActionBar();
		bar.show();
		bar.setTitle("Messages");
		progDailog = new ProgressDialog(this);
        progDailog.setMessage("Loading...");
        progDailog.setIndeterminate(false);
        progDailog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progDailog.setCancelable(false);
        progDailog.show();
		AsyncTask<String, Void, String> task = new AsyncTask<String, Void, String>() {
			@Override
			protected String doInBackground(String... arg0) {
				Bundle extras = getIntent().getExtras();
				eventid = extras.getInt("eventid");			
				HashMap<String, String> args = new HashMap<String, String>();
				args.put("eventid", eventid + "");
				response = DatabaseAccess.getData(AppUrls.ViewMessages, args);
				return null;
			}

			@Override
			protected void onPostExecute(String result) {
				try {
					JSONArray jArray = new JSONArray(response);
					int length = jArray.length();
					messages = new ArrayList<Message>(length);
					String name, message, time;
					for (int i = 0; i < length; i++){
		                JSONObject json_data = jArray.getJSONObject(i);
		                name = json_data.getString("user");
		                message = json_data.getString("message");
		                String dateStr = json_data.getString("time");
		                dateStr = dateStr + "-0000";
		                time = "";
		                try {
		                	Configuration userConfig = new Configuration();
		        			Settings.System.getConfiguration( getContentResolver() , userConfig );
		        			Calendar calendar = Calendar.getInstance( userConfig.locale );
		        			TimeZone tz = calendar.getTimeZone();
		        			
		                    SimpleDateFormat sdf =  new SimpleDateFormat("yyyy-MM-dd HH:mm:ssZ");
		                    sdf.setTimeZone(tz);
		                    Date date = sdf.parse(dateStr);

		                    sdf = new SimpleDateFormat("K:mm a  yyyy-MM-dd");
		                    time = sdf.format(date);

		                    System.out.println(time);
		                } catch (Exception e) {
		                    // TODO Auto-generated catch block
		                    e.printStackTrace();
		                }
		                messages.add(new Message(name, message, time));
			        }
				} catch (JSONException e) {
					setListAdapter(null);
					e.printStackTrace();
				} catch (NullPointerException e) {
					setListAdapter(null);
					e.printStackTrace();
				}
				adapter = new MessagesAdapter(Messages.this, R.layout.message, messages);
				setListAdapter(adapter);
				message = (TextView) findViewById(R.id.tvMessages);
				if(messages.size() == 0){
					message.setText("No Messages");
				}else{
					message.setVisibility(View.GONE);
				}
				
				messageBox = (EditText)findViewById(R.id.etMessagesMessage);
				send = (Button)findViewById(R.id.bMessagesSend);
				send.setOnClickListener(Messages.this);
				progDailog.dismiss();
			}
		};
		task.execute();
		}
	}
	
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
	}
	
	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		switch (arg0.getId()){
		case R.id.bMessagesSend:
			if(messageBox.getText().length() == 0){
				AppUtils.showAlertDialog(this, "No Message", "Please enter a message.", true);
			}else{
				progDailog = new ProgressDialog(this);
		        progDailog.setMessage("Sending Message...");
		        progDailog.setIndeterminate(false);
		        progDailog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		        progDailog.setCancelable(false);
		        progDailog.show();
				AsyncTask<String, Void, String> task = new AsyncTask<String, Void, String>() {
					@Override
					protected String doInBackground(String... arg0) {
						HashMap<String, String> args = new HashMap<String, String>();
						args.put("message", messageBox.getText().toString());
						args.put("userid", Main.userid + "");
						args.put("eventid", eventid + "");
						DatabaseAccess.getData(AppUrls.CreateMessage, args);
						return null;
					}
	
					@Override
					protected void onPostExecute(String result) {
						messageBox.setText("");
						refresh();
					}
				};
				task.execute();
			}
			break;
		}
	}
	
	public void refresh(){
		
		AsyncTask<String, Void, String> refresh = new AsyncTask<String, Void, String>() {
			@Override
			protected String doInBackground(String... arg0) {	
				HashMap<String, String> args = new HashMap<String, String>();
				args.put("eventid", eventid + "");
				response = DatabaseAccess.getData(AppUrls.ViewMessages, args);
				return null;
			}

			@Override
			protected void onPostExecute(String result) {
				try {
					JSONArray jArray = new JSONArray(response);
					int length = jArray.length();
					messages = new ArrayList<Message>(length);
					String name, message, time;
					for (int i = 0; i < length; i++){
		                JSONObject json_data = jArray.getJSONObject(i);
		                name = json_data.getString("user");
		                message = json_data.getString("message");
		                String dateStr = json_data.getString("time");
		                dateStr = dateStr + "-0000";
		                time = "";
		                try {
		                	Configuration userConfig = new Configuration();
		        			Settings.System.getConfiguration( getContentResolver() , userConfig );
		        			Calendar calendar = Calendar.getInstance( userConfig.locale );
		        			TimeZone tz = calendar.getTimeZone();
		        			
		                    SimpleDateFormat sdf =  new SimpleDateFormat("yyyy-MM-dd HH:mm:ssZ");
		                    sdf.setTimeZone(tz);
		                    Date date = sdf.parse(dateStr);

		                    sdf = new SimpleDateFormat("K:mm a  yyyy-MM-dd");
		                    time = sdf.format(date);
		                } catch (Exception e) {
		                    // TODO Auto-generated catch block
		                    e.printStackTrace();
		                }
		                messages.add(new Message(name, message, time));
			        }
				} catch (JSONException e) {
					setListAdapter(null);
					e.printStackTrace();
				} catch (NullPointerException e) {
					setListAdapter(null);
					e.printStackTrace();
				}
				adapter = new MessagesAdapter(Messages.this, R.layout.message, messages);
				setListAdapter(adapter);
				
				if(messages.size() == 0){
					message.setText("No Messages");
				}else{
					message.setVisibility(View.GONE);
				}
				progDailog.dismiss();
			}
		};
		refresh.execute();
	}

}
