package com.audit.device;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;

import androidx.core.content.ContextCompat;

import java.util.HashMap;
import java.util.Map;

public class LocationHelper {
    public static Map<String, Object> getLastLocation(Context ctx) {
        Map<String, Object> m = new HashMap<>();
        if (ContextCompat.checkSelfPermission(ctx, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED) {
            m.put("error", "no permission");
            return m;
        }
        try {
            LocationManager lm = (LocationManager) ctx.getSystemService(Context.LOCATION_SERVICE);
            Location loc = null;
            if (lm != null) {
                loc = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                if (loc == null) loc = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            }
            if (loc != null) {
                m.put("lat", loc.getLatitude());
                m.put("lng", loc.getLongitude());
                m.put("acc", loc.getAccuracy());
                m.put("ts", loc.getTime());
            } else m.put("error", "no location");
        } catch (Exception e) {
            m.put("error", e.getMessage());
        }
        return m;
    }
}
