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
    val sortedEntries = this.entries.sortedWith(comparator)
    entries.forEach {
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


