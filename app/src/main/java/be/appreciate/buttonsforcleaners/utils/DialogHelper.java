package be.appreciate.buttonsforcleaners.utils;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;

/**
 * Created by Inneke De Clippel on 2/03/2016.
 */
public class DialogHelper
{
    public static boolean canShowDialog(Fragment fragment)
    {
        FragmentActivity activity = fragment.getActivity();
        boolean finishing = activity == null || activity.isFinishing();
        boolean destroyed = activity == null || activity.getSupportFragmentManager() == null || activity.getSupportFragmentManager().isDestroyed();

        return !finishing && !destroyed;
    }
}
