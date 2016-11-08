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
import be.appreciate.buttonsforcleaners.database.PlanningProductTable;

/**
 * Created by Inneke De Clippel on 4/03/2016.
 */
public class PlanningProductContentProvider extends ContentProvider
{
    private DatabaseHelper databaseHelper;

    private static final String PROVIDER_NAME = "be.appreciate.buttonsforcleaners.contentproviders.PlanningProductContentProvider";
    public static final Uri CONTENT_URI = Uri.parse("content://" + PROVIDER_NAME + "/");
    private static final int PLANNING_PRODUCTS = 1;
    private static final UriMatcher URI_MATCHER;

    static
    {
        URI_MATCHER = new UriMatcher(UriMatcher.NO_MATCH);
        URI_MATCHER.addURI(PROVIDER_NAME, null, PLANNING_PRODUCTS);
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
            case PLANNING_PRODUCTS:
                return "vnd.android.cursor.dir/" + PROVIDER_NAME;
        }

        return null;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder)
    {
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        queryBuilder.setTables(PlanningProductTable.TABLE_NAME);
        queryBuilder.setProjectionMap(PlanningProductTable.PROJECTION_MAP);

        switch (URI_MATCHER.match(uri))
        {
            case PLANNING_PRODUCTS:
                break;

            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }

        SQLiteDatabase db = this.databaseHelper.getReadableDatabase();
        Cursor cursor = queryBuilder.query(db, projection, selection, selectionArgs, null, null, sortOrder);
        cursor.setNotificationUri(this.getContext().getContentResolver(), uri);
        return cursor;
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values)
    {
        long rowId;

        switch (URI_MATCHER.match(uri))
        {
            case PLANNING_PRODUCTS:
                SQLiteDatabase db = this.databaseHelper.getWritableDatabase();
                rowId = db.replace(PlanningProductTable.TABLE_NAME, null, values);
                break;

            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }

        if (rowId > 0)
        {
            this.getContext().getContentResolver().notifyChange(CONTENT_URI, null);
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
            case PLANNING_PRODUCTS:
                SQLiteDatabase db = this.databaseHelper.getWritableDatabase();
                rowsDeleted = db.delete(PlanningProductTable.TABLE_NAME, selection, selectionArgs);
                break;

            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }

        if(rowsDeleted > 0)
        {
            this.getContext().getContentResolver().notifyChange(CONTENT_URI, null);
        }

        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs)
    {
        int rowsUpdated;

        switch (URI_MATCHER.match(uri))
        {
            case PLANNING_PRODUCTS:
                SQLiteDatabase db = this.databaseHelper.getWritableDatabase();
                rowsUpdated = db.update(PlanningProductTable.TABLE_NAME, values, selection, selectionArgs);
                break;

            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }

        if(rowsUpdated > 0)
        {
            this.getContext().getContentResolver().notifyChange(CONTENT_URI, null);
        }

        return rowsUpdated;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values)
    {
        int rowsInserted = 0;

        switch (URI_MATCHER.match(uri))
        {
            case PLANNING_PRODUCTS:
                SQLiteDatabase db = this.databaseHelper.getWritableDatabase();
                DatabaseUtils.InsertHelper inserter = new DatabaseUtils.InsertHelper(db, PlanningProductTable.TABLE_NAME);

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
                                inserter.bind(inserter.getColumnIndex(PlanningProductTable.COLUMN_PRODUCT_ID), cv.getAsInteger(PlanningProductTable.COLUMN_PRODUCT_ID));
                                inserter.bind(inserter.getColumnIndex(PlanningProductTable.COLUMN_PLANNING_ID), cv.getAsInteger(PlanningProductTable.COLUMN_PLANNING_ID));
                                inserter.bind(inserter.getColumnIndex(PlanningProductTable.COLUMN_NAME), cv.getAsString(PlanningProductTable.COLUMN_NAME));
                                inserter.bind(inserter.getColumnIndex(PlanningProductTable.COLUMN_IMAGE_URL), cv.getAsString(PlanningProductTable.COLUMN_IMAGE_URL));
                                inserter.bind(inserter.getColumnIndex(PlanningProductTable.COLUMN_UNIT), cv.getAsString(PlanningProductTable.COLUMN_UNIT));
                                inserter.bind(inserter.getColumnIndex(PlanningProductTable.COLUMN_CATEGORY), cv.getAsString(PlanningProductTable.COLUMN_CATEGORY));

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
        }

        return rowsInserted;
    }
}
