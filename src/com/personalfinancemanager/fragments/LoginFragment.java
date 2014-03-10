package com.personalfinancemanager.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.example.personalfinancemanager.R;
import com.firebase.simplelogin.enums.Error;
import com.personalfinancemanager.activities.MainActivity;

public class LoginFragment extends Fragment {

	private String error;

	// UI references.
	private EditText mEmailView;
	private EditText mPasswordView;
	private View mLoginFormView;

	private MainActivity parentActivity;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		container.removeAllViews();
		super.onCreate(savedInstanceState);
		mLoginFormView = inflater.inflate(R.layout.activity_login, null);

		parentActivity = (MainActivity) getActivity();

		mEmailView = (EditText) mLoginFormView.findViewById(R.id.email);
		mEmailView.setText(parentActivity.getmEmail());

		mPasswordView = (EditText) mLoginFormView.findViewById(R.id.password);
		mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
				if (id == R.id.login || id == EditorInfo.IME_NULL) {
					startLogin();
					return true;
				}
				return false;
			}
		});

		mEmailView.setText("igorstajic273@gmail.com");
		mPasswordView.setText("sifra123");

		mLoginFormView.findViewById(R.id.sign_in_button).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View view) {

						startLogin();
					}
				});

		if (getArguments() != null) {
			if (getArguments().getInt("error") == Error.InvalidPassword.ordinal()) {
				mPasswordView.setError(getString(R.string.error_incorrect_password));
				mPasswordView.requestFocus();
			} else if (getArguments().getInt("error") == Error.InvalidEmail.ordinal()){
				mEmailView.setError(getString(R.string.error_invalid_email));
				mEmailView.requestFocus();
			}
		}

		return mLoginFormView;
	}

	private void startLogin() {

		boolean cancel = false;
		View focusView = null;

		// Store values at the time of the login attempt.
		String mEmail = mEmailView.getText().toString();
		String mPassword = mPasswordView.getText().toString();

		// Check for a valid password.
		if (TextUtils.isEmpty(mPassword)) {
			mPasswordView.setError(getString(R.string.error_field_required));
			focusView = mPasswordView;
			cancel = true;
		} else if (mPassword.length() < 4) {
			mPasswordView.setError(getString(R.string.error_invalid_password));
			focusView = mPasswordView;
			cancel = true;
		}

		// Check for a valid email address.
		if (TextUtils.isEmpty(mEmail)) {
			mEmailView.setError(getString(R.string.error_field_required));
			focusView = mEmailView;
			cancel = true;
		} else if (!mEmail.contains("@")) {
			mEmailView.setError(getString(R.string.error_invalid_email));
			focusView = mEmailView;
			cancel = true;
		}

		if (cancel) {
			// There was an error; don't attempt login and focus the first
			// form field with an error.
			focusView.requestFocus();
		} else {
			// Show a progress spinner, and kick off a background task to
			// perform the user login attempt.
			parentActivity.setmEmail(mEmail);
			parentActivity.setmPassword(mPassword);
			parentActivity.attemptLogin();
		}

	}

}
