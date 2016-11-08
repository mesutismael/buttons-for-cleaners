package be.appreciate.buttonsforcleaners.model.api;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Inneke De Clippel on 1/03/2016.
 */
public class LoginResponse
{
    @SerializedName("ErrorMessage")
    private String errorMessage;
    @SerializedName("Success")
    private boolean success;
    @SerializedName("ResourceId")
    private int id;
    @SerializedName("ResourceName")
    private String name;
    @SerializedName("ResourceType")
    private int type;
    @SerializedName("ResourceImageUrl")
    private String imageUrl;
    @SerializedName("PhoneNumber")
    private String phoneNumber;
    @SerializedName("LogoImageUrl")
    private String logoUrl;

    public String getErrorMessage()
    {
        return errorMessage;
    }

    public boolean isSuccess()
    {
        return success;
    }

    public int getId()
    {
        return id;
    }

    public String getName()
    {
        return name;
    }

    public int getType()
    {
        return type;
    }

    public String getImageUrl()
    {
        return imageUrl;
    }

    public String getPhoneNumber()
    {
        return phoneNumber;
    }

    public String getLogoUrl()
    {
        return logoUrl;
    }
}
