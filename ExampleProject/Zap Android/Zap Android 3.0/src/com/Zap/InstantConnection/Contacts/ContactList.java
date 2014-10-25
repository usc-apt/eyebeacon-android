package com.Zap.InstantConnection.Contacts;
import com.Zap.InstantConnection.*;

import java.util.ArrayList;
import java.util.HashMap;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockListActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
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

	private ArrayList<Contact> allContacts = new ArrayList<Contact>();
	private ArrayList<Contact> tempcontacts_sort = new ArrayList<Contact>();
	private ArrayList<String> array_sort = new ArrayList<String>();
	String contacts[] = new String[0];

	String response;
	ProgressDialog progDailog;
	EditText search;
	TextView message;
	int textlength = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		if(!AppUtils.haveNetworkConnection(this)) {
			AppUtils.showConnectionFailed(this);
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
            AsyncTask<String, Void, String> task = new AsyncTask<String, Void, String>() {
				@Override
				protected String doInBackground(String... arg0) {
					allContacts = AppUtils.getContacts(ContactList.this, false);
					return null;
				}

				@Override
				protected void onPostExecute(String result) {
					int size = allContacts.size();
					contacts = new String[size];
					for (int i = 0; i < size; i++) {
						if (allContacts.get(i).getName().charAt(0) == '*') {
							contacts[i] = allContacts.get(i).getName().substring(1);
						} else {
							contacts[i] = allContacts.get(i).getName();
						}
					}
					setListAdapter(new ArrayAdapter<String>(ContactList.this, android.R.layout.simple_list_item_1, contacts));
					message = (TextView) findViewById(R.id.tvContactList);
					if (contacts.length == 0) {
						message.setText("No Contacts");
					}else{
						message.setVisibility(View.GONE);
					}
					
					search = (EditText) findViewById(R.id.etContactListSearch);
					search.addTextChangedListener(new TextWatcher() {
						public void afterTextChanged(Editable s) {}

						public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

						public void onTextChanged(CharSequence s, int start, int before, int count) {
							textlength = search.getText().length();
							tempcontacts_sort.clear();
							array_sort.clear();
							for (int i = 0; i < allContacts.size(); i++) {
								if (textlength <= allContacts.get(i).getName().length()){
									String tempContactName;
									if(allContacts.get(i).getName().charAt(0) == '*'){
										tempContactName = allContacts.get(i).getName().substring(1);
									}else{
										tempContactName = allContacts.get(i).getName();
									}
									if(search.getText().toString().equalsIgnoreCase((String)tempContactName.subSequence(0,textlength))) {
										tempcontacts_sort.add(allContacts.get(i));
										array_sort.add(tempContactName);
		                            }
		                        }
		                    }
							setListAdapter(new ArrayAdapter<String>(ContactList.this, android.R.layout.simple_list_item_1, array_sort));
							message = (TextView) findViewById(R.id.tvContactList);
							if (array_sort.size() == 0) {
								message.setText("No Contacts Found");
							}else{
								message.setVisibility(View.GONE);
							}
						}
					});
					progDailog.dismiss();
				}
			};
			task.execute();			
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
            		Intent openAddUser = new Intent("com.Zap.InstantConnection.Contacts.ADDBYUSERNAME");
        			startActivity(openAddUser);
            		return true;
            	case R.id.iContactListAddByEmail:
            		Intent openAddContact = new Intent("com.Zap.InstantConnection.Contacts.ADDBYEMAIL");
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
        						response = DatabaseAccess.getData(AppUrls.SyncContacts, args);
        						System.out.println("sync contact response: "+response);
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
				Class ourClass = Class.forName("com.Zap.InstantConnection.Contacts.ContactInfo");
				Intent ourIntent = new Intent(ContactList.this, ourClass);
				ourIntent.putExtra("id", allContacts.get(position).getId());
				startActivity(ourIntent);
			}catch(ClassNotFoundException e){
				e.printStackTrace();
			}
		} else {
			try{
				Class ourClass = Class.forName("com.Zap.InstantConnection.Contacts.ContactInfo");
				Intent ourIntent = new Intent(ContactList.this, ourClass);
				ourIntent.putExtra("id", tempcontacts_sort.get(position).getId());
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
	
	public static HashMap<String, String> checkContacts(Context c) {
		String contactId = null;
		String phoneNo = null;
		String contactName = null;
		String hasPhone = null;

		HashMap<String, String> numbers = new HashMap<String, String>();

		Context context = c;
		Cursor cursor = context.getContentResolver().query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);

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