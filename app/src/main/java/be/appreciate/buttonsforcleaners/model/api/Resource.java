package be.appreciate.buttonsforcleaners.model.api;

import android.content.ContentValues;

import com.google.gson.annotations.SerializedName;

import be.appreciate.buttonsforcleaners.database.PlanningResourceTable;

/**
 * Created by Inneke De Clippel on 1/03/2016.
 */
public class Resource
{
    @SerializedName("Name")
    private String name;
    @SerializedName("ImageUrl")
    private String imageUrl;
    @SerializedName("Type")
    private String type;

    public String getImageUrl()
    {
        return imageUrl;
    }

    public ContentValues getContentValues(int planningId)
    {
        ContentValues cv = new ContentValues();

        cv.put(PlanningResourceTable.COLUMN_PLANNING_ID, planningId);
        cv.put(PlanningResourceTable.COLUMN_NAME, this.name);
        cv.put(PlanningResourceTable.COLUMN_IMAGE_URL, this.imageUrl);
        cv.put(PlanningResourceTable.COLUMN_TYPE, this.type);

        return cv;
    }
}
