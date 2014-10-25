package com.Zap.InstantConnection.Contacts;

import java.util.Comparator;

public class ContactsComparator implements Comparator<Contact> {
	@Override
	public int compare(Contact c1, Contact c2) {
		// TODO Auto-generated method stub
		return c1.getName().compareTo(c2.getName());
    }
}
