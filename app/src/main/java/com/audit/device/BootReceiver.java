package com.audit.device;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;

public class BootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context ctx, Intent i) {
        SharedPreferences sp = ctx.getSharedPreferences("audit_prefs", Context.MODE_PRIVATE);
        if (!sp.getBoolean("consent_given", false)) return;
        Intent svc = new Intent(ctx, AuditService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) ctx.startForegroundService(svc);
        else ctx.startService(svc);
    }
}
