package be.appreciate.buttonsforcleaners.database;

import android.database.sqlite.SQLiteDatabase;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Inneke De Clippel on 4/03/2016.
 */
public class PlanningTable
{
    public static final String TABLE_NAME = "planning_items";

    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_PLANNING_ID = "planning_id";
    public static final String COLUMN_CONTRACT_TYPE_ID = "contract_type_id";
    public static final String COLUMN_ACCOUNTANCY_NAME = "accountancy_name";
    public static final String COLUMN_CUSTOMER_NAME = "customer_name";
    public static final String COLUMN_DATE = "date";
    public static final String COLUMN_START_TIME = "start_time";
    public static final String COLUMN_END_TIME = "end_time";
    public static final String COLUMN_LOCATION_NAME = "location_name";
    public static final String COLUMN_PROXIMITY_REQUIRED = "proximity_required";
    public static final String COLUMN_DESCRIPTION = "description";
    public static final String COLUMN_INSTRUCTIONS = "instructions";
    public static final String COLUMN_PHONE = "phone";
    public static final String COLUMN_STREET = "street";
    public static final String COLUMN_POSTAL_CODE = "postal_code";
    public static final String COLUMN_CITY = "city";
    public static final String COLUMN_LATITUDE = "latitude";
    public static final String COLUMN_LONGITUDE = "longitude";

    public static final String COLUMN_ID_FULL = TABLE_NAME + "_" + COLUMN_ID;
    public static final String COLUMN_PLANNING_ID_FULL = TABLE_NAME + "_" + COLUMN_PLANNING_ID;
    public static final String COLUMN_CONTRACT_TYPE_ID_FULL = TABLE_NAME + "_" + COLUMN_CONTRACT_TYPE_ID;
    public static final String COLUMN_ACCOUNTANCY_NAME_FULL = TABLE_NAME + "_" + COLUMN_ACCOUNTANCY_NAME;
    public static final String COLUMN_CUSTOMER_NAME_FULL = TABLE_NAME + "_" + COLUMN_CUSTOMER_NAME;
    public static final String COLUMN_DATE_FULL = TABLE_NAME + "_" + COLUMN_DATE;
    public static final String COLUMN_START_TIME_FULL = TABLE_NAME + "_" + COLUMN_START_TIME;
    public static final String COLUMN_END_TIME_FULL = TABLE_NAME + "_" + COLUMN_END_TIME;
    public static final String COLUMN_LOCATION_NAME_FULL = TABLE_NAME + "_" + COLUMN_LOCATION_NAME;
    public static final String COLUMN_PROXIMITY_REQUIRED_FULL = TABLE_NAME + "_" + COLUMN_PROXIMITY_REQUIRED;
    public static final String COLUMN_DESCRIPTION_FULL = TABLE_NAME + "_" + COLUMN_DESCRIPTION;
    public static final String COLUMN_INSTRUCTIONS_FULL = TABLE_NAME + "_" + COLUMN_INSTRUCTIONS;
    public static final String COLUMN_PHONE_FULL = TABLE_NAME + "_" + COLUMN_PHONE;
    public static final String COLUMN_STREET_FULL = TABLE_NAME + "_" + COLUMN_STREET;
    public static final String COLUMN_POSTAL_CODE_FULL = TABLE_NAME + "_" + COLUMN_POSTAL_CODE;
    public static final String COLUMN_CITY_FULL = TABLE_NAME + "_" + COLUMN_CITY;
    public static final String COLUMN_LATITUDE_FULL = TABLE_NAME + "_" + COLUMN_LATITUDE;
    public static final String COLUMN_LONGITUDE_FULL = TABLE_NAME + "_" + COLUMN_LONGITUDE;
    public static final String COLUMN_QUESTIONS_AT_START_FULL = TABLE_NAME + "_" + "questions_at_start";
    public static final String COLUMN_QUESTIONS_AT_STOP_FULL = TABLE_NAME + "_" + "questions_at_stop";

    public static final Map<String, String> PROJECTION_MAP;

