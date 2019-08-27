package com.github.hereisderek.androidutil.flag

import java.util.concurrent.atomic.AtomicInteger

class Flag(var value: Int = 0) {
    constructor(vararg flags: Int) : this(sum(*flags))
    constructor(flags: List<Int>) : this(sum(flags))


    fun hasFlag(flag: Flag) : Boolean = has(this.value, flag.value)
    fun withFlag(flag: Flag) : Flag = Flag(with(value, flag.value))
    fun minusFlag(flag: Flag) : Flag = Flag(value, flag.value)

    fun hasFlag(flag: Int) : Boolean = has(this.value, flag)
    fun withFlag(flag: Int) : Flag = Flag(with(value, flag))
    fun minusFlag(flag: Int) : Flag = Flag(value, flag)



    operator fun minus(flag: Int) = minusFlag(flag)

    operator fun plus(flag: Int) = withFlag(flag)

    operator fun plusAssign(flag: Int) {
        value = with(value, flag)
    }

    operator fun minusAssign(flag: Int) {
        value = minus(value, flag)
    }



    operator fun minus(flag: Flag) = minus(flag.value)

    operator fun plus(flag: Flag): Flag = withFlag(flag.value)

    operator fun plusAssign(flag: Flag) {
        plusAssign(flag.value)
    }

    operator fun minusAssign(flag: Flag) {
        minusAssign(flag.value)
    }

    override fun toString(): String = "${javaClass.simpleName}:$value"


    companion object {
        val MAX_FLAG = Flag(Int.MAX_VALUE)


        private fun minus(from: Int, subtract: Int) = from and subtract.inv()

        private fun with(from: Int, addition: Int) = from or addition

        private fun has(from: Int, contains: Int) = from and contains == contains

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