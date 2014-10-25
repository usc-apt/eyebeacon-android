package com.MyInvite.Application;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONObject;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

public class MyEvent extends SherlockActivity implements OnClickListener {

	EditText title, location, hostName, notes;
	TextView timeDisplay, dateDisplay;

	private int myYear, myMonth, myDay, myHour, myMinute;
	static final int ID_DATEPICKER = 0;
	static final int ID_TIMEPICKER = 1;
	Button datePickerButton, timePickerButton;

	boolean infoChanged;
	String titleS, locationS, timeS, dateS, time, date, noteS;
	
	CheckBox checkBox;
	int open;
	private AsyncTask<String, Void, String> task;
	String response;
	ProgressDialog progDailog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
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
			setContentView(R.layout.myevent);
			ActionBar bar = getSupportActionBar();
			bar.show();
			bar.setTitle("My Event");
			title = (EditText) findViewById(R.id.etMyEventTitle);
			location = (EditText) findViewById(R.id.etMyEventLocation);
			notes = (EditText) findViewById(R.id.etMyEventNotes);

			timePickerButton = (Button) findViewById(R.id.bMyEventTimePicker);
			timePickerButton.setOnClickListener(this);
			timeDisplay = (TextView) findViewById(R.id.tvMyEventTime);
			datePickerButton = (Button) findViewById(R.id.bMyEventDatePicker);
			datePickerButton.setOnClickListener(this);
			dateDisplay = (TextView) findViewById(R.id.tvMyEventDate);
			checkBox = (CheckBox) findViewById(R.id.cbCreateEventOpen);
			
			getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
			
			progDailog = new ProgressDialog(MyEvent.this);
            progDailog.setMessage("Loading...");
            progDailog.setIndeterminate(false);
            progDailog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progDailog.setCancelable(false);
            progDailog.show();
			task = new AsyncTask<String, Void, String>() {
				@Override
				protected String doInBackground(String... arg0) {
					Bundle extras = getIntent().getExtras();
					HashMap<String, String> args = new HashMap<String, String>();
					Main.eventid = extras.getInt("eventid");
					args.put("eventid", Main.eventid + "");
					response = DatabaseAccess.getData(Main.serverName + "GetEventInfo.php", args);
					return null;
				}

				@Override
				protected void onPostExecute(String result) {
					try {
						JSONArray jArray = new JSONArray(response);
						JSONObject data = jArray.getJSONObject(0);
						title.setText(data.getString("title"));
						location.setText(data.getString("location"));
						String note = data.getString("notes");
						if(note == "null"){
							notes.setText("");
							System.out.println("empty notes: " + notes.getText().toString());
						}else{
							notes.setText(note);
							System.out.println("notes: " + notes.getText().toString());
						}
						noteS = notes.getText().toString();
						open = data.getInt("open");
						if(open == 0){
							checkBox.setChecked(false);
						}else if(open == 1){
							checkBox.setChecked(true);
						}
						
						String dateStr = data.getString("date");
						String timeStr = data.getString("time");
		                System.out.println(dateStr);
		                System.out.println(timeStr);
		                time = timeStr;
		                date = dateStr;
		                try {
		                	SimpleDateFormat sdf =  new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		                    Date date = sdf.parse(dateStr);
		                    sdf = new SimpleDateFormat("MM-dd-yyyy");
		                    dateDisplay.setText(sdf.format(date));
		                    
		                    sdf =  new SimpleDateFormat("HH:mm:ss");
		                    date = sdf.parse(timeStr);
		                    sdf = new SimpleDateFormat("h:mm a");		                    
		                    timeDisplay.setText(sdf.format(date));
		                    
		                } catch (Exception e) {
		                    // TODO Auto-generated catch block
		                    e.printStackTrace();
		                }
					
						String fullDate = data.getString("date");
						String m = fullDate.substring(5, 7);
						String d = fullDate.substring(8, 10);
						String y = fullDate.substring(0, 4);
						myYear = Integer.parseInt(y);
						myMonth = Integer.parseInt(m);
						myDay = Integer.parseInt(d);
						
						String fullTime = data.getString("time");
						int hourOfDay = Integer.parseInt(fullTime.substring(0, 2));
						int minute = Integer.parseInt(fullTime.substring(3, 5));
						myHour = hourOfDay;
						myMinute = minute;

						titleS = title.getText().toString();
						locationS = location.getText().toString();
						timeS = timeDisplay.getText().toString();
						dateS = dateDisplay.getText().toString();
					} catch (Exception e) {
						e.printStackTrace();
					}
					progDailog.dismiss();
				}
			};
			task.execute();
			
