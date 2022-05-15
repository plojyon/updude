package com.updude;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.facebook.react.ReactActivity;
import com.facebook.react.ReactActivityDelegate;
import com.facebook.react.ReactRootView;
import com.updude.common.Lock;
import com.updude.receivers.UnlockReceiver;

public class MainActivity extends ReactActivity {
  private final Lock lock = new Lock();

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(null);
    lock.init(this);
    IntentFilter intentFilter = new IntentFilter(Intent.ACTION_USER_PRESENT);
    BroadcastReceiver mReceiver = new UnlockReceiver(lock);
    registerReceiver(mReceiver, intentFilter);
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
