package com.github.hereisderek.androidutil.draw

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import androidx.core.content.FileProvider
import com.github.hereisderek.androidutil.base.FileAlreadyExistException
import com.github.hereisderek.androidutil.misc.measureNanoTime
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream

/**
 *
 * User: derekzhu
 * Date: 2019-08-06 13:17
 * Project: androidutil
 */


@Suppress("MemberVisibilityCanBePrivate")
object ImageUtil {



    suspend fun saveBitmapToFile(
        bitmap: Bitmap,
        output: File,
        overwrite: Boolean = true
    ) = withContext(Dispatchers.IO) {
        output.apply {
            val start = System.nanoTime()

            if (exists()) {
                if (!overwrite) {
                    throw FileAlreadyExistException(output)
                } else {
                    delete()
                    parentFile?.mkdirs()
                }
            }

            Timber.d("saveBitmapToFile: $absolutePath")
            var stream : OutputStream? = null
            var exception : Exception? = null
            try {
                stream = BufferedOutputStream(FileOutputStream(this))
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
            } catch (e: Exception) {
                Timber.e(e)
                exception = e
            } finally {
                stream?.flush()
                stream?.close()
                val took = System.nanoTime() - start
                val msg = "saveBitmapToFile -> bitmap saved to ${output.absolutePath} in $took nanoseconds, which is ${took / 1_000_000} milliseconds"
                if (exception != null) {
                    Timber.e("msg\nwith exception: ${exception.message}")
                } else {
                    Timber.d(msg)
                }
            }
            if (exception != null) throw exception
        }
        output
    }

    suspend fun saveBitmapToFileForUri(bitmap: Bitmap, outputFile: File, context: Context): Uri? =
        getImageUriForFile(context, saveBitmapToFile(bitmap, outputFile))

    fun getImageUriForFile(context: Context, file: File) : Uri = FileProvider.getUriForFile(context, getImageShareAuthority(context), file)

    fun getPackageName(context: Context) : String = context.packageName

    fun getImageShareAuthority(context: Context) = "${getPackageName(context)}.images_provider"
}