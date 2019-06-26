@file:Suppress("RedundantVisibilityModifier", "EXPERIMENTAL_FEATURE_WARNING")

package com.github.hereisderek.androidutil.util

import android.graphics.RectF
import android.os.Looper

/**
 *
 * User: derekzhu
 * Date: 2019-06-26 14:54
 * Project: AndroidUtil
 */


/**
 * execute pre block and post block respectively before/after the execution of @param block
 * to provide a cleaner code
 *
 * @param T return type
 * @param pre
 * @param post
 * @param block
 * @return
 */
public fun <T> methodWrapper(pre: (() -> Unit)? = null, post: ((t:T) -> Unit)? = null, block: ()->T) : T{
    pre?.invoke()
    val result = block.invoke()
    post?.invoke(result)
    return result
}

fun RectF.covers(x: Float, y: Float): Boolean {
    return x >= left && x <= left + width() && y >= top && y <= top + height()
}


/* thread */
val isOnMainThread get() = Looper.myLooper() == Looper.getMainLooper()


/* Time measurement */

/**
 * sample usage:
 *
 * val (time, result) = measureNanoTime {
 *      calculation()
 * }
 *
 * @param T return type
 * @param block calculation
 * @return time esculated and result pair
 */

public inline fun <T> measureTimeMillis(block: () -> T): Pair<Long, T> {
    val start = System.currentTimeMillis()
    val result = block()
    return Pair(System.currentTimeMillis() - start, result)
}

public inline fun <T> measureNanoTime(block: () -> T): Pair<Long, T> {
    val start = System.nanoTime()
    val result = block()
    return Pair(System.nanoTime() - start, result)
}


public inline fun <T> measureTimeMillisTimedResult(block: () -> T): TimedResult<T> {
    val start = System.currentTimeMillis()
    val result = block()
    return TimedResult(Pair(System.currentTimeMillis() - start, result))
}

public inline fun <T> measureNanoTimeTimedResult(block: () -> T): TimedResult<T> {
    val start = System.nanoTime()
    val result = block()
    return TimedResult(Pair(System.nanoTime() - start, result))
}

inline class TimedResult<T>(val timedResult: Pair<Long, T>) {
    val time get() = timedResult.first
    val result get() = timedResult.second
}

object Util{}