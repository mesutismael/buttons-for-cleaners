package be.appreciate.buttonsforcleaners.database;

import android.database.sqlite.SQLiteDatabase;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Inneke De Clippel on 3/03/2016.
 */
public class QuestionTable
{
    public static final String TABLE_NAME = "questions";

    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_QUESTION_ID = "question_id";
    public static final String COLUMN_LABEL = "label";
    public static final String COLUMN_TYPE = "type";
    public static final String COLUMN_ANSWERS = "answers";
    public static final String COLUMN_CONTRACT_TYPE_ID = "contract_type_id";
    public static final String COLUMN_POINT_OF_TIME = "point_of_time";

    public static final String COLUMN_ID_FULL = TABLE_NAME + "_" + COLUMN_ID;
    public static final String COLUMN_QUESTION_ID_FULL = TABLE_NAME + "_" + COLUMN_QUESTION_ID;
    public static final String COLUMN_LABEL_FULL = TABLE_NAME + "_" + COLUMN_LABEL;
    public static final String COLUMN_TYPE_FULL = TABLE_NAME + "_" + COLUMN_TYPE;
    public static final String COLUMN_ANSWERS_FULL = TABLE_NAME + "_" + COLUMN_ANSWERS;
    public static final String COLUMN_CONTRACT_TYPE_ID_FULL = TABLE_NAME + "_" + COLUMN_CONTRACT_TYPE_ID;
    public static final String COLUMN_POINT_OF_TIME_FULL = TABLE_NAME + "_" + COLUMN_POINT_OF_TIME;

    public static final Map<String, String> PROJECTION_MAP;

    static
    {
        PROJECTION_MAP = new HashMap<>();
        PROJECTION_MAP.put(TABLE_NAME + "." + COLUMN_ID, TABLE_NAME + "." + COLUMN_ID + " AS " + COLUMN_ID_FULL);
        PROJECTION_MAP.put(TABLE_NAME + "." + COLUMN_QUESTION_ID, TABLE_NAME + "." + COLUMN_QUESTION_ID + " AS " + COLUMN_QUESTION_ID_FULL);
        PROJECTION_MAP.put(TABLE_NAME + "." + COLUMN_LABEL, TABLE_NAME + "." + COLUMN_LABEL + " AS " + COLUMN_LABEL_FULL);
        PROJECTION_MAP.put(TABLE_NAME + "." + COLUMN_TYPE, TABLE_NAME + "." + COLUMN_TYPE + " AS " + COLUMN_TYPE_FULL);
        PROJECTION_MAP.put(TABLE_NAME + "." + COLUMN_ANSWERS, TABLE_NAME + "." + COLUMN_ANSWERS + " AS " + COLUMN_ANSWERS_FULL);
        PROJECTION_MAP.put(TABLE_NAME + "." + COLUMN_CONTRACT_TYPE_ID, TABLE_NAME + "." + COLUMN_CONTRACT_TYPE_ID + " AS " + COLUMN_CONTRACT_TYPE_ID_FULL);
        PROJECTION_MAP.put(TABLE_NAME + "." + COLUMN_POINT_OF_TIME, TABLE_NAME + "." + COLUMN_POINT_OF_TIME + " AS " + COLUMN_POINT_OF_TIME_FULL);
    }

    private static final String CREATE_TABLE = "create table IF NOT EXISTS " + TABLE_NAME + "("
            + COLUMN_ID + " integer primary key autoincrement, "
            + COLUMN_QUESTION_ID + " integer, "
            + COLUMN_LABEL + " text, "
            + COLUMN_TYPE + " integer, "
            + COLUMN_ANSWERS + " text, "
            + COLUMN_CONTRACT_TYPE_ID + " integer, "
            + COLUMN_POINT_OF_TIME + " integer"
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
