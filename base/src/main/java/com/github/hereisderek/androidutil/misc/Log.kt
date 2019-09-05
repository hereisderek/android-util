package com.github.hereisderek.androidutil.misc

import com.github.hereisderek.androidutil.BuildConfig
import timber.log.Timber

/**
 *
 * User: derekzhu
 * Date: 2019-09-05 11:17
 * Project: Safety Net
 */

private val debugTree by lazy(LazyThreadSafetyMode.NONE) { Timber.DebugTree() }

fun initTimber(enable: Boolean = BuildConfig.DEBUG){
    if (enable) {
        if (Timber.treeCount() == 0) {
            Timber.plant(debugTree)
            Timber.d("Timber initialized")
        }
    }
}