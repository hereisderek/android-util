package com.github.hereisderek.androidutil.obj

/**
 *
 * User: derekzhu
 * Date: 2019-06-26 15:55
 * Project: AndroidUtil
 */


@Suppress("MemberVisibilityCanBePrivate")
class VolatileObjectSuspend<T> (
    private val generator: suspend () -> T,
    private val onDirtyListener: ((old: T?)->Unit)? = null,
    private val updater: (suspend T.() -> Unit)? = null
) {


    private var dirty: Boolean = true
        @Synchronized set
        @Synchronized get

    private var t : T? = null

    val isDirty : Boolean get() = dirty

    suspend fun get() : T {
        val currentT = this.t


        if (dirty || currentT == null) {
            val t: T
            if (updater == null || currentT == null) {
                t = generator()
                dirty = false
            } else {
                t = currentT
                updater.invoke(t)
                dirty = false
            }
            this.t = t
            return t
        }
        return currentT
    }


    fun dirty() {
        if (!this.dirty) {
            this.dirty = true

            onDirtyListener?.invoke(t)
            if (updater == null) {
                t = null
            }
        }
    }

    fun invalidate() {
        if (t != null) {
            t = null
            dirty()
        }
    }
}