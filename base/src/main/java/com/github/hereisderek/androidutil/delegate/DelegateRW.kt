package com.github.hereisderek.androidutil.delegate

import android.view.View
import kotlin.reflect.KProperty

typealias OnChangedListener<T> = ((old: T, new: T) -> Unit)


// convenient methods
@JvmOverloads
fun <T> delegateWR(
    init: T,
    mode : LazyThreadSafetyMode = LazyThreadSafetyMode.NONE,
    onChangeListener: OnChangedListener<T>? = null
) = when(mode) {
    LazyThreadSafetyMode.SYNCHRONIZED -> DelegateRWSafe(init, onChangeListener)
    else -> DelegateRWUnSafe(init, onChangeListener)
}

// utility class for calling view.invalidate() when changed
fun <V : View, T> V.onChangeViewInvalidate(
    init: T,
    mode : LazyThreadSafetyMode = LazyThreadSafetyMode.NONE,
    onChangeListener: OnChangedListener<T>? = null
) : DelegateRW<T> = delegateWR(init, mode, onChangeListener).apply {
    subscribe { _, _ -> invalidate() }
}


/**
 * delegate class provides read/write functionality
 */
interface DelegateRW<T> {
    operator fun getValue(thisRef: Any?, property: KProperty<*>): T
    operator fun setValue(thisRef: Any?, property: KProperty<*>, value: T)
    operator fun plusAssign(onChangeListener: OnChangedListener<T>)
    fun subscribe(onChangeListener: OnChangedListener<T>) = this.also { this += onChangeListener }
}

open class DelegateRWUnSafe<T> (
    init: T,
    onChangeListener: OnChangedListener<T>? = null
) : DelegateRW<T> {
    private val listeners = ArrayList<OnChangedListener<T>>()
    private var value: T = init

    init {
        if (onChangeListener != null && !listeners.contains(onChangeListener)) {
            listeners += onChangeListener
        }
    }

    override operator fun getValue(thisRef: Any?, property: KProperty<*>): T = value

    override operator fun setValue(thisRef: Any?, property: KProperty<*>, value: T)  {
        val old = this.value
        if (value != old) {
            this.value = value
            listeners.forEach {
                it.invoke(old, value)
            }
        }
    }

    override fun plusAssign(onChangeListener: OnChangedListener<T>) {
        if (!listeners.contains(onChangeListener)){
            listeners += onChangeListener
        }
    }
}


open class DelegateRWSafe<T> (
    init: T,
    onChangeListener: OnChangedListener<T>? = null
) : DelegateRWUnSafe<T>(init, onChangeListener) {

    override operator fun getValue(thisRef: Any?, property: KProperty<*>): T = synchronized(this) {
        super.getValue(thisRef, property)
    }

    override operator fun setValue(thisRef: Any?, property: KProperty<*>, value: T) = synchronized(this) {
        super.setValue(thisRef, property, value)
    }
    override fun plusAssign(onChangeListener: OnChangedListener<T>) = synchronized(this) {
        super.plusAssign(onChangeListener)
    }
}
