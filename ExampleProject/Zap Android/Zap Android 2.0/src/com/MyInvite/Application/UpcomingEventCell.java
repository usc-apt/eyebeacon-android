package com.MyInvite.Application;

import android.os.Parcel;
import android.os.Parcelable;

public class UpcomingEventCell implements Parcelable {

	private String eventName;
	private String eventDay;
	private String eventDate;
	private String eventTime;
	private String eventHost;
	private String eventNotification;

	public UpcomingEventCell(String name, String day, String date, String time, String host, String notificationType) {
		eventName = name;
		eventDay = day;
		eventDate = date;
		eventTime = time;
		eventHost = host;
		eventNotification = notificationType;
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

	public String getHost() {
		return eventHost;
	}

	public void setHost(String host) {
		this.eventHost = host;
	}
	
	public String getNotification(){
		return eventNotification;
	}

	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	public void writeToParcel(Parcel out, int flags) {
		// TODO Auto-generated method stub
		out.writeStringArray(new String[] { eventName, eventDay, eventDate, eventTime, eventHost, eventNotification});
	}

	public static final Parcelable.Creator<UpcomingEventCell> CREATOR = new Parcelable.Creator<UpcomingEventCell>() {
		public UpcomingEventCell createFromParcel(Parcel in) {
			return new UpcomingEventCell(in);
		}

		public UpcomingEventCell[] newArray(int size) {
			return new UpcomingEventCell[size];
		}
	};

	private UpcomingEventCell(Parcel in) {
		String[] data = new String[6];
		in.readStringArray(data);
		eventName = data[0];
		eventDay = data[1];
		eventDate = data[2];
		eventTime = data[3];
		eventHost = data[4];
		eventNotification = data[5];
	}


}
