package com.sachin.nearbyresturants.utils;

import android.app.ActivityManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.util.LruCache;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;


import androidx.databinding.BindingAdapter;

import com.sachin.nearbyresturants.R;
import com.sachin.nearbyresturants.interfaces.ImageLoadListener;
import com.sachin.nearbyresturants.network.dto.Photo;
import com.sachin.nearbyresturants.network.dto.RestaurantDetail;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.net.URL;
import java.net.UnknownHostException;

import static com.sachin.nearbyresturants.network.ServerConstants.APIKEY;

/**
 * Utility class used for holding all the functionality related to images
 */
public class ImageUtils {

    private static final String PHOTO_BASE_URL = "https://maps.googleapis.com/maps/api/place/photo?maxwidth=400&key="+APIKEY+"&photoreference=";
    private static ImageUtils imageUtils;
    private static LruCache<String, Bitmap> photosLruCache = null;
    private static int IMAGE_REQUIRED_WIDTH_HEIGHT;

    public static synchronized void initCache(Context context) {
        if (photosLruCache != null) return;
        IMAGE_REQUIRED_WIDTH_HEIGHT = (int) context.getResources().getDimension(R.dimen.restaurant_image_width);
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        int memClass = activityManager != null ? activityManager.getMemoryClass() : 2;
        int cacheSize = 1024 * 1024 * memClass / 8;
        photosLruCache = new LruCache<>(cacheSize);
    }

    /**
     * Used for loading the restaurant images
     *
     * @param photo        the photo reference detail
     * @param imageView   where the downloaded image should be set on
     */
    @BindingAdapter("bind:restaurantImage")
    public static void loadRestaurantImages(ImageView imageView, Photo photo) {
        initCache(imageView.getContext());
        if (photo == null || TextUtils.isEmpty(photo.getPhotoReference()) || imageView == null || photosLruCache == null) return;
        //If the image not present in the cache, then download it from server
        String url = photo.getPhotoReference();
        Log.d("IMAGEUTILS", "URL: "+url);
        if (photosLruCache.get(url) == null) {
            downloadRestaurantImage(url, imageView);
        } else {
            imageView.setImageBitmap(photosLruCache.get(url));
        }
    }

    /**
     *  Used for downloading the bitmap images
     */
    public static class BitmapDownloaderTask extends AsyncTask<String, Void, Bitmap> {
        private final WeakReference<ImageView> imageViewReference;
        private String url;

        BitmapDownloaderTask(ImageView imageView, String url) {
            imageViewReference = new WeakReference<>(imageView);
            this.url = url;
        }

        @Override
        protected Bitmap doInBackground(String... params) {
            Bitmap bitmap = null;
            try {
                String imageUrl = PHOTO_BASE_URL + params[0];
                if (TextUtils.isEmpty(imageUrl)) return null;
                URL urlConnection = new URL(imageUrl);

                if (isCancelled()) return null;
                BitmapFactory.Options options = new BitmapFactory.Options();
                //Since we do not know the bitmap width/height and to avoid OOM Exception
                options.inJustDecodeBounds = true;
                BitmapFactory.decodeStream(urlConnection.openConnection().getInputStream(), null, options);
                //Same value IMAGE_REQUIRED_WIDTH_HEIGHT can be used as required width and height, since the value associated with it is the
                //minimum of the height
                options.inSampleSize = calculateInSampleSize(options, IMAGE_REQUIRED_WIDTH_HEIGHT, IMAGE_REQUIRED_WIDTH_HEIGHT);
                options.inJustDecodeBounds = false;
                bitmap = BitmapFactory.decodeStream(urlConnection.openConnection().getInputStream(), null, options);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (UnknownHostException e) {//For no internet
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();//No need for calling notifyImageDownloadError(), since IO can be caused from cancelling async task too
            }  catch (Exception e) {
                e.printStackTrace();
            }
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if (isCancelled()) bitmap = null;
            if (imageViewReference == null || imageViewReference.get() == null) return;
            ImageView imageView = imageViewReference.get();
            // Once the image is downloaded, associates it to the imageView
            BitmapDownloaderTask bitmapDownloaderTask = getBitmapDownloaderTask(imageView);
            if (this != bitmapDownloaderTask) return;
            imageView.setImageBitmap(bitmap);
            if (photosLruCache != null && !TextUtils.isEmpty(bitmapDownloaderTask.url) && bitmap != null)
                photosLruCache.put(bitmapDownloaderTask.url, bitmap);
        }
    }

