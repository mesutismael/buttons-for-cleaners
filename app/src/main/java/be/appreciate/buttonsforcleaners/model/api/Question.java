package be.appreciate.buttonsforcleaners.model.api;

import android.content.ContentValues;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import be.appreciate.buttonsforcleaners.database.QuestionTable;
import be.appreciate.buttonsforcleaners.utils.TextUtils;

/**
 * Created by Inneke De Clippel on 1/03/2016.
 */
public class Question
{
    @SerializedName("Id")
    private int id;
    @SerializedName("Label")
    private String label;
    @SerializedName("FieldType")
    private int questionType;
    @SerializedName("PossibleValues")
    private List<String> answers;
    @SerializedName("Location")
    private int pointOfTime;

    public ContentValues getContentValues(int contractTypeId)
    {
        ContentValues cv = new ContentValues();

        cv.put(QuestionTable.COLUMN_QUESTION_ID, this.id);
        cv.put(QuestionTable.COLUMN_LABEL, this.label);
        cv.put(QuestionTable.COLUMN_TYPE, this.questionType);
        cv.put(QuestionTable.COLUMN_ANSWERS, TextUtils.join(this.answers));
        cv.put(QuestionTable.COLUMN_CONTRACT_TYPE_ID, contractTypeId);
        cv.put(QuestionTable.COLUMN_POINT_OF_TIME, this.pointOfTime);

        return cv;
    }
}
