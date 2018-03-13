package com.kustomer.kustomersdk.Helpers;

import android.graphics.Bitmap;
import android.util.LruCache;

/**
 * Created by Junaid on 2/16/2018.
 */

public class KUSCache {

    private static LruCache<String,Bitmap> mMemoryCache;

    public KUSCache(){

        if(mMemoryCache == null) {
            final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);

            // Use 1/8th of the available memory for this memory cache.
            final int cacheSize = maxMemory / 8;

            mMemoryCache = new LruCache<String, Bitmap>(cacheSize) {
                @Override
                protected int sizeOf(String key, Bitmap bitmap) {
                    // The cache size will be measured in kilobytes rather than
                    // number of items.
                    return bitmap.getByteCount() / 1024;
                }
            };
        }

    }

    public void addBitmapToMemoryCache(String key, Bitmap bitmap) {
        if (getBitmapFromMemCache(key) == null) {
            mMemoryCache.put(key, bitmap);
        }
    }

    public Bitmap getBitmapFromMemCache(String key) {
        return mMemoryCache.get(key);
    }

    public void removeBitmapFromMemCache(String key){
        if(key != null)
            mMemoryCache.remove(key);
    }

}
