package com.MyInvite.Application;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockListActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.ListView;
import android.widget.TextView;

public class ClickedGroup extends SherlockListActivity implements OnClickListener {

	String members[] = new String[0];
	String phoneNumbers[];
	public static int ids[];
	int positionCheck[] = new int[0];
	public static int groupid;
	String response;
	Button remove;
	TextView groupName;
	int size;
	DataAdapter adapter;
	ProgressDialog progDailog;

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
			setContentView(R.layout.clickedgroup);
			ActionBar bar = getSupportActionBar();
			bar.show();
			bar.setTitle("Edit Group");
			progDailog = new ProgressDialog(this);
            progDailog.setMessage("Loading...");
            progDailog.setIndeterminate(false);
            progDailog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progDailog.setCancelable(false);
            progDailog.show();
            
			remove = (Button) findViewById(R.id.bClickedGroupRemove);
			remove.setOnClickListener(this);
			groupName = (TextView) findViewById(R.id.tvClickedGroupName);
			
			Bundle extras = getIntent().getExtras();
			groupid = extras.getInt("groupid");
			
			AsyncTask<String, Void, String> task = new AsyncTask<String, Void, String>() {
				@Override
				protected String doInBackground(String... arg0) {
					HashMap<String, String> args = new HashMap<String, String>();
					args.put("groupid", groupid+"");
					response = DatabaseAccess.getData(Main.serverName + "GetGroupInfo.php", args);
					//System.out.println("getClickedGroup response:" + response);
					return null;
				}

				@Override
				protected void onPostExecute(String result) {
					try {
						JSONArray jArray = new JSONArray(response);
						int length = jArray.length();
						members = new String[length];
						ids = new int[length];
						positionCheck = new int[length];
						for (int i = 0; i < length; i++) {
			                JSONObject json_data = jArray.getJSONObject(i);
			                int virtual = json_data.getInt("virtual");
			                String phoneNo;
			                if(virtual == 0) {
			                	members[i] = json_data.getString("name");
			                	ids[i] = json_data.getInt("id");
			                	phoneNo = json_data.getString("phone_number");
			                	if(i==0) groupName.setText(json_data.getString("groupname"));
			                } else if(virtual == 1) {
			                	phoneNo = json_data.getString("phone_number");
			                	members[i] = Main.numbers.get(phoneNo);
			                	ids[i] = json_data.getInt("id");
			                	if(i==0) groupName.setText(json_data.getString("groupname"));
			                }
			                positionCheck[i] = 0;
				        }
					} catch (JSONException e) {
						e.printStackTrace();
					}
					adapter = new DataAdapter(getApplicationContext(), android.R.layout.simple_list_item_checked, members);
					adapter.setToggleList(positionCheck);
					setListAdapter(adapter);
					progDailog.dismiss();
				}
			};
			task.execute();
		}
	}
	
	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    MenuInflater inflater = getSupportMenuInflater();
	    inflater.inflate(R.menu.clickedgroupitems, menu);
	    return true;
	}
	
	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
            switch(item.getItemId()) {
            	case R.id.iClickedGroupAdd:
            		Intent openContactList = new Intent("com.MyInvite.Application.ADDTOGROUP");
        			startActivity(openContactList);
            		return true;
            	case R.id.iClickedGroupDelete:
            		final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        			builder.setMessage("Are you sure you want to delete \"" + groupName.getText().toString() + "\"?");
        			builder.setCancelable(false);
        			builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
        				public void onClick(DialogInterface dialog, int id) {
        					AsyncTask<String, Void, String> task = new AsyncTask<String, Void, String>() {
        						@Override
        						protected String doInBackground(String... arg0) {
        							HashMap<String, String> args = new HashMap<String, String>();
                					args.put("groupid", groupid+"");
                					DatabaseAccess.getData(Main.serverName + "DeleteGroup.php", args);
                					finish();
        							return null;
        						}
        					};
        					task.execute();
        				}
        			});
        			builder.setNegativeButton("No",	new DialogInterface.OnClickListener() {
        				public void onClick(DialogInterface dialog, int id) {
        					dialog.cancel();
        				}
        			});
        			builder.show();
            		return true;
            	default:
                    return super.onOptionsItemSelected(item);
            }
    }

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		// TODO Auto-generated method stub
		super.onListItemClick(l, v, position, id);
		CheckedTextView textView = (CheckedTextView) v;
		textView.setChecked(!textView.isChecked());
		if (textView.isChecked()) {
			positionCheck[position] = 1;
		} else {
			positionCheck[position] = 0;
		}
		adapter.setToggleList(positionCheck);
	}

	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		switch (arg0.getId()) {
		case R.id.bClickedGroupRemove:
			if (!somethingClicked()) {
				final AlertDialog.Builder builder = new AlertDialog.Builder(this);
				builder.setTitle("No Contact Selected");
				builder.setMessage("Please select a contact to remove from group");
				builder.setNegativeButton("Ok",	new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog,	int which) {
						// TODO Auto-generated method stub
						builder.setCancelable(true);
					}
				});
				builder.show();
			} else {
				AsyncTask<String, Void, String> task = new AsyncTask<String, Void, String>() {
					@Override
					protected String doInBackground(String... arg0) {
						HashMap<String, String> args = new HashMap<String, String>();
						args.put("group_id", groupid+"");
						//args.put("groupname", groupName.getText().toString());
						
						ArrayList<Integer> user_ids_arraylist = new ArrayList<Integer>();
						for(int i = 0; i < ids.length; i++) {
							if(positionCheck[i] == 1) {
								user_ids_arraylist.add(ids[i]);
							}
						}
						JSONArray jsonArray = new JSONArray();
						for(Integer i : user_ids_arraylist){
							jsonArray.put(i+"");
						}
						System.out.println("jsonArray: " + jsonArray.toString());
						args.put("user_ids", jsonArray.toString());
//						String key = "1";
//						String result = "";
//						for(int i = 0; i < ids.length; i++) {
//							if(positionCheck[i] == 1) {
//								result = result + members[i] + "\n";
//								args.put(key, ids[i]+"");
//								key += "1";
//							}
//						}
						//response = DatabaseAccess.sendJSON("http://app.zapconnection.com/zapconnection/Server/"+ "RemoveGroupUsers.php", args);
						response = DatabaseAccess.sendJsonWithJsonArray("http://app.zapconnection.com/zapconnection/Server/RemoveGroupUsers.php", "group_id", groupid+"","user_ids", jsonArray);
						System.out.println("remove user response: " + response);
						finish();
						startActivity(getIntent());
						return null;
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
		super.onBackPressed();
	}
	
	public boolean somethingClicked() {
		for (int i = 0; i < positionCheck.length; i++) {
			if (positionCheck[i] == 1) {
				return true;
			}
		}
		return false;
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