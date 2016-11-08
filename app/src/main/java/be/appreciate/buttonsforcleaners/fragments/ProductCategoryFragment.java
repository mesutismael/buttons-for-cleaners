package be.appreciate.buttonsforcleaners.fragments;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import be.appreciate.buttonsforcleaners.R;
import be.appreciate.buttonsforcleaners.adapters.ProductCategoryAdapter;
import be.appreciate.buttonsforcleaners.contentproviders.PlanningProductContentProvider;
import be.appreciate.buttonsforcleaners.contentproviders.ProductContentProvider;
import be.appreciate.buttonsforcleaners.database.PlanningProductTable;
import be.appreciate.buttonsforcleaners.database.ProductTable;
import be.appreciate.buttonsforcleaners.decorations.DividerDecoration;
import be.appreciate.buttonsforcleaners.utils.Constants;

/**
 * Created by Inneke De Clippel on 22/03/2016.
 */
public class ProductCategoryFragment extends Fragment implements ProductCategoryAdapter.OnItemClickListener, LoaderManager.LoaderCallbacks<Cursor>
{
    private ItemClickListener listener;
    private ProductCategoryAdapter adapter;
    private int planningId;

    private static final String KEY_PLANNING_ID = "planning_id";
    private static final String KEY_KEEP_ITEM_SELECTED = "keep_item_selected";

    public static ProductCategoryFragment newInstance(int planningId, boolean keepItemSelected)
    {
        Bundle bundle = new Bundle();
        bundle.putInt(KEY_PLANNING_ID, planningId);
        bundle.putBoolean(KEY_KEEP_ITEM_SELECTED, keepItemSelected);

        ProductCategoryFragment fragment = new ProductCategoryFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_product_category, container, false);

        RecyclerView recyclerViewCategories = (RecyclerView) view.findViewById(R.id.recyclerView_categories);

        boolean keepItemSelected = this.getArguments().getBoolean(KEY_KEEP_ITEM_SELECTED);
        this.planningId = this.getArguments().getInt(KEY_PLANNING_ID);

        this.adapter = new ProductCategoryAdapter(keepItemSelected);
        this.adapter.setListener(this);
        recyclerViewCategories.setAdapter(this.adapter);
        DividerDecoration dividerDecoration = new DividerDecoration(view.getContext());
        recyclerViewCategories.addItemDecoration(dividerDecoration);

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        this.getLoaderManager().initLoader(Constants.LOADER_PRODUCTS, null, this);
        this.getLoaderManager().initLoader(Constants.LOADER_PRODUCTS_PLANNING, null, this);
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
    public Loader<Cursor> onCreateLoader(int id, Bundle args)
    {
        if(this.getView() == null)
        {
            return null;
        }

        switch (id)
        {
            case Constants.LOADER_PRODUCTS:
                String sortOrder = ProductTable.TABLE_NAME + "." + ProductTable.COLUMN_CATEGORY;
                return new CursorLoader(this.getView().getContext(), ProductContentProvider.CONTENT_URI_CATEGORIES, null, null, null, sortOrder);

            case Constants.LOADER_PRODUCTS_PLANNING:
                String selection = PlanningProductTable.TABLE_NAME + "." + PlanningProductTable.COLUMN_PLANNING_ID + " = " + this.planningId;
                return new CursorLoader(this.getView().getContext(), PlanningProductContentProvider.CONTENT_URI, null, selection, null, null);

            default:
                return null;
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data)
    {
        switch (loader.getId())
        {
            case Constants.LOADER_PRODUCTS:
                List<String> categories = new ArrayList<>();

                if(data != null && data.moveToFirst())
                {
                    do
                    {
                        categories.add(data.getString(data.getColumnIndex(ProductTable.COLUMN_CATEGORY_FULL)));
                    }
                    while (data.moveToNext());
                }

                this.adapter.setCategories(categories);
                break;

            case Constants.LOADER_PRODUCTS_PLANNING:
                boolean hasPlanningProducts = data != null && data.getCount() > 0;
                this.adapter.setSpecificCategory(hasPlanningProducts);
                break;
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader)
    {
    }

    @Override
    public void onCategoryClick(View caller, String categoryName)
    {
        if(this.listener != null)
        {
            this.listener.onItemClicked(categoryName);
        }
    }

    public interface ItemClickListener
    {
        void onItemClicked(String categoryName);
    }
}
