package com.personalfinancemanager.util;

import java.text.SimpleDateFormat;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.personalfinancemanager.R;
import com.personalfinancemanager.model.Transaction;

public class TransactionListRowAdapter extends ArrayAdapter<Transaction> {

	public TransactionListRowAdapter(Context context) {
		super(context, 0);

	}

	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {

			convertView = LayoutInflater.from(getContext()).inflate(
					R.layout.transaction_table_row, null);
		}

		TextView amount = (TextView) convertView
				.findViewById(R.id.tv_transaction_amount);
		TextView date = (TextView) convertView
				.findViewById(R.id.tv_transaction_date);
		TextView user = (TextView) convertView
				.findViewById(R.id.tv_transaction_user);

		SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm");
		amount.setText(getItem(position).getAmount().toString());
		date.setText(sdf.format(getItem(position).getDate()));
		user.setText(getItem(position).getUserEmail());

		return convertView;
	}
}

