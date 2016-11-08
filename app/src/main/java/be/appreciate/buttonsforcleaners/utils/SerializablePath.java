package be.appreciate.buttonsforcleaners.utils;

import android.graphics.Path;

/**
 * Created by Inneke De Clippel on 16/03/2016.
 */
public class SerializablePath extends Path
{
    private StringBuilder path;
    private boolean pathEmpty;

    private static final String SYMBOL_LINE = "L";
    private static final String SYMBOL_QUAD = "Q";
    private static final String SYMBOL_MOVE = "M";
    private static final String SEPARATOR_OBJECT = " ";
    private static final String SEPARATOR_VALUE = ",";

    public SerializablePath()
    {
        super();

        this.path = new StringBuilder();
        this.pathEmpty = true;
    }

    public String getPath()
    {
        return path.toString();
    }

    public void setPath(String path)
    {
        super.reset();

        this.path = new StringBuilder();
        this.pathEmpty = android.text.TextUtils.isEmpty(path);

        if(path != null)
        {
            this.path.append(path);

            float previousX = 0;
            float previousY = 0;
            String[] objects = path.split(SEPARATOR_OBJECT);

            for(String object : objects)
            {
                if(object != null)
                {
                    String[] values = object.split(SEPARATOR_VALUE);

                    if(values.length == 3)
                    {
                        try
                        {
                            float x = Float.parseFloat(values[0]);
                            float y = Float.parseFloat(values[1]);
                            String symbol = values[2];

                            if(SYMBOL_MOVE.equals(symbol))
                            {
                                super.moveTo(x, y);
                            }
                            else if(SYMBOL_QUAD.equals(symbol))
                            {
                                super.quadTo(previousX, previousY, x, y);
                            }
                            else if(SYMBOL_LINE.equals(symbol))
                            {
                                super.lineTo(x, y);
                            }

                            previousX = x;
                            previousY = y;
                        }
                        catch (NumberFormatException e)
                        {

                        }
                    }
                }
            }
        }
    }

    @Override
    public void moveTo(float x, float y)
    {
        if(!this.pathEmpty)
        {
            this.path.append(SEPARATOR_OBJECT);
        }

        this.path.append(x);
        this.path.append(SEPARATOR_VALUE);
        this.path.append(y);
        this.path.append(SEPARATOR_VALUE);
        this.path.append(SYMBOL_MOVE);

        this.pathEmpty = false;

        super.moveTo(x, y);
    }

    @Override
    public void quadTo(float x1, float y1, float x2, float y2)
    {
        if(!this.pathEmpty)
        {
            this.path.append(SEPARATOR_OBJECT);
        }

        this.path.append(x2);
        this.path.append(SEPARATOR_VALUE);
        this.path.append(y2);
        this.path.append(SEPARATOR_VALUE);
        this.path.append(SYMBOL_QUAD);

        this.pathEmpty = false;

        super.quadTo(x1, y1, x2, y2);
    }

    @Override
    public void lineTo(float x, float y)
    {
        if(!this.pathEmpty)
        {
            this.path.append(SEPARATOR_OBJECT);
        }

        this.path.append(x);
        this.path.append(SEPARATOR_VALUE);
        this.path.append(y);
        this.path.append(SEPARATOR_VALUE);
        this.path.append(SYMBOL_LINE);

        this.pathEmpty = false;

        super.lineTo(x, y);
    }

    @Override
    public void reset()
    {
        this.path = new StringBuilder();
        this.pathEmpty = true;

        super.reset();
    }
}
