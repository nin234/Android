package com.rekhaninan.common;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.content.Context;

/**
 * Created by ninanthomas on 12/30/16.
 */

public class PermissionsManager {

    private final  int CAMERA_PERMISSIONS_CODE =1;
    private final  int AUDIO_RECORD_PERMISSIONS_CODE =2;
    private final  int COARSE_LOCATION_PERMISSIONS_CODE =3;
    private final int FINE_LOCATION_PERMISSIONS_CODE = 4;
    private final int PERMISSIONS_ALL = 5;

    private static final PermissionsManager INSTANCE = new PermissionsManager();

    private PermissionsManager() {}

    public static PermissionsManager getInstance() {
        return INSTANCE;
    }

    public boolean hasCameraPermission(Context ctxt)
    {
        if (ContextCompat.checkSelfPermission(ctxt, android.Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED)
        {
            return true;
        }

        return false;
    }

    public boolean hasAudioRecordPermission(Context ctxt)
    {
        if (ContextCompat.checkSelfPermission(ctxt, Manifest.permission.RECORD_AUDIO)
                == PackageManager.PERMISSION_GRANTED)
        {
            return true;
        }

        return false;
    }

    public boolean hasLocationPermission(Context ctxt)
    {
        if (ContextCompat.checkSelfPermission(ctxt, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(ctxt, Manifest.permission.ACCESS_COARSE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED)
        {

            return true;
        }

        return false;
    }

    public void requestPermissionIfReqd(Context ctxt, Activity acty)
    {
        boolean reqPermission = false;
        if (ContextCompat.checkSelfPermission(ctxt, android.Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED)
        {
            reqPermission = true;
        }

        if (!reqPermission && ContextCompat.checkSelfPermission(ctxt, Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED)
        {
            reqPermission = true;
        }

        if (!reqPermission && ContextCompat.checkSelfPermission(ctxt, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED)
        {
            reqPermission = true;
        }

        if (!reqPermission && ContextCompat.checkSelfPermission(ctxt, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED)
        {
            reqPermission = true;
        }

        String Permissions[] = {Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO,
                Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};
        if (reqPermission) {
            ActivityCompat.requestPermissions(acty, Permissions, PERMISSIONS_ALL);
        }
    }

}
