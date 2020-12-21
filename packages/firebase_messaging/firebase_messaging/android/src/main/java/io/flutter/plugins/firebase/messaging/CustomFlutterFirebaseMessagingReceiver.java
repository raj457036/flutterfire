package io.flutter.plugins.firebasemessaging;

import android.content.Intent;

import com.google.firebase.messaging.RemoteMessage;

public class CustomFlutterFirebaseMessagingReceiver extends FlutterFirebaseMessagingReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
      
        super.onReceive(context, intent);
    
        RemoteMessage remoteMessage = new RemoteMessage(intent.getExtras());
        
        if (remoteMessage.getData().containsKey("is_call")) {
            Intent intent = getPackageManager().getLaunchIntentForPackage(getPackageName());
            startActivity(intent);
        }
        
        
    }
}
