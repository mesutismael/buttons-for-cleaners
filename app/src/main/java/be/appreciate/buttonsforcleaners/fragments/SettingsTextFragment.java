package be.appreciate.buttonsforcleaners.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import be.appreciate.buttonsforcleaners.R;
import be.appreciate.buttonsforcleaners.activities.LocationPermissionActivity;
import be.appreciate.buttonsforcleaners.activities.LoginActivity;
import be.appreciate.buttonsforcleaners.activities.MainActivity;
import be.appreciate.buttonsforcleaners.utils.PermissionHelper;
import be.appreciate.buttonsforcleaners.utils.PreferencesHelper;

/**
 * Created by Inneke De Clippel on 24/03/2016.
 */
public class SettingsTextFragment extends Fragment implements View.OnClickListener
{
    public static SettingsTextFragment newInstance()
    {
        return new SettingsTextFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_text_settings, container, false);

        Button buttonSettings = (Button) view.findViewById(R.id.button_settings);
        Button buttonSkip = (Button) view.findViewById(R.id.button_skip);

        buttonSettings.setOnClickListener(this);
        buttonSkip.setOnClickListener(this);

        PreferencesHelper.saveStartupSettingsCompleted(view.getContext(), true);

        return view;
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.button_settings:
                this.openSettings();
                break;

            case R.id.button_skip:
                this.startNextActivity();
                break;
        }
    }

    private void openSettings()
    {
        Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
        this.startActivity(intent);
    }

    private void startNextActivity()
    {
        if(this.getContext() != null)
        {
            Intent intent;

            if(!PermissionHelper.hasLocationPermission(this.getContext()))
            {
                intent = LocationPermissionActivity.getIntent(this.getContext());
            }
            else if(PreferencesHelper.isLoggedIn(this.getContext()))
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
}
