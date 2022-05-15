package com.updude;
import android.util.Log;
import androidx.annotation.NonNull;

import static android.content.Context.BLUETOOTH_SERVICE;

import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReactApplicationContext;

import android.bluetooth.le.ScanResult;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;

public class LockModule extends ReactContextBaseJavaModule {
	public static final int ALREADY_SCANNING = 1;
	public static final int BLUETOOTH_IS_OFF = 2;
	public static final int OK = 0;

	private BluetoothAdapter adapter = null;

	private boolean isScanning = false;
	private ScanCallback scanCallback = null;
	private BluetoothLeScanner scanner = null;

	public LockModule(ReactApplicationContext context) {
		super(context);
	}

	private void init() {
		// Obtain a Bluetooth adapter.
		// This cannot be done in the constructor because the current activity isn't available
		// and thus we cannot retrieve the context. Because of that, we call init() at the start
		// of any method that requires the BT adapter.
		if (this.adapter != null)
			return;
		BluetoothManager manager = (BluetoothManager) this.getCurrentActivity().getSystemService(BLUETOOTH_SERVICE);
		this.adapter = manager.getAdapter();
	}

	@NonNull
	@Override
	public String getName() {
		return "LockModule";
	}

	@ReactMethod
	public void stopScan() {
		this.init();

		if (!this.isScanning)
			return;

		this.scanner.stopScan(this.scanCallback);
	}

	@ReactMethod
	public int startScan() {
		this.init();

		if (this.isScanning)
			return ALREADY_SCANNING;

		this.isScanning = true;

		this.scanner = this.adapter.getBluetoothLeScanner();
		if (this.scanner == null) { // Bluetooth is disabled?
			this.isScanning = false;
			return BLUETOOTH_IS_OFF;
		}

		this.scanCallback = new ScanCallback() {
			@Override
			public void onScanResult(int callbackType, ScanResult result) {
				super.onScanResult(callbackType, result);
				Log.d(LockModule.class.getName(), "Scanned: " + result.getDevice().toString());
				// leDeviceListAdapter.addDevice(result.getDevice());
				// leDeviceListAdapter.notifyDataSetChanged();
			}

			@Override
			public void onScanFailed(int errorCode) {
				super.onScanFailed(errorCode);
				Log.d(LockModule.class.getName(), "Error: " + Integer.toString(errorCode));
			}
		};

		this.scanner.startScan(this.scanCallback);
		return OK;
    }
}