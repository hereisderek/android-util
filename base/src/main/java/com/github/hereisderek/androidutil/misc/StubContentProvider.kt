package com.github.hereisderek.androidutil.misc

import android.content.ContentProvider
import android.content.ContentValues
import android.database.Cursor
import android.net.Uri

open class StubContentProvider : ContentProvider() {
    override fun onCreate(): Boolean = false

    override fun insert(uri: Uri, values: ContentValues?): Uri? = null

    override fun query(uri: Uri, projection: Array<String>?, selection: String?, selectionArgs: Array<String>?, sortOrder: String?): Cursor? = null

    override fun update(uri: Uri, values: ContentValues?, selection: String?, selectionArgs: Array<String>?): Int = 0

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<String>?): Int = 0

    override fun getType(uri: Uri): String? = null
}