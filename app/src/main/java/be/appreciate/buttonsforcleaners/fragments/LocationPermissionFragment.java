package be.appreciate.buttonsforcleaners.fragments;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.afollestad.materialdialogs.MaterialDialog;

import be.appreciate.buttonsforcleaners.R;
import be.appreciate.buttonsforcleaners.activities.LoginActivity;
import be.appreciate.buttonsforcleaners.activities.MainActivity;
import be.appreciate.buttonsforcleaners.utils.PermissionHelper;
import be.appreciate.buttonsforcleaners.utils.PreferencesHelper;

/**
 * Created by Inneke De Clippel on 3/03/2016.
 */
public class LocationPermissionFragment extends Fragment implements View.OnClickListener
{
    private static final int REQUEST_CODE_PERMISSION = 1;

    public static LocationPermissionFragment newInstance()
    {
        return new LocationPermissionFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_location_permission, container, false);

        Button buttonShowDialog = (Button) view.findViewById(R.id.button_showPermission);

        buttonShowDialog.setOnClickListener(this);

        return view;
    }

    @Override
    public void onResume()
    {
        super.onResume();

        if(this.getContext() != null && PermissionHelper.hasLocationPermission(this.getContext()))
        {
            this.startNextActivity();
        }
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.button_showPermission:
                this.requestPermission();
                break;
        }
    }

    private void requestPermission()
    {
        String[] permissions = new String[]{Manifest.permission.ACCESS_COARSE_LOCATION};
        this.requestPermissions(permissions, REQUEST_CODE_PERMISSION);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults)
    {
        switch (requestCode)
        {
            case REQUEST_CODE_PERMISSION:
                if(PermissionHelper.allPermissionsGranted(grantResults))
                {
                    this.startNextActivity();
                }
                else
                {
                    this.showSettingsDialog();
                }
                break;
        }
    }

    private void showSettingsDialog()
    {
        if(this.getContext() != null)
        {
            new MaterialDialog.Builder(this.getContext())
                    .title(R.string.permission_location_declined_title)
                    .content(R.string.permission_location_declined_message)
                    .positiveText(R.string.permission_location_declined_positive)
                    .negativeText(R.string.dialog_negative)
                    .onPositive((dialog, which) ->
                    {
                        if (this.getContext() != null)
                        {
                            Intent settingsIntent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                            settingsIntent.setData(Uri.parse("package:" + this.getContext().getPackageName()));
                            this.startActivity(settingsIntent);
                        }
                    })
                    .show();
        }
    }

    private void startNextActivity()
    {
        if(this.getContext() != null)
        {
            boolean loggedIn = PreferencesHelper.isLoggedIn(this.getContext());

            Intent intent = loggedIn ? MainActivity.getIntent(this.getContext()) : LoginActivity.getIntent(this.getContext());
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            this.startActivity(intent);
        }
    }
}
