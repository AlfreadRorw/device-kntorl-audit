package com.audit.device;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class CommandReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context ctx, Intent i) {
        ctx.startService(new Intent(ctx, AuditService.class));
    }
}
