package com.github.hereisderek.androidutil.ext

fun String?.toBooleanOrNull() : Boolean? = if (
    "true".equals(this, true) || "false".equals(this, true)
) { this?.toBoolean() } else null

