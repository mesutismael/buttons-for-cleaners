package be.appreciate.buttonsforcleaners.model;

/**
 * Created by Inneke De Clippel on 9/03/2016.
 */
public enum ResourceType
{
    PERSON,
    VEHICLE,
    MATERIAL,
    EXTRA;

    public static ResourceType getResourceType(int resourceTypeId)
    {
        switch (resourceTypeId)
        {
            case 0:
                return EXTRA;

            case 1:
                return VEHICLE;

            case 2:
                return MATERIAL;

            case 3:
                return PERSON;

            default:
                return EXTRA;
        }
    }
}
