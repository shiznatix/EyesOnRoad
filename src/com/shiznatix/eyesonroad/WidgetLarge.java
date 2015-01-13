package com.shiznatix.eyesonroad;

import com.shiznatix.eyesonroad.services.widgets.LargeWidgetService;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.util.Log;


public class WidgetLarge extends WidgetMain {
	static final private String LOG_TAG = "er_WidgetLarge";
	
	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
		Log.i(LOG_TAG, "onUpdate");
		
		super.mWidgetServiceClass = LargeWidgetService.class;
		
		super.onUpdate(context, appWidgetManager, appWidgetIds);
	}
}