package be.appreciate.buttonsforcleaners.services;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.v4.content.ContextCompat;
import android.util.Base64;
import android.util.Base64OutputStream;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import be.appreciate.buttonsforcleaners.R;
import be.appreciate.buttonsforcleaners.api.ApiHelper;
import be.appreciate.buttonsforcleaners.contentproviders.AnsweredQuestionContentProvider;
import be.appreciate.buttonsforcleaners.contentproviders.FeedbackContentProvider;
import be.appreciate.buttonsforcleaners.database.AnsweredQuestionTable;
import be.appreciate.buttonsforcleaners.database.FeedbackTable;
import be.appreciate.buttonsforcleaners.model.AnsweredQuestion;
import be.appreciate.buttonsforcleaners.model.Feedback;
import be.appreciate.buttonsforcleaners.model.api.FeedbackRequest;
import be.appreciate.buttonsforcleaners.model.api.FeedbackResponse;
import be.appreciate.buttonsforcleaners.utils.Constants;
import be.appreciate.buttonsforcleaners.utils.DateUtils;
import be.appreciate.buttonsforcleaners.utils.SerializablePath;
import be.appreciate.buttonsforcleaners.utils.TextUtils;
import retrofit.RetrofitError;

/**
 * Created by Inneke De Clippel on 17/03/2016.
 */
public class UploadService extends IntentService
{
    public static boolean RUNNING = false;
    private static final String EXTRA_PLANNING_ID = "planning_id";

    public static Intent getIntent(Context context)
    {
        return new Intent(context, UploadService.class);
    }

    public static Intent getIntent(Context context, int planningId)
    {
        Intent intent = new Intent(context, UploadService.class);
        intent.putExtra(EXTRA_PLANNING_ID, planningId);
        return intent;
    }

    public UploadService()
    {
        super("UploadService");
    }

    public UploadService(String name)
    {
        super(name);
    }

    @Override
    protected void onHandleIntent(Intent intent)
    {
        RUNNING = true;

        int selectedPlanningId = intent.getIntExtra(EXTRA_PLANNING_ID, -1);

        List<Feedback> feedback = this.getFeedback(selectedPlanningId);

        for(Feedback feedbackItem : feedback)
        {
            int planningId = feedbackItem.getPlanningId();
            String startTime = DateUtils.formatApiDate(feedbackItem.getStartTime());
            String endTime = DateUtils.formatApiDate(feedbackItem.getEndTime());
            int distanceTraveled = feedbackItem.getDistanceTraveled();
            List<AnsweredQuestion> answers = this.getAnswers(planningId);
            List<be.appreciate.buttonsforcleaners.model.api.AnsweredQuestion> convertedAnswers = this.convertAnswers(answers);
            FeedbackRequest request = new FeedbackRequest(planningId, startTime, endTime, convertedAnswers, distanceTraveled);

            try
            {
                FeedbackResponse response = ApiHelper.postFeedbackBlocking(this, request);

                if(response != null && (response.isSuccess() || response.isAlreadyExecuted()))
                {
                    this.setFeedbackSuccessful(planningId);
                }
            }
            catch (RetrofitError e)
            {
                //Something went wrong while executing the POST
            }
        }

        RUNNING = false;
    }

    private List<Feedback> getFeedback(int planningId)
    {
        String selection = FeedbackTable.TABLE_NAME + "." + FeedbackTable.COLUMN_SENT_TO_API + " = 0"
                + " AND " + FeedbackTable.TABLE_NAME + "." + FeedbackTable.COLUMN_END_COMPLETED + " = 1"
                + (planningId >= 0 ? " AND " + FeedbackTable.TABLE_NAME + "." + FeedbackTable.COLUMN_PLANNING_ID + " = " + planningId : "");

        Cursor cursor = this.getContentResolver().query(FeedbackContentProvider.CONTENT_URI, null, selection, null, null);

        List<Feedback> feedback = Feedback.constructListFromCursor(cursor);

        if(cursor != null)
        {
            cursor.close();
        }

        return feedback;
    }

    private List<AnsweredQuestion> getAnswers(int planningId)
    {
        String selection = AnsweredQuestionTable.TABLE_NAME + "." + AnsweredQuestionTable.COLUMN_PLANNING_ID + " = " + planningId;

        Cursor cursor = this.getContentResolver().query(AnsweredQuestionContentProvider.CONTENT_URI_EXTRA, null, selection, null, null);

        List<AnsweredQuestion> answers = AnsweredQuestion.constructListFromCursor(cursor);

        if(cursor != null)
        {
            cursor.close();
        }

        return answers;
    }

