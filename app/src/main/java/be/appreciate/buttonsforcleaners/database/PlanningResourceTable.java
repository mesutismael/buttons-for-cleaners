package be.appreciate.buttonsforcleaners.database;

import android.database.sqlite.SQLiteDatabase;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Inneke De Clippel on 4/03/2016.
 */
public class PlanningResourceTable
{
    public static final String TABLE_NAME = "planning_resources";

    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_PLANNING_ID = "planning_id";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_IMAGE_URL = "image_url";
    public static final String COLUMN_TYPE = "type";

    public static final String COLUMN_ID_FULL = TABLE_NAME + "_" + COLUMN_ID;
    public static final String COLUMN_PLANNING_ID_FULL = TABLE_NAME + "_" + COLUMN_PLANNING_ID;
    public static final String COLUMN_NAME_FULL = TABLE_NAME + "_" + COLUMN_NAME;
    public static final String COLUMN_IMAGE_URL_FULL = TABLE_NAME + "_" + COLUMN_IMAGE_URL;
    public static final String COLUMN_TYPE_FULL = TABLE_NAME + "_" + COLUMN_TYPE;

    public static final Map<String, String> PROJECTION_MAP;

    static
    {
        PROJECTION_MAP = new HashMap<>();
        PROJECTION_MAP.put(TABLE_NAME + "." + COLUMN_ID, TABLE_NAME + "." + COLUMN_ID + " AS " + COLUMN_ID_FULL);
        PROJECTION_MAP.put(TABLE_NAME + "." + COLUMN_PLANNING_ID, TABLE_NAME + "." + COLUMN_PLANNING_ID + " AS " + COLUMN_PLANNING_ID_FULL);
        PROJECTION_MAP.put(TABLE_NAME + "." + COLUMN_NAME, TABLE_NAME + "." + COLUMN_NAME + " AS " + COLUMN_NAME_FULL);
        PROJECTION_MAP.put(TABLE_NAME + "." + COLUMN_IMAGE_URL, TABLE_NAME + "." + COLUMN_IMAGE_URL + " AS " + COLUMN_IMAGE_URL_FULL);
        PROJECTION_MAP.put(TABLE_NAME + "." + COLUMN_TYPE, TABLE_NAME + "." + COLUMN_TYPE + " AS " + COLUMN_TYPE_FULL);
    }

    private static final String CREATE_TABLE = "create table IF NOT EXISTS " + TABLE_NAME + "("
            + COLUMN_ID + " integer primary key autoincrement, "
            + COLUMN_PLANNING_ID + " integer, "
            + COLUMN_NAME + " text, "
            + COLUMN_IMAGE_URL + " text, "
            + COLUMN_TYPE + " integer"
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
