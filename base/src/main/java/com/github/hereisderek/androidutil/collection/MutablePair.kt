package com.github.hereisderek.androidutil.collection

/**
 *
 * User: derekzhu
 * Date: 2019-07-12 14:54
 * Project: Imagician Demo
 */

typealias MutablePairOptional<A, B> = MutablePair<A?, B?>


fun <A, B> MutablePairOptional<A, B>.clear() = this.apply {
    first = null
    second = null
}

inline fun <A, B> MutablePair<A, B>.use(action: (first: A, second:B)->Unit) = snapshot.apply {
    action.invoke(first, second)
}

interface MutablePair<A, B> {
    var first: A
    var second: B
    val snapshot: Pair<A, B>
    fun clearSnapshot()
}

@Suppress("CanBePrimaryConstructorProperty")
open class MutablePairImpl<A, B> (first: A, second: B) : MutablePair<A, B> {
    override var first: A = first; set(value) {
        field = value
        clearSnapshot()
    }
    override var second: B = second; set(value) {
        field = value
        clearSnapshot()
    }

    private var _snapshot: Pair<A, B>? = null; get() = field ?: Pair(first, second).also {
        field = it
    }

    override val snapshot: Pair<A, B> get() = _snapshot!!

    override fun clearSnapshot() {
        _snapshot = null
    }
}



class MutablePairSafe<A, B> constructor (
    first: A,
    second: B
) : MutablePairImpl<A, B>(first, second) {

    override var first: A
        get() = synchronized(this){ super.first }
        set(value) = synchronized(this) {
            super.first = value
        }

    override var second: B
        get() = synchronized(this){ super.second }
        set(value) = synchronized(this) {
            super.second = value
        }

    override val snapshot: Pair<A, B> get() = synchronized(this) { super.snapshot }

    override fun clearSnapshot() = synchronized(this) {
        super.clearSnapshot()
    }
}



