package com.personalfinancemanager.fragments;

import java.util.Comparator;
import java.util.Date;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.example.personalfinancemanager.R;
import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.personalfinancemanager.activities.MainActivity;
import com.personalfinancemanager.model.Group;
import com.personalfinancemanager.model.Transaction;
import com.personalfinancemanager.util.TransactionListRowAdapter;

public class GroupFragment extends Fragment {

	private String firebaseGroupID;
	private String ref = MainActivity.firebaseURL;
	Firebase firebaseRef = new Firebase(ref);
	Firebase thisGroupRef;

	Group groupObject;

	MainActivity parentActivity;

	private Button btnExpence;
	private Button btnIncome;
	private Button btnMoreTransactions;
	private TextView tvBalance;
	private TextView tvAmount;
	private ListView lvRecentTransactions;
	private TransactionListRowAdapter transactionListAdapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		parentActivity = (MainActivity) getActivity();

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		container.removeAllViews();// removes fragment overlap bug

		final View formView = inflater.inflate(R.layout.fragment_group, null);

		initRefs(formView);

		firebaseRef.child("groupdata").addChildEventListener(new ChildEventListener() {
			@Override
			public void onChildAdded(DataSnapshot snapshot, String previousChildName) {
				Group temp = snapshot.getValue(Group.class);
				if (temp.getId().equals(getArguments().get("id"))) {
					groupObject = temp;
					tvBalance.setText(groupObject.calculateBalance().toString());
					firebaseGroupID = snapshot.getName();
					thisGroupRef = firebaseRef.child("groupdata").child(firebaseGroupID);
					//Update group balance textView.
					thisGroupRef.addValueEventListener(new ValueEventListener() {
						@Override
						public void onDataChange(DataSnapshot arg0) {
							groupObject = (Group) arg0.getValue(Group.class);
							tvBalance.setText(groupObject.calculateBalance().toString());
						}

						@Override
						public void onCancelled(FirebaseError arg0) {
						}
					});
					
					fillTransactionTable();
					
					btnExpence.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							makeTransaction(-Integer.parseInt(tvAmount.getText().toString()));
						}
					});

					btnIncome.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							makeTransaction(Integer.parseInt(tvAmount.getText().toString()));
						}
					});

					btnMoreTransactions.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							TransactionsFragment newFragment = new TransactionsFragment();
							Bundle info = new Bundle();
							info.putString("groupID", firebaseGroupID);
							newFragment.setArguments(info);
							parentActivity.switchCurrentFragment(newFragment);
						}
					});

				}

			}

			private void fillTransactionTable() {
				thisGroupRef.child("transactions").limit(5)
						.addChildEventListener(new ChildEventListener() {
							@Override
							public void onChildRemoved(DataSnapshot arg0) {
							}

							@Override
							public void onChildMoved(DataSnapshot arg0, String arg1) {
							}

							@Override
							public void onChildChanged(DataSnapshot arg0, String arg1) {
							}

							@Override
							public void onChildAdded(DataSnapshot arg0, String arg1) {

								if (transactionListAdapter.getCount() == 5) {
									transactionListAdapter.remove(transactionListAdapter.getItem(4));
								}
								transactionListAdapter.add((Transaction) arg0
										.getValue(Transaction.class));
								transactionListAdapter.sort(new Comparator<Transaction>() {

									@Override
									public int compare(Transaction moreRecent,
											Transaction lessRecent) {
										// The most recent transaction goes
										// on top of the list.
										if (moreRecent.getDate().getTime() > lessRecent.getDate()
												.getTime()) {
											return -1;
										} else if (moreRecent.getDate().getTime() < lessRecent
												.getDate().getTime()) {
											return 1;
										}
										return 0;
									}
								});
							}

							@Override
							public void onCancelled(FirebaseError arg0) {
							}
						});
			}

			@Override
			public void onCancelled(FirebaseError arg0) {
			}

			@Override
			public void onChildChanged(DataSnapshot arg0, String arg1) {
			}

			@Override
			public void onChildMoved(DataSnapshot arg0, String arg1) {
			}

			@Override
			public void onChildRemoved(DataSnapshot arg0) {
			}
		});

		return formView;
	}

	private void initRefs(View v) {
		btnExpence = (Button) v.findViewById(R.id.btn_add_expence);
		btnIncome = (Button) v.findViewById(R.id.btn_add_income);
		btnMoreTransactions = (Button) v.findViewById(R.id.btn_more_transactions);
		tvBalance = (TextView) v.findViewById(R.id.textview_balance_value);
		tvAmount = (TextView) v.findViewById(R.id.textview_transaction_amount);
		lvRecentTransactions = (ListView) v.findViewById(R.id.lv_recent_transactions);
		transactionListAdapter = new TransactionListRowAdapter(getActivity());
		lvRecentTransactions.setAdapter(transactionListAdapter);
	}

	private void makeTransaction(int amount) {
		if (TextUtils.isEmpty(tvAmount.getText())) {
			tvAmount.setError(getString(R.string.error_field_required));
			tvAmount.requestFocus();
			return;
		}

		Transaction newExpence = new Transaction();
		newExpence.setAmount(amount);
		newExpence.setDate(new Date());
		newExpence.setUserEmail(parentActivity.getCurrentUserEmail());
		thisGroupRef.child("transactions").push()
				.setValue(newExpence, newExpence.getDate().getTime());

	}

}
