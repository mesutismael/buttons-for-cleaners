package be.appreciate.buttonsforcleaners.model;

import java.util.EnumSet;

/**
 * Created by Inneke De Clippel on 10/03/2016.
 */
public enum PointOfTime
{
    START(0),
    STOP(1);

    private int pointOfTimeId;

    PointOfTime(int pointOfTimeId)
    {
        this.pointOfTimeId = pointOfTimeId;
    }

    public int getPointOfTimeId()
    {
        return pointOfTimeId;
    }

    public static PointOfTime getPointOfTime(int pointOfTimeId)
    {
        for(PointOfTime pointOfTime : EnumSet.allOf(PointOfTime.class))
        {
            if(pointOfTime.pointOfTimeId == pointOfTimeId)
            {
                return pointOfTime;
            }
        }

        return START;
    }
}
