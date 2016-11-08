package be.appreciate.buttonsforcleaners.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import be.appreciate.buttonsforcleaners.R;
import be.appreciate.buttonsforcleaners.utils.ImageUtils;
import be.appreciate.buttonsforcleaners.views.SignatureView;

/**
 * Created by Inneke De Clippel on 15/03/2016.
 */
public class QuestionSignatureFragment extends QuestionFragment implements View.OnClickListener
{
    private TextView textViewProgress;
    private TextView textViewQuestion;
    private SignatureView signatureView;

    public static QuestionSignatureFragment newInstance(int questionId, int planningId, int currentQuestion, int totalQuestions)
    {
        QuestionSignatureFragment fragment = new QuestionSignatureFragment();
        fragment.setArguments(QuestionFragment.createBundle(questionId, planningId, currentQuestion, totalQuestions));
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_question_signature, container, false);

        this.textViewProgress = (TextView) view.findViewById(R.id.textView_progress);
        this.textViewQuestion = (TextView) view.findViewById(R.id.textView_question);
        this.signatureView = (SignatureView) view.findViewById(R.id.signatureView);
        ImageView imageViewReset = (ImageView) view.findViewById(R.id.imageView_reset);

        ImageUtils.tintIcon(imageViewReset, R.color.feedback_signature_reset);

        imageViewReset.setOnClickListener(this);

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        this.textViewProgress.setText(this.getString(R.string.feedback_progress, this.currentQuestion, this.totalQuestions));
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.imageView_reset:
                this.signatureView.reset();
                break;
        }
    }

    @Override
    protected void updateLayout()
    {
        if(this.question != null)
        {
            List<String> enteredAnswers = this.question.getEnteredAnswers();
            String enteredAnswer = enteredAnswers != null && enteredAnswers.size() > 0 ? enteredAnswers.get(0) : null;

            this.textViewQuestion.setText(this.question.getQuestion());
            this.signatureView.setPath(enteredAnswer);
        }
    }

    @Override
    public List<String> getEnteredAnswers()
    {
        String answer = this.signatureView.getPath();

        if(!TextUtils.isEmpty(answer))
        {
            List<String> answers = new ArrayList<>();
            answers.add(answer);
            return answers;
        }

        return null;
    }

    @Override
    public boolean isAnswerRequired()
    {
        return true;
    }
}
