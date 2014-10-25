package com.Zap.InstantConnection.Messages;

import android.os.Parcel;
import android.os.Parcelable;

public class Message implements Parcelable {
	
	private String name;
	private String message;
	private String time;
    
    public Message(String n, String m, String t){
    	name = n;
    	message = m;
    	time = t;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getMessage() {
        return message;
    }
    public void setMessage(String message) {
        this.message = message;
    }
    public String getTime() {
        return time;
    }
    public void setTime(String time) {
        this.time = time;
    }

	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	public void writeToParcel(Parcel out, int flags) {
		// TODO Auto-generated method stub
		out.writeStringArray(new String[] {name, message, time});
	}
	
	public static final Parcelable.Creator<Message> CREATOR = new Parcelable.Creator<Message>() {
        public Message createFromParcel(Parcel in) {
            return new Message(in);
        }

        public Message[] newArray(int size) {
            return new Message[size];
        }
    };
    
    private Message(Parcel in) {
    	String[] data = new String[3];
    	in.readStringArray(data);
    	name = data[0];
    	message = data[1];
    	time = data[2];
    }

}
