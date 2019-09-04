package com.github.hereisderek.androidutil.misc

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.content.ContentUris
import android.content.Context
import android.database.Cursor
import android.database.MergeCursor
import android.graphics.Bitmap
import android.graphics.Point
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Build
import android.os.CancellationSignal
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.util.Size
import androidx.annotation.WorkerThread
import com.github.hereisderek.androidutil.coroutine.pmap
import com.github.hereisderek.androidutil.misc.UriUtil.toUriIdFromContentUri
import com.github.hereisderek.androidutil.misc.UriUtil.uriType
import kotlinx.coroutines.CancellationException
import timber.log.Timber
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine
import java.io.File


/**
 *
 * User: derekzhu
 * Date: 2019-06-28 12:45
 * Project: Imagician Demo
 */

@Suppress("DEPRECATION")
object BitmapUtil {

    enum class SortOrder(val value: String) {
        DATE_DEC(MediaStore.Images.ImageColumns.DATE_TAKEN + " DESC")
    }


    enum class ContentUri(val uri: Uri) {
        IMAGE_EXTERNAL(MediaStore.Images.Media.EXTERNAL_CONTENT_URI),
        IMAGE_INTERNAL(MediaStore.Images.Media.INTERNAL_CONTENT_URI),
        VIDEO_EXTERNAL(MediaStore.Video.Media.EXTERNAL_CONTENT_URI),
        VIDEO_INTERNAL(MediaStore.Video.Media.INTERNAL_CONTENT_URI),
    }


    // most common info for an image
    val IMAGE_PROJECTION = arrayOf(
        MediaStore.Images.ImageColumns._ID,
        MediaStore.Images.ImageColumns.DATE_TAKEN,
        MediaStore.Images.ImageColumns.DATE_MODIFIED,
        MediaStore.Images.ImageColumns.MIME_TYPE,
        MediaStore.Images.ImageColumns.ORIENTATION,
        MediaStore.Images.ImageColumns.DATA,
        OpenableColumns.DISPLAY_NAME
    )

    private val IMAGE_ID_PROJECTION = arrayOf(MediaStore.Images.ImageColumns._ID)



    /// thumbnails

    private const val DEFAULT_THUMBNAIL_WIDTH = 800
    private const val DEFAULT_THUMBNAIL_HEIGHT = 600

    @TargetApi(21)
    private val DEFAULT_THUMBNAIL_SIZE = Size(DEFAULT_THUMBNAIL_WIDTH, DEFAULT_THUMBNAIL_HEIGHT)

    // MINI_KIND: 512 x 384 thumbnail
    // MICRO_KIND: 96 x 96 thumbnail
    private const val DEFAULT_THUMBNAIL_KIND = MediaStore.Images.Thumbnails.MINI_KIND


    @WorkerThread
    suspend fun getThumbnailForUris(context: Context, uri: Uri, preferredSize: Point? = null) : Bitmap {
        return if (Build.VERSION.SDK_INT >= 29) {
            val size = if (preferredSize == null) DEFAULT_THUMBNAIL_SIZE else Size(preferredSize.x, preferredSize.y)
            getThumbnailForUris29(context, uri, size)
        } else {
            val kind = when {
                preferredSize == null -> DEFAULT_THUMBNAIL_KIND
                preferredSize.x <= 96 && preferredSize.y <= 96 -> MediaStore.Images.Thumbnails.MINI_KIND
                else -> MediaStore.Images.Thumbnails.MICRO_KIND
            }
            getThumbnailForUrisPre29(context, uri, kind)
        }
    }

    @WorkerThread
    private suspend fun getThumbnailForUrisPre29(context: Context, uri: Uri, kind: Int) : Bitmap {
        return MediaStore.Images.Thumbnails.getThumbnail(context.contentResolver, getImageIdByUri(context, uri), kind, null)
    }

    @TargetApi(29)
    @WorkerThread
    private fun getThumbnailForUris29(context: Context, uri: Uri, size: Size) : Bitmap {
        return context.contentResolver.loadThumbnail(uri, size, CancellationSignal().apply {
            setOnCancelListener {
                Timber.e("getThumbnailForUris21 canceled")
                throw CancellationException("getThumbnailForUris21 canceled")
            }
        })
    }


    /*fun getThumbnailUriForUri(context: Context, uris: List<Uri>, kind: Int) : List<Uri> {
        // MediaStore.Images.Thumbnails.queryMiniThumbnails(context.contentResolver, uri, kind, )
    }

    fun getThumbnailUriForUri(context: Context, uri: Uri, kind: Int) : Uri {
        return MediaStore.Images.Thumbnails.queryMiniThumbnails(context.contentResolver, uri, kind, IMAGE_ID_PROJECTION).use {
            it.moveToFirst()

        }
    }*/

    /*fun getContentUri(context: Context, imageFile: File): Uri {
        val filePath = imageFile.getAbsolutePath()
        val cursor = context.contentResolver.query(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            arrayOf(MediaStore.Audio.Media._ID),
            MediaStore.Audio.Media.DATA + "=? ",
            arrayOf(filePath), null)

        if (cursor != null && cursor.moveToFirst()) {
            val id = cursor.getInt(cursor
                .getColumnIndex(MediaStore.MediaColumns._ID))
            val baseUri = Uri.parse("content://media/external/audio/media")
            return Uri.withAppendedPath(baseUri, "" + id)
        }
    }*/


    // fun createThumbnail() {
    //     // ThumbnailUtils.createImageThumbnail()
    // }


