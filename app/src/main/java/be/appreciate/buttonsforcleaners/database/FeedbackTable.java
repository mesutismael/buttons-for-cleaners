package be.appreciate.buttonsforcleaners.database;

import android.database.sqlite.SQLiteDatabase;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Inneke De Clippel on 7/03/2016.
 */
public class FeedbackTable
{
    public static final String TABLE_NAME = "feedback";

    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_PLANNING_ID = "planning_id";
    public static final String COLUMN_START_TIME = "start_time";
    public static final String COLUMN_END_TIME = "end_time";
    public static final String COLUMN_START_COMPLETED = "start_completed";
    public static final String COLUMN_END_COMPLETED = "end_completed";
    public static final String COLUMN_SENT_TO_API = "sent_to_api";
    public static final String COLUMN_DISTANCE_TRAVELED = "distance_traveled";

    public static final String COLUMN_ID_FULL = TABLE_NAME + "_" + COLUMN_ID;
    public static final String COLUMN_PLANNING_ID_FULL = TABLE_NAME + "_" + COLUMN_PLANNING_ID;
    public static final String COLUMN_START_TIME_FULL = TABLE_NAME + "_" + COLUMN_START_TIME;
    public static final String COLUMN_END_TIME_FULL = TABLE_NAME + "_" + COLUMN_END_TIME;
    public static final String COLUMN_START_COMPLETED_FULL = TABLE_NAME + "_" + COLUMN_START_COMPLETED;
    public static final String COLUMN_END_COMPLETED_FULL = TABLE_NAME + "_" + COLUMN_END_COMPLETED;
    public static final String COLUMN_SENT_TO_API_FULL = TABLE_NAME + "_" + COLUMN_SENT_TO_API;
    public static final String COLUMN_DISTANCE_TRAVELED_FULL = TABLE_NAME + "_" + COLUMN_DISTANCE_TRAVELED;

    public static final Map<String, String> PROJECTION_MAP;

    static
    {
        PROJECTION_MAP = new HashMap<>();
        PROJECTION_MAP.put(TABLE_NAME + "." + COLUMN_ID, TABLE_NAME + "." + COLUMN_ID + " AS " + COLUMN_ID_FULL);
        PROJECTION_MAP.put(TABLE_NAME + "." + COLUMN_PLANNING_ID, TABLE_NAME + "." + COLUMN_PLANNING_ID + " AS " + COLUMN_PLANNING_ID_FULL);
        PROJECTION_MAP.put(TABLE_NAME + "." + COLUMN_START_TIME, TABLE_NAME + "." + COLUMN_START_TIME + " AS " + COLUMN_START_TIME_FULL);
        PROJECTION_MAP.put(TABLE_NAME + "." + COLUMN_END_TIME, TABLE_NAME + "." + COLUMN_END_TIME + " AS " + COLUMN_END_TIME_FULL);
        PROJECTION_MAP.put(TABLE_NAME + "." + COLUMN_START_COMPLETED, TABLE_NAME + "." + COLUMN_START_COMPLETED + " AS " + COLUMN_START_COMPLETED_FULL);
        PROJECTION_MAP.put(TABLE_NAME + "." + COLUMN_END_COMPLETED, TABLE_NAME + "." + COLUMN_END_COMPLETED + " AS " + COLUMN_END_COMPLETED_FULL);
        PROJECTION_MAP.put(TABLE_NAME + "." + COLUMN_SENT_TO_API, TABLE_NAME + "." + COLUMN_SENT_TO_API + " AS " + COLUMN_SENT_TO_API_FULL);
        PROJECTION_MAP.put(TABLE_NAME + "." + COLUMN_DISTANCE_TRAVELED, TABLE_NAME + "." + COLUMN_DISTANCE_TRAVELED + " AS " + COLUMN_DISTANCE_TRAVELED_FULL);
    }

    private static final String CREATE_TABLE = "create table IF NOT EXISTS " + TABLE_NAME + "("
            + COLUMN_ID + " integer primary key autoincrement, "
            + COLUMN_PLANNING_ID + " integer, "
            + COLUMN_START_TIME + " integer, "
            + COLUMN_END_TIME + " integer, "
            + COLUMN_START_COMPLETED + " integer default 0, "
            + COLUMN_END_COMPLETED + " integer default 0, "
            + COLUMN_SENT_TO_API + " integer default 0, "
            + COLUMN_DISTANCE_TRAVELED + " integer default 0"
            + ");";

    public static void onCreate(SQLiteDatabase database)
    {
        database.execSQL(CREATE_TABLE);
    }

    public static void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion)
    {
        database.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(database);
    }
}
