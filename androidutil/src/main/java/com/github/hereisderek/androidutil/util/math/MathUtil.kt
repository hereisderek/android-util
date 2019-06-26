package com.github.hereisderek.androidutil.util.math

import java.util.*

/**
 *
 * User: derekzhu
 * Date: 2019-06-26 16:31
 * Project: AndroidUtil
 */


object MathUtil {

    val RANDOM by lazy { Random() }

    /// inclusive
    fun <T : Number> T.fallInRange(min: T, max: T) : T {
        val up : T
        val down : T
        if (min.toDouble() > max.toDouble()) {
            up = min
            down = max
        } else {
            up = max
            down = min
        }

        return when {
            this.toDouble() < down.toDouble() -> down
            this.toDouble() > up.toDouble() -> up
            else -> this
        }
    }

    val IntRange.random get() = RANDOM.nextInt((endInclusive + 1) - start) +  start

    val newUuid get() = UUID.randomUUID().toString()

    fun <T : Number> distance(x1: T, y1: T, x2: T, y2: T) : Double {
        val sq = Math.pow((x2.toDouble() - x1.toDouble()), 2.0) + Math.pow((y2.toDouble() - y1.toDouble()), 2.0)
        return Math.sqrt(sq)
    }
}