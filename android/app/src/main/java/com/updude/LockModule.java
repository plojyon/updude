package com.updude;
import android.util.Log;
import androidx.annotation.NonNull;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;

public class LockModule extends ReactContextBaseJavaModule {
    @NonNull
    @Override
    public String getName() {
        return "LockModule";
    }

    @ReactMethod
    public void hello() {
        Log.d(LockModule.class.getName(), "Hello from lock module!");
    }
}