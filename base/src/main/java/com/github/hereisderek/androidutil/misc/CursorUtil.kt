package com.github.hereisderek.androidutil.misc

import android.database.Cursor
import androidx.collection.ArrayMap

/**
 *
 * User: derekzhu
 * Date: 2019-06-26 16:41
 * Project: AndroidUtil
 */




inline fun <T> Cursor.toArrayList(block: (Cursor) -> T): ArrayList<T>  = this.toArrayList(false, block)

// for some reason @Overloads doesn't work here
// @JvmOverloads
inline fun <T> Cursor.toArrayList(close: Boolean = false, block: (Cursor) -> T): ArrayList<T> {
    return arrayListOf<T>().also { list ->
        try {
            if (moveToFirst()) {
                for (index in 0 until this.count) {
                    list.add(block.invoke(this))
                    if (!moveToNext()) break
                }
            }
        } finally {
            if (close) { close() }
        }
    }
}


@JvmOverloads
inline fun <T> Cursor.toArrayListIndexed(close: Boolean = false, block: (Cursor, index: Int) -> T): ArrayList<T> {
    return arrayListOf<T>().also { list ->
        try {
            if (moveToFirst()) {
                for (index in 0 until this.count) {
                    list.add(block.invoke(this, index))
                    if (!moveToNext()) break
                }
            }
        } finally {
            if (close) { close() }
        }
    }
}


@JvmOverloads
inline fun <T> Cursor.toSelfArrayList(close: Boolean = false, block: Cursor.() -> T): ArrayList<T>
    = this.toArrayList(close, block)


@JvmOverloads
fun <T> Cursor.toSelfArrayListIndexed(close: Boolean = false, block: Cursor.(index: Int) -> T): ArrayList<T> {
    return this.toSelfArrayListIndexed(close, block)
}




/// map

@JvmOverloads
inline fun <K, V> Cursor.toArrayMapIndexed(close: Boolean = false, block: (Cursor, index: Int) -> Pair<K, V>) : Map<K, V> {
    return ArrayMap<K, V>().also { map ->
        if (moveToFirst()) {
            for (index in 0 until this.count) {
                val result = block.invoke(this, index)
                map[result.first] = result.second
                if (!moveToNext()) break
            }
        }
        if (close) { close() }
    }
}




@JvmOverloads
inline fun <K, V> Cursor.toSelfArrayMapIndexed(close: Boolean = false, block: Cursor.(index: Int) -> Pair<K, V>) : Map<K, V>
    = this.toArrayMapIndexed(close, block)


