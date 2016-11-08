package be.appreciate.buttonsforcleaners.model.api;

import android.content.ContentValues;

import com.google.gson.annotations.SerializedName;

import be.appreciate.buttonsforcleaners.database.PlanningProductTable;
import be.appreciate.buttonsforcleaners.database.ProductTable;

/**
 * Created by Inneke De Clippel on 1/03/2016.
 */
public class Product
{
    @SerializedName("Id")
    private int id;
    @SerializedName("Name")
    private String name;
    @SerializedName("ImageUrl")
    private String imageUrl;
    @SerializedName("Unit")
    private String unit;
    @SerializedName("Category")
    private String category;

    public String getImageUrl()
    {
        return imageUrl;
    }

    public ContentValues getContentValues()
    {
        ContentValues cv = new ContentValues();

        cv.put(ProductTable.COLUMN_PRODUCT_ID, this.id);
        cv.put(ProductTable.COLUMN_NAME, this.name);
        cv.put(ProductTable.COLUMN_IMAGE_URL, this.imageUrl);
        cv.put(ProductTable.COLUMN_UNIT, this.unit);
        cv.put(ProductTable.COLUMN_CATEGORY, this.category);

        return cv;
    }

    public ContentValues getPlanningContentValues(int planningId)
    {
        ContentValues cv = new ContentValues();

        cv.put(PlanningProductTable.COLUMN_PRODUCT_ID, this.id);
        cv.put(PlanningProductTable.COLUMN_PLANNING_ID, planningId);
        cv.put(PlanningProductTable.COLUMN_NAME, this.name);
        cv.put(PlanningProductTable.COLUMN_IMAGE_URL, this.imageUrl);
        cv.put(PlanningProductTable.COLUMN_UNIT, this.unit);
        cv.put(PlanningProductTable.COLUMN_CATEGORY, this.category);

        return cv;
    }
}
