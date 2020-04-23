package com.github.hereisderek.androidutil.view.list.recyclerview

import android.view.View
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.OrientationHelper
import androidx.recyclerview.widget.RecyclerView
import timber.log.Timber
import kotlin.math.abs


open class CenterLinearSnapHelper : LinearSnapHelper() {

    private var mVerticalHelper: OrientationHelper? = null
    private var mHorizontalHelper: OrientationHelper? = null
    private var mRecyclerView: RecyclerView? = null
    private val onScrollListeners = ArrayList<RecyclerView.OnScrollListener>()
    private var snappedToPosition = RecyclerView.NO_POSITION

    private val onScrollListener = object : RecyclerView.OnScrollListener() {
        private var lastState = RecyclerView.SCROLL_STATE_IDLE

        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)
            onScrollListeners.forEach {
                it.onScrolled(recyclerView, dx, dy)
            }
            if (lastState == RecyclerView.SCROLL_STATE_SETTLING) {
                onScrollListeners.forEach {
                    (it as? OnSnapScrollListener)?.onSnappingToPosition(recyclerView, dx, dy, snappedToPosition)
                }
            }
        }

        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            var justSettled = false
            if (lastState != newState) {
                if (lastState == RecyclerView.SCROLL_STATE_SETTLING && newState == RecyclerView.SCROLL_STATE_IDLE) {
                    justSettled = true
                }
                lastState = newState
            }

            onScrollListeners.forEach {
                it.onScrollStateChanged(recyclerView, newState)
            }
            if (justSettled) {
                onScrollListeners.forEach {
                    (it as? OnSnapScrollListener)?.onSnappedToPosition(recyclerView, snappedToPosition)
                }
                snappedToPosition = RecyclerView.NO_POSITION
            }
        }
    }

    /**
     * @param includeDecoration
     * to include or exclude decoration when calculating view's center position
     * default is true, same behavior as  {@link LinearSnapHelper}
     * */
    var includeDecoration = true; set(value) {
        if (field != value) {
            field = value
            mRecyclerView?.requestLayout()
        }
    }

    fun addOnSnapScrollListener(listener: OnSnapScrollListener) = addOnScrollListener(listener)

    fun removeOnSnapScrollListener(listener: OnSnapScrollListener) = removeOnScrollListener(listener)

    @Suppress("MemberVisibilityCanBePrivate")
    fun addOnScrollListener(listener: RecyclerView.OnScrollListener) {
        if (!onScrollListeners.contains(listener)) {
            onScrollListeners.add(listener)
        }
    }

    @Suppress("MemberVisibilityCanBePrivate")
    fun removeOnScrollListener(listener: RecyclerView.OnScrollListener) {
        if (onScrollListeners.contains(listener)) {
            onScrollListeners.remove(listener)
        }
    }


    override fun attachToRecyclerView(recyclerView: RecyclerView?) {
        super.attachToRecyclerView(recyclerView)
        if (mRecyclerView != recyclerView) {
            mRecyclerView?.removeOnScrollListener(onScrollListener)

            if (recyclerView == null) {
                onScrollListeners.clear()
            } else {
                recyclerView.addOnScrollListener(onScrollListener)
            }
        }
        this.mRecyclerView = recyclerView
    }





    private fun getVerticalHelper(layoutManager: RecyclerView.LayoutManager): OrientationHelper {
        if (mVerticalHelper?.layoutManager != layoutManager) {
            mVerticalHelper = OrientationHelper.createVerticalHelper(layoutManager)
        }
        return mVerticalHelper!!
    }

    private fun getHorizontalHelper(layoutManager: RecyclerView.LayoutManager): OrientationHelper {
        if (mHorizontalHelper?.layoutManager !== layoutManager) {
            mHorizontalHelper = OrientationHelper.createHorizontalHelper(layoutManager)
        }
        return mHorizontalHelper!!
    }

    private fun findCenterView(layoutManager: RecyclerView.LayoutManager, helper: OrientationHelper): View? {
        val childCount = layoutManager.childCount
        if (childCount == 0) return null

        var closestChild: View? = null
        val center = helper.startAfterPadding + helper.totalSpace / 2
        var absClosest = Int.MAX_VALUE
        var closestChildIndex = RecyclerView.NO_POSITION

        for (i in 0 until childCount) {
            val child = layoutManager.getChildAt(i) ?: continue
            val childCenter = if (includeDecoration) {
                helper.getDecoratedStart(child) + helper.getDecoratedMeasurement(child) / 2
            } else {
                child.run { left + (width + ViewCompat.getPaddingStart(this) + ViewCompat.getPaddingEnd(this)) / 2 }
            }
            val absDistance = abs(childCenter - center)
            // Timber.d("index:$i childCenter:$childCenter, absDistance:$absDistance absClosest:$absClosest")
            if (absDistance < absClosest) {
                absClosest = absDistance
                closestChildIndex = i
                closestChild = child
            }
        }
        snappedToPosition = closestChildIndex
        // Timber.d("found closest at index:$closestChildIndex")
        return closestChild
    }

    override fun findSnapView(layoutManager: RecyclerView.LayoutManager): View? {
        if (layoutManager.canScrollVertically()) {
            return findCenterView(layoutManager, getVerticalHelper(layoutManager))
        } else if (layoutManager.canScrollHorizontally()) {
            return findCenterView(layoutManager, getHorizontalHelper(layoutManager))
        }
        return null
    }



    abstract class OnSnapScrollListener : RecyclerView.OnScrollListener(){
        open fun onSnappedToPosition(recyclerView: RecyclerView, position: Int) {}
        open fun onSnappingToPosition(recyclerView: RecyclerView, dx: Int, dy: Int, position: Int) {}
    }
}