    private List<be.appreciate.buttonsforcleaners.model.api.AnsweredQuestion> convertAnswers(List<AnsweredQuestion> answers)
    {
        List<be.appreciate.buttonsforcleaners.model.api.AnsweredQuestion> convertedAnswers = new ArrayList<>();

        for(AnsweredQuestion answer : answers)
        {
            int questionId = answer.getQuestionId();
            List<String> textAnswers;

            switch (answer.getType())
            {
                case TEXT_SHORT:
                    textAnswers = this.convertShortTextAnswer(answer.getAnswers());
                    break;

                case TEXT_LONG:
                    textAnswers = this.convertLongTextAnswer(answer.getAnswers());
                    break;

                case BOOLEAN:
                case SINGLE_CHOICE:
                case MULTI_CHOICE:
                    textAnswers = this.convertChoiceAnswer(answer.getAnswers());
                    break;

                case PHOTOS:
                    textAnswers = this.convertPhotoAnswer(answer.getAnswers());
                    break;

                case DRAWING:
                    textAnswers = this.convertDrawingAnswer(answer.getAnswers());
                    break;

                case PRODUCT_PICKER:
                    textAnswers = this.convertProductAnswer(answer.getAnswers());
                    break;

                default:
                    textAnswers = new ArrayList<>();
            }

            convertedAnswers.add(new be.appreciate.buttonsforcleaners.model.api.AnsweredQuestion(questionId, textAnswers));
        }

        return convertedAnswers;
    }

    private List<String> convertShortTextAnswer(List<String> answers)
    {
        List<String> convertedAnswers = new ArrayList<>();

        if(answers != null)
        {
            for(String answer : answers)
            {
                convertedAnswers.add(TextUtils.subString(answer, Constants.ANSWER_TEXT_SHORT_MAX_LENGTH));
            }
        }

        return convertedAnswers;
    }

    private List<String> convertLongTextAnswer(List<String> answers)
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

        return convertedAnswers;
    }

    private List<String> convertChoiceAnswer(List<String> answers)
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

        return convertedAnswers;
    }

    private List<String> convertPhotoAnswer(List<String> answers)
    {
        List<String> convertedAnswers = new ArrayList<>();

        if(answers != null)
        {
            for(String answer : answers)
            {
                if(answer != null)
                {
                    String base64 = this.convertFileToBase64(answer);
                    if(base64 != null)
                    {
                        convertedAnswers.add(base64);
                    }
                }
            }
        }

        return convertedAnswers;
    }

    private List<String> convertDrawingAnswer(List<String> answers)
    {
        List<String> convertedAnswers = new ArrayList<>();

        if(answers != null)
        {
            for(String answer : answers)
            {
                if(answer != null)
                {
                    String base64 = this.convertPathToBase64(answer);
                    if(base64 != null)
                    {
                        convertedAnswers.add(base64);
                    }
                }
            }
        }

        return convertedAnswers;
    }

    private List<String> convertProductAnswer(List<String> answers)
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

        return convertedAnswers;
    }

    private String convertFileToBase64(String absolutePath)
    {
        InputStream inputStream = null;
        ByteArrayOutputStream outputStream = null;
        Base64OutputStream output64 = null;
        String base64;

        try
        {
            inputStream = new FileInputStream(absolutePath);
            outputStream = new ByteArrayOutputStream();
            output64 = new Base64OutputStream(outputStream, Base64.DEFAULT);

            byte[] buffer = new byte[8192];
            int bytesRead;

            while ((bytesRead = inputStream.read(buffer)) != -1)
            {
                output64.write(buffer, 0, bytesRead);
            }

            base64 = outputStream.toString();
        }
        catch (IOException e)
        {
            base64 = null;
        }
        finally
        {
            if(inputStream != null)
            {
                try
                {
                    inputStream.close();
                }
                catch (IOException e)
                {
                }
            }

            if(outputStream != null)
            {
                try
                {
                    outputStream.close();
                }
                catch (IOException e)
                {
                }
            }

            if(output64 != null)
            {
                try
                {
                    output64.close();
                }
                catch (IOException e)
                {
                }
            }
        }

        return base64;
    }

    private String convertPathToBase64(String pathAsString)
    {
        SerializablePath path = new SerializablePath();
        path.setPath(pathAsString);

        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(ContextCompat.getColor(this, R.color.feedback_signature_line));
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStrokeWidth(4f);

        RectF bounds = new RectF();
        path.computeBounds(bounds, true);
        int width = (int) bounds.right;
        int height = (int) bounds.bottom;

        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);

        Canvas canvas = new Canvas(bitmap);
        canvas.drawColor(Color.WHITE);
        canvas.drawPath(path, paint);

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, byteArrayOutputStream);
        byte[] bytes = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(bytes, Base64.DEFAULT);
    }

    private void setFeedbackSuccessful(int planningId)
    {
        ContentValues cv = new ContentValues();
        cv.put(FeedbackTable.COLUMN_SENT_TO_API, true);

        String selection = FeedbackTable.COLUMN_PLANNING_ID + " = " + planningId;

        this.getContentResolver().update(FeedbackContentProvider.CONTENT_URI, cv, selection, null);
    }
}
