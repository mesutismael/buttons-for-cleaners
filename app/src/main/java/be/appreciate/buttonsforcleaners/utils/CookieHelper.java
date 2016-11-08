package be.appreciate.buttonsforcleaners.utils;

import java.util.List;

import retrofit.client.Header;

/**
 * Created by Inneke De Clippel on 17/03/2016.
 */
public class CookieHelper
{
    private static final String COOKIE_HEADER_KEY = "Set-Cookie";
    private static final String COOKIE_DELIMITER = ";";
    private static final String COOKIE_APPLICATION_KEY = ".AspNet.ApplicationCookie";

    public static String getApplicationCookie(List<Header> headers)
    {
        if(headers != null)
        {
            for(Header header : headers)
            {
                if(header != null && COOKIE_HEADER_KEY.equals(header.getName()))
                {
                    String cookie = header.getValue();

                    if(cookie != null && cookie.contains(COOKIE_APPLICATION_KEY))
                    {
                        int startIndex = cookie.indexOf(COOKIE_APPLICATION_KEY);
                        int endIndex = cookie.indexOf(COOKIE_DELIMITER, startIndex);

                        if(startIndex >= 0 && startIndex < cookie.length() && endIndex >= startIndex && endIndex < cookie.length())
                        {
                            return cookie.substring(startIndex, endIndex);
                        }
                    }
                }
            }
        }

        return null;
    }
}
