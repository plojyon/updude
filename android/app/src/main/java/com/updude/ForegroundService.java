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
import com.updude.common.DeviceCallback;
import com.updude.common.Lock;

import java.util.ArrayList;


public class ForegroundService extends Service {
    private static String CHANNEL_ID = "12";
    private final Bluetooth bluetooth = new Bluetooth();
    private final Lock lock = new Lock();
    private String type;
    private String value;

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

        type = Storage.get(getApplicationContext(), "type");
        value = Storage.get(getApplicationContext(), "value");
        lock.init(getApplicationContext());
        lock.lock();

        switch (type) {
            case "bluetooth": {
                // here is the code you wanna run in background
                bluetooth.init(getApplicationContext());
                bluetooth.startScan(new DeviceCallback() {
                    @Override
                    public void onResult(ArrayList<BluetoothDevice> devices) {
                        if (bluetooth.isDeviceInRange(value)) {
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
        switch (type) {
            case "bluetooth": {
                bluetooth.stopScan();
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
