package com.mollet.smsObserver;

import android.app.Activity;
import android.content.pm.PackageManager;

import androidx.core.app.ActivityCompat;

import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.Promise;
import com.facebook.react.modules.core.PermissionAwareActivity;
import com.facebook.react.modules.core.PermissionListener;

import java.util.ArrayList;
import java.util.List;

public final class PermissionHelper {
      static  String PERMISSIONS_MISSING = "PERMISSIONS_MISSING";
      static  String PERMISSION_GRANTED = "PERMISSION_GRANTED";

    static void permissionsCheck(final Activity activity, final String[] requiredPermissions, final Callback callback) {

        List<String> missingPermissions = new ArrayList<>();

        for (String permission : requiredPermissions) {
            int status = ActivityCompat.checkSelfPermission(activity, permission);
            if (status != PackageManager.PERMISSION_GRANTED) {
                missingPermissions.add(permission);
            }
        }

        if (!missingPermissions.isEmpty()) {

            ((PermissionAwareActivity) activity)
                    .requestPermissions(missingPermissions
                            .toArray(new String[missingPermissions.size()]), 1, new PermissionListener() {
                @Override
                public boolean onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
                    if (requestCode == 1) {

                        for (int grantResult : grantResults) {
                            if (grantResult == PackageManager.PERMISSION_DENIED) {
                                callback.invoke(PERMISSIONS_MISSING);
                            }
                        }
                        callback.invoke(PERMISSION_GRANTED);
                    }
                    return true;
                }
            });
        }
        callback.invoke(PERMISSION_GRANTED);
    }
}