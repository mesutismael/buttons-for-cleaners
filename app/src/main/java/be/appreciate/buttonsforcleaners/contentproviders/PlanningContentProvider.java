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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import be.appreciate.buttonsforcleaners.database.ContractTypeTable;
import be.appreciate.buttonsforcleaners.database.DatabaseHelper;
import be.appreciate.buttonsforcleaners.database.FeedbackTable;
import be.appreciate.buttonsforcleaners.database.PlanningTable;
import be.appreciate.buttonsforcleaners.database.QuestionTable;
import be.appreciate.buttonsforcleaners.model.PointOfTime;
import be.appreciate.buttonsforcleaners.utils.DateUtils;

/**
 * Created by Inneke De Clippel on 4/03/2016.
 */
public class PlanningContentProvider extends ContentProvider
{
    private DatabaseHelper databaseHelper;

    private static final String PROVIDER_NAME = "be.appreciate.buttonsforcleaners.contentproviders.PlanningContentProvider";
    public static final Uri CONTENT_URI = Uri.parse("content://" + PROVIDER_NAME + "/");
    public static final Uri CONTENT_URI_EXTRA = Uri.parse("content://" + PROVIDER_NAME + "/extra/");
    private static final int PLANNING_ITEMS = 1;
    private static final int PLANNING_ITEMS_EXTRA = 2;
    private static final int PLANNING_ITEM_EXTRA = 3;
    private static final UriMatcher URI_MATCHER;

