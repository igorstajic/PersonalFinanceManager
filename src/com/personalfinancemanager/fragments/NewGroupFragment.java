package com.personalfinancemanager.fragments;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.example.personalfinancemanager.R;
import com.firebase.client.Firebase;
import com.personalfinancemanager.activities.MainActivity;
import com.personalfinancemanager.model.Group;

public class NewGroupFragment extends Fragment {

	private String ref = MainActivity.firebaseRef;
	Firebase fbRef = new Firebase(ref);

	NewGroupListAdapter adapter;

	MainActivity parentActivity;

	View formView;
	EditText groupNameView;

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		parentActivity = (MainActivity) getActivity();

		adapter = new NewGroupListAdapter(parentActivity,
				R.layout.removable_row, new ArrayList<String>());

		formView = inflater.inflate(R.layout.fragment_newgroup, null);
		groupNameView = (EditText) formView.findViewById(R.id.ng_name_text);

		ListView listView = (ListView) formView
				.findViewById(R.id.ng_users_list);
		listView.setAdapter(adapter);

		formView.findViewById(R.id.ng_footer_button).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {

						EditText mNameView = (EditText) formView
								.findViewById(R.id.ng_name_text);
						String mName = mNameView.getText().toString();

						boolean cancel = false;
						View focusView = null;

						// check for valid group name
						if (TextUtils.isEmpty(mName)) {
							mNameView
									.setError(getString(R.string.error_field_required));
							focusView = mNameView;
							cancel = true;
						}
						if (cancel) {
							// There was an error;
							// focus the first
							// form field with an error.
							focusView.requestFocus();
						} else {

							String groupName = groupNameView.getText()
									.toString();
							HashMap<String, String> users = new HashMap<String, String>();
							for (int i = 0; i < adapter.getCount(); i++) {
								String tempEmail = adapter.getItem(i);
								users.put(tempEmail, parentActivity
										.getUserList().get(tempEmail));
							}

							makeGroup(groupName, users);
						}
					}
				});

		formView.findViewById(R.id.ng_add_user).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						EditText mEmailView = (EditText) formView
								.findViewById(R.id.ng_user_text);
						String mEmail = mEmailView.getText().toString();

						boolean cancel = false;
						View focusView = null;
						
						mEmailView.setError(null);

						// Check for a valid email address.
						if (TextUtils.isEmpty(mEmail)) {
							mEmailView
									.setError(getString(R.string.error_field_required));
							focusView = mEmailView;
							cancel = true;
						} else if (!mEmail.contains("@")) {
							mEmailView
									.setError(getString(R.string.error_invalid_email));
							focusView = mEmailView;
							cancel = true;
						} else if (!parentActivity.getUserList().containsKey(
								mEmail)) {
							mEmailView
									.setError(getString(R.string.error_nonexisting_email));
							focusView = mEmailView;
							cancel = true;
						} else if (mEmail.equals(parentActivity.getmEmail())) {
							mEmailView
									.setError(getString(R.string.error_adding_myself));
							focusView = mEmailView;
							cancel = true;
						} else if (adapter.items.contains(mEmail)){
							mEmailView
							.setError(getString(R.string.error_adding_double));
					focusView = mEmailView;
					cancel = true;
						}
						if (cancel) {
							// There was an error; don't attempt login and focus
							// the first
							// form field with an error.
							focusView.requestFocus();
						} else {
							// Show a progress spinner, and kick off a
							// background task to
							// perform the user login attempt.
							adapter.insert(mEmail, 0);
						}
					}
				});

		return formView;
	}

	public void removeRowClickHandler(View v) {
		String itemToRemove = (String) v.getTag();
		adapter.remove(itemToRemove);
	}

	private void makeGroup(String groupName, HashMap<String, String> users) {

		String loggedUserEmail = parentActivity.getmEmail();
		users.put(loggedUserEmail,
				parentActivity.getUserList().get(loggedUserEmail));

		Group newGroup = new Group(groupName);
		for (String email : users.keySet()) {

			Firebase thisUserRef = fbRef.child("users").child(users.get(email))
					.child("groups");

			thisUserRef.push().setValue(newGroup);

			fbRef.child("groupdata").push().setValue(newGroup);
		}

		Fragment newContent = new MyGroupsFragment();
		parentActivity.switchContent(newContent);
	}

	public class NewGroupListAdapter extends ArrayAdapter<String> {

		private List<String> items;
		private int layoutResourceId;
		private Context context;

		public NewGroupListAdapter(Context context, int layoutResourceId,
				List<String> items) {
			super(context, layoutResourceId, items);
			this.layoutResourceId = layoutResourceId;
			this.context = context;
			this.items = items;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View row = convertView;

			LayoutInflater inflater = ((Activity) context).getLayoutInflater();

			row = inflater.inflate(layoutResourceId, parent, false);
			row.setTag(items.get(position));
			TextView rowTextView = (TextView) row
					.findViewById(R.id.user_list_user_email);
			rowTextView.setText(items.get(position));

			row.findViewById(R.id.user_list_remove_user).setOnClickListener(
					new OnClickListener() {

						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub
							removeRowClickHandler((View) v.getParent());

						}
					});
			return row;
		}

	}

}
