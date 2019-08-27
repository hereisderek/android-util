package com.github.hereisderek.androidutil.obj

/**
 *
 * User: derekzhu
 * Date: 2019-08-05 22:36
 * Project: androidutil
 */

internal typealias OnEventHandler<T> = (handler: T) -> Boolean
class EventDistributor<T> {
    private val handlers = ArrayList<T>()


}