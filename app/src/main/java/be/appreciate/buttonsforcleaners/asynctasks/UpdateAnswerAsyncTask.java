package be.appreciate.buttonsforcleaners.asynctasks;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.os.AsyncTask;

import java.util.List;

import be.appreciate.buttonsforcleaners.contentproviders.AnsweredQuestionContentProvider;
import be.appreciate.buttonsforcleaners.database.AnsweredQuestionTable;
import be.appreciate.buttonsforcleaners.utils.TextUtils;

/**
 * Created by Inneke De Clippel on 11/03/2016.
 */
public class UpdateAnswerAsyncTask extends AsyncTask<Void, Void, Void>
{
    private ContentResolver contentResolver;
    private int planningId;
    private int questionId;
    private List<String> answers;

    public UpdateAnswerAsyncTask(Context context, int planningId, int questionId, List<String> answers)
    {
        this.contentResolver = context.getContentResolver();
        this.planningId = planningId;
        this.questionId = questionId;
        this.answers = answers;
    }

    @Override
    protected Void doInBackground(Void... params)
    {
        ContentValues cv = new ContentValues();
        cv.put(AnsweredQuestionTable.COLUMN_PLANNING_ID, this.planningId);
        cv.put(AnsweredQuestionTable.COLUMN_QUESTION_ID, this.questionId);
        cv.put(AnsweredQuestionTable.COLUMN_ANSWERS, TextUtils.join(this.answers));

        String where = AnsweredQuestionTable.COLUMN_PLANNING_ID + " = " + this.planningId
                + " AND " + AnsweredQuestionTable.COLUMN_QUESTION_ID + " = " + this.questionId;
        int updatedRows = this.contentResolver.update(AnsweredQuestionContentProvider.CONTENT_URI, cv, where, null);

        if(updatedRows == 0)
        {
            this.contentResolver.insert(AnsweredQuestionContentProvider.CONTENT_URI, cv);
        }

        return null;
    }
}
