package com.shiznatix.eyesonroad;

import java.util.Calendar;

import com.shiznatix.eyesonroad.services.WidgetService;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class WidgetMain extends AppWidgetProvider {
	static final private String LOG_TAG = "er_WidgetMain";
	
	private PendingIntent service = null;
	
	protected Class<?> mWidgetServiceClass = WidgetService.class;
	
	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
		Log.i(LOG_TAG, "onUpdate");
		
		super.onUpdate(context, appWidgetManager, appWidgetIds);
		
		final AlarmManager alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
		  
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		
		Intent intent = new Intent(context, mWidgetServiceClass);
		
		if (service == null) {
			service = PendingIntent.getService(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);//FLAG_CANCEL_CURRENT
		}
		
		alarmManager.setRepeating(AlarmManager.RTC, calendar.getTime().getTime(), 5000, service);
	}
	
	@Override
	public void onReceive(Context context, Intent intent) {
		Log.i(LOG_TAG, "onReceive: "+intent.getAction());
		
		super.onReceive(context, intent);
	}
	
	@Override
	public void onDisabled(Context context) {
		Log.i(LOG_TAG, "onDisabled");
		
		final AlarmManager alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);  
		  
		alarmManager.cancel(service);
	}
}