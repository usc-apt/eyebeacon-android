package com.MyInvite.Application;

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
import android.widget.TextView;

public class Messages extends SherlockListActivity {
	
	public ArrayList<Message> messages = new ArrayList<Message>();
	private MessagesAdapter adapter;
	int eventid;
	String response;
	TextView message;
	ProgressDialog progDailog;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public void onResume() {
		super.onResume();
		System.out.println("messages started");
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
				response = DatabaseAccess.getData(Main.serverName + "ViewChat.php", args);
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
		                //System.out.println(dateStr);
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
				progDailog.dismiss();
			}
		};
		task.execute();
	}
	
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    MenuInflater inflater = getSupportMenuInflater();
	    inflater.inflate(R.menu.messagesitems, menu);
	    return true;
	}
	
	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
        	case R.id.iMessagesAdd:
        		Intent openNewMessage = new Intent("com.MyInvite.Application.MESSAGECREATE");
        		openNewMessage.putExtra("eventid", eventid);
    			startActivity(openNewMessage);
        		return true;
    
        	default:
                return super.onOptionsItemSelected(item);
        }
	}

}
