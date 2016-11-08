package be.appreciate.buttonsforcleaners.model;

import android.database.Cursor;

import java.util.ArrayList;
import java.util.List;

import be.appreciate.buttonsforcleaners.database.PlanningProductTable;
import be.appreciate.buttonsforcleaners.database.ProductTable;

/**
 * Created by Inneke De Clippel on 9/03/2016.
 */
public class Product
{
    private int id;
    private String name;
    private String imageUrl;
    private String unit;
    private String category;
    private int type;

    public static final int TYPE_GENERIC = 0;
    public static final int TYPE_PLANNING = 1;

    public static List<Product> constructListFromCursor(Cursor cursor)
    {
        List<Product> products = new ArrayList<>();

        if(cursor != null && cursor.moveToFirst())
        {
            do
            {
                products.add(Product.constructFromCursor(cursor));
            }
            while (cursor.moveToNext());
        }

        return products;
    }

    public static Product constructFromCursor(Cursor cursor)
    {
        int type = TYPE_PLANNING;

        int idIndex = cursor.getColumnIndex(PlanningProductTable.COLUMN_PRODUCT_ID_FULL);
        if(idIndex < 0)
        {
            idIndex = cursor.getColumnIndex(ProductTable.COLUMN_PRODUCT_ID_FULL);
            type = TYPE_GENERIC;
        }
        int nameIndex = cursor.getColumnIndex(PlanningProductTable.COLUMN_NAME_FULL);
        if(nameIndex < 0)
        {
            nameIndex = cursor.getColumnIndex(ProductTable.COLUMN_NAME_FULL);
        }
        int imageIndex = cursor.getColumnIndex(PlanningProductTable.COLUMN_IMAGE_URL_FULL);
        if(imageIndex < 0)
        {
            imageIndex = cursor.getColumnIndex(ProductTable.COLUMN_IMAGE_URL_FULL);
        }
        int unitIndex = cursor.getColumnIndex(PlanningProductTable.COLUMN_UNIT_FULL);
        if(unitIndex < 0)
        {
            unitIndex = cursor.getColumnIndex(ProductTable.COLUMN_UNIT_FULL);
        }
        int categoryIndex = cursor.getColumnIndex(PlanningProductTable.COLUMN_CATEGORY_FULL);
        if(categoryIndex < 0)
        {
            categoryIndex = cursor.getColumnIndex(ProductTable.COLUMN_CATEGORY_FULL);
        }

        Product product = new Product();
        product.id = cursor.getInt(idIndex);
        product.name = cursor.getString(nameIndex);
        product.imageUrl = cursor.getString(imageIndex);
        product.unit = cursor.getString(unitIndex);
        product.category = cursor.getString(categoryIndex);
        product.type = type;

        return product;
    }

    public int getId()
    {
        return id;
    }

    public String getName()
    {
        return name;
    }

    public String getImageUrl()
    {
        return imageUrl;
    }

    public String getUnit()
    {
        return unit;
    }

    public String getCategory()
    {
        return category;
    }

    public int getType()
    {
        return type;
    }
}
