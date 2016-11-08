package be.appreciate.buttonsforcleaners.model.api;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Inneke De Clippel on 1/03/2016.
 */
public class AnsweredQuestion
{
    @SerializedName("FieldDefinitionId")
    private int questionId;
    @SerializedName("Values")
    private List<String> answers;

    public AnsweredQuestion(int questionId, List<String> answers)
    {
        this.questionId = questionId;
        this.answers = answers;
    }
}
