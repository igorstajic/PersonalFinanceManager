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

	private String ref = MainActivity.firebaseURL;
	Firebase firebaseRef = new Firebase(ref);

	MainActivity parentActivity;
	View formView;
	EditText etGroupName;
	EmailListAdapter emailListAdapter;

	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		parentActivity = (MainActivity) getActivity();

		formView = inflater.inflate(R.layout.fragment_newgroup, null);
		etGroupName = (EditText) formView.findViewById(R.id.ng_name_text);
		emailListAdapter = new EmailListAdapter(parentActivity, R.layout.removable_row,
				new ArrayList<String>());
		ListView listView = (ListView) formView.findViewById(R.id.ng_users_list);
		listView.setAdapter(emailListAdapter);

		formView.findViewById(R.id.ng_footer_button).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				EditText etGroupName = (EditText) formView.findViewById(R.id.ng_name_text);
				String groupName = etGroupName.getText().toString();

				// check for valid group name
				if (TextUtils.isEmpty(groupName)) {
					etGroupName.setError(getString(R.string.error_field_required));
					etGroupName.requestFocus();
				} else {
					HashMap<String, String> users = new HashMap<String, String>();
					for (int i = 0; i < emailListAdapter.getCount(); i++) {
						String tempEmail = emailListAdapter.getItem(i);
						users.put(tempEmail, parentActivity.getUserList().get(tempEmail));
					}
					makeGroup(groupName, users);
				}
			}
		});

		formView.findViewById(R.id.ng_add_user).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				EditText etEmail = (EditText) formView.findViewById(R.id.ng_user_text);
				String emailEntry = etEmail.getText().toString();

				etEmail.setError(null);
				if (isEmailValid(etEmail, emailEntry)) {
					etEmail.requestFocus();
				} else {
					emailListAdapter.insert(emailEntry, 0);
				}
			}

			private boolean isEmailValid(EditText etEmail, String emailEntry) {
				boolean cancel = false;
				if (TextUtils.isEmpty(emailEntry)) {
					etEmail.setError(getString(R.string.error_field_required));
					cancel = true;
				} else if (!emailEntry.contains("@")) {
					etEmail.setError(getString(R.string.error_invalid_email));
					cancel = true;
				} else if (!parentActivity.getUserList().containsKey(emailEntry)) {
					etEmail.setError(getString(R.string.error_nonexisting_email));
					cancel = true;
				} else if (emailEntry.equals(parentActivity.getCurrentUserEmail())) {
					etEmail.setError(getString(R.string.error_adding_myself));
					cancel = true;
				} else if (emailListAdapter.items.contains(emailEntry)) {
					etEmail.setError(getString(R.string.error_adding_double));
					cancel = true;
				}
				return cancel;
			}
		});
		return formView;
	}

	private void makeGroup(String groupName, HashMap<String, String> users) {

		String loggedUserEmail = parentActivity.getCurrentUserEmail();
		users.put(loggedUserEmail, parentActivity.getUserList().get(loggedUserEmail));

		Group newGroup = new Group(groupName);
		for (String email : users.keySet()) {
			Firebase thisUserRef = firebaseRef.child("users").child(users.get(email))
					.child("groups");
			
			//Input in database.
			thisUserRef.push().setValue(newGroup);
			firebaseRef.child("groupdata").push().setValue(newGroup);
		}

		Fragment newContent = new MyGroupsFragment();
		parentActivity.switchCurrentFragment(newContent);
	}

	public class EmailListAdapter extends ArrayAdapter<String> {

		private List<String> items;
		private int layoutResourceId;
		private Context context;

		public EmailListAdapter(Context context, int layoutResourceId, List<String> items) {
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
			TextView rowTextView = (TextView) row.findViewById(R.id.user_list_user_email);
			rowTextView.setText(items.get(position));

			row.findViewById(R.id.user_list_remove_user).setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					removeRowClickHandler((View) v.getParent());
				}
			});
			return row;
		}
	}

	public void removeRowClickHandler(View v) {
		String itemToRemove = (String) v.getTag();
		emailListAdapter.remove(itemToRemove);
	}
}
