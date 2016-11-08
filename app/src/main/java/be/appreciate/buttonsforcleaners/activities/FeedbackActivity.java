package be.appreciate.buttonsforcleaners.activities;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.NavUtils;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.afollestad.materialdialogs.MaterialDialog;

import java.util.List;

import be.appreciate.buttonsforcleaners.R;
import be.appreciate.buttonsforcleaners.adapters.FeedbackPagerAdapter;
import be.appreciate.buttonsforcleaners.asynctasks.UpdateAnswerAsyncTask;
import be.appreciate.buttonsforcleaners.asynctasks.UpdateFeedbackAsyncTask;
import be.appreciate.buttonsforcleaners.contentproviders.QuestionContentProvider;
import be.appreciate.buttonsforcleaners.database.QuestionTable;
import be.appreciate.buttonsforcleaners.fragments.QuestionFragment;
import be.appreciate.buttonsforcleaners.model.PointOfTime;
import be.appreciate.buttonsforcleaners.model.Question;
import be.appreciate.buttonsforcleaners.services.LocationService;
import be.appreciate.buttonsforcleaners.services.UploadService;
import be.appreciate.buttonsforcleaners.utils.Constants;
import be.appreciate.buttonsforcleaners.utils.TextUtils;
import be.appreciate.buttonsforcleaners.views.RadiusCirclePageIndicator;

/**
 * Created by Inneke De Clippel on 10/03/2016.
 */
