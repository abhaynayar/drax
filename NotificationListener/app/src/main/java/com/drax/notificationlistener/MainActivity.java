package com.drax.notificationlistener;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.IntentFilter;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.provider.Settings;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import android.widget.Button;
import android.widget.TextView;
import android.widget.ScrollView;

// TODO: Prevent SQL injection.
// TODO: Better representation for timestamp.
// TODO: Clear logs button, Export logs button.
// TODO: Self-notification to test if notifications are still being logged.

public class MainActivity extends AppCompatActivity {
    public static final String TAG = "NLS";
    public static SQLiteDatabase db;
    static TextView tvLog;

    RecyclerView rvLogs;
    RecyclerView.LayoutManager layoutManager;

    public static LogsAdapter logsAdapter;
    public static List<Logs> logsList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Getting some essential permissions:
        getBatteryAccess();
        getNotificationAccess();

        // Create DB:
        db = openOrCreateDatabase("nls", MODE_PRIVATE, null);
        db.execSQL("CREATE TABLE IF NOT EXISTS log(timeStamp INTEGER, packageName TEXT, title TEXT, text TEXT);");
        db.execSQL("DELETE FROM log");

        // Populate the recycler view:
        logsList = getAllLogs();
        rvLogs = findViewById(R.id.rvLogs);
        rvLogs.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        rvLogs.setLayoutManager(layoutManager);
        logsAdapter = new LogsAdapter(this, logsList, rvLogs);
        rvLogs.setAdapter(logsAdapter);

    }

    public List<Logs> getAllLogs() {

        List<Logs> logsList = new ArrayList<>();
        Cursor cursor = db.rawQuery("SELECT * FROM log ORDER BY timeStamp DESC;", null);
        if (cursor.moveToFirst() ){
            String[] columnNames = cursor.getColumnNames();
            do {
                Logs log = new Logs(
                    cursor.getString(cursor.getColumnIndex("timeStamp")),
                    cursor.getString(cursor.getColumnIndex("packageName")),
                    cursor.getString(cursor.getColumnIndex("title")),
                    cursor.getString(cursor.getColumnIndex("text"))
                );
                logsList.add(log);

            } while (cursor.moveToNext());
        }
        return logsList;
    }

    void getNotificationAccess() {
        // Ask for notification access permission.
        NotificationManager nm = (NotificationManager) getApplicationContext()
                .getSystemService(Context.NOTIFICATION_SERVICE);
        if (!nm.isNotificationPolicyAccessGranted()) {
            Log.i(TAG, "[-] Notification access denied.");
            startActivity(new Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS));
        } else {
            Log.i(TAG, "[+] Notification access granted.");
        }
    }

    void getBatteryAccess() {
        // Ask for disabling battery optimization.
        Intent intent = new Intent();
        String packageName = this.getPackageName();
        PowerManager pm = (PowerManager) this.getSystemService(Context.POWER_SERVICE);
        if (!pm.isIgnoringBatteryOptimizations(packageName)) {
            Log.e(TAG, "[-] Not ignoring battery optimization.");
            startActivity(new Intent(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS));
        } else {
            Log.i(TAG, "[+] Ignoring battery optimization.");
        }
    }
}
