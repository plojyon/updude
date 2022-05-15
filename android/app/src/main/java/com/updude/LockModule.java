package com.updude;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.WritableArray;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.bridge.WritableNativeArray;
import com.facebook.react.bridge.WritableNativeMap;
import com.facebook.react.modules.core.DeviceEventManagerModule;
import com.updude.common.Bluetooth;
import com.updude.common.DeviceCallback;
import com.updude.common.Lock;

import java.util.ArrayList;
import java.util.Objects;

public class LockModule extends ReactContextBaseJavaModule {
    private final Lock lock = new Lock();
    private final Bluetooth bluetooth = new Bluetooth();

    @RequiresApi(api = Build.VERSION_CODES.M)
    LockModule(ReactApplicationContext context) {
        super(context);
    }

    @NonNull
    @Override
    public String getName() {
        return "LockModule";
    }

    @ReactMethod
    public void enable() {
        lock.init(this.getCurrentActivity());
        lock.enable();
    }

    @ReactMethod
    public void disable() {
        lock.init(this.getCurrentActivity());
        lock.disable();
    }

    @ReactMethod
    public void lock() {
        lock.init(this.getCurrentActivity());
        lock.lock();
    }

	@ReactMethod
	public void stopScan() {
		bluetooth.init(this.getCurrentActivity());
		bluetooth.stopScan();
	}

	@ReactMethod
	public void startScan() {
		bluetooth.init(this.getCurrentActivity());
		bluetooth.startScan(new DeviceCallback() {
            @Override
            public void onResult(ArrayList<BluetoothDevice> devices) {
                // TODO: test
                for (BluetoothDevice device : devices) {
                    Log.d("LockModule", device.toString());
                }
                 sendEvent("BluetoothScanResult", serializeBluetoothDevices(devices));
            }
        });
	}

    @ReactMethod
    public boolean isAdminActive() {
        return lock.isAdminActive();
    }

    @ReactMethod
    public String getBluetoothDeviceUuid() {
        return getSharedPref().getString("bluetooth_device_uuid", null);
    }

    @ReactMethod
    public void setBluetoothDeviceUuid(String uuid) {
        SharedPreferences.Editor editor = getSharedPref().edit();
        editor.putString("bluetooth_device_uuid", uuid);
        editor.apply();
    }

    private WritableArray serializeBluetoothDevices(ArrayList<BluetoothDevice> devices) {
        WritableArray array = new WritableNativeArray();
        for (BluetoothDevice device : devices) {
            WritableMap map = new WritableNativeMap();
            map.putString("uuid", device.toString());
            map.putString("name", device.getName());
            array.pushMap(map);
        }
        return array;
    }

    private void sendEvent(String name, Object data) {
        getReactApplicationContext()
                .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
                .emit(name, data);
    }

    private SharedPreferences getSharedPref() {
        return Objects.requireNonNull(getCurrentActivity()).getPreferences(Context.MODE_PRIVATE);
    }
}