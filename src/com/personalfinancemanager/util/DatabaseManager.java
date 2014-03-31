package com.personalfinancemanager.util;

import java.util.HashMap;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.personalfinancemanager.model.AppUser;

public class DatabaseManager {

	public static final String firebaseURL = "https://mojatestbaza.firebaseio.com/";

	private Firebase firebaseRef = new Firebase(firebaseURL);

	private String resultString = "";
	private Boolean queryFinished;
	
	public String getUserFullName(String userEmail){
		queryFinished = false;
		// firebaseURL/users/firebaseID/fullName
		 firebaseRef.child("users").child(getUserIdMap().get(userEmail)).child("fullName").addValueEventListener(new ValueEventListener() {
			
			@Override
			public void onDataChange(DataSnapshot data) {
				resultString = (String)data.getValue();
				
			}
			
			@Override
			public void onCancelled(FirebaseError arg0) {

				
			}
		});
		 waitForQueryResult(queryFinished);
		 
		return resultString; 
	}

	public HashMap<String, String> getUserIdMap() {
		final HashMap<String, String> resultMap = new HashMap<String, String>();
		firebaseRef.child("users").addChildEventListener(new ChildEventListener() {

			@Override
			public void onChildAdded(DataSnapshot snapshot, String arg1) {
				AppUser tempUser = snapshot.getValue(AppUser.class);
				resultMap.put(tempUser.getEmailAddress(), snapshot.getName());
			}

			@Override
			public void onCancelled(FirebaseError arg0) {
			}

			@Override
			public void onChildChanged(DataSnapshot arg0, String arg1) {
			}

			@Override
			public void onChildMoved(DataSnapshot arg0, String arg1) {
			}

			@Override
			public void onChildRemoved(DataSnapshot arg0) {
			}

		});

		return resultMap;
	}

	public static void waitForQueryResult(Boolean done){
		while (!done){
			try {
				Thread.sleep(50);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
