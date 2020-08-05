package com.github.hereisderek.androidutil.misc

import android.content.ClipboardManager
import android.content.Context
import android.net.ConnectivityManager

object Managers {

    val Context.clipboardManager get() =
        applicationContext.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager

    val Context.connectivityManager get() =
        applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
}