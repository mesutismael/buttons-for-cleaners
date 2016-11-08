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

import java.util.HashMap;
import java.util.Map;

import be.appreciate.buttonsforcleaners.database.AnsweredQuestionTable;
import be.appreciate.buttonsforcleaners.database.DatabaseHelper;
import be.appreciate.buttonsforcleaners.database.QuestionTable;

/**
 * Created by Inneke De Clippel on 3/03/2016.
 */
public class QuestionContentProvider extends ContentProvider
{
    private DatabaseHelper databaseHelper;

    private static final String PROVIDER_NAME = "be.appreciate.buttonsforcleaners.contentproviders.QuestionContentProvider";
    public static final Uri CONTENT_URI = Uri.parse("content://" + PROVIDER_NAME + "/");
    public static final Uri CONTENT_URI_ANSWER = Uri.parse("content://" + PROVIDER_NAME + "/answer/");
    private static final int QUESTIONS = 1;
    private static final int QUESTION_ANSWER = 2;
    private static final UriMatcher URI_MATCHER;

    static
    {
        URI_MATCHER = new UriMatcher(UriMatcher.NO_MATCH);
        URI_MATCHER.addURI(PROVIDER_NAME, null, QUESTIONS);
        URI_MATCHER.addURI(PROVIDER_NAME, "answer/*", QUESTION_ANSWER);
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
            case QUESTIONS:
                return "vnd.android.cursor.dir/" + PROVIDER_NAME;

            case QUESTION_ANSWER:
                return "vnd.android.cursor.item/" + PROVIDER_NAME;
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
            case QUESTIONS:
                tables = QuestionTable.TABLE_NAME;
                projectionMap = QuestionTable.PROJECTION_MAP;
                queryBuilder.setTables(tables);
                queryBuilder.setProjectionMap(projectionMap);

                cursor = queryBuilder.query(db, projection, selection, selectionArgs, null, null, sortOrder);
                cursor.setNotificationUri(this.getContext().getContentResolver(), CONTENT_URI);
                return cursor;

            case QUESTION_ANSWER:
                String planningId = uri.getLastPathSegment();

                tables = QuestionTable.TABLE_NAME
                        + " left join " + AnsweredQuestionTable.TABLE_NAME
                        + " on " + QuestionTable.TABLE_NAME + "." + QuestionTable.COLUMN_QUESTION_ID + " = " + AnsweredQuestionTable.TABLE_NAME + "." + AnsweredQuestionTable.COLUMN_QUESTION_ID
                        + " AND " + AnsweredQuestionTable.TABLE_NAME + "." + AnsweredQuestionTable.COLUMN_PLANNING_ID + " = " + planningId;

                projectionMap = new HashMap<>();
                projectionMap.putAll(QuestionTable.PROJECTION_MAP);
                projectionMap.putAll(AnsweredQuestionTable.PROJECTION_MAP);

                queryBuilder.setTables(tables);
                queryBuilder.setProjectionMap(projectionMap);

                cursor = queryBuilder.query(db, projection, selection, selectionArgs, null, null, sortOrder);
                cursor.setNotificationUri(this.getContext().getContentResolver(), CONTENT_URI_ANSWER);
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
            case QUESTIONS:
                SQLiteDatabase db = this.databaseHelper.getWritableDatabase();
                rowId = db.replace(QuestionTable.TABLE_NAME, null, values);
                break;

            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }

        if (rowId > 0)
        {
            this.getContext().getContentResolver().notifyChange(CONTENT_URI, null);
            this.getContext().getContentResolver().notifyChange(CONTENT_URI_ANSWER, null);
            this.getContext().getContentResolver().notifyChange(AnsweredQuestionContentProvider.CONTENT_URI_EXTRA, null);
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
            case QUESTIONS:
                SQLiteDatabase db = this.databaseHelper.getWritableDatabase();
                rowsDeleted = db.delete(QuestionTable.TABLE_NAME, selection, selectionArgs);
                break;

            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }

        if(rowsDeleted > 0)
        {
            this.getContext().getContentResolver().notifyChange(CONTENT_URI, null);
            this.getContext().getContentResolver().notifyChange(CONTENT_URI_ANSWER, null);
            this.getContext().getContentResolver().notifyChange(AnsweredQuestionContentProvider.CONTENT_URI_EXTRA, null);
        }

        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs)
    {
        int rowsUpdated;

        switch (URI_MATCHER.match(uri))
        {
            case QUESTIONS:
                SQLiteDatabase db = this.databaseHelper.getWritableDatabase();
                rowsUpdated = db.update(QuestionTable.TABLE_NAME, values, selection, selectionArgs);
                break;

            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }

        if(rowsUpdated > 0)
        {
            this.getContext().getContentResolver().notifyChange(CONTENT_URI, null);
            this.getContext().getContentResolver().notifyChange(CONTENT_URI_ANSWER, null);
            this.getContext().getContentResolver().notifyChange(AnsweredQuestionContentProvider.CONTENT_URI_EXTRA, null);
        }

        return rowsUpdated;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values)
    {
        int rowsInserted = 0;

        switch (URI_MATCHER.match(uri))
        {
            case QUESTIONS:
                SQLiteDatabase db = this.databaseHelper.getWritableDatabase();
                DatabaseUtils.InsertHelper inserter = new DatabaseUtils.InsertHelper(db, QuestionTable.TABLE_NAME);

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
                                inserter.bind(inserter.getColumnIndex(QuestionTable.COLUMN_QUESTION_ID), cv.getAsInteger(QuestionTable.COLUMN_QUESTION_ID));
                                inserter.bind(inserter.getColumnIndex(QuestionTable.COLUMN_LABEL), cv.getAsString(QuestionTable.COLUMN_LABEL));
                                inserter.bind(inserter.getColumnIndex(QuestionTable.COLUMN_TYPE), cv.getAsString(QuestionTable.COLUMN_TYPE));
                                inserter.bind(inserter.getColumnIndex(QuestionTable.COLUMN_ANSWERS), cv.getAsString(QuestionTable.COLUMN_ANSWERS));
                                inserter.bind(inserter.getColumnIndex(QuestionTable.COLUMN_CONTRACT_TYPE_ID), cv.getAsInteger(QuestionTable.COLUMN_CONTRACT_TYPE_ID));
                                inserter.bind(inserter.getColumnIndex(QuestionTable.COLUMN_POINT_OF_TIME), cv.getAsInteger(QuestionTable.COLUMN_POINT_OF_TIME));

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
            this.getContext().getContentResolver().notifyChange(CONTENT_URI_ANSWER, null);
            this.getContext().getContentResolver().notifyChange(AnsweredQuestionContentProvider.CONTENT_URI_EXTRA, null);
        }

        return rowsInserted;
    }
}
