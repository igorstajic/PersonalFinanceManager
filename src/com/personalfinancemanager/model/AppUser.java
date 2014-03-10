package com.personalfinancemanager.model;

import java.util.HashMap;
import java.util.Map;

public class AppUser {

	private String fullName;
	private String emailAddress;
	private HashMap<String, Group> groups;
	
	

	public AppUser() {
	}
	public AppUser(String fullName, String emailAddress) {
		super();
		this.fullName = fullName;
		this.emailAddress = emailAddress;
		this.groups = new HashMap<String, Group>();
		
	}
	public String getFullName() {
		return fullName;
	}
	public void setFullName(String fullName) {
		this.fullName = fullName;
	}
	public String getEmailAddress() {
		return emailAddress;
	}
	public void setEmailAddress(String eMailAddress) {
		this.emailAddress = eMailAddress;
	}
	public HashMap<String, Group> getGroups() {
		return groups;
	}
	public void setGroups(HashMap<String, Group> groups) {
		this.groups = groups;
	}
	
	
	
	
}
