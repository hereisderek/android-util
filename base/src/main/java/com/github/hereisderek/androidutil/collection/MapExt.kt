package com.github.hereisderek.androidutil.collection


/**
 * User: derek
 * Date: 23/9/20 3:44 pm
 * Project: Safety Net
 * @see <a href="https://github.com/hereisderek/">Github</a>
 */

val <K, V>Map.Entry<K, V>.debugStr get() = "[$key:$value]"

val <K, V>Pair<K, V>.debugStr get() = "[$first:$second]"


/**
 *
 */
val <K, V> Map<K, V>.debugStr : String get() = this.map {
    it.debugStr
}.joinToString(prefix = "[", postfix = "]")
