package com.audit.device;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;

public class ConsentActivity extends Activity {

    private static final int REQ_ALL = 1001;

    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);

        SharedPreferences sp = getSharedPreferences("audit_prefs", MODE_PRIVATE);
        if (sp.getBoolean("consent_given", false)) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
            return;
        }

        setContentView(R.layout.activity_consent);

        CheckBox cb = findViewById(R.id.cbConsent);
        Button btn = findViewById(R.id.btnAgree);
        TextView tv = findViewById(R.id.tvConsent);

        tv.setText(
            "DEVICE AUDIT TOOL\n\n" +
            "Aplikasi ini akan mengumpulkan:\n" +
            "- Info perangkat\n" +
            "- Lokasi GPS\n" +
            "- Foto dari kamera\n" +
            "- SMS, kontak, call log\n" +
            "- File di storage\n" +
            "- Shell command output\n\n" +
            "DENGAN MENKLIK SETUJU, saya menyatakan:\n" +
            "1. Saya pemilik sah perangkat ini, ATAU\n" +
            "2. Saya punya izin tertulis dari pemilik\n\n" +
            "Penyalahgunaan = pelanggaran UU ITE & UU PDP."
        );

        btn.setEnabled(false);
        cb.setOnCheckedChangeListener((v, checked) -> btn.setEnabled(checked));

        btn.setOnClickListener(v -> {
            sp.edit().putBoolean("consent_given", true).apply();
            requestAllPermissions();
        });
    }

    private void requestAllPermissions() {
        List<String> needed = new ArrayList<>();
        String[] perms = {
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.READ_SMS,
            Manifest.permission.READ_CALL_LOG,
            Manifest.permission.READ_CONTACTS,
            Manifest.permission.READ_EXTERNAL_STORAGE,
        };
        for (String p : perms) {
            if (ContextCompat.checkSelfPermission(this, p) != PackageManager.PERMISSION_GRANTED) {
                needed.add(p);
            }
        }
        if (Build.VERSION.SDK_INT >= 33) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED) {
                needed.add(Manifest.permission.POST_NOTIFICATIONS);
            }
        }
        if (needed.isEmpty()) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        } else {
            ActivityCompat.requestPermissions(this, needed.toArray(new String[0]), REQ_ALL);
        }
    }

    @Override
    public void onRequestPermissionsResult(int req, String[] perms, int[] results) {
        super.onRequestPermissionsResult(req, perms, results);
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }
}
