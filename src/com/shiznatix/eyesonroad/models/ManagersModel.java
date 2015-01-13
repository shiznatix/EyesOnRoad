package com.shiznatix.eyesonroad.models;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import com.shiznatix.eyesonroad.services.WidgetService;
import com.shiznatix.eyesonroad.services.widgets.LargeWidgetService;
import com.shiznatix.eyesonroad.services.widgets.SmallWidgetService;

import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.util.Log;

public class ManagersModel {
	static final private String LOG_TAG = "er_ManagersModel";
	
	static final public String ACTION_CAR_DEVICE_HASH_UPDATED = "com.shiznatix.eyesonroad.deviceidupdated";
	static final public String ACTION_CAR_MANAGER_UPDATED = "com.shiznatix.eyesonroad.managerupdated";
	static final public String ACTION_CHANGED_DISABLED_STATE = "com.shiznatix.eyesonroad.changeddisabledstate";
	
	static final public String CAR_DEVICE_HASH = "carDeviceHash";
	static final public String CAR_MANAGER_NAME = "carManagerName";
	static final public String DISABLED_STATE = "disabledState";
	
	static final public String MANAGER_WIFI = "wifi";
	static final public String MANAGER_MOBILE_DATA = "mobileData";
	static final public String MANAGER_AUDIO = "audio";
	
	protected PreferencesModel mPreferencesModel;
	
	static public boolean CONNECTED_TO_CAR = false;
	
	protected int mOriginalAudioState = AudioManager.RINGER_MODE_NORMAL;
	protected boolean mOriginalWifiState = true;
	protected boolean mOriginalMobileDataState = true;
	
	protected AudioManager mAudioManager;
	protected WifiManager mWifiManager;
	protected Object mConnectivityManager;
	protected Method mSetMobileDataEnabledMethod;
	
	protected List<Integer> mConnectedDevices = new ArrayList<Integer>();
	
	private final BroadcastReceiver mCarDeviceHashUpdated = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			int newHashCode = intent.getIntExtra(CAR_DEVICE_HASH, 0);
			
			if (0 == newHashCode) {
				Log.i(LOG_TAG, "New hash is 0");
				return;
			}
			
			Log.i(LOG_TAG, "New car hash: "+newHashCode);
			
			Integer hashCode = intToInteger(newHashCode);
			
