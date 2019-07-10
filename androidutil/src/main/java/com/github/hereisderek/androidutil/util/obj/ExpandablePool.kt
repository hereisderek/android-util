package com.github.hereisderek.androidutil.util.obj

import androidx.annotation.NonNull
import androidx.core.util.Pools

/**
 *
 * User: derekzhu
 * Date: 2019-07-10 13:14
 * Project: Imagician Demo
 */
public interface ExpandablePool<T> : Pools.Pool<T>{

    /**
     * the actual available instance in the pool, which can grow when more needed
     */
    val size: Int

    /**
     * @return An instance from the pool if such, null otherwise.
     *  unlike @see [Pools.Pool.acquire], this guarantees that the result is not null
     *  unless the pool has been destroyed (in which case, an [IllegalStateException] will be thrown
     *
     *  @throws IllegalStateException if the pool has been destroyed
     */
    @NonNull
    override fun acquire(): T

    /**
     * acquire a [T] (or create a new one if needed) for use, and immediately release it afterwards
     * @param R return object type
     * @param block to run on the acquired [T] object
     * @return
     */
    fun <R>use(block: (T)->R) : R

    fun acquirePoolObj(): PooledObj<T>


    fun isInPool(instance: T) : Boolean

    /**
     * trim the pool to a specific size
     *
     * @param size: the ideal size of the pool
     * @return the actual size
     */
    fun trimToSize(size: Int) : Int
}

open class SimpleExpandablePool<T>(
    initialSize: Int = 10,
    generator: (()->T)
) : ExpandablePool<T> {

    private var generator: (()->T)? = generator
        get() = field ?: throw IllegalStateException("unable to create new object, the pool has been destroyed (monstrously if I may add)")

    private val mStorage = ArrayList<T>(initialSize)

    override val size: Int get() = mStorage.size

    override fun acquire(): T {
        return mStorage.firstOrNull()
            ?: generator!!()
    }

    override fun acquirePoolObj(): PooledObj<T> {
        return PooledObj(acquire(), this)
    }

    override fun release(instance: T): Boolean {
        if (isInPool(instance)) {
            throw IllegalStateException("instance is already in the pool")
        }

        return mStorage.add(instance)
    }

    override fun isInPool(instance: T) : Boolean {
        return mStorage.contains(instance)
    }

    override fun <R>use(block: (T)->R) : R {
        val t = acquire()
        try {
            return block.invoke(t)
        } catch (e: Throwable) {
            throw e
        } finally {
            release(t)
        }
    }

    open fun clearPool() {
        mStorage.clear()
    }

    open fun destroy() {
        clearPool()
        generator = null
    }

    override fun trimToSize(size: Int): Int {
        when{
            // no need further action
            size >= this.size -> Unit
            size < 0 -> throw IllegalArgumentException("The trimmed size must be >= 0")
            else -> {
                for (i in (this.size - 1) downTo size ) {
                    mStorage.removeAt(i)
                }
            }
        }
        return this.size
    }
}

open class SynchronizedExpandablePool<T>(
    initialSize: Int = 10,
    lock: Any? = null,
    generator: (()->T)
) : SimpleExpandablePool<T>(initialSize, generator){
    private val lock = lock ?: this
    override val size: Int
        get() = synchronized(lock) {super.size}

    override fun acquire(): T = synchronized(lock) {
        super.acquire()
    }

    override fun acquirePoolObj(): PooledObj<T> = synchronized(lock) {
        return super.acquirePoolObj()
    }

    override fun release(instance: T): Boolean = synchronized(lock) {
        super.release(instance)
    }

    override fun isInPool(instance: T): Boolean = synchronized(lock){
        super.isInPool(instance)
    }

    override fun <R> use(block: (T) -> R): R = synchronized(lock) {
        return super.use(block)
    }

    override fun clearPool() = synchronized(lock){
        super.clearPool()
    }

    override fun destroy() = synchronized(lock){
        super.destroy()
    }
}


class PooledObj<T>(val obj:T, private val pool: ExpandablePool<T>) : Recyclable {

    override val isRecycled: Boolean = pool.isInPool(obj)

    override fun recycle() = pool.release(obj)
}

fun <T>pool(
    mode : LazyThreadSafetyMode = LazyThreadSafetyMode.SYNCHRONIZED,
    initialSize : Int = 10,
    generator : () -> T
) : ExpandablePool<T> {
    return when (mode) {
        LazyThreadSafetyMode.NONE -> SimpleExpandablePool(initialSize, generator)
        else -> SynchronizedExpandablePool(initialSize, null, generator)
    }
}