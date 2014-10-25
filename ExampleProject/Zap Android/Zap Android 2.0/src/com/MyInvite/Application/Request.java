package com.MyInvite.Application;

import android.os.Parcel;
import android.os.Parcelable;

public class Request implements Parcelable {
	
	private String requester;
	private String requestDate;
	private String requestTime;
    private String requestLocation;
    
    public Request(String name, String date, String time, String location){
    	requester = name;
    	requestDate = date;
    	requestTime = time;
    	requestLocation = location;
    }

    public String getRequester() {
        return requester;
    }
    public void setRequester(String name) {
        this.requester = name;
    }
    public String getDate() {
        return requestDate;
    }
    public void setDate(String date) {
        this.requestDate = date;
    }
    public String getTime() {
        return requestTime;
    }
    public void setTime(String time) {
        this.requestTime = time;
    }
    public String getLocation() {
        return requestLocation;
    }
    public void setLocation(String location) {
        this.requestLocation = location;
    }

	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	public void writeToParcel(Parcel out, int flags) {
		// TODO Auto-generated method stub
		out.writeStringArray(new String[] {requester, requestDate, requestTime, requestLocation});
	}
	
	public static final Parcelable.Creator<Request> CREATOR = new Parcelable.Creator<Request>() {
        public Request createFromParcel(Parcel in) {
            return new Request(in);
        }

        public Request[] newArray(int size) {
            return new Request[size];
        }
    };
    
    private Request(Parcel in) {
    	String[] data = new String[4];
    	in.readStringArray(data);
    	requester = data[0];
    	requestDate = data[1];
    	requestTime = data[2];
    	requestLocation = data[3];
    }

}
