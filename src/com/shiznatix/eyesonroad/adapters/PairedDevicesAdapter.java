package com.shiznatix.eyesonroad.adapters;

import java.util.ArrayList;
import java.util.List;

import com.shiznatix.eyesonroad.R;
import com.shiznatix.eyesonroad.models.PreferencesModel;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class PairedDevicesAdapter extends ArrayAdapter<BluetoothDevice> {
	private final Context mContext;
	private List<BluetoothDevice> mDevices = new ArrayList<BluetoothDevice>();
	private int mLayout;
	
	private PreferencesModel mPreferencesModel;
	
	public PairedDevicesAdapter(Context context, int resourceid, List<BluetoothDevice> devices) {
		super(context, resourceid, devices);
		
		mDevices = devices;
		mContext = context;
		mLayout = resourceid;
		mPreferencesModel = new PreferencesModel(mContext);
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View rowView = View.inflate(mContext, mLayout, null);
		
		BluetoothDevice device = mDevices.get(position);
		
		TextView usernameTextView = (TextView)rowView.findViewById(R.id.nameTextView);
		usernameTextView.setText(device.getName());
		
		TextView commentTextView = (TextView)rowView.findViewById(R.id.hashCodeTextView);
		commentTextView.setText(Integer.toString(device.hashCode()));
		
		ImageView checkmarkImageView = (ImageView)rowView.findViewById(R.id.checkmarkImageView);
		
		if (mPreferencesModel.getCarBluetoothHash() == device.hashCode()) {
			rowView.setBackgroundColor(Color.parseColor("#33b5e5"));
			checkmarkImageView.setVisibility(View.VISIBLE);
		}
		else {
			rowView.setBackgroundColor(Color.WHITE);
			checkmarkImageView.setVisibility(View.INVISIBLE);
		}
		
		return rowView;
	}
}
