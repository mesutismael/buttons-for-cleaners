package be.appreciate.buttonsforcleaners.model.api;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Inneke De Clippel on 1/03/2016.
 */
public class LoginRequest
{
    @SerializedName("Username")
    private String username;
    @SerializedName("Password")
    private String password;

    public LoginRequest(String username, String password)
    {
        this.username = username;
        this.password = password;
    }
}
