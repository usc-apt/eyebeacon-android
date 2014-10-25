package com.Zap.InstantConnection;

import java.util.Calendar;
import java.util.HashMap;

import com.google.android.gcm.GCMBaseIntentService;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;

public class GCMIntentService extends GCMBaseIntentService{

	HashMap<String, String> args = new HashMap<String, String>();;
	AsyncTask<String, Void, String> task;
	String response;
	
	@Override
    protected void onRegistered(Context arg0, String registrationId) {
		System.out.println("Device registered: regId = " + registrationId);
        args = new HashMap<String, String>();
        args.put("regid", registrationId);
        args.put("userid", Main.userid+"");
		task = new AsyncTask<String, Void, String>() {
			@Override
			protected String doInBackground(String... arg0) {
				
		        DatabaseAccess.getData(AppUrls.RegisterGCMId, args);
				return null;
			}
		};
		task.execute();
    }

    @Override
    protected void onUnregistered(Context arg0, String arg1) {
        System.out.println("unregistered = " + arg1);
    }

    @Override
    protected void onMessage(Context arg0, Intent intent) {
    	Bundle extras = intent.getExtras();
    	String message = extras.getString("message");
    	String title = extras.getString("title");
    	String eventid = extras.getString("eventid");
    	Main.eventid = Integer.parseInt(eventid);
    	String time = extras.getString("time");
    	String date = extras.getString("date");
    	String typeString = extras.getString("type");
    	int open = extras.getInt("open");
    	int type = Integer.parseInt(typeString);
    	int calid = 0;
    	System.out.println("type" + type);
    	if (type == 1) {
			try {
				int year = Integer.valueOf(date.substring(0, 4));
				int month = Integer.valueOf(date.substring(5, 7));
				int day = Integer.valueOf(date.substring(8, 10));
				System.out.println(time);
				int hours = Integer.valueOf(time.substring(0, 2));
				int minutes = Integer.valueOf(time.substring(3, 5));

				ContentValues cvEvent = new ContentValues();
				cvEvent.put("calendar_id", 1);
				cvEvent.put("title", title);

				long startMillis = 0;
				long endMillis = 0;

				Calendar beginTime = Calendar.getInstance();
				beginTime.set(year, month - 1, day, hours, minutes);
				startMillis = beginTime.getTimeInMillis();
				Calendar endTime = Calendar.getInstance();
				endTime.set(year, month - 1, day, hours + 1, minutes);
				endMillis = endTime.getTimeInMillis();

				cvEvent.put("dtstart", startMillis);
				// cvEvent.put("hasAlarm", 1);
				cvEvent.put("dtend", endMillis);
				cvEvent.put("visibility", 3);
				Uri uri = getContentResolver().insert(Uri.parse("content://com.android.calendar/events"), cvEvent);
				long eventID = Long.parseLong(uri.getLastPathSegment());
				args = new HashMap<String, String>();
				args.put("calid", eventID + "");
				args.put("userid", Main.userid + "");
				args.put("eventid", Integer.parseInt(eventid)+ "");
				System.out.println(Main.userid + " " + eventID + " " + eventid);
			} catch (Exception e) {
				e.printStackTrace();
			}
	    	task = new AsyncTask<String, Void, String>() {
	    		@Override
	    		protected String doInBackground(String... arg0) {
	    			//response = DatabaseAccess.getData(Main.serverName + "UpdateCalendarId.php", args);
	    			return null;
	    		}
	
	    		@Override
	    		protected void onPostExecute(String result) {
	    		}
	    	};
	    	task.execute();
		} else if (type == 0) {
			try {
		    	calid = Integer.parseInt(message);
				Uri deleteUri = null;
				deleteUri = ContentUris.withAppendedId(Uri.parse("content://com.android.calendar/events"), calid);
				getContentResolver().delete(deleteUri, null, null);
			} catch (Exception e) {
				
			}
		} else if (type == 2) {
			try {
		    	calid = Integer.parseInt(message);
		    	int year = Integer.valueOf(date.substring(0, 4));
				int month = Integer.valueOf(date.substring(5, 7));
				int day = Integer.valueOf(date.substring(8, 10));
				int hours = Integer.valueOf(time.substring(0, 2));
				int minutes = Integer.valueOf(time.substring(3, 5));

				ContentValues cvEvent = new ContentValues();
				cvEvent.put("calendar_id", 1);
				cvEvent.put("title", title);

				long startMillis = 0;
				long endMillis = 0;

				Calendar beginTime = Calendar.getInstance();
				beginTime.set(year, month - 1, day, hours, minutes);
				startMillis = beginTime.getTimeInMillis();
				Calendar endTime = Calendar.getInstance();
				endTime.set(year, month - 1, day, hours + 1, minutes);
				endMillis = endTime.getTimeInMillis();

				cvEvent.put("dtstart", startMillis);
				// cvEvent.put("hasAlarm", 1);
				cvEvent.put("dtend", endMillis);
				cvEvent.put("visibility", 3); 
				Uri uri = ContentUris.withAppendedId(Uri.parse("content://com.android.calendar/events"), calid);
		    	getContentResolver().update(uri, cvEvent, null, null);
			
			} catch (Exception e) {
				
			}
			
		} else {
			String ns = Context.NOTIFICATION_SERVICE;
			NotificationManager mNotificationManager = (NotificationManager) getSystemService(ns);

			int icon = R.drawable.ic_launcher;
			CharSequence tickerText = "Zap";
			long when = System.currentTimeMillis();

			Notification notification = new Notification(icon, tickerText, when);

			Context context = getApplicationContext();
			CharSequence contentTitle = title;
			CharSequence contentText = message;
			Class ourClass;
			Intent ourIntent = new Intent();
			try {
				System.out.println("opening from notification");
				ourClass = Class.forName("com.Zap.InstantConnection.Main");
				ourIntent = new Intent(this, ourClass);
				//Main.eventid = Integer.parseInt(eventid);
				//System.out.println("notification eventid:" + Main.eventid);
				//ourIntent.putExtra("eventid", Main.eventid);
				//ourIntent.putExtra("open", open);
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			// Intent notificationIntent = new Intent(this,
			// GCMIntentService.class);
			PendingIntent contentIntent = PendingIntent.getActivity(this, 0, ourIntent, 0);

			notification.setLatestEventInfo(context, contentTitle, contentText,	contentIntent);
			notification.defaults |= Notification.DEFAULT_SOUND;

			final int NOTIF_ID = Integer.parseInt(eventid);

			mNotificationManager.notify(NOTIF_ID, notification);
		}
    }

    @Override
    protected void onError(Context arg0, String errorId) {
        System.out.println("Received error: " + errorId);
    }

    @Override
    protected boolean onRecoverableError(Context context, String errorId) {
        return super.onRecoverableError(context, errorId);
    }

}
