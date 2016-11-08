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

import java.util.Map;

import be.appreciate.buttonsforcleaners.database.DatabaseHelper;
import be.appreciate.buttonsforcleaners.database.ProductTable;

/**
 * Created by Inneke De Clippel on 3/03/2016.
 */
public class ProductContentProvider extends ContentProvider
{
    private DatabaseHelper databaseHelper;

    private static final String PROVIDER_NAME = "be.appreciate.buttonsforcleaners.contentproviders.ProductContentProvider";
    public static final Uri CONTENT_URI = Uri.parse("content://" + PROVIDER_NAME + "/");
    public static final Uri CONTENT_URI_CATEGORIES = Uri.parse("content://" + PROVIDER_NAME + "/categories/");
    private static final int PRODUCTS = 1;
    private static final int CATEGORIES = 2;
    private static final UriMatcher URI_MATCHER;

    static
    {
        URI_MATCHER = new UriMatcher(UriMatcher.NO_MATCH);
        URI_MATCHER.addURI(PROVIDER_NAME, null, PRODUCTS);
        URI_MATCHER.addURI(PROVIDER_NAME, "categories", CATEGORIES);
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
            case PRODUCTS:
            case CATEGORIES:
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
            case PRODUCTS:
                tables = ProductTable.TABLE_NAME;
                projectionMap = ProductTable.PROJECTION_MAP;
                queryBuilder.setTables(tables);
                queryBuilder.setProjectionMap(projectionMap);

                cursor = queryBuilder.query(db, projection, selection, selectionArgs, null, null, sortOrder);
                cursor.setNotificationUri(this.getContext().getContentResolver(), CONTENT_URI);
                return cursor;

            case CATEGORIES:
                tables = ProductTable.TABLE_NAME;
                projectionMap = ProductTable.PROJECTION_MAP;
                queryBuilder.setTables(tables);
                queryBuilder.setProjectionMap(projectionMap);

                String groupBy = ProductTable.TABLE_NAME + "." + ProductTable.COLUMN_CATEGORY;

                cursor = queryBuilder.query(db, projection, selection, selectionArgs, groupBy, null, sortOrder);
                cursor.setNotificationUri(this.getContext().getContentResolver(), CONTENT_URI);
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
            case PRODUCTS:
                SQLiteDatabase db = this.databaseHelper.getWritableDatabase();
                rowId = db.replace(ProductTable.TABLE_NAME, null, values);
                break;

            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }

        if (rowId > 0)
        {
            this.getContext().getContentResolver().notifyChange(CONTENT_URI, null);
            this.getContext().getContentResolver().notifyChange(CONTENT_URI_CATEGORIES, null);
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
            case PRODUCTS:
                SQLiteDatabase db = this.databaseHelper.getWritableDatabase();
                rowsDeleted = db.delete(ProductTable.TABLE_NAME, selection, selectionArgs);
                break;

            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }

        if(rowsDeleted > 0)
        {
            this.getContext().getContentResolver().notifyChange(CONTENT_URI, null);
            this.getContext().getContentResolver().notifyChange(CONTENT_URI_CATEGORIES, null);
        }

        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs)
    {
        int rowsUpdated;

        switch (URI_MATCHER.match(uri))
        {
            case PRODUCTS:
                SQLiteDatabase db = this.databaseHelper.getWritableDatabase();
                rowsUpdated = db.update(ProductTable.TABLE_NAME, values, selection, selectionArgs);
                break;

            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }

        if(rowsUpdated > 0)
        {
            this.getContext().getContentResolver().notifyChange(CONTENT_URI, null);
            this.getContext().getContentResolver().notifyChange(CONTENT_URI_CATEGORIES, null);
        }

        return rowsUpdated;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values)
    {
        int rowsInserted = 0;

        switch (URI_MATCHER.match(uri))
        {
            case PRODUCTS:
                SQLiteDatabase db = this.databaseHelper.getWritableDatabase();
                DatabaseUtils.InsertHelper inserter = new DatabaseUtils.InsertHelper(db, ProductTable.TABLE_NAME);

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
                                inserter.bind(inserter.getColumnIndex(ProductTable.COLUMN_PRODUCT_ID), cv.getAsInteger(ProductTable.COLUMN_PRODUCT_ID));
                                inserter.bind(inserter.getColumnIndex(ProductTable.COLUMN_NAME), cv.getAsString(ProductTable.COLUMN_NAME));
                                inserter.bind(inserter.getColumnIndex(ProductTable.COLUMN_IMAGE_URL), cv.getAsString(ProductTable.COLUMN_IMAGE_URL));
                                inserter.bind(inserter.getColumnIndex(ProductTable.COLUMN_UNIT), cv.getAsString(ProductTable.COLUMN_UNIT));
                                inserter.bind(inserter.getColumnIndex(ProductTable.COLUMN_CATEGORY), cv.getAsString(ProductTable.COLUMN_CATEGORY));

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
            this.getContext().getContentResolver().notifyChange(CONTENT_URI_CATEGORIES, null);
        }

        return rowsInserted;
    }
}
