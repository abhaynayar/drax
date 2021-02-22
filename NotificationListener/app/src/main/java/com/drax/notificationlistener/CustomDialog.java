package com.drax.notificationlistener;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

public class CustomDialog extends DialogFragment {

    Intent intent;
    String msg;

    public CustomDialog(Intent intent, String msg) {
        this.intent = intent;
        this.msg = msg;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Permission Dialog")
                .setMessage(msg)
                .setPositiveButton("GRANT", (dialog, id) -> {
                    startActivity(intent);
                })
                .setNegativeButton("CANCEL", (dialog, id) -> {
                    Toast.makeText(getContext(), "Won't work without permission", Toast.LENGTH_LONG).show();
                });
        return builder.create();
    }
}


