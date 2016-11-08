package be.appreciate.buttonsforcleaners.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import be.appreciate.buttonsforcleaners.R;
import be.appreciate.buttonsforcleaners.model.QuestionProduct;
import be.appreciate.buttonsforcleaners.utils.ImageUtils;

/**
 * Created by Inneke De Clippel on 16/03/2016.
 */
public class QuestionProductAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
{
    private List<QuestionProduct> products;
    private OnItemClickListener listener;

    private static final int TYPE_PRODUCT = 1;
    private static final int TYPE_ADD = 2;

    public QuestionProductAdapter()
    {
    }

    public void setListener(OnItemClickListener listener)
    {
        this.listener = listener;
    }

    public void setProducts(List<QuestionProduct> products)
    {
        this.products = products;
        this.notifyDataSetChanged();
    }

    public void removeProduct(QuestionProduct product)
    {
        if(this.products != null && product != null)
        {
            int productIndex = this.products.indexOf(product);

            if(productIndex >= 0)
            {
                this.products.remove(product);
                this.notifyItemRemoved(productIndex + 1);
            }
        }
    }

    public void updateAmount(QuestionProduct product, int amount)
    {
        if(this.products != null && product != null)
        {
            int productIndex = this.products.indexOf(product);

            if(productIndex >= 0)
            {
                product.setAmount(amount);
                this.notifyItemChanged(productIndex + 1);
            }
        }
    }

    @Override
    public int getItemViewType(int position)
    {
        return position == 0 ? TYPE_ADD : TYPE_PRODUCT;
    }

    @Override
    public int getItemCount()
    {
        int productCount = this.products != null ? this.products.size() : 0;
        return productCount + 1;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        switch (viewType)
        {
            case TYPE_PRODUCT:
                View viewProduct = LayoutInflater.from(parent.getContext()).inflate(R.layout.listitem_question_product, parent, false);
                return new ProductViewHolder(viewProduct);

            case TYPE_ADD:
                View viewAdd = LayoutInflater.from(parent.getContext()).inflate(R.layout.listitem_question_product_add, parent, false);
                return new AddViewHolder(viewAdd);

            default:
                return null;
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position)
    {
        if(holder instanceof ProductViewHolder)
        {
            QuestionProduct product = this.products.get(position - 1);
            ((ProductViewHolder) holder).bind(product);
        }
    }

    public class ProductViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {
        private ImageView imageViewIcon;
        private TextView textViewName;
        private TextView textViewAmount;
        private QuestionProduct product;

        public ProductViewHolder(View itemView)
        {
            super(itemView);

            this.imageViewIcon = (ImageView) itemView.findViewById(R.id.imageView_icon);
            this.textViewName = (TextView) itemView.findViewById(R.id.textView_name);
            this.textViewAmount = (TextView) itemView.findViewById(R.id.textView_amount);
            ImageView imageViewDelete = (ImageView) itemView.findViewById(R.id.imageView_delete);

            ImageUtils.tintIcon(imageViewDelete, R.color.feedback_product_delete_icon);

            this.textViewAmount.setOnClickListener(this);
            imageViewDelete.setOnClickListener(this);
        }

        public void bind(QuestionProduct product)
        {
            this.product = product;

            String imageUrl = product != null && product.getProduct() != null ? product.getProduct().getImageUrl() : null;
            String name = product != null && product.getProduct() != null ? product.getProduct().getName() : null;
            int amount = product != null ? product.getAmount() : 0;

            ImageUtils.loadImage(this.imageViewIcon, imageUrl);
            this.textViewName.setText(name);
            this.textViewAmount.setText(String.valueOf(amount));
        }

        @Override
        public void onClick(View v)
        {
            if(QuestionProductAdapter.this.listener != null)
            {
                QuestionProductAdapter.this.listener.onProductClicked(v, this.product);
            }
        }
    }

    public class AddViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {
        public AddViewHolder(View itemView)
        {
            super(itemView);

            ImageView imageViewAdd = (ImageView) itemView.findViewById(R.id.imageView_add);

            ImageUtils.tintIcon(imageViewAdd, R.color.feedback_product_add_icon);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v)
        {
            if(QuestionProductAdapter.this.listener != null)
            {
                QuestionProductAdapter.this.listener.onAddClicked(v);
            }
        }
    }

    public interface OnItemClickListener
    {
        void onAddClicked(View caller);
        void onProductClicked(View caller, QuestionProduct product);
    }
}
