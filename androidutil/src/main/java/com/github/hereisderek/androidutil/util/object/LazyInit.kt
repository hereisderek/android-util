package com.github.hereisderek.androidutil.util.`object`

/**
 *
 * User: derekzhu
 * Date: 2019-06-26 16:00
 * Project: AndroidUtil
 */

/**
 * lazily create object by calling initializer, and will release its value once it's called
 * @param T
 * @constructor
 * @param initializer
 */
class LazyInit<T>(initializer: () -> T) {
    private var initializer: (() -> T)? = initializer @Synchronized set
    private var t: T? = null
        @Synchronized set

    fun get() = t ?: initializer?.invoke()?.also {
        t = it
        initializer = null
    }
}

/**
 * @see LazyInit, with 1 parameter
 * @param initializer
 */
class LazyInit1<T, Param>(initializer: (a: Param) -> T) {
    private var initializer: ((a: Param) -> T)? = initializer
    private var t: T? = null @Synchronized set @Synchronized get


    fun get(a: Param) = t ?: initializer!!.invoke(a)?.also {
        t = it
        initializer = null
    }
}


class LazyInit2<T, Param1, Param2>(initializer: (a: Param1, b: Param2) -> T) {
    private var initializer: ((a: Param1, b: Param2) -> T)? = initializer
    private var t: T? = null @Synchronized set

    fun get(a: Param1, b: Param2) = t ?: initializer?.invoke(a, b)?.also {
        t = it
        initializer = null
    }
}