			if (mConnectedDevices.contains(hashCode)) {
				try {
					requireCarDeviceHash(newHashCode);
					enableManagers();
				}
				catch (Exception e) {
					Log.i(LOG_TAG, e.getMessage());
				}
			}
			else if (CONNECTED_TO_CAR) {
				disableManagers();
			}
		}
	};
	
	private final BroadcastReceiver mCarManagerUpdated = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String manager = intent.getStringExtra(CAR_MANAGER_NAME);
			String widget = intent.getStringExtra(WidgetService.WIDGET_NAME);
			
			if (null == manager || null == widget) {
				Log.i(LOG_TAG, "Error: manager: "+manager+" widget: "+widget);
				return;
			}
			
			Log.i(LOG_TAG, "Manager updated: "+manager);
			
			if (manager.equals(MANAGER_WIFI)) {
				mPreferencesModel.setManageWifi(!mPreferencesModel.getManageWifi());
			}
			else if (manager.equals(MANAGER_MOBILE_DATA)) {
				mPreferencesModel.setManageMobileData(!mPreferencesModel.getManageMobileData());
			}
			else if (manager.equals(MANAGER_AUDIO)) {
				mPreferencesModel.setManageAudio(!mPreferencesModel.getManageAudio());
			}
			
			Intent updateWidgetIntent = null;
			
			if (WidgetService.LARGE_WIDGET_NAME.equals(widget)) {
				updateWidgetIntent = new Intent(context, LargeWidgetService.class);
			}
			else if (WidgetService.SMALL_WIDGET_NAME.equals(widget)) {
				updateWidgetIntent = new Intent(context, SmallWidgetService.class);
			}
			
			if (null != updateWidgetIntent) {
				context.startService(updateWidgetIntent);
			}
		}
	};
	
	private final BroadcastReceiver mDisabledStateChanged = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			boolean isDisabled = intent.getBooleanExtra(DISABLED_STATE, false);
			
			if (isDisabled && CONNECTED_TO_CAR) {
				disableManagers(false);
			}
		}
	};
	
	private final BroadcastReceiver mConnectReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			Log.i(LOG_TAG, "Bluetooth device connected");
			
			BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
			
			if (null == device) {
				Log.i(LOG_TAG, "device is null");
				return;
			}
			
			Integer hashCode = intToInteger(device.hashCode());
			
			try {
				if (!mConnectedDevices.contains(hashCode)) {
					mConnectedDevices.add(hashCode);
				}
				
				requireCarDeviceHash(hashCode);
				enableManagers();
			}
			catch (Exception e) {
				e.printStackTrace();
				
				Log.i(LOG_TAG, e.getMessage());
			}
		}
	};
	
	private final BroadcastReceiver mDisconnectReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			Log.i(LOG_TAG, "Bluetooth device disconnected");
			
			BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
			
			if (null == device) {
				Log.i(LOG_TAG, "device is null");
				return;
			}
			
			Integer hashCode = intToInteger(device.hashCode());
			
			try {
				if (mConnectedDevices.contains(hashCode)) {
					mConnectedDevices.remove(hashCode);
				}
				
				requireCarDeviceHash(hashCode);
				disableManagers();
			}
			catch (Exception e) {
				e.printStackTrace();
				
				Log.i(LOG_TAG, e.getMessage());
			}
		}
	};
	
	public ManagersModel(Context context) {
		Log.i(LOG_TAG, "ManagersModel construct");
		
		mPreferencesModel = new PreferencesModel(context);
		
		mAudioManager = (AudioManager)context.getSystemService(Context.AUDIO_SERVICE);
		mOriginalAudioState = mAudioManager.getRingerMode();
		
		mWifiManager = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);
		mOriginalWifiState = mWifiManager.isWifiEnabled();
		
		try {
			ConnectivityManager cm = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
			
			Field connectivityManagerField = Class.forName(cm.getClass().getName()).getDeclaredField("mService");
			connectivityManagerField.setAccessible(true);
			
			mConnectivityManager = connectivityManagerField.get(cm);
			
			Class<?> connectivityManagerClass = Class.forName(mConnectivityManager.getClass().getName());
			
			mSetMobileDataEnabledMethod = connectivityManagerClass.getDeclaredMethod("setMobileDataEnabled", Boolean.TYPE);
			mSetMobileDataEnabledMethod.setAccessible(true);
			
			Method getMobileDataEnabledMethod = connectivityManagerClass.getDeclaredMethod("getMobileDataEnabled");
			getMobileDataEnabledMethod.setAccessible(true);
			mOriginalMobileDataState = (boolean)getMobileDataEnabledMethod.invoke(mConnectivityManager);
		}
		catch (IllegalAccessException | IllegalArgumentException | ClassNotFoundException | NoSuchFieldException | NoSuchMethodException | InvocationTargetException e) {
			Log.e(LOG_TAG, "Toggle mobile data error: "+e.getMessage());
			
			e.printStackTrace();
		}
		
		context.registerReceiver(mConnectReceiver, new IntentFilter(BluetoothDevice.ACTION_ACL_CONNECTED));
		context.registerReceiver(mDisconnectReceiver, new IntentFilter(BluetoothDevice.ACTION_ACL_DISCONNECTED));
		context.registerReceiver(mCarDeviceHashUpdated, new IntentFilter(ACTION_CAR_DEVICE_HASH_UPDATED));
		context.registerReceiver(mCarManagerUpdated, new IntentFilter(ACTION_CAR_MANAGER_UPDATED));
		context.registerReceiver(mDisabledStateChanged, new IntentFilter(ACTION_CHANGED_DISABLED_STATE));
	}
	
	public void requireCarDeviceHash(int hash) throws Exception {
		if (hash != mPreferencesModel.getCarBluetoothHash()) {
			throw new Exception("Invalid device hash code");
		}
	}
	
	public void setAudioState(int state) {
		if (mPreferencesModel.getManageAudio()) {
			mAudioManager.setRingerMode(state);
		}
	}
	
	public void setAudioState() {
		setAudioState(mOriginalAudioState);
	}
	
	public void setWifiState(boolean state) {
		if (mPreferencesModel.getManageWifi()) {
			mWifiManager.setWifiEnabled(state);
		}
	}
	
	public void setWifiState() {
		setWifiState(mOriginalWifiState);
	}
	
	public void setMobileDataState(boolean state) {
		if (mPreferencesModel.getManageMobileData()) {
			try {
				mSetMobileDataEnabledMethod.invoke(mConnectivityManager, state);
			}
			catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void setMobileDataState() {
		setMobileDataState(mOriginalMobileDataState);
	}
	
	protected void disableManagers(boolean checkIsDisabled) {
		Log.i(LOG_TAG, "disableManagers");
		
		if (checkIsDisabled && mPreferencesModel.getIsDisabled()) {
			return;
		}
		
		CONNECTED_TO_CAR = false;
		
		setAudioState();
		setWifiState();
		setMobileDataState();
	}
	
	protected void disableManagers() {
		disableManagers(true);
	}
	
	protected void enableManagers(boolean checkIsDisabled) {
		Log.i(LOG_TAG, "enableManagers");
		
		if (checkIsDisabled && mPreferencesModel.getIsDisabled()) {
			return;
		}
		
		CONNECTED_TO_CAR = true;
		
		setAudioState(AudioManager.RINGER_MODE_SILENT);
		setWifiState(false);
		setMobileDataState(false);
	}
	
	protected void enableManagers() {
		enableManagers(true);
	}
	
	public boolean getIsGlobalDisabled() {
		return mPreferencesModel.getIsDisabled();
	}
	
	public boolean getShouldShutoffAudio() {
		return mPreferencesModel.getManageAudio();
	}
	
	public boolean getShouldShutoffWifi() {
		return mPreferencesModel.getManageWifi();
	}
	
	public boolean getShouldShutoffMobileData() {
		return mPreferencesModel.getManageMobileData();
	}
	
	protected Integer intToInteger(int rawInt) {
		return Integer.valueOf(rawInt);
	}
}