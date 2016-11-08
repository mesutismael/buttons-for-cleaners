package be.appreciate.buttonsforcleaners.model.api;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Inneke De Clippel on 1/03/2016.
 */
public class FeedbackRequest
{
    @SerializedName("PlanningId")
    private int planningId;
    @SerializedName("RealStartTime")
    private String startTime;
    @SerializedName("RealEndTime")
    private String endTime;
    @SerializedName("ExtraFieldValues")
    private List<AnsweredQuestion> answeredQuestions;
    @SerializedName("TraveledKms")
    private double distanceTraveled;

    public FeedbackRequest(int planningId, String startTime, String endTime, List<AnsweredQuestion> answeredQuestions, int distanceTraveled)
    {
        this.planningId = planningId;
        this.startTime = startTime;
        this.endTime = endTime;
        this.answeredQuestions = answeredQuestions;
        this.distanceTraveled = distanceTraveled * 0.001;
    }
}
