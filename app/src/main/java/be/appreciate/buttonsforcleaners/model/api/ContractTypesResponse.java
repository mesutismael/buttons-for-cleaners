package be.appreciate.buttonsforcleaners.model.api;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Inneke De Clippel on 1/03/2016.
 */
public class ContractTypesResponse
{
    @SerializedName("ErrorMessage")
    private String errorMessage;
    @SerializedName("Success")
    private boolean success;
    @SerializedName("ContractTypes")
    private List<ContractType> contractTypes;

    public String getErrorMessage()
    {
        return errorMessage;
    }

    public boolean isSuccess()
    {
        return success;
    }

    public List<ContractType> getContractTypes()
    {
        return contractTypes;
    }
}
