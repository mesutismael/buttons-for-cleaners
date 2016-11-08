package be.appreciate.buttonsforcleaners.asynctasks;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.os.AsyncTask;

import be.appreciate.buttonsforcleaners.contentproviders.FeedbackContentProvider;
import be.appreciate.buttonsforcleaners.database.FeedbackTable;

/**
 * Created by Inneke De Clippel on 25/03/2016.
 */
public class UpdateDistanceAsyncTask extends AsyncTask<Void, Void, Void>
{
    private ContentResolver contentResolver;
    private int planningId;
    private int distance;

    public UpdateDistanceAsyncTask(Context context, int planningId, int distance)
    {
        this.contentResolver = context.getContentResolver();
        this.planningId = planningId;
        this.distance = distance;
    }

    @Override
    protected Void doInBackground(Void... params)
    {
        ContentValues cv = new ContentValues();
        cv.put(FeedbackTable.COLUMN_DISTANCE_TRAVELED, this.distance);

        String where = FeedbackTable.COLUMN_PLANNING_ID + " = " + this.planningId
                + " AND " + FeedbackTable.COLUMN_DISTANCE_TRAVELED + " < " + this.distance;

        this.contentResolver.update(FeedbackContentProvider.CONTENT_URI_DISTANCE, cv, where, null);

        return null;
    }
}
