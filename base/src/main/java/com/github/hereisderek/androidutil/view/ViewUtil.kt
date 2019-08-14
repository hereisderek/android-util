package com.github.hereisderek.androidutil.view

import android.content.Context
import android.graphics.Matrix
import android.os.Build
import android.util.DisplayMetrics
import android.view.MotionEvent
import timber.log.Timber

/**
 *
 * User: derekzhu
 * Date: 2019-06-26 16:34
 * Project: AndroidUtil
 */


object ViewUtil {
    private lateinit var displayMetrics: DisplayMetrics
    /* View */
    fun init(context: Context) {
        displayMetrics = context.resources.displayMetrics
    }

    private fun getDisplayMetrics(context: Context?) = if (ViewUtil::displayMetrics.isInitialized) displayMetrics else {
        init(context
            ?: throw Exception("displayMetrics hasn't been initialized and context is null"))
        displayMetrics
    }

    fun pxToDp(px: Float, context: Context? = null): Float {
        val metrics = getDisplayMetrics(context)
        return px / (metrics.densityDpi / 160f)
    }

    fun dpToPx(dp: Float, context: Context? = null) : Float {
        val metrics = getDisplayMetrics(context)
        return  (metrics.densityDpi / 160f) * dp
    }

    fun pixelsToSp(px: Float, context: Context?): Float {
        return px / getDisplayMetrics(context).scaledDensity
    }

    fun spToPixel(sp: Float, context: Context?) : Float {
        return getDisplayMetrics(context).scaledDensity * sp
    }


    /* Touch Event */
    @JvmStatic
    val MotionEvent.actionString: String get() = if (Build.VERSION.SDK_INT >= 19) {
        MotionEvent.actionToString(action)
    } else {
        when (action) {
            MotionEvent.ACTION_DOWN -> "ACTION_DOWN"
            MotionEvent.ACTION_UP -> "ACTION_UP"
            MotionEvent.ACTION_CANCEL -> "ACTION_CANCEL"
            MotionEvent.ACTION_OUTSIDE -> "ACTION_OUTSIDE"
            MotionEvent.ACTION_MOVE -> "ACTION_MOVE"
            MotionEvent.ACTION_HOVER_MOVE -> "ACTION_HOVER_MOVE"
            MotionEvent.ACTION_SCROLL -> "ACTION_SCROLL"
            MotionEvent.ACTION_HOVER_ENTER -> "ACTION_HOVER_ENTER"
            MotionEvent.ACTION_HOVER_EXIT -> "ACTION_HOVER_EXIT"
            MotionEvent.ACTION_BUTTON_PRESS -> "ACTION_BUTTON_PRESS"
            MotionEvent.ACTION_BUTTON_RELEASE -> "ACTION_BUTTON_RELEASE"
            else -> {
                val index = action and MotionEvent.ACTION_POINTER_INDEX_MASK shr MotionEvent.ACTION_POINTER_INDEX_SHIFT
                when (action and MotionEvent.ACTION_MASK) {
                    MotionEvent.ACTION_POINTER_DOWN -> "ACTION_POINTER_DOWN($index)"
                    MotionEvent.ACTION_POINTER_UP -> "ACTION_POINTER_UP($index)"
                    else -> Integer.toString(action)
                }
            }
        }
    }

    fun MotionEvent.printDebug(tag: String? = null, message: String? = null, handler: ((String) -> Unit) = { s -> Timber.d(s)}) : String{
        return "MotionEvent ${if (!tag.isNullOrBlank()) tag else ""} action: $actionString, $x:$y${if (message.isNullOrBlank()) "" else ", $message"}".also {
            handler(it)
        }
    }

    val MotionEvent.debugStr : String get() = "MotionEvent action: $actionString, $x:$y$"


    /* matrix */
    private val matrixFloatArray = FloatArray(9)
    operator fun Matrix.get(index: Int) : Float {
        getValues(matrixFloatArray)
        return matrixFloatArray[index]
    }

    val Matrix.averageScale : Float get() = (get(Matrix.MSCALE_X) + get(Matrix.MSCALE_Y)) / 2f
}
