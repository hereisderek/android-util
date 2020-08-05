package com.github.hereisderek.androidutil.ui.bottomsheet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewbinding.ViewBinding
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

abstract class CustomViewBottomSheetDialog<T : ViewBinding>(
) : BottomSheetDialogFragment() {

    com.android.tools.build:gradle
    /*var customViewBinding : T = customViewBinding; set(value) {
        if (field != value) {
            field = value
            view?.requestLayout()
        }
    }*/



    init {
        (requireDialog() as BottomSheetDialog).dismissWithAnimation = true

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        onCreateBinding(inflater, container, savedInstanceState)
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    open fun onCreateBinding(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): T? {

    }


}
