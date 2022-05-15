package com.updude.common;

import static android.content.Context.BLUETOOTH_SERVICE;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.util.Log;

import com.updude.LockModule;

import java.util.ArrayList;

public class Bluetooth {

    public static final int ALREADY_SCANNING = 1;
    public static final int BLUETOOTH_IS_OFF = 2;
    public static final int OK = 0;
    private BluetoothAdapter adapter = null;

    private boolean isScanning = false;
    private ScanCallback scanCallback = null;
    private BluetoothLeScanner scanner = null;
    private final ArrayList<BluetoothDevice> devices = new ArrayList<>();

    public void init(Context activity) {
        // Obtain a Bluetooth adapter.
        // This cannot be done in the constructor because the current activity isn't available
        // and thus we cannot retrieve the context. Because of that, we call init() at the start
        // of any method that requires the BT adapter.
        if (this.adapter != null)
            return;
        BluetoothManager manager = (BluetoothManager) activity.getSystemService(BLUETOOTH_SERVICE);
        this.adapter = manager.getAdapter();
    }

    public void stopScan() {
        if (!this.isScanning)
            return;

        this.isScanning = false;

        this.scanner.stopScan(this.scanCallback);
    }

    public int startScan(BluetoothCallback callback) {
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

                if (result == null) {
                    return;
                }

                Log.d(LockModule.class.getName(), "Scanned: " + result.getDevice().toString());

                if (!isDeviceInRange(result.getDevice())) {
                    devices.add(result.getDevice());
                    callback.onResult(devices);
                }
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

    public boolean isDeviceInRange(BluetoothDevice targetDevice) {
        for (BluetoothDevice device : devices) {
            if (device.equals(targetDevice)) {
                return true;
            }
        }
        return false;
    }

    public boolean isDeviceInRange(String uuid) {
        for (BluetoothDevice device : devices) {
            if (device.toString().equals(uuid)) {
                return true;
            }
        }
        return false;
    }
}

