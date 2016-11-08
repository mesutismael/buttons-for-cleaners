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

import be.appreciate.buttonsforcleaners.database.DatabaseHelper;
import be.appreciate.buttonsforcleaners.database.FeedbackTable;

/**
 * Created by Inneke De Clippel on 7/03/2016.
 */
public class FeedbackContentProvider extends ContentProvider
{
    private DatabaseHelper databaseHelper;

    private static final String PROVIDER_NAME = "be.appreciate.buttonsforcleaners.contentproviders.FeedbackContentProvider";
    public static final Uri CONTENT_URI = Uri.parse("content://" + PROVIDER_NAME + "/");
    public static final Uri CONTENT_URI_DISTANCE = Uri.parse("content://" + PROVIDER_NAME + "/distance/");
    private static final int FEEDBACK_ITEMS = 1;
    private static final int FEEDBACK_ITEMS_DISTANCE = 2;
    private static final UriMatcher URI_MATCHER;

    static
    {
        URI_MATCHER = new UriMatcher(UriMatcher.NO_MATCH);
        URI_MATCHER.addURI(PROVIDER_NAME, null, FEEDBACK_ITEMS);
        URI_MATCHER.addURI(PROVIDER_NAME, "distance", FEEDBACK_ITEMS_DISTANCE);
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
            case FEEDBACK_ITEMS:
            case FEEDBACK_ITEMS_DISTANCE:
                return "vnd.android.cursor.dir/" + PROVIDER_NAME;
        }

        return null;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder)
    {
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        queryBuilder.setTables(FeedbackTable.TABLE_NAME);
        queryBuilder.setProjectionMap(FeedbackTable.PROJECTION_MAP);

        switch (URI_MATCHER.match(uri))
        {
            case FEEDBACK_ITEMS:
                break;

            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }

        SQLiteDatabase db = this.databaseHelper.getReadableDatabase();
        Cursor cursor = queryBuilder.query(db, projection, selection, selectionArgs, null, null, sortOrder);
        cursor.setNotificationUri(this.getContext().getContentResolver(), CONTENT_URI);
        return cursor;
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values)
    {
        long rowId;

        switch (URI_MATCHER.match(uri))
        {
            case FEEDBACK_ITEMS:
                SQLiteDatabase db = this.databaseHelper.getWritableDatabase();
                rowId = db.replace(FeedbackTable.TABLE_NAME, null, values);
                break;

            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }

        if (rowId > 0)
        {
            this.getContext().getContentResolver().notifyChange(CONTENT_URI, null);
            this.getContext().getContentResolver().notifyChange(PlanningContentProvider.CONTENT_URI_EXTRA, null);
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
            case FEEDBACK_ITEMS:
                SQLiteDatabase db = this.databaseHelper.getWritableDatabase();
                rowsDeleted = db.delete(FeedbackTable.TABLE_NAME, selection, selectionArgs);
                break;

            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }

        if(rowsDeleted > 0)
        {
            this.getContext().getContentResolver().notifyChange(CONTENT_URI, null);
            this.getContext().getContentResolver().notifyChange(PlanningContentProvider.CONTENT_URI_EXTRA, null);
        }

        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs)
    {
        int rowsUpdated;
        SQLiteDatabase db;

        switch (URI_MATCHER.match(uri))
        {
            case FEEDBACK_ITEMS:
                db = this.databaseHelper.getWritableDatabase();
                rowsUpdated = db.update(FeedbackTable.TABLE_NAME, values, selection, selectionArgs);

                if(rowsUpdated > 0)
                {
                    this.getContext().getContentResolver().notifyChange(CONTENT_URI, null);
                    this.getContext().getContentResolver().notifyChange(PlanningContentProvider.CONTENT_URI_EXTRA, null);
                }
                break;

            case FEEDBACK_ITEMS_DISTANCE:
                db = this.databaseHelper.getWritableDatabase();
                rowsUpdated = db.update(FeedbackTable.TABLE_NAME, values, selection, selectionArgs);
                //No listeners should be notified of changes to the distance
                break;

            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }

        return rowsUpdated;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values)
    {
        int rowsInserted = 0;

        switch (URI_MATCHER.match(uri))
        {
            case FEEDBACK_ITEMS:
                SQLiteDatabase db = this.databaseHelper.getWritableDatabase();
                DatabaseUtils.InsertHelper inserter = new DatabaseUtils.InsertHelper(db, FeedbackTable.TABLE_NAME);

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
                                inserter.bind(inserter.getColumnIndex(FeedbackTable.COLUMN_PLANNING_ID), cv.getAsInteger(FeedbackTable.COLUMN_PLANNING_ID));
                                inserter.bind(inserter.getColumnIndex(FeedbackTable.COLUMN_START_TIME), cv.getAsInteger(FeedbackTable.COLUMN_START_TIME));
                                inserter.bind(inserter.getColumnIndex(FeedbackTable.COLUMN_END_TIME), cv.getAsInteger(FeedbackTable.COLUMN_END_TIME));
                                inserter.bind(inserter.getColumnIndex(FeedbackTable.COLUMN_START_COMPLETED), cv.getAsInteger(FeedbackTable.COLUMN_START_COMPLETED));
                                inserter.bind(inserter.getColumnIndex(FeedbackTable.COLUMN_END_COMPLETED), cv.getAsInteger(FeedbackTable.COLUMN_END_COMPLETED));
                                inserter.bind(inserter.getColumnIndex(FeedbackTable.COLUMN_SENT_TO_API), cv.getAsInteger(FeedbackTable.COLUMN_SENT_TO_API));
                                inserter.bind(inserter.getColumnIndex(FeedbackTable.COLUMN_DISTANCE_TRAVELED), cv.getAsInteger(FeedbackTable.COLUMN_DISTANCE_TRAVELED));

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
            this.getContext().getContentResolver().notifyChange(PlanningContentProvider.CONTENT_URI_EXTRA, null);
        }

        return rowsInserted;
    }
}
