package com.updude;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import static android.nfc.NfcAdapter.FLAG_READER_NFC_A;

import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.facebook.react.bridge.Callback;
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

    @ReactMethod
    public void enable() {
        lock.init(this.getCurrentActivity());
        lock.enable(this.getCurrentActivity());
    }

    @ReactMethod
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
                sendEvent("BluetoothScanResult", serializeBluetoothDevices(devices));
            }
        });
    }

    @ReactMethod
    public boolean isAdminActive() {
        return lock.isAdminActive();
    }

    @ReactMethod
    public void getSettings(Callback callback) {
        Log.d("LockModule", "get settings");
        WritableMap map = new WritableNativeMap();
        map.putString("type", Storage.get(getCurrentActivity(), "type"));
        map.putString("value", Storage.get(getCurrentActivity(), "value"));
        Log.d("LockModule", map.toString());
        callback.invoke(map);
    }

    @ReactMethod
    public void updateSettings(String type, String value) {
        Log.d("LockModule", "update settings");
        Storage.update(getCurrentActivity(), "type", type);
        Storage.update(getCurrentActivity(), "value", value);
    }

    @ReactMethod
    public void startForegroundService() {
        getCurrentActivity().startService(new Intent(getCurrentActivity(), ForegroundService.class));
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

    @ReactMethod
    public int startReading() {
        this.adapter = NfcAdapter.getDefaultAdapter(this.getCurrentActivity());
        if (this.adapter == null || !this.adapter.isEnabled()) { // NFC disabled?
            return NO_NFC;
        }
        this.adapter.enableReaderMode(this.getCurrentActivity(), new NfcAdapter.ReaderCallback() {
            @Override
            public void onTagDiscovered(Tag tag) {
                sendEvent("NFCReadResult", bin2hex(tag.getId()));
            }
        }, FLAG_READER_NFC_A, new Bundle());

        return OK;
    }

    private static String bin2hex(byte[] data) {
        return String.format("%0" + (data.length * 2) + "X", new BigInteger(1, data));
    }
}