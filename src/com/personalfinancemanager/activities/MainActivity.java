package com.personalfinancemanager.activities;

import java.util.HashMap;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.AttributeSet;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.example.personalfinancemanager.R;
import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.simplelogin.SimpleLogin;
import com.firebase.simplelogin.SimpleLoginAuthenticatedHandler;
import com.firebase.simplelogin.User;
import com.firebase.simplelogin.enums.Error;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.jeremyfeinstein.slidingmenu.lib.app.SlidingFragmentActivity;
import com.personalfinancemanager.fragments.LoadingFragment;
import com.personalfinancemanager.fragments.LoginFragment;
import com.personalfinancemanager.fragments.SlidingMenuFragment;
import com.personalfinancemanager.fragments.StartFragment;
import com.personalfinancemanager.fragments.TransactionsFragment;
import com.personalfinancemanager.model.AppUser;

public class MainActivity extends SlidingFragmentActivity {

	public static final String firebaseRef = "https://mojatestbaza.firebaseio.com/";

	private String mEmail;
	private String mPassword;
	private String mFullName;

	private Fragment mContent = new StartFragment(); // currently shown fragment
	private Firebase ref = new Firebase(firebaseRef);
	private SimpleLogin authClient = new SimpleLogin(ref);

	private HashMap<String, String> userList = new HashMap<String, String>();

	public Fragment getmContent() {
		return mContent;
	}

	public String getUserFirebaseID() {
		return userList.get(mEmail);
	}

	public HashMap<String, String> getUserList() {
		return userList;
	}

	public String getmFullName() {
		return mFullName;
	}

	public void setmFullName(String mFullName) {
		this.mFullName = mFullName;
	}

	public String getmEmail() {
		return mEmail;
	}

	public void setmEmail(String mEmail) {
		this.mEmail = mEmail;
	}

	public String getmPassword() {
		return mPassword;
	}

