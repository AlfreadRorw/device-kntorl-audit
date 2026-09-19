package com.audit.device;

import android.content.Context;
import android.os.Build;
import android.provider.Settings;

import java.util.HashMap;
import java.util.Map;

public class DeviceInfo {

    public static String getDeviceId(Context ctx) {
        String id = Settings.Secure.getString(ctx.getContentResolver(), Settings.Secure.ANDROID_ID);
        if (id == null || id.isEmpty()) id = "unknown";
        return id;
    }

    public static Map<String, Object> collect(Context ctx) {
        Map<String, Object> m = new HashMap<>();
        m.put("device_id", getDeviceId(ctx));
        m.put("model", Build.MODEL);
        m.put("manufacturer", Build.MANUFACTURER);
        m.put("brand", Build.BRAND);
        m.put("android", Build.VERSION.RELEASE);
        m.put("sdk", Build.VERSION.SDK_INT);
        m.put("fingerprint", Build.FINGERPRINT);
        m.put("last_seen", System.currentTimeMillis());
        return m;
    }
}
