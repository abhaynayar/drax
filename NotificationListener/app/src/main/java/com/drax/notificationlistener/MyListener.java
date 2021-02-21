package com.drax.notificationlistener;

import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import java.util.Calendar;
import java.util.Date;

import static com.drax.notificationlistener.MainActivity.TAG;
import static com.drax.notificationlistener.MainActivity.db;

public class MyListener extends NotificationListenerService {

    @Override
    public void onNotificationPosted(StatusBarNotification sbn){
        String title="", text="";

        Date currentTime = Calendar.getInstance().getTime();
        Bundle extras = sbn.getNotification().extras;

        if(extras.getString("android.title") != null){
            title = extras.getString("android.title");
        }

        if(extras.getCharSequence("android.text") != null){
            text += extras.getCharSequence("android.text").toString();
        }

        db.execSQL("INSERT INTO log VALUES('" + currentTime + "', '" + sbn.getPackageName() + "', '"
                                                           + title + "', '" + text + "');");

        String tableString="";
        Cursor allRows  = db.rawQuery("SELECT * FROM log;", null);
        if (allRows.moveToFirst() ){
            String[] columnNames = allRows.getColumnNames();
            do {
                for (String name: columnNames) {
                    tableString += String.format("%s: %s\n", name,
                            allRows.getString(allRows.getColumnIndex(name)));
                }
                tableString += "\n";

            } while (allRows.moveToNext());
        }

        Log.i(TAG, tableString);
        MainActivity.asdf(tableString);
    }
}