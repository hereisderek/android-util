package com.github.hereisderek.androidutil.base

import android.content.ContentProvider
import android.content.ContentValues
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.StrictMode
import com.github.hereisderek.androidutil.BuildConfig
import com.github.hereisderek.androidutil.util.ViewUtil
import com.github.hereisderek.androidutil.util.color.ColorUtil
import timber.log.Timber

/**
 *
 * User: derekzhu
 * Date: 2019-06-26 14:18
 * Project: AndroidUtil
 */


class LibInit : ContentProvider() {

    override fun onCreate(): Boolean {
        setupTimber()
        context?.also {context ->
            ColorUtil.init(context)
            ViewUtil.init(context)
        }

        return false
    }


    /// helpers




    /// stubs
    override fun insert(uri: Uri, values: ContentValues?): Uri? = null

    override fun query(uri: Uri, projection: Array<String>?, selection: String?, selectionArgs: Array<String>?, sortOrder: String?): Cursor? = null

    override fun update(uri: Uri, values: ContentValues?, selection: String?, selectionArgs: Array<String>?): Int = 0

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<String>?): Int = 0

    override fun getType(uri: Uri): String? = null




    companion object {
        private val debugTree by lazy(LazyThreadSafetyMode.NONE) { Timber.DebugTree() }
        private fun setupTimber(){
            if (BuildConfig.DEBUG) {
                if (Timber.treeCount() == 0) {
                    Timber.plant(debugTree)
                    Timber.d("Timber initialized")
                }
            }
        }

        private fun setupStrickMode() {
            if (!BuildConfig.DEBUG) return
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