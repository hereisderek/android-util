package com.github.hereisderek.androidutil.obj

/**
 *
 * User: derekzhu
 * Date: 2019-08-16 13:00
 * Project: Imagician Demo
 */


class MutableObjSuspend <T>(
    private val onDirty: ((first: Boolean, oldValue: T?, lazyValue: suspend ()->T?)->Unit)? = null,
    private val updater: suspend (oldValue : T?) -> T
) {
    private var _isDirty = true; @Synchronized set
    private val isDirty get() = _isDirty
    private var currentValue : T? = null

    fun dirty() {
        val dirty = _isDirty
        this._isDirty = true
        onDirty?.invoke(!dirty, currentValue){
            get()
        }
    }

    @Synchronized suspend fun  get() : T {
        var value = currentValue
        if (_isDirty || value == null) {
            value = updater.invoke(currentValue)
            currentValue = value
            _isDirty = false
            return value
        }
        return value
    }
}