package com.MyInvite.Application;

import java.util.ArrayList;
import java.util.Collections;
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
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class AddContact extends SherlockListActivity implements OnClickListener {

	String contacts[] = new String[0];
	String usernames[];
	int ids[];
	int positionCheck[] = new int[0];
	String phoneNumbers[];
	int originalIndex[] = new int[0];
	ProgressDialog progDailog;
	Button submit;
	TextView message;
	EditText search;
	private ArrayList<String> contacts_sort = new ArrayList<String>();
	private ArrayList<Integer> ids_sort = new ArrayList<Integer>();
	private ArrayList<Integer> position_sort = new ArrayList<Integer>();
	int textlength = 0;
	DataAdapter adapter;
	
	String response;
	int tempIndex;
	
	//ArrayList<IDandPhoneNumber> checkedContacts = new ArrayList<IDandPhoneNumber>();
	//IDandPhoneNumber currentContact;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
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
		} else {	
			setContentView(R.layout.addcontact);
			ActionBar bar = getSupportActionBar();
			bar.show();
			bar.setTitle("Add Guests");
			getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
			progDailog = new ProgressDialog(AddContact.this);
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
					response = DatabaseAccess.getData(Main.serverName + "GetContacts.php", args);
					return null;
				}

				@Override
				protected void onPostExecute(String result) {
					try {
						JSONArray jArray = new JSONArray(response);
						int length = jArray.length();
						ArrayList<String> sortedContacts= new ArrayList<String>();
						ArrayList<String> tempContacts = new ArrayList<String>();
						ArrayList<Integer> tempIds = new ArrayList<Integer>();
						ArrayList<String> tempUsernames = new ArrayList<String>();
						ArrayList<String> tempPhoneNumbers = new ArrayList<String>();
						int virtual;
						String phoneNumber;
						for (int i = 0; i < length; i++) {
			                JSONObject json_data = jArray.getJSONObject(i);
			                virtual = json_data.getInt("virtual");
			                if(virtual == 0) {
			                	phoneNumber = json_data.getString("phone_number");
			                	tempIds.add(json_data.getInt("contactid"));
			                	tempContacts.add("*"+json_data.getString("name"));
			                	sortedContacts.add("*"+json_data.getString("name"));
			                	tempUsernames.add(json_data.getString("username"));
			                	tempPhoneNumbers.add(phoneNumber);
			                } else if(virtual == 1) {
			                	phoneNumber = json_data.getString("phone_number");
			                	if(Main.numbers.containsKey(phoneNumber)) {
			                		tempIds.add(json_data.getInt("contactid"));
				                	tempContacts.add(Main.numbers.get(phoneNumber)+"*");
				                	sortedContacts.add(Main.numbers.get(phoneNumber)+"*");
				                	tempUsernames.add("");
				                	tempPhoneNumbers.add(phoneNumber);
			                	}
			                }
				        }
						for(String phoneNum: Main.numbers.keySet()) {
							if(!tempContacts.contains("*"+Main.numbers.get(phoneNum)) && !tempContacts.contains(Main.numbers.get(phoneNum)+"*")) {
		            			tempIds.add(-1);
		            			tempContacts.add(Main.numbers.get(phoneNum)+"*");
		            			sortedContacts.add(Main.numbers.get(phoneNum)+"*");
		            			tempUsernames.add("");
		            			tempPhoneNumbers.add(phoneNum);
							}
						}
						int size = tempContacts.size();
						Collections.sort(sortedContacts);
						positionCheck = new int[size];
						originalIndex = new int[size];
						contacts = new String[size];
						ids = new int[size];
						usernames = new String[size];
						phoneNumbers = new String[size];
						for(int i = 0; i < size; i++) {
							for(int j = 0; j < size; j++) {
								if(tempContacts.get(i).equals(sortedContacts.get(j))) {
									if(tempContacts.get(i).charAt(0) == '*') {
										contacts[j] = tempContacts.get(i).substring(1);
									} else {
										contacts[j] = tempContacts.get(i);
									}
									ids[j] = tempIds.get(i);
									usernames[j] = tempUsernames.get(i);
									phoneNumbers[j] = tempPhoneNumbers.get(i);
									positionCheck[j] = 0;
									originalIndex[j] = 0;
								}
							}
						}
						
					} catch (Exception e) {
						e.printStackTrace();
					}
					adapter = new DataAdapter(AddContact.this, android.R.layout.simple_list_item_checked, contacts);
					adapter.setToggleList(positionCheck);
					setListAdapter(adapter);
					submit = (Button) findViewById(R.id.bAddContactSubmit);
					submit.setOnClickListener(AddContact.this);
					message = (TextView) findViewById(R.id.tvAddContactMessage);
					if (contacts.length == 0) {
						message.setText("No Contacts.\nGo to \"Contact List\" in main menu to manage contacts.");
					}else{
						message.setText("Select contacts to add to your event.");
					}
								
					search = (EditText) findViewById(R.id.etAddContactSearch);
					search.addTextChangedListener(new TextWatcher(){
						public void afterTextChanged(Editable s) {
						// Abstract Method of TextWatcher Interface.
						}
						public void beforeTextChanged(CharSequence s, int start, int count, int after) {
						// Abstract Method of TextWatcher Interface.
						}
						public void onTextChanged(CharSequence s, int start, int before, int count)	{
							//System.out.println("search length: " + search.length());
							
							textlength = search.getText().length();
							contacts_sort.clear();
							ids_sort.clear();
							position_sort.clear();
							for (int i = 0; i < contacts.length; i++) {
								if (textlength <= contacts[i].length())	{
									if(search.getText().toString().equalsIgnoreCase((String) contacts[i].subSequence(0,	textlength))) {
										contacts_sort.add(contacts[i]);
										ids_sort.add(ids[i]);
										position_sort.add(i);
		                            }
		                        }
		                    }
							positionCheck = new int[contacts_sort.size()];
							for(int j = 0; j < position_sort.size();j++){
								if(originalIndex[position_sort.get(j)] == 1){
									positionCheck[j] = 1;
								}else{
									positionCheck[j] = 0;
								}
							}
							adapter = new DataAdapter(AddContact.this, android.R.layout.simple_list_item_checked, contacts_sort);
							adapter.setToggleList(positionCheck);
							setListAdapter(adapter);
						}
					});
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
		if(search.length() == 0){
			if (textView.isChecked()){
				originalIndex[position] = 1;
			}else{
				originalIndex[position] = 0;
			}
			adapter.setToggleList(originalIndex);
		}else{
			if (textView.isChecked()){
				positionCheck[position] = 1;
				originalIndex[position_sort.get(position)] = 1;
			}else{
				positionCheck[position] = 0;
				originalIndex[position_sort.get(position)] = 0;
			}
			adapter.setToggleList(positionCheck);
		}
	}

	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		switch (arg0.getId()) {
		case R.id.bAddContactSubmit:
			//check for no contact selected later
			if(originalIndex.length == 0){
				final AlertDialog.Builder builder = new AlertDialog.Builder(this);
				builder.setTitle("No Contact Selected");
				builder.setMessage("Please select a contact or press the back button.");
				builder.setNegativeButton("Ok",	new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog,	int which) {
						// TODO Auto-generated method stub
						builder.setCancelable(true);
					}
				});
				builder.show();
			}else{
				try {
					for (int i = 0; i < originalIndex.length; i++) {
						if (originalIndex[i] == 1) {
							tempIndex = i;
							if (ids[i] == -1) {
								AsyncTask<Integer, Void, String> task = new AsyncTask<Integer, Void, String>() {
									@Override
									protected String doInBackground(Integer... arg0) {
										HashMap<String, String> args = new HashMap<String, String>();
										args.put("userid", Main.userid+"");
										args.put("phone_number", phoneNumbers[tempIndex]);
										response = DatabaseAccess.getData(Main.serverName + "CreateVirtualUser.php", args);
										return null;
									}
	
									@Override
									protected void onPostExecute(String result) {
										try {
											JSONArray jArray = new JSONArray(response);
											JSONObject json_data = jArray.getJSONObject(0);
							                int contactid = json_data.getInt("contactid");
							                HashMap<String, String> addUser = new HashMap<String, String>();
											addUser.put("eventid", Main.eventid + "");
											addUser.put("userid", contactid + "");
											DatabaseAccess.getData(Main.serverName + "InviteUserWithIdChangeRequest.php", addUser);
										} catch (JSONException e) {
											// TODO Auto-generated catch block
											e.printStackTrace();
										}
									}
								};
								task.execute();
							} else {
								AsyncTask<Integer, Void, String> task = new AsyncTask<Integer, Void, String>() {
									@Override
									protected String doInBackground(Integer... arg0) {
										HashMap<String, String> addUser = new HashMap<String, String>();
										addUser.put("eventid", Main.eventid + "");
										addUser.put("userid", ids[tempIndex]+"");
										DatabaseAccess.getData(Main.serverName + "InviteUserWithIdChangeRequest.php", addUser);
										return null;
									}
	
									@Override
									protected void onPostExecute(String result) {	
									}
								};
								task.execute();
							}
						}	
					}
					Toast.makeText(AddContact.this, "- Guests Added -", Toast.LENGTH_SHORT).show();
				}catch(NullPointerException e){
					e.printStackTrace();
				}finally{
//					Intent openViewGuests = new Intent("com.MyInvite.Application.VIEWGUESTS");
//	    			openViewGuests.putExtra("eventid", Main.eventid);
//	    			startActivity(openViewGuests);
	    			finish();
				}
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