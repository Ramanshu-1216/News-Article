package com.assignment.newsarticle.utils

import android.graphics.Bitmap
import android.util.Log
import android.util.LruCache
import android.widget.ImageView

class ImageCache(){
    private val memoryCache: LruCache<String, Bitmap> = object  : LruCache<String, Bitmap>(cacheSize()){
        override fun sizeOf(key: String?, bitmap: Bitmap): Int {
            return bitmap.byteCount / 1024
        }
    }

    fun getCachedImage(url: String) : Bitmap?{
        return memoryCache.get(url)
    }

    fun setCachedImage(url: String, bitmap: Bitmap){
        memoryCache.put(url, bitmap)
    }

    private fun cacheSize(): Int{
        val maxMemory = (Runtime.getRuntime().maxMemory() / 1024).toInt()
        return maxMemory/8
    }
}