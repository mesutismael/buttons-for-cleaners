package be.appreciate.buttonsforcleaners.views;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by Inneke De Clippel on 10/03/2016.
 */
public class NonSwipingViewPager extends ViewPager
{
    private boolean swipeEnabled;

    public NonSwipingViewPager(Context context)
    {
        super(context);
        this.swipeEnabled = false;
    }

    public NonSwipingViewPager(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        this.swipeEnabled = false;
    }

    public void setSwipeEnabled(boolean swipeEnabled)
    {
        this.swipeEnabled = swipeEnabled;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev)
    {
        if(this.swipeEnabled)
        {
            return super.onInterceptTouchEvent(ev);
        }

        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev)
    {
        if(this.swipeEnabled)
        {
            return super.onTouchEvent(ev);
        }

        return false;
    }
}
