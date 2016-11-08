package be.appreciate.buttonsforcleaners.model.api;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Inneke De Clippel on 1/03/2016.
 */
public class NotifyCustomerRequest
{
    @SerializedName("PlanningId")
    private int planningId;
    @SerializedName("AccountancyName")
    private String accountancyName;
    @SerializedName("LocationAddress")
    private String address;
    @SerializedName("PhoneNumber")
    private String phoneNumber;

    public NotifyCustomerRequest(int planningId, String accountancyName, String address, String phoneNumber)
    {
        this.planningId = planningId;
        this.accountancyName = accountancyName;
        this.address = address;
        this.phoneNumber = phoneNumber;
    }
}
