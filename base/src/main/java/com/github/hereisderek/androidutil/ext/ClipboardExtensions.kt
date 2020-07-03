package com.github.hereisderek.androidutil.ext

import android.content.ClipData
import android.content.Context
import android.net.Uri
import com.github.hereisderek.androidutil.misc.Managers.clipboardManager

fun Context.copyTextToClipboard(value: String) {
    clipboardManager.setPrimaryClip(ClipData.newPlainText("text", value))
}

fun Context.copyUriToClipboard(uri: Uri) {
    clipboardManager.setPrimaryClip(ClipData.newUri(contentResolver, "uri", uri))
}

fun Context.getTextFromClipboard(): CharSequence? {
    val clipData = clipboardManager.primaryClip ?: return null
    return if (clipData.itemCount > 0) {
        clipData.getItemAt(0).coerceToText(this)
    } else null
}

fun Context.getUriFromClipboard(): Uri? {
    val clipData = clipboardManager.primaryClip ?: return null
    return if (clipData.itemCount > 0) {
        clipData.getItemAt(0).uri
    } else null
}