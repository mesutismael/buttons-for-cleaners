package be.appreciate.buttonsforcleaners.model.api;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Inneke De Clippel on 1/03/2016.
 */
public class Address
{
    @SerializedName("Street")
    private String street;
    @SerializedName("PostalCode")
    private String postalCode;
    @SerializedName("City")
    private String city;
    @SerializedName("Latitude")
    private double latitude;
    @SerializedName("Longitude")
    private double longitude;

    public String getStreet()
    {
        return street;
    }

    public String getPostalCode()
    {
        return postalCode;
    }

    public String getCity()
    {
        return city;
    }

    public double getLatitude()
    {
        return latitude;
    }

    public double getLongitude()
    {
        return longitude;
    }
}
