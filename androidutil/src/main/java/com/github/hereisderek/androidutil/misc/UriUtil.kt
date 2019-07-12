package com.github.hereisderek.androidutil.misc

import android.content.ContentResolver
import android.content.ContentUris
import android.content.Context
import android.net.Uri
import timber.log.Timber

/**
 *
 * User: derekzhu
 * Date: 2019-06-28 13:48
 * Project: Imagician Demo
 */


object UriUtil {
    private const val FILE_SCHEME = "file:///"
    private const val ASSET_SCHEME = "file:///android_asset/"

    enum class UriType { ResourceUri, FileUri, ContentUri, Other }

    val Uri.uriType : UriType
        get() = when (this.scheme) {
        ContentResolver.SCHEME_ANDROID_RESOURCE -> UriUtil.UriType.ResourceUri
        ContentResolver.SCHEME_FILE -> UriUtil.UriType.FileUri
        ContentResolver.SCHEME_CONTENT -> UriUtil.UriType.ContentUri
        else -> UriUtil.UriType.Other.also {
            Timber.i("Unrecognized scheme:$scheme")
        }
    }

    fun toUriFromResourceId(resId: Int, context: Context) : Uri {
        return Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + context.packageName + "/" + resId)
    }

    fun toUriFromString(uriStr: String) : Uri {
        var uri = uriStr
        if (!uri.contains("://")) {
            if (uri.startsWith("/")) {
                uri = uri.substring(1)
            }
            uri = FILE_SCHEME + uri
        }
        return Uri.parse(uri)
    }

    fun toUriFromAssetName(assetName: String) =
        toUriFromString(ASSET_SCHEME + assetName)


    fun toUriIdFromContentUri(uri: Uri) : Long {
        return ContentUris.parseId(uri)
    }
}