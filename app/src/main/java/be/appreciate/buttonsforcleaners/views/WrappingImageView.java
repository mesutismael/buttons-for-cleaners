package be.appreciate.buttonsforcleaners.views;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * Created by Inneke De Clippel on 2/03/2016.
 */
public class WrappingImageView extends ImageView
{
    public WrappingImageView(Context context)
    {
        super(context);
    }

    public WrappingImageView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public WrappingImageView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        Drawable d = this.getDrawable();

        if(d!=null)
        {
            int width = MeasureSpec.getSize(widthMeasureSpec);
            int height = (int) Math.ceil((float) width * (float) d.getIntrinsicHeight() / (float) d.getIntrinsicWidth());
            this.setMeasuredDimension(width, height);
        }else
        {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }
    }
}
