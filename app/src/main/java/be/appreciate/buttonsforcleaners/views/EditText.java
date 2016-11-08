package be.appreciate.buttonsforcleaners.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;

import be.appreciate.buttonsforcleaners.R;
import be.appreciate.buttonsforcleaners.utils.TypefaceHelper;

/**
 * Created by Inneke De Clippel on 2/03/2016.
 */
public class EditText extends android.widget.EditText
{
    public static final int REGULAR = 1;
    public static final int MEDIUM = 2;

    public EditText(Context context)
    {
        this(context, null);
    }

    public EditText(Context context, AttributeSet attrs)
    {
        this(context, attrs, android.R.attr.editTextStyle);
    }

    public EditText(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);

        if(attrs != null)
        {
            TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.EditText, 0, 0);

            int typefaceNumber = a.getInt(R.styleable.EditText_typeface, REGULAR);
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
