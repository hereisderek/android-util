package com.github.hereisderek.androidutil

import android.database.sqlite.SQLiteDatabase
import timber.log.Timber
import java.io.Closeable
import kotlin.math.ceil

/**
 *
 * User: derekzhu
 * Date: 2019-08-21 01:43
 * Project: Imagician Demo
 */

inline fun <reified T: Any> javaClass(): Class<T> = T::class.java
