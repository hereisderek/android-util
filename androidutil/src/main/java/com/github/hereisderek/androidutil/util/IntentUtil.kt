package com.github.hereisderek.androidutil.util

import androidx.annotation.VisibleForTesting
import java.util.concurrent.atomic.AtomicInteger

/**
 *
 * User: derekzhu
 * Date: 2019-07-02 02:38
 * Project: Imagician Demo
 */


object IntentUtil {
    /* request code */
    private const val initialSeedValue = 1000
    private val seed by lazy { AtomicInteger(initialSeedValue) }
    @VisibleForTesting
    fun setCurrentSeedValue(value: Int) = seed.set(value)

    val newRequestCode: Int get() = if (seed.compareAndSet(Int.MAX_VALUE, initialSeedValue)) initialSeedValue else seed.incrementAndGet()
}