package com.github.hereisderek.androidutil.util

import java.util.concurrent.atomic.AtomicBoolean

/**
 *
 * User: derekzhu
 * Date: 2019-06-26 16:12
 * Project: AndroidUtil
 */


/**
 * only execute when @param condition is true
 *
 * @property condition
 * @property executeOnce
 * @property action
 */
class ConditionalExecutor(
    private val condition: (() -> Boolean),
    private val executeOnce: Boolean = true,
    private val action: (() -> Unit)
) {
    private val _executed = AtomicBoolean(false)
    var executed: Boolean
        get() = _executed.get()
        private set(value) {
            _executed.set(value)
        }

    // return has been executed
    @Synchronized fun checkAndExecute() {
        if (executed && executeOnce) return
        if (!condition.invoke()) return

        action?.invoke()
        executed = true
    }


    fun reset() {
        executed = false
    }
}