    suspend fun getImageIdByUri(context: Context, uris: List<Uri>) : List<Long> = uriToContentUri(context, uris).map {
        toUriIdFromContentUri(it)
    }

    suspend fun getImageIdByUri2(context: Context, uris: List<Uri>) : List<Long> {
        val contentUris = uriToContentUri(context, uris)
        return getCursorForUriList(context, contentUris, IMAGE_ID_PROJECTION, null, null) {
            it.getLong(it.getColumnIndex(MediaStore.MediaColumns._ID))
        }
    }

    suspend fun getImageIdByUri(context: Context, uri: Uri) : Long = getImageIdByUri(context, listOf(uri)).first()

    /**
     * convert of list of mixed uri type to a list of content uri
     * @param context
     * @param uris
     * @return
     */
    suspend fun uriToContentUri(context: Context, uris: List<Uri>) : List<Uri> {
        val needConversion = uris.firstOrNull { it.uriType == UriUtil.UriType.FileUri } != null
        if (!needConversion) return uris
        return uris.map { uriToContentUri(context, it) }
    }


    suspend fun uriToContentUri(context: Context, uri: Uri) : Uri {
        return if (uri.uriType == UriUtil.UriType.FileUri && !uri.path.isNullOrEmpty()) suspendCoroutine {
            MediaScannerConnection.scanFile(context, arrayOf(uri.path), null) { path, uri ->
                // if (uri == null) { Timber.e("unable to scan image, uri is null. image path:${uri.path}") }
                it.resume(uri)
            }
        } else uri
    }



    /**
     * convert a list of uri (could contain a mix of filetype and content type) to a contentType and file path and operate on it
     * @param T
     * @param context
     * @param list
     * @param saveTo
     * @return
     */
    suspend fun <T> urisToContentUris(context: Context, list: List<Uri>, saveTo: (path: String?, uri: Uri) -> T): List<T> = list.pmap {
        when (it.uriType) {
            UriUtil.UriType.FileUri -> suspendCoroutine { continuation ->
                MediaScannerConnection.scanFile(context, arrayOf(it.path), null) { path, uri ->
                    if (uri == null) { Timber.e("unable to scan image, uri is null. image path:${it.path}") }
                    continuation.resume(saveTo(path, uri))
                }
            }
            else -> saveTo(it.path, it)
        }
    }


    @JvmOverloads
    suspend fun <T>getCursorForUriList(
        context: Context,
        uris: List<Uri>,
        projection: Array<String>? = null,
        sortOrder: String? = BitmapUtil.SortOrder.DATE_DEC.value,
        rootUri: ContentUri? = null,
        useBlock: (cursor: Cursor) -> T) : ArrayList<T> {
        return getCursorForUriList(context, uris, projection, sortOrder, rootUri)?.use {
            it.toArrayList(useBlock)
        } ?: ArrayList()
    }


    @SuppressLint("Recycle")
    @JvmOverloads
    suspend fun getCursorForUriList(
        context: Context,
        uris: List<Uri>,
        projection: Array<String>? = null,
        sortOrder: String? = BitmapUtil.SortOrder.DATE_DEC.value,
        rootUri: ContentUri? = null
    ) : Cursor? {
        if (uris.isEmpty()) return null

        // convert to content uri if needed
        val contentUriList = uriToContentUri(context, uris)

        val selection = StringBuilder("${MediaStore.Images.Media._ID} IN ").apply {
            val size = contentUriList.size
            contentUriList.forEachIndexed { index, uri ->
                if (index == 0) append("(")
                append(ContentUris.parseId(uri).toString())
                append(if (index == size - 1) ")" else " ,")
            }
        }.toString()

        Timber.d("getCursorForUriListAsync selection:$selection")


        return if (rootUri == null) {
            MergeCursor(arrayOf(
                context.contentResolver.query(BitmapUtil.ContentUri.IMAGE_INTERNAL.uri, projection, selection, null, sortOrder),
                context.contentResolver.query(BitmapUtil.ContentUri.IMAGE_EXTERNAL.uri, projection, selection, null, sortOrder)
            ))
        } else {
            context.contentResolver.query(rootUri.uri, projection, selection, null, sortOrder)
        }
    }





    /* BitmapFactory.Options */
    val Bitmap.Config.bytePerPixel : Int? @TargetApi(Build.VERSION_CODES.O) get() =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && this == Bitmap.Config.RGBA_F16) 8 else when(this){
            Bitmap.Config.ALPHA_8 -> 1
            Bitmap.Config.RGB_565, Bitmap.Config.ARGB_4444 -> 2
            Bitmap.Config.ARGB_8888 -> 4
            else -> null
        }







    /// get random images
    fun getImagesUriFromStorage(
        context: Context,
        size: Int,
        skip: Int = 0,
        sortOrder: String = SortOrder.DATE_DEC.value,
        uriType: UriUtil.UriType = UriUtil.UriType.ContentUri) : ArrayList<Uri> {

        val columns = IMAGE_PROJECTION
        val cursor = context.contentResolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            columns, null, null, sortOrder
        )
        val result = ArrayList<Uri>()
        cursor?.use {cursor ->
            if (cursor.moveToPosition(skip)) {
                for (i in 0 until size) {
                    val imageContentUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        cursor.getLong(cursor.getColumnIndex(MediaStore.Images.ImageColumns._ID)))

                    val filePath = cursor.getString(cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA))
                    val imageFileUri = Uri.fromFile(File(filePath))

                    result.add(if (uriType == UriUtil.UriType.ContentUri) imageContentUri else imageFileUri )

                    if (cursor.isLast || !cursor.moveToNext()) break
                }
            }
        }
        return result
    }


}