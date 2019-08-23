package com.github.hereisderek.androidutil.math

import java.util.*
import kotlin.math.max as kMax
import kotlin.math.min as kMin

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


    fun inRange(value : Int, end1: Int, end2: Int) : Int {
        val min = kMin(end1, end2)
        val max = kMax(end1, end2)
        return kMax(kMin(value, max), min)
    }

    fun inRange(value: Int, vararg ranges: Int): Int? {
        return inRange(value, ranges.min() ?: return null, ranges.max() ?: return null)
    }




    /*fun <T : Number> inRange(value : T, end1: T, end2: T) {
        val min = min(end1, end2)
        val max = max(end1, end2)
        return min()
    }

    fun <T : Number> min(a: T, b: T) : T{
        return when(a) {
            is Int -> calculate(a, b) {
                kMin(a as Int, b as Int) as Int
            }
            is Float -> min(a as Float, b as Float)
            else -> min(a, b)
        } as T
    }

    fun <T : Number> calculate(a:T, b:T, action: (a:T, b:T) -> T) : T {
        when(a) {
            is Int -> action.invoke(a, b)
            else
        }
    }*/

    val max_int : (a: Int, b: Int) -> Int = { a, b -> kMax(a, b) }

}