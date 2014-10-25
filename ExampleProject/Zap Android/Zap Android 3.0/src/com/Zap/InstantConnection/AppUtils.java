package com.Zap.InstantConnection;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.Zap.InstantConnection.Contacts.Contact;
import com.Zap.InstantConnection.Contacts.ContactsAdapter;
import com.Zap.InstantConnection.Contacts.ContactsComparator;
import com.Zap.InstantConnection.Guests.AddContacts;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class AppUtils {

	public static boolean haveNetworkConnection(Context c) {
	    boolean haveConnectedWifi = false;
	    boolean haveConnectedMobile = false;

	    ConnectivityManager cm = (ConnectivityManager) c.getSystemService(Context.CONNECTIVITY_SERVICE);
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
	
	public static void showAlertDialog(Context c, String title, String message, boolean cancelable){
		final Context context = c;
		AlertDialog.Builder builder = new AlertDialog.Builder(c);
		builder.setTitle(title);
		builder.setMessage(message);
		builder.setCancelable(cancelable);
		if(cancelable){
			builder.setNeutralButton("Ok", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					dialog.cancel();
				}
			});
		}else{
			builder.setNegativeButton("Ok", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					((Activity)context).finish();
				}
			});
		}
		builder.show();
	}
	
	public static void showYesNoAlertDialog(Context c, String title, String message){
		final Context context = c;
		AlertDialog.Builder builder = new AlertDialog.Builder(c);
		builder.setTitle(title);
		builder.setMessage(message);
		builder.setCancelable(false);
		builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				((Activity)context).finish();
			}
		});
		builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				dialog.cancel();
			}
		});
		builder.show();
	}
	
	public static void showToast(Context c, String message){
		Toast.makeText(c, message, Toast.LENGTH_SHORT).show();
	}
	
	public static void showConnectionFailed(Context c){
		showAlertDialog(c, AppConstants.connection_failed_title, AppConstants.connection_failed_message, false);
	}
	
	public static boolean checkValidDate(int myYear, int myMonth, int myDay) {
		Calendar c = Calendar.getInstance();
		int currentYear = c.get(Calendar.YEAR);
		int currentMonth = c.get(Calendar.MONTH);
		int currentDay = c.get(Calendar.DAY_OF_MONTH);

		if (myYear < currentYear) {
			return false;
		} else if (myYear == currentYear && myMonth < currentMonth) {
			return false;
		} else if (myYear == currentYear && myMonth == currentMonth && myDay < currentDay) {
			return false;
		}
		return true;
	}
	
	public static boolean checkValidTime(int myYear, int myMonth, int myDay, int myHour, int myMinute) {
		Calendar c = Calendar.getInstance();
		int currentYear = c.get(Calendar.YEAR);
		int currentMonth = c.get(Calendar.MONTH);
		int currentDay = c.get(Calendar.DAY_OF_MONTH);
		int currentHour = c.get(Calendar.HOUR_OF_DAY);
		int currentMinute = c.get(Calendar.MINUTE);
		
		if (myYear == currentYear && myMonth == currentMonth && myDay == currentDay) {
			if (myHour < currentHour) {
				return false;
			} else if (myHour == currentHour && myMinute < currentMinute) {
				return false;
			}
		}
		return true;
	}
	
	public static boolean checkSomethingSelected(int[] checklist){
		for(int i = 0; i < checklist.length; i++){
			if(checklist[i] == 1){
				return true;
			}
		}
		return false;
	}
	
	public static ArrayList<Contact> getContacts(Context c, Boolean fullContact){
		ArrayList<Contact> tempContacts = new ArrayList<Contact>();
		
		HashMap<String, String> args = new HashMap<String, String>();
		SharedPreferences prefs = c.getSharedPreferences("com.Zap.InstantConnection", Context.MODE_PRIVATE);
		args.put("userid", prefs.getInt("userid", -1)+"");
		String response = DatabaseAccess.getData(AppUrls.GetContacts, args);
		if(response == null){
			return tempContacts;
		}
		try {
			JSONArray jArray = new JSONArray(response);
			int length = jArray.length();
			tempContacts = new ArrayList<Contact>(length);
			ArrayList<String> tempNumbers = new ArrayList<String>();
			int virtual;
			String phoneNumber;
			for (int i = 0; i < length; i++) {
                JSONObject json_data = jArray.getJSONObject(i);
                Contact tempContact = null;
                virtual = json_data.getInt("virtual");
                phoneNumber = json_data.getString("phone_number");
                tempNumbers.add(phoneNumber);
                if(virtual == 0) {
                	tempContact = new Contact("*" + json_data.getString("name"),
							phoneNumber,
							json_data.getInt("contactid"),
							virtual);
                } else if(virtual == 1) {
                	phoneNumber = json_data.getString("phone_number");
					if (Main.numbers.containsKey(phoneNumber)) {
						tempContact = new Contact(Main.numbers.get(phoneNumber) + "*",
								phoneNumber,
								json_data.getInt("contactid"),
								virtual);
					}
                }
                if(tempContact != null){
					tempContacts.add(tempContact);
				}
	        }
			
			if(fullContact){
				for(String phoneNum: Main.numbers.keySet()) {
					if(!tempNumbers.contains(phoneNum)) {
						Contact tempContact = new Contact(Main.numbers.get(phoneNum)+"*", phoneNum, -1, 1);
						tempContacts.add(tempContact);
					}
				}
			}
			Collections.sort(tempContacts, new ContactsComparator());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return tempContacts;
	}
	
	public static boolean checkLocation(Context c, String address){
		String searchPattern = address;
	    LocationManager lm = (LocationManager)c.getSystemService(Context.LOCATION_SERVICE);
	    //I use last known location, but here we can get real location
	    Location lastKnownLocation = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);

	    List<Address> addresses = null;
	    try {
	        //trying to get all possible addresses by search pattern
	        addresses = (new Geocoder(c)).getFromLocationName(searchPattern, Integer.MAX_VALUE);
	    } catch (IOException e) {
	    	System.out.println(e);
	    	return false;
	    }
	    if (addresses == null || lastKnownLocation == null) {
	        // location service unavailable or incorrect address
	        // so returns null
	        return false;
	    }

	    Address closest = null;
	    float closestDistance = Float.MAX_VALUE;
	    // look for address, closest to our location
	    for (Address adr : addresses) {
	        if (closest == null) {
	            closest = adr;
	        } else {
	            float[] result = new float[1];
	            Location.distanceBetween(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude(), adr.getLatitude(), adr.getLongitude(), result);
	            float distance = result[0];
	            if (distance < closestDistance) {
	                closest = adr;
	                closestDistance = distance;
	            }
	        }
	    }
	    
	    if(closest != null){
	    	System.out.println("closest address: " + closest.toString());
	    	return true;
	    }else{
	    	return false;
	    }
	}
}
