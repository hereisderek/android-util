package com.github.hereisderek.androidutil.util.math

/**
 *
 * User: derekzhu
 * Date: 2019-06-26 16:30
 * Project: AndroidUtil
 */


/**
 * calculate a linear equation in the form of
 * y = ax + b that saticfies points (x1, y1) and (x2, y2)
 *
 *
 * @property x1
 * @property x2
 * @property y1
 * @property y2
 */




class LinearPlot <T1 : Number, T2 : Number> @JvmOverloads constructor(
    y1: T1, y2: T1, x1: T2 = 0 as T2, x2: T2 = 100 as T2, lockWithIn: Boolean = false
) : LinearPlotF(y1.toFloat(), y2.toFloat(), x1.toFloat(), x2.toFloat(), lockWithIn) {

    fun get(x : T2) : T1 = super.get(x.toFloat()) as T1
    // fun findXForValue(y : T1) : T2 = super.findXForValue(y.toFloat()) as T2
}

open class LinearPlotF @JvmOverloads constructor(val y1 : Float, val y2 : Float, val x1 : Float = 0f, val x2 : Float = 1f, val lockWithIn: Boolean = false) {
    init {
        assert(y1 < y2)
        assert(x1 != x2)
    }

    val a : Float = (y1 - y2) / (x1 - x2)
    val b : Float by lazy(LazyThreadSafetyMode.NONE) { (y1 * x2 - x1 * y2) / (x2 - x1) }

    open fun get(x : Float) = when {
        lockWithIn && x <= x1 -> y1
        lockWithIn && x >= x2 -> y2
        // else ->  ((x - x1) / (x2 - x1)) * (y2 - y1) + y1
        else -> a * x + b
    }

    /**
     *
     * @param portion from 0f to 1f
     * @return
     */
    open fun getAtPortion(portion: Float) : Float {
        return y1 + (y2 - y1) * portion
    }

    open fun findXForValue(y : Float) = (y - b) / a
}

