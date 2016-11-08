package be.appreciate.buttonsforcleaners.fragments;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;

import java.util.List;

import be.appreciate.buttonsforcleaners.R;
import be.appreciate.buttonsforcleaners.activities.FeedbackActivity;
import be.appreciate.buttonsforcleaners.api.ApiHelper;
import be.appreciate.buttonsforcleaners.asynctasks.UpdateFeedbackAsyncTask;
import be.appreciate.buttonsforcleaners.contentproviders.PlanningContentProvider;
import be.appreciate.buttonsforcleaners.contentproviders.PlanningResourceContentProvider;
import be.appreciate.buttonsforcleaners.database.PlanningResourceTable;
import be.appreciate.buttonsforcleaners.loaders.LocationLoader;
import be.appreciate.buttonsforcleaners.model.PlanningItem;
import be.appreciate.buttonsforcleaners.model.PointOfTime;
import be.appreciate.buttonsforcleaners.model.Resource;
import be.appreciate.buttonsforcleaners.services.LocationService;
import be.appreciate.buttonsforcleaners.services.UploadService;
import be.appreciate.buttonsforcleaners.utils.Constants;
import be.appreciate.buttonsforcleaners.utils.ImageUtils;
import be.appreciate.buttonsforcleaners.utils.Observer;

/**
 * Created by Inneke De Clippel on 25/02/2016.
 */
