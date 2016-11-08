package be.appreciate.buttonsforcleaners.model.api;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Inneke De Clippel on 1/03/2016.
 */
public class ProductsResponse
{
    @SerializedName("ErrorMessage")
    private String errorMessage;
    @SerializedName("Success")
    private boolean success;
    @SerializedName("Products")
    private List<Product> products;

    public String getErrorMessage()
    {
        return errorMessage;
    }

    public boolean isSuccess()
    {
        return success;
    }

    public List<Product> getProducts()
    {
        return products;
    }
}
