package com.example.mostafa.myapplication.BasicAndroidFunctionalities;


import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import com.example.mostafa.myapplication.UIs.MainActivity;


/**
 * Created by Mostafa on 6/24/2018.
 */

public class Flashlight {

    private boolean hasCameraFlash;
    private boolean isEnabled ;
    private boolean flashLightStatus=false;
    private Context c;


    public Flashlight(Context context){
        c=context;
        hasCameraFlash = context.getPackageManager().
                hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);
        isEnabled = ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED;
        if (!isEnabled)
        ActivityCompat.requestPermissions((Activity) context, new String[] {Manifest.permission.CAMERA},
                MainActivity.CAMERA_REQUEST);

    }
    public boolean flashLightOn() {
        if (!hasCameraFlash) return false;
        CameraManager cameraManager = (CameraManager) c.getSystemService(Context.CAMERA_SERVICE);
        try {
            String cameraId = null;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                cameraId = cameraManager.getCameraIdList()[0];
                cameraManager.setTorchMode(cameraId, true);
            }else return false;
            flashLightStatus = true;
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    public boolean flashLightOff() {
        if (!hasCameraFlash) return false;
        CameraManager cameraManager = (CameraManager) c.getSystemService(Context.CAMERA_SERVICE);
        try {
            String cameraId=null;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                cameraId = cameraManager.getCameraIdList()[0];
                cameraManager.setTorchMode(cameraId, false);
            }else return false;
            flashLightStatus = false;
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
