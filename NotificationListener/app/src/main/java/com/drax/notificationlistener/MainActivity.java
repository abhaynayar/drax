package com.drax.notificationlistener;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

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
import java.util.Calendar;
import java.util.Date;

import android.widget.Button;
import android.widget.TextView;
import android.widget.ScrollView;

// TODO: Display most recent log at the top.
// TODO: Save logs to persistent storage.
// TODO: Self-notification to test if notifications are still being logged.

public class MainActivity extends AppCompatActivity {
    public static final String TAG = "NLS";
    public static SQLiteDatabase db;
    static TextView tvLog;

    public static void asdf(String asdf) {
        tvLog.setText(asdf);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tvLog = (TextView) findViewById(R.id.tvLog);

        // Getting some essential permissions:
        getBatteryAccess();
        getNotificationAccess();

        // Create DB:
        db = openOrCreateDatabase("nls", MODE_PRIVATE, null);
        db.execSQL("CREATE TABLE IF NOT EXISTS log(ts TEXT, package TEXT, title TEXT, text TEXT);");
        db.execSQL("DELETE FROM log;");

        // Share logs:
        Button btnSave = (Button) findViewById(R.id.btnShare);
        btnSave.setOnClickListener(v -> {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_TEXT, tvLog.getText());
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Notification log " + Calendar.getInstance().getTime());
            startActivity(Intent.createChooser(shareIntent, "Share..."));
        });
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
