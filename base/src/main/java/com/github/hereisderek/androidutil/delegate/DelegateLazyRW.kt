package com.github.hereisderek.androidutil.delegate

import com.github.hereisderek.androidutil.collection.addIfNotContain
import kotlin.reflect.KProperty



@JvmOverloads
fun <T> delegateWR(
    lazyValue: () -> T,
    mode : LazyThreadSafetyMode = LazyThreadSafetyMode.NONE,
    vararg onChangeListener: OnChangedListener<T>
) : DelegateRW<T> = when(mode) {
    LazyThreadSafetyMode.SYNCHRONIZED -> DelegateLazyRWSafe(lazyValue, *onChangeListener)
    else -> DelegateLazyRW(lazyValue, *onChangeListener)
}

/**
 *
 */
open class DelegateLazyRW<T>(
    private val lazyValue: ()->T,
    vararg onChangeListener: OnChangedListener<T>
) : DelegateRW<T> {
    private var value: T? = null
    private val listeners = ArrayList<OnChangedListener<T>>()

    init {
        listeners.addAll(onChangeListener)
    }

    override fun plusAssign(onChangeListener: OnChangedListener<T>) {
        listeners.addIfNotContain(onChangeListener)
    }

    override fun getValue(thisRef: Any?, property: KProperty<*>): T = this.value ?: lazyValue.invoke()

    override fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
        val old = this.value
        if (old != value) {
            this.value = value

            // skip initial set since it was the default value
            if (old != null) {
                listeners.forEach { it.invoke(old, value) }
            }
        }
    }
}


open class DelegateLazyRWSafe<T>(
    lazyValue: ()->T,
    vararg onChangeListener: OnChangedListener<T>
) : DelegateLazyRW<T>(lazyValue, *onChangeListener) {

    override fun plusAssign(onChangeListener: OnChangedListener<T>) = synchronized(this){
        super.plusAssign(onChangeListener)
    }

    override fun getValue(thisRef: Any?, property: KProperty<*>): T = synchronized(this){
        super.getValue(thisRef, property)
    }

    override fun setValue(thisRef: Any?, property: KProperty<*>, value: T) = synchronized(this){
        super.setValue(thisRef, property, value)
    }
}

