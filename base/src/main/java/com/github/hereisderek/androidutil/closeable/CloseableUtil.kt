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

    private val closeable = lazy{
        _closeable ?: generator.invoke()
    }

    val created : Boolean get() = closeable.isInitialized()

    fun <R> useOrClose(
        close: Ternary = Ternary.Default,
        action: T.(willClose: Boolean) -> R
    ) : R {
        val closeable = this.closeable.value
        val willClose = close.value ?: _closeable == null

        return action.invoke(closeable, willClose).also {
            if (willClose){
                closeable.close()
            }
        }

    }

    fun get() : T = closeable.value


    override fun close() {
        if (closeable.isInitialized()){
            closeable.value.close()
        }
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