			infoChanged = false;
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    MenuInflater inflater = getSupportMenuInflater();
	    inflater.inflate(R.menu.myeventitems, menu);
	    return true;
	}
	
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	HashMap<String, String> args;
        switch(item.getItemId()) {
        	case R.id.iMyEventAddGuests:
        		Intent openContactList = new Intent("com.MyInvite.Application.ADDGUESTMENU");
    			startActivity(openContactList);
    			//finish();
        		return true;
        	case R.id.iMyEventViewGuests:
        		Intent openViewGuests = new Intent("com.MyInvite.Application.VIEWGUESTS");
    			openViewGuests.putExtra("eventid", Main.eventid);
    			openViewGuests.putExtra("open", 1);
    			startActivity(openViewGuests);
    			//finish();
        		return true;
        	case R.id.iMyEventUpdate:
        		update();
        		return true;
        	case R.id.iMyEventDelete:
        		final AlertDialog.Builder builder = new AlertDialog.Builder(this);
    			builder.setMessage("Are you sure you want to delete \"" + title.getText().toString() + "\"?");
    			builder.setCancelable(false);
    			builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
    				public void onClick(DialogInterface dialog, int id) {
    					task = new AsyncTask<String, Void, String>() {
        					@Override
        					protected String doInBackground(String... arg0) {
        						HashMap<String, String> args = new HashMap<String, String>();
            					args.put("eventid", Main.eventid + "");
            					response = DatabaseAccess.getData(Main.serverName + "DeleteEvent.php",	args);
        						return null;
        					}

        					@Override
        					protected void onPostExecute(String result) {
        						finish();
            					Toast.makeText(MyEvent.this, "- Event Deleted -", Toast.LENGTH_SHORT).show();
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
    
    public void update(){
    	if (title.length() <= 0) {
			final AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle("Invalid Title");
			builder.setMessage("Please enter a title for the event.");
			builder.setNegativeButton("Ok",	new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog,	int which) {
					// TODO Auto-generated method stub
					builder.setCancelable(true);
				}
			});
			builder.show();
		} else if (location.length() <= 0) {
			final AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle("Invalid Location");
			builder.setMessage("Please enter a location for the event.");
			builder.setNegativeButton("Ok",	new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog,	int which) {
					// TODO Auto-generated method stub
					builder.setCancelable(true);
				}
			});
			builder.show();
		} else if (!checkValidDate()) {
			final AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle("Invalid Date");
			builder.setMessage("The date you've chosen has already past.");
			builder.setNegativeButton("Ok",	new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog,	int which) {
					// TODO Auto-generated method stub
					builder.setCancelable(true);
				}
			});
			builder.show();
		} else if (!checkValidTime()) {
			final AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle("Invalid Time");
			builder.setMessage("The time you've chosen has already past.");
			builder.setNegativeButton("Ok",	new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					builder.setCancelable(true);
				}
			});
			builder.show();
		} else {
			task = new AsyncTask<String, Void, String>() {
				@Override
				protected String doInBackground(String... arg0) {
					HashMap<String, String> args = new HashMap<String, String>();
    				args.put("eventid", Main.eventid + "");
    				args.put("title", title.getText().toString());
    				args.put("location", location.getText().toString());
    				args.put("date", date);
    				args.put("time", time);
    				args.put("notes", notes.getText().toString());
    				if(checkBox.isChecked()){
    					open = 1;
    				}else{
    					open = 0;
    				}
    				args.put("open", open+ "");
					response = DatabaseAccess.getData(Main.serverName + "UpdateEventOpen.php", args);
					return null;
				}

				@Override
				protected void onPostExecute(String result) {
					infoChanged = false;
    				finish();
    				Toast.makeText(MyEvent.this, "- Event Updated -", Toast.LENGTH_SHORT).show();
				}
			};
			task.execute();
		}
    }

	public void onClick(View arg0) {
		HashMap<String, String> args;
		switch (arg0.getId()) {
		case R.id.bMyEventDatePicker:
			showDialog(ID_DATEPICKER);
			break;
		case R.id.bMyEventTimePicker:
			showDialog(ID_TIMEPICKER);
			break;
		}
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		checkChanges();
		if (infoChanged == true) {
			final AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle("Changes Made");
			builder.setMessage("Do you wish to save the changes to the event?");
			builder.setCancelable(false);
			builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					update();
				}
			});
			builder.setNegativeButton("No",	new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					infoChanged = false;
					finish();
				}
			});
			builder.show();
		} else {
			finish();
		}
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		// TODO Auto-generated method stub
		Calendar c = Calendar.getInstance();
		int currentYear = c.get(Calendar.YEAR);
		int currentMonth = c.get(Calendar.MONTH);
		int currentDay = c.get(Calendar.DAY_OF_MONTH);
		int currentHour = c.get(Calendar.HOUR_OF_DAY);
		switch (id) {
		case ID_DATEPICKER:
			return new DatePickerDialog(this, myDateSetListener, currentYear, currentMonth, currentDay);
		case ID_TIMEPICKER:
			return new TimePickerDialog(this, myTimeSetListener, currentHour + 1, 0, false);
		default:
			return null;
		}
	}

	private DatePickerDialog.OnDateSetListener myDateSetListener = new DatePickerDialog.OnDateSetListener() {
		public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
			myYear = year;
			myMonth = monthOfYear;
			myDay = dayOfMonth;
			date = String.valueOf(year) + "-" + String.valueOf(monthOfYear + 1)	+ "-" + String.valueOf(dayOfMonth);
			dateDisplay.setText(String.valueOf(monthOfYear + 1) + "-" + String.valueOf(dayOfMonth) + "-" + String.valueOf(year));
		}
	};

	private TimePickerDialog.OnTimeSetListener myTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
		public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
			myHour = hourOfDay;
			myMinute = minute;

			String h, m;
			boolean pm = true;
			if (minute < 10) {
				m = "0" + String.valueOf(minute);
			} else {
				m = String.valueOf(minute);
			}
			if (hourOfDay == 0) {
				h = "12";
				pm = false;
			} else if (hourOfDay == 12) {
				h = "12";
			} else if (hourOfDay > 12) {
				h = String.valueOf(hourOfDay - 12);
			} else {
				h = String.valueOf(hourOfDay);
				pm = false;
			}
			if (pm) {
				timeDisplay.setText(h + ":" + m + " PM");
			} else {
				timeDisplay.setText(h + ":" + m + " AM");
			}

			if (hourOfDay == 0) {
				time = "00:" + m;
			} else {
				if(hourOfDay == 24){
					time = "00:" + m;
				}else{
					time = String.valueOf(hourOfDay) + ":" + m;
				}
			}
		}
	};

	public void checkChanges() {
		String newTitle = title.getText().toString();
		if (!newTitle.equals(titleS)) {
			infoChanged = true;
			System.out.println("title changed");
		}
		String newLocation = location.getText().toString();
		if (!newLocation.equals(locationS)) {
			infoChanged = true;
			System.out.println("location changed");
		}
		String newTime = timeDisplay.getText().toString();
		if (!newTime.equals(timeS)) {
			infoChanged = true;
			System.out.println("time changed");
		}
		String newDate = dateDisplay.getText().toString();
		if (!newDate.equals(dateS)) {
			infoChanged = true;
			System.out.println("date changed");
		}
		String newNote = notes.getText().toString();
		if (!newNote.equals(noteS)) {
			infoChanged = true;
			System.out.println("notes changed");
		}
		if (open == 0 && checkBox.isChecked()) {
			infoChanged = true;
			System.out.println("check mark changed");
		}
		if (open == 1 && !checkBox.isChecked()) {
			infoChanged = true;
			System.out.println("check mark changed");
		}
	}

	public boolean checkValidDate() {
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
	
	public boolean checkValidTime() {
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
