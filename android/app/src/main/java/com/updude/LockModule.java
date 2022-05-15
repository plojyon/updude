package com.updude;

import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Build;

import android.nfc.NfcAdapter;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.ReactApplicationContext;

import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.WritableArray;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.bridge.WritableNativeArray;
import com.facebook.react.bridge.WritableNativeMap;
import com.facebook.react.modules.core.DeviceEventManagerModule;
import com.updude.common.Bluetooth;
import com.updude.common.BluetoothCallback;
import com.updude.common.Lock;
import com.updude.common.Nfc;
import com.updude.common.NfcCallback;

import java.math.BigInteger;

import java.util.ArrayList;

public class LockModule extends ReactContextBaseJavaModule {
    public static final int NOT_READING = 1;
    public static final int OK = 0;
    public static final int NO_NFC = 2;
    private NfcAdapter adapter = null;

    private final Lock lock = new Lock();
    private final Nfc nfc = new Nfc();
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
        lock.init(this.getCurrentActivity());
        lock.enable(this.getCurrentActivity());
    }

    @ReactMethod
    public int stopReading() {
        nfc.init(getCurrentActivity());
        return nfc.stopReading();
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
        bluetooth.startScan(new BluetoothCallback() {
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
        map.putString("ble", Storage.get(getCurrentActivity(), "ble"));
        map.putString("nfc", Storage.get(getCurrentActivity(), "nfc"));
        map.putString("steps", Storage.get(getCurrentActivity(), "steps"));
        map.putString("ble_name", Storage.get(getCurrentActivity(), "ble_name"));
        map.putString("nfc_name", Storage.get(getCurrentActivity(), "nfc_name"));
        Log.d("LockModule", map.toString());
        callback.invoke(map);
    }

    @ReactMethod
    public void wipeSettings(Callback callback) {
        Storage.remove(getCurrentActivity(), "ble");
        Storage.remove(getCurrentActivity(), "nfc");
        Storage.remove(getCurrentActivity(), "steps");
        Storage.remove(getCurrentActivity(), "ble_name");
        Storage.remove(getCurrentActivity(), "nfc_name");

        callback.invoke();
    }

    @ReactMethod
    public void updateSettings(String type, String value) {
        Log.d("LockModule", "update settings");
        Storage.update(getCurrentActivity(), type, value);
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
        nfc.init(getCurrentActivity());
        return nfc.startReading(new NfcCallback() {
            @Override
            public void onResult(String tag) {
                sendEvent("NFCReadResult", tag);
            }
        });
    }

    private static String bin2hex(byte[] data) {
        return String.format("%0" + (data.length * 2) + "X", new BigInteger(1, data));
    }
}