package com.shiznatix.eyesonroad.models;

import android.content.Context;
import android.content.SharedPreferences;

public class PreferencesModel {
	static public final String PREF_FILE_NAME = "EyesOnRoadPreferences";
	
	protected SharedPreferences mSharedPreferences;
	
	public PreferencesModel(Context context) {
		mSharedPreferences = context.getSharedPreferences(PREF_FILE_NAME, 0);
	}
	
	public int getCarBluetoothHash() {
		return mSharedPreferences.getInt("bluetoothHash", 0);
	}
	
	public void setCarBluetoothHash(int hashCode) {
		SharedPreferences.Editor editor = mSharedPreferences.edit();
		editor.putInt("bluetoothHash", hashCode);
		editor.commit();
	}
	
	public boolean getManageWifi() {
		return mSharedPreferences.getBoolean("manageWifi", true);
	}
	
	public void setManageWifi(boolean manageWifi) {
		SharedPreferences.Editor editor = mSharedPreferences.edit();
		editor.putBoolean("manageWifi", manageWifi);
		editor.commit();
	}
	
	public boolean getManageMobileData() {
		return mSharedPreferences.getBoolean("manageMobileData", true);
	}
	
	public void setManageMobileData(boolean manageMobileData) {
		SharedPreferences.Editor editor = mSharedPreferences.edit();
		editor.putBoolean("manageMobileData", manageMobileData);
		editor.commit();
	}
	
	public boolean getManageAudio() {
		return mSharedPreferences.getBoolean("manageAudio", true);
	}
	
	public void setManageAudio(boolean manageSounds) {
		SharedPreferences.Editor editor = mSharedPreferences.edit();
		editor.putBoolean("manageAudio", manageSounds);
		editor.commit();
	}
	
	public boolean getIsDisabled() {
		return mSharedPreferences.getBoolean("isDisabled", false);
	}
	
	public void setIsDisabled(boolean isDisabled) {
		SharedPreferences.Editor editor = mSharedPreferences.edit();
		editor.putBoolean("isDisabled", isDisabled);
		editor.commit();
	}
}