package be.appreciate.buttonsforcleaners.fragments;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;

import java.util.List;

import be.appreciate.buttonsforcleaners.R;
import be.appreciate.buttonsforcleaners.adapters.ProductAdapter;
import be.appreciate.buttonsforcleaners.contentproviders.PlanningProductContentProvider;
import be.appreciate.buttonsforcleaners.contentproviders.ProductContentProvider;
import be.appreciate.buttonsforcleaners.database.PlanningProductTable;
import be.appreciate.buttonsforcleaners.database.ProductTable;
import be.appreciate.buttonsforcleaners.decorations.DividerDecoration;
import be.appreciate.buttonsforcleaners.model.Product;
import be.appreciate.buttonsforcleaners.utils.Constants;
import be.appreciate.buttonsforcleaners.utils.ProductResultHandler;
import be.appreciate.buttonsforcleaners.utils.TextUtils;

/**
 * Created by Inneke De Clippel on 22/03/2016.
 */
public class ProductFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>, ProductAdapter.OnItemClickListener
{
    private ProductAdapter adapter;
    private String categoryName;
    private int planningId;
    private boolean twoPane;

    private static final String KEY_CATEGORY_NAME = "category_name";
    private static final String KEY_PLANNING_ID = "planning_id";
    private static final String KEY_TWO_PANE = "two_pane";

    public static ProductFragment newInstance(String categoryName, int planningId, boolean twoPane)
    {
        Bundle bundle = new Bundle();
        bundle.putString(KEY_CATEGORY_NAME, categoryName);
        bundle.putInt(KEY_PLANNING_ID, planningId);
        bundle.putBoolean(KEY_TWO_PANE, twoPane);

        ProductFragment fragment = new ProductFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_product, container, false);

        TextView textViewCategory = (TextView) view.findViewById(R.id.textView_category);
        RecyclerView recyclerViewProducts = (RecyclerView) view.findViewById(R.id.recyclerView_products);

        this.categoryName = this.getArguments().getString(KEY_CATEGORY_NAME);
        this.planningId = this.getArguments().getInt(KEY_PLANNING_ID);
        this.twoPane = this.getArguments().getBoolean(KEY_TWO_PANE);

        String title = this.categoryName != null ? this.categoryName : this.getString(R.string.product_category_specific);

        textViewCategory.setText(title);

        if(this.getActivity() != null && !this.twoPane)
        {
            this.getActivity().setTitle(title);
        }

        this.adapter = new ProductAdapter();
        this.adapter.setListener(this);
        recyclerViewProducts.setAdapter(this.adapter);
        RecyclerView.LayoutManager layoutManager = this.getResources().getBoolean(R.bool.tablet) ? new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL) : new LinearLayoutManager(view.getContext());
        recyclerViewProducts.setLayoutManager(layoutManager);
        DividerDecoration dividerDecoration = new DividerDecoration(view.getContext());
        dividerDecoration.setPaddingLeftRes(R.dimen.product_divider_padding_left);
        recyclerViewProducts.addItemDecoration(dividerDecoration);

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        this.getLoaderManager().initLoader(Constants.LOADER_PRODUCTS, null, this);
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
                boolean genericProduct = this.categoryName != null;

                Uri uri = genericProduct ? ProductContentProvider.CONTENT_URI : PlanningProductContentProvider.CONTENT_URI;
                String selection = genericProduct
                        ? ProductTable.TABLE_NAME + "." + ProductTable.COLUMN_CATEGORY + " = ?"
                        : PlanningProductTable.TABLE_NAME + "." + PlanningProductTable.COLUMN_PLANNING_ID + " = ?";
                String[] selectionArgs = new String[]{genericProduct
                        ? this.categoryName.replace("'", "\'")
                        : String.valueOf(this.planningId)};
                String sortOrder = genericProduct
                        ? ProductTable.TABLE_NAME + "." + ProductTable.COLUMN_NAME
                        : PlanningProductTable.TABLE_NAME + "." + PlanningProductTable.COLUMN_NAME;

                return new CursorLoader(this.getView().getContext(), uri, null, selection, selectionArgs, sortOrder);

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
                List<Product> products = Product.constructListFromCursor(data);
                this.adapter.setProducts(products);
                break;
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader)
    {
    }

    @Override
    public void onProductClick(View caller, Product product)
    {
        this.showAmountDialog(caller.getContext(), product);
    }

    private void showAmountDialog(Context context, Product product)
    {
        if(product == null)
        {
            return;
        }

        View view = LayoutInflater.from(context).inflate(R.layout.dialog_input_amount, null);

        EditText editTextAmount = (EditText) view.findViewById(R.id.editText_input);
        Button buttonIncrease = (Button) view.findViewById(R.id.button_increase);
        Button buttonDecrease = (Button) view.findViewById(R.id.button_decrease);

        editTextAmount.setText(String.valueOf(1));
        editTextAmount.setHint(R.string.product_amount_hint);
        editTextAmount.setSingleLine();
        editTextAmount.setInputType(InputType.TYPE_CLASS_NUMBER);

        buttonIncrease.setOnClickListener(v ->
        {
            int enteredAmount = TextUtils.getIntegerFromEditText(editTextAmount);
            editTextAmount.setText(String.valueOf(enteredAmount + 1));
            editTextAmount.setSelection(editTextAmount.getText().length());
        });

        buttonDecrease.setOnClickListener(v ->
        {
            int enteredAmount = TextUtils.getIntegerFromEditText(editTextAmount);
            if(enteredAmount > 0)
            {
                editTextAmount.setText(String.valueOf(enteredAmount - 1));
                editTextAmount.setSelection(editTextAmount.getText().length());
            }
        });

        new MaterialDialog.Builder(context)
                .title(R.string.product_amount_title)
                .customView(view, false)
                .positiveText(R.string.dialog_positive)
                .showListener(dialog ->
                {
                    editTextAmount.requestFocus();

                    if (this.getContext() != null)
                    {
                        InputMethodManager imm = (InputMethodManager) this.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                        if (imm != null)
                        {
                            imm.showSoftInput(editTextAmount, InputMethodManager.SHOW_FORCED);
                        }

                        if (editTextAmount.getText().length() > 0)
                        {
                            editTextAmount.setSelection(editTextAmount.getText().length());
                        }
                    }
                })
                .dismissListener(dialog ->
                {
                    if (this.getContext() != null)
                    {
                        InputMethodManager imm = (InputMethodManager) this.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                        if (imm != null)
                        {
                            imm.hideSoftInputFromWindow(editTextAmount.getWindowToken(), 0);
                        }
                    }
                })
                .onPositive((dialog, which) ->
                {
                    int amount = TextUtils.getIntegerFromEditText(editTextAmount);
                    if (this.getActivity() != null && this.getActivity() instanceof ProductResultHandler)
                    {
                        ((ProductResultHandler) this.getActivity()).finishWithSuccess(product.getId(), product.getType(), amount);
                    }
                })
                .show();
    }
}
