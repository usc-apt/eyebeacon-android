package com.Zap.InstantConnection.Contacts;

public class Contact {
	
	//public String username;
	private String name, number;
	private int id, virtual;
	
	public Contact(String name, String number, int id, int virtual){
		this.name = name;
		this.number = number;
		this.id = id;
		this.virtual = virtual;
	}
	
	public String getName(){
		return name;
	}
	
	public String getNumber(){
		return number;
	}
	
	public int getId(){
		return id;
	}
	
	public int getVirtual(){
		return virtual;
	}

}
