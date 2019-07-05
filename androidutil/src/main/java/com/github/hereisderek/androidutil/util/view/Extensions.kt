package com.github.hereisderek.androidutil.util.view

import android.app.Activity
import android.content.Context
import android.view.ViewManager
import android.widget.LinearLayout

/**
 *
 * User: derekzhu
 * Date: 2019-07-05 16:06
 * Project: Imagician Demo
 */

val Context.themeId : Int get() = (this as? Activity)?.componentName?.let {packageManager.getActivityInfo(it, 0).themeResource} ?: 0