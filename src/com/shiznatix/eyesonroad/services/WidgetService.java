package com.shiznatix.eyesonroad.services;

import com.shiznatix.eyesonroad.models.ManagersModel;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class WidgetService extends Service {
	static final private String LOG_TAG = "er_WidgetService";
	
	static final public String WIDGET_NAME = "widget";
	static final public String LARGE_WIDGET_NAME = "largeWidget";
	static final public String SMALL_WIDGET_NAME = "smallWidget";
	
	protected ManagersModel mManagersModel;
	
	@Override  
	public void onCreate() {
		super.onCreate();
		
		Log.i(LOG_TAG, "onCreate");
		
		mManagersModel = new ManagersModel(getApplicationContext());
	}
	
	@Override  
	public IBinder onBind(Intent intent) {
		return null;
	}
}