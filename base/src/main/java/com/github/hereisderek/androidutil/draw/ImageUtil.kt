package com.github.hereisderek.androidutil.draw

import android.annotation.SuppressLint
import android.content.Context
import android.database.Cursor
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import androidx.annotation.IntDef
import androidx.core.content.FileProvider
import androidx.exifinterface.media.ExifInterface
import com.github.hereisderek.androidutil.base.FileAlreadyExistException
import com.github.hereisderek.androidutil.misc.*
import com.github.hereisderek.androidutil.misc.UriUtil.uriType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.io.*

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


    fun getImageShareAuthority(context: Context) = "${getPackageName(context)}.images_provider"


    /** orientation */

    const val ORIENTATION_0 = 0
    /** Rotate the image 90 degrees clockwise.  */
    const val ORIENTATION_90 = 90
    /** Rotate the image 180 degrees.  */
    const val ORIENTATION_180 = 180
    /** Rotate the image 270 degrees clockwise.  */
    const val ORIENTATION_270 = 270

    private val ORIENTATIONS = intArrayOf(ORIENTATION_0, ORIENTATION_90, ORIENTATION_180, ORIENTATION_270)


    @Retention(AnnotationRetention.SOURCE)
    @IntDef(value = [ORIENTATION_0, ORIENTATION_90, ORIENTATION_180, ORIENTATION_270])
    annotation class Orientation

    private val orientationColumns by lazy(LazyThreadSafetyMode.NONE) {
        arrayOf(MediaStore.Images.Media.ORIENTATION, MediaStore.Images.Media.DATA)
    }


    @SuppressLint("NewApi")
    @Suppress("NestedLambdaShadowedImplicitParameter")
    @Orientation fun getImageOrientation(context: Context, uri: Uri) : Int? {
        Timber.d("getImageOrientation uri:$uri, mainThread:$isOnMainThread")
        var filePath : String? = null

        @Orientation fun parseCursor(cursor: Cursor) : Int? {
            val orientation = cursor.getInt(0)
            filePath = cursor.getString(1)
            return if (ORIENTATIONS.contains(orientation)) orientation else null
        }

        filePath?.let {
            Timber.d("getImageOrientationQuery failed for uri:$uri, filePath:$it")
            getImageOrientationContentResolver(it)
        }


        try {
            val orientation = context.contentResolver.query(uri, orientationColumns,  null, null, null)?.use {
                if (it.moveToFirst()) { it.use { parseCursor(it) } } else null
            }
            if (orientation != null) return orientation
        } catch (e: Exception) {
            Timber.e("contentResolver.query failed:$e")
            e.printStackTrace()
        }

        if (filePath.isNullOrEmpty() && uri.uriType == UriUtil.UriType.FileUri) {
            filePath = uri.path
        }

        filePath?.also {
            val orientation = getImageOrientationContentResolver(it)
            if (orientation != null) return orientation
        }

        return try {
            context.contentResolver.openFileDescriptor(uri, "r")?.use {
                getImageOrientationContentResolver(ExifInterface(it.fileDescriptor))
            }
        } catch (e: Exception) {
            null
        }

    }

    @Orientation fun getImageOrientationContentResolver(filePath: String) : Int? {
        val (time, orientation) = measureNanoTime {
            try {
                getImageOrientationContentResolver(ExifInterface(filePath))
            } catch (e: Exception) {
                Timber.e("getImageOrientationContentResolver exception:${e.message}")
                e.printStackTrace()
                null
            }
        }
        Timber.d("getImageOrientationContentResolver filePath:$filePath, took:$time nanoseconds, mainThread:$isOnMainThread, result:$orientation")
        return orientation
    }

    @Orientation
    fun getImageOrientationContentResolver(exifInterface: ExifInterface): Int? =
        when (val orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)) {
            ExifInterface.ORIENTATION_NORMAL, ExifInterface.ORIENTATION_UNDEFINED -> ORIENTATION_0
            ExifInterface.ORIENTATION_ROTATE_90 -> ORIENTATION_90
            ExifInterface.ORIENTATION_ROTATE_180 -> ORIENTATION_180
            ExifInterface.ORIENTATION_ROTATE_270 -> ORIENTATION_270
            else -> {
                Timber.e("getImageOrientationContentResolver unrecognized orientation:$orientation")
                null
            }
        }

}