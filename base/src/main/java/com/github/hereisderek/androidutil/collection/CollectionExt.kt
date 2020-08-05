package com.github.hereisderek.androidutil.collection

import android.util.SparseArray
import androidx.core.util.forEach
import kotlin.math.min

/**
 *
 * User: derekzhu
 * Date: 15/11/19 4:57 PM
 * Project: android-util
 */



/**
 * flatMap with predefine byKey for the order
 */
public inline fun <K, V, R> Map<out K, V>.flatMap(
    byKey: Array<K>,
    transform: (Map.Entry<K, V>) -> Iterable<R>
): Iterable<R> = flatMapTo(ArrayList<R>(), byKey, transform)


public inline fun <K, V, R> Map<out K, V>.flatMap(
    comparator: Comparator<in Map.Entry<K, V>>,
    transform: (Map.Entry<K, V>) -> Iterable<R>
): Iterable<R> = flatMapTo(ArrayList<R>(), comparator, transform)



public inline fun <K, V, R, C : MutableCollection<in R>> Map<out K, V>.flatMapTo(
    destination: C,
    comparator: Comparator<in Map.Entry<K, V>>,
    transform: (Map.Entry<K, V>) -> Iterable<R>
): C {
    this.entries.sortedWith(comparator).forEach {
        val obj = transform(it)
        destination.addAll(obj)
    }
    return destination
}


public inline fun <K, V, R, C : MutableCollection<in R>> Map<out K, V>.flatMapTo(
    destination: C,
    byKey: Array<out K>,
    transform: (Map.Entry<K, V>) -> Iterable<R>
): C {
    val uniqueKey = byKey.distinct()
    val entries = this.entries

    for(key in uniqueKey) {
        val entriesForKey = entries.filter { it.key == key }
        entriesForKey.forEach {
            val obj = transform(it)
            destination.addAll(obj)
        }
    }
    entries.forEach {
        if (!uniqueKey.contains(it.key)) {
            val obj = transform(it)
            destination.addAll(obj)
        }
    }
    return destination
}

/**
 * check if a collection contains any of the element in @param elements
 */
fun <E> Collection<E>.containsAnyOf(vararg elements: E): Boolean {
    return this.any { it in elements }
}


inline fun <K, V, R> Map<out K, V>.mapNotNullValues(transform: (Map.Entry<K, V>) -> R?): Map<K, R> =
    entries.associateByNotNull({ it.key }, transform)

inline fun <T, K, V> Iterable<T>.associateByNotNull(keySelector: (T) -> K?, valueTransform: (T) -> V?): Map<K, V> {
    val capacity = if (this is Collection<*>) this.size else 16
    return associateByToNotNull(LinkedHashMap(capacity), keySelector, valueTransform)
}

inline fun <T, K, V, M : MutableMap<in K, in V>> Iterable<T>.associateByToNotNull(
    destination: M,
    keySelector: (T) -> K?,
    valueTransform: (T) -> V?
): M {
    for (element in this) {
        val key = keySelector(element) ?: continue
        val value = valueTransform(element) ?: continue
        destination.put(key, value)
    }
    return destination
}

fun <T> Iterable<T>.collectionSizeOrDefault(default: Int): Int = if (this is Collection<*>) this.size else default

inline fun <K, V> Iterable<K>.associateWithNotNull(valueSelector: (K) -> V?): Map<K, V> = associateWithNotNullTo(LinkedHashMap(collectionSizeOrDefault(10)), valueSelector)

inline fun <K, V, M : MutableMap<in K, in V>> Iterable<K>.associateWithNotNullTo(destination: M, valueSelector: (K) -> V?): M {
    for(e in this) {
        valueSelector(e)?.also { destination.put(e, it) }
    }
    return destination
}


public inline fun <T> Array<out T>.sumByFloat(selector: (T) -> Float): Float {
    var sum: Double = 0.0
    for (element in this) {
        sum += selector(element)
    }
    return sum.toFloat()
}


/// ifNotEmptyOrNull and ifEmptyOrNull

// Map
/**
 * invoke a function on the calling collection if it's not empty
 */
public inline fun <C : Map<*, *>, R> C.ifNotEmptyOrNull(defaultValue: (objects: C) -> R) : R?
        = if (this.isNotEmpty()) defaultValue.invoke(this) else null

/**
 * invoke a function on the calling collection if it's not empty
 */
public inline fun <C : Map<*, *>, R> C.ifEmptyOrNull(defaultValue: (objects: C) -> R) : R?
        = if (this.isEmpty()) defaultValue.invoke(this) else null


// Collection
/**
 * invoke a function on the calling collection if it's not empty
 */
public inline fun <C : Collection<*>, R> C.ifNotEmptyOrNull(defaultValue: (objects: C) -> R) : R?
        = if (this.isNotEmpty()) defaultValue.invoke(this) else null

/**
 * invoke a function on the calling collection if it's not empty
 */
public inline fun <C : Collection<*>, R> C.ifEmptyOrNull(defaultValue: (objects: C) -> R) : R?
        = if (this.isEmpty()) defaultValue.invoke(this) else null

// Array
/**
 * invoke a function on the calling collection if it's not empty
 */
@Suppress("UPPER_BOUND_CANNOT_BE_ARRAY")
public inline fun <C : Array<*>, R> C.ifNotEmptyOrNull(defaultValue: (objects: C) -> R) : R?
        = if (this.isNotEmpty()) defaultValue.invoke(this) else null

/**
 * invoke a function on the calling collection if it's not empty
 */
@Suppress("UPPER_BOUND_CANNOT_BE_ARRAY")
public inline fun <C : Array<*>, R> C.ifEmptyOrNull(defaultValue: (objects: C) -> R) : R?
        = if (this.isEmpty()) defaultValue.invoke(this) else null


// List
/**
 * add the given @param obj to the MutableList if it doesn't already exist
 */
public fun <C> MutableList<C>.addIfNotContain(obj: C) = if (!contains(obj)) add(obj) else false


/*public fun <T, A : Appendable> joinToString(
    onObject: Iterable<T>,
    buffer: A,
    separator: CharSequence = ", ",
    prefix: CharSequence = "",
    postfix: CharSequence = "",
    limit: Int = -1,
    truncated: CharSequence = "...",
    transform: ((T) -> CharSequence)? = null
): A {

}*/




/**
 * */
public fun <T> SparseArray<out T>.joinToString(
    separator: CharSequence = ", ",
    prefix: CharSequence = "",
    postfix: CharSequence = "",
    limit: Int = -1,
    truncated: CharSequence = "...",
    transform: ((value: T, index: Int, key: Int) -> CharSequence)? = { value, index, key ->
        "$index:$key:$value"
    }
): String {
    val buffer = StringBuilder(prefix)
    val end = if (limit == -1) size() else min(limit, size())
    for (i in 0 until end) {
        if (i >= 1) buffer.append(separator)
        val key = keyAt(i)
        val element = valueAt(i)
        val output = transform?.invoke(element, i, key) ?: element.toString()
        buffer.append(output)
    }
    if (end < size()) buffer.append(truncated)
    buffer.append(postfix)
    return buffer.toString()
}