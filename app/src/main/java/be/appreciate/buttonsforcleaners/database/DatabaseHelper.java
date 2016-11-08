package be.appreciate.buttonsforcleaners.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Inneke De Clippel on 3/03/2016.
 */
public class DatabaseHelper extends SQLiteOpenHelper
{
    public static final String DATABASE_NAME = "buttons_for_cleaners.db";
    public static final int DATABASE_VERSION = 1;

    public DatabaseHelper(Context context)
    {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        ProductTable.onCreate(db);
        ContractTypeTable.onCreate(db);
        QuestionTable.onCreate(db);
        PlanningTable.onCreate(db);
        PlanningProductTable.onCreate(db);
        PlanningResourceTable.onCreate(db);
        FeedbackTable.onCreate(db);
        AnsweredQuestionTable.onCreate(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        ProductTable.onUpgrade(db, oldVersion, newVersion);
        ContractTypeTable.onUpgrade(db, oldVersion, newVersion);
        QuestionTable.onUpgrade(db, oldVersion, newVersion);
        PlanningTable.onUpgrade(db, oldVersion, newVersion);
        PlanningProductTable.onUpgrade(db, oldVersion, newVersion);
        PlanningResourceTable.onUpgrade(db, oldVersion, newVersion);
        FeedbackTable.onUpgrade(db, oldVersion, newVersion);
        AnsweredQuestionTable.onUpgrade(db, oldVersion, newVersion);
    }
}
