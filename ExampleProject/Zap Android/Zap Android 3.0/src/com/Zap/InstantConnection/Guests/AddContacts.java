package com.Zap.InstantConnection.Guests;
import com.Zap.InstantConnection.*;
import com.Zap.InstantConnection.Contacts.Contact;
import com.Zap.InstantConnection.Contacts.ContactsAdapter;
import java.util.ArrayList;
import java.util.HashMap;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockListActivity;

import android.app.ProgressDialog;
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

public class AddContacts extends SherlockListActivity implements OnClickListener {

	int positionCheck[] = new int[0];
	int originalIndex[] = new int[0];
	ProgressDialog progDialog;
	Button submit;
	TextView message, searchResult;
	EditText search;
	
	private ArrayList<Contact> allContacts = new ArrayList<Contact>();
	private ArrayList<Contact> tempcontacts_sort = new ArrayList<Contact>();
	private ArrayList<Integer> position_sort = new ArrayList<Integer>();
	ContactsAdapter adapter;
	
	String response;
	int textlength = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		if(!AppUtils.haveNetworkConnection(this)) {
			AppUtils.showConnectionFailed(this);
		} else {	
			setContentView(R.layout.addguestbycontact);
			ActionBar bar = getSupportActionBar();
			bar.show();
			bar.setTitle("Add Guests");
			getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
			
			progDialog = new ProgressDialog(AddContacts.this);
            progDialog.setMessage("Loading...");
            progDialog.setIndeterminate(false);
            progDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progDialog.setCancelable(false);
            progDialog.show();
            
			AsyncTask<String, Void, String> task = new AsyncTask<String, Void, String>() {
				@Override
				protected String doInBackground(String... arg0) {
					allContacts = AppUtils.getContacts(AddContacts.this, true);
					return null;
				}

				@Override
				protected void onPostExecute(String result) {
					int size = allContacts.size();
					positionCheck = new int[size];
					originalIndex = new int[size];
					for(int i = 0; i < size; i++) {
						positionCheck[i] = 0;
						originalIndex[i] = 0;
					}
					adapter = new ContactsAdapter(AddContacts.this, android.R.layout.simple_list_item_checked, allContacts);
					adapter.setToggleList(positionCheck);
					setListAdapter(adapter);
					
					submit = (Button) findViewById(R.id.bAddContactSubmit);
					submit.setOnClickListener(AddContacts.this);
					
					message = (TextView) findViewById(R.id.tvAddContactMessage);
					if (allContacts.size() == 0) {
						message.setText("No Contacts.\nGo to \"Contact List\" in main menu to manage contacts.");
					}else{
						message.setText("Select contacts to add to your event.");
					}
					search = (EditText) findViewById(R.id.etAddContactSearch);
					search.addTextChangedListener(new TextWatcher(){
						public void afterTextChanged(Editable s) {}
						public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
						public void onTextChanged(CharSequence s, int start, int before, int count)	{
							textlength = search.getText().length();
							tempcontacts_sort.clear();
							position_sort.clear();
							for (int i = 0; i < allContacts.size(); i++) {
								if (textlength <= allContacts.get(i).getName().length()){
									String tempContactName;
									if(allContacts.get(i).getName().charAt(0) == '*'){
										tempContactName = allContacts.get(i).getName().substring(1);
									}else{
										tempContactName = allContacts.get(i).getName();
									}
									if(search.getText().toString().equalsIgnoreCase((String)tempContactName.subSequence(0,textlength))) {
										System.out.println(allContacts.get(i).getName() + " matches.");
										tempcontacts_sort.add(allContacts.get(i));
										position_sort.add(i);
		                            }
		                        }
		                    }
							positionCheck = new int[tempcontacts_sort.size()];
							for(int j = 0; j < position_sort.size();j++){
								if(originalIndex[position_sort.get(j)] == 1){
									positionCheck[j] = 1;
								}else{
									positionCheck[j] = 0;
								}
							}
							adapter = new ContactsAdapter(AddContacts.this, android.R.layout.simple_list_item_checked, tempcontacts_sort);
							adapter.setToggleList(positionCheck);
							setListAdapter(adapter);
							searchResult = (TextView) findViewById(R.id.tvAddContactSearchResult);
							if (search.getText().length() != 0 && tempcontacts_sort.size() == 0) {
								System.out.println("no match");
								searchResult.setVisibility(View.VISIBLE);
							}else{
								searchResult.setVisibility(View.GONE);
							}
						}
					});
					progDialog.dismiss();
				}
			};
			task.execute();
		}
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		// TODO Auto-generated method stub
		super.onListItemClick(l, v, position, id);
		CheckedTextView textView = (CheckedTextView) v.findViewById(R.id.tvContactName);
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
			if(!AppUtils.checkSomethingSelected(originalIndex)){
				AppUtils.showAlertDialog(this, AppConstants.no_contact_selected_title, AppConstants.no_contact_selected, true);
			}else{	
				progDialog = new ProgressDialog(AddContacts.this);
	            progDialog.setMessage("Adding Guests...");
	            progDialog.setIndeterminate(false);
	            progDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
	            progDialog.setCancelable(false);
	            progDialog.show();
				AsyncTask<Integer, Void, String> task = new AsyncTask<Integer, Void, String>() {
					@Override
					protected String doInBackground(Integer... arg0) {
						for (int i = 0; i < originalIndex.length; i++) {
							if (originalIndex[i] == 1) {
								if (allContacts.get(i).getId() == -1) {
									HashMap<String, String> args = new HashMap<String, String>();
									args.put("userid", Main.userid+"");
									args.put("phone_number", allContacts.get(i).getNumber());
									response = DatabaseAccess.getData(AppUrls.CreateVirtualUser, args);
									try {
										JSONArray jArray = new JSONArray(response);
										JSONObject json_data = jArray.getJSONObject(0);
						                int contactid = json_data.getInt("contactid");
						                HashMap<String, String> addUser = new HashMap<String, String>();
										addUser.put("eventid", Main.eventid + "");
										addUser.put("userid", contactid + "");
										DatabaseAccess.getData(AppUrls.InviteGuests, addUser);
									} catch (JSONException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
								} else {
									HashMap<String, String> addUser = new HashMap<String, String>();
									addUser.put("eventid", Main.eventid + "");
									addUser.put("userid", allContacts.get(i).getId()+"");
									DatabaseAccess.getData(AppUrls.InviteGuests, addUser);
								}
							}
						}
						return null;
					}
					
					@Override
					protected void onPostExecute(String result) {
						progDialog.dismiss();
						AppUtils.showToast(AddContacts.this, "- Guests Added -");
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
		if(AppUtils.checkSomethingSelected(originalIndex)){
			AppUtils.showYesNoAlertDialog(this, AppConstants.contact_selected_title, AppConstants.contact_selected);
		}else{
			super.onBackPressed();
		}
	}

}