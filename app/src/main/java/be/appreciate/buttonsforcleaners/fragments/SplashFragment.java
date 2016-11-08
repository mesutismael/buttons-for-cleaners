package be.appreciate.buttonsforcleaners.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import be.appreciate.buttonsforcleaners.R;
import be.appreciate.buttonsforcleaners.activities.LocationPermissionActivity;
import be.appreciate.buttonsforcleaners.activities.LoginActivity;
import be.appreciate.buttonsforcleaners.activities.MainActivity;
import be.appreciate.buttonsforcleaners.activities.SettingsTextActivity;
import be.appreciate.buttonsforcleaners.api.ApiHelper;
import be.appreciate.buttonsforcleaners.utils.Observer;
import be.appreciate.buttonsforcleaners.utils.PermissionHelper;
import be.appreciate.buttonsforcleaners.utils.PreferencesHelper;

/**
 * Created by Inneke De Clippel on 2/03/2016.
 */
public class SplashFragment extends Fragment
{
    private boolean timePassed;
    private boolean apiCallsDone;
    private boolean loggedIn;

    private static final long SPLASH_TIMEOUT = 1500;

    public static SplashFragment newInstance()
    {
        return new SplashFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_splash, container, false);

        ProgressBar progressBar = (ProgressBar) view.findViewById(R.id.progressBar);

        this.loggedIn = PreferencesHelper.isLoggedIn(view.getContext());

        progressBar.setVisibility(this.loggedIn ? View.VISIBLE : View.GONE);

        this.startTimeOutHandler();

        if(this.loggedIn)
        {
            this.startApiCalls(view.getContext());
        }
        else
        {
            this.apiCallsDone = true;
        }

        return view;
    }

    private void startTimeOutHandler()
    {
        new Handler().postDelayed(() -> {
            this.timePassed = true;
            this.startNextActivity();
        }, SPLASH_TIMEOUT);
    }

    private void startApiCalls(Context context)
    {
        ApiHelper.doStartUpCalls(context).subscribe(this.startUpObserver);
    }

    private void startNextActivity()
    {
        if(this.timePassed && this.apiCallsDone && this.getContext() != null)
        {
            Intent intent;

            if(!PreferencesHelper.isStartupSettingsCompleted(this.getContext()))
            {
                intent = SettingsTextActivity.getIntent(this.getContext());
            }
            else if(!PermissionHelper.hasLocationPermission(this.getContext()))
            {
                intent = LocationPermissionActivity.getIntent(this.getContext());
            }
            else if(this.loggedIn)
            {
                intent = MainActivity.getIntent(this.getContext());
            }
            else
            {
                intent = LoginActivity.getIntent(this.getContext());
            }

            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            this.startActivity(intent);
        }
    }

    private Observer<Object> startUpObserver = new Observer<Object>()
    {
        @Override
        public void onCompleted()
        {
            SplashFragment.this.apiCallsDone = true;
            SplashFragment.this.startNextActivity();
        }

        @Override
        public void onError(Throwable e)
        {
            SplashFragment.this.apiCallsDone = true;
            SplashFragment.this.startNextActivity();
        }
    };
}
