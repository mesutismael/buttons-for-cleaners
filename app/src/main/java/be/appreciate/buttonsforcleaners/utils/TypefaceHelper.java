package be.appreciate.buttonsforcleaners.utils;

import android.content.Context;
import android.graphics.Typeface;

/**
 * Created by Inneke De Clippel on 2/03/2016.
 */
public class TypefaceHelper
{
    private static Typeface typefaceRegular;
    private static Typeface typefaceMedium;

    public static Typeface regular(Context context)
    {
        if(typefaceRegular == null)
        {
            typefaceRegular = Typeface.createFromAsset(context.getAssets(), "Roboto-Regular.ttf");
        }

        return typefaceRegular;
    }

    public static Typeface medium(Context context)
    {
        if(typefaceMedium == null)
        {
            typefaceMedium = Typeface.createFromAsset(context.getAssets(), "Roboto-Medium.ttf");
        }

        return typefaceMedium;
    }
}
