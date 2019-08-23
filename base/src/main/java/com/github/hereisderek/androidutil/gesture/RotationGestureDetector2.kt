package com.github.hereisderek.androidutil.gesture

import android.view.MotionEvent
import androidx.annotation.FloatRange
import kotlin.math.atan2

/**
 * User: derekzhu
 * Date: 2019-08-23 11:35
 * Project: Imagician Demo
 */

// https://stackoverflow.com/questions/10682019/android-two-finger-rotation
/**
 * @Deprecated: use RotationGestureDetector for better handling on smaller views
 * */
@Deprecated("replaced by RotationGestureDetector", ReplaceWith("RotationGestureDetector"))
class RotationGestureDetector2(private val mListener: OnRotationGestureListener?) {
    private var fX: Float = 0f
    private var fY: Float = 0f
    private var sX: Float = 0f
    private var sY: Float = 0f
    private var ptrID1: Int = INVALID_POINTER_ID
    private var ptrID2: Int = INVALID_POINTER_ID
    var angle: Float = 0.toFloat()
        private set

    init { }

    fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> ptrID1 = event.getPointerId(event.actionIndex)
            MotionEvent.ACTION_POINTER_DOWN -> {
                ptrID2 = event.getPointerId(event.actionIndex)
                sX = event.getX(event.findPointerIndex(ptrID1))
                sY = event.getY(event.findPointerIndex(ptrID1))
                fX = event.getX(event.findPointerIndex(ptrID2))
                fY = event.getY(event.findPointerIndex(ptrID2))
            }
            MotionEvent.ACTION_MOVE -> if (ptrID1 != INVALID_POINTER_ID && ptrID2 != INVALID_POINTER_ID) {
                val nsX = event.getX(event.findPointerIndex(ptrID1))
                val nsY = event.getY(event.findPointerIndex(ptrID1))
                val nfX = event.getX(event.findPointerIndex(ptrID2))
                val nfY = event.getY(event.findPointerIndex(ptrID2))

                angle = angleBetweenLines(fX, fY, sX, sY, nfX, nfY, nsX, nsY)

                mListener?.onRotation(angle, this)
            }
            MotionEvent.ACTION_UP -> ptrID1 = INVALID_POINTER_ID
            MotionEvent.ACTION_POINTER_UP -> ptrID2 = INVALID_POINTER_ID
            MotionEvent.ACTION_CANCEL -> {
                ptrID1 = INVALID_POINTER_ID
                ptrID2 = INVALID_POINTER_ID
            }
        }
        return true
    }

    private fun angleBetweenLines(fX: Float, fY: Float, sX: Float, sY: Float, nfX: Float, nfY: Float, nsX: Float, nsY: Float): Float {
        val angle1 = atan2((fY - sY).toDouble(), (fX - sX).toDouble()).toFloat()
        val angle2 = atan2((nfY - nsY).toDouble(), (nfX - nsX).toDouble()).toFloat()

        var angle = Math.toDegrees((angle1 - angle2).toDouble()).toFloat() % 360
        if (angle < -180f) angle += 360.0f
        if (angle > 180f) angle -= 360.0f
        return angle
    }

    interface OnRotationGestureListener {
        fun onRotation(@FloatRange(from = -180.0, to = 180.0, fromInclusive = true, toInclusive = true) angle: Float, rotationDetector: RotationGestureDetector2)
    }

    companion object {
        private const val INVALID_POINTER_ID = -1
    }
}