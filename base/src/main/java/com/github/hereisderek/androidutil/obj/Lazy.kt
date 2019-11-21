@file:Suppress("ClassName")

package com.github.hereisderek.androidutil.obj

import kotlinx.serialization.Serializable

/**
 *
 * User: derekzhu
 * Date: 2019-06-26 16:00
 * Project: AndroidUtil
 */

internal object UNINITIALIZED_VALUE

/**
 * lazily create object by calling initializer, and will release its value once it's called
 * @param T
 * @constructor
 * @param initializer
 */

@Deprecated("use by lazy{}", ReplaceWith("kotlin#lazy"))
class LazyInit<T>(initializer: () -> T) : Lazy<T> {
    private var initializer: (() -> T)? = initializer @Synchronized set
    private var _value: Any? = UNINITIALIZED_VALUE @Synchronized set

    override val value: T
        get() {
            if (_value === UNINITIALIZED_VALUE) {
                _value = initializer!!()
                initializer = null
            }
            @Suppress("UNCHECKED_CAST")
            return _value as T
        }

    override fun isInitialized(): Boolean = _value !== UNINITIALIZED_VALUE

    fun get() = value
}

/**
 * @see LazyInit, with 1 parameter
 * @param initializer
 */
interface Lazy1<out T, Param> {
    fun isInitialized(): Boolean
    fun get(param: Param) : T
}


@Serializable
class UnsafeLazy1Impl<out T, Param>(initializer: (a: Param) -> T) : Lazy1<T, Param> {
    private var initializer: ((a: Param) -> T)? = initializer
    private var t: T? = null @Synchronized set @Synchronized get

    override fun get(param: Param) = t ?: initializer!!.invoke(param).also {
        t = it
        initializer = null
    }

    override fun isInitialized(): Boolean = (t != null)
}

class SynchronizedLazy1<out T, Param>(initializer: (param: Param) -> T, lock: Any? = null) : Lazy1<T, Param> {
    private val lock = lock ?: this
    private var initializer: ((param: Param) -> T)? = initializer
    @Volatile private var _value: Any? = UNINITIALIZED_VALUE

    @Suppress("LocalVariableName")
    override fun get(param: Param): T {
        val _v1 = _value
        @Suppress("UNCHECKED_CAST")
        if (_v1 !== UNINITIALIZED_VALUE) return _v1 as T

        return synchronized(lock) {
            val _v2 = _value
            if (_v2 !== UNINITIALIZED_VALUE) {
                @Suppress("UNCHECKED_CAST")
                return _v2 as T
            } else {
                initializer!!.invoke(param).also {
                    _value = it
                    initializer = null
                }
            }
        }
    }

    override fun isInitialized(): Boolean = _value !== UNINITIALIZED_VALUE
}



/// Lazy1
@Deprecated("use by lazy{}", ReplaceWith("kotlin#lazy"))
fun <T> lazyInit(initializer: () -> T) = kotlin.lazy { initializer() }

@Deprecated("use by lazy{}", ReplaceWith("kotlin#lazy"))
fun <T> lazy(mode: LazyThreadSafetyMode = LazyThreadSafetyMode.SYNCHRONIZED, initializer: () -> T) = kotlin.lazy(mode) { initializer() }


fun <T, Param> lazyInit(
    mode : LazyThreadSafetyMode = LazyThreadSafetyMode.SYNCHRONIZED,
    initializer: (a: Param) -> T
) : Lazy1<T, Param> = when(mode){
    LazyThreadSafetyMode.NONE -> UnsafeLazy1Impl(initializer)
    else -> SynchronizedLazy1(initializer)
}

fun <T, Param> lazyInit(lock: Any?, initializer: (a: Param) -> T) = SynchronizedLazy1(initializer, lock)



/// Lazy2
interface Lazy2<out T, Param1, Param2> {
    fun isInitialized(): Boolean
    fun get(param1: Param1, param2: Param2) : T
}

class UnsafeLazy2Impl<T, Param1, Param2>(
    initializer: (param1: Param1, param2: Param2) -> T
) : Lazy2<T, Param1, Param2> {
    private var initializer: ((param1: Param1, param2: Param2) -> T)? = initializer
    private var _value: Any? = UNINITIALIZED_VALUE @Synchronized set

    override fun get(param1: Param1, param2: Param2) : T {
        if (_value === UNINITIALIZED_VALUE) {
            _value = initializer!!.invoke(param1, param2)
            initializer = null
        }
        @Suppress("UNCHECKED_CAST")
        return _value as T
    }

    override fun isInitialized(): Boolean = (_value !== UNINITIALIZED_VALUE)
}