public class FeedbackActivity extends AppCompatActivity implements View.OnClickListener, LoaderManager.LoaderCallbacks<Cursor>,
        ViewPager.OnPageChangeListener, UpdateFeedbackAsyncTask.UpdateAsyncTaskListener
{
    private ViewPager viewPager;
    private Button buttonPrevious;
    private Button buttonNext;
    private boolean twoPane;
    private int planningId;
    private int contractTypeId;
    private PointOfTime pointOfTime;
    private FeedbackPagerAdapter pagerAdapter;

    private static final String KEY_PLANNING_ID = "planning_id";
    private static final String KEY_CONTRACT_TYPE_ID = "contract_type_id";
    private static final String KEY_POINT_OF_TIME = "point_in_time";
    private static final String KEY_TWO_PANE = "two_pane";

    public static Intent getIntent(Context context, int planningId, int contractTypeId, int pointOfTime, boolean twoPane)
    {
        Intent intent = new Intent(context, FeedbackActivity.class);
        intent.putExtra(KEY_PLANNING_ID, planningId);
        intent.putExtra(KEY_CONTRACT_TYPE_ID, contractTypeId);
        intent.putExtra(KEY_POINT_OF_TIME, pointOfTime);
        intent.putExtra(KEY_TWO_PANE, twoPane);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        this.setRequestedOrientation(this.getResources().getBoolean(R.bool.tablet) ? ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE : ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        this.setContentView(R.layout.activity_feedback);

        Toolbar toolbar = (Toolbar) this.findViewById(R.id.toolbar);
        RadiusCirclePageIndicator indicator = (RadiusCirclePageIndicator) this.findViewById(R.id.indicator_feedback);
        this.viewPager = (ViewPager) this.findViewById(R.id.viewPager_feedback);
        this.buttonPrevious = (Button) this.findViewById(R.id.button_previous);
        this.buttonNext = (Button) this.findViewById(R.id.button_next);

        this.setSupportActionBar(toolbar);
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        this.pagerAdapter = new FeedbackPagerAdapter(this.getSupportFragmentManager());
        this.viewPager.setAdapter(this.pagerAdapter);
        indicator.setViewPager(this.viewPager);
        indicator.setOnPageChangeListener(this);

        this.buttonPrevious.setOnClickListener(this);
        this.buttonNext.setOnClickListener(this);

        this.updateButtons(0);

        this.twoPane = this.getIntent().getBooleanExtra(KEY_TWO_PANE, true);
        this.planningId = this.getIntent().getIntExtra(KEY_PLANNING_ID, 0);
        this.contractTypeId = this.getIntent().getIntExtra(KEY_CONTRACT_TYPE_ID, 0);
        this.pointOfTime = PointOfTime.getPointOfTime(this.getIntent().getIntExtra(KEY_POINT_OF_TIME, 0));

        this.getSupportLoaderManager().initLoader(Constants.LOADER_QUESTIONS, null, this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case android.R.id.home:
                this.showBackDialog(true);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed()
    {
        this.showBackDialog(false);
    }

    @Override
    public void onClick(View v)
    {
        int currentPage = this.viewPager.getCurrentItem();
        QuestionFragment currentFragment = this.pagerAdapter.getFragment(currentPage);
        List<String> previousAnswers = currentFragment != null ? currentFragment.getPreviousAnswers() : null;
        List<String> enteredAnswers = currentFragment != null ? currentFragment.getEnteredAnswers() : null;
        int questionId = currentFragment != null ? currentFragment.getQuestionId() : 0;
        boolean answerRequired = currentFragment == null || currentFragment.isAnswerRequired();

        switch (v.getId())
        {
            case R.id.button_previous:
                if(!TextUtils.equals(previousAnswers, enteredAnswers))
                {
                    UpdateAnswerAsyncTask task = new UpdateAnswerAsyncTask(v.getContext(), this.planningId, questionId, enteredAnswers);
                    task.execute();
                }

                this.viewPager.setCurrentItem(currentPage - 1);
                break;

            case R.id.button_next:
                if((enteredAnswers == null || enteredAnswers.size() == 0) && answerRequired)
                {
                    new MaterialDialog.Builder(v.getContext())
                            .title(R.string.dialog_error)
                            .content(R.string.feedback_next_error)
                            .positiveText(R.string.dialog_positive)
                            .show();
                }
                else
                {
                    enteredAnswers = TextUtils.convertEnteredAnswers(enteredAnswers);

                    if(!TextUtils.equals(previousAnswers, enteredAnswers))
                    {
                        UpdateAnswerAsyncTask task = new UpdateAnswerAsyncTask(v.getContext(), this.planningId, questionId, enteredAnswers);
                        task.execute();
                    }

                    if(currentPage == this.viewPager.getAdapter().getCount() - 1)
                    {
                        UpdateFeedbackAsyncTask task = new UpdateFeedbackAsyncTask(v.getContext(), this.planningId, this.contractTypeId, this.pointOfTime, false, true);
                        task.setListener(this.pointOfTime == PointOfTime.STOP ? this : null);
                        task.execute();

                        if(this.pointOfTime == PointOfTime.STOP)
                        {
                            v.getContext().startService(LocationService.getIntent(v.getContext(), this.planningId, this.pointOfTime, null));
                        }
                        else
                        {
                            this.finish();
                        }
                    }
                    else
                    {
                        this.viewPager.setCurrentItem(this.viewPager.getCurrentItem() + 1);
                    }
                }
                break;
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args)
    {
        switch (id)
        {
            case Constants.LOADER_QUESTIONS:
                String where = QuestionTable.TABLE_NAME + "." + QuestionTable.COLUMN_CONTRACT_TYPE_ID + " = " + this.contractTypeId
                        + " AND " + QuestionTable.TABLE_NAME + "." + QuestionTable.COLUMN_POINT_OF_TIME + " = " + this.pointOfTime.getPointOfTimeId();
                return new CursorLoader(this, QuestionContentProvider.CONTENT_URI, null, where, null, null);

            default:
                return null;
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data)
    {
        switch (loader.getId())
        {
            case Constants.LOADER_QUESTIONS:
                List<Question> questions = Question.constructListFromCursor(data);
                this.pagerAdapter.setQuestions(this.planningId, questions);
                this.updateButtons(this.viewPager.getCurrentItem());
                break;
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader)
    {
    }

    @Override
    public void onPageSelected(int position)
    {
        this.updateButtons(position);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels)
    {
    }

    @Override
    public void onPageScrollStateChanged(int state)
    {
    }

    private void updateButtons(int position)
    {
        int count = this.viewPager.getAdapter().getCount();

        this.buttonPrevious.setVisibility(position > 0 ? View.VISIBLE : View.INVISIBLE);
        this.buttonPrevious.setEnabled(position > 0);
        this.buttonNext.setVisibility(count > 0 ? View.VISIBLE : View.INVISIBLE);
        this.buttonNext.setEnabled(count > 0);
        this.buttonNext.setText(position < count - 1 ? R.string.feedback_next : R.string.feedback_finish);
    }

    private void showBackDialog(boolean pressedUp)
    {
        new MaterialDialog.Builder(this)
                .title(R.string.dialog_warning)
                .content(R.string.feedback_back_warning)
                .positiveText(R.string.dialog_positive)
                .negativeText(R.string.dialog_negative)
                .onPositive((dialog, which) ->
                {
                    if (pressedUp)
                    {
                        this.goUp();
                    }
                    else
                    {
                        this.goBack();
                    }
                })
                .show();
    }

    private void goUp()
    {
        if(!this.twoPane)
        {
            NavUtils.navigateUpTo(this, PlanningDetailActivity.getIntent(this, this.planningId));
        }
        else
        {
            NavUtils.navigateUpFromSameTask(this);
        }
    }

    private void goBack()
    {
        super.onBackPressed();
    }

    @Override
    public void onFeedbackUpdated()
    {
        this.startService(UploadService.getIntent(this, this.planningId));
        this.finish();
    }
}
