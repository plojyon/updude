package com.updude;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.facebook.react.ReactActivity;
import com.facebook.react.ReactActivityDelegate;
import com.facebook.react.ReactRootView;
import com.updude.common.Lock;
import com.updude.receivers.UnlockReceiver;

import java.math.BigInteger;

public class MainActivity extends ReactActivity {
  private final Lock lock = new Lock();
  private static final int REQUEST_LOCATION_ENABLE_CODE = 1; // StackOverflow told me

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(null);
    lock.init(this);
    IntentFilter intentFilter = new IntentFilter(Intent.ACTION_USER_PRESENT);
    BroadcastReceiver mReceiver = new UnlockReceiver(lock);
    registerReceiver(mReceiver, intentFilter);
    if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
            android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
    } else {
      ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
              REQUEST_LOCATION_ENABLE_CODE);
    }
  }

  /**
   * Returns the name of the main component registered from JavaScript. This is used to schedule
   * rendering of the component.
   */
  @Override
  protected String getMainComponentName() {
    return "UpDude";
  }

  /**
   * Returns the instance of the {@link ReactActivityDelegate}. There the RootView is created and
   * you can specify the rendered you wish to use (Fabric or the older renderer).
   */
  @Override
  protected ReactActivityDelegate createReactActivityDelegate() {
    return new MainActivityDelegate(this, getMainComponentName());
  }

  public static class MainActivityDelegate extends ReactActivityDelegate {
    public MainActivityDelegate(ReactActivity activity, String mainComponentName) {
      super(activity, mainComponentName);
    }

    @Override
    protected ReactRootView createRootView() {
      ReactRootView reactRootView = new ReactRootView(getContext());
      // If you opted-in for the New Architecture, we enable the Fabric Renderer.
      reactRootView.setIsFabric(BuildConfig.IS_NEW_ARCHITECTURE_ENABLED);
      return reactRootView;
    }
  }

  @Override
  public void onNewIntent(Intent intent) {
    super.onNewIntent(intent);

    Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
    Log.d("abcd", bin2hex(tag.getId()));
  }

  private static String bin2hex(byte[] data) {
    return String.format("%0" + (data.length * 2) + "X", new BigInteger(1, data));
  }

  @Override
  public void onActivityResult(int requestCode, int resultCode, Intent data) {
    Log.d(MainActivity.class.getName(), String.format("%d, %d, %s", requestCode, resultCode, data));
    switch(requestCode) {
      case Lock.RESULT_ENABLE:
        if (resultCode == Activity.RESULT_OK) {
          Toast.makeText(MainActivity.this, "You have enabled the Admin Device features", Toast.LENGTH_SHORT).show();
        } else {
          Toast.makeText(MainActivity.this, "Problem to enable the Admin Device features", Toast.LENGTH_SHORT).show();
        }
        break;
    }

    super.onActivityResult(requestCode, resultCode, data);
  }
}
