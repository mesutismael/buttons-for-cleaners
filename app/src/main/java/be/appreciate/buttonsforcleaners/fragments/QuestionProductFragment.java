package be.appreciate.buttonsforcleaners.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;

import java.util.ArrayList;
import java.util.List;

import be.appreciate.buttonsforcleaners.R;
import be.appreciate.buttonsforcleaners.activities.ProductCategoryActivity;
import be.appreciate.buttonsforcleaners.adapters.QuestionProductAdapter;
import be.appreciate.buttonsforcleaners.contentproviders.PlanningProductContentProvider;
import be.appreciate.buttonsforcleaners.contentproviders.ProductContentProvider;
import be.appreciate.buttonsforcleaners.model.Product;
import be.appreciate.buttonsforcleaners.model.QuestionProduct;
import be.appreciate.buttonsforcleaners.utils.Constants;
import be.appreciate.buttonsforcleaners.utils.ProductHelper;
import be.appreciate.buttonsforcleaners.utils.ProductResultHandler;
import be.appreciate.buttonsforcleaners.utils.TextUtils;

/**
 * Created by Inneke De Clippel on 16/03/2016.
 */
public class QuestionProductFragment extends QuestionFragment implements QuestionProductAdapter.OnItemClickListener
{
    private TextView textViewProgress;
    private TextView textViewQuestion;
    private QuestionProductAdapter adapter;
    private List<QuestionProduct> selectedProducts;

    private static final int REQUEST_PRODUCT = 1;
    private static final String KEY_SELECTION = "selection";

    public static QuestionProductFragment newInstance(int questionId, int planningId, int currentQuestion, int totalQuestions)
    {
        QuestionProductFragment fragment = new QuestionProductFragment();
        fragment.setArguments(QuestionFragment.createBundle(questionId, planningId, currentQuestion, totalQuestions));
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_question_product, container, false);

        this.textViewProgress = (TextView) view.findViewById(R.id.textView_progress);
        this.textViewQuestion = (TextView) view.findViewById(R.id.textView_question);
        RecyclerView recyclerViewProducts = (RecyclerView) view.findViewById(R.id.recyclerView_products);

        this.adapter = new QuestionProductAdapter();
        this.adapter.setListener(this);
        recyclerViewProducts.setAdapter(this.adapter);

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        this.textViewProgress.setText(this.getString(R.string.feedback_progress, this.currentQuestion, this.totalQuestions));
    }

    @Override
    protected void updateLayout()
    {
        if(this.question != null)
        {
            this.selectedProducts = ProductHelper.convertToProducts(this.question.getEnteredAnswers());

            this.textViewQuestion.setText(this.question.getQuestion());

            if(ProductHelper.containsProducts(this.selectedProducts, Product.TYPE_GENERIC))
            {
                this.getLoaderManager().restartLoader(Constants.LOADER_ANSWER_GENERIC_PRODUCTS, this.createProductBundle(Product.TYPE_GENERIC), this);
            }

            if(ProductHelper.containsProducts(this.selectedProducts, Product.TYPE_PLANNING))
            {
                this.getLoaderManager().restartLoader(Constants.LOADER_ANSWER_PLANNING_PRODUCTS, this.createProductBundle(Product.TYPE_PLANNING), this);
            }
        }
    }

    @Override
    public List<String> getEnteredAnswers()
    {
        return ProductHelper.convertToAnswers(this.selectedProducts);
    }

    @Override
    public boolean isAnswerRequired()
    {
        return false;
    }

    @Override
    public void onAddClicked(View caller)
    {
        this.startActivityForResult(ProductCategoryActivity.getIntent(caller.getContext(), this.planningId), REQUEST_PRODUCT);
    }

    @Override
    public void onProductClicked(View caller, QuestionProduct product)
    {
        switch (caller.getId())
        {
            case R.id.textView_amount:
                if(product != null)
                {
                    this.showAmountDialog(caller.getContext(), product);
                }
                break;

            case R.id.imageView_delete:
                if(product != null)
                {
                    this.adapter.removeProduct(product);

                    if(this.selectedProducts != null)
                    {
                        this.selectedProducts.remove(product);
                    }
                }
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode)
        {
            case REQUEST_PRODUCT:
                if(resultCode == Activity.RESULT_OK && data != null)
                {
                    int productId = data.getIntExtra(ProductResultHandler.RESULT_PRODUCT_ID, 0);
                    int productType = data.getIntExtra(ProductResultHandler.RESULT_PRODUCT_TYPE, 0);
                    int amount = data.getIntExtra(ProductResultHandler.RESULT_AMOUNT, 0);

                    if(this.selectedProducts == null)
                    {
                        this.selectedProducts = new ArrayList<>();
                    }
                    ProductHelper.addProduct(this.selectedProducts, productId, productType, amount);

                    int loaderId = productType == Product.TYPE_PLANNING ? Constants.LOADER_ANSWER_PLANNING_PRODUCTS : Constants.LOADER_ANSWER_GENERIC_PRODUCTS;
                    this.getLoaderManager().restartLoader(loaderId, this.createProductBundle(productType), this);
                }
                break;
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args)
    {
        switch (id)
        {
            case Constants.LOADER_ANSWER_GENERIC_PRODUCTS:
                if(this.getContext() != null)
                {
                    String selection = args != null ? args.getString(KEY_SELECTION, null) : null;
                    return new CursorLoader(this.getContext(), ProductContentProvider.CONTENT_URI, null, selection, null, null);
                }
                return null;

            case Constants.LOADER_ANSWER_PLANNING_PRODUCTS:
                if(this.getContext() != null)
                {
                    String selection = args != null ? args.getString(KEY_SELECTION, null) : null;
                    return new CursorLoader(this.getContext(), PlanningProductContentProvider.CONTENT_URI, null, selection, null, null);
                }
                return null;

            default:
                return super.onCreateLoader(id, args);
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data)
    {
        switch (loader.getId())
        {
            case Constants.LOADER_ANSWER_GENERIC_PRODUCTS:
            case Constants.LOADER_ANSWER_PLANNING_PRODUCTS:
                List<Product> products = Product.constructListFromCursor(data);
                ProductHelper.updateProducts(this.selectedProducts, products);
                this.adapter.setProducts(this.selectedProducts);
                break;

            default:
                super.onLoadFinished(loader, data);
        }
    }

    private Bundle createProductBundle(int type)
    {
        Bundle bundle = new Bundle();
        bundle.putString(KEY_SELECTION, ProductHelper.createProductSelection(this.selectedProducts, type));
        return bundle;
    }

    private void showAmountDialog(Context context, QuestionProduct product)
    {
        if(product == null)
        {
            return;
        }

        View view = LayoutInflater.from(context).inflate(R.layout.dialog_input_amount, null);

        EditText editTextAmount = (EditText) view.findViewById(R.id.editText_input);
        Button buttonIncrease = (Button) view.findViewById(R.id.button_increase);
        Button buttonDecrease = (Button) view.findViewById(R.id.button_decrease);

        editTextAmount.setText(String.valueOf(product.getAmount()));
        editTextAmount.setHint(R.string.feedback_product_amount_hint);
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
                .title(R.string.feedback_product_amount_title)
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
                    int newAmount = TextUtils.getIntegerFromEditText(editTextAmount);
                    if (newAmount != product.getAmount())
                    {
                        this.adapter.updateAmount(product, newAmount);
                        product.setAmount(newAmount);
                    }
                })
                .show();
    }
}
