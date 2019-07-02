package com.github.hereisderek.androidutil.util

import android.database.Cursor

/**
 *
 * User: derekzhu
 * Date: 2019-06-26 16:41
 * Project: AndroidUtil
 */


fun <T> Cursor.toArrayList(block: (Cursor) -> T): ArrayList<T> {
    return arrayListOf<T>().also { list ->
        if (moveToFirst()) {
            do {
                list.add(block.invoke(this))
            } while (moveToNext())
        }
    }
}

fun <T> Cursor.toArrayListIndexed(block: (Cursor, index: Int) -> T): ArrayList<T> {
    return arrayListOf<T>().also { list ->
        if (moveToFirst()) {
            for (index in 0 until this.count) {
                list.add(block.invoke(this, index))
                if (!moveToNext()) break
            }
        }
    }
}