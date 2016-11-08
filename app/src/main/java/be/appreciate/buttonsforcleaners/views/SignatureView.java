package be.appreciate.buttonsforcleaners.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import be.appreciate.buttonsforcleaners.R;
import be.appreciate.buttonsforcleaners.utils.SerializablePath;

/**
 * Created by Inneke De Clippel on 16/03/2016.
 */
public class SignatureView extends View
{
    private SerializablePath path;
    private Paint paint;
    private float touchX;
    private float touchY;

    public SignatureView(Context context)
    {
        this(context, null);
    }

    public SignatureView(Context context, AttributeSet attrs)
    {
        this(context, attrs, 0);
    }

    public SignatureView(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);

        this.path = new SerializablePath();

        this.paint = new Paint();
        this.paint.setAntiAlias(true);
        this.paint.setColor(ContextCompat.getColor(context, R.color.feedback_signature_line));
        this.paint.setStyle(Paint.Style.STROKE);
        this.paint.setStrokeJoin(Paint.Join.ROUND);
        this.paint.setStrokeWidth(4f);
    }

    public void setPath(String path)
    {
        this.path.setPath(path);
        this.invalidate();
    }

    public String getPath()
    {
        return this.path.getPath();
    }

    public void reset()
    {
        this.path.reset();
        this.invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);

        canvas.drawPath(this.path, this.paint);
    }

    private void startTouch(float x, float y)
    {
        this.path.moveTo(x, y);
        this.touchX = x;
        this.touchY = y;
    }

    private void moveTouch(float x, float y)
    {
        this.path.quadTo(this.touchX, this.touchY, (x + this.touchX) / 2, (y + this.touchY) / 2);
        this.path.lineTo(x, y);
        this.touchX = x;
        this.touchY = y;
    }

    private void stopTouch()
    {
        this.path.lineTo(this.touchX, this.touchY);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        float x = event.getX();
        float y = event.getY();

        switch (event.getAction())
        {
            case MotionEvent.ACTION_DOWN:
                this.startTouch(x, y);
                this.invalidate();
                break;

            case MotionEvent.ACTION_MOVE:
                this.moveTouch(x, y);
                this.invalidate();
                break;

            case MotionEvent.ACTION_UP:
                this.stopTouch();
                this.invalidate();
                break;
        }

        return true;
    }
}