    static
    {
        URI_MATCHER = new UriMatcher(UriMatcher.NO_MATCH);
        URI_MATCHER.addURI(PROVIDER_NAME, null, PLANNING_ITEMS);
        URI_MATCHER.addURI(PROVIDER_NAME, "extra", PLANNING_ITEMS_EXTRA);
        URI_MATCHER.addURI(PROVIDER_NAME, "extra/*", PLANNING_ITEM_EXTRA);
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
            case PLANNING_ITEMS:
            case PLANNING_ITEMS_EXTRA:
                return "vnd.android.cursor.dir/" + PROVIDER_NAME;

            case PLANNING_ITEM_EXTRA:
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
            case PLANNING_ITEMS:
                tables = PlanningTable.TABLE_NAME;
                projectionMap = PlanningTable.PROJECTION_MAP;
                queryBuilder.setTables(tables);
                queryBuilder.setProjectionMap(projectionMap);

                cursor = queryBuilder.query(db, projection, selection, selectionArgs, null, null, sortOrder);
                cursor.setNotificationUri(this.getContext().getContentResolver(), CONTENT_URI);
                return cursor;

            case PLANNING_ITEMS_EXTRA:
                tables = PlanningTable.TABLE_NAME
                        + " left join " + ContractTypeTable.TABLE_NAME
                        + " on " + PlanningTable.TABLE_NAME + "." + PlanningTable.COLUMN_CONTRACT_TYPE_ID + " = " + ContractTypeTable.TABLE_NAME + "." + ContractTypeTable.COLUMN_CONTRACT_TYPE_ID
                        + " left join " + FeedbackTable.TABLE_NAME
                        + " on " + PlanningTable.TABLE_NAME + "." + PlanningTable.COLUMN_PLANNING_ID + " = " + FeedbackTable.TABLE_NAME + "." + FeedbackTable.COLUMN_PLANNING_ID;

                projectionMap = new HashMap<>();
                projectionMap.putAll(PlanningTable.PROJECTION_MAP);
                projectionMap.putAll(ContractTypeTable.PROJECTION_MAP);
                projectionMap.putAll(FeedbackTable.PROJECTION_MAP);

                queryBuilder.setTables(tables);
                queryBuilder.setProjectionMap(projectionMap);

                String selectionToday = PlanningTable.TABLE_NAME + "." + PlanningTable.COLUMN_DATE + " BETWEEN " + DateUtils.getStartOfToday() + " AND " + DateUtils.getEndOfToday();
                selection = TextUtils.isEmpty(selection) ? selectionToday : selection + " AND " + selectionToday;

                cursor = queryBuilder.query(db, projection, selection, selectionArgs, null, null, sortOrder);
                cursor.setNotificationUri(this.getContext().getContentResolver(), CONTENT_URI_EXTRA);
                return cursor;

            case PLANNING_ITEM_EXTRA:
                tables = PlanningTable.TABLE_NAME
                        + " left join " + ContractTypeTable.TABLE_NAME
                        + " on " + PlanningTable.TABLE_NAME + "." + PlanningTable.COLUMN_CONTRACT_TYPE_ID + " = " + ContractTypeTable.TABLE_NAME + "." + ContractTypeTable.COLUMN_CONTRACT_TYPE_ID
                        + " left join " + FeedbackTable.TABLE_NAME
                        + " on " + PlanningTable.TABLE_NAME + "." + PlanningTable.COLUMN_PLANNING_ID + " = " + FeedbackTable.TABLE_NAME + "." + FeedbackTable.COLUMN_PLANNING_ID;

                projectionMap = new HashMap<>();
                projectionMap.putAll(PlanningTable.PROJECTION_MAP);
                projectionMap.putAll(ContractTypeTable.PROJECTION_MAP);
                projectionMap.putAll(FeedbackTable.PROJECTION_MAP);

                queryBuilder.setTables(tables);
                queryBuilder.setProjectionMap(projectionMap);

                String innerQueryStart = "SELECT COUNT(*)"
                        + " FROM " + QuestionTable.TABLE_NAME
                        + " WHERE " + QuestionTable.COLUMN_POINT_OF_TIME + " = " + PointOfTime.START.getPointOfTimeId()
                        + " AND " + QuestionTable.COLUMN_CONTRACT_TYPE_ID + " = " + PlanningTable.TABLE_NAME + "." + PlanningTable.COLUMN_CONTRACT_TYPE_ID;

                String innerQueryStop = "SELECT COUNT(*)"
                        + " FROM " + QuestionTable.TABLE_NAME
                        + " WHERE " + QuestionTable.COLUMN_POINT_OF_TIME + " = " + PointOfTime.STOP.getPointOfTimeId()
                        + " AND " + QuestionTable.COLUMN_CONTRACT_TYPE_ID + " = " + PlanningTable.TABLE_NAME + "." + PlanningTable.COLUMN_CONTRACT_TYPE_ID;

                List<String> projectionAsList = new ArrayList<>();
                if (projection == null || projection.length == 0)
                {
                    for (String key : projectionMap.keySet())
                    {
                        projectionAsList.add(key);
                    }
                }
                else
                {
                    Collections.addAll(projectionAsList, projection);
                }

                projectionAsList.add("(" + innerQueryStart + ") AS " + PlanningTable.COLUMN_QUESTIONS_AT_START_FULL);
                projectionAsList.add("(" + innerQueryStop + ") AS " + PlanningTable.COLUMN_QUESTIONS_AT_STOP_FULL);

                projection = projectionAsList.toArray(new String[projectionAsList.size()]);

                String planningId = uri.getLastPathSegment();
                String selectionId = PlanningTable.TABLE_NAME + "." + PlanningTable.COLUMN_PLANNING_ID + " = " + planningId;
                selection = TextUtils.isEmpty(selection) ? selectionId : selection + " AND " + selectionId;

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
            case PLANNING_ITEMS:
                SQLiteDatabase db = this.databaseHelper.getWritableDatabase();
                rowId = db.replace(PlanningTable.TABLE_NAME, null, values);
                break;

            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }

        if (rowId > 0)
        {
            this.getContext().getContentResolver().notifyChange(CONTENT_URI, null);
            this.getContext().getContentResolver().notifyChange(CONTENT_URI_EXTRA, null);
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
            case PLANNING_ITEMS:
                SQLiteDatabase db = this.databaseHelper.getWritableDatabase();
                rowsDeleted = db.delete(PlanningTable.TABLE_NAME, selection, selectionArgs);
                break;

            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }

