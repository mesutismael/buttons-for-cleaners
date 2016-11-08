package be.appreciate.buttonsforcleaners.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import be.appreciate.buttonsforcleaners.R;
import be.appreciate.buttonsforcleaners.adapters.QuestionChoiceAdapter;
import be.appreciate.buttonsforcleaners.model.QuestionType;
import be.appreciate.buttonsforcleaners.utils.TextUtils;

/**
 * Created by Inneke De Clippel on 14/03/2016.
 */
public class QuestionChoiceFragment extends QuestionFragment
{
    private TextView textViewProgress;
    private TextView textViewQuestion;
    private QuestionType type;
    private QuestionChoiceAdapter adapter;

    private static final String KEY_TYPE = "type";

    public static QuestionChoiceFragment newInstance(int questionId, int planningId, int currentQuestion, int totalQuestions, QuestionType type)
    {
        Bundle bundle = QuestionFragment.createBundle(questionId, planningId, currentQuestion, totalQuestions);
        bundle.putInt(KEY_TYPE, type.ordinal());

        QuestionChoiceFragment fragment = new QuestionChoiceFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_question_choice, container, false);

        this.textViewProgress = (TextView) view.findViewById(R.id.textView_progress);
        this.textViewQuestion = (TextView) view.findViewById(R.id.textView_question);
        RecyclerView recyclerViewAnswers = (RecyclerView) view.findViewById(R.id.recyclerView_answers);

        this.type = QuestionType.values()[this.getArguments().getInt(KEY_TYPE, QuestionType.BOOLEAN.ordinal())];

        this.adapter = new QuestionChoiceAdapter(this.type);
        recyclerViewAnswers.setAdapter(this.adapter);

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
            List<String> answers = this.question.getAnswers();
            List<String> enteredAnswers = this.question.getEnteredAnswers();

            if(this.type == QuestionType.BOOLEAN)
            {
                answers = new ArrayList<>();
                answers.add(this.getString(R.string.feedback_boolean_true));
                answers.add(this.getString(R.string.feedback_boolean_false));

                if(this.getContext() != null)
                {
                    enteredAnswers = TextUtils.convertBooleanAnswers(this.getContext(), enteredAnswers, true);
                }
            }

            this.textViewQuestion.setText(this.question.getQuestion());

            this.adapter.setAnswers(answers);
            this.adapter.setSelectedAnswers(enteredAnswers);
        }
    }

    @Override
    public List<String> getEnteredAnswers()
    {
        List<String> enteredAnswers = this.adapter.getSelectedAnswers();
        boolean convert = this.type == QuestionType.BOOLEAN && this.getContext() != null;
        return convert ? TextUtils.convertBooleanAnswers(this.getContext(), enteredAnswers, false) : enteredAnswers;
    }

    @Override
    public boolean isAnswerRequired()
    {
        return this.type == null || this.type != QuestionType.MULTI_CHOICE;
    }
}
