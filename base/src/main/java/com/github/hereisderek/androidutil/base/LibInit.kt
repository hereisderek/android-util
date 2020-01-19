package com.github.hereisderek.androidutil.base

import android.content.ContentProvider
import android.content.ContentValues
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.StrictMode
import com.github.hereisderek.androidutil.view.ViewUtil
import com.github.hereisderek.androidutil.color.ColorUtil
import com.github.hereisderek.androidutil.misc.StubContentProvider
import com.github.hereisderek.androidutil.misc.initTimber
import com.github.hereisderek.androidutl.base.BuildConfig
import timber.log.Timber

/**
 *
 * User: derekzhu
 * Date: 2019-06-26 14:18
 * Project: AndroidUtil
 */


class LibInit : StubContentProvider() {

    override fun onCreate(): Boolean {
        context?.also {context ->
            ColorUtil.init(context)
            ViewUtil.init(context)
        }

        return false
    }
    
    companion object {
        private val debugTree by lazy(LazyThreadSafetyMode.NONE) { Timber.DebugTree() }

        fun initTimber(enable: Boolean = BuildConfig.DEBUG){
            if (enable) {
                // if no debug tree
                if (Timber.treeCount() == 0 || Timber.forest().count { it is Timber.DebugTree } == 0) {
                    Timber.plant(debugTree)
                    Timber.d("Timber initialized")
                }
            }
        }

        fun setupStrictMode() {
            StrictMode.setThreadPolicy(StrictMode.ThreadPolicy.Builder()
                .detectAll()
                .detectDiskReads()
                .detectDiskWrites()
                .detectNetwork()
                .penaltyLog()
                .build())

            StrictMode.setVmPolicy(StrictMode.VmPolicy.Builder()
                .detectAll()
                .apply {
                    if (Build.VERSION.SDK_INT >= 18) detectFileUriExposure()
                }
                .detectLeakedSqlLiteObjects()
                .detectLeakedClosableObjects()
                .penaltyLog()
                .penaltyDeath()
                .apply {
                    if (Build.VERSION.SDK_INT >= 26) {
                        detectContentUriWithoutPermission()
                    }
                }
                .build())
        }

    }
}