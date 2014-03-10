package com.personalfinancemanager.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.personalfinancemanager.R;

public class LoadingFragment extends Fragment{

	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
		container.removeAllViews();
		return inflater.inflate(R.layout.fragment_loading, null);
	}
}
