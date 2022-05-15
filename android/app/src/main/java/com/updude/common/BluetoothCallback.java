package com.updude.common;

import android.bluetooth.BluetoothDevice;

import java.util.ArrayList;

public interface BluetoothCallback {
    void onResult(ArrayList<BluetoothDevice> devices);
}
