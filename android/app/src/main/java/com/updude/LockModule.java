package com.updude;
import static android.content.Context.ACTIVITY_SERVICE;
import static android.content.Context.DEVICE_POLICY_SERVICE;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;

public class LockModule extends ReactContextBaseJavaModule {
    private ActivityManager activityManager;
    private DevicePolicyManager devicePolicyManager;
    private ComponentName compName;
    private Activity currActivity = null;
    public static final int RESULT_ENABLE = 11;

    @RequiresApi(api = Build.VERSION_CODES.M)
    LockModule(ReactApplicationContext context) {
        super(context);
    }

    @NonNull
    @Override
    public String getName() {
        return "LockModule";
    }

    private void init() {
        if (currActivity != null) {
            return;
        }
        currActivity = this.getCurrentActivity();
        compName = new ComponentName(currActivity, AdminReceiver.class);
        activityManager = (ActivityManager) currActivity.getSystemService(ACTIVITY_SERVICE);
        devicePolicyManager = (DevicePolicyManager) currActivity.getSystemService(DEVICE_POLICY_SERVICE);
    }

    @ReactMethod
    public void enable() {
        init();
        Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
        intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, compName);
        intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, "Additional text explaining why we need this permission");
        currActivity.startActivityForResult(intent, RESULT_ENABLE);
    }


    @ReactMethod
    public void disable() {
        init();
        devicePolicyManager.removeActiveAdmin(compName);
    }

    @ReactMethod
    public void lock() {
        init();
        boolean active = devicePolicyManager.isAdminActive(compName);
        if (active) {
            devicePolicyManager.lockNow();
        } else {
            Toast.makeText(currActivity, "You need to enable the Admin Device Features", Toast.LENGTH_SHORT).show();
        }
    }

    @ReactMethod
    public void hello() {
        Log.d(LockModule.class.getName(), "Hello from lock module!");
    }

    @ReactMethod
    public boolean isAdminActive() {
        return devicePolicyManager.isAdminActive(compName);
    }
}