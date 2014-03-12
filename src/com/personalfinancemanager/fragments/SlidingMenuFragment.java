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
import com.personalfinancemanager.model.AppUser;

public class SlidingMenuFragment extends android.support.v4.app.ListFragment {

	
	private MainActivity menuActivity;

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		menuActivity = (MainActivity) getActivity();

		if (getArguments() != null) {
			
			View thisView = inflater.inflate(R.layout.fragment_menu_logged_user, null);
			TextView textviewEmail = (TextView)thisView.findViewById(R.id.tv_menu_email);
			textviewEmail.setText(getArguments().getString("email"));
			TextView textviewFullName = (TextView)thisView.findViewById(R.id.tv_menu_fullname);
			textviewFullName.setText(getArguments().getString("fullname"));
			return thisView;
		}

		return inflater.inflate(R.layout.list, null);
	}

	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		SlidingMenuRowAdapter adapter = new SlidingMenuRowAdapter(getActivity());

		if (getArguments() != null) {
			adapter.add(getString(R.string.my_groups));
		} else {
			adapter.add(getString(R.string.login));
			adapter.add(getString(R.string.register));
		}

		setListAdapter(adapter);
	}

	@Override
	public void onListItemClick(ListView lv, View v, int position, long id) {
		Fragment newContent = null;
		if (getArguments() != null) {
			switch (position) {
			case 0:
				newContent = new MyGroupsFragment();
				break;
			case 1:
				
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
			switchFragment(newContent);
	}

	// the meat of switching the above fragment
	private void switchFragment(Fragment fragment) {
		if (getActivity() == null)
			return;

		MainActivity fca = (MainActivity) getActivity();
		fca.switchCurrentFragment(fragment);

	}

	public class SlidingMenuRowAdapter extends ArrayAdapter<String> {

		public SlidingMenuRowAdapter(Context context) {
			super(context, 0);

		}

		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {

				convertView = LayoutInflater.from(getContext()).inflate(
						R.layout.row, parent, false);
			}

			TextView title = (TextView) convertView
					.findViewById(R.id.row_title);
			title.setText(getItem(position));

			return convertView;
		}

	}

}
