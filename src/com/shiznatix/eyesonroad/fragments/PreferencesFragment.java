package com.shiznatix.eyesonroad.fragments;

import com.shiznatix.eyesonroad.R;
import com.shiznatix.eyesonroad.models.PreferencesModel;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ToggleButton;

public class PreferencesFragment extends Fragment {
	static private final String LOG_TAG = "er_PreferencesFragment";
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_preferences, container, false);
		
		Log.i(LOG_TAG, "onCreateView");
		
		final PreferencesModel preferencesModel = new PreferencesModel(getActivity());
		
		ToggleButton toggleWifi = (ToggleButton)view.findViewById(R.id.toggleWifi);
		toggleWifi.setChecked(preferencesModel.getManageWifi());
		toggleWifi.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton button, boolean checked) {
				preferencesModel.setManageWifi(checked);
			}
		});
		
		ToggleButton toggleMobileData = (ToggleButton)view.findViewById(R.id.toggleMobileData);
		toggleMobileData.setChecked(preferencesModel.getManageMobileData());
		toggleMobileData.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton button, boolean checked) {
				preferencesModel.setManageMobileData(checked);
			}
		});
		
		ToggleButton toggleSounds = (ToggleButton)view.findViewById(R.id.toggleSounds);
		toggleSounds.setChecked(preferencesModel.getManageAudio());
		toggleSounds.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton button, boolean checked) {
				preferencesModel.setManageAudio(checked);
			}
		});
		
		return view;
	}
}