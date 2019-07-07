package com.github.hereisderek.androidutil.util.view

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.view.View
import android.view.ViewManager
import android.widget.LinearLayout
import androidx.core.view.ViewCompat
import androidx.core.view.doOnPreDraw
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

/**
 *
 * User: derekzhu
 * Date: 2019-07-05 16:06
 * Project: Imagician Demo
 */

val Context.themeId: Int
    get() = (this as? Activity)?.componentName?.let {
        packageManager.getActivityInfo(it, 0).themeResource
    } ?: 0

fun View.setTouchThrough(touchThrough: Boolean) {
    isClickable = !touchThrough
    isFocusable = !touchThrough
}

@Suppress("LocalVariableName")
suspend fun View.getSuspendWidth() : Int = suspendCoroutine{ susp ->
    if (width != 0) {
        susp.resume(width)
        return@suspendCoroutine
    }
    doOnPreDraw {
        val _width = width
        if (_width == 0) {
            susp.resumeWithException(Exception("Unable to get view width"))
        } else susp.resume(_width)
    }
}


@Suppress("LocalVariableName")
suspend fun View.getSuspendHeight() : Int = suspendCoroutine{ susp ->
    if (height != 0) {
        susp.resume(height)
        return@suspendCoroutine
    }
    doOnPreDraw {
        val _height = height
        if (_height == 0) {
            susp.resumeWithException(Exception("Unable to get view width"))
        } else susp.resume(_height)
    }
}


val View.activity: Activity?
    get() {
        var ctx = context
        while (true) {
            if (!ContextWrapper::class.java.isInstance(ctx)) {
                return null
            }
            if (Activity::class.java.isInstance(ctx)) {
                return ctx as Activity
            }
            ctx = (ctx as ContextWrapper).baseContext
        }
    }

fun View.getOrGenerateId() : Int {
    if (id != View.NO_ID) return id
    return ViewCompat.generateViewId().also { id = it }
}