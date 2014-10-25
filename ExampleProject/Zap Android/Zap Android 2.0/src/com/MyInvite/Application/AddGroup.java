package com.MyInvite.Application;

import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockListActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class AddGroup extends SherlockListActivity implements OnClickListener {

	int positionCheck[] = new int[0];
	String groups[] = new String[0];
	int groupids[];
	Button submit;
	TextView message;
	String[] members;
	String[] usernames;
	int[] ids;
	ProgressDialog progDailog;
	String response;
	DataAdapter adapter;
	int size;
	HashMap<String, String> args;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	}
	
	@Override
	protected void onResume() {
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
			setContentView(R.layout.addgroup);
			ActionBar bar = getSupportActionBar();
			bar.show();
			bar.setTitle("Add Guests");
			progDailog = new ProgressDialog(AddGroup.this);
            progDailog.setMessage("Loading...");
            progDailog.setIndeterminate(false);
            progDailog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progDailog.setCancelable(false);
            progDailog.show();
			AsyncTask<String, Void, String> task = new AsyncTask<String, Void, String>() {
				@Override
				protected String doInBackground(String... arg0) {
					HashMap<String, String> args = new HashMap<String, String>();
					args.put("userid", Main.userid+"");
					response = DatabaseAccess.getData(Main.serverName + "GetGroups.php", args);
					return null;
				}

				@Override
				protected void onPostExecute(String result) {
					groups = new String[0];
					try {
						JSONArray jArray = new JSONArray(response);
						int length = jArray.length();
						groups = new String[length];
						groupids = new int[length];
						positionCheck = new int[length];
						for (int i = 0; i < length; i++){
			                JSONObject json_data = jArray.getJSONObject(i);
			                groups[i] = json_data.getString("groupname");
			                groupids[i] = json_data.getInt("id");
			                positionCheck[i] = 0;
				        }
						adapter = new DataAdapter(getApplicationContext(), android.R.layout.simple_list_item_checked, groups);
						adapter.setToggleList(positionCheck);
						setListAdapter(adapter);
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					submit = (Button) findViewById(R.id.bAddGroupSubmit);
					submit.setOnClickListener(AddGroup.this);
					message = (TextView) findViewById(R.id.tvAddGroupMessage);
					if (groups.length == 0) {
						message.setText("No Groups.\nGo to \"Manage Group\" in main menu to manage groups.");
					}else{
						message.setText("Select groups to add to your event.");
					}
					progDailog.dismiss();
				}
			};
			task.execute();
			
		}
	}
	
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		// TODO Auto-generated method stub
		super.onListItemClick(l, v, position, id);
		CheckedTextView textView = (CheckedTextView) v;
		textView.setChecked(!textView.isChecked());
		if (textView.isChecked()){
			positionCheck[position] = 1;
		}else{
			positionCheck[position] = 0;
		}
		adapter.setToggleList(positionCheck);
	}

	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		switch (arg0.getId()) {
		case R.id.bAddGroupSubmit:
			try{
				for (int i = 0; i < positionCheck.length; i++) {
					if (positionCheck[i] == 1) {
						args = new HashMap<String, String>();
						args.put("groupid", groupids[i]+"");
						AsyncTask<Integer, Void, String> task = new AsyncTask<Integer, Void, String>() {
							@Override
							protected String doInBackground(Integer... arg0) {
								response = DatabaseAccess.getData(Main.serverName + "GetGroupInfo.php", args);
								return null;
							}
							@Override
							protected void onPostExecute(String result) {
								try {
									JSONArray jArray = new JSONArray(response);
									int length = jArray.length();
									members = new String[length];
									usernames = new String[length];
									ids = new int[length];
									for (int j = 0; j < length; j++) {
						                JSONObject json_data = jArray.getJSONObject(j);
						                members[j] = json_data.getString("name");
						                usernames[j] = json_data.getString("username");
						                ids[j] = json_data.getInt("id");
							        }
									
									for (int k = 0; k < usernames.length; k++) {
										AsyncTask<Integer, Void, String> innerTask = new AsyncTask<Integer, Void, String>() {
											@Override
											protected String doInBackground(Integer... arg0) {
												HashMap<String, String> addUser = new HashMap<String, String>();
												addUser.put("eventid", Main.eventid + "");
												addUser.put("userid", ids[arg0[0]]+"");
												DatabaseAccess.getData(Main.serverName + "InviteUserWithIdChangeRequest.php", addUser);
												return null;
											}
										};
										innerTask.execute(k);
									}
								} catch (JSONException e) {
									e.printStackTrace();
								}
							}
						};
						task.execute();	
					}
				}
				Toast.makeText(AddGroup.this, "- Guests Added -", Toast.LENGTH_SHORT).show();
			}catch(NullPointerException e){
				e.printStackTrace();
			}finally{
//				Intent openViewGuests = new Intent("com.MyInvite.Application.VIEWGUESTS");
//    			openViewGuests.putExtra("eventid", Main.eventid);
//    			startActivity(openViewGuests);
				finish();
			}
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
