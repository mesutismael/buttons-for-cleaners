package be.appreciate.buttonsforcleaners.activities;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import be.appreciate.buttonsforcleaners.R;
import be.appreciate.buttonsforcleaners.fragments.ProductFragment;
import be.appreciate.buttonsforcleaners.utils.ProductResultHandler;

/**
 * Created by Inneke De Clippel on 22/03/2016.
 */
public class ProductActivity extends AppCompatActivity implements ProductResultHandler
{
    private static final String KEY_CATEGORY_NAME = "category_name";
    private static final String KEY_PLANNING_ID = "planning_id";

    public static Intent getIntent(Context context, String categoryName, int planningId)
    {
        Intent intent = new Intent(context, ProductActivity.class);
        intent.putExtra(KEY_CATEGORY_NAME, categoryName);
        intent.putExtra(KEY_PLANNING_ID, planningId);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        this.setRequestedOrientation(this.getResources().getBoolean(R.bool.tablet) ? ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE : ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        this.setContentView(R.layout.activity_product);

        Toolbar toolbar = (Toolbar) this.findViewById(R.id.toolbar);
        this.setSupportActionBar(toolbar);
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        String categoryName = this.getIntent().getStringExtra(KEY_CATEGORY_NAME);
        int planningId = this.getIntent().getIntExtra(KEY_PLANNING_ID, 0);

        this.getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.frameLayout_product, ProductFragment.newInstance(categoryName, planningId, false))
                .commit();
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
