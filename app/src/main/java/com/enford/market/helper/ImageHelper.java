package com.enford.market.helper;

import android.content.Context;
import android.graphics.Bitmap;
import android.widget.ImageView;

import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.PauseOnScrollListener;

/**
 * Say something about this class
 *
 * @author xiads
 * @Date 2/16/15.
 */
public final class ImageHelper {

    private static ImageHelper sInstance;
    private DisplayImageOptions mDefaultOptions;

    int DEFALUT_THREAD_POOL_SIZE = 3;
    int DEFAULT_LRU_MEMORY_CACHE_SIZE = 20;

    private static ImageLoader sImageLoader = null;

    private ImageHelper(Context c) {
        ImageLoaderConfiguration configuration = new ImageLoaderConfiguration.Builder(c)
                .threadPoolSize(DEFALUT_THREAD_POOL_SIZE)
                .memoryCache(new LruMemoryCache(DEFAULT_LRU_MEMORY_CACHE_SIZE))
                //.writeDebugLogs()
                .build();
        sImageLoader = ImageLoader.getInstance();
        sImageLoader.init(configuration);

        mDefaultOptions = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .imageScaleType(ImageScaleType.IN_SAMPLE_INT)
                .build();
    }

    public synchronized static ImageHelper getInstance(Context c) {
        if (sInstance == null) {
            sInstance = new ImageHelper(c);
        }
        return sInstance;
    }

    public void displayImage(String url, ImageView img) {
        sImageLoader.displayImage(url, img, mDefaultOptions);
    }

    public void displayImage(String url, ImageView img, DisplayImageOptions options) {
        sImageLoader.displayImage(url, img, options);
    }

    public void loadImage(String url, ImageLoadingListener listener) {
        sImageLoader.loadImage(url, mDefaultOptions, listener);
    }

    public void loadImage(String url, ImageSize size, ImageLoadingListener listener) {
        sImageLoader.loadImage(url, size, mDefaultOptions, listener);
    }

    public void loadImage(String url, ImageSize size, DisplayImageOptions options, ImageLoadingListener listener) {
        sImageLoader.loadImage(url, size, options, listener);
    }

    public ImageLoader getImageLoader() {
        return sImageLoader;
    }

    public void clearDiskCache() {
        sImageLoader.clearDiskCache();
    }

    public PauseOnScrollListener getScrollListener() {
        return new PauseOnScrollListener(getImageLoader(), true, true);
    }

    public static void clearCache() {
        sImageLoader.clearDiskCache();
        sImageLoader.clearMemoryCache();
    }
}
