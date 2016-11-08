package be.appreciate.buttonsforcleaners.adapters;

import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

import be.appreciate.buttonsforcleaners.R;
import be.appreciate.buttonsforcleaners.utils.ImageUtils;

/**
 * Created by Inneke De Clippel on 15/03/2016.
 */
public class QuestionPhotoAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
{
    private List<String> photoPaths;
    private OnItemClickListener listener;

    private static final int TYPE_PHOTO = 1;
    private static final int TYPE_ADD = 2;
    private static final int MAX_PHOTOS = 3;

    public QuestionPhotoAdapter()
    {
    }

    public void setListener(OnItemClickListener listener)
    {
        this.listener = listener;
    }

    public void setPhotos(List<String> photoPaths)
    {
        this.photoPaths = photoPaths != null ? new ArrayList<>(photoPaths) : null;
        this.notifyDataSetChanged();
    }

    public List<String> getPhotos()
    {
        return this.photoPaths;
    }

    public void addPhoto(String path)
    {
        if(this.getPhotoCount() < MAX_PHOTOS)
        {
            if (this.photoPaths == null)
            {
                this.photoPaths = new ArrayList<>();
            }

            int addCountBefore = this.getAddCount();
            this.photoPaths.add(path);
            int addCountAfter = this.getAddCount();

            if (addCountBefore > addCountAfter)
            {
                this.notifyItemRemoved(0);
            }

            int itemCount = this.getItemCount();
            this.notifyItemInserted(itemCount - 1);
            this.updateDecoration(itemCount - 1, true);
        }
    }

    public void removePhoto(String path)
    {
        if(this.getPhotoCount() > 0 && !TextUtils.isEmpty(path))
        {
            int photoIndex = this.photoPaths.indexOf(path);

            if(photoIndex >= 0)
            {
                int addCountBefore = this.getAddCount();
                this.photoPaths.remove(path);
                int addCountAfter = this.getAddCount();
                this.notifyItemRemoved(photoIndex + addCountBefore);
                this.updateDecoration(photoIndex + addCountBefore, false);

                if (addCountBefore < addCountAfter)
                {
                    this.notifyItemInserted(0);
                    this.updateDecoration(0, true);
                }
            }
        }
    }

    private void updateDecoration(int position, boolean inserted)
    {
        int positionLeft = position - 1;
        int positionRight = inserted ? position + 1 : position;
        int itemCount = this.getItemCount();

        if(positionLeft >= 0 && positionLeft < itemCount)
        {
            //Update the decoration of the item to the left of the inserted/removed item
            this.notifyItemChanged(positionLeft);
        }

        if(positionRight >= 0 && positionRight < itemCount)
        {
            //Update the decoration of the item to the right of the inserted/removed item
            this.notifyItemChanged(positionRight);
        }
    }

    @Override
    public int getItemViewType(int position)
    {
        return position == 0 && this.getAddCount() > 0 ? TYPE_ADD : TYPE_PHOTO;
    }

    @Override
    public int getItemCount()
    {
        return this.getPhotoCount() + this.getAddCount();
    }

    private int getPhotoCount()
    {
        return this.photoPaths != null ? this.photoPaths.size() : 0;
    }

    private int getAddCount()
    {
        return this.getPhotoCount() < MAX_PHOTOS ? 1 : 0;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        switch (viewType)
        {
            case TYPE_PHOTO:
                View viewPhoto = LayoutInflater.from(parent.getContext()).inflate(R.layout.listitem_photo, parent, false);
                return new PhotoViewHolder(viewPhoto);

            case TYPE_ADD:
                View viewAdd = LayoutInflater.from(parent.getContext()).inflate(R.layout.listitem_photo_add, parent, false);
                return new AddViewHolder(viewAdd);

            default:
                return null;
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position)
    {
        if(holder instanceof PhotoViewHolder)
        {
            String path = this.photoPaths.get(position - this.getAddCount());
            ((PhotoViewHolder) holder).bind(path);
        }
    }

    public class PhotoViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {
        private ImageView imageViewPhoto;
        private String photoPath;

        public PhotoViewHolder(View itemView)
        {
            super(itemView);

            this.imageViewPhoto = (ImageView) itemView.findViewById(R.id.imageView_photo);
            ImageView imageViewDelete = (ImageView) itemView.findViewById(R.id.imageView_delete);

            ImageUtils.tintIcon(imageViewDelete, R.color.feedback_photo_icon);

            imageViewDelete.setOnClickListener(this);
        }

        public void bind(String photoPath)
        {
            this.photoPath = photoPath;

            ImageUtils.loadImage(this.imageViewPhoto, photoPath);
        }

        @Override
        public void onClick(View v)
        {
            switch (v.getId())
            {
                case R.id.imageView_delete:
                    if(QuestionPhotoAdapter.this.listener != null)
                    {
                        QuestionPhotoAdapter.this.listener.onDeleteClicked(v, this.photoPath);
                    }
                    break;
            }
        }
    }

    public class AddViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {
        public AddViewHolder(View itemView)
        {
            super(itemView);

            ImageView imageViewAdd = (ImageView) itemView.findViewById(R.id.imageView_add);

            ImageUtils.tintIcon(imageViewAdd, R.color.feedback_photo_icon);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v)
        {
            if(QuestionPhotoAdapter.this.listener != null)
            {
                QuestionPhotoAdapter.this.listener.onAddClicked(v);
            }
        }
    }

    public interface OnItemClickListener
    {
        void onAddClicked(View caller);
        void onDeleteClicked(View caller, String photoPath);
    }
}
