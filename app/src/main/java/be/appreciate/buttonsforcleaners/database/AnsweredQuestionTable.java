package be.appreciate.buttonsforcleaners.database;

import android.database.sqlite.SQLiteDatabase;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Inneke De Clippel on 7/03/2016.
 */
public class AnsweredQuestionTable
{
    public static final String TABLE_NAME = "answered_questions";

    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_PLANNING_ID = "planning_id";
    public static final String COLUMN_QUESTION_ID = "question_id";
    public static final String COLUMN_ANSWERS = "answers";

    public static final String COLUMN_ID_FULL = TABLE_NAME + "_" + COLUMN_ID;
    public static final String COLUMN_PLANNING_ID_FULL = TABLE_NAME + "_" + COLUMN_PLANNING_ID;
    public static final String COLUMN_QUESTION_ID_FULL = TABLE_NAME + "_" + COLUMN_QUESTION_ID;
    public static final String COLUMN_ANSWERS_FULL = TABLE_NAME + "_" + COLUMN_ANSWERS;

    public static final Map<String, String> PROJECTION_MAP;

    static
    {
        PROJECTION_MAP = new HashMap<>();
        PROJECTION_MAP.put(TABLE_NAME + "." + COLUMN_ID, TABLE_NAME + "." + COLUMN_ID + " AS " + COLUMN_ID_FULL);
        PROJECTION_MAP.put(TABLE_NAME + "." + COLUMN_PLANNING_ID, TABLE_NAME + "." + COLUMN_PLANNING_ID + " AS " + COLUMN_PLANNING_ID_FULL);
        PROJECTION_MAP.put(TABLE_NAME + "." + COLUMN_QUESTION_ID, TABLE_NAME + "." + COLUMN_QUESTION_ID + " AS " + COLUMN_QUESTION_ID_FULL);
        PROJECTION_MAP.put(TABLE_NAME + "." + COLUMN_ANSWERS, TABLE_NAME + "." + COLUMN_ANSWERS + " AS " + COLUMN_ANSWERS_FULL);
    }

    private static final String CREATE_TABLE = "create table IF NOT EXISTS " + TABLE_NAME + "("
            + COLUMN_ID + " integer primary key autoincrement, "
            + COLUMN_PLANNING_ID + " integer, "
            + COLUMN_QUESTION_ID + " integer, "
            + COLUMN_ANSWERS + " text"
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
