package com.shiznatix.eyesonroad.fragments;

import java.util.ArrayList;
import java.util.List;

import com.shiznatix.eyesonroad.R;
import com.shiznatix.eyesonroad.adapters.PairedDevicesAdapter;
import com.shiznatix.eyesonroad.models.ManagersModel;
import com.shiznatix.eyesonroad.models.PreferencesModel;

import android.app.Fragment;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

public class BluetoothDevicesFragment extends Fragment {
	static private final String LOG_TAG = "er_BluetoothDevicesFragment";
	
	static private final int REQUEST_ENABLE_BLUETOOTH = 1;
	
	protected BluetoothAdapter mBluetoothAdapter;
	protected List<BluetoothDevice> mDevices = new ArrayList<BluetoothDevice>();
	protected PairedDevicesAdapter mPairedDevicesAdapter;
	protected boolean mAskedForBluetooth = false;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_bluetooth_devices, container, false);
		
		Log.i(LOG_TAG, "onCreateView");
		
		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		
		if (mBluetoothAdapter == null) {
			Log.e(LOG_TAG, "bluetooth is not available! forcing shutdown");
			
			return view;
		}
		
		mDevices.clear();
		mDevices.addAll(mBluetoothAdapter.getBondedDevices());
		mPairedDevicesAdapter = new PairedDevicesAdapter(getActivity(), R.layout.cell_device, mDevices);
		
		ListView pairedDevicesListView = (ListView)view.findViewById(R.id.pairedDevicesListView);
		pairedDevicesListView.setAdapter(mPairedDevicesAdapter);
		pairedDevicesListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				BluetoothDevice device = mDevices.get(position);
				
				Log.i(LOG_TAG, "selected device: "+device.hashCode());
				
				PreferencesModel preferencesModel = new PreferencesModel(getActivity());
				preferencesModel.setCarBluetoothHash(device.hashCode());
				
				mPairedDevicesAdapter.notifyDataSetChanged();
				
				Intent intent = new Intent();
				intent.putExtra(ManagersModel.CAR_DEVICE_HASH, device.hashCode());
				intent.setAction(ManagersModel.ACTION_CAR_DEVICE_HASH_UPDATED);
				getActivity().sendBroadcast(intent);
			}
		});
		
		return view;
	}
	
	@Override
	public void onResume() {
		super.onResume();
		
		Log.i(LOG_TAG, "onResume");
		
		if (null != mBluetoothAdapter && !mBluetoothAdapter.isEnabled() && !mAskedForBluetooth) {
			final Handler handler = new Handler();
			handler.postDelayed(new Runnable() {
				@Override
				public void run() {
					mAskedForBluetooth = true;
					
					Intent enableBluetoothIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
					startActivityForResult(enableBluetoothIntent, REQUEST_ENABLE_BLUETOOTH);
				}
			}, 1000);
		}
	}
	
	@Override
	public void onStop() {
		super.onStop();
		
		Log.i(LOG_TAG, "onStop");
		
		mAskedForBluetooth = false;
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == REQUEST_ENABLE_BLUETOOTH) {
			mDevices.clear();
			mDevices.addAll(mBluetoothAdapter.getBondedDevices());
			
			mPairedDevicesAdapter.notifyDataSetChanged();
		}
	}
}