    /**
     * Calculates the sample size for the bitmap image to avoid unnecessary downloading the full size
     *
     * @param options   contains the bitmap image
     * @param reqWidth  width of the image view
     * @param reqHeight height of the image view
     * @return integer number to be set as sample size
     */
    private static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) >= reqHeight
                    && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }
        return inSampleSize;
    }

    private static void downloadRestaurantImage(String url, ImageView imageView) {
        //If the same URL is not downloading, then only proceed
        if (cancelPotentialDownload(url, imageView)) {
            BitmapDownloaderTask task = new BitmapDownloaderTask(imageView, url);
            DownloadedDrawable downloadedDrawable = new DownloadedDrawable(task);
            imageView.setImageDrawable(downloadedDrawable);
            task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, url);
        }
    }

    /**
     * Checks if an already async task is downloading the image based on the image view reference and the URL
     *
     * @param url       image URL to be checked if already downloading
     * @param imageView reference to check DownloadedDrawable associated with the image view
     * @return true if already download is present, or else false
     */
    private static boolean isAlreadyDownloadPresent(String url, ImageView imageView) {
        BitmapDownloaderTask bitmapDownloaderTask = getBitmapDownloaderTask(imageView);
        if (bitmapDownloaderTask == null || TextUtils.isEmpty(bitmapDownloaderTask.url)) return false;
        String bitmapUrl = bitmapDownloaderTask.url;
        return bitmapUrl != null && bitmapUrl.equals(url);
    }

    /**
     * Cancels an already running async task
     *
     * @param url       of the image where download should be cancelled
     * @param imageView reference to check DownloadedDrawable associated with the image view
     */
    public static boolean cancelPotentialDownload(String url, ImageView imageView) {
        BitmapDownloaderTask bitmapDownloaderTask = getBitmapDownloaderTask(imageView);
        if (bitmapDownloaderTask != null) {
            String bitmapUrl = bitmapDownloaderTask.url;
            if ((bitmapUrl == null) || (!bitmapUrl.equals(url))) {
                Log.d("IMAGEUTILS", "cancelPotentialDownload CANCELLED URL: "+url);
                bitmapDownloaderTask.cancel(true);
            } else {
                // The same URL is already being downloaded.
                return false;
            }
        }
        return true;
    }

    private static BitmapDownloaderTask getBitmapDownloaderTask(ImageView imageView) {
        if (imageView == null) return null;
        Drawable drawable = imageView.getDrawable();
        if (!(drawable instanceof DownloadedDrawable)) return null;
        DownloadedDrawable downloadedDrawable = (DownloadedDrawable) drawable;
        return downloadedDrawable.getBitmapDownloaderTask();
    }

    /**
     *  This class is used to set with the image view to associate the async task running with it
     */
    static class DownloadedDrawable extends ColorDrawable {
        private final WeakReference<BitmapDownloaderTask> bitmapDownloaderTaskReference;

        DownloadedDrawable(BitmapDownloaderTask bitmapDownloaderTask) {
            super(Color.TRANSPARENT);
            bitmapDownloaderTaskReference =
                    new WeakReference<>(bitmapDownloaderTask);
        }

        BitmapDownloaderTask getBitmapDownloaderTask() {
            return bitmapDownloaderTaskReference.get();
        }
    }

}