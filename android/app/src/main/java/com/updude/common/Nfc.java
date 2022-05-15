package com.updude.common;

import static android.nfc.NfcAdapter.FLAG_READER_NFC_A;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.Bundle;
import android.util.Log;

import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.WritableArray;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.bridge.WritableNativeArray;
import com.facebook.react.bridge.WritableNativeMap;
import com.facebook.react.modules.core.DeviceEventManagerModule;
import com.updude.ForegroundService;
import com.updude.Storage;

import java.math.BigInteger;
import java.util.ArrayList;

public class Nfc {
    public static final int NOT_READING = 1;
    public static final int OK = 0;
    public static final int NO_NFC = 2;
    private Context context;
    private NfcAdapter adapter = null;

    public void init(Context context) {
        this.context = context;
    }

    public int stopReading() {
        if (this.adapter == null)
            return NOT_READING;
//        this.adapter.disableReaderMode(context);
        this.adapter = null;
        return OK;
    }

    public int startReading(NfcCallback callback) {
        this.adapter = NfcAdapter.getDefaultAdapter(context);
        if (this.adapter == null || !this.adapter.isEnabled()) { // NFC disabled?
            return NO_NFC;
        }
        this.adapter.enableReaderMode(context, new NfcAdapter.ReaderCallback() {
            @Override
            public void onTagDiscovered(Tag tag) {
                callback.onResult(bin2hex(tag.getId()));
            }
        }, FLAG_READER_NFC_A, new Bundle());

        return OK;
    }

    private static String bin2hex(byte[] data) {
        return String.format("%0" + (data.length * 2) + "X", new BigInteger(1, data));
    }
}
