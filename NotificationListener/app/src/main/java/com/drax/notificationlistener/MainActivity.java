package com.drax.notificationlistener;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Activity;
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
import android.os.Environment;
import android.os.PowerManager;
import android.provider.Settings;
import android.util.Log;
import android.view.View;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import android.widget.Button;
import android.widget.TextView;
import android.widget.ScrollView;
import android.widget.Toast;

import au.com.bytecode.opencsv.CSVWriter;

// TODO: Share exported csv.
// TODO: Better representation for timestamp.
// TODO: Restore backup notification logs from an older CSV file.
// TODO: Prevent SQL injection. (may be able to crash application)
// TODO: Self-notification to test if notifications are still being logged.

public class MainActivity extends AppCompatActivity {

    public static final String TAG = "NLS";
    public static SQLiteDatabase db;
    public static RecyclerView.LayoutManager layoutManager;
    public static RecyclerView rvLogs;

    public static LogsAdapter logsAdapter;
    public static List<Logs> logsList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Getting some essential permissions:
        getBatteryAccess();
        getStorageAccess();
        getNotificationAccess();

        // Create DB:
        db = openOrCreateDatabase("nls", MODE_PRIVATE, null);
        db.execSQL("CREATE TABLE IF NOT EXISTS log(timeStamp INTEGER, packageName TEXT, title TEXT, text TEXT);");

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
        if (cursor.moveToFirst()) {
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

    public void clearDatabase(View view) {
        int size = logsList.size();
        logsList.clear();
        logsAdapter.notifyItemRangeRemoved(0, size);
        db.execSQL("DELETE FROM log;");
    }
    
    public void exportCsv(View view) {
        Cursor c = null;

        try {
            c = db.rawQuery("select * from log", null);
            int rowcount = 0;
            int colcount = 0;
            File sdCardDir = Environment.getExternalStorageDirectory();
            String filename = "notifications.csv";

            File saveFile = new File(sdCardDir, filename);
            FileWriter fw = new FileWriter(saveFile);

            BufferedWriter bw = new BufferedWriter(fw);
            rowcount = c.getCount();
            colcount = c.getColumnCount();
            if (rowcount > 0) {
                c.moveToFirst();

                for (int i=0; i<colcount; ++i) {
                    if (i != colcount-1)
                        bw.write(c.getColumnName(i) + ",");
                    else bw.write(c.getColumnName(i));
                }

                bw.newLine();

                for (int i=0; i<rowcount; ++i) {
                    c.moveToPosition(i);
                    for (int j=0; j<colcount; ++j) {
                        if (j != colcount-1)
                            bw.write(c.getString(j) + ",");
                        else bw.write(c.getString(j));
                    }
                    bw.newLine();
                }

                bw.flush();
                Toast.makeText(this, "Exported successfully.", Toast.LENGTH_LONG).show();
            }
        } catch (Exception ex) {
            if (db.isOpen()) {
                Toast.makeText(this, ex.getMessage(), Toast.LENGTH_LONG).show();
            }
        }
    }

    // ------------------------------------ PERMISSIONS: --------------------------------------- //

    private void getStorageAccess() {
        final int REQUEST_EXTERNAL_STORAGE = 1;
        String[] PERMISSIONS_STORAGE = {
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        };
        int permission = ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    this,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }

    void getBatteryAccess() {
        String packageName = this.getPackageName();
        PowerManager pm = (PowerManager) this.getSystemService(Context.POWER_SERVICE);
        if (!pm.isIgnoringBatteryOptimizations(packageName)) {
            Log.e(TAG, "[-] Battery access denied.");

            CustomDialog cd = new CustomDialog(new Intent(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS), "Battery access");
            cd.show(getSupportFragmentManager(), "BatteryAccessDialog");
        } else {
            Log.i(TAG, "[+] Battery access granted.");
        }
    }

    void getNotificationAccess() {

        NotificationManager nm = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        if (!nm.isNotificationPolicyAccessGranted()) {
            Log.i(TAG, "[-] Notification access denied.");

            CustomDialog cd = new CustomDialog(new Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS), "Notification access");
            cd.show(getSupportFragmentManager(), "NotificationAccessDialog");
        } else {
            Log.i(TAG, "[+] Notification access granted.");
        }
    }
}

