package be.appreciate.buttonsforcleaners.model;

/**
 * Created by Inneke De Clippel on 23/03/2016.
 */
public class QuestionProduct
{
    private int productId;
    private int productType;
    private int amount;
    private Product product;

    public QuestionProduct(int productId, int productType, int amount)
    {
        this.productId = productId;
        this.productType = productType;
        this.amount = amount;
    }

    public void setAmount(int amount)
    {
        this.amount = amount;
    }

    public void setProduct(Product product)
    {
        this.product = product;
    }

    public void increaseAmount(int amount)
    {
        this.amount += amount;
    }

    public int getProductId()
    {
        return productId;
    }

    public int getProductType()
    {
        return productType;
    }

    public int getAmount()
    {
        return amount;
    }

    public Product getProduct()
    {
        return product;
    }
}
