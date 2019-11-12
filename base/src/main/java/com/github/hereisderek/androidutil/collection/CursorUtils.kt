package com.github.hereisderek.androidutil.collection

import android.database.Cursor
import androidx.collection.ArrayMap
import com.github.hereisderek.androidutil.closeable.closeQuiet

/**
 *
 * User: derekzhu
 * Date: 2019-09-05 17:19
 * Project: android-util
 */

/**
 * @param from the zero-based starting position. (default: 0)
 */
fun <T> Cursor.toList(
    close: Boolean = false,
    from: Int = 0,
    limit: Int = -1,
    mapping: Cursor.(index: Int) -> T
) : List<T> {
    require(limit == -1 || limit >= 0)

    if (!moveToPosition(from) || limit == 0){
        return emptyList()
    }

    val list = ArrayList<T>(count)

    try {
        val endIndex = if (limit == -1)
            count
        else
            minOf(from + limit, count)

        for (i in from until endIndex){
            val obj = mapping.invoke(this, i)
            list.add(obj)
            if (!moveToLast()) break
        }
    } catch (e: Exception) {
    } finally {
        if (close) {
            closeQuiet()
        } else {
            moveToFirst()
        }
    }
    return list
}

