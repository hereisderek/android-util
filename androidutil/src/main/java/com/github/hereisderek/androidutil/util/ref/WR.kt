package com.github.hereisderek.androidutil.util.ref

import java.lang.ref.WeakReference

/**
 *
 * User: derekzhu
 * Date: 2019-06-26 16:35
 * Project: AndroidUtil
 */


/**
 * WeakReference
 *
 * @param T
 * @constructor
 *
 * @param t
 */
class WR<T>(t: T? = null) {
    private var wr : WeakReference<T>? = null

    init { set(t) }

    @Synchronized fun set(t: T?) {
        wr = if (t == null) null else WeakReference(t)
    }

    fun get() : T? = wr?.get()
}