package be.appreciate.buttonsforcleaners.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by Inneke De Clippel on 4/03/2016.
 */
public class DateUtils
{
    private static final SimpleDateFormat SDF_API_DATE = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
    private static final SimpleDateFormat SDF_DETAIL = new SimpleDateFormat("EEEE d MMMM", Locale.getDefault());
    private static final String API_MILLIS_PREFIX = "/Date(";
    private static final String API_MILLIS_SUFFIX = ")/";

    public static synchronized String formatApiDate(long millis)
    {
        return SDF_API_DATE.format(new Date(millis));
    }

    public static synchronized String formatDetailDate(long millis)
    {
        return SDF_DETAIL.format(new Date(millis));
    }

    public static long getStartOfToday()
    {
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("Europe/Brussels"));
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        return calendar.getTimeInMillis();
    }

    public static long getEndOfToday()
    {
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("Europe/Brussels"));
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);

        return calendar.getTimeInMillis();
    }

    public static long extractMillis(String millisAsString)
    {
        if(millisAsString != null && millisAsString.startsWith(API_MILLIS_PREFIX) && millisAsString.endsWith(API_MILLIS_SUFFIX))
        {
            String extract = millisAsString.substring(API_MILLIS_PREFIX.length(), millisAsString.length() - API_MILLIS_SUFFIX.length());

            try
            {
                return Long.parseLong(extract);
            }
            catch (NumberFormatException e)
            {
                return System.currentTimeMillis();
            }
        }

        return System.currentTimeMillis();
    }
}
