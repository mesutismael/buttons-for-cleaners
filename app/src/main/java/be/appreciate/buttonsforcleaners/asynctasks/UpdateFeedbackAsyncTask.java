package be.appreciate.buttonsforcleaners.asynctasks;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.os.AsyncTask;

import java.lang.ref.WeakReference;

import be.appreciate.buttonsforcleaners.contentproviders.AnsweredQuestionContentProvider;
import be.appreciate.buttonsforcleaners.contentproviders.FeedbackContentProvider;
import be.appreciate.buttonsforcleaners.database.AnsweredQuestionTable;
import be.appreciate.buttonsforcleaners.database.FeedbackTable;
import be.appreciate.buttonsforcleaners.database.QuestionTable;
import be.appreciate.buttonsforcleaners.model.PointOfTime;

/**
 * Created by Inneke De Clippel on 25/03/2016.
 */
public class UpdateFeedbackAsyncTask extends AsyncTask<Void, Void, Void>
{
    private ContentResolver contentResolver;
    private int planningId;
    private int contractTypeId;
    private PointOfTime pointOfTime;
    private boolean beforeQuestions;
    private boolean hasQuestions;
    private WeakReference<UpdateAsyncTaskListener> listener;

    public UpdateFeedbackAsyncTask(Context context, int planningId, int contractTypeId, PointOfTime pointOfTime, boolean beforeQuestions, boolean hasQuestions)
    {
        this.contentResolver = context.getContentResolver();
        this.planningId = planningId;
        this.contractTypeId = contractTypeId;
        this.pointOfTime = pointOfTime;
        this.beforeQuestions = beforeQuestions;
        this.hasQuestions = hasQuestions;
    }

    public void setListener(UpdateAsyncTaskListener listener)
    {
        if(listener != null)
        {
            this.listener = new WeakReference<>(listener);
        }
    }

    @Override
    protected Void doInBackground(Void... params)
    {
        switch (this.pointOfTime)
        {
            case START:
                if(!this.hasQuestions)
                {
                    //The user pressed start and there are no questions: feedback is completed
                    ContentValues cv = new ContentValues();
                    cv.put(FeedbackTable.COLUMN_PLANNING_ID, this.planningId);
                    cv.put(FeedbackTable.COLUMN_START_TIME, System.currentTimeMillis());
                    cv.put(FeedbackTable.COLUMN_START_COMPLETED, true);
                    cv.put(FeedbackTable.COLUMN_DISTANCE_TRAVELED, 0);
                    this.updateFeedback(cv, true);
                }
                else if(this.beforeQuestions)
                {
                    //The user pressed start and there are questions: feedback is not completed
                    ContentValues cv = new ContentValues();
                    cv.put(FeedbackTable.COLUMN_PLANNING_ID, this.planningId);
                    cv.put(FeedbackTable.COLUMN_START_TIME, System.currentTimeMillis());
                    cv.put(FeedbackTable.COLUMN_DISTANCE_TRAVELED, 0);
                    this.updateFeedback(cv, true);

                    this.deletePreviousAnswers();
                }
                else
                {
                    //The user pressed finish at the end of the feedback: feedback is completed
                    ContentValues cv = new ContentValues();
                    cv.put(FeedbackTable.COLUMN_START_COMPLETED, true);
                    this.updateFeedback(cv, false);
                }
                break;

            case STOP:
                if(!this.hasQuestions)
                {
                    //The user pressed stop and there are no questions: feedback is completed
                    ContentValues cv = new ContentValues();
                    cv.put(FeedbackTable.COLUMN_END_TIME, System.currentTimeMillis());
                    cv.put(FeedbackTable.COLUMN_END_COMPLETED, true);
                    this.updateFeedback(cv, false);
                }
                else if(this.beforeQuestions)
                {
                    //The user pressed stop and there are questions: feedback is not completed
                    this.deletePreviousAnswers();
                }
                else
                {
                    //The user pressed finish at the end of the feedback: feedback is completed
                    ContentValues cv = new ContentValues();
                    cv.put(FeedbackTable.COLUMN_END_TIME, System.currentTimeMillis());
                    cv.put(FeedbackTable.COLUMN_END_COMPLETED, true);
                    this.updateFeedback(cv, false);
                }
                break;
        }

        return null;
    }

    private void deletePreviousAnswers()
    {
        String innerQuery = "SELECT " + QuestionTable.COLUMN_QUESTION_ID
                + " FROM " + QuestionTable.TABLE_NAME
                + " WHERE " + QuestionTable.COLUMN_CONTRACT_TYPE_ID + " = " + this.contractTypeId
                + " AND " + QuestionTable.COLUMN_POINT_OF_TIME + " = " + this.pointOfTime.getPointOfTimeId();
        String selection = AnsweredQuestionTable.COLUMN_PLANNING_ID + " = " + this.planningId
                + " AND " + AnsweredQuestionTable.COLUMN_QUESTION_ID + " IN (" + innerQuery + ")";

        this.contentResolver.delete(AnsweredQuestionContentProvider.CONTENT_URI, selection, null);
    }

    private void updateFeedback(ContentValues cv, boolean insertIfNotUpdated)
    {
        String selection = FeedbackTable.COLUMN_PLANNING_ID + " = " + this.planningId;
        int updatedRows = this.contentResolver.update(FeedbackContentProvider.CONTENT_URI, cv, selection, null);

        if(insertIfNotUpdated && updatedRows == 0)
        {
            this.contentResolver.insert(FeedbackContentProvider.CONTENT_URI, cv);
        }
    }

    @Override
    protected void onPostExecute(Void aVoid)
    {
        super.onPostExecute(aVoid);

        if(this.listener != null && this.listener.get() != null)
        {
            this.listener.get().onFeedbackUpdated();
        }
    }

    public interface UpdateAsyncTaskListener
    {
        void onFeedbackUpdated();
    }
}
