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

	// UI references.
	private EditText etEmail;
	private EditText etPassword;
	private View formView;

	private MainActivity parentActivity;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		container.removeAllViews();
		super.onCreate(savedInstanceState);
		formView = inflater.inflate(R.layout.activity_login, null);

		parentActivity = (MainActivity) getActivity();
		etEmail = (EditText) formView.findViewById(R.id.email);
		etEmail.setText(parentActivity.getCurrentUserEmail());
		etPassword = (EditText) formView.findViewById(R.id.password);
		etPassword.setOnEditorActionListener(new TextView.OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
				if (id == EditorInfo.IME_NULL) {
					startLogin();
					return true;
				}
				return false;
			}
		});

		etEmail.setText("igorstajic273@gmail.com");
		etPassword.setText("sifra123");

		formView.findViewById(R.id.sign_in_button).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				startLogin();
			}
		});
		// If there was an error logging in.
		if (getArguments() != null) {
			if (getArguments().getInt("error") == Error.InvalidPassword.ordinal()) {
				etPassword.setError(getString(R.string.error_incorrect_password));
				etPassword.requestFocus();
			} else if (getArguments().getInt("error") == Error.InvalidEmail.ordinal()) {
				etEmail.setError(getString(R.string.error_invalid_email));
				etEmail.requestFocus();
			}
		}

		return formView;
	}

	private void startLogin() {

		boolean cancel = false;
		View focusView = null;

		// Store values at the time of the login attempt.
		String mEmail = etEmail.getText().toString();
		String mPassword = etPassword.getText().toString();

		// Check for a valid password.
		if (TextUtils.isEmpty(mPassword)) {
			etPassword.setError(getString(R.string.error_field_required));
			focusView = etPassword;
			cancel = true;
		} else if (mPassword.length() < 4) {
			etPassword.setError(getString(R.string.error_invalid_password));
			focusView = etPassword;
			cancel = true;
		}

		// Check for a valid email address.
		if (TextUtils.isEmpty(mEmail)) {
			etEmail.setError(getString(R.string.error_field_required));
			focusView = etEmail;
			cancel = true;
		} else if (!mEmail.contains("@")) {
			etEmail.setError(getString(R.string.error_invalid_email));
			focusView = etEmail;
			cancel = true;
		}

		if (cancel) {
			// There was an error; don't attempt login and focus the first
			// form field with an error.
			focusView.requestFocus();
		} else {
			parentActivity.setCurrentUserEmail(mEmail);
			parentActivity.setCurrentUserPassword(mPassword);
			parentActivity.attemptLogin();
		}

	}

}
