package be.appreciate.buttonsforcleaners.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.ViewGroup;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import be.appreciate.buttonsforcleaners.fragments.QuestionChoiceFragment;
import be.appreciate.buttonsforcleaners.fragments.QuestionFragment;
import be.appreciate.buttonsforcleaners.fragments.QuestionPhotoFragment;
import be.appreciate.buttonsforcleaners.fragments.QuestionProductFragment;
import be.appreciate.buttonsforcleaners.fragments.QuestionSignatureFragment;
import be.appreciate.buttonsforcleaners.fragments.QuestionTextFragment;
import be.appreciate.buttonsforcleaners.model.Question;
import be.appreciate.buttonsforcleaners.model.QuestionType;

/**
 * Created by Inneke De Clippel on 10/03/2016.
 */
public class FeedbackPagerAdapter extends FragmentStatePagerAdapter
{
    private int planningId;
    private List<Question> questions;
    private Map<Integer, QuestionFragment> fragments;

    public FeedbackPagerAdapter(FragmentManager fragmentManager)
    {
        super(fragmentManager);
        this.fragments = new HashMap<>();
    }

    public void setQuestions(int planningId, List<Question> questions)
    {
        this.planningId = planningId;
        this.questions = questions;
        this.notifyDataSetChanged();
    }

    @Override
    public Fragment getItem(int position)
    {
        Question question = this.questions.get(position);
        int questionId = question.getId();
        int currentQuestion = position + 1;
        int totalQuestions = this.getCount();
        QuestionType type = question.getType();
        QuestionFragment fragment;

        switch (type)
        {
            case TEXT_SHORT:
                fragment = QuestionTextFragment.newInstance(questionId, this.planningId, currentQuestion, totalQuestions, true);
                break;

            case TEXT_LONG:
                fragment = QuestionTextFragment.newInstance(questionId, this.planningId, currentQuestion, totalQuestions, false);
                break;

            case BOOLEAN:
            case SINGLE_CHOICE:
            case MULTI_CHOICE:
                fragment = QuestionChoiceFragment.newInstance(questionId, this.planningId, currentQuestion, totalQuestions, type);
                break;

            case PHOTOS:
                fragment = QuestionPhotoFragment.newInstance(questionId, this.planningId, currentQuestion, totalQuestions);
                break;

            case DRAWING:
                fragment = QuestionSignatureFragment.newInstance(questionId, this.planningId, currentQuestion, totalQuestions);
                break;

            case PRODUCT_PICKER:
                fragment = QuestionProductFragment.newInstance(questionId, this.planningId, currentQuestion, totalQuestions);
                break;

            default:
                fragment = QuestionTextFragment.newInstance(questionId, this.planningId, currentQuestion, totalQuestions, true);
                break;
        }

        this.fragments.put(position, fragment);
        return fragment;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object)
    {
        super.destroyItem(container, position, object);

        this.fragments.remove(position);
    }

    @Override
    public int getCount()
    {
        return this.questions != null ? this.questions.size() : 0;
    }

    public QuestionFragment getFragment(int position)
    {
        return this.fragments.get(position);
    }
}
