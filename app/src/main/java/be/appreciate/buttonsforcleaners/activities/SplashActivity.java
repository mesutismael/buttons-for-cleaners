package be.appreciate.buttonsforcleaners.activities;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import be.appreciate.buttonsforcleaners.R;
import be.appreciate.buttonsforcleaners.fragments.SplashFragment;

/**
 * Created by Inneke De Clippel on 2/03/2016.
 */
public class SplashActivity extends AppCompatActivity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        this.setRequestedOrientation(this.getResources().getBoolean(R.bool.tablet) ? ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE : ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        this.setContentView(R.layout.activity_splash);

        this.getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.frameLayout_content, SplashFragment.newInstance())
                .commit();
    }
}
