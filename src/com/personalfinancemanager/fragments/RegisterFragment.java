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
import com.personalfinancemanager.activities.MainActivity;

public class RegisterFragment extends Fragment {

	private String error;

	// UI references.
	private EditText mEmailView;
	private EditText mPasswordView;
	private EditText mFullNameView;
	private TextView mLoginScreen;

	private String mEmail;
	private String mPassword;
	private String mFullName;

	private View mFormView;

	private MainActivity parentActivity;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		container.removeAllViews();
		super.onCreate(savedInstanceState);
		mFormView = inflater.inflate(R.layout.activity_register, null);

		parentActivity = (MainActivity) getActivity();

		mFullNameView = (EditText) mFormView.findViewById(R.id.reg_fullname);

		mEmailView = (EditText) mFormView.findViewById(R.id.reg_email);
		mEmailView.setText(parentActivity.getCurrentUserEmail());

		mPasswordView = (EditText) mFormView.findViewById(R.id.reg_password);
		mPasswordView
				.setOnEditorActionListener(new TextView.OnEditorActionListener() {
					@Override
					public boolean onEditorAction(TextView textView, int id,
							KeyEvent keyEvent) {
						if (id == R.id.login || id == EditorInfo.IME_NULL) {
							attemptRegister();
							return true;
						}
						return false;
					}
				});

		mFormView.findViewById(R.id.btnRegister).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View view) {

						attemptRegister();
					}
				});

		if (getArguments() != null) {
			mPasswordView
					.setError(getString(R.string.error_incorrect_password));
			mPasswordView.requestFocus();
		}

		return mFormView;
	}

	private void attemptRegister() {

		boolean cancel = false;
		View focusView = null;

		// Reset errors.
		mEmailView.setError(null);
		mPasswordView.setError(null);
		mFullNameView.setError(null);

		// Store values at the time of the login attempt.
		mEmail = mEmailView.getText().toString();
		mPassword = mPasswordView.getText().toString();
		mFullName = mFullNameView.getText().toString();

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
		} else if (parentActivity.getUserList().containsKey(mEmail)){
			mEmailView.setError(getString(R.string.error_existing_email));
			focusView = mEmailView;
			cancel = true;
		}
		
		if (TextUtils.isEmpty(mFullName)) {
			mEmailView.setError(getString(R.string.error_field_required));
			focusView = mFullNameView;
			cancel = true;
		}

		if (cancel) {
			// There was an error; don't attempt to register and focus the first
			// form field with an error.
			focusView.requestFocus();
		} else {
			// Show a progress spinner, and kick off a background task to
			// perform the register attempt.
			parentActivity.setCurrentUserEmail(mEmail);
			parentActivity.setCurrentUserPassword(mPassword);
			parentActivity.setCurrentUserFullName(mFullName);
			parentActivity.attempRegister();
		}

	}

}
