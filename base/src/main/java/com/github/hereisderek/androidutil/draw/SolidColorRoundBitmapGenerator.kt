package com.github.hereisderek.androidutil.draw

import android.content.res.Resources
import android.graphics.*
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory
import com.github.hereisderek.androidutil.obj.pool

/**
 *
 * User: derekzhu
 * Date: 2019-07-05 20:16
 * Project: Imagician Demo
 */

// https://android--examples.blogspot.com/2015/11/android-circular-bitmap-with-border-and.html
class SolidColorRoundBitmapGenerator constructor(
    private val mBitmapPool : BitmapPool? = null,
    private val mConfig: Bitmap.Config = Bitmap.Config.ARGB_8888
){

    private val mPaintPool = pool(Paint::class.java, initialSize = MAX_POOL_SIZE) {
        isAntiAlias = true
        style = Paint.Style.FILL
    }

    private val mCanvasPool = pool(initialSize = MAX_POOL_SIZE){
        Canvas()
    }

    private val mOutputOption = BitmapFactory.Options()



    fun solidColorCircularBitmapWithBorder(
        @ColorInt circleColor: Int,
        radius: Int,
        @ColorInt borderColor: Int = Color.WHITE,
        borderWidth: Float = 0f,
        shadowColor: Int = Color.LTGRAY,
        shadowWidth: Float = 0f,
        config: Bitmap.Config = mConfig
    ) : Bitmap {
        val dimension = radius * 2
        val radiusF = radius.toFloat()
        val bitmap = createEmptyBitmap(dimension, dimension, config)

        mCanvasPool.use { canvas ->
            mPaintPool.use { paint ->
                canvas.setBitmap(bitmap)

                // draw circle
                paint.apply {
                    color = circleColor
                    style = Paint.Style.FILL
                }
                canvas.drawCircle(radiusF, radiusF, radiusF - (borderWidth + shadowWidth), paint)


                // draw border
                if (borderWidth > 0) {
                    paint.apply {
                        color = borderColor
                        style = Paint.Style.STROKE
                        strokeWidth = borderWidth
                    }
                    canvas.drawCircle(radiusF, radiusF, radiusF - shadowWidth - borderWidth / 2, paint)
                }


                // draw shadow
                if (shadowWidth > 0) {
                    paint.apply {
                        color = shadowColor
                        style = Paint.Style.STROKE
                        strokeWidth = shadowWidth
                    }
                    canvas.drawCircle(radiusF, radiusF, radiusF - borderWidth / 2, paint)
                }
            }
        }
        return bitmap
    }



    fun solidColorCircularBitmap(
        @ColorInt color: Int,
        radius: Int,
        config: Bitmap.Config = mConfig
    ) : Bitmap {
        val bitmap = createEmptyBitmap(radius * 2, config = config)
        val radiusF = radius.toFloat()

        mCanvasPool.use { canvas ->
            mPaintPool.acquire { paint ->
                canvas.setBitmap(bitmap)
                paint.style = Paint.Style.FILL
                paint.color = color
                canvas.drawCircle(radiusF, radiusF, radiusF, paint)
            }
        }

        return bitmap
    }




    fun getRoundedBitmapFromResource(
        resources: Resources,
        @DrawableRes drawableId: Int,
        config: Bitmap.Config = mConfig
    ) : Bitmap? {
        mOutputOption.inPreferredConfig = config
        val originalBitmap = BitmapFactory.decodeResource(resources, drawableId, mOutputOption)
        val circularBitmapDrawable = RoundedBitmapDrawableFactory.create(resources, originalBitmap).apply{
            isCircular = true
        }
        return circularBitmapDrawable.bitmap
    }



    /// helper
    private fun createEmptyBitmap(width: Int, height: Int = width, config: Bitmap.Config = mConfig)
            = mBitmapPool?.get(width, height, config)?: Bitmap.createBitmap(width, height, config)


    private fun recycleBitmap(bitmap: Bitmap) = if (mBitmapPool != null) {
        mBitmapPool.put(bitmap)
    } else {
        bitmap.recycle()
    }


    companion object {
        private const val MAX_POOL_SIZE = 10


    }
}