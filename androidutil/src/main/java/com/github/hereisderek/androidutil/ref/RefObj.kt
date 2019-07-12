package com.github.hereisderek.androidutil.ref

import java.lang.ref.PhantomReference
import java.lang.ref.Reference
import java.lang.ref.SoftReference
import java.lang.ref.WeakReference

/**
 *
 * User: derekzhu
 * Date: 2019-07-07 15:53
 * Project: Imagician Demo
 */

enum class RefType {
    WEAK, SOFT, PHANTOM
}

class RefObj<T>(t: T, val refType: RefType = RefType.WEAK) {
    private var mRef : Reference<T>? = null
    val obj get() = mRef?.get()

    init {
        set(t)
    }

    fun set(t: T) {
        when (refType) {
            RefType.WEAK -> mRef = WeakReference(t)
            RefType.SOFT -> mRef = SoftReference(t)
            RefType.PHANTOM -> mRef = PhantomReference(t, null)
        }
    }

    fun get() : T? = obj
}