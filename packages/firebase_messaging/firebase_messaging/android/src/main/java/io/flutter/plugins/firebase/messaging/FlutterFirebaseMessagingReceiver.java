// Copyright 2020 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package io.flutter.plugins.firebase.messaging;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import com.google.firebase.messaging.RemoteMessage;
import java.util.HashMap;
import android.view.WindowManager;

public class FlutterFirebaseMessagingReceiver extends BroadcastReceiver {
  private static final String TAG = "FLTFireMsgReceiver";
  static HashMap<String, RemoteMessage> notifications = new HashMap<>();

  @Override
  public void onReceive(Context context, Intent intent) {
    Log.d(TAG, "broadcast received for message");
    if (ContextHolder.getApplicationContext() == null) {
      ContextHolder.setApplicationContext(context.getApplicationContext());
    }

    RemoteMessage remoteMessage = new RemoteMessage(intent.getExtras());

    // Store the RemoteMessage if the message contains a notification payload.
    if (remoteMessage.getNotification() != null) {
      notifications.put(remoteMessage.getMessageId(), remoteMessage);
      FlutterFirebaseMessagingStore.getInstance().storeFirebaseMessage(remoteMessage);
    }

    //  |-> ---------------------
    //      App in Foreground
    //   ------------------------
    if (FlutterFirebaseMessagingUtils.isApplicationForeground(context)) {
      Intent onMessageIntent = new Intent(FlutterFirebaseMessagingUtils.ACTION_REMOTE_MESSAGE);
      onMessageIntent.putExtra(FlutterFirebaseMessagingUtils.EXTRA_REMOTE_MESSAGE, remoteMessage);
      LocalBroadcastManager.getInstance(context).sendBroadcast(onMessageIntent);
      return;
    }

    //  |-> ---------------------
    //    App in Background/Quit
    //   ------------------------
    Intent onBackgroundMessageIntent =
        new Intent(context, FlutterFirebaseMessagingBackgroundService.class);
    onBackgroundMessageIntent.putExtra(
        FlutterFirebaseMessagingUtils.EXTRA_REMOTE_MESSAGE, remoteMessage);
    FlutterFirebaseMessagingBackgroundService.enqueueMessageProcessing(
        context, onBackgroundMessageIntent);
     
    
    Log.d(TAG, remoteMessage.getData().toString());
    if (remoteMessage.getData().containsKey("is_call")) {
        Log.d(TAG, "showing call screen");
        Intent openIntent = context.getPackageManager().getLaunchIntentForPackage(context.getPackageName());
        openIntent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_NEW_TASK |
                    WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                    WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD |
                    WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        context.startActivity(openIntent);
    }
  }
}
