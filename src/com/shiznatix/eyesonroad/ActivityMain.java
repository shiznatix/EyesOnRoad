package com.shiznatix.eyesonroad;

import com.shiznatix.eyesonroad.fragments.BluetoothDevicesFragment;
import com.shiznatix.eyesonroad.fragments.PreferencesFragment;
import com.shiznatix.eyesonroad.models.ManagersModel;
import com.shiznatix.eyesonroad.models.PreferencesModel;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ToggleButton;

public class ActivityMain extends Activity {
	static private final String LOG_TAG = "er_ActivityMain";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		Log.i(LOG_TAG, "onCreate");
		
		ActionBar actionBar = getActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		//actionBar.setDisplayShowTitleEnabled(true);
		
		Tab bluetoothDeviceTab = actionBar.newTab();
		bluetoothDeviceTab.setText(getResources().getString(R.string.bluetoothDevice));
		bluetoothDeviceTab.setTabListener(new TabListener<BluetoothDevicesFragment>(this, "bluetoothDevice", BluetoothDevicesFragment.class));
		
		Tab preferencesTab = actionBar.newTab();
		preferencesTab.setText(getResources().getString(R.string.preferences));
		preferencesTab.setTabListener(new TabListener<PreferencesFragment>(this, "preferences", PreferencesFragment.class));
		
		actionBar.addTab(bluetoothDeviceTab);
		actionBar.addTab(preferencesTab);
		
		final PreferencesModel preferencesModel = new PreferencesModel(this);
		
		ToggleButton disableToggleButton = (ToggleButton)findViewById(R.id.disableButton);
		disableToggleButton.setChecked(!preferencesModel.getIsDisabled());
		disableToggleButton.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
				preferencesModel.setIsDisabled(!preferencesModel.getIsDisabled());
				
				Intent intent = new Intent();
				intent.putExtra(ManagersModel.DISABLED_STATE, preferencesModel.getIsDisabled());
				intent.setAction(ManagersModel.ACTION_CHANGED_DISABLED_STATE);
				sendBroadcast(intent);
			}
		});
	}
	
	public static class TabListener<T extends Fragment> implements ActionBar.TabListener {
		private Fragment mFragment;
		private final Activity mActivity;
		private final String mTag;
		private final Class<T> mClass;
		
		public TabListener(Activity activity, String tag, Class<T> clz) {
			mActivity = activity;
			mTag = tag;
			mClass = clz;
		}
		
		public void onTabSelected(Tab tab, FragmentTransaction ft) {
			if (mFragment == null) {
				mFragment = Fragment.instantiate(mActivity, mClass.getName());
				ft.add(R.id.fragmentLayout, mFragment, mTag);
			}
			else {
				ft.attach(mFragment);
			}
		}
		
		public void onTabUnselected(Tab tab, FragmentTransaction ft) {
			if (mFragment != null) {
				ft.detach(mFragment);
			}
		}
		
		public void onTabReselected(Tab tab, FragmentTransaction ft) {
		}
	}
}
