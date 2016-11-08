package be.appreciate.buttonsforcleaners.loaders;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.Loader;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

/**
 * Created by Inneke De Clippel on 7/03/2016.
 */
public class LocationLoader extends Loader<Location> implements LocationListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener
{
    private Location lastLocation;
    private LocationRequest locationRequest;
    private GoogleApiClient apiClient;
    private int errorReason;
    private Handler handlerLocation;
    private Runnable callbackLocation;
    private long interval;

    private static final long TIMEOUT_LOCATION = 8000;

    public static final int ERROR_NO_ERROR = 0;
    public static final int ERROR_NO_PLAY_SERVICES = 1;
    public static final int ERROR_CONNECTION_SUSPENDED = 2;
    public static final int ERROR_CONNECTION_FAILED = 3;
    public static final int ERROR_LOCATION_DISABLED = 4;
    public static final int ERROR_TIMED_OUT = 5;
    public static final int ERROR_NO_PERMISSION = 6;

    public LocationLoader(Context context, long interval)
    {
        super(context);
        this.interval = interval;
    }

    @Override
    protected void onStartLoading()
    {
        if(this.lastLocation != null)
        {
            this.deliverResult(this.lastLocation);
        }
        else
        {
            this.createCallback();
            this.createLocationRequest();

            if(this.apiClient != null && this.apiClient.isConnected())
            {
                this.requestLocationUpdate();
            }
            else
            {
                this.createApiClientAndConnect();
            }
        }
    }

    @Override
    protected void onForceLoad()
    {
        this.createCallback();
        this.createLocationRequest();

        if(this.apiClient != null && this.apiClient.isConnected())
        {
            this.requestLocationUpdate();
        }
        else
        {
            this.createApiClientAndConnect();
        }
    }

    @Override
    protected void onReset()
    {
        this.disconnectApiClient();
        this.stopTimerLocation();
    }

    @Override
    public void onConnected(Bundle bundle)
    {
        this.requestLocationUpdate();
    }

    @Override
    public void onConnectionSuspended(int i)
    {
        this.errorReason = ERROR_CONNECTION_SUSPENDED;
        this.deliverResult(null);
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult)
    {
        this.errorReason = ERROR_CONNECTION_FAILED;
        this.deliverResult(null);
    }

    @Override
    public void onLocationChanged(Location location)
    {
        this.stopTimerLocation();

        if(this.interval <= 0 && this.apiClient != null && this.apiClient.isConnected())
        {
            LocationServices.FusedLocationApi.removeLocationUpdates(this.apiClient, this);
        }

        this.lastLocation = location;
        this.errorReason = ERROR_NO_ERROR;
        this.deliverResult(location);
    }

    public int getErrorReason()
    {
        return this.errorReason;
    }

    private void createCallback()
    {
        if(this.handlerLocation == null)
        {
            this.handlerLocation = new Handler();
        }

        if(this.callbackLocation == null)
        {
            this.callbackLocation = () ->
            {
                if (this.apiClient != null && this.apiClient.isConnected())
                {
                    LocationServices.FusedLocationApi.removeLocationUpdates(this.apiClient, this);
                    this.errorReason = ERROR_TIMED_OUT;
                    this.deliverResult(null);
                }
            };
        }
    }

    private void createLocationRequest()
    {
        if(this.locationRequest == null)
        {
            this.locationRequest = new LocationRequest();
            this.locationRequest.setInterval(this.interval);
            this.locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        }
    }

    private void createApiClientAndConnect()
    {
        if(this.apiClient == null)
        {
            if (this.hasPlayServices())
            {
                this.apiClient = new GoogleApiClient.Builder(this.getContext())
                        .addConnectionCallbacks(this)
                        .addOnConnectionFailedListener(this)
                        .addApi(LocationServices.API)
                        .build();

                this.apiClient.connect();
            }
            else
            {
                this.errorReason = ERROR_NO_PLAY_SERVICES;
                this.deliverResult(null);
            }
        }
        else if(!this.apiClient.isConnected())
        {
            this.apiClient.connect();
        }
    }

    private void disconnectApiClient()
    {
        if(this.apiClient != null && this.apiClient.isConnected())
        {
            LocationServices.FusedLocationApi.removeLocationUpdates(this.apiClient, this);
            this.apiClient.disconnect();
        }
    }

    private void requestLocationUpdate()
    {
        if(ContextCompat.checkSelfPermission(this.getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            this.errorReason = ERROR_NO_PERMISSION;
            this.deliverResult(null);
        }
        else
        {
            LocationServices.FusedLocationApi.requestLocationUpdates(this.apiClient, this.locationRequest, this);

            LocationManager lm = (LocationManager) this.getContext().getSystemService(Context.LOCATION_SERVICE);
            boolean gpsEnabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
            boolean networkEnabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if (!gpsEnabled && !networkEnabled)
            {
                this.errorReason = ERROR_LOCATION_DISABLED;
                this.deliverResult(null);
            }
            else if(this.interval <= 0)
            {
                this.stopTimerLocation();
                this.startTimerLocation();
            }
        }
    }

    private void startTimerLocation()
    {
        if(this.handlerLocation != null && this.callbackLocation != null)
        {
            this.handlerLocation.postDelayed(this.callbackLocation, TIMEOUT_LOCATION);
        }
    }

    private void stopTimerLocation()
    {
        if(this.handlerLocation != null && this.callbackLocation != null)
        {
            this.handlerLocation.removeCallbacks(this.callbackLocation);
        }
    }

    private boolean hasPlayServices()
    {
        try
        {
            return GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(this.getContext()) == 0;
        }
        catch (NoClassDefFoundError e)
        {
            return false;
        }
    }
}
