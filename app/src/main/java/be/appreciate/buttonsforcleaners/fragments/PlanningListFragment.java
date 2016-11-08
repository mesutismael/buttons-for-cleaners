package be.appreciate.buttonsforcleaners.fragments;

import android.content.Context;
import android.database.Cursor;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;

import com.afollestad.materialdialogs.MaterialDialog;

import java.util.List;

import be.appreciate.buttonsforcleaners.R;
import be.appreciate.buttonsforcleaners.adapters.PlanningAdapter;
import be.appreciate.buttonsforcleaners.api.ApiHelper;
import be.appreciate.buttonsforcleaners.contentproviders.PlanningContentProvider;
import be.appreciate.buttonsforcleaners.decorations.DividerDecoration;
import be.appreciate.buttonsforcleaners.loaders.LocationLoader;
import be.appreciate.buttonsforcleaners.model.PlanningItem;
import be.appreciate.buttonsforcleaners.utils.Constants;
import be.appreciate.buttonsforcleaners.utils.Observer;
import be.appreciate.buttonsforcleaners.utils.PreferencesHelper;

/**
 * Created by Inneke De Clippel on 25/02/2016.
 */
public class PlanningListFragment extends Fragment implements PlanningAdapter.OnItemClickListener, LoaderManager.LoaderCallbacks<Cursor>,
        SwipeRefreshLayout.OnRefreshListener
{
    private SwipeRefreshLayout swipeRefreshLayout;
    private ScrollView scrollViewEmpty;
    private ItemClickListener listener;
    private PlanningAdapter adapter;
    private Handler handlerRefresh;
    private Runnable refreshCallback;

    private static final long LOCATION_INTERVAL = 20 * 1000; //20 seconds
    private static final long PLANNING_REFRESH_INTERVAL = 15 * 60 * 1000; // 15 minutes
    private static final String KEY_KEEP_ITEM_SELECTED = "keep_item_selected";

    public static PlanningListFragment newInstance(boolean keepItemSelected)
    {
        Bundle bundle = new Bundle();
        bundle.putBoolean(KEY_KEEP_ITEM_SELECTED, keepItemSelected);

        PlanningListFragment fragment = new PlanningListFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_planning_list, container, false);

        RecyclerView recyclerViewPlanning = (RecyclerView) view.findViewById(R.id.recyclerView_planning);
        this.swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipeRefresh_planning);
        this.scrollViewEmpty = (ScrollView) view.findViewById(R.id.scrollView_empty);

        boolean keepItemSelected = this.getArguments().getBoolean(KEY_KEEP_ITEM_SELECTED);

        this.adapter = new PlanningAdapter(keepItemSelected);
        this.adapter.setListener(this);
        recyclerViewPlanning.setAdapter(this.adapter);
        DividerDecoration dividerDecoration = new DividerDecoration(view.getContext());
        recyclerViewPlanning.addItemDecoration(dividerDecoration);
        recyclerViewPlanning.addOnScrollListener(new RecyclerView.OnScrollListener()
        {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy)
            {
                View firstChild = recyclerView.getChildCount() > 0 ? recyclerView.getChildAt(0) : null;
                int firstChildTop = firstChild != null ? recyclerView.getLayoutManager().getDecoratedTop(firstChild) : 0;
                int firstChildLayoutPosition = firstChild != null ? recyclerView.getChildLayoutPosition(firstChild) : 0;
                PlanningListFragment.this.swipeRefreshLayout.setEnabled(firstChildLayoutPosition == 0 && firstChildTop >= 0);
            }
        });

        this.swipeRefreshLayout.setOnRefreshListener(this);

        this.handlerRefresh = new Handler();
        this.refreshCallback = () -> this.getPlanning(false);

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        this.getLoaderManager().initLoader(Constants.LOADER_PLANNING_ITEMS, null, this);
        this.getLoaderManager().initLoader(Constants.LOADER_LOCATION, null, this.locationLoaderCallbacks);
    }

    @Override
    public void onAttach(Context context)
    {
        super.onAttach(context);

        if(context instanceof ItemClickListener)
        {
            this.listener = (ItemClickListener) context;
        }
    }

    @Override
    public void onDetach()
    {
        this.listener = null;

        super.onDetach();
    }

    @Override
    public void onResume()
    {
        super.onResume();

        this.getPlanning(false);
    }

    @Override
    public void onPause()
    {
        this.handlerRefresh.removeCallbacks(this.refreshCallback);

        super.onPause();
    }

    @Override
    public void onRefresh()
    {
        this.getPlanning(true);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args)
    {
        if(this.getView() == null)
        {
            return null;
        }

        switch (id)
        {
            case Constants.LOADER_PLANNING_ITEMS:
                return new CursorLoader(this.getView().getContext(), PlanningContentProvider.CONTENT_URI_EXTRA, null, null, null, null);

            default:
                return null;
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data)
    {
        switch (loader.getId())
        {
            case Constants.LOADER_PLANNING_ITEMS:
                List<PlanningItem> planningItems = PlanningItem.constructListFromCursor(data);
                this.adapter.setPlanningItems(planningItems);
                this.scrollViewEmpty.setVisibility(planningItems.size() > 0 ? View.GONE : View.VISIBLE);
                break;
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader)
    {
    }

    @Override
    public void onPlanningClick(View caller, PlanningItem planningItem)
    {
        switch (caller.getId())
        {
            case R.id.imageView_alert:
                this.showWarningDialog();
                break;

            default:
                if(this.listener != null && planningItem != null)
                {
                    this.listener.onItemClicked(planningItem.getId());
                }
                break;
        }
    }

    private void showWarningDialog()
    {
        if(this.getContext() != null)
        {
            new MaterialDialog.Builder(this.getContext())
                    .title(R.string.planning_list_warning_title)
                    .content(R.string.planning_list_warning_message)
                    .positiveText(R.string.dialog_positive)
                    .show();
        }
    }

    private void getPlanning(boolean force)
    {
        if(this.getContext() != null)
        {
            if(force)
            {
                ApiHelper.getPlanning(this.getContext()).subscribe(this.planningObserver);
            }
            else
            {
                long lastPlanningRefresh = PreferencesHelper.getLastPlanningRefresh(this.getContext());
                long timeSinceLastPlanningRefresh = System.currentTimeMillis() - lastPlanningRefresh;

                if(timeSinceLastPlanningRefresh >= PLANNING_REFRESH_INTERVAL)
                {
                    ApiHelper.getPlanning(this.getContext()).subscribe(this.planningObserver);
                    this.planNextRefresh(PLANNING_REFRESH_INTERVAL);
                }
                else
                {
                    this.planNextRefresh(PLANNING_REFRESH_INTERVAL - timeSinceLastPlanningRefresh);
                }
            }
        }
    }

    private void planNextRefresh(long millis)
    {
        this.handlerRefresh.removeCallbacks(this.refreshCallback);
        this.handlerRefresh.postDelayed(this.refreshCallback, millis);
    }

    private Observer<Object> planningObserver = new Observer<Object>()
    {
        @Override
        public void onCompleted()
        {
            PlanningListFragment.this.swipeRefreshLayout.setRefreshing(false);
        }

        @Override
        public void onError(Throwable e)
        {
            PlanningListFragment.this.swipeRefreshLayout.setRefreshing(false);
        }
    };

    private LoaderManager.LoaderCallbacks<Location> locationLoaderCallbacks = new LoaderManager.LoaderCallbacks<Location>()
    {
        @Override
        public Loader<Location> onCreateLoader(int id, Bundle args)
        {
            if(PlanningListFragment.this.getView() != null)
            {
                return new LocationLoader(PlanningListFragment.this.getView().getContext(), LOCATION_INTERVAL);
            }

            return null;
        }

        @Override
        public void onLoadFinished(Loader<Location> loader, Location data)
        {
            switch (((LocationLoader) loader).getErrorReason())
            {
                case LocationLoader.ERROR_NO_ERROR:
                    PlanningListFragment.this.adapter.setCurrentLocation(data);
                    break;

                case LocationLoader.ERROR_LOCATION_DISABLED:
                case LocationLoader.ERROR_TIMED_OUT:
                case LocationLoader.ERROR_NO_PLAY_SERVICES:
                case LocationLoader.ERROR_CONNECTION_SUSPENDED:
                case LocationLoader.ERROR_CONNECTION_FAILED:
                case LocationLoader.ERROR_NO_PERMISSION:
                    //TODO do something if there is no location
                    break;
            }
        }

        @Override
        public void onLoaderReset(Loader<Location> loader)
        {
        }
    };

    public interface ItemClickListener
    {
        void onItemClicked(int planningId);
    }
}
