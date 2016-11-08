package be.appreciate.buttonsforcleaners.adapters;

import android.location.Location;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import be.appreciate.buttonsforcleaners.R;
import be.appreciate.buttonsforcleaners.model.PlanningItem;
import be.appreciate.buttonsforcleaners.utils.ImageUtils;
import be.appreciate.buttonsforcleaners.utils.LocationUtils;

/**
 * Created by Inneke De Clippel on 26/02/2016.
 */
public class PlanningAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
{
    private List<PlanningItem> planningItems;
    private OnItemClickListener listener;
    private boolean keepItemSelected;
    private int selectedPosition;
    private Location currentLocation;

    private static final int TYPE_PLANNING = 1;

    public PlanningAdapter(boolean keepItemSelected)
    {
        this.keepItemSelected = keepItemSelected;
        this.selectedPosition = -1;
    }

    public void setPlanningItems(List<PlanningItem> planningItems)
    {
        this.planningItems = planningItems;
        this.notifyDataSetChanged();
    }

    public void setCurrentLocation(Location currentLocation)
    {
        if(LocationUtils.isDifferentLocation(this.currentLocation, currentLocation))
        {
            this.currentLocation = currentLocation;
            this.notifyDataSetChanged();
        }
    }

    public void setListener(OnItemClickListener listener)
    {
        this.listener = listener;
    }

    public void setSelectedPosition(int selectedPosition)
    {
        if(this.selectedPosition >= 0 && this.selectedPosition < this.getItemCount())
        {
            this.notifyItemChanged(this.selectedPosition);
        }

        this.selectedPosition = selectedPosition;

        if(this.selectedPosition >= 0 && this.selectedPosition < this.getItemCount())
        {
            this.notifyItemChanged(this.selectedPosition);
        }
    }

    @Override
    public int getItemViewType(int position)
    {
        return TYPE_PLANNING;
    }

    @Override
    public int getItemCount()
    {
        return this.planningItems != null ? this.planningItems.size() : 0;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        switch (viewType)
        {
            case TYPE_PLANNING:
                View viewPlanning = LayoutInflater.from(parent.getContext()).inflate(R.layout.listitem_planning, parent, false);
                return new PlanningViewHolder(viewPlanning);

            default:
                return null;
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position)
    {
        if(holder instanceof PlanningViewHolder)
        {
            boolean selected = this.keepItemSelected && position == this.selectedPosition;
            PlanningItem planningItem = this.planningItems.get(position);
            ((PlanningViewHolder) holder).bind(position, selected, planningItem, this.currentLocation);
        }
    }

    public class PlanningViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {
        private TextView textViewTitle;
        private TextView textViewTime;
        private TextView textViewAddress;
        private TextView textViewDistance;
        private ImageView imageViewAlert;
        private ImageView imageViewCompleted;
        private int position;
        private PlanningItem planningItem;

        public PlanningViewHolder(View itemView)
        {
            super(itemView);

            this.textViewTitle = (TextView) itemView.findViewById(R.id.textView_title);
            this.textViewTime = (TextView) itemView.findViewById(R.id.textView_time);
            this.textViewAddress = (TextView) itemView.findViewById(R.id.textView_address);
            this.textViewDistance = (TextView) itemView.findViewById(R.id.textView_distance);
            this.imageViewAlert = (ImageView) itemView.findViewById(R.id.imageView_alert);
            this.imageViewCompleted = (ImageView) itemView.findViewById(R.id.imageView_completed);

            ImageUtils.tintIcon(this.imageViewAlert, R.color.planning_list_item_icon);
            ImageUtils.tintIcon(this.imageViewCompleted, R.color.planning_list_item_completed);

            this.imageViewAlert.setOnClickListener(this);
            itemView.setOnClickListener(this);
        }

        public void bind(int position, boolean selected, PlanningItem planningItem, Location currentLocation)
        {
            this.position = position;
            this.planningItem = planningItem;

            String title = planningItem.getLocationName();
            String time = this.itemView.getContext().getString(R.string.planning_list_item_time, planningItem.getStartTime(), planningItem.getEndTime());
            String address = planningItem.getAddress();
            String distance = LocationUtils.getFormattedDistance(this.itemView.getContext(), currentLocation, planningItem.getLatitude(), planningItem.getLongitude());
            boolean showAlert = planningItem.isEndCompleted() && !planningItem.isSentToApi();
            boolean showCompleted = planningItem.isEndCompleted() && planningItem.isSentToApi();

            this.textViewTitle.setText(title);
            this.textViewTitle.setVisibility(TextUtils.isEmpty(title) ? View.GONE : View.VISIBLE);
            this.textViewTime.setText(time);
            this.textViewTime.setVisibility(TextUtils.isEmpty(time) ? View.GONE : View.VISIBLE);
            this.textViewAddress.setText(address);
            this.textViewAddress.setVisibility(TextUtils.isEmpty(address) ? View.GONE : View.VISIBLE);
            this.textViewDistance.setText(distance);
            this.textViewDistance.setVisibility(TextUtils.isEmpty(distance) ? View.GONE : View.VISIBLE);
            this.imageViewAlert.setVisibility(showAlert ? View.VISIBLE : View.GONE);
            this.imageViewCompleted.setVisibility(showCompleted ? View.VISIBLE : View.GONE);

            this.itemView.setActivated(selected);
        }

        @Override
        public void onClick(View v)
        {
            PlanningAdapter.this.setSelectedPosition(this.position);

            if(PlanningAdapter.this.listener != null)
            {
                PlanningAdapter.this.listener.onPlanningClick(v, this.planningItem);
            }
        }
    }

    public interface OnItemClickListener
    {
        void onPlanningClick(View caller, PlanningItem planningItem);
    }
}
