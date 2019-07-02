package com.github.hereisderek.androidutil.util.flag

import java.util.concurrent.atomic.AtomicInteger

inline class Flag(val flag: Int) {
    constructor(vararg flags: Int) : this(sum(*flags))
    constructor(flags: List<Int>) : this(sum(flags))


    fun hasFlag(flag: Int) = flag and this.flag == flag
    fun withFlag(flag: Int) = this.flag or flag
    fun minusFlag(flag: Int) = this.flag and flag.inv()

    operator fun minus(flag: Int) = minusFlag(flag)

    operator fun plus(flag: Int) = withFlag(flag)

    companion object {
        val MAX_FLAG = Flag(Int.MAX_VALUE)

        private fun sum(vararg flags: Int) : Int {
            var sum = 0
            flags.forEach { sum = (sum or it) }
            return sum
        }

        private fun sum(flags: List<Int>) : Int {
            var sum = 0
            flags.forEach { sum = (sum or it) }
            return sum
        }
    }
}

class FlagGenerator(private val initialShift: Int = 1) {
    private val counter = AtomicInteger(initialShift)

    fun nextFlag() : Flag = Flag( 1 shl counter.getAndIncrement())

    fun clear() { counter.set(initialShift) }
}