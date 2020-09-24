package com.github.hereisderek.androidutil.ext

/**
 *
 * User: derekzhu
 * Date: 2019-08-21 01:43
 * Project: Imagician Demo
 */

inline fun <reified T: Any> javaClass(): Class<T> = T::class.java