	public void setmPassword(String mPassword) {
		this.mPassword = mPassword;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (savedInstanceState != null) {
			mEmail = savedInstanceState.getString("email");
			mFullName = savedInstanceState.getString("fullname");
			userList = (HashMap<String, String>) savedInstanceState.getSerializable("users");
		}

		setBehindContentView(R.layout.menu_frame);

		// Customize the SlidingMenu.
		SlidingMenu sm = getSlidingMenu();
		sm.setFadeDegree(0.65f);
		sm.setBackgroundColor(Color.LTGRAY);
		sm.setBehindOffsetRes(R.dimen.slidingmenu_offset);
		sm.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
		setSlidingActionBarEnabled(false);

		setContentView(R.layout.content_frame);

		// Sliding menu setup.
		if (mEmail == null) {
			getSupportFragmentManager().beginTransaction()
					.replace(R.id.menu_frame, new SlidingMenuFragment()).commit();
		} else {
			getSupportFragmentManager().beginTransaction()
					.replace(R.id.menu_frame, makeLoggedUserMenu()).commit();
		}

		// Fill user list.
		ref.child("users").addChildEventListener(new ChildEventListener() {

			@Override
			public void onChildAdded(DataSnapshot snapshot, String arg1) {

				AppUser temp = snapshot.getValue(AppUser.class);
				userList.put(temp.getEmailAddress(), snapshot.getName());
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

	}

	@Override
	public View onCreateView(View parent, String name, Context context, AttributeSet attrs) {
		// showMenu();
		return super.onCreateView(parent, name, context, attrs);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle item selection.
		switch (item.getItemId()) {
		case R.id.action_about:
			// Show about dialog.
			return true;
		case R.id.action_exit:
			authClient.logout();
			finish();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		outState.putString("email", mEmail);
		outState.putString("fullname", mFullName);
		outState.putSerializable("users", userList);
		outState.putString("currentView", mContent.getTag());

		super.onSaveInstanceState(outState);
	}

	@Override
	public void onBackPressed() {

		if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			// Add the buttons.
			builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					// User clicked OK button.
					finish();
				}
			});
			builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					// User cancelled the dialog.
				}
			});
			// Set other dialog properties.
			builder.setTitle("Exit?");

			// Create the AlertDialog.
			AlertDialog dialog = builder.create();
			dialog.show();

		} else if (mContent instanceof LoadingFragment) {
			return;
		} else {
			super.onBackPressed();
		}
	}

	// Changing fragment that is currently shown.
	public void switchContent(Fragment fragment) {
		String fragmentTag = null;
		if (fragment instanceof TransactionsFragment) {
			fragmentTag = "transactions";
		}
		// Loading screen fragment should not be on backstack.
		if ((fragment instanceof LoadingFragment) || (mContent instanceof LoadingFragment)) {
			getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, fragment)
					.commit();

		} else {
			getSupportFragmentManager().beginTransaction()
					.replace(R.id.content_frame, fragment, fragmentTag)
					.addToBackStack(mContent.getTag()).commit();
		}
		mContent = fragment;
		getSlidingMenu().showContent();
	}

	public void attemptLogin() {
		switchContent(new LoadingFragment());
		startLogin();
		hideKeyboard();
	}

	public void registerAndLogin() {
		switchContent(new LoadingFragment());
		registerUser();
	}

	private Fragment makeLoggedUserMenu() {
		Bundle bun = new Bundle();
		bun.putString("fullname", mFullName);
		bun.putString("email", mEmail);

		SlidingMenuFragment retVal = new SlidingMenuFragment();
		retVal.setArguments(bun);
		return retVal;
	}

	private void hideKeyboard() {
		InputMethodManager imm = (InputMethodManager) this
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(this.findViewById(android.R.id.content).getWindowToken(),
				InputMethodManager.HIDE_NOT_ALWAYS);
	}

	private Boolean startLogin() {
		authClient.loginWithEmail(mEmail, mPassword, new SimpleLoginAuthenticatedHandler() {
			public void authenticated(Error error, User user) {
				if (error != null) {
					// There was an error logging into this account.
					Bundle bun = new Bundle();

					bun.putInt("error", error.ordinal());

					LoginFragment newLoginFragment = new LoginFragment();
					newLoginFragment.setArguments(bun);

					switchContent(newLoginFragment);

				} else {
					switchContent(new StartFragment());
					Toast.makeText(MainActivity.this, "Connected", Toast.LENGTH_SHORT).show();

					ref.child("users").addChildEventListener(new ChildEventListener() {
						@Override
						public void onChildAdded(DataSnapshot snapshot, String previousChildName) {
							AppUser temp = snapshot.getValue(AppUser.class);

							if (temp.getEmailAddress().equals(mEmail)) {

								mFullName = temp.getFullName();

								getSupportFragmentManager().beginTransaction()
										.replace(R.id.menu_frame, makeLoggedUserMenu()).commit();
							}
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

				}
			}
		});

		return true;

	}

	// Register new user and log him in.
	private void registerUser() {
		authClient.createUser(mEmail, mPassword, new SimpleLoginAuthenticatedHandler() {
			public void authenticated(Error error, User user) {
				if (error != null) {
					// There was an error creating this account.

				} else {

					AppUser newUser = new AppUser(mFullName, mEmail);
					ref.child("users").push().setValue(newUser);

					Toast.makeText(MainActivity.this, "Register complete!", Toast.LENGTH_SHORT)
							.show();
					attemptLogin();
				}
			}
		});

	}

	public void logout(View v) {
		authClient.logout();
		mEmail = null;
		mPassword = null;
		mFullName = null;
		getSupportFragmentManager().beginTransaction()
				.replace(R.id.menu_frame, new SlidingMenuFragment()).commit();
		// Clear backstack.
		getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);

		switchContent(new LoginFragment());

	}

}
