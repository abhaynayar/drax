package com.abhaynayar.notificationlistener;

import androidx.appcompat.app.AppCompatActivity;
import android.content.BroadcastReceiver;
import android.app.NotificationManager;
import android.content.IntentFilter;
import android.provider.Settings;
import android.os.PowerManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import java.util.Calendar;
import android.widget.Button;
import android.widget.TextView;
import android.widget.ScrollView;

// TODO: Battery optimization jump to package name.

public class MainActivity extends AppCompatActivity {

    static String aggregation = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.i("NLS", "onCreate");

        // Launch battery optimization settings:
        Button btnOptimization = (Button) findViewById(R.id.btnOptimization);
        btnOptimization.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS);
                startActivity(intent);
            }
        });

        // Launch notification access settings:
        Button btnPermission = (Button) findViewById(R.id.btnPermission);
        btnPermission.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startActivity(new Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS));
            }
        });

        // Listen for broadcasts by the notification listener.
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.abhaynayar.notificationlistener.xyz");
        registerReceiver(new SomeBroadcastReceiver(), intentFilter);

        // When you add more notifications, scroll to the bottom.
        ScrollView sv = (ScrollView) findViewById(R.id.SCROLLER_ID);
        sv.fullScroll(View.FOCUS_DOWN);

        // Share notifications log:
        Button btnSave = (Button) findViewById(R.id.btnSave);
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

    @Override
    protected void onResume() {
        super.onResume();

        // Battery optimization check:
        Button btnOptimization = (Button) findViewById(R.id.btnOptimization);
        TextView tvOptimization = (TextView) findViewById(R.id.tvOptimization);
        PowerManager pm = (PowerManager) getSystemService(POWER_SERVICE);
        if (!pm.isIgnoringBatteryOptimizations(getPackageName())) {
            tvOptimization.setText("Not ignoring battery optimization.");
            btnOptimization.setEnabled(true);
        } else {
            tvOptimization.setText("[OK] Ignoring battery optimization.");
            btnOptimization.setEnabled(false);
        }

        // Notification access check:
        Button btnPermission = (Button) findViewById(R.id.btnPermission);
        TextView tvPermission = (TextView) findViewById(R.id.tvPermission);
        NotificationManager nm = (NotificationManager) getApplicationContext()
                .getSystemService(Context.NOTIFICATION_SERVICE);
        if(!nm.isNotificationPolicyAccessGranted()) {
            tvPermission.setText("Notification access not granted.");
            btnPermission.setEnabled(true);
        } else {
            tvPermission.setText("[OK] Notification access granted.");
            btnPermission.setEnabled(false);
        }
    }

    public class SomeBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Concatenate notification contents from the broadcast.
            String nc = intent.getExtras().getString("notificationContent");
            aggregation += nc + "\n";
            Log.i("NLS", nc);

            // Update TextView with the concatenated result.
            TextView tvNotifications = (TextView) findViewById(R.id.tvNotifications);
            tvNotifications.setText(aggregation);
        }
    }
}