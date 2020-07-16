package com.github.hereisderek.androidutil.view

import android.content.Context
import android.content.res.Resources
import android.graphics.Matrix
import android.os.Build
import android.util.DisplayMetrics
import android.util.TypedValue
import android.view.MotionEvent
import androidx.annotation.IntDef
import timber.log.Timber

/**
 *
 * User: derekzhu
 * Date: 2019-06-26 16:34
 * Project: AndroidUtil
 */


object ViewUtil {
    private lateinit var displayMetrics: DisplayMetrics

    @Deprecated("use DimenExt.kt instead", level = DeprecationLevel.WARNING)
    fun init(context: Context?) {
        displayMetrics = context?.resources?.displayMetrics ?: Resources.getSystem().displayMetrics
    }

    private fun getDisplayMetrics(context: Context?) = if (ViewUtil::displayMetrics.isInitialized) displayMetrics else {
        init(context)
        displayMetrics
    }

    @Deprecated("use DimenExt.kt instead", level = DeprecationLevel.WARNING)
    fun pxToDp(px: Float, context: Context? = null): Float {
        val metrics = getDisplayMetrics(context)
        return px / (metrics.densityDpi / 160f)
    }

    @Deprecated("use DimenExt.kt instead", level = DeprecationLevel.WARNING)
    fun dpToPx(dp: Float, context: Context? = null) : Float {
        val metrics = getDisplayMetrics(context)
        return  (metrics.densityDpi / 160f) * dp
    }

    @Deprecated("use DimenExt.kt instead", level = DeprecationLevel.WARNING)
    fun pixelsToSp(px: Float, context: Context? = null): Float {
        return px / getDisplayMetrics(context).scaledDensity
    }

    @Deprecated("use DimenExt.kt instead", level = DeprecationLevel.WARNING)
    fun spToPixel(sp: Float, context: Context? = null) : Float {
        return getDisplayMetrics(context).scaledDensity * sp
    }

    @Deprecated("use DimenExt.kt instead", level = DeprecationLevel.WARNING)
    fun applyDimension(@TypedValueUnit unit: Int, value: Float, context: Context? = null)
            = TypedValue.applyDimension(unit, value, getDisplayMetrics(context))


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
                    else -> action.toString()
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