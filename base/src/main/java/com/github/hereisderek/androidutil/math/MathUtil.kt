package com.github.hereisderek.androidutil.math

import java.util.*
import kotlin.math.ceil
import kotlin.math.pow
import kotlin.math.sqrt
import kotlin.math.max as kMax
import kotlin.math.min as kMin

/**
 *
 * User: derekzhu
 * Date: 2019-06-26 16:31
 * Project: AndroidUtil
 */


val Float.ceil : Int get() = ceil(this).toInt()
val Double.ceil : Int get() = ceil(this).toInt()

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
        val sq = (x2.toDouble() - x1.toDouble()).pow(2.0) + (y2.toDouble() - y1.toDouble()).pow(2.0)
        return sqrt(sq)
    }


    fun inRange(value : Int, end1: Int, end2: Int) : Int {
        val min = kMin(end1, end2)
        val max = kMax(end1, end2)
        return kMax(kMin(value, max), min)
    }

    fun inRange(value: Int, vararg ranges: Int): Int? {
        return inRange(value, ranges.min() ?: return null, ranges.max() ?: return null)
    }


    val max_int : (a: Int, b: Int) -> Int = { a, b -> kMax(a, b) }

}