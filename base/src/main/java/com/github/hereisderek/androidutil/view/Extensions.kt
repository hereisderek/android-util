package com.github.hereisderek.androidutil.view

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.graphics.Rect
import android.os.Build
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.annotation.Size
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

fun View.doOnEveryParent(breakIf: ((parent: ViewGroup) -> Boolean)? = null, block: ViewGroup.()->Unit){
    var p = parent
    while (p is ViewGroup) {
        if (breakIf != null && breakIf.invoke(p)) break
        block.invoke(p)
        p = p.parent
    }
}

fun View.isPointInside(
    rawX: Int,
    rawY: Int,
    outRec: Rect = Rect(),
    @Size(2)  location: IntArray = IntArray(2)
    ) : Boolean {
    if (location.size != 2)
        throw IllegalArgumentException("location IntArray must be an array of two integers")

    getDrawingRect(outRec)
    getLocationOnScreen(location)
    outRec.offset(location[0], location[1])
    return outRec.contains(rawX, rawY)
}

fun hideKeyboard(view: View){
    (view.context.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager)
        ?.hideSoftInputFromWindow(view.windowToken, 0)
}

fun Activity.hideKeyboard() {
    currentFocus?.also { focus ->
        hideKeyboard(focus)
    }
}