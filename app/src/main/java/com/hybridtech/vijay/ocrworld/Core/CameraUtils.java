package com.hybridtech.vijay.ocrworld.Core;

import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.util.Log;

public class CameraUtils {

    static final String TAG = "DBG_ " + CameraUtils.class.getName();

    public static boolean deviceHasCamera(Context context) {
        return context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA);
    }

    public static boolean isCameraAvailable(Context context) {
        return context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY);
    }

    public static Camera getCamera() {
        try {
            return Camera.open();
        } catch (Exception e) {
            Log.e(TAG, "Cannot getCamera()");
            return null;
        }
    }
}
