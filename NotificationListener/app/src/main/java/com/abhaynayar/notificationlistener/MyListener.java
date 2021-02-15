package com.abhaynayar.notificationlistener;

import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;

public class MyListener extends NotificationListenerService {

    @Override
    public void onNotificationPosted(StatusBarNotification sbn){
        Intent intent = new  Intent("com.abhaynayar.notificationlistener.xyz");

        String all = "";
        all += sbn.getPackageName() + ", ";
        Bundle extras = sbn.getNotification().extras;

        if(extras.getString("android.title") != null){
            all += extras.getString("android.title") + ", ";
        }

        if(extras.getCharSequence("android.text") != null){
            all += extras.getCharSequence("android.text").toString();
        }

        intent.putExtra("notificationContent", all);
        sendBroadcast(intent);

        Log.i("NLS", "onNotificationPosted");
    }
}