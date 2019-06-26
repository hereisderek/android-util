package com.github.hereisderek.androidutil.util

import kotlin.experimental.and
import kotlin.experimental.inv
import kotlin.experimental.or

/**
 *
 * User: derekzhu
 * Date: 2019-06-26 15:57
 * Project: AndroidUtil
 */


object FlagUtil {
    inline fun Int.hasFlag(flag: Int) = flag and this == flag
    inline fun Int.withFlag(flag: Int) = this or flag
    inline fun Int.minusFlag(flag: Int) = this and flag.inv()

    inline fun Byte.hasFlag(flag: Byte) = flag and this == flag
    inline fun Byte.withFlag(flag: Byte) = this or flag
    inline fun Byte.minusFlag(flag: Byte) = this and flag.inv()
}