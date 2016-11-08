package be.appreciate.buttonsforcleaners.contentproviders;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import java.util.HashMap;
import java.util.Map;

import be.appreciate.buttonsforcleaners.database.AnsweredQuestionTable;
import be.appreciate.buttonsforcleaners.database.DatabaseHelper;
import be.appreciate.buttonsforcleaners.database.QuestionTable;

/**
 * Created by Inneke De Clippel on 7/03/2016.
 */
public class AnsweredQuestionContentProvider extends ContentProvider
{
    private DatabaseHelper databaseHelper;

    private static final String PROVIDER_NAME = "be.appreciate.buttonsforcleaners.contentproviders.AnsweredQuestionContentProvider";
    public static final Uri CONTENT_URI = Uri.parse("content://" + PROVIDER_NAME + "/");
    public static final Uri CONTENT_URI_EXTRA = Uri.parse("content://" + PROVIDER_NAME + "/question/");
    private static final int ANSWERED_QUESTIONS = 1;
    private static final int ANSWERED_QUESTIONS_EXTRA = 2;
    private static final UriMatcher URI_MATCHER;

    static
    {
        URI_MATCHER = new UriMatcher(UriMatcher.NO_MATCH);
        URI_MATCHER.addURI(PROVIDER_NAME, null, ANSWERED_QUESTIONS);
        URI_MATCHER.addURI(PROVIDER_NAME, "question", ANSWERED_QUESTIONS_EXTRA);
    }

    @Override
    public boolean onCreate()
    {
        this.databaseHelper = new DatabaseHelper(this.getContext());
        return true;
    }

