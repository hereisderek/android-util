package com.github.hereisderek.androidutil.obj

/**
 *
 * User: derekzhu
 * Date: 2019-06-26 15:44
 * Project: AndroidUtil
 */


/**
 * create a wrapper for an object whose data might change at any time.
 * the object will be lazy created by @property generator
 * and its data be lazy calculated by the updater
 *
 * when updater is null, every time when data is requested (or get() been called),
 * generator will be invoked and T instance with updated data will be expected
 *
 * <b> note: for it depends that dirty() been called when object is been updated </b>
 *
 * this philosophy behind is not to track object changes, but to provide an updated value (lazy calculated) when it is marked as dirty
 *
 * @param T the data type
 * @property generator lazy creation of the object. no need to update its data yet
 *
 * @property updater block of code to update the @param T object
 */

@Suppress("MemberVisibilityCanBePrivate")
class VolatileObject<T>(
    private val generator: () -> T,
    private val onDirtyListener: ((lazyValue: ()->T?)->Unit)? = null,
    private val updater: (T.() -> Boolean)? = null) {


    private var dirty: Boolean = true
        @Synchronized set
        @Synchronized get

    private var t : T? = null

    val isDirty : Boolean get() = dirty

    val value: T  @Synchronized get() {
        val currentT = this.t

        if (dirty || currentT == null) {
            val t: T
            if (updater == null) {
                t = generator()
                dirty = false
            } else {
                t = currentT ?: generator()
                if (updater.invoke(t)){
                    dirty = false
                    this.t = t
                } else return t // failed to update
            }
            return t
        }
        return currentT
    }


/*    constructor(
        generator: () -> T,
        onDirtyListener: (()->Unit)? = null,
        updater: (T.() -> Boolean)? = null
    ) : this(generator, if (onDirtyListener == null) null else {_ -> onDirtyListener.invoke()}, updater)*/


    fun dirty() {
        if (!this.dirty) {
            this.dirty = true
            onDirtyListener?.invoke {value}
        }
    }

    fun invalidate() {
        if (t != null) {
            t = null
            dirty()
        }
    }



    /**
     * @return null if failed to update the data
     */
    fun get() = value


    companion object {

    }
}