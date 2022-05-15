package com.updude;
import static android.nfc.NfcAdapter.FLAG_READER_NFC_A;

import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.Bundle;
import android.util.Log;
import androidx.annotation.NonNull;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;

import java.math.BigInteger;

public class LockModule extends ReactContextBaseJavaModule {
    public static final int NOT_READING = 1;
    public static final int OK = 0;
    public static final int NO_NFC = 2;
    private NfcAdapter adapter = null;

    @NonNull
    @Override
    public String getName() {
        return "LockModule";
    }

    public LockModule(ReactApplicationContext context) {
        super(context);
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