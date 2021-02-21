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
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import java.util.Calendar;
import android.widget.Button;
import android.widget.TextView;
import android.widget.ScrollView;

// TODO: Save logs to persistent storage.
// TODO: Use table instead of csv.
// TODO: Add timestamp to logs.
// TODO: Display most recent log at the top.

public class MainActivity extends AppCompatActivity {
    private static final String LOG_TAG = "NLS";

    void getNotificationAccess() {
        // Ask for notification access permission.
        NotificationManager nm = (NotificationManager) getApplicationContext()
                .getSystemService(Context.NOTIFICATION_SERVICE);
        if (!nm.isNotificationPolicyAccessGranted()) {
            Log.i(LOG_TAG, "[-] Notification access denied.");
            startActivity(new Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS));
        } else {
            Log.i(LOG_TAG, "[+] Notification access granted.");
        }
    }

    void getBatteryAccess() {
        // Ask for disabling battery optimization.
        Intent intent = new Intent();
        String packageName = this.getPackageName();
        PowerManager pm = (PowerManager) this.getSystemService(Context.POWER_SERVICE);
        if (!pm.isIgnoringBatteryOptimizations(packageName)) {
            Log.e(LOG_TAG, "[-] Not ignoring battery optimization.");
            startActivity(new Intent(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS));
        } else {
            Log.i(LOG_TAG, "[+] Ignoring battery optimization.");
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Get some essential permissions:
        getBatteryAccess();
        getNotificationAccess();

        // Listen for broadcasts by the notification listener.
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.drax.nls");
        registerReceiver(new SomeBroadcastReceiver(), intentFilter);

        // Share notifications log:
        Button btnSave = (Button) findViewById(R.id.btnShare);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                TextView tvNotifications = (TextView) findViewById(R.id.tvNotifications);
                shareIntent.putExtra(Intent.EXTRA_TEXT,tvNotifications.getText());
                shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Notification log " + Calendar.getInstance().getTime());
                startActivity(Intent.createChooser(shareIntent, "Share..."));
            }
        });
    }

    static String aggregation = "";
    public class SomeBroadcastReceiver extends BroadcastReceiver {
        @Override

        public void onReceive(Context context, Intent intent) {

            // Concatenate notification contents from the broadcast.
            String nc = intent.getExtras().getString("notificationContent");
            aggregation += nc + "\n";

            // Update TextView with the concatenated result.
            TextView tvNotifications = (TextView) findViewById(R.id.tvNotifications);
            tvNotifications.setText(aggregation);
        }
    }
}
