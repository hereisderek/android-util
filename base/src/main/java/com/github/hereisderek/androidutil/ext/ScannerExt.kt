package com.github.hereisderek.androidutil.ext

import java.util.*


/**
 * User: derek
 * Date: 23/9/20 3:17 pm
 * Project: Safety Net
 */

private const val defaultRadix = 10

fun Scanner.nextOrNull() : String? = if (hasNext()) next() else null

fun Scanner.nextLineOrNull() : String? = if (hasNextLine()) nextLine() else null

fun Scanner.nextBooleanOrNull() : Boolean? = if (hasNextBoolean()) nextBoolean() else null

fun Scanner.nextIntOrNull(radix: Int = defaultRadix) : Int? = if (hasNextInt(radix)) nextInt(radix) else null

fun Scanner.nextByteOrNull(radix: Int = defaultRadix) : Byte? = if (hasNextByte(radix)) nextByte(radix) else null

fun Scanner.nextShortOrNull(radix: Int = defaultRadix) : Short? = if (hasNextShort(radix)) nextShort(radix) else null

fun Scanner.nextLongOrNull(radix: Int = defaultRadix) : Long? = if (hasNextLong(radix)) nextLong(radix) else null

fun Scanner.nextFloatOrNull() : Float? = if (hasNextFloat()) nextFloat() else null

fun Scanner.nextDoubleOrNull() : Double? = if (hasNextDouble()) nextDouble() else null