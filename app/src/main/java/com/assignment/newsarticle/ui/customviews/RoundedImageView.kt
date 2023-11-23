package com.assignment.newsarticle.ui.customviews

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Path
import android.graphics.RectF
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.widget.ImageView

class RoundedImageView : ImageView {
    private var cornerRadius: Float = 20f

    constructor(context: Context) : super(context){
        init()
    }
    constructor(context: Context, attrs: AttributeSet?) :super(context, attrs){
        init()
    }
    constructor(context: Context, attrs : AttributeSet?, defStyle: Int): super(context, attrs, defStyle){
        init()
    }
    private fun init(){
        scaleType =  ScaleType.CENTER_CROP
    }
    override fun onDraw(canvas: Canvas){
        val drawable = drawable ?: return
        if(width == 0 || height == 0){
            return
        }
        val bitmap = getBitampFromDrawable(drawable)
        if(bitmap != null){
            val path = Path()
            val rect = RectF(0f,0f,width.toFloat(),height.toFloat())
            path.addRoundRect(rect, cornerRadius, cornerRadius, Path.Direction.CW)
            canvas.clipPath(path)
            canvas.drawBitmap(bitmap, null, rect, null)
        }
    }
    private fun getBitampFromDrawable(drawable: Drawable): Bitmap?{
        if(drawable is BitmapDrawable){
            return drawable.bitmap
        }
        try {
            val bitmap = Bitmap.createBitmap(drawable.intrinsicWidth, drawable.intrinsicHeight, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(bitmap)
            drawable.setBounds(0, 0, canvas.width, canvas.height)
            drawable.draw(canvas)
            return  bitmap
        }
        catch (e: Exception){
            e.printStackTrace()
        }
        return null
    }
    fun setCornerRadius(radius: Float){
        cornerRadius = radius
        invalidate()
    }
}