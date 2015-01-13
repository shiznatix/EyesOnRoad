package com.shiznatix.eyesonroad;

import com.shiznatix.eyesonroad.services.widgets.SmallWidgetService;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.util.Log;

public class WidgetSmall extends WidgetMain {
	static final private String LOG_TAG = "er_WidgetSmall";
	
	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
		Log.i(LOG_TAG, "onUpdate");
		
		super.mWidgetServiceClass = SmallWidgetService.class;
		
		super.onUpdate(context, appWidgetManager, appWidgetIds);
	}
}