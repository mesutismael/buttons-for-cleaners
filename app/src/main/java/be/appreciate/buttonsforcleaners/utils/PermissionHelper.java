package be.appreciate.buttonsforcleaners.utils;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.content.ContextCompat;

/**
 * Created by Inneke De Clippel on 3/03/2016.
 */
public class PermissionHelper
{
    public static boolean hasLocationPermission(Context context)
    {
        return ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    public static boolean allPermissionsGranted(int[] grantResults)
    {
        if(grantResults != null)
        {
            for(int result : grantResults)
            {
                if(result != PackageManager.PERMISSION_GRANTED)
                {
                    return false;
                }
            }
        }

        return true;
    }
}
