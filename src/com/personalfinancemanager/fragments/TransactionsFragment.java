package com.personalfinancemanager.fragments;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.TextView;

import com.example.personalfinancemanager.R;
import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.personalfinancemanager.activities.MainActivity;
import com.personalfinancemanager.model.Transaction;
import com.personalfinancemanager.util.TransactionListRowAdapter;

public class TransactionsFragment extends Fragment {

	private String ref = MainActivity.firebaseURL;
	Firebase fbRef = new Firebase(ref);
	Firebase thisGroupRef;
	private TransactionListRowAdapter adapter;

	MainActivity parentActivity;

	private Button btnFromDate;
	private Button btnUntilDate;
	private Button btnApplyFilter;
	private TextView tvFromDate;
	private TextView tvUntilDate;
	private ListView lvFilteredTransactions;

	long parsedFromDate = 0;
	long parsedUntilDate = 0;

	public TextView getTvFromDate() {
		return tvFromDate;
	}

	public void setTvFromDate(TextView tvFromDate) {
		this.tvFromDate = tvFromDate;
	}

	public TextView getTvUntilDate() {
		return tvUntilDate;
	}

	public void setTvUntilDate(TextView tvUntilDate) {
		this.tvUntilDate = tvUntilDate;
	}

	private void initRefs(View v) {
		btnFromDate = (Button) v.findViewById(R.id.btn_from_date);
		btnUntilDate = (Button) v.findViewById(R.id.btn_until_date);
		btnApplyFilter = (Button) v.findViewById(R.id.btn_apply_filter);
		tvFromDate = (TextView) v.findViewById(R.id.label_from_date);
		tvUntilDate = (TextView) v.findViewById(R.id.label_until_date);
		lvFilteredTransactions = (ListView) v
				.findViewById(R.id.lv_filtered_transactions);
		adapter = new TransactionListRowAdapter(getActivity());
		lvFilteredTransactions.setAdapter(adapter);

		thisGroupRef = fbRef.child("groupdata").child(
				getArguments().getString("groupID"));
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		parentActivity = (MainActivity) getActivity();

	}

	
	
	@Override
	public void onSaveInstanceState(Bundle outState) {
		// TODO Auto-generated method stub
		outState.putString("fromDate", tvFromDate.getText().toString());
		outState.putString("untilDate", tvUntilDate.getText().toString());
		super.onSaveInstanceState(outState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		container.removeAllViews();
		final View formView = inflater.inflate(R.layout.fragment_transactions,
				null);

		initRefs(formView);

		SimpleDateFormat labelDateFormat = new SimpleDateFormat("dd-MM-yyyy");
		String currentDate = labelDateFormat.format(new Date());
		if (savedInstanceState == null){
		tvFromDate.setText(currentDate);
		tvUntilDate.setText(currentDate);
		} else {
			tvFromDate.setText(savedInstanceState.getString("fromDate"));
			tvUntilDate.setText(savedInstanceState.getString("untilDate"));
		}
		parseDate();
		fillTable();

		btnApplyFilter.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				parseDate();
				adapter.clear();
				fillTable();
			}
		});

		btnFromDate.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				DialogFragment newFragment = new DatePickerFragment();
				Bundle bun = new Bundle();

				newFragment.show(parentActivity.getSupportFragmentManager(),
						"fromDate");
			}
		});

		btnUntilDate.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				DialogFragment newFragment = new DatePickerFragment();
				newFragment.show(parentActivity.getSupportFragmentManager(),
						"untilDate");
			}
		});

		return formView;
	}

	private void fillTable() {
		thisGroupRef.child("transactions").startAt(parsedFromDate)
				.endAt(parsedUntilDate)
				.addChildEventListener(new ChildEventListener() {

					@Override
					public void onChildRemoved(DataSnapshot arg0) {
						// TODO Auto-generated method stub

					}

					@Override
					public void onChildMoved(DataSnapshot arg0, String arg1) {
						// TODO Auto-generated method stub

					}

					@Override
					public void onChildChanged(DataSnapshot arg0, String arg1) {
						// TODO Auto-generated method stub

					}

					@Override
					public void onChildAdded(DataSnapshot arg0, String arg1) {
						// TODO Auto-generated method stub
						adapter.add((Transaction) arg0
								.getValue(Transaction.class));
						adapter.sort(new Comparator<Transaction>() {

							@Override
							public int compare(Transaction lhs, Transaction rhs) {
								// TODO Auto-generated method stub
								if (lhs.getDate().getTime() > rhs.getDate()
										.getTime()) {
									return -1;
								} else if (lhs.getDate().getTime() < rhs
										.getDate().getTime()) {
									return 1;
								}
								return 0;
							}
						});
					}

					@Override
					public void onCancelled(FirebaseError arg0) {
						// TODO Auto-generated method stub

					}
				});
	}

	private void parseDate() {
		try {

			SimpleDateFormat queryDateFormat = new SimpleDateFormat(
					"dd-MM-yyyy HH:mm");

			parsedFromDate = queryDateFormat.parse(
					tvFromDate.getText().toString() + " 00:00").getTime();

			parsedUntilDate = queryDateFormat.parse(
					tvUntilDate.getText().toString() + " 23:59").getTime();

		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static class DatePickerFragment extends DialogFragment implements
			DatePickerDialog.OnDateSetListener {

		public DatePickerFragment() {
		}

		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			// Use the current date as the default date in the picker
			final Calendar c = Calendar.getInstance();
			int year = c.get(Calendar.YEAR);
			int month = c.get(Calendar.MONTH);
			int day = c.get(Calendar.DAY_OF_MONTH);

			// Create a new instance of DatePickerDialog and return it
			return new DatePickerDialog(getActivity(), this, year, month, day);
		}

		public void onDateSet(DatePicker view, int year, int month, int day) {
			// Do something with the date chosen by the user

			month++;
			TransactionsFragment parent = (TransactionsFragment) getActivity()
					.getSupportFragmentManager().findFragmentByTag(
							"transactions");

			if (getTag().equals("fromDate")) {
				parent.getTvFromDate().setText(day + "-" + month + "-" + year);
			} else {
				parent.getTvUntilDate().setText(day + "-" + month + "-" + year);
			}
		}
	}

}
