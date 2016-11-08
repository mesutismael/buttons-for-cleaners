package be.appreciate.buttonsforcleaners.model.api;

import android.content.ContentValues;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import be.appreciate.buttonsforcleaners.database.ContractTypeTable;

/**
 * Created by Inneke De Clippel on 1/03/2016.
 */
public class ContractType
{
    @SerializedName("Id")
    private int id;
    @SerializedName("Name")
    private String name;
    @SerializedName("Definitions")
    private List<Question> questions;

    public ContentValues getContentValues()
    {
        ContentValues cv = new ContentValues();

        cv.put(ContractTypeTable.COLUMN_CONTRACT_TYPE_ID, this.id);
        cv.put(ContractTypeTable.COLUMN_NAME, this.name);

        return cv;
    }

    public ContentValues[] getQuestionContentValues()
    {
        ContentValues[] cvArray = new ContentValues[this.questions != null ? this.questions.size() : 0];

        if(this.questions != null)
        {
            for(int i = 0; i < this.questions.size(); i++)
            {
                Question question = this.questions.get(i);
                cvArray[i] = question.getContentValues(this.id);
            }
        }

        return cvArray;
    }
}
