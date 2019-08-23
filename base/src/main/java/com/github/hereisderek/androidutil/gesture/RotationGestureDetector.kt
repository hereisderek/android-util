package com.github.hereisderek.androidutil.gesture

import android.graphics.PointF
import android.view.MotionEvent
import android.view.View

import timber.log.Timber
import kotlin.math.cos
import kotlin.math.sin

/**
 * User: derekzhu
 * Date: 2019-08-23 11:44
 * Project: Imagician Demo
 */


class RotationGestureDetector(private val mListener: OnRotationGestureListener?, private val mView: View) {
    private val mFPoint = PointF()
    private val mSPoint = PointF()
    private var mPtrID1: Int = INVALID_POINTER_ID
    private var mPtrID2: Int = INVALID_POINTER_ID
    private val mLocation = intArrayOf(0, 0)
    var angle: Float = 0.toFloat()
        private set


    fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.actionMasked) {
            MotionEvent.ACTION_OUTSIDE -> Timber.d("ACTION_OUTSIDE")
            MotionEvent.ACTION_DOWN -> {
                Timber.d("ACTION_DOWN")
                mPtrID1 = event.getPointerId(event.actionIndex)
            }
            MotionEvent.ACTION_POINTER_DOWN -> {
                Timber.d("ACTION_POINTER_DOWN")
                mPtrID2 = event.getPointerId(event.actionIndex)

                getRawPoint(event, mPtrID1, mSPoint)
                getRawPoint(event, mPtrID2, mFPoint)
            }
            MotionEvent.ACTION_MOVE -> if (mPtrID1 != INVALID_POINTER_ID && mPtrID2 != INVALID_POINTER_ID) {
                val nfPoint = PointF()
                val nsPoint = PointF()

                getRawPoint(event, mPtrID1, nsPoint)
                getRawPoint(event, mPtrID2, nfPoint)

                angle = angleBetweenLines(mFPoint, mSPoint, nfPoint, nsPoint)

                mListener?.onRotation(this)
            }
            MotionEvent.ACTION_UP -> mPtrID1 = INVALID_POINTER_ID
            MotionEvent.ACTION_POINTER_UP -> mPtrID2 = INVALID_POINTER_ID
            MotionEvent.ACTION_CANCEL -> {
                mPtrID1 = INVALID_POINTER_ID
                mPtrID2 = INVALID_POINTER_ID
            }
            else -> {
            }
        }
        return true
    }

    private fun getRawPoint(ev: MotionEvent, index: Int, point: PointF) {
        val location = mLocation
        mView.getLocationOnScreen(location)

        var x = ev.getX(index)
        var y = ev.getY(index)

        var angle = Math.toDegrees(Math.atan2(y.toDouble(), x.toDouble()))
        angle += mView.rotation.toDouble()

        val length = PointF.length(x, y)

        x = (length * cos(Math.toRadians(angle))).toFloat() + location[0]
        y = (length * sin(Math.toRadians(angle))).toFloat() + location[1]

        point.set(x, y)
    }

    private fun angleBetweenLines(fPoint: PointF, sPoint: PointF, nFpoint: PointF, nSpoint: PointF): Float {
        val angle1 = Math.atan2((fPoint.y - sPoint.y).toDouble(), (fPoint.x - sPoint.x).toDouble()).toFloat()
        val angle2 = Math.atan2((nFpoint.y - nSpoint.y).toDouble(), (nFpoint.x - nSpoint.x).toDouble()).toFloat()

        var angle = Math.toDegrees((angle1 - angle2).toDouble()).toFloat() % 360
        if (angle < -180f) angle += 360.0f
        if (angle > 180f) angle -= 360.0f
        return -angle
    }

    interface OnRotationGestureListener {
        fun onRotation(rotationDetector: RotationGestureDetector)
    }

    companion object {
        private const val INVALID_POINTER_ID = -1
    }
}