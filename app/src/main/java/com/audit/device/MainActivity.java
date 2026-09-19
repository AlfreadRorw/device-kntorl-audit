package com.audit.device;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.activity_main);

        TextView tv = findViewById(R.id.tvStatus);
        Button btnStart = findViewById(R.id.btnStart);
        Button btnStop = findViewById(R.id.btnStop);

        tv.setText("Device ID: " + DeviceInfo.getDeviceId(this) + "\nStatus: idle");

        btnStart.setOnClickListener(v -> {
            Intent svc = new Intent(this, AuditService.class);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) startForegroundService(svc);
            else startService(svc);
            tv.setText("Device ID: " + DeviceInfo.getDeviceId(this) + "\nStatus: RUNNING");
            Toast.makeText(this, "Audit started", Toast.LENGTH_SHORT).show();
        });

        btnStop.setOnClickListener(v -> {
            stopService(new Intent(this, AuditService.class));
            tv.setText("Device ID: " + DeviceInfo.getDeviceId(this) + "\nStatus: STOPPED");
            Toast.makeText(this, "Audit stopped", Toast.LENGTH_SHORT).show();
        });
    }
}
