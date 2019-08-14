package com.github.hereisderek.androidutil.obj

import androidx.core.util.Pools

/**
 *
 * User: derekzhu
 * Date: 2019-07-07 15:10
 * Project: Imagician Demo
 */
fun <T, R>Pools.Pool<T>.useNullable(block: (T?)->R) : R {
    val t = acquire()
    try{
        return block.invoke(t)
    } catch (e: Throwable) {
        throw e
    } finally {
        if (t != null) {
            release(t)
        }
    }
}

class ObjectPool<T> constructor(
    maxPoolSize: Int = MAX_POOL_SIZE,
    private val mInitializer: (T.() -> Unit)? = null,
    private val mGenerator: () -> T
) : Pools.SynchronizedPool<T>(maxPoolSize) {


    constructor(
        clazz: Class<T>,
        maxPoolSize: Int = MAX_POOL_SIZE,
        initializer: (T.() -> Unit)? = null
    ) : this(maxPoolSize, initializer, {clazz.newInstance()})




    override fun acquire(): T {
        return (super.acquire() ?: mGenerator.invoke()).apply {
            mInitializer?.invoke(this)
        }
    }

    override fun release(element: T): Boolean {
        return super.release(element)
    }

    fun <R>acquire(block: (T)->R) : R {
        val t = acquire()
        var error : Throwable? = null
        try {
            return block.invoke(t)
        } catch (e: Exception) {
            error = e
            throw e
        } finally {
            release(t)
            if (error != null) {
                throw error
            }
        }
    }

    companion object {
        private const val MAX_POOL_SIZE = 10
    }
}

