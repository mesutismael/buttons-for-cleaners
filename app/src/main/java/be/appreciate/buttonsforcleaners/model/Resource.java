package be.appreciate.buttonsforcleaners.model;

import android.database.Cursor;

import java.util.ArrayList;
import java.util.List;

import be.appreciate.buttonsforcleaners.database.PlanningResourceTable;

/**
 * Created by Inneke De Clippel on 9/03/2016.
 */
public class Resource
{
    private String name;
    private String imageUrl;
    private ResourceType type;

    public static List<Resource> constructListFromCursor(Cursor cursor)
    {
        List<Resource> resources = new ArrayList<>();

        if(cursor != null && cursor.moveToFirst())
        {
            do
            {
                resources.add(Resource.constructFromCursor(cursor));
            }
            while (cursor.moveToNext());
        }

        return resources;
    }

    public static Resource constructFromCursor(Cursor cursor)
    {
        Resource resource = new Resource();

        resource.name = cursor.getString(cursor.getColumnIndex(PlanningResourceTable.COLUMN_NAME_FULL));
        resource.imageUrl = cursor.getString(cursor.getColumnIndex(PlanningResourceTable.COLUMN_IMAGE_URL_FULL));
        resource.type = ResourceType.getResourceType(cursor.getInt(cursor.getColumnIndex(PlanningResourceTable.COLUMN_TYPE_FULL)));

        return resource;
    }

    public String getName()
    {
        return name;
    }

    public String getImageUrl()
    {
        return imageUrl;
    }

    public ResourceType getType()
    {
        return type;
    }
}
