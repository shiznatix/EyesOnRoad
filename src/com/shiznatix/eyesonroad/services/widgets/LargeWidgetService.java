package com.shiznatix.eyesonroad.services.widgets;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.bluetooth.BluetoothAdapter;
import android.content.ComponentName;
import android.content.Intent;
import android.widget.RemoteViews;

import com.shiznatix.eyesonroad.ActivityMain;
import com.shiznatix.eyesonroad.R;
import com.shiznatix.eyesonroad.WidgetLarge;
import com.shiznatix.eyesonroad.models.ManagersModel;
import com.shiznatix.eyesonroad.services.WidgetService;

public class LargeWidgetService extends WidgetService {
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		/* Actually, it doesn't really matter for this view if no bluetooth exists...*/
		BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		
		if (bluetoothAdapter == null) {
			return super.onStartCommand(intent, flags, startId);//no bluetooth at all? no hope!
		}
		
		RemoteViews views = new RemoteViews(getPackageName(), R.layout.widget_large);
		
		int wifiIntentCode = 1;
		int mobileDataIntentCode = 2;
		int audioIntentCode = 3;
		
		//connection status
		int connectionImage = (ManagersModel.CONNECTED_TO_CAR ? R.drawable.connected : R.drawable.not_connected);
		
		Intent onClickIntent = new Intent(this, ActivityMain.class);
		PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, onClickIntent, 0);
		
		String connectedText = null;
		
		if (mManagersModel.getIsGlobalDisabled()) {
			connectedText = getApplicationContext().getString(R.string.disabled);
		}
		else if (ManagersModel.CONNECTED_TO_CAR) {
			connectedText = getApplicationContext().getString(R.string.connected);
		}
		
		views.setImageViewResource(R.id.connectionStatusImageButton, connectionImage);
		views.setTextViewText(R.id.connectionStatusTextView, connectedText);
		views.setOnClickPendingIntent(R.id.connectionStatusImageButton, pendingIntent);
		
		//wifi status
		int wifiImage = (mManagersModel.getShouldShutoffWifi() ? R.drawable.wifi_on : R.drawable.wifi_off);
		
		Intent onWifiClickIntent = new Intent();
		onWifiClickIntent.setAction(ManagersModel.ACTION_CAR_MANAGER_UPDATED);
		onWifiClickIntent.putExtra(ManagersModel.CAR_MANAGER_NAME, ManagersModel.MANAGER_WIFI);
		onWifiClickIntent.putExtra(WidgetService.WIDGET_NAME, WidgetService.LARGE_WIDGET_NAME);
		PendingIntent wifiPendingIntent = PendingIntent.getBroadcast(this, wifiIntentCode, onWifiClickIntent, 0);
		
		views.setImageViewResource(R.id.wifiStatusImageButton, wifiImage);
		views.setTextViewText(R.id.wifiStatusTextView, (mManagersModel.getShouldShutoffWifi() ? getApplicationContext().getString(R.string.managed) : null));
		views.setOnClickPendingIntent(R.id.wifiStatusImageButton, wifiPendingIntent);
		
		//mobile data status
		int mobileDataImage = (mManagersModel.getShouldShutoffMobileData() ? R.drawable.mobile_data_on : R.drawable.mobile_data_off);
		
		Intent onMobileDataClickIntent = new Intent();
		onMobileDataClickIntent.setAction(ManagersModel.ACTION_CAR_MANAGER_UPDATED);
		onMobileDataClickIntent.putExtra(ManagersModel.CAR_MANAGER_NAME, ManagersModel.MANAGER_MOBILE_DATA);
		onMobileDataClickIntent.putExtra(WidgetService.WIDGET_NAME, WidgetService.LARGE_WIDGET_NAME);
		PendingIntent mobileDataPendingIntent = PendingIntent.getBroadcast(this, mobileDataIntentCode, onMobileDataClickIntent, 0);
		
		views.setImageViewResource(R.id.mobileDataStatusImageButton, mobileDataImage);
		views.setTextViewText(R.id.mobileDataStatusTextView, (mManagersModel.getShouldShutoffMobileData() ? getApplicationContext().getString(R.string.managed) : null));
		views.setOnClickPendingIntent(R.id.mobileDataStatusImageButton, mobileDataPendingIntent);
		
		//sounds status
		int soundImage = (mManagersModel.getShouldShutoffAudio() ? R.drawable.sound_on : R.drawable.sound_off);
		
		Intent onAudioClickIntent = new Intent();
		onAudioClickIntent.setAction(ManagersModel.ACTION_CAR_MANAGER_UPDATED);
		onAudioClickIntent.putExtra(ManagersModel.CAR_MANAGER_NAME, ManagersModel.MANAGER_AUDIO);
		onAudioClickIntent.putExtra(WidgetService.WIDGET_NAME, WidgetService.LARGE_WIDGET_NAME);
		PendingIntent audioPendingIntent = PendingIntent.getBroadcast(this, audioIntentCode, onAudioClickIntent, 0);
		
		views.setImageViewResource(R.id.audioStatusImageButton, soundImage);
		views.setTextViewText(R.id.audioStatusTextView, (mManagersModel.getShouldShutoffAudio() ? getApplicationContext().getString(R.string.managed) : null));
		views.setOnClickPendingIntent(R.id.audioStatusImageButton, audioPendingIntent);
		
		// Push update for this widget to the home screen
		ComponentName thisWidget = new ComponentName(this, WidgetLarge.class);
		AppWidgetManager manager = AppWidgetManager.getInstance(this);
		manager.updateAppWidget(thisWidget, views);
		
		return super.onStartCommand(intent, flags, startId);
	}
}
