package be.appreciate.buttonsforcleaners.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

/**
 * Created by Inneke De Clippel on 1/03/2016.
 */
public class PreferencesHelper
{
    private static final String PREFERENCES_NAME = "BfcPrefs";

    private static final String PREFERENCE_STARTUP_CALLS_COMPLETED = "startup_calls_completed";
    private static final String PREFERENCE_STARTUP_SETTINGS_COMPLETED = "startup_settings_completed";
    private static final String PREFERENCE_DOMAIN = "domain";
    private static final String PREFERENCE_USER_ID = "user_id";
    private static final String PREFERENCE_USER_NAME = "user_name";
    private static final String PREFERENCE_USER_TYPE = "user_type";
    private static final String PREFERENCE_USER_IMAGE_URL = "user_image_url";
    private static final String PREFERENCE_USER_PHONE = "user_phone";
    private static final String PREFERENCE_USER_LOGO_URL = "user_logo_url";
    private static final String PREFERENCE_LAST_PLANNING_REFRESH = "last_planning_refresh";
    private static final String PREFERENCE_APPLICATION_COOKIE = "application_cookie";
    private static final String PREFERENCE_LOCATION_SERVICE_STATE = "location_service_state";

    public static SharedPreferences getPreferences(Context context)
    {
        return context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
    }

    public static boolean isLoggedIn(Context context)
    {
        String domain = PreferencesHelper.getDomain(context);
        int userId = PreferencesHelper.getUserId(context);
        boolean startupCallsCompleted = PreferencesHelper.isStartupCallsCompleted(context);

        return !TextUtils.isEmpty(domain) && userId != 0 && startupCallsCompleted;
    }

    public static void clearUser(Context context)
    {
        SharedPreferences.Editor prefs = PreferencesHelper.getPreferences(context).edit();

        prefs.remove(PREFERENCE_STARTUP_CALLS_COMPLETED);
        prefs.remove(PREFERENCE_USER_ID);
        prefs.remove(PREFERENCE_USER_NAME);
        prefs.remove(PREFERENCE_USER_TYPE);
        prefs.remove(PREFERENCE_USER_IMAGE_URL);
        prefs.remove(PREFERENCE_USER_PHONE);
        prefs.remove(PREFERENCE_USER_LOGO_URL);
        prefs.remove(PREFERENCE_LAST_PLANNING_REFRESH);
        prefs.remove(PREFERENCE_APPLICATION_COOKIE);
        prefs.remove(PREFERENCE_LOCATION_SERVICE_STATE);

        prefs.apply();
    }

    // --- Getters and setters ---

    public static boolean isStartupCallsCompleted(Context context)
    {
        return PreferencesHelper.getPreferences(context).getBoolean(PREFERENCE_STARTUP_CALLS_COMPLETED, false);
    }

    public static void saveStartupCallsCompleted(Context context, boolean completed)
    {
        SharedPreferences.Editor prefs = PreferencesHelper.getPreferences(context).edit();
        prefs.putBoolean(PREFERENCE_STARTUP_CALLS_COMPLETED, completed);
        prefs.apply();
    }

    public static boolean isStartupSettingsCompleted(Context context)
    {
        return PreferencesHelper.getPreferences(context).getBoolean(PREFERENCE_STARTUP_SETTINGS_COMPLETED, false);
    }

    public static void saveStartupSettingsCompleted(Context context, boolean completed)
    {
        SharedPreferences.Editor prefs = PreferencesHelper.getPreferences(context).edit();
        prefs.putBoolean(PREFERENCE_STARTUP_SETTINGS_COMPLETED, completed);
        prefs.apply();
    }

    public static String getDomain(Context context)
    {
        return PreferencesHelper.getPreferences(context).getString(PREFERENCE_DOMAIN, null);
    }

    public static void saveDomain(Context context, String domain)
    {
        SharedPreferences.Editor prefs = PreferencesHelper.getPreferences(context).edit();
        prefs.putString(PREFERENCE_DOMAIN, domain);
        prefs.apply();
    }

