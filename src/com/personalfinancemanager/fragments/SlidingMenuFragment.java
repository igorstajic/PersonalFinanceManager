package com.personalfinancemanager.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.personalfinancemanager.R;
import com.personalfinancemanager.activities.MainActivity;

public class SlidingMenuFragment extends android.support.v4.app.ListFragment {

	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// There are no arguments when there is no user logged in.
		if (getArguments() != null) {
			View thisView = inflater.inflate(R.layout.fragment_menu_logged_user, null);
			TextView tvEmail = (TextView) thisView.findViewById(R.id.tv_menu_email);
			tvEmail.setText(getArguments().getString("email"));
			TextView tvFullName = (TextView) thisView.findViewById(R.id.tv_menu_fullname);
			tvFullName.setText(getArguments().getString("fullname"));
			return thisView;
		}

		return inflater.inflate(R.layout.list, null);
	}

	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		SlidingMenuRowAdapter menuListAdapter = new SlidingMenuRowAdapter(getActivity());

		if (getArguments() != null) {
			menuListAdapter.add(getString(R.string.my_groups));
		} else {
			menuListAdapter.add(getString(R.string.login));
			menuListAdapter.add(getString(R.string.register));
		}

		setListAdapter(menuListAdapter);
	}

	@Override
	public void onListItemClick(ListView lv, View v, int position, long id) {
		Fragment newContent = null;
		if (getArguments() != null) {
			switch (position) {
			case 0:
				newContent = new MyGroupsFragment();
				break;
			}

		} else {
			switch (position) {
			case 0:
				newContent = new LoginFragment();
				break;
			case 1:
				newContent = new RegisterFragment();
				break;
			}
		}
		if (newContent != null)
			((MainActivity) getActivity()).switchCurrentFragment(newContent);
	}

	public class SlidingMenuRowAdapter extends ArrayAdapter<String> {

		public SlidingMenuRowAdapter(Context context) {
			super(context, 0);
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = LayoutInflater.from(getContext())
						.inflate(R.layout.row, parent, false);
			}

			TextView title = (TextView) convertView.findViewById(R.id.row_title);
			title.setText(getItem(position));

			return convertView;
		}

	}

}
