package com.github.hereisderek.androidutil.misc

import android.content.ContentResolver
import android.content.ContentUris
import android.content.Context
import android.content.res.Resources
import android.net.Uri
import androidx.annotation.AnyRes
import androidx.annotation.NonNull
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

    fun getType(uri: Uri) = uri.uriType



    fun getResUri2(resId: Int, context: Context) : Uri {
        return Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + context.packageName + "/" + resId)
    }

    /**
     * get uri to any resource type
     * @param context - context
     * @param resId - resource id
     * @throws Resources.NotFoundException if the given ID does not exist.
     * @return - Uri to resource by given id
     */
    @Throws(Resources.NotFoundException::class)
    fun getResUri(@NonNull context: Context, @AnyRes resId: Int): Uri {
        /** Return a Resources instance for your application's package.  */
        val res: Resources = context.resources
        /** return uri  */
        return Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + res.getResourcePackageName(resId) + '/' + res.getResourceTypeName(resId) + '/' + res.getResourceEntryName(resId))
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

    fun parseOrNull(uriStr: String?) : Uri? = try {
        if (uriStr.isNullOrEmpty()) null
        else Uri.parse(uriStr)
    } catch (e: Exception) {
        null
    }

    fun toUriFromAssetName(assetName: String) =
        toUriFromString(ASSET_SCHEME + assetName)


    fun toUriIdFromContentUri(uri: Uri) : Long {
        return if (uri.uriType != UriType.ContentUri) -1L else try {
            ContentUris.parseId(uri)
        } catch (e: Exception) {
            -1L
        }
    }
}