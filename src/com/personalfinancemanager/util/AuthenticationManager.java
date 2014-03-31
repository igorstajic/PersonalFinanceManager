package com.personalfinancemanager.util;

import android.R.bool;

import com.firebase.client.Firebase;
import com.firebase.simplelogin.SimpleLogin;
import com.firebase.simplelogin.SimpleLoginAuthenticatedHandler;
import com.firebase.simplelogin.User;
import com.firebase.simplelogin.enums.Error;

public class AuthenticationManager {

	private Firebase firebaseRef = new Firebase(DatabaseManager.firebaseURL);
	private SimpleLogin firebaseAuthenticationClient = new SimpleLogin(firebaseRef);

	private String errorMessage = "";
	private Boolean queryFinished;

	// Returns empty string if success, else returns error message.
	public String attemptLoginWithEmail(String email, String password) {
		queryFinished = false;
		firebaseAuthenticationClient.loginWithEmail(email, password,
				new SimpleLoginAuthenticatedHandler() {
					public void authenticated(Error error, User user) {
						if (error != null) {
							// There was an error logging into this account.
							if (error == Error.InvalidPassword) {
								errorMessage = "Password was incorrect!";
							} else if (error == Error.InvalidEmail) {
								errorMessage = "Email doen't exist!";
							}
						} else {
							errorMessage = "Login complete!";
						}
						queryFinished = true;
					}
				});
		DatabaseManager.waitForQueryResult(queryFinished);
		return errorMessage;

	}

	public void logout() {
		firebaseAuthenticationClient.logout();
	}
}
