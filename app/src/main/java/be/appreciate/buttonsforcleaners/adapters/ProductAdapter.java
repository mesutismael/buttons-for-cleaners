package be.appreciate.buttonsforcleaners.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import be.appreciate.buttonsforcleaners.R;
import be.appreciate.buttonsforcleaners.model.Product;
import be.appreciate.buttonsforcleaners.utils.ImageUtils;

/**
 * Created by Inneke De Clippel on 22/03/2016.
 */
public class ProductAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
{
    private List<Product> products;
    private OnItemClickListener listener;

    private static final int TYPE_PRODUCT = 1;

    public ProductAdapter()
    {
    }

    public void setProducts(List<Product> products)
    {
        this.products = products;
        this.notifyDataSetChanged();
    }

    public void setListener(OnItemClickListener listener)
    {
        this.listener = listener;
    }

    @Override
    public int getItemViewType(int position)
    {
        return TYPE_PRODUCT;
    }

    @Override
    public int getItemCount()
    {
        return this.products != null ? this.products.size() : 0;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        switch (viewType)
        {
            case TYPE_PRODUCT:
                View viewProduct = LayoutInflater.from(parent.getContext()).inflate(R.layout.listitem_product, parent, false);
                return new ProductViewHolder(viewProduct);

            default:
                return null;
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position)
    {
        if(holder instanceof ProductViewHolder)
        {
            Product product = this.products.get(position);
            ((ProductViewHolder) holder).bind(product);
        }
    }

    public class ProductViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {
        private ImageView imageViewIcon;
        private TextView textViewName;
        private Product product;

        public ProductViewHolder(View itemView)
        {
            super(itemView);

            this.imageViewIcon = (ImageView) itemView.findViewById(R.id.imageView_icon);
            this.textViewName = (TextView) itemView.findViewById(R.id.textView_name);

            itemView.setOnClickListener(this);
        }

        public void bind(Product product)
        {
            this.product = product;

            ImageUtils.loadImage(this.imageViewIcon, product.getImageUrl(), R.drawable.placeholder_product);
            this.textViewName.setText(product.getName());
        }

        @Override
        public void onClick(View v)
        {
            if(ProductAdapter.this.listener != null)
            {
                ProductAdapter.this.listener.onProductClick(v, this.product);
            }
        }
    }

    public interface OnItemClickListener
    {
        void onProductClick(View caller, Product product);
    }
}
