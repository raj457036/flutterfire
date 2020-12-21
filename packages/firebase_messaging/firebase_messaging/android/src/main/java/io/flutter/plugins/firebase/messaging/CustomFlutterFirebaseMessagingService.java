package io.flutter.plugins.firebase.messaging;

import android.content.Intent;

import com.google.firebase.messaging.RemoteMessage;
import java.io.*; 


public class CustomFlutterFirebaseMessagingService extends FlutterFirebaseMessagingService {
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        System.out.print("GfG1"); 
        System.out.print(remoteMessage.getData());
        if (remoteMessage.getData().containsKey("is_call")) {
            System.out.print("has key"); 
            Intent intent = getPackageManager().getLaunchIntentForPackage(getPackageName());
            startActivity(intent);
        } else
            super.onMessageReceived(remoteMessage);
    }
}
