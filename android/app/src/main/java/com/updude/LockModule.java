package com.updude;

import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.updude.common.Lock;

public class LockModule extends ReactContextBaseJavaModule {
    private final Lock lock = new Lock();

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
    public void hello() {
        Log.d(LockModule.class.getName(), "Hello from lock module!");
    }

    @ReactMethod
    public boolean isAdminActive() {
        return lock.isAdminActive();
    }
}