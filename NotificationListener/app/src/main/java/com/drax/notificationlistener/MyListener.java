package com.drax.notificationlistener;

import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static com.drax.notificationlistener.MainActivity.TAG;
import static com.drax.notificationlistener.MainActivity.db;
import static com.drax.notificationlistener.MainActivity.logsList;
import static com.drax.notificationlistener.MainActivity.rvLogs;

public class MyListener extends NotificationListenerService {

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        Log.i(TAG, "Notification posted.");
        String title="", text="";

        String currentTime = String.valueOf(System.currentTimeMillis());
        Bundle extras = sbn.getNotification().extras;

        if(extras.getString("android.title") != null){
            title = extras.getString("android.title");
        }

        if(extras.getCharSequence("android.text") != null){
            text += extras.getCharSequence("android.text").toString();
        }

        db.execSQL("INSERT INTO log VALUES(" + currentTime + ", '" + sbn.getPackageName() + "', '" + title + "', '" + text + "');");
        Logs log = new Logs(currentTime.toString(), sbn.getPackageName(), title, text);
        Log.d(TAG, log.toString());

        MainActivity.logsList.add(0, log);
        MainActivity.logsAdapter.notifyItemInserted(0);
        MainActivity.layoutManager.smoothScrollToPosition(MainActivity.rvLogs, null, 0);
    }
}