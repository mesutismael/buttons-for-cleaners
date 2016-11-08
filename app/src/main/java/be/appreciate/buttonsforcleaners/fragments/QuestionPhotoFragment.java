package be.appreciate.buttonsforcleaners.fragments;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.List;

import be.appreciate.buttonsforcleaners.R;
import be.appreciate.buttonsforcleaners.adapters.QuestionPhotoAdapter;
import be.appreciate.buttonsforcleaners.decorations.PaddingDecoration;
import be.appreciate.buttonsforcleaners.utils.ImageUtils;

/**
 * Created by Inneke De Clippel on 14/03/2016.
 */
public class QuestionPhotoFragment extends QuestionFragment implements QuestionPhotoAdapter.OnItemClickListener
{
    private TextView textViewProgress;
    private TextView textViewQuestion;
    private QuestionPhotoAdapter adapter;
    private File imageFile;

    private static final int REQUEST_TAKE_PHOTO = 1;

    public static QuestionPhotoFragment newInstance(int questionId, int planningId, int currentQuestion, int totalQuestions)
    {
        QuestionPhotoFragment fragment = new QuestionPhotoFragment();
        fragment.setArguments(QuestionFragment.createBundle(questionId, planningId, currentQuestion, totalQuestions));
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_question_photo, container, false);

        this.textViewProgress = (TextView) view.findViewById(R.id.textView_progress);
        this.textViewQuestion = (TextView) view.findViewById(R.id.textView_question);
        RecyclerView recyclerViewPhotos = (RecyclerView) view.findViewById(R.id.recyclerView_photos);

        int orientation = this.getResources().getBoolean(R.bool.tablet) ? LinearLayoutManager.HORIZONTAL : LinearLayoutManager.VERTICAL;
        recyclerViewPhotos.setLayoutManager(new LinearLayoutManager(view.getContext(), orientation, false));
        this.adapter = new QuestionPhotoAdapter();
        this.adapter.setListener(this);
        recyclerViewPhotos.setAdapter(this.adapter);
        PaddingDecoration decoration = new PaddingDecoration(view.getContext());
        recyclerViewPhotos.addItemDecoration(decoration);

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        this.textViewProgress.setText(this.getString(R.string.feedback_progress, this.currentQuestion, this.totalQuestions));
    }

    @Override
    protected void updateLayout()
    {
        if(this.question != null)
        {
            List<String> enteredAnswers = this.question.getEnteredAnswers();

            this.textViewQuestion.setText(this.question.getQuestion());
            this.adapter.setPhotos(enteredAnswers);
        }
    }

    @Override
    public List<String> getEnteredAnswers()
    {
        return this.adapter.getPhotos();
    }

    @Override
    public boolean isAnswerRequired()
    {
        return false;
    }

    @Override
    public void onAddClicked(View caller)
    {
        this.imageFile = ImageUtils.createFeedbackImageFile(caller.getContext(), this.planningId, this.questionId);

        if(this.imageFile != null)
        {
            Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(this.imageFile));

            try
            {
                this.startActivityForResult(cameraIntent, REQUEST_TAKE_PHOTO);
            }
            catch (ActivityNotFoundException e)
            {
                Toast.makeText(caller.getContext(), R.string.feedback_photo_error_no_app, Toast.LENGTH_LONG).show();
            }
        }
        else
        {
            Toast.makeText(caller.getContext(), R.string.feedback_photo_error_storage, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onDeleteClicked(View caller, String photoPath)
    {
        this.adapter.removePhoto(photoPath);

        File imageFile = new File(photoPath);
        if(imageFile.exists())
        {
            imageFile.delete();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        switch (requestCode)
        {
            case REQUEST_TAKE_PHOTO:
                //Check if the data is not null because the resultCode is not always Activity.RESULT_OK
                if((resultCode == Activity.RESULT_OK || data != null) && this.imageFile.exists())
                {
                    this.adapter.addPhoto(this.imageFile.getAbsolutePath());
                }
                break;
        }
    }
}
