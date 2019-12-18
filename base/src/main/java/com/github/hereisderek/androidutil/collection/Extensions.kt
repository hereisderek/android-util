package com.github.hereisderek.androidutil.collection

import android.database.Cursor
import androidx.collection.SimpleArrayMap
import java.lang.IndexOutOfBoundsException
import kotlin.math.max
import kotlin.math.min

/**
 *
 * User: derekzhu
 * Date: 2019-06-26 14:42
 * Project: AndroidUtil
 */


public inline fun <K, V> MutableMap<K, V>.getOrPutNotNull(key: K, defaultValue: () -> V?) : V? {
    return get(key) ?: defaultValue()?.also {
        put(key, it)
    }
}

fun <T> List<T>.toArrayList(): ArrayList<T> = this as? ArrayList<T> ?: ArrayList<T>().apply {
    addAll(this@toArrayList)
}

public inline fun <T> List<T>.filterBackwards(predicate: (T) -> Boolean): List<T> {
    return filterTo(ArrayList(), predicate)
}

/**
 * @param size: size of the items
 */
public fun <T> List<T>.take(size: Int = -1, skip: Int = 0): List<T> {
    val actualStart = max(skip, 0)
    val end = min(this.size, actualStart + size)
    return subList(actualStart, end)
}



public inline fun <T, C : MutableCollection<in T>> List<T>.filterBackwardsTo(destination: C, predicate: (T) -> Boolean): C {
    val iterator = this.listIterator(size)
    while (iterator.hasPrevious()) {
        val element = iterator.previous()
        if (predicate(element)) destination.add(element)
    }
    return destination
}

/**
 * floatArrayOf()
 */
fun FloatArray.set(vararg values: Float)  {
    require(this.size >= values.size){
        "values length is larger than destination array, ${values.size} vs ${this.size}"
    }
    values.forEachIndexed { index, fl ->
        this[index] = fl
    }
}


fun <K, V> SimpleArrayMap<K, V>.toKeyList() : List<K> {
    val size = this.size()
    if (size() == 0) return emptyList()
    val list = ArrayList<K>(size())
    for ( i in 0 until size) {
        list.add(this.keyAt(i))
    }
    return list
}

