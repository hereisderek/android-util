package com.github.hereisderek.androidutil.misc

import android.app.ActivityManager
import android.content.Context
import android.os.Debug
import timber.log.Timber

/**
 *
 * User: derekzhu
 * Date: 2019-06-26 16:32
 * Project: AndroidUtil
 */


object MemUtil {
    // https://stackoverflow.com/questions/2630158/detect-application-heap-size-in-android/2634738#2634738
    val maxMemory get() = Runtime.getRuntime().maxMemory()

    val usedMemory get() = Runtime.getRuntime().let { it.totalMemory() - it.freeMemory() }

    val availableMemory get() = maxMemory - usedMemory

    fun getMemoryClass(context: Context) = (context.applicationContext.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager).memoryClass


    fun printMemDebugInfo(context: Context? = null) {
        val memoryInfo = Debug.MemoryInfo()
        Debug.getMemoryInfo(memoryInfo)
        val maxMemory = (Runtime.getRuntime().maxMemory() / 1024).toInt()
        val freeMemory = (Runtime.getRuntime().freeMemory() / 1024).toInt()

        val memMessage = if (context == null) {
            String.format(
                "Free=%d kB, MaxMem=%d kB, Memory: Pss=%.2f MB, Private=%.2f MB, Shared=%.2f MB",
                freeMemory, maxMemory, memoryInfo.getTotalPss() / 1024.0, memoryInfo.getTotalPrivateDirty() / 1024.0, memoryInfo.getTotalSharedDirty() / 1024.0)
        } else {
            String.format("Free=%s, MaxMem=%s, Memory: Pss=%s, Private=%s, Shared=%s",
                byteToString(context, freeMemory),
                byteToString(context, maxMemory),
                byteToString(context, memoryInfo.totalPss),
                byteToString(context, memoryInfo.totalPrivateDirty),
                byteToString(context, memoryInfo.totalSharedDirty)
            )
        }
        Timber.i("memDebugInfo: $memMessage")
    }

    fun byteToString(context: Context, byteSize: Number) = android.text.format.Formatter.formatFileSize(context, byteSize.toLong())
    // fun byteToString(context: Context, byteSize: Long) = android.text.format.Formatter.formatFileSize(context, byteSize)

    @JvmOverloads
    fun printMemInfo(context: Context? = null) {
        if (context == null) {
            Timber.i("meminfo maxMemory:$maxMemory, usedMemory:$usedMemory, availableMemory:$availableMemory")
        } else {
            Timber.i("meminfo maxMemory:${byteToString(context, maxMemory)}, usedMemory:${byteToString(context, usedMemory)}, availableMemory:${byteToString(context, availableMemory)}")
        }
    }
}