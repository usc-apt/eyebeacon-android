package com.Zap.InstantConnection.Events;

import android.os.Parcel;
import android.os.Parcelable;

public class MyEventCell implements Parcelable {

	private String eventName;
	private String eventDay;
	private String eventDate;
	private String eventTime;
	private String eventLocation;

	public MyEventCell(String name, String day, String date, String time, String location) {
		eventName = name;
		eventDay = day;
		eventDate = date;
		eventTime = time;
		eventLocation = location;
	}

	public String getEventName() {
		return eventName;
	}

	public void setEventName(String name) {
		this.eventName = name;
	}
	
	public String getDay() {
		return eventDay;
	}

	public void setDay(String day) {
		this.eventDay = day;
	}

	public String getDate() {
		return eventDate;
	}

	public void setDate(String date) {
		this.eventDate = date;
	}

	public String getTime() {
		return eventTime;
	}

	public void setTime(String time) {
		this.eventTime = time;
	}

	public String getLocation() {
		return eventLocation;
	}

	public void setLocation(String location) {
		this.eventLocation = location;
	}

	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	public void writeToParcel(Parcel out, int flags) {
		// TODO Auto-generated method stub
		out.writeStringArray(new String[] { eventName, eventDay, eventDate, eventTime, eventLocation});
	}

	public static final Parcelable.Creator<MyEventCell> CREATOR = new Parcelable.Creator<MyEventCell>() {
		public MyEventCell createFromParcel(Parcel in) {
			return new MyEventCell(in);
		}

		public MyEventCell[] newArray(int size) {
			return new MyEventCell[size];
		}
	};

	private MyEventCell(Parcel in) {
		String[] data = new String[5];
		in.readStringArray(data);
		eventName = data[0];
		eventDay = data[1];
		eventDate = data[2];
		eventTime = data[3];
		eventLocation = data[4];
	}

}
