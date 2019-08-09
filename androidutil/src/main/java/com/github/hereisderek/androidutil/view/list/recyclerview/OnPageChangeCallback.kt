package com.github.hereisderek.androidutil.view.list.recyclerview

import androidx.annotation.CallSuper
import androidx.viewpager2.widget.ViewPager2

/**
 *
 * User: derekzhu
 * Date: 2019-07-11 13:25
 * Project: Imagician Demo
 */


/**
 * Callback interface for responding to changing state of the selected page.
 * @see [ViewPager2.OnPageChangeCallback]
 *
 * however, this also provides api for getting previous position through [onPageSelected] callback
 */
open class OnPageChangeCallback : ViewPager2.OnPageChangeCallback() {
    private var previousPosition = -1
    override fun onPageScrollStateChanged(state: Int) {
        super.onPageScrollStateChanged(state)
    }

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
        super.onPageScrolled(position, positionOffset, positionOffsetPixels)
    }

    @CallSuper
    override fun onPageSelected(position: Int) {
        super.onPageSelected(position)
        onPageSelected(previousPosition, position)
        previousPosition = position
    }

    open fun onPageSelected(from: Int, to: Int) {

    }
}