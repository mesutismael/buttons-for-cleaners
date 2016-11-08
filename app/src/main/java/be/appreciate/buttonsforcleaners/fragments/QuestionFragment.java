package be.appreciate.buttonsforcleaners.fragments;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.View;

import java.util.List;

import be.appreciate.buttonsforcleaners.contentproviders.QuestionContentProvider;
import be.appreciate.buttonsforcleaners.database.QuestionTable;
import be.appreciate.buttonsforcleaners.model.Question;
import be.appreciate.buttonsforcleaners.utils.Constants;

/**
 * Created by Inneke De Clippel on 11/03/2016.
 */
public abstract class QuestionFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>
{
    protected int questionId;
    protected int planningId;
    protected int currentQuestion;
    protected int totalQuestions;
    protected Question question;

    private static final String KEY_QUESTION_ID = "question_id";
    private static final String KEY_PLANNING_ID = "planning_id";
    private static final String KEY_CURRENT_QUESTION = "current_question";
    private static final String KEY_TOTAL_QUESTIONS = "total_questions";

    public static Bundle createBundle(int questionId, int planningId, int currentQuestion, int totalQuestions)
    {
        Bundle bundle = new Bundle();
        bundle.putInt(KEY_QUESTION_ID, questionId);
        bundle.putInt(KEY_PLANNING_ID, planningId);
        bundle.putInt(KEY_CURRENT_QUESTION, currentQuestion);
        bundle.putInt(KEY_TOTAL_QUESTIONS, totalQuestions);
        return bundle;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        this.questionId = this.getArguments().getInt(KEY_QUESTION_ID);
        this.planningId = this.getArguments().getInt(KEY_PLANNING_ID);
        this.currentQuestion = this.getArguments().getInt(KEY_CURRENT_QUESTION);
        this.totalQuestions = this.getArguments().getInt(KEY_TOTAL_QUESTIONS);

        this.getLoaderManager().initLoader(Constants.LOADER_QUESTIONS, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args)
    {
        switch (id)
        {
            case Constants.LOADER_QUESTIONS:
                Uri uri = Uri.withAppendedPath(QuestionContentProvider.CONTENT_URI_ANSWER, String.valueOf(this.planningId));
                String where = QuestionTable.TABLE_NAME + "." + QuestionTable.COLUMN_QUESTION_ID + " = " + this.questionId;
                return new CursorLoader(this.getContext(), uri, null, where, null, null);

            default:
                return null;
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data)
    {
        switch (loader.getId())
        {
            case Constants.LOADER_QUESTIONS:
                if(data != null && data.moveToFirst())
                {
                    this.question = Question.constructFromCursor(data, true);
                    this.updateLayout();
                }
                break;
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader)
    {
    }

    protected abstract void updateLayout();

    public abstract boolean isAnswerRequired();

    public abstract List<String> getEnteredAnswers();

    public List<String> getPreviousAnswers()
    {
        return this.question != null ? this.question.getEnteredAnswers() : null;
    }

    public int getQuestionId()
    {
        return this.questionId;
    }
}
