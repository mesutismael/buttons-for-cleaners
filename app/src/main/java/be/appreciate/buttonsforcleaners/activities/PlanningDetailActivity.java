package be.appreciate.buttonsforcleaners.activities;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import be.appreciate.buttonsforcleaners.R;
import be.appreciate.buttonsforcleaners.fragments.PlanningDetailFragment;

/**
 * Created by Inneke De Clippel on 25/02/2016.
 */
public class PlanningDetailActivity extends AppCompatActivity
{
    private static final String KEY_PLANNING_ID = "planning_id";

    public static Intent getIntent(Context context, int planningId)
    {
        Intent intent = new Intent(context, PlanningDetailActivity.class);
        intent.putExtra(KEY_PLANNING_ID, planningId);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        this.setRequestedOrientation(this.getResources().getBoolean(R.bool.tablet) ? ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE : ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        this.setContentView(R.layout.activity_detail);

        Toolbar toolbar = (Toolbar) this.findViewById(R.id.toolbar);
        this.setSupportActionBar(toolbar);
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        int planningId = this.getIntent().getIntExtra(KEY_PLANNING_ID, 0);
        this.getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.frameLayout_detail, PlanningDetailFragment.newInstance(planningId, false))
                .commit();
    }
}
