package com.github.hereisderek.androidutil.obj

/**
 *
 * User: derekzhu
 * Date: 2019-08-16 12:42
 * Project: Imagician Demo
 */

fun <T> mutable(
    onDirty: ((first: Boolean, oldValue: T?, lazyValue: ()->T?)->Unit)? = null,
    updater: (oldValue : T?) -> T
) : MutableObj<T> = MutableObj<T>(onDirty, updater)


fun <T, D> mutable(
    onDirty: ((first: Boolean, oldValue: T?, lazyValue: (depend: D)->T?)->Unit)? = null,
    updater: (depend: D, reuse : T?) -> T
) : MutableObj2<T, D> = MutableObj2<T, D>(onDirty, updater)



/**
 * a self-contained, reusable computed obj that only update
 * when requested but marked as dirty
 *
 * @param T type of the requested obj
 * @property onDirty a listener that notifies when [dirty()] been called, first is when it was first marked as dirty,
 *                  lazyValue: a lazy object that can be invoked to retrieve the new obj
 *
 * @property updater a computable that passed in the old result for reuse (null if first initialized)
 *
 */

class MutableObj<T>(
    private val onDirty: ((first: Boolean, oldValue: T?, lazyValue: ()->T?)->Unit)? = null,
    private val updater: (oldValue : T?) -> T
) {
    private var _isDirty = true; @Synchronized set

    val isDirty get() = _isDirty
    val value get() = get()

    private var currentValue : T? = null

    fun dirty() {
        // we save the dirty to a local variable so in onDirty block if the user
        // gets the new value it won't be marked dirty again
        val dirty = _isDirty
        this._isDirty = true
        onDirty?.invoke(!dirty, currentValue){
            get()
        }

    }

    @Synchronized fun  get() : T {
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
 * @param D: dependent
 * */
class MutableObj2<T, D>(
    private val onDirty: ((first: Boolean, oldValue: T?, lazyValue: (depend: D)->T?)->Unit)? = null,
    private val updater: (depend: D, reuse : T?) -> T
) {
    private var _isDirty = true; @Synchronized set

    val isDirty get() = _isDirty

    private var currentValue : T? = null

    fun dirty() {
        // we save the dirty to a local variable so in onDirty block if the user
        // gets the new value it won't be marked dirty again
        val dirty = _isDirty
        this._isDirty = true
        onDirty?.invoke(!dirty, currentValue){
            get(it)
        }
    }

    @Synchronized fun  get(depend: D) : T {
        var value = currentValue
        if (_isDirty || value == null) {
            value = updater.invoke(depend, currentValue)
            currentValue = value
            _isDirty = false
            return value
        }
        return value
    }
}