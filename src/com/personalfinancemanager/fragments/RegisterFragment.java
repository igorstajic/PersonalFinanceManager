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

	// UI references.
	private EditText etEmail;
	private EditText etPassword;
	private EditText etFullName;

	private String email;
	private String password;
	private String fullName;

	private View formView;

	private MainActivity parentActivity;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		container.removeAllViews();
		super.onCreate(savedInstanceState);

		formView = inflater.inflate(R.layout.activity_register, null);
		parentActivity = (MainActivity) getActivity();

		etFullName = (EditText) formView.findViewById(R.id.reg_fullname);
		etEmail = (EditText) formView.findViewById(R.id.reg_email);
		etEmail.setText(parentActivity.getCurrentUserEmail());
		etPassword = (EditText) formView.findViewById(R.id.reg_password);
		etPassword.setOnEditorActionListener(new TextView.OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
				if (id == EditorInfo.IME_NULL) {
					attemptRegister();
					return true;
				}
				return false;
			}
		});

		formView.findViewById(R.id.btnRegister).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				attemptRegister();
			}
		});

		if (getArguments() != null) {
			etPassword.setError(getString(R.string.error_incorrect_password));
			etPassword.requestFocus();
		}

		return formView;
	}

	private void attemptRegister() {

		boolean cancel = false;
		View focusView = null;

		// Reset errors.
		etEmail.setError(null);
		etPassword.setError(null);
		etFullName.setError(null);

		// Store values at the time of the login attempt.
		email = etEmail.getText().toString();
		password = etPassword.getText().toString();
		fullName = etFullName.getText().toString();

		// Check for a valid password.
		if (TextUtils.isEmpty(password)) {
			etPassword.setError(getString(R.string.error_field_required));
			focusView = etPassword;
			cancel = true;
		} else if (password.length() < 4) {
			etPassword.setError(getString(R.string.error_invalid_password));
			focusView = etPassword;
			cancel = true;
		}

		// Check for a valid email address.
		if (TextUtils.isEmpty(email)) {
			etEmail.setError(getString(R.string.error_field_required));
			focusView = etEmail;
			cancel = true;
		} else if (!email.contains("@")) {
			etEmail.setError(getString(R.string.error_invalid_email));
			focusView = etEmail;
			cancel = true;
		} else if (parentActivity.getUserList().containsKey(email)) {
			etEmail.setError(getString(R.string.error_existing_email));
			focusView = etEmail;
			cancel = true;
		}
		
		if (TextUtils.isEmpty(fullName)) {
			etEmail.setError(getString(R.string.error_field_required));
			focusView = etFullName;
			cancel = true;
		}

		if (cancel) {
			focusView.requestFocus();
		} else {
			parentActivity.setCurrentUserEmail(email);
			parentActivity.setCurrentUserPassword(password);
			parentActivity.setCurrentUserFullName(fullName);
			parentActivity.attempRegister();
		}

	}

}
