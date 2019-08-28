package com.github.hereisderek.androidutil.misc

import android.content.Context
import android.net.Uri
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.content.ContentUris
import android.os.Environment.getExternalStorageDirectory
import android.os.Build
import android.content.ContentResolver
import android.database.Cursor
import android.os.Environment
import androidx.annotation.RequiresApi


/**
 *
 * User: derekzhu
 * Date: 2019-08-27 18:26
 * Project: Imagician Demo
 */

/**
 * https://juejin.im/entry/5a508655f265da3e2d3340d5
 * */
@Suppress("MemberVisibilityCanBePrivate")
object FileUtil {

    @RequiresApi(Build.VERSION_CODES.KITKAT)
    fun isDocumentUri(context: Context, uri: Uri) = DocumentsContract.isDocumentUri(context, uri)

    /**
     * @param uri
     * The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    fun isExternalStorageDocument(uri: Uri): Boolean {
        return "com.android.externalstorage.documents" == uri.authority
    }

    /**
     * @param uri
     * The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    fun isDownloadsDocument(uri: Uri): Boolean {
        return "com.android.providers.downloads.documents" == uri.authority
    }

    /**
     * @param uri
     * The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    fun isMediaDocument(uri: Uri): Boolean {
        return "com.android.providers.media.documents" == uri.authority
    }

    /**
     * @param uri
     * The Uri to check.
     * @return Whether the Uri authority is Google Photos.
     */
    fun isGooglePhotosUri(uri: Uri): Boolean {
        return "com.google.android.apps.photos.content" == uri.authority
    }

    /** {@hide} */
    fun getFilePathByUri(context: Context, uri: Uri) : String? {
        when(uri.scheme) {
            ContentResolver.SCHEME_FILE -> return uri.path
            ContentResolver.SCHEME_CONTENT -> when{
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && isDocumentUri(context, uri) -> when{
                    isExternalStorageDocument(uri) -> {
                        val docId = DocumentsContract.getDocumentId(uri)

                    }
                }
            }
        }
        TODO("Not implemented")
        // return null
    }



    /** {@hide} */
    fun getFilePathByUri2(context: Context, uri: Uri): String? {
        var path: String? = null
        if (ContentResolver.SCHEME_FILE == uri.scheme) {
            path = uri.path
            return path
        }

        // 以 content:// 开头的，比如 content://media/extenral/images/media/17766
        if (ContentResolver.SCHEME_CONTENT == uri.scheme && Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            val cursor = context.contentResolver.query(uri, arrayOf(MediaStore.Images.Media.DATA), null, null, null)
            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    val columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
                    if (columnIndex > -1) {
                        path = cursor.getString(columnIndex)
                    }
                }
                cursor.close()
            }
            return path
        }
        // 4.4及之后的 是以 content:// 开头的，比如 content://com.android.providers.media.documents/document/image%3A235700
        if (ContentResolver.SCHEME_CONTENT == uri.scheme && Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if (DocumentsContract.isDocumentUri(context, uri)) {
                if (isExternalStorageDocument(uri)) {
                    // ExternalStorageProvider
                    val docId = DocumentsContract.getDocumentId(uri)
                    val split = docId.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                    val type = split[0]
                    if ("primary".equals(type, ignoreCase = true)) {
                        path = Environment.getExternalStorageDirectory().path + "/" + split[1]
                        return path
                    }
                } else if (isDownloadsDocument(uri)) {
                    // DownloadsProvider
                    val id = DocumentsContract.getDocumentId(uri)
                    val contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), java.lang.Long.valueOf(id))
                    path = getDataColumn(context, contentUri, null, null)
                    return path
                } else if (isMediaDocument(uri)) {
                    // MediaProvider
                    val docId = DocumentsContract.getDocumentId(uri)
                    val split = docId.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                    val type = split[0]
                    var contentUri: Uri? = null
                    if ("image" == type) {
                        contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                    } else if ("video" == type) {
                        contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                    } else if ("audio" == type) {
                        contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
                    }
                    val selection = "_id=?"
                    val selectionArgs = arrayOf(split[1])
                    path = getDataColumn(context, contentUri, selection, selectionArgs)
                    return path
                }
            }
        }
        return null
    }

    private fun getDataColumn(context: Context, uri: Uri?, selection: String?, selectionArgs: Array<String>?): String? {
        var cursor: Cursor? = null
        val column = "_data"
        val projection = arrayOf(column)
        try {
            cursor = context.contentResolver.query(uri!!, projection, selection, selectionArgs, null)
            if (cursor != null && cursor!!.moveToFirst()) {
                val column_index = cursor!!.getColumnIndexOrThrow(column)
                return cursor!!.getString(column_index)
            }
        } finally {
            if (cursor != null) cursor!!.close()
        }
        return null
    }
}