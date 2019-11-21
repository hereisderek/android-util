package com.github.hereisderek.androidutil.closeable

import timber.log.Timber
import java.io.Closeable

/**
 *
 * User: derekzhu
 * Date: 12/11/19 9:54 AM
 * Project: android-util
 */


@JvmOverloads
fun Closeable?.closeQuiet(handler: ((Exception)->Unit)? = null) {
    if (this == null) return
    try {
        this.close()
    } catch (e: Exception) {
        Timber.e("Closeable?.closeQuiet encountered exception:${e.message}")
        handler?.invoke(e)
    }
}


/**
 * Note for database:
 *  https://stackoverflow.com/questions/6608498/best-place-to-close-database-connection
 */
fun <T, C : Closeable> C?.useOrCreateAndClose(
    generator: ()-> C,
    action: C.(created: Boolean)-> T
) : T = useOrCreateAndCloseJvm(this, generator, action)

/**
 * for calling from java code
 */
@JvmOverloads
fun <T, C : Closeable> useOrCreateAndCloseJvm(
    _closeable: C? = null,
    generator: ()-> C,
    action: C.(willClose: Boolean)-> T
) : T {
    val willClose = _closeable == null // we'll need to create it therefore we'll close it after using
    val closeable = _closeable ?: generator.invoke()
    return action(closeable, willClose).also {
        if (willClose) {
            closeable.closeQuiet()
        }
    }
}