class SynchronizedLazy2<out T, Param1, Param2>(
    initializer: (param1: Param1, param2: Param2) -> T,
    lock: Any? = null
) : Lazy2<T, Param1, Param2> {
    private val lock = lock ?: this
    private var initializer: ((param1: Param1, param2: Param2) -> T)? = initializer
    @Volatile private var _value: Any? = UNINITIALIZED_VALUE

    @Suppress("LocalVariableName")
    override fun get(param1: Param1, param2: Param2): T {
        val _v1 = _value
        @Suppress("UNCHECKED_CAST")
        if (_v1 !== UNINITIALIZED_VALUE) return _v1 as T

        return synchronized(lock) {
            val _v2 = _value
            if (_v2 !== UNINITIALIZED_VALUE) {
                @Suppress("UNCHECKED_CAST")
                return _v2 as T
            } else {
                initializer!!.invoke(param1, param2).also {
                    _value = it
                    initializer = null
                }
            }
        }
    }

    override fun isInitialized(): Boolean = _value !== UNINITIALIZED_VALUE
}

fun <T, Param1, Param2> lazyInit(
    mode : LazyThreadSafetyMode = LazyThreadSafetyMode.SYNCHRONIZED,
    initializer: (param1: Param1, param2: Param2) -> T
) : Lazy2<T, Param1, Param2> = when(mode) {
    LazyThreadSafetyMode.NONE -> UnsafeLazy2Impl(initializer)
    else -> {
        SynchronizedLazy2(initializer)
    }
}



/// Lazy3
interface Lazy3<out T, Param1, Param2, Param3> {
    fun isInitialized(): Boolean
    fun get(param1: Param1, param2: Param2, param3: Param3) : T
}

class UnsafeLazy3Impl<T, Param1, Param2, Param3>(
    initializer: (param1: Param1, param2: Param2, param3: Param3) -> T
) : Lazy3<T, Param1, Param2, Param3> {
    private var initializer: ((param1: Param1, param2: Param2, param3: Param3) -> T)? = initializer
    private var _value: Any? = UNINITIALIZED_VALUE @Synchronized set

    override fun get(param1: Param1, param2: Param2, param3: Param3) : T {
        if (_value === UNINITIALIZED_VALUE) {
            _value = initializer!!.invoke(param1, param2, param3)
            initializer = null
        }
        @Suppress("UNCHECKED_CAST")
        return _value as T
    }

    override fun isInitialized(): Boolean = (_value !== UNINITIALIZED_VALUE)
}


class SynchronizedLazy3<out T, Param1, Param2, Param3>(
    initializer: (param1: Param1, param2: Param2, param3: Param3) -> T,
    lock: Any? = null
) : Lazy3<T, Param1, Param2, Param3> {
    private val lock = lock ?: this
    private var initializer: ((param1: Param1, param2: Param2, param3: Param3) -> T)? = initializer
    @Volatile private var _value: Any? = UNINITIALIZED_VALUE

    @Suppress("LocalVariableName")
    override fun get(param1: Param1, param2: Param2, param3: Param3): T {
        val _v1 = _value
        @Suppress("UNCHECKED_CAST")
        if (_v1 !== UNINITIALIZED_VALUE) return _v1 as T

        return synchronized(lock) {
            val _v3 = _value
            if (_v3 !== UNINITIALIZED_VALUE) {
                @Suppress("UNCHECKED_CAST")
                return _v3 as T
            } else {
                initializer!!.invoke(param1, param2, param3).also {
                    _value = it
                    initializer = null
                }
            }
        }
    }

    override fun isInitialized(): Boolean = _value !== UNINITIALIZED_VALUE
}

fun <T, Param1, Param2, Param3> lazyInit(
    mode : LazyThreadSafetyMode = LazyThreadSafetyMode.SYNCHRONIZED,
    initializer: (param1: Param1, param2: Param2, param3: Param3) -> T
) : Lazy3<T, Param1, Param2, Param3> = when(mode) {
    LazyThreadSafetyMode.NONE -> UnsafeLazy3Impl(initializer)
    else -> {
        SynchronizedLazy3(initializer)
    }
}



fun <K, V> lazyMap(initializer: (K) -> V): Map<K, V> {
    val map = mutableMapOf<K, V>()
    return map.withDefault { key ->
        val newValue = initializer(key)
        map[key] = newValue
        return@withDefault newValue
    }
}

