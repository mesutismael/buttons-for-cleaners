package be.appreciate.buttonsforcleaners.utils;

import java.util.ArrayList;
import java.util.List;

import be.appreciate.buttonsforcleaners.database.PlanningProductTable;
import be.appreciate.buttonsforcleaners.database.ProductTable;
import be.appreciate.buttonsforcleaners.model.Product;
import be.appreciate.buttonsforcleaners.model.QuestionProduct;

/**
 * Created by Inneke De Clippel on 23/03/2016.
 */
public class ProductHelper
{
    private static final String DELIMITER_PRODUCT_VALUES = "/";

    public static List<QuestionProduct> convertToProducts(List<String> answers)
    {
        if(answers == null)
        {
            return null;
        }

        List<QuestionProduct> products = new ArrayList<>();

        for(String answer : answers)
        {
            if(answer != null)
            {
                String[] values = answer.split(DELIMITER_PRODUCT_VALUES);

                if(values.length == 3)
                {
                    try
                    {
                        int id = Integer.parseInt(values[0]);
                        int type = Integer.parseInt(values[1]);
                        int amount = Integer.parseInt(values[2]);

                        products.add(new QuestionProduct(id, type, amount));
                    }
                    catch (NumberFormatException e)
                    {
                    }
                }
            }
        }

        return products;
    }

    public static List<String> convertToAnswers(List<QuestionProduct> products)
    {
        if(products == null)
        {
            return null;
        }

        List<String> answers = new ArrayList<>();

        for(QuestionProduct product : products)
        {
            if(product != null)
            {
                int id = product.getProductId();
                int type = product.getProductType();
                int amount = product.getAmount();

                String values = String.valueOf(id) + DELIMITER_PRODUCT_VALUES + String.valueOf(type) + DELIMITER_PRODUCT_VALUES + String.valueOf(amount);
                answers.add(values);
            }
        }

        return answers;
    }

    public static String createProductSelection(List<QuestionProduct> products, int productType)
    {
        StringBuilder ids = new StringBuilder();
        boolean empty = true;

        if(products != null)
        {
            for(QuestionProduct product : products)
            {
                if(product != null && product.getProductType() == productType)
                {
                    if (!empty)
                    {
                        ids.append(",");
                    }
                    ids.append(product.getProductId());
                    empty = false;
                }
            }
        }

        if(empty)
        {
            return null;
        }

        String column = productType == Product.TYPE_PLANNING ? PlanningProductTable.COLUMN_PRODUCT_ID : ProductTable.COLUMN_PRODUCT_ID;
        return column + " IN (" + ids.toString() + ")";
    }

    public static boolean containsProducts(List<QuestionProduct> products, int productType)
    {
        if(products != null)
        {
            for(QuestionProduct product : products)
            {
                if(product != null && product.getProductType() == productType)
                {
                    return true;
                }
            }
        }

        return false;
    }

    public static void addProduct(List<QuestionProduct> products, int productId, int productType, int amount)
    {
        if(products == null)
        {
            products = new ArrayList<>();
        }

        for(QuestionProduct product : products)
        {
            if(product != null && product.getProductId() == productId && product.getProductType() == productType)
            {
                product.increaseAmount(amount);
                return;
            }
        }

        products.add(new QuestionProduct(productId, productType, amount));
    }

    public static void updateProducts(List<QuestionProduct> questionProducts, List<Product> products)
    {
        if(questionProducts != null && products != null)
        {
            for(QuestionProduct questionProduct : questionProducts)
            {
                int id = questionProduct.getProductId();
                int type = questionProduct.getProductType();

                for(Product product : products)
                {
                    if(product != null && product.getId() == id && product.getType() == type)
                    {
                        questionProduct.setProduct(product);
                        break;
                    }
                }
            }
        }
    }
}
