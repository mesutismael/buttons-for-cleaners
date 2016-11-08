package be.appreciate.buttonsforcleaners.activities;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import be.appreciate.buttonsforcleaners.R;
import be.appreciate.buttonsforcleaners.fragments.ProductCategoryFragment;
import be.appreciate.buttonsforcleaners.fragments.ProductFragment;
import be.appreciate.buttonsforcleaners.utils.ProductResultHandler;

/**
 * Created by Inneke De Clippel on 22/03/2016.
 */
public class ProductCategoryActivity extends AppCompatActivity implements ProductCategoryFragment.ItemClickListener,
        ProductResultHandler
{
    private boolean twoPane;
    private int planningId;

    private static final int REQUEST_PRODUCT = 1;
    private static final String KEY_PLANNING_ID = "planning_id";

    public static Intent getIntent(Context context, int planningId)
    {
        Intent intent = new Intent(context, ProductCategoryActivity.class);
        intent.putExtra(KEY_PLANNING_ID, planningId);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        this.setRequestedOrientation(this.getResources().getBoolean(R.bool.tablet) ? ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE : ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        this.setContentView(R.layout.activity_product_category);

        Toolbar toolbar = (Toolbar) this.findViewById(R.id.toolbar);
        this.setSupportActionBar(toolbar);
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        this.getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close);

        this.twoPane = this.findViewById(R.id.frameLayout_product) != null;
        this.planningId = this.getIntent().getIntExtra(KEY_PLANNING_ID, 0);

        this.getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.frameLayout_category, ProductCategoryFragment.newInstance(this.planningId, this.twoPane))
                .commit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case android.R.id.home:
                this.onBackPressed();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onItemClicked(String categoryName)
    {
        if(this.twoPane)
        {
            this.getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.frameLayout_product, ProductFragment.newInstance(categoryName, this.planningId, this.twoPane))
                    .commit();
        }
        else
        {
            this.startActivityForResult(ProductActivity.getIntent(this, categoryName, this.planningId), REQUEST_PRODUCT);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode)
        {
            case REQUEST_PRODUCT:
                this.setResult(resultCode, data);
                this.finish();
                break;
        }
    }

    @Override
    public void finishWithSuccess(int productId, int productType, int amount)
    {
        Intent resultIntent = new Intent();
        resultIntent.putExtra(RESULT_PRODUCT_ID, productId);
        resultIntent.putExtra(RESULT_PRODUCT_TYPE, productType);
        resultIntent.putExtra(RESULT_AMOUNT, amount);
        this.setResult(RESULT_OK, resultIntent);
        this.finish();
    }
}
