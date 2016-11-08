package be.appreciate.buttonsforcleaners.database;

import android.database.sqlite.SQLiteDatabase;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Inneke De Clippel on 3/03/2016.
 */
public class ContractTypeTable
{
    public static final String TABLE_NAME = "contract_types";

    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_CONTRACT_TYPE_ID = "contract_type_id";
    public static final String COLUMN_NAME = "name";

    public static final String COLUMN_ID_FULL = TABLE_NAME + "_" + COLUMN_ID;
    public static final String COLUMN_CONTRACT_TYPE_ID_FULL = TABLE_NAME + "_" + COLUMN_CONTRACT_TYPE_ID;
    public static final String COLUMN_NAME_FULL = TABLE_NAME + "_" + COLUMN_NAME;

    public static final Map<String, String> PROJECTION_MAP;

    static
    {
        PROJECTION_MAP = new HashMap<>();
        PROJECTION_MAP.put(TABLE_NAME + "." + COLUMN_ID, TABLE_NAME + "." + COLUMN_ID + " AS " + COLUMN_ID_FULL);
        PROJECTION_MAP.put(TABLE_NAME + "." + COLUMN_CONTRACT_TYPE_ID, TABLE_NAME + "." + COLUMN_CONTRACT_TYPE_ID + " AS " + COLUMN_CONTRACT_TYPE_ID_FULL);
        PROJECTION_MAP.put(TABLE_NAME + "." + COLUMN_NAME, TABLE_NAME + "." + COLUMN_NAME + " AS " + COLUMN_NAME_FULL);
    }

    private static final String CREATE_TABLE = "create table IF NOT EXISTS " + TABLE_NAME + "("
            + COLUMN_ID + " integer primary key autoincrement, "
            + COLUMN_CONTRACT_TYPE_ID + " integer, "
            + COLUMN_NAME + " text"
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