    static
    {
        PROJECTION_MAP = new HashMap<>();
        PROJECTION_MAP.put(TABLE_NAME + "." + COLUMN_ID, TABLE_NAME + "." + COLUMN_ID + " AS " + COLUMN_ID_FULL);
        PROJECTION_MAP.put(TABLE_NAME + "." + COLUMN_PLANNING_ID, TABLE_NAME + "." + COLUMN_PLANNING_ID + " AS " + COLUMN_PLANNING_ID_FULL);
        PROJECTION_MAP.put(TABLE_NAME + "." + COLUMN_CONTRACT_TYPE_ID, TABLE_NAME + "." + COLUMN_CONTRACT_TYPE_ID + " AS " + COLUMN_CONTRACT_TYPE_ID_FULL);
        PROJECTION_MAP.put(TABLE_NAME + "." + COLUMN_ACCOUNTANCY_NAME, TABLE_NAME + "." + COLUMN_ACCOUNTANCY_NAME + " AS " + COLUMN_ACCOUNTANCY_NAME_FULL);
        PROJECTION_MAP.put(TABLE_NAME + "." + COLUMN_CUSTOMER_NAME, TABLE_NAME + "." + COLUMN_CUSTOMER_NAME + " AS " + COLUMN_CUSTOMER_NAME_FULL);
        PROJECTION_MAP.put(TABLE_NAME + "." + COLUMN_DATE, TABLE_NAME + "." + COLUMN_DATE + " AS " + COLUMN_DATE_FULL);
        PROJECTION_MAP.put(TABLE_NAME + "." + COLUMN_START_TIME, TABLE_NAME + "." + COLUMN_START_TIME + " AS " + COLUMN_START_TIME_FULL);
        PROJECTION_MAP.put(TABLE_NAME + "." + COLUMN_END_TIME, TABLE_NAME + "." + COLUMN_END_TIME + " AS " + COLUMN_END_TIME_FULL);
        PROJECTION_MAP.put(TABLE_NAME + "." + COLUMN_LOCATION_NAME, TABLE_NAME + "." + COLUMN_LOCATION_NAME + " AS " + COLUMN_LOCATION_NAME_FULL);
        PROJECTION_MAP.put(TABLE_NAME + "." + COLUMN_PROXIMITY_REQUIRED, TABLE_NAME + "." + COLUMN_PROXIMITY_REQUIRED + " AS " + COLUMN_PROXIMITY_REQUIRED_FULL);
        PROJECTION_MAP.put(TABLE_NAME + "." + COLUMN_DESCRIPTION, TABLE_NAME + "." + COLUMN_DESCRIPTION + " AS " + COLUMN_DESCRIPTION_FULL);
        PROJECTION_MAP.put(TABLE_NAME + "." + COLUMN_INSTRUCTIONS, TABLE_NAME + "." + COLUMN_INSTRUCTIONS + " AS " + COLUMN_INSTRUCTIONS_FULL);
        PROJECTION_MAP.put(TABLE_NAME + "." + COLUMN_PHONE, TABLE_NAME + "." + COLUMN_PHONE + " AS " + COLUMN_PHONE_FULL);
        PROJECTION_MAP.put(TABLE_NAME + "." + COLUMN_STREET, TABLE_NAME + "." + COLUMN_STREET + " AS " + COLUMN_STREET_FULL);
        PROJECTION_MAP.put(TABLE_NAME + "." + COLUMN_POSTAL_CODE, TABLE_NAME + "." + COLUMN_POSTAL_CODE + " AS " + COLUMN_POSTAL_CODE_FULL);
        PROJECTION_MAP.put(TABLE_NAME + "." + COLUMN_CITY, TABLE_NAME + "." + COLUMN_CITY + " AS " + COLUMN_CITY_FULL);
        PROJECTION_MAP.put(TABLE_NAME + "." + COLUMN_LATITUDE, TABLE_NAME + "." + COLUMN_LATITUDE + " AS " + COLUMN_LATITUDE_FULL);
        PROJECTION_MAP.put(TABLE_NAME + "." + COLUMN_LONGITUDE, TABLE_NAME + "." + COLUMN_LONGITUDE + " AS " + COLUMN_LONGITUDE_FULL);
    }

    private static final String CREATE_TABLE = "create table IF NOT EXISTS " + TABLE_NAME + "("
            + COLUMN_ID + " integer primary key autoincrement, "
            + COLUMN_PLANNING_ID + " integer, "
            + COLUMN_CONTRACT_TYPE_ID + " integer, "
            + COLUMN_ACCOUNTANCY_NAME + " text, "
            + COLUMN_CUSTOMER_NAME + " text, "
            + COLUMN_DATE + " integer, "
            + COLUMN_START_TIME + " text, "
            + COLUMN_END_TIME + " text, "
            + COLUMN_LOCATION_NAME + " text, "
            + COLUMN_PROXIMITY_REQUIRED + " integer, "
            + COLUMN_DESCRIPTION + " text, "
            + COLUMN_INSTRUCTIONS + " text, "
            + COLUMN_PHONE + " text, "
            + COLUMN_STREET + " text, "
            + COLUMN_POSTAL_CODE + " text, "
            + COLUMN_CITY + " text, "
            + COLUMN_LATITUDE + " real, "
            + COLUMN_LONGITUDE + " real"
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
