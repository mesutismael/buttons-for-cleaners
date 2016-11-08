package be.appreciate.buttonsforcleaners.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;

import be.appreciate.buttonsforcleaners.R;
import be.appreciate.buttonsforcleaners.utils.TypefaceHelper;

/**
 * Created by Inneke De Clippel on 2/03/2016.
 */
public class TextView extends android.widget.TextView
{
    public static final int REGULAR = 1;
    public static final int MEDIUM = 2;

    public TextView(Context context)
    {
        this(context, null);
    }

    public TextView(Context context, AttributeSet attrs)
    {
        this(context, attrs, android.R.attr.textViewStyle);
    }

    public TextView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);

        if(attrs != null)
        {
            TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.TextView, 0, 0);

            int typefaceNumber = a.getInt(R.styleable.TextView_typeface, REGULAR);
            this.setTypeface(context, typefaceNumber);

            a.recycle();
        }
    }

    public void setTypeface(Context context, int typefaceNumber)
    {
        switch (typefaceNumber)
        {
            case REGULAR:
                this.setTypeface(TypefaceHelper.regular(context));
                break;

            case MEDIUM:
                this.setTypeface(TypefaceHelper.medium(context));
                break;
        }
    }
}
