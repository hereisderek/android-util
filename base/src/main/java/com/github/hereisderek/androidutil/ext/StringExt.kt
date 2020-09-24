package com.github.hereisderek.androidutil.ext

import com.github.hereisderek.androidutil.EMPTY_STRING


fun String?.toBooleanOrNull() : Boolean? = if (
    "true".equals(this, true) || "false".equals(this, true)
) { this?.toBoolean() } else null

fun <T> Appendable.appendElement(element: T, transform: ((T) -> CharSequence)?) {
    when {
        transform != null -> append(transform(element))
        element is CharSequence? -> append(element)
        element is Char -> append(element)
        else -> append(element.toString())
    }
}

fun String.removeWhiteSpace() : String = replace("\\s".toRegex(), EMPTY_STRING)