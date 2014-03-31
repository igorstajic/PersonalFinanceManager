package com.personalfinancemanager.activities;

import java.util.HashMap;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
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

	public static final String firebaseURL = "https://mojatestbaza.firebaseio.com/";

	private String currentUserEmail;
	private String currentUserPassword;
	private String currentUserFullName;
	private Fragment currentlyShownFragment = new StartFragment();

	private Firebase firebaseRef = new Firebase(firebaseURL);
	private SimpleLogin firebaseAuthenticationClient = new SimpleLogin(firebaseRef);

	private HashMap<String, String> userList = new HashMap<String, String>();

	// Key is users email, value is users generated firebase id

	public Fragment getCurrentlyShownFragment() {
		return currentlyShownFragment;
	}

	public String getUserFirebaseID() {
		return userList.get(currentUserEmail);
	}

	public HashMap<String, String> getUserList() {
		return userList;
	}

	public String getCurrentUserFullName() {
		return currentUserFullName;
	}

	public void setCurrentUserFullName(String mFullName) {
		this.currentUserFullName = mFullName;
	}

	public String getCurrentUserEmail() {
		return currentUserEmail;
	}

	public void setCurrentUserEmail(String mEmail) {
		this.currentUserEmail = mEmail;
	}

	public String getCurrentUserPassword() {
		return currentUserPassword;
	}

	public void setCurrentUserPassword(String mPassword) {
		this.currentUserPassword = mPassword;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (savedInstanceState != null) {
			currentUserEmail = savedInstanceState.getString("email");
			currentUserFullName = savedInstanceState.getString("fullname");
			userList = (HashMap<String, String>) savedInstanceState.getSerializable("users");
		}

		makeUserList();

		setContentView(R.layout.content_frame);
		setBehindContentView(R.layout.menu_frame);

		// Customize the SlidingMenu.
		SlidingMenu slidingMenu = getSlidingMenu();
		slidingMenu.setFadeDegree(0.65f);
		slidingMenu.setBackgroundColor(Color.LTGRAY);
		slidingMenu.setBehindOffsetRes(R.dimen.slidingmenu_offset);
		slidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
		setSlidingActionBarEnabled(false);
		setupSlidingMenuFragment();

	}

	private void makeUserList() {
		firebaseRef.child("users").addChildEventListener(new ChildEventListener() {

			@Override
			public void onChildAdded(DataSnapshot snapshot, String arg1) {
				AppUser tempUser = snapshot.getValue(AppUser.class);
				userList.put(tempUser.getEmailAddress(), snapshot.getName());
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

	private void setupSlidingMenuFragment() {
		if (currentUserEmail == null) {
			getSupportFragmentManager().beginTransaction()
					.replace(R.id.menu_frame, new SlidingMenuFragment()).commit();
		} else {
			getSupportFragmentManager().beginTransaction()
					.replace(R.id.menu_frame, makeLoggedUserMenu()).commit();
		}
	}
	
	private Fragment makeLoggedUserMenu() {
		Bundle bun = new Bundle();
		bun.putString("fullname", currentUserFullName);
		bun.putString("email", currentUserEmail);

		SlidingMenuFragment retVal = new SlidingMenuFragment();
		retVal.setArguments(bun);
		return retVal;
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
			// TODO: Show about dialog.
			return true;
		case R.id.action_exit:
			firebaseAuthenticationClient.logout();
			finish();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		outState.putString("email", currentUserEmail);
		outState.putString("fullname", currentUserFullName);
		outState.putSerializable("users", userList);
		outState.putString("currentView", currentlyShownFragment.getTag());
		super.onSaveInstanceState(outState);
	}

	@Override
	public void onBackPressed() {
		if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
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

		} else if (currentlyShownFragment instanceof LoadingFragment) {
			return;
		} else {
			super.onBackPressed();
		}
	}

	// Changing fragment that is currently shown.
	public void switchCurrentFragment(Fragment fragment) {
		String fragmentTag = null;
		if (fragment instanceof TransactionsFragment) {
			fragmentTag = "transactions";
		}
		// Loading screen fragment should not be on backstack.
		if ((fragment instanceof LoadingFragment)
				|| (currentlyShownFragment instanceof LoadingFragment)) {
			getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, fragment)
					.commit();

		} else {
			getSupportFragmentManager().beginTransaction()
					.replace(R.id.content_frame, fragment, fragmentTag)
					.addToBackStack(currentlyShownFragment.getTag()).commit();
		}
		currentlyShownFragment = fragment;
		getSlidingMenu().showContent();
	}

	public void attemptLogin() {
		switchCurrentFragment(new LoadingFragment());
		startLogin();
		hideKeyboard();
	}
	
	private void hideKeyboard() {
		InputMethodManager imm = (InputMethodManager) this
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(this.findViewById(android.R.id.content).getWindowToken(),
				InputMethodManager.HIDE_NOT_ALWAYS);
	}
	
	private Boolean startLogin() {
		firebaseAuthenticationClient.loginWithEmail(currentUserEmail, currentUserPassword,
				new SimpleLoginAuthenticatedHandler() {
					public void authenticated(Error error, User user) {
						if (error != null) {
							// There was an error logging into this account.
							Bundle bun = new Bundle();

							bun.putInt("error", error.ordinal());

							LoginFragment newLoginFragment = new LoginFragment();
							newLoginFragment.setArguments(bun);

							switchCurrentFragment(newLoginFragment);

						} else {
							switchCurrentFragment(new StartFragment());
							Toast.makeText(MainActivity.this, "Connected", Toast.LENGTH_SHORT)
									.show();

							firebaseRef.child("users").addChildEventListener(
									new ChildEventListener() {
										@Override
										public void onChildAdded(DataSnapshot snapshot,
												String previousChildName) {
											AppUser temp = snapshot.getValue(AppUser.class);

											if (temp.getEmailAddress().equals(currentUserEmail)) {

												currentUserFullName = temp.getFullName();

												getSupportFragmentManager()
														.beginTransaction()
														.replace(R.id.menu_frame,
																makeLoggedUserMenu()).commit();
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

	public void attempRegister() {
		switchCurrentFragment(new LoadingFragment());
		registerAndLogin();
	}

	private void registerAndLogin() {
		firebaseAuthenticationClient.createUser(currentUserEmail, currentUserPassword,
				new SimpleLoginAuthenticatedHandler() {
					public void authenticated(Error error, User user) {
						if (error != null) {
							// There was an error creating this account.

						} else {

							AppUser newUser = new AppUser(currentUserFullName, currentUserEmail);
							firebaseRef.child("users").push().setValue(newUser);

							Toast.makeText(MainActivity.this, "Register complete!",
									Toast.LENGTH_SHORT).show();
							attemptLogin();
						}
					}
				});

	}

	public void logout(View v) {
		firebaseAuthenticationClient.logout();
		currentUserEmail = null;
		currentUserPassword = null;
		currentUserFullName = null;
		getSupportFragmentManager().beginTransaction()
				.replace(R.id.menu_frame, new SlidingMenuFragment()).commit();
		// Clear backstack.
		getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);

		switchCurrentFragment(new LoginFragment());

	}

}
