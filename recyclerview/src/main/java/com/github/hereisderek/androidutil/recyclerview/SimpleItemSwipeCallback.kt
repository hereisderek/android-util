package com.github.hereisderek.androidutil.recyclerview

import android.graphics.Canvas
import android.graphics.drawable.Drawable
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView


typealias SwipeEnabledForRowChecker = (recyclerView: RecyclerView, vh: RecyclerView.ViewHolder, position: Int) -> Boolean

class SimpleItemSwipeCallback constructor(
    private val onLeft: SwipeOperation? = null,
    private val onRight: SwipeOperation? = null
) : ItemTouchHelper.SimpleCallback(
    0, ((onLeft?.let { ItemTouchHelper.LEFT } ?: 0) or (onRight?.let { ItemTouchHelper.RIGHT } ?: 0))
) {

    override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
        return false
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        when(direction) {
            ItemTouchHelper.LEFT -> onLeft
            ItemTouchHelper.RIGHT -> onRight
            else -> null
        }?.onAction?.invoke(viewHolder.adapterPosition, viewHolder)
    }

    override fun getMovementFlags(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder): Int {
        val position = viewHolder.layoutPosition
        return if (position != RecyclerView.NO_POSITION) {
            val swipeFromLeftEnabled = onLeft?.swipeFromEnabledForRow?.invoke(recyclerView, viewHolder, position) ?: (onLeft != null)
            val swipeFromRightEnabled = onRight?.swipeFromEnabledForRow?.invoke(recyclerView, viewHolder, position) ?: (onRight != null)
            val swipeFlag = (if (swipeFromLeftEnabled) ItemTouchHelper.RIGHT else 0) or (if(swipeFromRightEnabled) ItemTouchHelper.LEFT else 0)
            makeMovementFlags(getDragDirs(recyclerView, viewHolder), swipeFlag)
        } else 0
    }

    override fun onChildDraw(c: Canvas, recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, dX: Float, dY: Float, actionState: Int, isCurrentlyActive: Boolean) {
        if (dX != 0f) {
            val itemView = viewHolder.itemView
            val itemHeight = itemView.bottom - itemView.top

            val operation = (if (dX < 0) onLeft else onRight) ?: return
            val icon : Drawable = operation.iconDrawable
            val bgDrawable : Drawable? = operation.backgroundDrawable

            val iconWith : Int = icon.intrinsicWidth
            val iconHeight : Int = icon.intrinsicHeight
            val iconEdgeMargin = if (operation.iconMarginPx == SWIPE_DEFAULT_ICON_MARGIN) {
                (itemHeight - iconHeight) / 2
            } else operation.iconMarginPx

            val bgLeft : Int = 0
            val bgRight : Int = itemView.right
            val bgTop = itemView.top
            val bgBottom = itemView.bottom

            val iconLeft : Int
            val iconRight : Int
            val iconTop = itemView.top + iconEdgeMargin
            val iconBottom = iconTop + iconHeight

            if (dX > 0) { // swipe to the right
                iconLeft = iconEdgeMargin
                iconRight = iconLeft + iconWith
            } else {
                iconRight = itemView.right - iconEdgeMargin
                iconLeft = iconRight - iconWith
            }

            bgDrawable?.apply {
                setBounds(bgLeft, bgTop, bgRight, bgBottom)
                draw(c)
            }

            icon.apply {
                setBounds(iconLeft, iconTop, iconRight, iconBottom)
                draw(c)
            }
        }

        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
    }


    data class SwipeOperation(
        val iconDrawable: Drawable,
        val backgroundDrawable: Drawable? = null,
        val iconMarginPx : Int = SWIPE_DEFAULT_ICON_MARGIN,
        val swipeFromEnabledForRow: SwipeEnabledForRowChecker? = null,
        val onAction: (position: Int, vh: RecyclerView.ViewHolder)->Unit
    )

    companion object {
        const val SWIPE_DEFAULT_ICON_MARGIN = -1
    }
}