package be.appreciate.buttonsforcleaners.model;

import android.database.Cursor;

import java.util.ArrayList;
import java.util.List;

import be.appreciate.buttonsforcleaners.database.AnsweredQuestionTable;
import be.appreciate.buttonsforcleaners.database.QuestionTable;
import be.appreciate.buttonsforcleaners.utils.TextUtils;

/**
 * Created by Inneke De Clippel on 10/03/2016.
 */
public class Question
{
    private int id;
    private String question;
    private List<String> answers;
    private QuestionType type;
    private List<String> enteredAnswers;

    public static List<Question> constructListFromCursor(Cursor cursor)
    {
        List<Question> questions = new ArrayList<>();

        if(cursor != null && cursor.moveToFirst())
        {
            do
            {
                questions.add(Question.constructFromCursor(cursor, false));
            }
            while (cursor.moveToNext());
        }

        return questions;
    }

    public static Question constructFromCursor(Cursor cursor, boolean withAnswers)
    {
        Question question = new Question();

        question.id = cursor.getInt(cursor.getColumnIndex(QuestionTable.COLUMN_QUESTION_ID_FULL));
        question.question = cursor.getString(cursor.getColumnIndex(QuestionTable.COLUMN_LABEL_FULL));
        question.answers = TextUtils.split(cursor.getString(cursor.getColumnIndex(QuestionTable.COLUMN_ANSWERS_FULL)));
        question.type = QuestionType.getQuestionType(cursor.getInt(cursor.getColumnIndex(QuestionTable.COLUMN_TYPE_FULL)));

        if(withAnswers)
        {
            question.enteredAnswers = TextUtils.split(cursor.getString(cursor.getColumnIndex(AnsweredQuestionTable.COLUMN_ANSWERS_FULL)));
        }

        return question;
    }

    public int getId()
    {
        return id;
    }

    public String getQuestion()
    {
        return question;
    }

    public List<String> getAnswers()
    {
        return answers;
    }

    public QuestionType getType()
    {
        return type;
    }

    public List<String> getEnteredAnswers()
    {
        return enteredAnswers;
    }
}
