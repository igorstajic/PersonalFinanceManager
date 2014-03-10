package com.personalfinancemanager.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.personalfinancemanager.R;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.firebase.simplelogin.SimpleLogin;
import com.personalfinancemanager.activities.MainActivity;
import com.personalfinancemanager.model.AppUser;
import com.personalfinancemanager.model.Group;

public class StartFragment extends Fragment{

	
	
	
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
		View thisView = inflater.inflate(R.layout.fragment_start, null);
		container.removeAllViews();
	
		
		return thisView;
	}
}
