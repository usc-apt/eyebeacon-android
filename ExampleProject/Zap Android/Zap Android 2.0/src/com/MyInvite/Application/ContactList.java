package com.MyInvite.Application;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import org.json.JSONArray;
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
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

public class ContactList extends SherlockListActivity {

	String contacts[] = new String[0];
	String usernames[];
	String phoneNumbers[];
	String response;
	int ids[];
	int positionCheck[] = new int[0];
	ProgressDialog progDailog;
	EditText search;
	TextView message;
	private ArrayList<String> array_sort = new ArrayList<String>();
	private ArrayList<Integer> ids_sort = new ArrayList<Integer>();
	private AsyncTask<String, Void, String> task;
	int textlength=0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		if (!haveNetworkConnection()) {
			final AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle("Connection failed");
			builder.setMessage("Make sure you are connected to the internet.");
			builder.setNegativeButton("Ok",	new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					builder.setCancelable(false);
					finish();
				}
			});
			builder.show();
		} else {
			setContentView(R.layout.contactlist);
			ActionBar bar = getSupportActionBar();
			bar.show();
			bar.setTitle("Contact List");
			getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
			
			progDailog = new ProgressDialog(ContactList.this);
            progDailog.setMessage("Loading...");
            progDailog.setIndeterminate(false);
            progDailog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progDailog.setCancelable(false);
            progDailog.show();
			task = new AsyncTask<String, Void, String>() {
				@Override
				protected String doInBackground(String... arg0) {
					HashMap<String, String> args = new HashMap<String, String>();
					args.put("userid", Main.userid + "");
					response = DatabaseAccess.getData(Main.serverName + "GetContacts.php", args);
					System.out.println("get contacts: " + response);
					return null;
				}

				@Override
				protected void onPostExecute(String result) {
					contacts = new String[0];
					try {
						JSONArray jArray = new JSONArray(response);
						int length = jArray.length();
						ArrayList<String> sortedContacts = new ArrayList<String>();
						ArrayList<String> tempContacts = new ArrayList<String>();
						ArrayList<Integer> tempIds = new ArrayList<Integer>();
						ArrayList<String> tempUsernames = new ArrayList<String>();
						ArrayList<String> tempPhoneNumbers = new ArrayList<String>();
						int virtual;
						String phoneNumber;
						// HashMap<String, String> nativeContacts = Main.numbers;
						for (int i = 0; i < length; i++) {
							JSONObject json_data = jArray.getJSONObject(i);
							virtual = json_data.getInt("virtual");
							if (virtual == 0) {
								tempIds.add(json_data.getInt("contactid"));
								tempContacts.add("*" + json_data.getString("name"));
								sortedContacts.add("*" + json_data.getString("name"));
								tempUsernames.add(json_data.getString("username"));
								phoneNumber = json_data.getString("phone_number");
								tempPhoneNumbers.add(phoneNumber);
								if (Main.numbers.containsKey(phoneNumber)) {
									// nativeContacts.remove(phoneNumber);
								}
							} else if (virtual == 1) {
								phoneNumber = json_data.getString("phone_number");
								if (Main.numbers.containsKey(phoneNumber)) {
									tempIds.add(json_data.getInt("contactid"));
									tempContacts.add(Main.numbers.get(phoneNumber) + "*");
									sortedContacts.add(Main.numbers.get(phoneNumber) + "*");
									tempUsernames.add("");
									tempPhoneNumbers.add(phoneNumber);
									// nativeContacts.remove(phoneNumber);
								}
							}
						}
						for (String phoneNum : Main.numbers.keySet()) {
							if (!tempContacts.contains("*" + Main.numbers.get(phoneNum))
									&& !tempContacts.contains(Main.numbers.get(phoneNum) + "*")) {
								tempIds.add(-1);
								tempContacts.add(Main.numbers.get(phoneNum) + "*");
								sortedContacts.add(Main.numbers.get(phoneNum) + "*");
								tempUsernames.add("");
								tempPhoneNumbers.add(phoneNum);
							}
						}
						int size = tempContacts.size();
						Collections.sort(sortedContacts);
						positionCheck = new int[size];
						contacts = new String[size];
						ids = new int[size];
						usernames = new String[size];
						phoneNumbers = new String[size];
						for (int i = 0; i < size; i++) {
							for (int j = 0; j < size; j++) {
								if (tempContacts.get(i).equals(sortedContacts.get(j))) {
									if (tempContacts.get(i).charAt(0) == '*') {
										contacts[j] = tempContacts.get(i).substring(1);
									} else {
										contacts[j] = tempContacts.get(i);
									}
									ids[j] = tempIds.get(i);
									usernames[j] = tempUsernames.get(i);
									phoneNumbers[j] = tempPhoneNumbers.get(i);
								}
							}
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
					setListAdapter(new ArrayAdapter<String>(ContactList.this, android.R.layout.simple_list_item_1, contacts));
					message = (TextView) findViewById(R.id.tvContactList);
					if (contacts.length == 0) {
						message.setText("No Contacts");
					}else{
						message.setVisibility(View.GONE);
					}
					progDailog.dismiss();
				}
			};
			task.execute();
			
			search = (EditText) findViewById(R.id.etContactListSearch);
			search.addTextChangedListener(new TextWatcher() {
				public void afterTextChanged(Editable s) {
					// Abstract Method of TextWatcher Interface.
				}

				public void beforeTextChanged(CharSequence s, int start, int count, int after) {
					// Abstract Method of TextWatcher Interface.
				}

				public void onTextChanged(CharSequence s, int start, int before, int count) {
					//System.out.println("search length: " + search.length());
					textlength = search.getText().length();
					array_sort.clear();
					ids_sort.clear();
					for (int i = 0; i < contacts.length; i++) {
						if (textlength <= contacts[i].length()) {
							if (search.getText().toString().equalsIgnoreCase((String) contacts[i].subSequence(0,textlength))) {
								array_sort.add(contacts[i]);
								ids_sort.add(ids[i]);
							}
						}
					}
					setListAdapter(new ArrayAdapter<String>(ContactList.this, android.R.layout.simple_list_item_1, array_sort));
					message = (TextView) findViewById(R.id.tvContactList);
					if (array_sort.size() == 0) {
						message.setText("No Contacts");
					}else{
						message.setVisibility(View.GONE);
					}
				}
			});
			
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    MenuInflater inflater = getSupportMenuInflater();
	    inflater.inflate(R.menu.contactlistitems, menu);
	    return true;
	}
	
	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
            switch(item.getItemId()) {
            	case R.id.iContactListAddByUsername:
            		Intent openAddUser = new Intent("com.MyInvite.Application.ADDUSERCONTACT");
        			startActivity(openAddUser);
            		return true;
            	case R.id.iContactListAddByEmail:
            		Intent openAddContact = new Intent("com.MyInvite.Application.ADDBYEMAIL");
        			startActivity(openAddContact);
        			return true;
            	case R.id.iContactListSyncFromAddressBook:
            		AsyncTask<String, Void, String> task = new AsyncTask<String, Void, String>() {
        				@Override
        				protected String doInBackground(String... arg0) {
        					try {
        						ArrayList<String> numbers = checkContactsNumbers(getApplicationContext());
        						HashMap<String, String> args = new HashMap<String, String>();
        						args.put("userid", Main.userid+"");
        						String key = "1";
        						for(int i = 0; i < numbers.size(); i++) {
        								args.put(key, numbers.get(i));
        								key += "1";
        						}
        						DatabaseAccess.getData(Main.serverName + "SyncContacts2.php", args);
        						} catch (Exception e) {
        							e.printStackTrace();
        						}
        					return null;
        				}

        				@Override
        				protected void onPostExecute(String result) {
        					onResume();
        				}
            		};
            		task.execute();	
            	default:
                    return super.onOptionsItemSelected(item);
            }
    }
	
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		// TODO Auto-generated method stub
		super.onListItemClick(l, v, position, id);
		if (search.length() == 0) {
			try{
				Class ourClass = Class.forName("com.MyInvite.Application.ContactInfo");
				Intent ourIntent = new Intent(ContactList.this, ourClass);
				ourIntent.putExtra("id", ids[position]);
				startActivity(ourIntent);
			}catch(ClassNotFoundException e){
				e.printStackTrace();
			}
		} else {
			try{
				Class ourClass = Class.forName("com.MyInvite.Application.ContactInfo");
				Intent ourIntent = new Intent(ContactList.this, ourClass);
				ourIntent.putExtra("id", ids_sort.get(position));
				startActivity(ourIntent);
			}catch(ClassNotFoundException e){
				e.printStackTrace();
			}
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
	
	public static HashMap<String, String> checkContacts(Context c) {
		String contactId = null;
		String phoneNo = null;
		String contactName = null;
		String hasPhone = null;

		HashMap<String, String> numbers = new HashMap<String, String>();

		Context context = c;
		Cursor cursor = context.getContentResolver().query(
				ContactsContract.Contacts.CONTENT_URI, null, null, null, null);

		while (cursor.moveToNext()) {
			contactId = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
			contactName = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
			hasPhone = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));

			if (Integer.parseInt(hasPhone) == 1) {
				Cursor phone = context.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
						ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + contactId, null, null);

				while (phone.moveToNext()) {
					int type = phone.getInt(phone.getColumnIndex(Phone.TYPE));
					phoneNo = phone.getString(phone.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
					phoneNo = phoneNo.replaceAll("-", "");
					phoneNo = phoneNo.replaceAll( "[^\\d]", "");
					if(phoneNo.charAt(0) == '1') {
						phoneNo = phoneNo.substring(1);
					}
	                switch (type) {
	                    case Phone.TYPE_HOME:
	                    	numbers.put(phoneNo, contactName + " (Home)");
	                        break;
	                    case Phone.TYPE_MOBILE:
	                    	numbers.put(phoneNo, contactName);
	                        break;
	                    case Phone.TYPE_WORK:
	                    	numbers.put(phoneNo, contactName + " (Work)");
	                        break;
	                }		
				}
				phone.close();
			}
		}
		cursor.close();
		return numbers;
	}
	
	public static ArrayList<String> checkContactsNumbers(Context c) {
		String contactId = null;
		String phoneNo = null;
		String contactName = null;
		String hasPhone = null;

		ArrayList<String> numbers = new ArrayList<String>();

		Context context = c;
		Cursor cursor = context.getContentResolver().query(
				ContactsContract.Contacts.CONTENT_URI, null, null, null, null);

		while (cursor.moveToNext()) {
			contactId = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
			contactName = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
			hasPhone = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));

			if (Integer.parseInt(hasPhone) == 1) {
				Cursor phone = context.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
						ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + contactId, null, null);

				while (phone.moveToNext()) {
					phoneNo = phone.getString(phone.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
					phoneNo = phoneNo.replaceAll("-", "");
					phoneNo = phoneNo.replaceAll( "[^\\d]", "");
					if(phoneNo.charAt(0) == '1') {
						phoneNo = phoneNo.substring(1);
					}
					numbers.add(phoneNo);
				}
				
				phone.close();
			}
		}
		cursor.close();
		return numbers;
	}

}