package com.github.hereisderek.androidutil.closeable

import com.github.hereisderek.androidutil.obj.Ternary
import timber.log.Timber
import java.io.Closeable
import java.util.concurrent.atomic.AtomicReference

/**
 *
 * User: derekzhu
 * Date: 12/11/19 10:28 AM
 * Project: android-util
 */

/**
 * NOT TESTED
 */
@Deprecated(
    "THIS IMPLEMENTATION HAS NOT BEEN TESTED, it might eat your cat",
    level = DeprecationLevel.WARNING
)
@Suppress("MemberVisibilityCanBePrivate")
open class CloseableSafe <T : Closeable> (
    private val generator: ()-> T,
    private val _closeable: T? = null
) : Closeable {

    private var mCloseable: AtomicReference<T?> = AtomicReference()

    private val closeable: T get() = mCloseable.get() ?: generator.invoke().also {
        mCloseable.set(it)
    }
    // private val closeable = lazy(generator)


    val created : Boolean get() = mCloseable.get() != null

    fun <R> useOrClose(
        close: Ternary = Ternary.Default,
        action: T.(willClose: Boolean) -> R
    ) : R {
        val c = closeable
        val willClose = close.value ?: created
        return action.invoke(c, willClose).also {
            if (willClose){
                c.close()
            }
        }

    }

    fun get() : T = closeable



    override fun close() {
        _closeable?.close()
        mCloseable.get()?.close()
    }

    open fun closeQuiet(handler: ((Exception)->Unit)? = null) {
        try {
            close()
        } catch (e: Exception) {
            Timber.e("CloseableSafe.closeQuiet encountered exception:${e.message}")
            handler?.invoke(e)
        }
    }
}