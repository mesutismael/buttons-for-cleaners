package be.appreciate.buttonsforcleaners.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import be.appreciate.buttonsforcleaners.R;

/**
 * Created by Inneke De Clippel on 22/03/2016.
 */
public class ProductCategoryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
{
    private List<String> categories;
    private boolean specificCategory;
    private OnItemClickListener listener;
    private boolean keepItemSelected;
    private int selectedPosition;

    private static final int TYPE_CATEGORY = 1;

    public ProductCategoryAdapter(boolean keepItemSelected)
    {
        this.keepItemSelected = keepItemSelected;
        this.selectedPosition = -1;
    }

    public void setCategories(List<String> categories)
    {
        this.categories = categories;
        this.notifyDataSetChanged();
    }

    public void setSpecificCategory(boolean specificCategory)
    {
        if(this.specificCategory != specificCategory)
        {
            this.specificCategory = specificCategory;
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
        return TYPE_CATEGORY;
    }

    @Override
    public int getItemCount()
    {
        int specificCount = this.specificCategory ? 1 : 0;
        int categoryCount = this.categories != null ? this.categories.size() : 0;
        return specificCount + categoryCount;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        switch (viewType)
        {
            case TYPE_CATEGORY:
                View viewCategory = LayoutInflater.from(parent.getContext()).inflate(R.layout.listitem_product_category, parent, false);
                return new CategoryViewHolder(viewCategory);

            default:
                return null;
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position)
    {
        if(holder instanceof CategoryViewHolder)
        {
            boolean selected = this.keepItemSelected && position == this.selectedPosition;
            boolean specific = position == 0 && this.specificCategory;
            String category = specific ? null : this.categories.get(this.specificCategory ? position - 1 : position);
            ((CategoryViewHolder) holder).bind(position, selected, category);
        }
    }

    public class CategoryViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {
        private TextView textViewName;
        private int position;
        private String categoryName;

        public CategoryViewHolder(View itemView)
        {
            super(itemView);

            this.textViewName = (TextView) itemView.findViewById(R.id.textView_name);

            itemView.setOnClickListener(this);
        }

        public void bind(int position, boolean selected, String categoryName)
        {
            this.position = position;
            this.categoryName = categoryName;

            if(categoryName == null)
            {
                this.textViewName.setText(R.string.product_category_specific);
            }
            else
            {
                this.textViewName.setText(categoryName);
            }
            this.itemView.setActivated(selected);
        }

        @Override
        public void onClick(View v)
        {
            ProductCategoryAdapter.this.setSelectedPosition(this.position);

            if(ProductCategoryAdapter.this.listener != null)
            {
                ProductCategoryAdapter.this.listener.onCategoryClick(v, this.categoryName);
            }
        }
    }

    public interface OnItemClickListener
    {
        void onCategoryClick(View caller, String categoryName);
    }
}
