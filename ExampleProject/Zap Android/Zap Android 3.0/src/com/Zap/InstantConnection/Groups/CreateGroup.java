package com.Zap.InstantConnection.Groups;
import com.Zap.InstantConnection.*;
import com.Zap.InstantConnection.Contacts.*;

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
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

public class CreateGroup extends SherlockListActivity implements OnClickListener {

	String contacts[] = new String[0];
	String usernames[];
	String phoneNumbers[];
	int ids[];
	int positionCheck[] = new int[0];
	int originalIndex[] = new int[0];
	String response;
	Button createGroup, home;
	EditText groupName, search;
	TextView message;
	private ArrayList<String> array_sort = new ArrayList<String>();
	private ArrayList<Integer> ids_sort = new ArrayList<Integer>();
	private ArrayList<Integer> position_sort = new ArrayList<Integer>();
	int textlength = 0;
	DataAdapter adapter;
	ProgressDialog progDailog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		if(!AppUtils.haveNetworkConnection(this)) {
			AppUtils.showConnectionFailed(this);
		}else{	
			setContentView(R.layout.creategroup);
			ActionBar bar = getSupportActionBar();
			bar.show();
			bar.setTitle("Create Group");
			progDailog = new ProgressDialog(this);
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
					response = DatabaseAccess.getData(AppUrls.GetContacts, args);
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
						HashMap<String, String> nativeContacts = ContactList.checkContacts(getApplicationContext());
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
			                	if(nativeContacts.containsKey(phoneNumber)) {
			                		nativeContacts.remove(phoneNumber);
			                	}
			                } else if(virtual == 1) {
			                	phoneNumber = json_data.getString("phone_number");
			                	if(nativeContacts.containsKey(phoneNumber)) {
			                		tempIds.add(json_data.getInt("contactid"));
				                	tempContacts.add(nativeContacts.get(phoneNumber)+"*");
				                	sortedContacts.add(nativeContacts.get(phoneNumber)+"*");
				                	tempUsernames.add("");
				                	tempPhoneNumbers.add(phoneNumber);
				                	nativeContacts.remove(phoneNumber);
			                	}
			                }
				        }
						for(String phoneNum: nativeContacts.keySet()) {
		            		tempIds.add(-1);
		                	tempContacts.add(nativeContacts.get(phoneNum)+"*");
		                	sortedContacts.add(nativeContacts.get(phoneNum)+"*");
		                	tempUsernames.add("");
		                	tempPhoneNumbers.add(phoneNum);
						}
						int size = tempContacts.size();
						Collections.sort(sortedContacts);
						positionCheck = new int[size];
						originalIndex = new int[size];
						contacts = new String[size];
						ids = new int[size];
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
									phoneNumbers[j] = tempPhoneNumbers.get(i);
									positionCheck[j] = 0;
									originalIndex[j] = 0;
								}
							}
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
					adapter = new DataAdapter(CreateGroup.this, android.R.layout.simple_list_item_checked, contacts);
					adapter.setToggleList(positionCheck);
					setListAdapter(adapter);
					message = (TextView) findViewById(R.id.tvCreateGroupMessage);
					if(contacts.length == 0){
						message.setText("No Contacts.");
					}else{
						message.setVisibility(View.GONE);
					}
					search = (EditText) findViewById(R.id.etCreateGroupSearch);
					search.addTextChangedListener(new TextWatcher(){
						public void afterTextChanged(Editable s) {
						// Abstract Method of TextWatcher Interface.
						}
						public void beforeTextChanged(CharSequence s, int start, int count, int after) {
						// Abstract Method of TextWatcher Interface.
						}
						public void onTextChanged(CharSequence s, int start, int before, int count)	{
							System.out.println("search length: " + search.length());
							
							textlength = search.getText().length();
							array_sort.clear();
							ids_sort.clear();
							position_sort.clear();
							for (int i = 0; i < contacts.length; i++) {
								if (textlength <= contacts[i].length())	{
									if(search.getText().toString().equalsIgnoreCase((String) contacts[i].subSequence(0,	textlength))) {
										array_sort.add(contacts[i]);
										ids_sort.add(ids[i]);
										position_sort.add(i);
		                            }
		                        }
		                    }
							positionCheck = new int[array_sort.size()];
							for(int j = 0; j < position_sort.size();j++){
								if(originalIndex[position_sort.get(j)] == 1){
									positionCheck[j] = 1;
								}else{
									positionCheck[j] = 0;
								}
							}
							adapter = new DataAdapter(CreateGroup.this, android.R.layout.simple_list_item_checked, array_sort);
							adapter.setToggleList(positionCheck);
							setListAdapter(adapter);
						}
					});
					progDailog.dismiss();
				}
			};
			task.execute();
			createGroup = (Button) findViewById(R.id.bCreateGroup);
			createGroup.setOnClickListener(CreateGroup.this);
			groupName = (EditText) findViewById(R.id.etCreateGroupName);
			
		}
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		// TODO Auto-generated method stub
		super.onListItemClick(l, v, position, id);
		if(search.length() == 0){
			CheckedTextView textView = (CheckedTextView) v;
			textView.setChecked(!textView.isChecked());
			if (textView.isChecked()){
				originalIndex[position] = 1;
			}else{
				originalIndex[position] = 0;
			}
			adapter.setToggleList(originalIndex);
		}else{
			CheckedTextView textView = (CheckedTextView) v;
			textView.setChecked(!textView.isChecked());
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
		case R.id.bCreateGroup:
			if(!AppUtils.checkSomethingSelected(originalIndex)){
				AppUtils.showAlertDialog(this, AppConstants.no_group_member_selected_title, AppConstants.no_group_member_selected, true);
			}else{
				AsyncTask<String, Void, String> task = new AsyncTask<String, Void, String>() {
					@Override
					protected String doInBackground(String... arg0) {
						ArrayList<Integer> user_ids_arraylist = new ArrayList<Integer>();
						for(int i = 0; i < ids.length; i++) {
							if(originalIndex[i] == 1) {
								if(ids[i] == -1) {
									HashMap<String, String> createVirtual = new HashMap<String, String>();
									createVirtual.put("userid", Main.userid+"");
									createVirtual.put("phone_number", phoneNumbers[i]);
									response = DatabaseAccess.getData(AppUrls.CreateVirtualUser, createVirtual);
									try {
										JSONArray jArray = new JSONArray(response);
										JSONObject json_data = jArray.getJSONObject(0);
						                int contactid = json_data.getInt("contactid");
						                user_ids_arraylist.add(contactid);
									} catch (JSONException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
								} else {
									user_ids_arraylist.add(ids[i]);
								}
							}
						}
						JSONArray jsonArray = new JSONArray();
						for(Integer i : user_ids_arraylist){
							jsonArray.put(i+"");
						}

						JSONObject args = new JSONObject();
						try{
							args.put("user_id", Main.userid+"");
							args.put("groupname", groupName.getText());
							args.put("user_ids", jsonArray);
						}catch(JSONException e){
							e.printStackTrace();
						}
						response = DatabaseAccess.sendJsonObject(AppUrls.CreateGroup, args);
						System.out.println("create group response: " + response);
						return null;
					}
		
					@Override
					protected void onPostExecute(String result) {
						AppUtils.showToast(CreateGroup.this, "- Group Created -");
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
		super.onBackPressed();
	}

}