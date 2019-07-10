package com.github.hereisderek.androidutil.util.view

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.os.Build
import android.view.View
import android.view.ViewManager
import android.widget.LinearLayout
import androidx.core.view.ViewCompat
import androidx.core.view.doOnPreDraw
import com.github.hereisderek.androidutil.util.SDK_INT
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

/** as a replacement for [ViewCompat.isLaidOut] */
@Suppress("FunctionName")
fun View.laidOut() : Boolean {
    return if (Build.VERSION.SDK_INT >= 19) {
        ViewCompat.isLaidOut(this)
    } else width > 0 || height > 0
}

@Suppress("LocalVariableName")
suspend fun View.getSuspendWidth() : Int = suspendCoroutine{ susp ->
    if (laidOut()) {
        susp.resume(width)
        return@suspendCoroutine
    }
    doOnPreDraw {
        susp.resume(width)
    }
}


@Suppress("LocalVariableName")
suspend fun View.getSuspendHeight() : Int = suspendCoroutine{ susp ->
    if (laidOut()) {
        susp.resume(height)
        return@suspendCoroutine
    }
    doOnPreDraw {
        susp.resume(height)
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