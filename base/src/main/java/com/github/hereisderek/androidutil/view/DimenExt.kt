package com.github.hereisderek.androidutil.view

import android.content.Context
import android.content.res.Resources
import android.util.DisplayMetrics
import android.util.TypedValue
import android.view.View
import android.view.ViewConfiguration
import androidx.annotation.IntDef
import androidx.fragment.app.Fragment

/**
 * Author: Derek Zhu
 */

@Retention(AnnotationRetention.SOURCE)
@IntDef(value = [TypedValue.COMPLEX_UNIT_PX, TypedValue.COMPLEX_UNIT_DIP, TypedValue.COMPLEX_UNIT_SP, TypedValue.COMPLEX_UNIT_PT,  TypedValue.COMPLEX_UNIT_IN, TypedValue.COMPLEX_UNIT_MM])
annotation class TypedValueUnit

val View.displayMetrics : DisplayMetrics get() = resources.displayMetrics
val Context.displayMetrics : DisplayMetrics get() = resources.displayMetrics

fun DisplayMetrics.convert(
    sourceUnit: Int,
    value: Float,
    targetUnit: Int = TypedValue.COMPLEX_UNIT_PX
) : Float {
    val px = TypedValue.applyDimension(sourceUnit, value, this)
    return if (targetUnit == TypedValue.COMPLEX_UNIT_PX) px else {
        px / TypedValue.applyDimension(targetUnit, 1f, this)
    }
}

fun View.convert(
    @TypedValueUnit sourceUnit: Int,
    value: Float,
    targetUnit: Int = TypedValue.COMPLEX_UNIT_PX
) : Float = displayMetrics.convert(sourceUnit, value, targetUnit)

fun Context.convert(
    sourceUnit: Int,
    value: Float,
    targetUnit: Int = TypedValue.COMPLEX_UNIT_PX
) : Float = displayMetrics.convert(sourceUnit, value, targetUnit)

fun Resources.convert(
    sourceUnit: Int,
    value: Float,
    targetUnit: Int = TypedValue.COMPLEX_UNIT_PX
) : Float = displayMetrics.convert(sourceUnit, value, targetUnit)

fun View.dpToPx(value: Float) : Float = convert(TypedValue.COMPLEX_UNIT_DIP, value, TypedValue.COMPLEX_UNIT_PX)
fun Context.dpToPx(value: Float) : Float = convert(TypedValue.COMPLEX_UNIT_DIP, value, TypedValue.COMPLEX_UNIT_PX)
fun Resources.dpToPx(value: Float) : Float = convert(TypedValue.COMPLEX_UNIT_DIP, value, TypedValue.COMPLEX_UNIT_PX)

fun View.spToPx(value: Float) : Float = convert(TypedValue.COMPLEX_UNIT_SP, value, TypedValue.COMPLEX_UNIT_PX)
fun Context.spToPx(value: Float) : Float = convert(TypedValue.COMPLEX_UNIT_SP, value, TypedValue.COMPLEX_UNIT_PX)
fun Resources.spToPx(value: Float) : Float = convert(TypedValue.COMPLEX_UNIT_SP, value, TypedValue.COMPLEX_UNIT_PX)



// ViewConfiguration
val Context.viewConfiguration : ViewConfiguration get() = ViewConfiguration.get(this)
val View.viewConfiguration : ViewConfiguration get() = context.viewConfiguration
val Fragment.viewConfiguration : ViewConfiguration? get() = context?.viewConfiguration

