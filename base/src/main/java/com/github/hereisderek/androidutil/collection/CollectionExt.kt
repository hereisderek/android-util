package com.github.hereisderek.androidutil.collection

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



/// ifNotEmptyOrNull and ifEmptyOrNull

// Map
public inline fun <C : Map<*, *>, R> C.ifNotEmptyOrNull(defaultValue: (objects: C) -> R) : R?
        = if (this.isNotEmpty()) defaultValue.invoke(this) else null

public inline fun <C : Map<*, *>, R> C.ifEmptyOrNull(defaultValue: (objects: C) -> R) : R?
        = if (this.isEmpty()) defaultValue.invoke(this) else null


// Collection
public inline fun <C : Collection<*>, R> C.ifNotEmptyOrNull(defaultValue: (objects: C) -> R) : R?
        = if (this.isNotEmpty()) defaultValue.invoke(this) else null

public inline fun <C : Collection<*>, R> C.ifEmptyOrNull(defaultValue: (objects: C) -> R) : R?
        = if (this.isEmpty()) defaultValue.invoke(this) else null

// Array
@Suppress("UPPER_BOUND_CANNOT_BE_ARRAY")
public inline fun <C : Array<*>, R> C.ifNotEmptyOrNull(defaultValue: (objects: C) -> R) : R?
        = if (this.isNotEmpty()) defaultValue.invoke(this) else null

@Suppress("UPPER_BOUND_CANNOT_BE_ARRAY")
public inline fun <C : Array<*>, R> C.ifEmptyOrNull(defaultValue: (objects: C) -> R) : R?
        = if (this.isEmpty()) defaultValue.invoke(this) else null
