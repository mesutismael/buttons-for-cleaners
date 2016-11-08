package be.appreciate.buttonsforcleaners.model.api;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Inneke De Clippel on 1/03/2016.
 */
public class PlanningResponse
{
    @SerializedName("ErrorMessage")
    private String errorMessage;
    @SerializedName("Success")
    private boolean success;
    @SerializedName("PlanningEntries")
    private List<PlanningItem> planningItems;

    public String getErrorMessage()
    {
        return errorMessage;
    }

    public boolean isSuccess()
    {
        return success;
    }

    public List<PlanningItem> getPlanningItems()
    {
        return planningItems;
    }
}
