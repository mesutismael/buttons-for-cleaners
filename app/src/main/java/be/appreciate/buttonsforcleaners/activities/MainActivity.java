package be.appreciate.buttonsforcleaners.activities;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;

import be.appreciate.buttonsforcleaners.R;
import be.appreciate.buttonsforcleaners.asynctasks.LogOutAsyncTask;
import be.appreciate.buttonsforcleaners.contentproviders.FeedbackContentProvider;
import be.appreciate.buttonsforcleaners.database.FeedbackTable;
import be.appreciate.buttonsforcleaners.fragments.PlanningDetailFragment;
import be.appreciate.buttonsforcleaners.fragments.PlanningListFragment;
import be.appreciate.buttonsforcleaners.services.UploadService;
import be.appreciate.buttonsforcleaners.utils.ConnectivityManager;
import be.appreciate.buttonsforcleaners.utils.Constants;
import be.appreciate.buttonsforcleaners.utils.ImageUtils;
import be.appreciate.buttonsforcleaners.utils.PreferencesHelper;

/**
 * Created by Inneke De Clippel on 25/02/2016.
 */
public class MainActivity extends AppCompatActivity implements PlanningListFragment.ItemClickListener,
        NavigationView.OnNavigationItemSelectedListener, ConnectivityManager.ConnectivityListener,
        LoaderManager.LoaderCallbacks<Cursor>, LogOutAsyncTask.LogOutAsyncTaskListener
{
    private NavigationView navigationView;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle drawerToggle;
    private boolean twoPane;
    private boolean connected;
    private int notSyncedFeedbackCount;
    private long lastSyncAttempt;
    private MaterialDialog progressDialog;

    private static final long SYNC_ATTEMPT_INTERVAL = 3 * 60 * 1000; // 3 minutes

    public static Intent getIntent(Context context)
    {
        return new Intent(context, MainActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        this.setRequestedOrientation(this.getResources().getBoolean(R.bool.tablet) ? ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE : ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        this.setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) this.findViewById(R.id.toolbar);
        this.drawerLayout = (DrawerLayout) this.findViewById(R.id.drawerLayout);
        this.navigationView = (NavigationView) this.findViewById(R.id.navigationView);
        View headerView = this.navigationView.getHeaderView(0);
        ImageView imageViewUser = (ImageView) headerView.findViewById(R.id.imageView_user);
        TextView textViewUsername = (TextView) headerView.findViewById(R.id.textView_name);

        this.setSupportActionBar(toolbar);

        this.drawerToggle = new ActionBarDrawerToggle(this, this.drawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close);
        this.drawerLayout.addDrawerListener(this.drawerToggle);

        this.navigationView.setNavigationItemSelectedListener(this);
        this.navigationView.getMenu().findItem(R.id.action_synchronised).setVisible(true);
        this.navigationView.getMenu().findItem(R.id.action_not_synchronised).setVisible(false);
        this.navigationView.getMenu().findItem(R.id.action_sync).setVisible(false);

        this.twoPane = this.findViewById(R.id.frameLayout_detail) != null;
        this.connected = true;

        this.getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.frameLayout_list, PlanningListFragment.newInstance(this.twoPane))
                .commit();

        String userName = PreferencesHelper.getUserName(this);
        String userImage = PreferencesHelper.getUserImageUrl(this);
        textViewUsername.setText(userName);
        ImageUtils.loadImage(imageViewUser, userImage, R.drawable.placeholder_person);

        this.getSupportLoaderManager().initLoader(Constants.LOADER_FEEDBACK, null, this);
    }

    @Override
    protected void onStart()
    {
        super.onStart();

        ConnectivityManager.addListener(this);
    }

    @Override
    protected void onStop()
    {
        ConnectivityManager.removeListener(this);

        super.onStop();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState)
    {
        super.onPostCreate(savedInstanceState);

        this.drawerToggle.syncState();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        this.getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu)
    {
        MenuItem menuItemConnection = menu.findItem(R.id.action_connection);
        menuItemConnection.setVisible(!this.connected);
        ImageUtils.tintIcon(this, menuItemConnection, R.color.main_menu_icon);

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        if (this.drawerToggle.onOptionsItemSelected(item))
        {
            return true;
        }

        switch (item.getItemId())
        {
            case R.id.action_connection:
                new MaterialDialog.Builder(this)
                        .title(R.string.main_connection_dialog_title)
                        .content(R.string.main_connection_dialog_message)
                        .positiveText(R.string.dialog_positive)
                        .show();

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig)
    {
        super.onConfigurationChanged(newConfig);

        this.drawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public void onBackPressed()
    {
        if (this.drawerLayout.isDrawerOpen(Gravity.LEFT))
        {
            this.drawerLayout.closeDrawers();
            return;
        }

        super.onBackPressed();
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.action_logOut:
                if(this.notSyncedFeedbackCount > 0)
                {
                    this.showLogOutDialog();
                }
                else
                {
                    this.logOut();
                }
                this.drawerLayout.closeDrawers();
                return true;

            case R.id.action_sync:
                this.syncFeedback(true);
                return true;

            default:
                return false;
        }
    }

    @Override
    public void onItemClicked(int planningId)
    {
        if(this.twoPane)
        {
            this.getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.frameLayout_detail, PlanningDetailFragment.newInstance(planningId, this.twoPane))
                    .commit();
        }
        else
        {
            this.startActivity(PlanningDetailActivity.getIntent(this, planningId));
        }
    }

    @Override
    public void onConnected()
    {
        if(!this.connected)
        {
            this.connected = true;
            this.supportInvalidateOptionsMenu();
        }

        this.syncFeedback(false);
    }

    @Override
    public void onDisconnected()
    {
        if(this.connected)
        {
            this.connected = false;
            this.supportInvalidateOptionsMenu();
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args)
    {
        switch (id)
        {
            case Constants.LOADER_FEEDBACK:
                String selection = FeedbackTable.TABLE_NAME + "." + FeedbackTable.COLUMN_END_COMPLETED + " = 1"
                        + " AND " + FeedbackTable.TABLE_NAME + "." + FeedbackTable.COLUMN_SENT_TO_API + " = 0";
                return new CursorLoader(this, FeedbackContentProvider.CONTENT_URI, null, selection, null, null);

            default:
                return null;
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data)
    {
        switch (loader.getId())
        {
            case Constants.LOADER_FEEDBACK:
                this.notSyncedFeedbackCount = data != null ? data.getCount() : 0;
                this.syncFeedback(false);

                this.navigationView.getMenu().findItem(R.id.action_synchronised).setVisible(this.notSyncedFeedbackCount == 0);
                this.navigationView.getMenu().findItem(R.id.action_not_synchronised).setVisible(this.notSyncedFeedbackCount != 0);
                this.navigationView.getMenu().findItem(R.id.action_sync).setVisible(this.notSyncedFeedbackCount != 0);
                break;
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader)
    {
    }

    private void syncFeedback(boolean force)
    {
        if(this.connected && this.notSyncedFeedbackCount > 0)
        {
            long now = System.currentTimeMillis();
            long elapsedTime = now - this.lastSyncAttempt;
            boolean sync = elapsedTime > SYNC_ATTEMPT_INTERVAL;

            this.lastSyncAttempt = now;

            if((force && !UploadService.RUNNING) || sync)
            {
                this.startService(UploadService.getIntent(this));
            }
        }
    }

    private void showLogOutDialog()
    {
        new MaterialDialog.Builder(this)
                .title(R.string.dialog_warning)
                .content(R.string.main_log_out_sync)
                .positiveText(R.string.main_log_out_positive)
                .negativeText(R.string.dialog_negative)
                .onPositive((dialog, which) ->
                {
                    this.logOut();
                })
                .show();
    }

    private void logOut()
    {
        this.progressDialog = new MaterialDialog.Builder(this)
                .content(R.string.main_log_out_progress)
                .progress(true, 0)
                .cancelable(false)
                .show();

        LogOutAsyncTask task = new LogOutAsyncTask(this);
        task.setListener(this);
        task.execute();
    }

    @Override
    public void onDataCleared()
    {
        if(this.progressDialog != null)
        {
            this.progressDialog.dismiss();
        }

        Intent intent = LoginActivity.getIntent(this);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        this.startActivity(intent);
    }
}
