package com.updude.common;

import static android.content.Context.DEVICE_POLICY_SERVICE;

import android.app.Activity;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.updude.receivers.AdminReceiver;

public class Lock {

    private DevicePolicyManager devicePolicyManager;
    private ComponentName compName;
    private Context currActivity = null;
    public static final int RESULT_ENABLE = 11;

    public void init(Context activity) {
        if (currActivity != null) {
            return;
        }
        currActivity = activity;
        compName = new ComponentName(currActivity, AdminReceiver.class);
        devicePolicyManager = (DevicePolicyManager) currActivity.getSystemService(DEVICE_POLICY_SERVICE);
    }

    // WARNING: YOU MAY ONLY CALL ENABLE LOCK MODULE
    public void enable(Activity activity) {
        Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
        intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, compName);
        intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, "Additional text explaining why we need this permission");
        // TODO: do we need bellow line ?
         activity.startActivityForResult(intent, RESULT_ENABLE);
    }


    public void disable() {
        devicePolicyManager.removeActiveAdmin(compName);
    }

    public void lock() {
        boolean active = devicePolicyManager.isAdminActive(compName);
        if (active) {
            devicePolicyManager.lockNow();
        } else {
            Toast.makeText(currActivity, "You need to enable the Admin Device Features", Toast.LENGTH_SHORT).show();
        }
    }

    public boolean isAdminActive() {
        return devicePolicyManager.isAdminActive(compName);
    }
}
