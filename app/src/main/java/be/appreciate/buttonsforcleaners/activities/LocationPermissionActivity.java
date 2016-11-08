package be.appreciate.buttonsforcleaners.activities;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import be.appreciate.buttonsforcleaners.R;
import be.appreciate.buttonsforcleaners.fragments.LocationPermissionFragment;

/**
 * Created by Inneke De Clippel on 3/03/2016.
 */
public class LocationPermissionActivity extends AppCompatActivity
{
    public static Intent getIntent(Context context)
    {
        return new Intent(context, LocationPermissionActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        this.setRequestedOrientation(this.getResources().getBoolean(R.bool.tablet) ? ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE : ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        this.setContentView(R.layout.activity_location_permission);

        this.getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.frameLayout_content, LocationPermissionFragment.newInstance())
                .commit();
    }
}
