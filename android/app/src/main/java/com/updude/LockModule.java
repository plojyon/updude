package com.updude;
import static android.content.Context.SENSOR_SERVICE;
import static android.hardware.Sensor.TYPE_STEP_COUNTER;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;
import androidx.annotation.NonNull;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;

public class LockModule extends ReactContextBaseJavaModule {
    public static int steps = -1;

    public LockModule(ReactApplicationContext context) {
        super(context);
    }

    @NonNull
    @Override
    public String getName() {
        return "LockModule";
    }

    private static SensorManager sensorMan = null;
    private static Sensor stepSensor = null;
    
    private void init() {
        if (this.steps != -1)
            return;
        sensorMan = (SensorManager)this.getCurrentActivity().getSystemService(SENSOR_SERVICE);
        stepSensor = sensorMan.getDefaultSensor(TYPE_STEP_COUNTER);
        sensorMan.registerListener(new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {
                LockModule.steps = (int)event.values[0];
                Log.d(LockModule.class.getName(), "Steps: " + Integer.toString(LockModule.steps));
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int i) {}
        }, stepSensor, SensorManager.SENSOR_DELAY_UI);
        Log.d(LockModule.class.getName(), "Registered listener.");
    }
    
    @ReactMethod
    public void hello() {
        init();
        Log.d(LockModule.class.getName(), "Steps: " + Integer.toString(LockModule.steps));
    }
    public int getSteps() {
        return LockModule.steps;
    }
}
