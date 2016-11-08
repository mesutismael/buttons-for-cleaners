package be.appreciate.buttonsforcleaners.application;

import android.app.Application;

import com.crashlytics.android.Crashlytics;

import be.appreciate.buttonsforcleaners.BuildConfig;
import io.fabric.sdk.android.Fabric;

/**
 * Created by Inneke De Clippel on 8/04/2016.
 */
public class BfcApplication extends Application
{
    @Override
    public void onCreate()
    {
        super.onCreate();

        if(!BuildConfig.DEBUG)
        {
            Fabric.with(this, new Crashlytics());
        }
    }
}
