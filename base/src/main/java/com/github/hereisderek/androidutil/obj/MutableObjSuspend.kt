package com.github.hereisderek.androidutil.obj

/**
 *
 * User: derekzhu
 * Date: 2019-08-16 13:00
 * Project: Imagician Demo
 */


fun <T> mutableSuspend(
    onDirty: ((first: Boolean, oldValue: T?, lazyValue: suspend ()->T?)->Unit)? = null,
    updater: suspend (oldValue : T?) -> T
) : MutableObjSuspend<T> = MutableObjSuspend<T>(onDirty, updater)



fun <T, Dep> mutableSuspend(
    onDirty: ((first: Boolean, oldValue: T?, lazyValue: suspend (dep: Dep)->T?)->Unit)? = null,
    updater: suspend (dep: Dep, oldValue : T?) -> T
) : MutableObj2Suspend<T, Dep> = MutableObj2Suspend<T, Dep>(onDirty, updater)




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


/**
 * @param Dep dependency
 *
 * @param onDirty
 *
 * */
class MutableObj2Suspend <T, Dep>(
    private val onDirty: ((first: Boolean, oldValue: T?, lazyValue: suspend (dep: Dep)->T?)->Unit)? = null,
    private val updater: suspend (dep: Dep, oldValue : T?) -> T
) {
    private var _isDirty = true; @Synchronized set
    private val isDirty get() = _isDirty
    private var currentValue : T? = null

    fun dirty() {
        val dirty = _isDirty
        this._isDirty = true
        onDirty?.invoke(!dirty, currentValue){
            get(it)
        }
    }

    @Synchronized suspend fun  get(dep: Dep) : T {
        var value = currentValue
        if (_isDirty || value == null) {
            value = updater.invoke(dep, currentValue)
            currentValue = value
            _isDirty = false
            return value
        }
        return value
    }
}

