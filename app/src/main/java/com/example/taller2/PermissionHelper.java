package com.example.taller2;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

class PermissionHelper {
    public static void requestPermission(Activity activity, String permission, int permission_code, String rationale) {
        if (!hasPermission(activity, permission)) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)) {
                Toast.makeText(activity, rationale, Toast.LENGTH_LONG).show();
            }
            ActivityCompat.requestPermissions(activity, new String[]{permission}, permission_code);
        }
    }
    public static boolean hasPermission(Activity activity, String permission) {
        return ContextCompat.checkSelfPermission(activity, permission) ==
                PackageManager.PERMISSION_GRANTED;
    }
}
