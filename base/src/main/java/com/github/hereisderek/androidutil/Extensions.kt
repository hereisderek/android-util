package com.github.hereisderek.androidutil

import timber.log.Timber
import java.io.Closeable

/**
 *
 * User: derekzhu
 * Date: 2019-08-21 01:43
 * Project: Imagician Demo
 */


fun Closeable?.closeQuiet() {
    if (this == null) return
    try {
        this.close()
    } catch (e: Exception) {
        Timber.e("Closeable?.closeQuiet ignored exception:${e.message}")
    }
}