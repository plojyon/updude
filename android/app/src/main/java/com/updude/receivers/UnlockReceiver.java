package com.updude.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.updude.common.Lock;

public class UnlockReceiver extends BroadcastReceiver {

    private final Lock lock;

    public UnlockReceiver(Lock lock) {
        this.lock = lock;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        StringBuilder sb = new StringBuilder();
        sb.append("Action: " + intent.getAction() + "\n");
        sb.append("URI: " + intent.toUri(Intent.URI_INTENT_SCHEME).toString() + "\n");
        String log = sb.toString();
//        lock.lock();
        Log.d(UnlockReceiver.class.getName(), log);
        Toast.makeText(context, log, Toast.LENGTH_LONG).show();
    }
}
