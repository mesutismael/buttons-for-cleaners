package be.appreciate.buttonsforcleaners.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.text.InputType;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import be.appreciate.buttonsforcleaners.R;
import be.appreciate.buttonsforcleaners.utils.Constants;

/**
 * Created by Inneke De Clippel on 10/03/2016.
 */
public class QuestionTextFragment extends QuestionFragment
{
    private TextView textViewProgress;
    private TextView textViewQuestion;
    private EditText editTextAnswer;
    private TextInputLayout textInputLayoutAnswer;
    private boolean limited;

    private static final String KEY_LIMITED = "limited";

    public static QuestionTextFragment newInstance(int questionId, int planningId, int currentQuestion, int totalQuestions, boolean limited)
    {
        Bundle bundle = QuestionFragment.createBundle(questionId, planningId, currentQuestion, totalQuestions);
        bundle.putBoolean(KEY_LIMITED, limited);

        QuestionTextFragment fragment = new QuestionTextFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_question_text, container, false);

        this.textViewProgress = (TextView) view.findViewById(R.id.textView_progress);
        this.textViewQuestion = (TextView) view.findViewById(R.id.textView_question);
        this.editTextAnswer = (EditText) view.findViewById(R.id.editText_answer);
        this.textInputLayoutAnswer = (TextInputLayout) view.findViewById(R.id.textInputLayout_answer);

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        this.limited = this.getArguments().getBoolean(KEY_LIMITED, true);

        this.textViewProgress.setText(this.getString(R.string.feedback_progress, this.currentQuestion, this.totalQuestions));
        this.textInputLayoutAnswer.setCounterMaxLength(Constants.ANSWER_TEXT_SHORT_MAX_LENGTH);
        this.textInputLayoutAnswer.setCounterEnabled(this.limited);
        this.editTextAnswer.setInputType(this.limited
                ? InputType.TYPE_CLASS_TEXT
                : InputType.TYPE_CLASS_TEXT|InputType.TYPE_TEXT_FLAG_MULTI_LINE|InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
    }

    @Override
    protected void updateLayout()
    {
        if(this.question != null)
        {
            List<String> enteredAnswers = this.question.getEnteredAnswers();
            String enteredAnswer = enteredAnswers != null && enteredAnswers.size() > 0 ? enteredAnswers.get(0) : null;

            this.textViewQuestion.setText(this.question.getQuestion());
            this.editTextAnswer.setText(enteredAnswer);
        }
    }

    @Override
    public List<String> getEnteredAnswers()
    {
        String answer = this.editTextAnswer.getText().toString();

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
