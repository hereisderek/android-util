package com.github.hereisderek.androidutil.util.color

import android.content.Context
import android.graphics.Color
import androidx.annotation.ColorInt
import androidx.annotation.IntRange
import androidx.core.graphics.ColorUtils
import com.github.hereisderek.androidutil.R
import timber.log.Timber

/**
 *
 * User: derekzhu
 * Date: 2019-06-26 16:08
 * Project: AndroidUtil
 */


object ColorUtil {

    private var rainbowColors : IntArray? = null

    private var currentRainbowColorIndex : Int = -1
        @Synchronized get() = rainbowColors?.let { colors ->
            field = (++field) % colors.size
            field
        } ?: -1

    fun init(context: Context) {
        getRainbowColors(context)
    }

    @ColorInt
    fun getRainbowColors(context: Context) : IntArray {
        return rainbowColors ?: context.resources.getIntArray(R.array.rain_bow_colors).also {
            rainbowColors = it
        }
    }

    @ColorInt
    fun getRandomRainbowColor(context: Context) : Int = getRainbowColors(context).random()

    @ColorInt
    fun getRandomRainbowColor() : Int? = rainbowColors?.random()

    @ColorInt
    fun getNextRainBowColor(context: Context) : Int = getRainbowColors(context)[currentRainbowColorIndex]

    @ColorInt
    fun getNextRainBowColor() : Int? = rainbowColors?.get(currentRainbowColorIndex)


    fun getColorFromProgress(progress: Int): Int {
        var color1 = 0
        var color2 = 0
        var color = 0
        var p = progress.toFloat()
        if (progress <= 10)
        /* black to red */ {
            color1 = 0
            color2 = 0xff0000
            p = progress / 10.0f
        } else if (progress <= 25)
        /* red to yellow */ {
            color1 = 0xff0000
            color2 = 0xffff00
            p = (progress - 10) / 15.0f
        } else if (progress <= 40)
        /* yellow to lime green */ {
            color1 = 0xffff00
            color2 = 0x00ff00
            p = (progress - 25) / 15.0f
        } else if (progress <= 55)
        /* lime green to aqua */ {
            color1 = 0x00ff00
            color2 = 0x00ffff
            p = (progress - 40) / 15.0f
        } else if (progress <= 70)
        /* aqua to blue */ {
            color1 = 0x00ffff
            color2 = 0x0000ff
            p = (progress - 55) / 15.0f
        } else if (progress <= 90)
        /* blue to fuchsia */ {
            color1 = 0x0000ff
            color2 = 0x00ff00
            p = (progress - 70) / 20.0f
        } else if (progress <= 98)
        /* fuchsia to white */ {
            color1 = 0x00ff00
            color2 = 0xff00ff
            p = (progress - 90) / 8.0f
        } else {
            color1 = 0xffffff
            color2 = 0xffffff
            p = 1.0f
        }

        val r1 = color1 shr 16 and 0xff
        val r2 = color2 shr 16 and 0xff
        val g1 = color1 shr 8 and 0xff
        val g2 = color2 shr 8 and 0xff
        val b1 = color1 and 0xff
        val b2 = color2 and 0xff

        val r3 = (r2 * p + r1 * (1.0f - p)).toInt()
        val g3 = (g2 * p + g1 * (1.0f - p)).toInt()
        val b3 = (b2 * p + b1 * (1.0f - p)).toInt()

        color = r3 shl 16 or (g3 shl 8) or b3

        return color
    }


    /**
     * This function returns the calculated in-between value for a color
     * given integers that represent the start and end values in the four
     * bytes of the 32-bit int. Each channel is separately linearly interpolated
     * and the resulting calculated values are recombined into the return value.
     *
     * @param fraction The fraction from the starting to the ending values
     * @param startValue A 32-bit int value representing colors in the
     * separate bytes of the parameter
     * @param endValue A 32-bit int value representing colors in the
     * separate bytes of the parameter
     * @return A value that is calculated to be the linearly interpolated
     * result, derived by separating the start and end values into separate
     * color channels and interpolating each one separately, recombining the
     * resulting values in the same way.
     */
    private fun interpolateColor(fraction: Float, startValue: Int, endValue: Int): Int {
        val startA = startValue shr 24 and 0xff
        val startR = startValue shr 16 and 0xff
        val startG = startValue shr 8 and 0xff
        val startB = startValue and 0xff
        val endA = endValue shr 24 and 0xff
        val endR = endValue shr 16 and 0xff
        val endG = endValue shr 8 and 0xff
        val endB = endValue and 0xff
        return startA + (fraction * (endA - startA)).toInt() shl 24 or
                (startR + (fraction * (endR - startR)).toInt() shl 16) or
                (startG + (fraction * (endG - startG)).toInt() shl 8) or
                startB + (fraction * (endB - startB)).toInt()
    }

    fun getColorAlpha(@ColorInt color: Int) = (Color.alpha(color)).also {
        Timber.d("get alpha of color: $color, alpha:$it")
    }

    fun setColorAlpha(@ColorInt color: Int, @IntRange(from = 0x0, to = 0xFF)  alpha: Int) = ColorUtils.setAlphaComponent(color, alpha).also {
        Timber.d("setColorAlpha color:$color, alpha:$alpha")
    }

    fun getOpaqueColor(@ColorInt color: Int) = setColorAlpha(color, 0xFF)





    val ALPHA_CHANNEL: Int = 24
    val RED_CHANNEL: Int = 16
    val GREEN_CHANNEL: Int = 8
    val BLUE_CHANNEL: Int = 0

    // using:
    var red: Byte = (0xff).toByte()
    var green: Byte = (0xff).toByte()
    var blue: Byte = (0xff).toByte()
    var alpha: Byte = (0xff).toByte()


    // https://youtrack.jetbrains.com/issue/KT-2780
    fun changeBlue(color: Int, action: (blue: Int) -> Int) : Int {
        val blue = color ushr BLUE_CHANNEL and 0xff
        val newBlue = action.invoke(blue)
        return (color and 0xffffff00.toInt()) or (newBlue shl BLUE_CHANNEL)
    }
}