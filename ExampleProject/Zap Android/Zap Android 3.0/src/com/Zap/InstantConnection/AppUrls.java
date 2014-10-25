package com.Zap.InstantConnection;

public class AppUrls {
	
	public static final String ServerName = "http://app.zapconnection.com/MyInvite/";
	public static final String ServerName2 = "http://app.zapconnection.com/zapconnection/Server/";
	
	public static final String CreateUser = ServerName + "CreateUserWithConfirmation.php";
	public static final String ForgotPassword = ServerName + "ForgotPassword.php";
	
	public static final String CreateVirtualUser = ServerName + "CreateVirtualUser.php";
	
	public static final String Login = ServerName + "LoginSecure.php";
	public static final String Logout = ServerName + "Logout.php";
	
	public static final String RegisterGCMId = ServerName + "RegisterGCMId.php";
	
	// ------------ Main -----------
	public static final String GetMyEvents = ServerName + "GetMyEventsVer2.php";
	public static final String GetUpcomingEvents = ServerName + "GetUpcomingEventsVer2.php";
	
	// ------------ MyEvent -----------
	public static final String CreateEvent = ServerName + "CreateEvent.php";
	public static final String UpdateEvent = ServerName + "UpdateEventOpen.php";
	public static final String DeleteEvent = ServerName + "DeleteEvent.php";
	
	public static final String GetEventInfo = ServerName + "GetEventInfo.php";
	public static final String GetNumEvents = ServerName + "GetNumEvents.php";
	
	// ------------ UpcomingEvent -----------
	public static final String GetUpcomingEventInfo = ServerName + "GetUpcomingEventInfo.php";
	public static final String RemoveUpcomingEvent = ServerName + "RemoveUpcomingEvent.php";
	
	public static final String SetEventToNotified = ServerName + "SetNotified.php";
	public static final String RSVP = ServerName + "RSVP.php";
	
	// ------------ Contacts -----------
	public static final String GetContacts = ServerName + "GetContacts.php";
	public static final String GetContactInfo = ServerName + "GetContactInfo.php";
	public static final String SyncContacts = ServerName + "SyncContacts2.php";
	public static final String RemoveContact = ServerName + "RemoveContact.php";
	public static final String AddContactByEmail = ServerName + "AddEmailToContacts.php";
	public static final String AddContactByUsername = ServerName + "AddUserToContacts.php";
	
	// ------------ Groups -----------
	public static final String CreateGroup = ServerName2 + "CreateGroupN.php";
	public static final String GetGroups = ServerName + "GetGroups.php";
	public static final String GetGroupInfo = ServerName2 + "GetGroupInfo.php";
	public static final String DeleteGroup = ServerName + "DeleteGroup.php";
	public static final String AddUsersToGroup = "http://app.zapconnection.com/zapconnection/Server/AddUsersToGroupN.php";
	public static final String RemoveUsersFromGroup = "http://app.zapconnection.com/zapconnection/Server/RemoveGroupUsers.php";
	
	// ------------ Guests -----------
	public static final String ViewGuests = ServerName + "ViewGuests.php";
	public static final String GetGuestInfo = ServerName + "GetContactInfo.php";
	public static final String InviteUser = ServerName + "InviteUser.php";
	public static final String InviteGuests = ServerName + "InviteUserWithIdChangeRequest.php";
	public static final String InviteGroups = ServerName2 + "InviteGroupN.php";
	
	// ------------ Change Requests -----------
	public static final String MakeRequest = ServerName + "ChangeRequest.php";
	public static final String ViewChangeRequests = ServerName + "ViewChangeRequests.php";
	public static final String ChangeRequestVote = ServerName + "ChangeRequestVote.php";
	
	// ------------ Messages -----------
	public static final String CreateMessage = ServerName + "ChatMessage.php";
	public static final String ViewMessages = ServerName + "ViewChat.php";

}
