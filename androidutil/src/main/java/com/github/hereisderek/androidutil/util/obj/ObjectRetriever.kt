package com.github.hereisderek.androidutil.util.obj

import timber.log.Timber

/**
 *
 * User: derekzhu
 * Date: 2019-06-26 14:14
 * Project: AndroidUtil
 */

/**
 * to help retrieve object from a map-like object @constructor source: T by a given key @param Key
 * */
open class ObjectRetriever<T, Key, ResultType>(
    open var source: T? = null,
    private val retriever: Retriever<T, Key, ResultType>) {

    open operator fun get(key: Key) : ResultType? {
        val mother = this.source
        emptyList<String>()
        return if (mother == null) {
            Timber.e("${javaClass.simpleName} source is not set ")
            null
        } else {
            retriever.retrieve(mother, key)
        }
    }


    interface Retriever<T, Key, ResultType> {
        fun retrieve(from: T, key: Key) : ResultType?
    }
}