    public static int getUserId(Context context)
    {
        return PreferencesHelper.getPreferences(context).getInt(PREFERENCE_USER_ID, 0);
    }

    public static void saveUserId(Context context, int userId)
    {
        SharedPreferences.Editor prefs = PreferencesHelper.getPreferences(context).edit();
        prefs.putInt(PREFERENCE_USER_ID, userId);
        prefs.apply();
    }

    public static String getUserName(Context context)
    {
        return PreferencesHelper.getPreferences(context).getString(PREFERENCE_USER_NAME, null);
    }

    public static void saveUserName(Context context, String name)
    {
        SharedPreferences.Editor prefs = PreferencesHelper.getPreferences(context).edit();
        prefs.putString(PREFERENCE_USER_NAME, name);
        prefs.apply();
    }

    public static int getUserType(Context context)
    {
        return PreferencesHelper.getPreferences(context).getInt(PREFERENCE_USER_TYPE, 0);
    }

    public static void saveUserType(Context context, int userType)
    {
        SharedPreferences.Editor prefs = PreferencesHelper.getPreferences(context).edit();
        prefs.putInt(PREFERENCE_USER_TYPE, userType);
        prefs.apply();
    }

    public static String getUserImageUrl(Context context)
    {
        return PreferencesHelper.getPreferences(context).getString(PREFERENCE_USER_IMAGE_URL, null);
    }

    public static void saveUserImageUrl(Context context, String imageUrl)
    {
        SharedPreferences.Editor prefs = PreferencesHelper.getPreferences(context).edit();
        prefs.putString(PREFERENCE_USER_IMAGE_URL, imageUrl);
        prefs.apply();
    }

    public static String getUserPhone(Context context)
    {
        return PreferencesHelper.getPreferences(context).getString(PREFERENCE_USER_PHONE, null);
    }

    public static void saveUserPhone(Context context, String phone)
    {
        SharedPreferences.Editor prefs = PreferencesHelper.getPreferences(context).edit();
        prefs.putString(PREFERENCE_USER_PHONE, phone);
        prefs.apply();
    }

    public static String getUserLogoUrl(Context context)
    {
        return PreferencesHelper.getPreferences(context).getString(PREFERENCE_USER_LOGO_URL, null);
    }

    public static void saveUserLogoUrl(Context context, String logoUrl)
    {
        SharedPreferences.Editor prefs = PreferencesHelper.getPreferences(context).edit();
        prefs.putString(PREFERENCE_USER_LOGO_URL, logoUrl);
        prefs.apply();
    }

    public static long getLastPlanningRefresh(Context context)
    {
        return PreferencesHelper.getPreferences(context).getLong(PREFERENCE_LAST_PLANNING_REFRESH, 0);
    }

    public static void saveLastPlanningRefresh(Context context, long millis)
    {
        SharedPreferences.Editor prefs = PreferencesHelper.getPreferences(context).edit();
        prefs.putLong(PREFERENCE_LAST_PLANNING_REFRESH, millis);
        prefs.apply();
    }

    public static String getApplicationCookie(Context context)
    {
        return PreferencesHelper.getPreferences(context).getString(PREFERENCE_APPLICATION_COOKIE, null);
    }

    public static void saveApplicationCookie(Context context, String cookie)
    {
        SharedPreferences.Editor prefs = PreferencesHelper.getPreferences(context).edit();
        prefs.putString(PREFERENCE_APPLICATION_COOKIE, cookie);
        prefs.apply();
    }

    public static String getLocationServiceState(Context context)
    {
        return PreferencesHelper.getPreferences(context).getString(PREFERENCE_LOCATION_SERVICE_STATE, null);
    }

    public static void saveLocationServiceState(Context context, String state)
    {
        SharedPreferences.Editor prefs = PreferencesHelper.getPreferences(context).edit();
        prefs.putString(PREFERENCE_LOCATION_SERVICE_STATE, state);
        prefs.apply();
    }
}
