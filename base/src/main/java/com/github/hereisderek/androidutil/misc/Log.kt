package com.github.hereisderek.androidutil.misc

import com.github.hereisderek.androidutil.base.LibInit
import com.github.hereisderek.androidutl.base.BuildConfig
import timber.log.Timber

/**
 *
 * User: derekzhu
 * Date: 2019-09-05 11:17
 * Project: Safety Net
 */


@Deprecated("use LibInit.initTimber", replaceWith = ReplaceWith("LibInit.initTimber"))
fun initTimber(enable: Boolean = BuildConfig.DEBUG) = LibInit.initTimber(enable)