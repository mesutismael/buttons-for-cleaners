package be.appreciate.buttonsforcleaners.utils;

import android.content.Context;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import be.appreciate.buttonsforcleaners.R;

/**
 * Created by Inneke De Clippel on 3/03/2016.
 */
public class TextUtils
{
    private static final String DELIMITER = "//---//";
    private static final String BOOLEAN_TRUE = "true";
    private static final String BOOLEAN_FALSE = "false";

    public static String join(List<String> list)
    {
        if(list == null)
        {
            return null;
        }

        return android.text.TextUtils.join(DELIMITER, list);
    }

    public static List<String> split(String string)
    {
        if(string == null)
        {
            return null;
        }

        String[] array = android.text.TextUtils.split(string, DELIMITER);

        return new ArrayList<>(Arrays.asList(array));
    }

    public static boolean equals(List<String> list1, List<String> list2)
    {
        return list1 != null ? list1.equals(list2) : list2 == null;
    }

    public static List<String> convertBooleanAnswers(Context context, List<String> answers, boolean fromBoolean)
    {
        if(answers == null)
        {
            return null;
        }

        String answerTrue = context.getString(R.string.feedback_boolean_true);
        String answerFalse = context.getString(R.string.feedback_boolean_false);
        List<String> convertedAnswers = new ArrayList<>();

        if(fromBoolean)
        {
            for(String answer : answers)
            {
                convertedAnswers.add(BOOLEAN_TRUE.equals(answer) ? answerTrue : answerFalse);
            }
        }
        else
        {
            for(String answer : answers)
            {
                convertedAnswers.add(answerTrue.equals(answer) ? BOOLEAN_TRUE : BOOLEAN_FALSE);
            }
        }

        return convertedAnswers;
    }

    public static List<String> convertEnteredAnswers(List<String> answers)
    {
        List<String> convertedAnswers = new ArrayList<>();

        if(answers != null)
        {
            for(String answer : answers)
            {
                if(answer != null)
                {
                    convertedAnswers.add(answer);
                }
            }
        }

        if(convertedAnswers.size() == 0)
        {
            convertedAnswers.add("");
        }

        return convertedAnswers;
    }

    public static String subString(String text, int count)
    {
        if(text == null)
        {
            text = "";
        }

        if(text.length() > count)
        {
            return text.substring(0, count);
        }

        return text;
    }

    public static int getIntegerFromEditText(EditText editText)
    {
        if(editText == null)
        {
            return 0;
        }

        try
        {
            return Integer.parseInt(editText.getText().toString());
        }
        catch (NumberFormatException e)
        {
            return 0;
        }
    }
}