public class PlanningDetailFragment extends Fragment implements View.OnClickListener, LoaderManager.LoaderCallbacks<Cursor>,
        UpdateFeedbackAsyncTask.UpdateAsyncTaskListener
{
    private LinearLayout layoutTime;
    private TextView textViewDateTitle;
    private TextView textViewDate;
    private TextView textViewStartTimeTitle;
    private TextView textViewStartTime;
    private TextView textViewEndTimeTitle;
    private TextView textViewEndTime;
    private Button buttonNotify;
    private LinearLayout layoutGeneral;
    private TextView textViewDescriptionTitle;
    private TextView textViewDescription;
    private TextView textViewAddressTitle;
    private TextView textViewAddress;
    private Button buttonNavigate;
    private LinearLayout layoutRemarks;
    private TextView textViewInstructionsTitle;
    private TextView textViewInstructions;
    private LinearLayout layoutPeople;
    private LinearLayout layoutVehicles;
    private LinearLayout layoutMaterials;
    private LinearLayout layoutExtra;
    private Button buttonStartStop;
    private int planningId;
    private boolean twoPane;
    private PlanningItem planningItem;
    private List<Resource> resources;
    private MaterialDialog progressDialog;
    private Location currentLocation;

    private static final long LOCATION_INTERVAL = 20 * 1000; //20 seconds
    private static final String KEY_PLANNING_ID = "planning_id";
    private static final String KEY_TWO_PANE = "two_pane";

    public static PlanningDetailFragment newInstance(int planningId, boolean twoPane)
    {
        Bundle bundle = new Bundle();
        bundle.putInt(KEY_PLANNING_ID, planningId);
        bundle.putBoolean(KEY_TWO_PANE, twoPane);

        PlanningDetailFragment fragment = new PlanningDetailFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_planning_detail, container, false);

        this.layoutTime = (LinearLayout) view.findViewById(R.id.layout_time);
        this.textViewDateTitle = (TextView) view.findViewById(R.id.textView_dateTitle);
        this.textViewDate = (TextView) view.findViewById(R.id.textView_date);
        this.textViewStartTimeTitle = (TextView) view.findViewById(R.id.textView_startTimeTitle);
        this.textViewStartTime = (TextView) view.findViewById(R.id.textView_startTime);
        this.textViewEndTimeTitle = (TextView) view.findViewById(R.id.textView_endTimeTitle);
        this.textViewEndTime = (TextView) view.findViewById(R.id.textView_endTime);
        this.buttonNotify = (Button) view.findViewById(R.id.button_notify);
        this.layoutGeneral = (LinearLayout) view.findViewById(R.id.layout_general);
        this.textViewDescriptionTitle = (TextView) view.findViewById(R.id.textView_descriptionTitle);
        this.textViewDescription = (TextView) view.findViewById(R.id.textView_description);
        this.textViewAddressTitle = (TextView) view.findViewById(R.id.textView_addressTitle);
        this.textViewAddress = (TextView) view.findViewById(R.id.textView_address);
        this.buttonNavigate = (Button) view.findViewById(R.id.button_navigation);
        this.layoutRemarks = (LinearLayout) view.findViewById(R.id.layout_remarks);
        this.textViewInstructionsTitle = (TextView) view.findViewById(R.id.textView_instructionsTitle);
        this.textViewInstructions = (TextView) view.findViewById(R.id.textView_instructions);
        this.layoutPeople = (LinearLayout) view.findViewById(R.id.layout_people);
        this.layoutVehicles = (LinearLayout) view.findViewById(R.id.layout_vehicles);
        this.layoutMaterials = (LinearLayout) view.findViewById(R.id.layout_materials);
        this.layoutExtra = (LinearLayout) view.findViewById(R.id.layout_extra);
        this.buttonStartStop = (Button) view.findViewById(R.id.button_startStop);

        this.buttonNotify.setOnClickListener(this);
        this.buttonNavigate.setOnClickListener(this);
        this.buttonStartStop.setOnClickListener(this);

        this.planningId = this.getArguments().getInt(KEY_PLANNING_ID);
        this.twoPane = this.getArguments().getBoolean(KEY_TWO_PANE);

        this.updateLayout();

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        this.getLoaderManager().initLoader(Constants.LOADER_PLANNING_ITEMS, null, this);
        this.getLoaderManager().initLoader(Constants.LOADER_PLANNING_RESOURCES, null, this);
        this.getLoaderManager().initLoader(Constants.LOADER_LOCATION, null, this.locationLoaderCallbacks);
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.button_notify:
                this.notifyCustomer(v.getContext());
                break;

            case R.id.button_navigation:
                if(this.planningItem != null)
                {
                    try
                    {
                        Uri uri = Uri.parse(this.getString(R.string.navigation_uri, this.planningItem.getLatitude(), this.planningItem.getLongitude()));
                        Intent mapIntent = new Intent(Intent.ACTION_VIEW, uri);
                        mapIntent.setPackage("com.google.android.apps.maps");
                        this.startActivity(mapIntent);
                    }
                    catch (ActivityNotFoundException e)
                    {
                        Toast.makeText(v.getContext(), R.string.detail_navigate_error, Toast.LENGTH_LONG).show();
                    }
                }
                break;

            case R.id.button_startStop:
                if(this.planningItem != null)
                {
                    PointOfTime pointOfTime = this.planningItem.isStartCompleted() ? PointOfTime.STOP : PointOfTime.START;
                    int contractTypeId = this.planningItem.getContractTypeId();
                    boolean hasQuestions = this.planningItem.getQuestionCount() > 0;

                    if(pointOfTime == PointOfTime.START && !this.planningItem.canStartExecution(this.currentLocation))
                    {
                        new MaterialDialog.Builder(v.getContext())
                                .title(R.string.dialog_error)
                                .content(R.string.detail_proximity_error)
                                .positiveText(R.string.dialog_positive)
                                .show();
                    }
                    else
                    {
                        UpdateFeedbackAsyncTask task = new UpdateFeedbackAsyncTask(v.getContext(), this.planningId, contractTypeId, pointOfTime, true, hasQuestions);
                        task.setListener(pointOfTime == PointOfTime.STOP && !hasQuestions ? this : null);
                        task.execute();

                        if(hasQuestions)
                        {
                            this.startActivity(FeedbackActivity.getIntent(v.getContext(), this.planningId, contractTypeId, pointOfTime.getPointOfTimeId(), this.twoPane));
                        }

                        if(pointOfTime == PointOfTime.START || (pointOfTime == PointOfTime.STOP && !hasQuestions))
                        {
                            v.getContext().startService(LocationService.getIntent(v.getContext(), this.planningId, pointOfTime, this.currentLocation));
                        }
                    }
                }
                break;
        }
    }

    @Override
    public void onFeedbackUpdated()
    {
        if(this.getContext() != null)
        {
            this.getContext().startService(UploadService.getIntent(this.getContext(), this.planningId));
        }
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
                Uri uri = Uri.withAppendedPath(PlanningContentProvider.CONTENT_URI_EXTRA, String.valueOf(this.planningId));
                return new CursorLoader(this.getView().getContext(), uri, null, null, null, null);

            case Constants.LOADER_PLANNING_RESOURCES:
                String resourcesWhere = PlanningResourceTable.TABLE_NAME + "." +PlanningResourceTable.COLUMN_PLANNING_ID + " = " + this.planningId;
                return new CursorLoader(this.getView().getContext(), PlanningResourceContentProvider.CONTENT_URI, null, resourcesWhere, null, null);

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
                if(data != null && data.moveToFirst())
                {
                    this.planningItem = PlanningItem.constructFromCursor(data);
                    this.updateLayout();
                }
                break;

            case Constants.LOADER_PLANNING_RESOURCES:
                this.resources = Resource.constructListFromCursor(data);
                this.updateLayout();
                break;
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader)
    {
    }

    private void updateLayout()
    {
        boolean hasDate = false;
        boolean hasStartTime = false;
        boolean hasEndTime = false;
        boolean hasPhone = false;
        boolean hasDescription = false;
        boolean hasAddress = false;
        boolean hasCoordinates = false;
        boolean hasInstructions = false;
        boolean hasPeople = false;
        boolean hasVehicles = false;
        boolean hasMaterials = false;
        boolean hasExtra = false;
        boolean showStartStop = false;

        if(this.planningItem != null)
        {
            String date = this.planningItem.getFormattedDate();
            String startTime = this.planningItem.getStartTime();
            String endTime = this.planningItem.getEndTime();
            String phone = this.planningItem.getPhone();
            String description = this.planningItem.getDescription();
            String address = this.planningItem.getAddress();
            double latitude = this.planningItem.getLatitude();
            double longitude = this.planningItem.getLongitude();
            String instructions = this.planningItem.getInstructions();
            String locationName = this.planningItem.getLocationName();
            boolean startCompleted = this.planningItem.isStartCompleted();
            boolean endCompleted = this.planningItem.isEndCompleted();

            hasDate = !TextUtils.isEmpty(date);
            hasStartTime = !TextUtils.isEmpty(startTime);
            hasEndTime = !TextUtils.isEmpty(endTime);
            hasPhone = !TextUtils.isEmpty(phone);
            hasDescription = !TextUtils.isEmpty(description);
            hasAddress = !TextUtils.isEmpty(address);
            hasCoordinates = latitude != 0 && longitude != 0;
            hasInstructions = !TextUtils.isEmpty(instructions);
            showStartStop = !endCompleted;

            this.textViewDate.setText(date);
            this.textViewStartTime.setText(startTime);
            this.textViewEndTime.setText(endTime);
            this.textViewDescription.setText(description);
            this.textViewAddress.setText(address);
            this.textViewInstructions.setText(instructions);
            this.buttonStartStop.setText(startCompleted ? R.string.detail_stop : R.string.detail_start);

            if(this.getActivity() != null && !TextUtils.isEmpty(locationName) && !this.twoPane)
            {
                this.getActivity().setTitle(locationName);
            }
        }

        if(this.resources != null && this.resources.size() > 0)
        {
            this.removeResourceViews(this.layoutPeople);
            this.removeResourceViews(this.layoutVehicles);
            this.removeResourceViews(this.layoutMaterials);
            this.removeResourceViews(this.layoutExtra);

            for(Resource resource : this.resources)
            {
                switch (resource.getType())
                {
                    case PERSON:
                        hasPeople = true;
                        this.addResourceView(this.layoutPeople, resource.getImageUrl(), resource.getName(), R.drawable.placeholder_person);
                        break;

                    case VEHICLE:
                        hasVehicles = true;
                        this.addResourceView(this.layoutVehicles, resource.getImageUrl(), resource.getName(), R.drawable.placeholder_vehicle);
                        break;

                    case MATERIAL:
                        hasMaterials = true;
                        this.addResourceView(this.layoutMaterials, resource.getImageUrl(), resource.getName(), R.drawable.placeholder_product);
                        break;

                    case EXTRA:
                        hasExtra = true;
                        this.addResourceView(this.layoutExtra, resource.getImageUrl(), resource.getName(), R.drawable.placeholder_extra);
                        break;
                }
            }
        }

        this.layoutTime.setVisibility(View.VISIBLE);
        this.textViewDateTitle.setVisibility(hasDate ? View.VISIBLE : View.GONE);
        this.textViewDate.setVisibility(hasDate ? View.VISIBLE : View.GONE);
        this.textViewStartTimeTitle.setVisibility(hasStartTime ? View.VISIBLE : View.GONE);
        this.textViewStartTime.setVisibility(hasStartTime ? View.VISIBLE : View.GONE);
        this.textViewEndTimeTitle.setVisibility(hasEndTime ? View.VISIBLE : View.GONE);
        this.textViewEndTime.setVisibility(hasEndTime ? View.VISIBLE : View.GONE);
        this.buttonNotify.setVisibility(hasPhone ? View.VISIBLE : View.GONE);

        this.layoutGeneral.setVisibility(hasDescription || hasAddress || hasCoordinates ? View.VISIBLE : View.GONE);
        this.textViewDescriptionTitle.setVisibility(hasDescription ? View.VISIBLE : View.GONE);
        this.textViewDescription.setVisibility(hasDescription ? View.VISIBLE : View.GONE);
        this.textViewAddressTitle.setVisibility(hasAddress ? View.VISIBLE : View.GONE);
        this.textViewAddress.setVisibility(hasAddress ? View.VISIBLE : View.GONE);
        this.buttonNavigate.setVisibility(hasCoordinates ? View.VISIBLE : View.GONE);

        this.layoutRemarks.setVisibility(hasInstructions ? View.VISIBLE : View.GONE);
        this.textViewInstructionsTitle.setVisibility(hasInstructions ? View.VISIBLE : View.GONE);
        this.textViewInstructions.setVisibility(hasInstructions ? View.VISIBLE : View.GONE);

        this.layoutPeople.setVisibility(hasPeople ? View.VISIBLE : View.GONE);
        this.layoutVehicles.setVisibility(hasVehicles ? View.VISIBLE : View.GONE);
        this.layoutMaterials.setVisibility(hasMaterials ? View.VISIBLE : View.GONE);
        this.layoutExtra.setVisibility(hasExtra ? View.VISIBLE : View.GONE);

        this.buttonStartStop.setVisibility(showStartStop ? View.VISIBLE : View.GONE);
    }

    private void addResourceView(ViewGroup parent, String imageUrl, String name, int placeholderResId)
    {
        View resourceView = LayoutInflater.from(parent.getContext()).inflate(R.layout.listitem_planning_resource, parent, false);
        ImageView imageViewIcon = (ImageView) resourceView.findViewById(R.id.imageView_icon);
        TextView textViewName = (TextView) resourceView.findViewById(R.id.textView_name);

        ImageUtils.loadImage(imageViewIcon, imageUrl, placeholderResId);
        textViewName.setText(name);

        parent.addView(resourceView);
    }

    private void removeResourceViews(ViewGroup parent)
    {
        int resourceViewCount = parent.getChildCount() - 1;
        if(resourceViewCount > 0)
        {
            parent.removeViews(1, resourceViewCount);
        }
    }

    private void notifyCustomer(Context context)
    {
        if(this.planningItem != null)
        {
            this.progressDialog = new MaterialDialog.Builder(context)
                    .content(R.string.detail_notify_progress)
                    .progress(true, 0)
                    .cancelable(false)
                    .show();

            String accountancyName = this.planningItem.getAccountancyName();
            String address = this.planningItem.getAddress();
            String phone = this.planningItem.getPhone();
            ApiHelper.notifyCustomer(context, this.planningId, accountancyName, address, phone).subscribe(this.notifyObserver);
        }
    }

    private Observer<Object> notifyObserver = new Observer<Object>()
    {
        @Override
        public void onCompleted()
        {
            if(PlanningDetailFragment.this.progressDialog != null)
            {
                PlanningDetailFragment.this.progressDialog.dismiss();
            }

            if(PlanningDetailFragment.this.getContext() != null)
            {
                Toast.makeText(PlanningDetailFragment.this.getContext(), R.string.detail_notify_success, Toast.LENGTH_LONG).show();
            }
        }

        @Override
        public void onError(Throwable e)
        {
            if(PlanningDetailFragment.this.progressDialog != null)
            {
                PlanningDetailFragment.this.progressDialog.dismiss();
            }

            if(PlanningDetailFragment.this.getContext() != null)
            {
                Toast.makeText(PlanningDetailFragment.this.getContext(), R.string.detail_notify_error, Toast.LENGTH_LONG).show();
            }
        }
    };

    private LoaderManager.LoaderCallbacks<Location> locationLoaderCallbacks = new LoaderManager.LoaderCallbacks<Location>()
    {
        @Override
        public Loader<Location> onCreateLoader(int id, Bundle args)
        {
            if(PlanningDetailFragment.this.getView() != null)
            {
                return new LocationLoader(PlanningDetailFragment.this.getView().getContext(), LOCATION_INTERVAL);
            }

            return null;
        }

        @Override
        public void onLoadFinished(Loader<Location> loader, Location data)
        {
            switch (((LocationLoader) loader).getErrorReason())
            {
                case LocationLoader.ERROR_NO_ERROR:
                    PlanningDetailFragment.this.currentLocation = data;
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
}
