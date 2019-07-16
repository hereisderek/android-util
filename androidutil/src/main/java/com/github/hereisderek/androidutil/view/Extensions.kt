package com.github.hereisderek.androidutil.view

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.os.Build
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.core.view.doOnLayout
import timber.log.Timber
import kotlin.coroutines.resume
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

suspend fun View.getSuspendWidth() : Int = suspendCoroutine{ susp ->
    doOnLayout {
        susp.resume(width)
    }
}


suspend fun View.getSuspendHeight() : Int = suspendCoroutine{ susp ->
    doOnLayout {
        susp.resume(height)
    }
}

val View.activity: Activity?
    get() {
        var ctx = context
        while (ctx != null){
            when (ctx) {
                is Activity -> return ctx
                else -> ctx = (ctx as? ContextWrapper)?.baseContext ?: return null
            }
        }
        return null
    }


fun View.getOrGenerateId() : Int {
    if (id != View.NO_ID) return id
    return ViewCompat.generateViewId().also { id = it }
}

fun View.doOnEveryParent(block: ViewGroup.()->Unit){
    var p = parent
    while (p is ViewGroup) {
        Timber.d("got parent view group:${p.javaClass.simpleName}")
        block.invoke(p)
        p = p.parent
    }
}