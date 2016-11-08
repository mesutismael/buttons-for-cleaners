package be.appreciate.buttonsforcleaners.model.api;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Inneke De Clippel on 1/03/2016.
 */
public class FeedbackResponse
{
    @SerializedName("ErrorCode")
    private int errorCode;
    @SerializedName("ErrorMessage")
    private String errorMessage;
    @SerializedName("Success")
    private boolean success;

    private static final int ERROR_CODE_ALREADY_EXECUTED = 7;

    public String getErrorMessage()
    {
        return errorMessage;
    }

    public boolean isSuccess()
    {
        return success;
    }

    public boolean isAlreadyExecuted()
    {
        return this.errorCode == ERROR_CODE_ALREADY_EXECUTED;
    }
}
