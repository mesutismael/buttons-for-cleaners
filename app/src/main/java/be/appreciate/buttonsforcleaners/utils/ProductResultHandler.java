package be.appreciate.buttonsforcleaners.utils;

/**
 * Created by Inneke De Clippel on 23/03/2016.
 */
public interface ProductResultHandler
{
    String RESULT_PRODUCT_ID = "product_id";
    String RESULT_PRODUCT_TYPE = "product_type";
    String RESULT_AMOUNT = "amount";

    void finishWithSuccess(int productId, int productType, int amount);
}
