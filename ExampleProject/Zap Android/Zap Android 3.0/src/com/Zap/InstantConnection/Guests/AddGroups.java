package com.Zap.InstantConnection.Guests;
import com.Zap.InstantConnection.*;

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

public class AddGroups extends SherlockListActivity implements OnClickListener {

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
		
		if(!AppUtils.haveNetworkConnection(this)) {
			AppUtils.showConnectionFailed(this);
		}else{	
			setContentView(R.layout.addguestbygroup);
			ActionBar bar = getSupportActionBar();
			bar.show();
			bar.setTitle("Add Guests");
			progDailog = new ProgressDialog(AddGroups.this);
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
					response = DatabaseAccess.getData(AppUrls.GetGroups, args);
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
					submit.setOnClickListener(AddGroups.this);
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
			if(!AppUtils.checkSomethingSelected(positionCheck)){
				AppUtils.showAlertDialog(this, AppConstants.no_group_selected_title, AppConstants.no_group_selected, true);
			}else{	
				progDailog = new ProgressDialog(AddGroups.this);
	            progDailog.setMessage("Adding Guests...");
	            progDailog.setIndeterminate(false);
	            progDailog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
	            progDailog.setCancelable(false);
	            progDailog.show();
				AsyncTask<Integer, Void, String> task = new AsyncTask<Integer, Void, String>() {
					@Override
					protected String doInBackground(Integer... arg0) {
						for (int i = 0; i < positionCheck.length; i++) {
							if (positionCheck[i] == 1) {
								args = new HashMap<String, String>();
								args.put("groupid", groupids[i]+"");
								args.put("eventid", Main.eventid + "");
								response = DatabaseAccess.getData(AppUrls.InviteGroups, args);
								System.out.println("invite group response: "+ response);
//								try {
//									JSONArray jArray = new JSONArray(response);
//									for (int j = 0; j < jArray.length(); j++) {
//						                JSONObject json_data = jArray.getJSONObject(j);
//						                HashMap<String, String> addUser = new HashMap<String, String>();
//										addUser.put("eventid", Main.eventid + "");
//										addUser.put("userid", json_data.getInt("id")+"");
//										DatabaseAccess.getData(AppUrls.InviteGuests, addUser);
//							        }
//								} catch (JSONException e) {
//									e.printStackTrace();
//								}
							}
						}
						return null;
					}
					@Override
					protected void onPostExecute(String result) {
						AppUtils.showToast(AddGroups.this, "- Guests Added -");
						progDailog.dismiss();
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
		if(AppUtils.checkSomethingSelected(positionCheck)){
			AppUtils.showYesNoAlertDialog(this, AppConstants.group_selected_title, AppConstants.group_selected);
		}else{
			super.onBackPressed();
		}
	}

}
