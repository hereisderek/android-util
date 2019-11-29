@file:Suppress("MemberVisibilityCanBePrivate")

package com.github.hereisderek.androidutil.misc

import kotlin.math.min

/**
 *
 * User: derekzhu
 * Date: 27/11/19 2:25 PM
 * Project: android-util
 */

fun CharSequence.subSequenceOrNull(startIndex: Int, endIndex: Int = this.length) : CharSequence?
        = if (startIndex >= 0 && endIndex <= length) subSequence(startIndex, endIndex) else null


fun String.subStringOrNull(startIndex: Int, endIndex: Int = this.length) : String?
        = if (startIndex >= 0 && endIndex <= length) substring(startIndex, endIndex) else null


fun String.subStringOrOmit(startIndex: Int, endIndex: Int = this.length) : String
        = substring(startIndex, min(endIndex, length))