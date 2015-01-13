package com.shiznatix.eyesonroad.services.widgets;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.bluetooth.BluetoothAdapter;
import android.content.ComponentName;
import android.content.Intent;
import android.graphics.Paint;
import android.widget.RemoteViews;

import com.shiznatix.eyesonroad.ActivityMain;
import com.shiznatix.eyesonroad.R;
import com.shiznatix.eyesonroad.WidgetSmall;
import com.shiznatix.eyesonroad.models.ManagersModel;
import com.shiznatix.eyesonroad.services.WidgetService;

public class SmallWidgetService extends WidgetService {
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		/* Actually, it doesn't really matter for this view if no bluetooth exists...*/
		BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		
		if (bluetoothAdapter == null) {
			return super.onStartCommand(intent, flags, startId);//no bluetooth at all? no hope!
		}
		
		RemoteViews views = new RemoteViews(getPackageName(), R.layout.widget_small);
		
		//connection status
		int connectionImage = (ManagersModel.CONNECTED_TO_CAR ? R.drawable.connected : R.drawable.not_connected);
		
		Intent onClickIntent = new Intent(this, ActivityMain.class);
		onClickIntent.putExtra(WidgetService.WIDGET_NAME, WidgetService.SMALL_WIDGET_NAME);
		PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, onClickIntent, 0);
		
		String enabledText = null;
		String connectedText = getApplicationContext().getString(R.string.connected);
		
		if (mManagersModel.getIsGlobalDisabled()) {
			enabledText = getApplicationContext().getString(R.string.disabled);
		}
		else {
			enabledText = getApplicationContext().getString(R.string.managed);
		}
		
		if (!ManagersModel.CONNECTED_TO_CAR) {
			views.setInt(R.id.connectionStatusTextView, "setPaintFlags", Paint.STRIKE_THRU_TEXT_FLAG | Paint.ANTI_ALIAS_FLAG);
		}
		
		views.setTextViewText(R.id.enabledStatusTextView, enabledText);
		views.setImageViewResource(R.id.connectionStatusImageButton, connectionImage);
		views.setTextViewText(R.id.connectionStatusTextView, connectedText);
		views.setOnClickPendingIntent(R.id.connectionStatusImageButton, pendingIntent);
		
		// Push update for this widget to the home screen
		ComponentName thisWidget = new ComponentName(this, WidgetSmall.class);
		AppWidgetManager manager = AppWidgetManager.getInstance(this);
		manager.updateAppWidget(thisWidget, views);
		
		return super.onStartCommand(intent, flags, startId);
	}
}