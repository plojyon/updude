package com.updude;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.updude.common.Bluetooth;
import com.updude.common.BluetoothCallback;
import com.updude.common.Lock;
import com.updude.common.Nfc;
import com.updude.common.NfcCallback;

import java.util.ArrayList;


public class ForegroundService extends Service {
    private static String CHANNEL_ID = "12";
    private final Lock lock = new Lock();
    private final Nfc nfc1 = new Nfc();
    private Bluetooth bluetooth;
    private String ble;
    private String nfc;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("ForegroundService", "Hello World!");
        String input = intent.getStringExtra("inputExtra");
        createNotificationChannel();

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, MainActivity.class), 0);

        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Foreground Service")
                .setContentText(input)
                .setContentIntent(pendingIntent)
                .build();

        startForeground(1, notification);

        ble = Storage.get(getApplicationContext(), "ble");
        nfc = Storage.get(getApplicationContext(), "nfc");
        nfc1.init(getApplicationContext());
        lock.init(getApplicationContext());
//        lock.lock();



        if (nfc != null) {
            nfc1.startReading(new NfcCallback() {
                @Override
                public void onResult(String tag) {
                    Log.d("NFC", tag + ", " + nfc);
                    if (tag.equals(nfc)) {
                        Log.d("EQUALS", tag);
                    }
                }
            });
        }

//
        switch (ble) {
            case "bluetooth": {
                // here is the code you wanna run in background
                bluetooth.init(getApplicationContext());
                bluetooth.startScan(new BluetoothCallback() {
                    @Override
                    public void onResult(ArrayList<BluetoothDevice> devices) {
                        if (bluetooth.isDeviceInRange(nfc)) {
                            Log.d("ForegroundService", "device in range");
                            lock.disable();
                        }
                    }
                });
                break;
            }
            case "nfc": {

                break;
            }
            case "steps": {

                break;
            }
        }


        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        switch (ble) {
            case "bluetooth": {
                break;
            }
            case "nfc": {

                break;
            }
            case "steps": {

                break;
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void createNotificationChannel() {
        NotificationChannel serviceChannel = new NotificationChannel(
                CHANNEL_ID,
                "UpDude",
                NotificationManager.IMPORTANCE_DEFAULT);

        getSystemService(NotificationManager.class).createNotificationChannel(serviceChannel);
    }
}
