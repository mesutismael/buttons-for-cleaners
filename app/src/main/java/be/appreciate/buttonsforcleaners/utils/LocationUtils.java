package be.appreciate.buttonsforcleaners.utils;

import android.content.Context;
import android.location.Location;

import be.appreciate.buttonsforcleaners.R;

/**
 * Created by Inneke De Clippel on 7/03/2016.
 */
public class LocationUtils
{
    public static boolean isDifferentLocation(Location location1, Location location2)
    {
        if(location1 == null && location2 == null)
        {
            return false;
        }
        else if((location1 == null) != (location2 == null))
        {
            return true;
        }
        else
        {
            return location1.getLatitude() != location2.getLatitude() || location1.getLongitude() != location2.getLongitude();
        }
    }

    public static String getFormattedDistance(Context context, Location currentLocation, double latitude, double longitude)
    {
        if(currentLocation == null || (latitude == 0 && longitude == 0))
        {
            return null;
        }
        else
        {
            float distanceInMeter = LocationUtils.getDistanceBetween(currentLocation, latitude, longitude);
            return context.getString(R.string.planning_list_item_distance, distanceInMeter / 1000);
        }
    }

    public static float getDistanceBetween(Location currentLocation, double latitude, double longitude)
    {
        if(currentLocation == null)
        {
            return -1;
        }

        float[] distance = new float[1];
        Location.distanceBetween(currentLocation.getLatitude(), currentLocation.getLongitude(), latitude, longitude, distance);
        return distance[0];
    }
}
