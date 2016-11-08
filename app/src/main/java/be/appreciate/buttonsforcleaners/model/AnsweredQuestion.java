package be.appreciate.buttonsforcleaners.model;

import android.database.Cursor;

import java.util.ArrayList;
import java.util.List;

import be.appreciate.buttonsforcleaners.database.AnsweredQuestionTable;
import be.appreciate.buttonsforcleaners.database.QuestionTable;
import be.appreciate.buttonsforcleaners.utils.TextUtils;

/**
 * Created by Inneke De Clippel on 17/03/2016.
 */
public class AnsweredQuestion
{
    private int questionId;
    private List<String> answers;
    private QuestionType type;

    public static List<AnsweredQuestion> constructListFromCursor(Cursor cursor)
    {
        List<AnsweredQuestion> answers = new ArrayList<>();

        if(cursor != null && cursor.moveToFirst())
        {
            do
            {
                answers.add(AnsweredQuestion.constructFromCursor(cursor));
            }
            while (cursor.moveToNext());
        }

        return answers;
    }

    public static AnsweredQuestion constructFromCursor(Cursor cursor)
    {
        AnsweredQuestion answer = new AnsweredQuestion();

        answer.questionId = cursor.getInt(cursor.getColumnIndex(AnsweredQuestionTable.COLUMN_QUESTION_ID_FULL));
        answer.answers = TextUtils.split(cursor.getString(cursor.getColumnIndex(AnsweredQuestionTable.COLUMN_ANSWERS_FULL)));
        answer.type = QuestionType.getQuestionType(cursor.getInt(cursor.getColumnIndex(QuestionTable.COLUMN_TYPE_FULL)));

        return answer;
    }

    public int getQuestionId()
    {
        return questionId;
    }

    public List<String> getAnswers()
    {
        return answers;
    }

    public QuestionType getType()
    {
        return type;
    }
}