    @Nullable
    @Override
    public String getType(Uri uri)
    {
        switch (URI_MATCHER.match(uri))
        {
            case ANSWERED_QUESTIONS:
            case ANSWERED_QUESTIONS_EXTRA:
                return "vnd.android.cursor.dir/" + PROVIDER_NAME;
        }

        return null;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder)
    {
        SQLiteDatabase db = this.databaseHelper.getReadableDatabase();
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        Cursor cursor;
        String tables;
        Map<String, String> projectionMap;

        switch (URI_MATCHER.match(uri))
        {
            case ANSWERED_QUESTIONS:
                tables = AnsweredQuestionTable.TABLE_NAME;
                projectionMap = AnsweredQuestionTable.PROJECTION_MAP;
                queryBuilder.setTables(tables);
                queryBuilder.setProjectionMap(projectionMap);

                cursor = queryBuilder.query(db, projection, selection, selectionArgs, null, null, sortOrder);
                cursor.setNotificationUri(this.getContext().getContentResolver(), CONTENT_URI);
                return cursor;

            case ANSWERED_QUESTIONS_EXTRA:
                tables = AnsweredQuestionTable.TABLE_NAME
                        + " left join " + QuestionTable.TABLE_NAME
                        + " on " + AnsweredQuestionTable.TABLE_NAME + "." + AnsweredQuestionTable.COLUMN_QUESTION_ID + " = " + QuestionTable.TABLE_NAME + "." + QuestionTable.COLUMN_QUESTION_ID;

                projectionMap = new HashMap<>();
                projectionMap.putAll(AnsweredQuestionTable.PROJECTION_MAP);
                projectionMap.putAll(QuestionTable.PROJECTION_MAP);

                queryBuilder.setTables(tables);
                queryBuilder.setProjectionMap(projectionMap);

                cursor = queryBuilder.query(db, projection, selection, selectionArgs, null, null, sortOrder);
                cursor.setNotificationUri(this.getContext().getContentResolver(), CONTENT_URI_EXTRA);
                return cursor;

            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values)
    {
        long rowId;

        switch (URI_MATCHER.match(uri))
        {
            case ANSWERED_QUESTIONS:
                SQLiteDatabase db = this.databaseHelper.getWritableDatabase();
                rowId = db.replace(AnsweredQuestionTable.TABLE_NAME, null, values);
                break;

            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }

        if (rowId > 0)
        {
            this.getContext().getContentResolver().notifyChange(CONTENT_URI, null);
            this.getContext().getContentResolver().notifyChange(CONTENT_URI_EXTRA, null);
            this.getContext().getContentResolver().notifyChange(QuestionContentProvider.CONTENT_URI_ANSWER, null);
            return Uri.withAppendedPath(CONTENT_URI, String.valueOf(rowId));
        }

        throw new SQLException("Failed to add a record into " + uri);
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs)
    {
        int rowsDeleted;

        switch (URI_MATCHER.match(uri))
        {
            case ANSWERED_QUESTIONS:
                SQLiteDatabase db = this.databaseHelper.getWritableDatabase();
                rowsDeleted = db.delete(AnsweredQuestionTable.TABLE_NAME, selection, selectionArgs);
                break;

            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }

        if(rowsDeleted > 0)
        {
            this.getContext().getContentResolver().notifyChange(CONTENT_URI, null);
            this.getContext().getContentResolver().notifyChange(CONTENT_URI_EXTRA, null);
            this.getContext().getContentResolver().notifyChange(QuestionContentProvider.CONTENT_URI_ANSWER, null);
        }

        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs)
    {
        int rowsUpdated;

        switch (URI_MATCHER.match(uri))
        {
            case ANSWERED_QUESTIONS:
                SQLiteDatabase db = this.databaseHelper.getWritableDatabase();
                rowsUpdated = db.update(AnsweredQuestionTable.TABLE_NAME, values, selection, selectionArgs);
                break;

            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }

        if(rowsUpdated > 0)
        {
            this.getContext().getContentResolver().notifyChange(CONTENT_URI, null);
            this.getContext().getContentResolver().notifyChange(CONTENT_URI_EXTRA, null);
            this.getContext().getContentResolver().notifyChange(QuestionContentProvider.CONTENT_URI_ANSWER, null);
        }

        return rowsUpdated;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values)
    {
        int rowsInserted = 0;

        switch (URI_MATCHER.match(uri))
        {
            case ANSWERED_QUESTIONS:
                SQLiteDatabase db = this.databaseHelper.getWritableDatabase();
                DatabaseUtils.InsertHelper inserter = new DatabaseUtils.InsertHelper(db, AnsweredQuestionTable.TABLE_NAME);

                db.beginTransaction();
                try
                {
                    if(values != null)
                    {
                        for (ContentValues cv : values)
                        {
                            if(cv != null)
                            {
                                inserter.prepareForInsert();
                                inserter.bind(inserter.getColumnIndex(AnsweredQuestionTable.COLUMN_PLANNING_ID), cv.getAsInteger(AnsweredQuestionTable.COLUMN_PLANNING_ID));
                                inserter.bind(inserter.getColumnIndex(AnsweredQuestionTable.COLUMN_QUESTION_ID), cv.getAsInteger(AnsweredQuestionTable.COLUMN_QUESTION_ID));
                                inserter.bind(inserter.getColumnIndex(AnsweredQuestionTable.COLUMN_ANSWERS), cv.getAsString(AnsweredQuestionTable.COLUMN_ANSWERS));

                                long rowId = inserter.execute();

                                if (rowId != -1)
                                {
                                    rowsInserted++;
                                }
                            }
                        }
                    }

                    db.setTransactionSuccessful();
                }
                catch (Exception e)
                {
                    rowsInserted = 0;
                }
                finally
                {
                    db.endTransaction();
                    inserter.close();
                }
                break;

            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }

        if (rowsInserted > 0)
        {
            this.getContext().getContentResolver().notifyChange(CONTENT_URI, null);
            this.getContext().getContentResolver().notifyChange(CONTENT_URI_EXTRA, null);
            this.getContext().getContentResolver().notifyChange(QuestionContentProvider.CONTENT_URI_ANSWER, null);
        }

        return rowsInserted;
    }
}
