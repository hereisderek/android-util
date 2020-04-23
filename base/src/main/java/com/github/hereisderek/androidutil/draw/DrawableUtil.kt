package com.github.hereisderek.androidutil.draw

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import androidx.annotation.DrawableRes
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat


@SuppressLint("NewApi")
fun Context.getDrawableCompat(@DrawableRes id: Int) = this.getDrawableCompat(id, null)


@RequiresApi(Build.VERSION_CODES.LOLLIPOP)
fun Context.getDrawableCompat(@DrawableRes id: Int, tintColor: Int? = null) = ContextCompat.getDrawable(this, id)?.let {
    if (tintColor == null) it else {
        require(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            "setTint requires at least api LOLLIPOP"
        }
        DrawableCompat.wrap(it).mutate().apply { setTint(tintColor) }
    }
}
