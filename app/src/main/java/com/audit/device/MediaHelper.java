package com.audit.device;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.util.Base64;

import androidx.core.content.ContextCompat;

import java.io.ByteArrayOutputStream;

public class MediaHelper {

    @SuppressWarnings("deprecation")
    public static String takePhoto(Context ctx) {
        if (ContextCompat.checkSelfPermission(ctx, Manifest.permission.CAMERA)
            != PackageManager.PERMISSION_GRANTED) return "no permission";
        Camera cam = null;
        try {
            cam = Camera.open(0);
            Camera.Parameters p = cam.getParameters();
            p.setPictureFormat(ImageFormat.JPEG);
            cam.setParameters(p);
            cam.startPreview();
            Thread.sleep(500);
            final ByteArrayOutputStream bos = new ByteArrayOutputStream();
            cam.takePicture(null, null, (data, camera) -> {
                try { bos.write(data); } catch (Exception ignored) {}
            });
            Thread.sleep(1500);
            cam.stopPreview();
            cam.release();
            byte[] jpeg = bos.toByteArray();
            if (jpeg.length == 0) return "empty";
            if (jpeg.length > 200000) return "too big: " + jpeg.length;
            return "data:image/jpeg;base64," + Base64.encodeToString(jpeg, Base64.NO_WRAP);
        } catch (Exception e) {
            if (cam != null) try { cam.release(); } catch (Exception ignored) {}
            return "err: " + e.getMessage();
        }
    }
}
