package com.Zap.InstantConnection.Events;
import com.Zap.InstantConnection.*;
import com.Zap.InstantConnection.Groups.CreateGroup;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockActivity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
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

public class CreateEvent extends SherlockActivity implements OnClickListener {

	public static ArrayList<String> sentUsernames;
	int eventid;
	Button submit;
	EditText title, location, notes;
	TextView timeDisplay, dateDisplay;
	String time, date, response;
	private int myYear, myMonth, myDay, myHour, myMinute;
	static final int ID_DATEPICKER = 0;
	static final int ID_TIMEPICKER = 1;
	Button datePickerButton, timePickerButton;
	CheckBox checkBox;
	int open;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		if(!AppUtils.haveNetworkConnection(this)) {
			AppUtils.showConnectionFailed(this);
		}else{
			setContentView(R.layout.createevent);
			ActionBar bar = getSupportActionBar();
			bar.show();
			bar.setTitle("Create Event");
			submit = (Button) findViewById(R.id.bCreateEventSubmit);
			submit.setOnClickListener(this);
	
			title = (EditText) findViewById(R.id.etCreateEventTitle);
			location = (EditText) findViewById(R.id.etCreateEventLocation);
			notes = (EditText) findViewById(R.id.etCreateEventNotes);
	
			timePickerButton = (Button) findViewById(R.id.bCreateEventTimePicker);
			timePickerButton.setOnClickListener(this);
			timeDisplay = (TextView) findViewById(R.id.tvCreateEventTime);
			datePickerButton = (Button) findViewById(R.id.bCreateEventDatePicker);
			datePickerButton.setOnClickListener(this);
			dateDisplay = (TextView) findViewById(R.id.tvCreateEventDate);
			checkBox = (CheckBox) findViewById(R.id.cbCreateEventOpen);
			if (checkBox.isChecked()) {
	             checkBox.setChecked(false);
	        }
			getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
		}
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}

	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		switch (arg0.getId()) {
		case R.id.bCreateEventSubmit:
			if (title.length() <= 0) {
				AppUtils.showAlertDialog(this, AppConstants.invalid_event_title, AppConstants.invalid_event, true);
			} else if (location.length() <= 0) {
				AppUtils.showAlertDialog(this, AppConstants.invalid_location_title, AppConstants.invalid_location, true);
			} else if (timeDisplay.length() <= 0) {
				AppUtils.showAlertDialog(this, AppConstants.invalid_date_title, AppConstants.invalid_date_empty, true);
			} else if (dateDisplay.length() <= 0) {
				AppUtils.showAlertDialog(this, AppConstants.invalid_date_title, AppConstants.invalid_date_empty, true);
			} else if (!AppUtils.checkValidDate(myYear, myMonth, myDay)) {
				AppUtils.showAlertDialog(this, AppConstants.invalid_date_title, AppConstants.invalid_date, true);
			} else if (!AppUtils.checkValidTime(myYear, myMonth, myDay, myHour, myMinute)) {
				AppUtils.showAlertDialog(this, AppConstants.invalid_time_title, AppConstants.invalid_time, true);
			} else {
				final HashMap<String, String> args = new HashMap<String, String>();
				args.put("title", title.getText().toString());
				args.put("location", location.getText().toString());
				if(notes.length() == 0){
					args.put("note", "null");
				}else{
					args.put("notes", notes.getText().toString());
				}
				args.put("time", time);
				args.put("date", date);
				args.put("host", Main.userid + "");			
				if(checkBox.isChecked()){
					open = 1;
				}else{
					open = 0;
				}
				args.put("open", open + "");
				AsyncTask<String, Void, String> task = new AsyncTask<String, Void, String>() {
					@Override
					protected String doInBackground(String... arg0) {
						response = DatabaseAccess.getData(AppUrls.CreateEvent, args);
						try {
							JSONArray jArray = new JSONArray(response);
							JSONObject json_data = jArray.getJSONObject(0);
							eventid = json_data.getInt("response");
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						Main.eventid = eventid;
						return null;
					}

					@Override
					protected void onPostExecute(String result) {
						AppUtils.showToast(CreateEvent.this, "- Event Created -");
						Intent openContactList = new Intent("com.Zap.InstantConnection.Guests.ADDGUESTMENU");
	        			startActivity(openContactList);
						finish();
					}
				};
				task.execute();	
			}
			break;

		case R.id.bCreateEventDatePicker:
			showDialog(ID_DATEPICKER);
			break;

		case R.id.bCreateEventTimePicker:
			showDialog(ID_TIMEPICKER);
			break;

		}
	}
	
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
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
	
}
