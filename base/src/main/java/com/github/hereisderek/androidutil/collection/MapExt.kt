package com.github.hereisderek.androidutil.collection


/**
 * User: derek
 * Date: 23/9/20 3:44 pm
 * Project: Safety Net
 * @see <a href="https://github.com/hereisderek/">Github</a>
 */

private fun <A, B> pairDebugString(a:A, b:B) : String = "[$a:$b]"



val <K, V>Map.Entry<K, V>.debugStr get() = pairDebugString(key, value)

val <K, V>Pair<K, V>.debugStr get() = pairDebugString(first, second)

val <K, V>MutablePair<K, V>.debugStr get() = pairDebugString(first, second)

val <K, V> Map<K, V>.debugStr : String get() = this.map { it.debugStr }.joinToString(prefix = "[", postfix = "]", separator = ",")