        if(rowsDeleted > 0)
        {
            this.getContext().getContentResolver().notifyChange(CONTENT_URI, null);
            this.getContext().getContentResolver().notifyChange(CONTENT_URI_EXTRA, null);
        }

        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs)
    {
        int rowsUpdated;

        switch (URI_MATCHER.match(uri))
        {
            case PLANNING_ITEMS:
                SQLiteDatabase db = this.databaseHelper.getWritableDatabase();
                rowsUpdated = db.update(PlanningTable.TABLE_NAME, values, selection, selectionArgs);
                break;

            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }

        if(rowsUpdated > 0)
        {
            this.getContext().getContentResolver().notifyChange(CONTENT_URI, null);
            this.getContext().getContentResolver().notifyChange(CONTENT_URI_EXTRA, null);
        }

        return rowsUpdated;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values)
    {
        int rowsInserted = 0;

        switch (URI_MATCHER.match(uri))
        {
            case PLANNING_ITEMS:
                SQLiteDatabase db = this.databaseHelper.getWritableDatabase();
                DatabaseUtils.InsertHelper inserter = new DatabaseUtils.InsertHelper(db, PlanningTable.TABLE_NAME);

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
                                inserter.bind(inserter.getColumnIndex(PlanningTable.COLUMN_PLANNING_ID), cv.getAsInteger(PlanningTable.COLUMN_PLANNING_ID));
                                inserter.bind(inserter.getColumnIndex(PlanningTable.COLUMN_CONTRACT_TYPE_ID), cv.getAsInteger(PlanningTable.COLUMN_CONTRACT_TYPE_ID));
                                inserter.bind(inserter.getColumnIndex(PlanningTable.COLUMN_ACCOUNTANCY_NAME), cv.getAsString(PlanningTable.COLUMN_ACCOUNTANCY_NAME));
                                inserter.bind(inserter.getColumnIndex(PlanningTable.COLUMN_CUSTOMER_NAME), cv.getAsString(PlanningTable.COLUMN_CUSTOMER_NAME));
                                inserter.bind(inserter.getColumnIndex(PlanningTable.COLUMN_DATE), cv.getAsInteger(PlanningTable.COLUMN_DATE));
                                inserter.bind(inserter.getColumnIndex(PlanningTable.COLUMN_START_TIME), cv.getAsString(PlanningTable.COLUMN_START_TIME));
                                inserter.bind(inserter.getColumnIndex(PlanningTable.COLUMN_END_TIME), cv.getAsString(PlanningTable.COLUMN_END_TIME));
                                inserter.bind(inserter.getColumnIndex(PlanningTable.COLUMN_LOCATION_NAME), cv.getAsString(PlanningTable.COLUMN_LOCATION_NAME));
                                inserter.bind(inserter.getColumnIndex(PlanningTable.COLUMN_PROXIMITY_REQUIRED), cv.getAsString(PlanningTable.COLUMN_PROXIMITY_REQUIRED));
                                inserter.bind(inserter.getColumnIndex(PlanningTable.COLUMN_DESCRIPTION), cv.getAsString(PlanningTable.COLUMN_DESCRIPTION));
                                inserter.bind(inserter.getColumnIndex(PlanningTable.COLUMN_INSTRUCTIONS), cv.getAsString(PlanningTable.COLUMN_INSTRUCTIONS));
                                inserter.bind(inserter.getColumnIndex(PlanningTable.COLUMN_PHONE), cv.getAsString(PlanningTable.COLUMN_PHONE));
                                inserter.bind(inserter.getColumnIndex(PlanningTable.COLUMN_STREET), cv.getAsString(PlanningTable.COLUMN_STREET));
                                inserter.bind(inserter.getColumnIndex(PlanningTable.COLUMN_POSTAL_CODE), cv.getAsString(PlanningTable.COLUMN_POSTAL_CODE));
                                inserter.bind(inserter.getColumnIndex(PlanningTable.COLUMN_CITY), cv.getAsString(PlanningTable.COLUMN_CITY));
                                inserter.bind(inserter.getColumnIndex(PlanningTable.COLUMN_LATITUDE), cv.getAsDouble(PlanningTable.COLUMN_LATITUDE));
                                inserter.bind(inserter.getColumnIndex(PlanningTable.COLUMN_LONGITUDE), cv.getAsString(PlanningTable.COLUMN_LONGITUDE));

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
        }

        return rowsInserted;
    }
}
