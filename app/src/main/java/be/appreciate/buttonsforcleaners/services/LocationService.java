package be.appreciate.buttonsforcleaners.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.content.Loader;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

import be.appreciate.buttonsforcleaners.asynctasks.UpdateDistanceAsyncTask;
import be.appreciate.buttonsforcleaners.loaders.LocationLoader;
import be.appreciate.buttonsforcleaners.model.PointOfTime;
import be.appreciate.buttonsforcleaners.utils.LocationUtils;
import be.appreciate.buttonsforcleaners.utils.PreferencesHelper;

/**
 * Created by Inneke De Clippel on 25/03/2016.
 */
public class LocationService extends Service implements Loader.OnLoadCompleteListener<Location>
{
    private List<StartedPlanningItem> startedPlanningItems;
    private LocationLoader locationLoader;

    private static final String KEY_PLANNING_ID = "planning_id";
    private static final String KEY_POINT_OF_TIME = "point_of_time";
    private static final String KEY_LATITUDE = "latitude";
    private static final String KEY_LONGITUDE = "longitude";

    private static final long LOCATION_INTERVAL = 2 * 60 * 1000; //Every 2 minutes

    public static Intent getIntent(Context context, int planningId, PointOfTime pointOfTime, Location location)
    {
        Bundle bundle = new Bundle();
        bundle.putInt(KEY_PLANNING_ID, planningId);
        bundle.putInt(KEY_POINT_OF_TIME, (pointOfTime != null ? pointOfTime : PointOfTime.START).getPointOfTimeId());
        bundle.putDouble(KEY_LATITUDE, location != null ? location.getLatitude() : 0);
        bundle.putDouble(KEY_LONGITUDE, location != null ? location.getLongitude() : 0);

        Intent intent = new Intent(context, LocationService.class);
        intent.putExtras(bundle);
        return intent;
    }

    @Override
    public void onCreate()
    {
        super.onCreate();

        this.locationLoader = new LocationLoader(this, LOCATION_INTERVAL);
        this.locationLoader.registerListener(0, this);
        this.locationLoader.startLoading();
    }

    @Override
    public void onDestroy()
    {
        if (this.locationLoader != null)
        {
            this.locationLoader.unregisterListener(this);
            this.locationLoader.reset();
            this.locationLoader.cancelLoad();
            this.locationLoader.stopLoading();
        }

        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent)
    {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        if(intent == null)
        {
            this.restoreState();
        }
        else
        {
            int planningId = intent.getIntExtra(KEY_PLANNING_ID, 0);
            PointOfTime pointOfTime = PointOfTime.getPointOfTime(intent.getIntExtra(KEY_POINT_OF_TIME, PointOfTime.START.getPointOfTimeId()));
            double latitude = intent.getDoubleExtra(KEY_LATITUDE, 0);
            double longitude = intent.getDoubleExtra(KEY_LONGITUDE, 0);

            switch (pointOfTime)
            {
                case START:
                    this.addItem(planningId, latitude, longitude);
                    break;

                case STOP:
                    this.removeItem(planningId);
                    break;
            }

            this.saveState();
        }

        if (this.startedPlanningItems == null || this.startedPlanningItems.isEmpty())
        {
            this.stopSelf();
        }

        return START_STICKY;
    }

    @Override
    public void onLoadComplete(Loader<Location> loader, Location data)
    {
        this.updateDistances(data);
    }

    private void addItem(int planningId, double latitude, double longitude)
    {
        StartedPlanningItem itemInList = this.getItem(planningId);

        if (itemInList != null)
        {
            itemInList.latitude = latitude;
            itemInList.longitude = longitude;
            itemInList.distanceTraveled = 0;
        }
        else
        {
            if(this.startedPlanningItems == null)
            {
                this.startedPlanningItems = new ArrayList<>();
            }

            this.startedPlanningItems.add(new StartedPlanningItem(planningId, latitude, longitude));
        }
    }

    private void removeItem(int planningId)
    {
        StartedPlanningItem itemInList = this.getItem(planningId);

        if (itemInList != null)
        {
            this.startedPlanningItems.remove(itemInList);
        }
    }

    private StartedPlanningItem getItem(int planningId)
    {
        if(this.startedPlanningItems != null)
        {
            for(StartedPlanningItem item : this.startedPlanningItems)
            {
                if(item != null && item.planningId == planningId)
                {
                    return item;
                }
            }
        }

        return null;
    }

    private void updateDistances(Location location)
    {
        if(location != null && this.startedPlanningItems != null)
        {
            for(StartedPlanningItem item : this.startedPlanningItems)
            {
                if(item != null)
                {
                    boolean distanceUpdated = item.updateDistance(location);

                    if (distanceUpdated)
                    {
                        UpdateDistanceAsyncTask task = new UpdateDistanceAsyncTask(this, item.planningId, item.distanceTraveled);
                        task.execute();
                    }
                }
            }
        }
    }

    private void saveState()
    {
        String state = new Gson().toJson(this.startedPlanningItems);
        PreferencesHelper.saveLocationServiceState(this, state);
    }

    private void restoreState()
    {
        String state = PreferencesHelper.getLocationServiceState(this);
        this.startedPlanningItems = new Gson().fromJson(state, new TypeToken<ArrayList<StartedPlanningItem>>() {}.getType());
    }

    private class StartedPlanningItem
    {
        private int planningId;
        private double latitude;
        private double longitude;
        private int distanceTraveled;

        public StartedPlanningItem(int planningId, double latitude, double longitude)
        {
            this.planningId = planningId;
            this.latitude = latitude;
            this.longitude = longitude;
        }

        public boolean updateDistance(Location location)
        {
            if(this.latitude != 0 || this.longitude != 0)
            {
                int distance = (int) LocationUtils.getDistanceBetween(location, this.latitude, this.longitude);

                if(distance > this.distanceTraveled)
                {
                    this.distanceTraveled = distance;
                    return true;
                }
            }

            return false;
        }
    }
}
