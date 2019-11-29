package com.github.hereisderek.androidutil.view.list.recyclerview

import android.view.View
import androidx.recyclerview.widget.RecyclerView

/**
 *
 * User: derekzhu
 * Date: 22/11/19 1:29 PM
 * Project: android-util
 */


@Suppress("UNCHECKED_CAST")
class ViewHolder<T : View> (view: T) : RecyclerView.ViewHolder(view){
    fun <T> getItemView() = itemView as T
}