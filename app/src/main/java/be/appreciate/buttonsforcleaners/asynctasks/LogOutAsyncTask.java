package be.appreciate.buttonsforcleaners.asynctasks;

import android.content.ContentResolver;
import android.content.Context;
import android.os.AsyncTask;

import java.lang.ref.WeakReference;

import be.appreciate.buttonsforcleaners.contentproviders.AnsweredQuestionContentProvider;
import be.appreciate.buttonsforcleaners.contentproviders.ContractTypeContentProvider;
import be.appreciate.buttonsforcleaners.contentproviders.FeedbackContentProvider;
import be.appreciate.buttonsforcleaners.contentproviders.PlanningContentProvider;
import be.appreciate.buttonsforcleaners.contentproviders.PlanningProductContentProvider;
import be.appreciate.buttonsforcleaners.contentproviders.PlanningResourceContentProvider;
import be.appreciate.buttonsforcleaners.contentproviders.ProductContentProvider;
import be.appreciate.buttonsforcleaners.contentproviders.QuestionContentProvider;
import be.appreciate.buttonsforcleaners.utils.PreferencesHelper;

/**
 * Created by Inneke De Clippel on 24/03/2016.
 */
public class LogOutAsyncTask extends AsyncTask<Void, Void, Void>
{
    private WeakReference<Context> context;
    private WeakReference<LogOutAsyncTaskListener> listener;

    public LogOutAsyncTask(Context context)
    {
        this.context = new WeakReference<>(context);
    }

    public void setListener(LogOutAsyncTaskListener listener)
    {
        this.listener = new WeakReference<>(listener);
    }

    @Override
    protected Void doInBackground(Void... params)
    {
        ContentResolver contentResolver = this.context != null && this.context.get() != null ? this.context.get().getContentResolver() : null;

        if(contentResolver != null)
        {
            contentResolver.delete(AnsweredQuestionContentProvider.CONTENT_URI, null, null);
            contentResolver.delete(ContractTypeContentProvider.CONTENT_URI, null, null);
            contentResolver.delete(FeedbackContentProvider.CONTENT_URI, null, null);
            contentResolver.delete(PlanningContentProvider.CONTENT_URI, null, null);
            contentResolver.delete(PlanningProductContentProvider.CONTENT_URI, null, null);
            contentResolver.delete(PlanningResourceContentProvider.CONTENT_URI, null, null);
            contentResolver.delete(ProductContentProvider.CONTENT_URI, null, null);
            contentResolver.delete(QuestionContentProvider.CONTENT_URI, null, null);
        }

        Context context = this.context != null && this.context.get() != null ? this.context.get() : null;

        if(context != null)
        {
            PreferencesHelper.clearUser(context);
        }

        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid)
    {
        super.onPostExecute(aVoid);

        if(this.listener != null && this.listener.get() != null)
        {
            this.listener.get().onDataCleared();
        }
    }

    public interface LogOutAsyncTaskListener
    {
        void onDataCleared();
    }
}
