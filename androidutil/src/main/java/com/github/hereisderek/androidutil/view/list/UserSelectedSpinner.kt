package com.github.hereisderek.androidutil.view.list

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.AdapterView
import androidx.appcompat.widget.AppCompatSpinner
import timber.log.Timber



/**
 *
 *  A Spinner class that distinguish between programmatically set OnItemClick or that triggered by the user
 *
 * @param context
 * @param attrs
 * @param defStyleAttr
 */

class UserSelectedSpinner@JvmOverloads constructor (
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : AppCompatSpinner(context, attrs, defStyleAttr) {
    private var mIsOpenedByUser = false
    private var mOnUserSelectedItemListener : OnItemSelectedListener? = null

    private val mOnItemSelectedListenerInternal = object : AdapterView.OnItemSelectedListener {
        override fun onNothingSelected(parent: AdapterView<*>?) {
            mOnUserSelectedItemListener?.onNothingSelected(parent)
        }

        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
            mOnUserSelectedItemListener?.onItemSelected(parent, view, mIsOpenedByUser, position, id)
            mIsOpenedByUser = false
        }
    }


    interface OnItemSelectedListener : AdapterView.OnItemSelectedListener {
        override fun onNothingSelected(parent: AdapterView<*>?) {}
        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long){}
        fun onUserOpenedMenu() {}
        fun onItemSelected(parent: AdapterView<*>?, view: View?, byUser: Boolean, position: Int, id: Long) {
            onItemSelected(parent, view, position, id)
        }
    }


    override fun setOnItemSelectedListener(listener: AdapterView.OnItemSelectedListener?) {
        if (listener is OnItemSelectedListener) {
            super.setOnItemSelectedListener(mOnItemSelectedListenerInternal)
            mOnUserSelectedItemListener = listener
        } else {
            super.setOnItemSelectedListener(listener)
            mOnUserSelectedItemListener = null
            if (listener != null) {
                Timber.w("setOnItemSelectedListener is not using ${javaClass.simpleName}.OnItemSelectedListener")
            }

        }
    }

    override fun performClick(): Boolean {
        mIsOpenedByUser = true
        mOnUserSelectedItemListener?.onUserOpenedMenu()
        return super.performClick()
    }
}