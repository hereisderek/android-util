package com.github.hereisderek.androidutil.misc

import android.database.Cursor
import androidx.collection.ArrayMap
import com.github.hereisderek.androidutil.closeable.closeQuiet

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


/**
 * the same as Cursor.toSelfArrayList but using this for the cursor
 */
@JvmOverloads
fun <T> Cursor.toSelfArrayListIndexed(close: Boolean = false, block: Cursor.(index: Int) -> T): ArrayList<T> {
    return this.toSelfArrayListIndexed(close, block)
}

inline fun Cursor.forEachIndexed(close: Boolean = false, block: (cursor: Cursor, index: Int) -> Unit) {
    check(!this.isClosed) { "Cursor has already been closed" }
    if (moveToFirst()) {
        for (index in 0 until this.count) {
            block.invoke(this, index)
            if (!moveToNext()) break
        }
    }
    if (close) { closeQuiet() }
}

inline fun Cursor.forEachIndexedSelf(close: Boolean = false, block: Cursor.(index: Int) -> Unit) {
    check(!this.isClosed) { "Cursor has already been closed" }
    if (moveToFirst()) {
        for (index in 0 until this.count) {
            block.invoke(this, index)
            if (!moveToNext()) break
        }
    }
    if (close) { closeQuiet() }
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


val Cursor?.isNullOrEmpty get() = this == null || this.count == 0

@JvmOverloads
inline fun <K, V> Cursor.toSelfArrayMapIndexed(close: Boolean = false, block: Cursor.(index: Int) -> Pair<K, V>) : Map<K, V>
    = this.toArrayMapIndexed(close, block)


// parsing

private inline fun <T> getOrNull(cursor: Cursor, columnIndex: Int): T? =
    if (columnIndex in 0 until cursor.columnCount) {
        when (val type = cursor.getType(columnIndex)) {
            Cursor.FIELD_TYPE_INTEGER -> cursor.getLong(columnIndex)
            Cursor.FIELD_TYPE_FLOAT -> cursor.getFloat(columnIndex)
            Cursor.FIELD_TYPE_STRING -> cursor.getString(columnIndex)
            Cursor.FIELD_TYPE_BLOB -> cursor.getBlob(columnIndex)
            else -> null
        } as? T
    } else { null }

// private inline fun <T> parseIfHas(cursor: Cursor, columnIndex: Int, operation: (index: Int)->T) : T?
//         = if (columnIndex in 0 until cursor.columnCount){ operation.invoke(columnIndex) } else null
//
// private fun parseLong(cursor: Cursor, columnIndex: Int) = parseIfHas(cursor, columnIndex, cursor::getLong)
//
// private fun parseString(cursor: Cursor, columnIndex: Int) = parseIfHas(cursor, columnIndex, cursor::getString)
//
// private fun parseInt(cursor: Cursor, columnIndex: Int) = parseIfHas(cursor, columnIndex, cursor::getInt)
//
// private fun parseShort(cursor: Cursor, columnIndex: Int) = parseIfHas(cursor, columnIndex, cursor::getShort)

