package com.github.hereisderek.androidutil.util.obj

import java.io.Closeable

/**
 *
 * User: derekzhu
 * Date: 2019-07-07 16:37
 * Project: Imagician Demo
 */


interface Recyclable {
    val isRecycled: Boolean
    fun recycle(): Boolean
}

fun Recyclable?.recycleFinally(cause: Throwable?) = when {
    this == null -> {}
    // cause == null -> recycle()
    else -> {
        try {
            recycle()
        } catch (closeException: Throwable) {
            // try {
            //     cause.addSuppressed(closeException)
            // } catch (e: Exception) {
            // }
        }
    }
}


public inline fun <T : Recyclable?, R> T.use(block: (T) -> R): R {
    var exception: Throwable? = null
    try {
        return block(this)
    } catch (e: Throwable) {
        exception = e
        throw e
    } finally {
        this.recycleFinally(exception)
    }
}