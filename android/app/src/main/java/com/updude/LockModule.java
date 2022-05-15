package com.updude;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import static android.nfc.NfcAdapter.FLAG_READER_NFC_A;

import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.facebook.react.bridge.ReactApplicationContext;

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

import java.math.BigInteger;

import java.util.ArrayList;
import java.util.Objects;

public class LockModule extends ReactContextBaseJavaModule {
    public static final int NOT_READING = 1;
    public static final int OK = 0;
    public static final int NO_NFC = 2;
    private NfcAdapter adapter = null;

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

    public LockModule(ReactApplicationContext context) {
        super(context);
    }

    @ReactMethod
    public void enable() {
        lock.init(this.getCurrentActivity());
        lock.enable();
    public int stopReading() {
        if (this.adapter == null)
            return NOT_READING;
        this.adapter.disableReaderMode(this.getCurrentActivity());
        this.adapter = null;
        return OK;
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

    @ReactMethod
    public int startReading() {
        this.adapter = NfcAdapter.getDefaultAdapter(this.getCurrentActivity());
        if (this.adapter == null || !this.adapter.isEnabled()) { // NFC disabled?
            return NO_NFC;
        }
        this.adapter.enableReaderMode(this.getCurrentActivity(), new NfcAdapter.ReaderCallback() {
            @Override
            public void onTagDiscovered(Tag tag) {
                Log.d(LockModule.class.getName(), "Got tag id: "+bin2hex(tag.getId()));
            }
        }, FLAG_READER_NFC_A, new Bundle());

        return OK;
    }

    private static String bin2hex(byte[] data) {
        return String.format("%0" + (data.length * 2) + "X", new BigInteger(1,data));
    }
}