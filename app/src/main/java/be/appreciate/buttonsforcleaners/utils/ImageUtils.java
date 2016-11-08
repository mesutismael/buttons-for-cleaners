package be.appreciate.buttonsforcleaners.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.text.TextUtils;
import android.view.MenuItem;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.Target;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;

import be.appreciate.buttonsforcleaners.R;

/**
 * Created by Inneke De Clippel on 25/02/2016.
 */
public class ImageUtils
{
    private static final String FEEDBACK_IMAGE_FILE_NAME = "feedback_%d_%d_%d.jpg";

    public static void loadImage(ImageView imageView, String imageUrl)
    {
        ImageUtils.loadImage(imageView, imageUrl, R.drawable.placeholder_person);
    }

    public static void loadImage(ImageView imageView, String imageUrl, @DrawableRes int placeholderResId)
    {
        File imageFile = ImageUtils.getImageFile(imageView.getContext(), imageUrl);

        if(imageFile != null && imageFile.exists())
        {
            Glide.with(imageView.getContext())
                    .load(imageFile)
                    .placeholder(placeholderResId)
                    .error(placeholderResId)
                    .dontAnimate()
                    .into(imageView);
        }
        else if(!TextUtils.isEmpty(imageUrl))
        {
            Glide.with(imageView.getContext())
                    .load(imageUrl)
                    .placeholder(placeholderResId)
                    .error(placeholderResId)
                    .dontAnimate()
                    .into(imageView);
        }
        else
        {
            imageView.setImageResource(placeholderResId);
        }
    }

    /**
     * Download an image from a url in its original size and save it in the private storage of the app.
     * @param context A context
     * @param imageUrl The url of the image
     * @return True if the image is downloaded and saved, false if it failed
     */
    public static boolean downloadAndSaveImage(Context context, String imageUrl)
    {
        boolean success;
        File imageFile = ImageUtils.getImageFile(context, imageUrl);

        if(imageFile == null)
        {
            success = true;
        }
        else if(imageFile.exists())
        {
            success = true;
        }
        else
        {
            FileOutputStream fileOutputStream = null;

            try
            {
                Bitmap bitmap = Glide.with(context)
                        .load(imageUrl)
                        .asBitmap()
                        .into(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
                        .get();

                if(bitmap != null && !bitmap.isRecycled())
                {
                    fileOutputStream = context.openFileOutput(ImageUtils.getFileName(imageUrl), Context.MODE_PRIVATE);
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);
                    success = true;
                }
                else
                {
                    success = false;
                }
            }
            catch (CancellationException | InterruptedException | ExecutionException | FileNotFoundException e)
            {
                success = false;
            }
            finally
            {
                if(fileOutputStream != null)
                {
                    try
                    {
                        fileOutputStream.close();
                    }
                    catch (IOException e)
                    {

                    }
                }
            }
        }

        return success;
    }

    /**
     * Get the file containing the image.
     * @param context A context
     * @param imageUrl The url of the image
     * @return Null if the url is empty, a nonexistent file if the image has not been saved yet, or a file containing the image
     */
    private static File getImageFile(Context context, String imageUrl)
    {
        String fileName = ImageUtils.getFileName(imageUrl);

        if(!TextUtils.isEmpty(fileName))
        {
            return new File(context.getFilesDir(), fileName);
        }
        else
        {
            return null;
        }
    }

    /**
     * Get the name of the file for the image on this url, this will the hashcode of the url.
     * @param imageUrl The url of the image
     * @return The name of the file, or null if the url is empty
     */
    private static String getFileName(String imageUrl)
    {
        if(!TextUtils.isEmpty(imageUrl))
        {
            return String.valueOf(imageUrl.hashCode());
        }
        else
        {
            return null;
        }
    }

    /**
     * Create a file on the external storage to save a photo taken by the camera.
     * @param context A context
     * @param planningId The id of the planning item, used in the filename
     * @param questionId The id of the question, used in the filename
     * @return A file to save the photo in, or null if there is no external storage
     */
    public static File createFeedbackImageFile(Context context, int planningId, int questionId)
    {
        String fileName = String.format(FEEDBACK_IMAGE_FILE_NAME, planningId, questionId, System.currentTimeMillis());
        File externalStorage = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        boolean storageExists = externalStorage != null && externalStorage.exists();
        return storageExists ? new File(externalStorage, fileName) : null;
    }

    /**
     * Tint the drawable in an ImageView.
     * @param imageView The ImageView that contains the drawable to be tinted
     * @param colorRes The color resource to be used
     */
    public static void tintIcon(ImageView imageView, @ColorRes int colorRes)
    {
        if(imageView != null && imageView.getDrawable() != null)
        {
            Drawable tintedDrawable = ImageUtils.tintIcon(imageView.getContext(), imageView.getDrawable(), colorRes);
            imageView.setImageDrawable(tintedDrawable);
        }
    }

    /**
     * Tint the drawable in an ImageView.
     * @param context A context
     * @param menuItem The MenuItem that contains the drawable to be tinted
     * @param colorRes The color resource to be used
     */
    public static void tintIcon(Context context, MenuItem menuItem, @ColorRes int colorRes)
    {
        if(menuItem != null && menuItem.getIcon() != null)
        {
            Drawable tintedDrawable = ImageUtils.tintIcon(context, menuItem.getIcon(), colorRes);
            menuItem.setIcon(tintedDrawable);
        }
    }

    /**
     * Tint the drawable.
     * @param context A context
     * @param drawable The drawable to be tinted
     * @param colorRes The color resource to be used
     * @return The tinted drawable
     */
    public static Drawable tintIcon(Context context, Drawable drawable, @ColorRes int colorRes)
    {
        if(drawable != null)
        {
            int color = ContextCompat.getColor(context, colorRes);
            Drawable.ConstantState state = drawable.getConstantState();
            drawable = DrawableCompat.wrap(state == null ? drawable : state.newDrawable()).mutate();
            DrawableCompat.setTint(drawable, color);
        }

        return drawable;
    }
}
