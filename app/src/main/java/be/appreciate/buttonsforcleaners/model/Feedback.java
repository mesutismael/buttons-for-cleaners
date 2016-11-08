package be.appreciate.buttonsforcleaners.model;

import android.database.Cursor;

import java.util.ArrayList;
import java.util.List;

import be.appreciate.buttonsforcleaners.database.FeedbackTable;

/**
 * Created by Inneke De Clippel on 17/03/2016.
 */
public class Feedback
{
    private int planningId;
    private long startTime;
    private long endTime;
    private int distanceTraveled;

    public static List<Feedback> constructListFromCursor(Cursor cursor)
    {
        List<Feedback> feedback = new ArrayList<>();

        if(cursor != null && cursor.moveToFirst())
        {
            do
            {
                feedback.add(Feedback.constructFromCursor(cursor));
            }
            while (cursor.moveToNext());
        }

        return feedback;
    }

    public static Feedback constructFromCursor(Cursor cursor)
    {
        Feedback feedback = new Feedback();

        feedback.planningId = cursor.getInt(cursor.getColumnIndex(FeedbackTable.COLUMN_PLANNING_ID_FULL));
        feedback.startTime = cursor.getLong(cursor.getColumnIndex(FeedbackTable.COLUMN_START_TIME_FULL));
        feedback.endTime = cursor.getLong(cursor.getColumnIndex(FeedbackTable.COLUMN_END_TIME_FULL));
        feedback.distanceTraveled = cursor.getInt(cursor.getColumnIndex(FeedbackTable.COLUMN_DISTANCE_TRAVELED_FULL));

        return feedback;
    }

    public int getPlanningId()
    {
        return planningId;
    }

    public long getStartTime()
    {
        return startTime;
    }

    public long getEndTime()
    {
        return endTime;
    }

    public int getDistanceTraveled()
    {
        return distanceTraveled;
    }
}
